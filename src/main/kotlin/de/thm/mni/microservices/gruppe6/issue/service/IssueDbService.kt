package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.issue.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.issue.model.persistence.IssueRepository
import de.thm.mni.microservices.gruppe6.lib.event.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime
import java.util.*

@Component
class IssueDbService(@Autowired val issueRepo: IssueRepository, @Autowired val sender: JmsTemplate) {

    fun getAllIssues(): Flux<Issue> = issueRepo.findAll()

    fun getAllProjectIssues(projectId: UUID): Flux<Issue> = issueRepo.getIssuesByProjectId(projectId)

    fun getIssue(issueId: UUID): Mono<Issue> {
        return issueRepo.findById(issueId)
    }


    fun createIssue(issueDTO: IssueDTO): Mono<Issue> {
        return Mono.just(issueDTO).map { Issue(it) }.flatMap { issueRepo.save(it) }
            .publishOn(Schedulers.boundedElastic()).map {
                sender.convertAndSend(EventTopic.DataEvents.topic, IssueDataEvent(DataEventCode.CREATED, it.id!!))
            it
            }
    }

    fun updateIssue(issueId: UUID, issueDTO: IssueDTO): Mono<Issue> {
        return issueRepo.findById(issueId)
            .map { it.applyIssueDTO(issueDTO) }
            .map { issueRepo.save(it.first)
            it
            }
            .publishOn(Schedulers.boundedElastic()).map {
                sender.convertAndSend(EventTopic.DataEvents.topic, IssueDataEvent(DataEventCode.UPDATED, issueId))
                it.second.forEach {(topic, event) -> sender.convertAndSend(topic, event) }
                it.first
            }
    }

    fun deleteIssue(issueId: UUID): Mono<Void> {
        return issueRepo.deleteById(issueId)
            .publishOn(Schedulers.boundedElastic()).map {
                sender.convertAndSend(EventTopic.DataEvents.topic, IssueDataEvent(DataEventCode.DELETED, issueId))
                it
            }
    }

    fun Issue.applyIssueDTO(issueDTO: IssueDTO): Pair<Issue, List<Pair<String,DomainEvent>>> {
        val eventList = ArrayList<Pair<String,DomainEvent>>()

        if(this.projectId != issueDTO.projectId)
            throw IllegalArgumentException("You may not update the project ID of an existing Issue")
        if(this.message != issueDTO.message!!){
            eventList.add(
                Pair(EventTopic.DomainEvents_IssueService.topic,
                    DomainEventChangedString(
                        DomainEventCode.ISSUE_CHANGED_MESSAGE,
                        this.id!!,
                        this.message,
                        issueDTO.message))
            )
            this.message = issueDTO.message!!
        }
        if(this.deadline != issueDTO.deadline){
            eventList.add(
                Pair(EventTopic.DomainEvents_IssueService.topic,
                    DomainEventChangedDate(
                        DomainEventCode.ISSUE_CHANGED_DEADLINE,
                        this.id!!,
                        this.deadline,
                        issueDTO.deadline
                )))
            this.deadline = issueDTO.deadline
        }
        if(this.assignedUserId != issueDTO.assignedUserId){
            eventList.add(
                Pair(EventTopic.DomainEvents_IssueService.topic,
                    DomainEventChangedUUID(
                        DomainEventCode.ISSUE_CHANGED_USER,
                        this.id!!,
                        this.assignedUserId,
                        issueDTO.assignedUserId))
            )
            this.assignedUserId = issueDTO.assignedUserId
        }
        this.updateTime = LocalDateTime.now()
        return Pair(this, eventList)
    }

}
