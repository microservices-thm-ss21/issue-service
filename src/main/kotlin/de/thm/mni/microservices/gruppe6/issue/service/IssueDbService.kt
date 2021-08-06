package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.IssueRepository
import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import de.thm.mni.microservices.gruppe6.issue.model.persistence.UserRepository
import de.thm.mni.microservices.gruppe6.issue.requests.Requester
import de.thm.mni.microservices.gruppe6.lib.classes.issueService.Issue
import de.thm.mni.microservices.gruppe6.lib.classes.issueService.IssueDTO
import de.thm.mni.microservices.gruppe6.lib.classes.userService.GlobalRole
import de.thm.mni.microservices.gruppe6.lib.classes.userService.User
import de.thm.mni.microservices.gruppe6.lib.event.*
import de.thm.mni.microservices.gruppe6.lib.exception.ServiceException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime
import java.util.*

/**
 * Implements the functionality used to process issues
 */
@Component
class IssueDbService(
    @Autowired val issueRepo: IssueRepository,
    @Autowired val sender: JmsTemplate,
    @Autowired val requester: Requester,
    @Autowired val userRepository: UserRepository,
    @Autowired val projectRepository: ProjectRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Returns all issues
     * @return flux of issues
     */
    fun getAllIssues(): Flux<Issue> {
        logger.debug("getAllIssues")
        return issueRepo.findAll()
    }

    /**
     * Get all Issues from DB of a Project
     * @param projectId Project Id
     */
    fun getAllProjectIssues(projectId: UUID): Flux<Issue> {
        logger.debug("getAllProjectIssues $projectId")
        return issueRepo.getIssuesByProjectId(projectId)
    }

    fun getAllAssignedIssues(userId: UUID): Flux<Issue> {
        logger.debug("getAllAssignedIssues $userId")
        return issueRepo.getIssuesByAssignedUserId(userId)
    }

    /**
     * Get Issue from DB by Id
     * @param issueId Issue Id
     */
    fun getIssue(issueId: UUID): Mono<Issue> {
        logger.debug("getIssue $issueId")
        return issueRepo.findById(issueId).switchIfEmpty { Mono.error(ServiceException(HttpStatus.NOT_FOUND)) }
    }


    /**
     * Creates an issue, stores it inside database and sends all necessary events.
     * @param issueDTO holding all info to create the issue
     * @param requesterId
     * @return created issue
     */
    fun createIssue(issueDTO: IssueDTO, requester: User): Mono<Issue> {
        logger.debug("updateIssue: $issueDTO $issueDTO ${requester.id}")
        return userRepository.existsById(requester.id!!)
            .filter { it }
            .switchIfEmpty {
                Mono.error(ServiceException(HttpStatus.NOT_FOUND, "creator user does not exist"))
            }
            .filter { issueDTO.message != null && issueDTO.projectId != null }
            .switchIfEmpty {
                Mono.error(
                    ServiceException(
                        HttpStatus.BAD_REQUEST,
                        "issue message and project id necessary to create an issue"
                    )
                )
            }
            .flatMap {
                if (issueDTO.assignedUserId != null) userRepository.existsById(issueDTO.assignedUserId!!)
                else Mono.just(true)
            }
            .filter { it }
            .switchIfEmpty {
                Mono.error(ServiceException(HttpStatus.NOT_FOUND, "assigned user does not exist"))
            }
            .flatMap {
                projectRepository.existsById(issueDTO.projectId!!)
            }
            .filter { it }
            .switchIfEmpty {
                Mono.error(ServiceException(HttpStatus.NOT_FOUND, "associated project does not exist"))
            }
            .flatMap {
                if(issueDTO.assignedUserId != null) {
                    sendMemberRequest(issueDTO.projectId!!, issueDTO.assignedUserId!!)
                } else {
                    Mono.just(true)
                }
            }
            .filter{it}
            .switchIfEmpty {
                Mono.error(ServiceException(HttpStatus.BAD_REQUEST, "assigned user is not member of project"))
            }
            .map { Issue(issueDTO, requester.id!!) }
            .flatMap { issueRepo.save(it) }
            .publishOn(Schedulers.boundedElastic()).map {
                sender.convertAndSend(
                    EventTopic.DataEvents.topic,
                    IssueDataEvent(DataEventCode.CREATED, it.id!!, it.projectId)
                )
                it
            }
    }

    /**
     * Updates an issue, checks if user as permissions to do it and sends all necessary events.
     * @param issueId
     * @param issueDTO holding all info to update the issue
     * @param requesterUser
     * @throws ServiceException when permissions are not fulfilled or project or user do not exist.
     * @return updated issue
     */
    fun updateIssue(issueId: UUID, issueDTO: IssueDTO, requesterUser: User): Mono<Issue> {
        logger.debug("updateIssue: $issueId $issueDTO $requesterUser")
        return Mono.just(issueDTO.assignedUserId != null).flatMap {
                if (it)
                    userRepository.existsById(issueDTO.assignedUserId!!)
                else
                    Mono.just(true)
            }
            .filter { it }
            .switchIfEmpty {
                Mono.error(ServiceException(HttpStatus.NOT_FOUND, "assigned user does not exist"))
            }
            .flatMap {
                projectRepository.existsById(issueDTO.projectId!!)
            }
            .filter { it }
            .switchIfEmpty {
                Mono.error(ServiceException(HttpStatus.NOT_FOUND, "associated project does not exist"))
            }
            .flatMap {
                issueRepo.findById(issueId)
            }
            .switchIfEmpty {
                Mono.error(ServiceException(HttpStatus.NOT_FOUND, "issue does not exist"))
            }
            .flatMap { issue ->
                checkProjectMember(issue, requesterUser)
            }
            .map { it.applyIssueDTO(issueDTO) }
            .flatMap {
                issueRepo.save(it.first).map { issue ->
                    Pair(issue, it.second)
                }
            }
            .publishOn(Schedulers.boundedElastic()).map {
                sender.convertAndSend(
                    EventTopic.DataEvents.topic,
                    IssueDataEvent(DataEventCode.UPDATED, issueId, it.first.projectId)
                )
                it.second.forEach { (topic, event) -> sender.convertAndSend(topic, event) }
                it.first
            }
    }

    /**
     * Deletes an issue and sends necessary events.
     * @param issueId Issue Id
     * @param requesterUser
     */
    fun deleteIssue(issueId: UUID, requesterUser: User): Mono<UUID> {
        logger.debug("deleteIssue: $issueId $requesterUser")
        return issueRepo.findById(issueId)
            .switchIfEmpty {
                Mono.error(ServiceException(HttpStatus.NOT_FOUND, "Issue does not exist"))
            }
            .flatMap { checkProjectMember(it, requesterUser) }
            .flatMap {
                issueRepo.deleteById(issueId).thenReturn(issueId)
            }
            .publishOn(Schedulers.boundedElastic()).map {
                sender.convertAndSend(EventTopic.DataEvents.topic, IssueDataEvent(DataEventCode.DELETED, issueId))
                it
            }
    }


    /**
     * apply the issueDTO to the issue model as stored in DB and generate Domain Events
     * @param issueDTO request body to apply to an issue
     * @return the updated issue and a list of events to be issued: (Topic, new DomainEvent)
     */
    fun Issue.applyIssueDTO(issueDTO: IssueDTO): Pair<Issue, List<Pair<String, DomainEvent>>> {
        val eventList = ArrayList<Pair<String, DomainEvent>>()

        if (issueDTO.projectId != null && this.projectId != issueDTO.projectId)
            throw ServiceException(HttpStatus.BAD_REQUEST, "You may not update the project ID of an existing Issue")
        if (issueDTO.message != null && this.message != issueDTO.message!!) {
            eventList.add(
                Pair(
                    EventTopic.DomainEvents_IssueService.topic,
                    DomainEventChangedString(
                        DomainEventCode.ISSUE_CHANGED_MESSAGE,
                        this.id!!,
                        this.message,
                        issueDTO.message
                    )
                )
            )
            this.message = issueDTO.message!!
        }
        if (issueDTO.deadline != null && this.deadline != issueDTO.deadline) {
            eventList.add(
                Pair(
                    EventTopic.DomainEvents_IssueService.topic,
                    DomainEventChangedDate(
                        DomainEventCode.ISSUE_CHANGED_DEADLINE,
                        this.id!!,
                        this.deadline,
                        issueDTO.deadline
                    )
                )
            )
            this.deadline = issueDTO.deadline
        }
        if (issueDTO.assignedUserId != null && this.assignedUserId != issueDTO.assignedUserId) {
            eventList.add(
                Pair(
                    EventTopic.DomainEvents_IssueService.topic,
                    DomainEventChangedUUID(
                        DomainEventCode.ISSUE_CHANGED_USER,
                        this.id!!,
                        this.assignedUserId,
                        issueDTO.assignedUserId
                    )
                )
            )
            this.assignedUserId = issueDTO.assignedUserId
        }
        if (issueDTO.status != null && this.status != issueDTO.status!!.name) {
            eventList.add(
                Pair(
                    EventTopic.DomainEvents_IssueService.topic,
                    DomainEventChangedString(
                        DomainEventCode.ISSUE_CHANGED_STATUS,
                        this.id!!,
                        this.status,
                        issueDTO.status!!.name
                    )
                )
            )
            this.status = issueDTO.status!!.name
        }

        this.updateTime = LocalDateTime.now()
        return Pair(this, eventList)
    }

    /**
     * Send a request to the project service and check if the user is member of the project
     * the issue belongs to.
     * @param issue
     * @param user
     * @param issue that was passed in
     */
    fun checkProjectMember(issue: Issue, user: User): Mono<Issue> {
        logger.debug("checkProjectMember: $issue $user")
        return Mono.just(issue).flatMap {
            // Check for either creator or global support/admin
            if (user.id == issue.creatorId || user.globalRole != GlobalRole.USER.name) {
                Mono.just(issue)
            } else {
                // Else send request to projectService and ask if user is member
                sendMemberRequest(issue.projectId, user.id!!)   // check if is member
                    .filter {
                        logger.debug("$it")
                        it
                    }
                    .map {
                        issue
                    }
            }
        }.switchIfEmpty(Mono.error(ServiceException(HttpStatus.FORBIDDEN, "Current has no permissions")))
    }

    fun sendMemberRequest(projectId: UUID, userId: UUID) : Mono<Boolean> {
        return requester.forwardGetRequestMono(
                "http://project-service:8082",
                "api/projects/${projectId}/members/${userId}/exists",
                Boolean::class.java
        )
    }

}
