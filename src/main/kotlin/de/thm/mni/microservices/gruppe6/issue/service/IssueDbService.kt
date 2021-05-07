package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.issue.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.issue.model.persistence.IssueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Component
class IssueDbService(@Autowired val issueRepo: IssueRepository) {

    fun getAllIssues(): Flux<Issue> = issueRepo.findAll()

    fun getAllProjectIssues(projectId: UUID): Flux<Issue> = issueRepo.findAll().filter { it.projectId == projectId }

    fun getIssue(projectId: UUID, issueId: UUID): Mono<Issue> =
        issueRepo.findById(issueId).filter { it.projectId == projectId } // projectId komplett useless weil issueId unique

    fun putIssue(projectId: UUID, issueDTO: IssueDTO): Mono<Issue> = issueRepo.save(Issue(null, projectId, issueDTO))

    fun updateIssue(projectId: UUID, issueId: UUID, issueDTO: IssueDTO): Mono<Issue> {
        val user = issueRepo.findById(issueId).filter { it.projectId == projectId }
        return user.map { it.applyIssueDTO(issueDTO) }
    }

    fun deleteIssue(projectId: UUID, issueId: UUID): Mono<Void> =
        issueRepo.deleteById(issueId) // projectId komplett useless

    fun Issue.applyIssueDTO(issueDTO: IssueDTO): Issue {
        this.message = issueDTO.message!!
        this.deadline = issueDTO.deadline
        this.userId = issueDTO.userId
        this.updateTime = LocalDateTime.now()
        this.globalRole = issueDTO.globalRole!!
        return this
    }

}
