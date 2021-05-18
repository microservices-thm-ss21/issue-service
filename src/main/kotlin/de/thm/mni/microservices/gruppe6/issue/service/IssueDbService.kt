package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.issue.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.issue.model.persistence.IssueRepository
import de.thm.mni.microservices.gruppe6.lib.event.*
import de.thm.mni.microservices.gruppe6.lib.exception.ServiceException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.util.*

@Component
class IssueDbService(@Autowired val issueRepo: IssueRepository, @Autowired val sender: JmsTemplate) {

    fun getAllIssues(): Flux<Issue> = issueRepo.findAll()

    fun getAllProjectIssues(projectId: UUID): Flux<Issue> = issueRepo.getIssuesByProjectId(projectId)

    fun getIssue(issueId: UUID): Mono<Issue> {
        return issueRepo.findById(issueId).switchIfEmpty(Mono.error(ServiceException("Nicht gefunden!")))
    }


    fun putIssue(issueDTO: Mono<IssueDTO>): Mono<Issue> {
        return issueDTO.map { Issue(it) }.flatMap { issueRepo.save(it) }
            .publishOn(Schedulers.boundedElastic()).map {
                sender.convertAndSend(IssueEvent(DataEventCode.CREATED, it.id!!))
            it
        }
    }

    fun updateIssue(issueId: UUID, issueDTO: IssueDTO): Mono<Issue> {
        return issueRepo.findById(issueId)
            .flatMap { issueRepo.save(it.applyIssueDTO(issueDTO)) }
            .publishOn(Schedulers.boundedElastic()).map {
                sender.convertAndSend(IssueEvent(DataEventCode.UPDATED, issueId))
                it
            }
    }

    fun deleteIssue(issueId: UUID): Mono<Void> {
        return issueRepo.deleteById(issueId)
            .publishOn(Schedulers.boundedElastic()).map {
                sender.convertAndSend(IssueEvent(DataEventCode.DELETED, issueId))
                it
            }
    }

    fun Issue.applyIssueDTO(issueDTO: IssueDTO): Issue {
        if(this.projectId != issueDTO.projectId)
            throw IllegalArgumentException("You may not update the project ID of an existing Issue")

        this.message = issueDTO.message!!
        this.deadline = issueDTO.deadline
        this.assignedUserId = issueDTO.assignedUserId
        this.updateTime = LocalDateTime.now()
        this.globalRole = issueDTO.globalRole!!
        return this
    }

}
