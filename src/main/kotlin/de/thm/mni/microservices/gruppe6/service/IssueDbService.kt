package de.thm.mni.microservices.gruppe6.service

import de.thm.mni.microservices.gruppe6.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.model.persistence.IssueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Component
class IssueDbService(@Autowired val issueRepo: IssueRepository) {

    fun getAllIssues(): Flux<Issue> = issueRepo.findAll()

    fun getAllProjectIssues(projectId: UUID): Flux<Issue> = issueRepo.findAll().filter{ it.projectId == projectId }

    fun getIssue(projectId: UUID, id: UUID): Mono<Issue> = issueRepo.findById(id).filter{ it.projectId == projectId}

    fun putIssue(projectId: UUID, issueDTO: IssueDTO): Mono<Issue> = issueRepo.save(Issue(null, projectId, issueDTO))

    fun updateIssue(projectId: UUID, id: UUID, issueDTO: IssueDTO): Mono<Issue> {
        val user = issueRepo.findById(id).filter{it.projectId == projectId}
        return user.map { it.applyIssueDTO(issueDTO) }
    }

    fun deleteIssue(projectId: UUID, id: UUID): Mono<Void> = issueRepo.deleteById(id) // projectId komplett useless

    fun Issue.applyIssueDTO(issueDTO: IssueDTO): Issue {
        this.message = issueDTO.message!!
        this.deadline = issueDTO.deadline
        this.userId = issueDTO.userId
        this.updateTime = LocalDateTime.now()
        this.globalRole = issueDTO.globalRole!!
        return this
    }

}
