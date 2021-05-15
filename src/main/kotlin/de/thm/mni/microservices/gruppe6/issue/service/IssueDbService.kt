package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.event.Sender
import de.thm.mni.microservices.gruppe6.issue.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.issue.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.issue.model.persistence.IssueRepository
import de.thm.mni.microservices.gruppe6.lib.event.*
import de.thm.mni.microservices.gruppe6.lib.exception.ServiceException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Component
class IssueDbService(@Autowired val issueRepo: IssueRepository, @Autowired val sender: Sender) {

    fun getAllIssues(): Flux<Issue> = issueRepo.findAll()

    fun getAllProjectIssues(projectId: UUID): Flux<Issue> = issueRepo.getIssuesByProjectId(projectId)

    fun getIssue(issueId: UUID): Mono<Issue> {
        return issueRepo.findById(issueId).switchIfEmpty(Mono.error(ServiceException("Nicht gefunden!")))
    }


    fun putIssue(issueDTO: IssueDTO): Mono<Issue> {
        sender.send(ServiceEvent(EventCode.ISSUE_CREATED, "Success"))
        return issueRepo.save(Issue(issueDTO))
    }

    fun updateIssue(issueId: UUID, issueDTO: IssueDTO): Mono<Issue> {
        val issue = issueRepo.findById(issueId)
        return issue.flatMap { issueRepo.save(it.applyIssueDTO(issueDTO)) }
    }

    fun deleteIssue(issueId: UUID): Mono<Void> =
        issueRepo.deleteById(issueId)

    fun Issue.applyIssueDTO(issueDTO: IssueDTO): Issue {
        this.projectId = issueDTO.projectId!!
        this.message = issueDTO.message!!
        this.deadline = issueDTO.deadline
        this.assignedUserId = issueDTO.assignedUserId
        this.updateTime = LocalDateTime.now()
        this.globalRole = issueDTO.globalRole!!
        return this
    }

}
