package de.thm.mni.microservices.gruppe6.template.service

import de.thm.mni.microservices.gruppe6.template.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.template.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.template.model.persistence.IssueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Component
class IssueDbService(@Autowired val issueRepo: IssueRepository) {

    fun getAllIssues(): Flux<Issue> = issueRepo.findAll()

    fun getAllProjectIssues(prjId: UUID): Flux<Issue> = issueRepo.findAll().filter{ it.prjId == prjId }

    fun getIssue(prjId: UUID, id: UUID): Mono<Issue> = issueRepo.findById(id).filter{ it.prjId == prjId}

    fun putIssue(prjId: UUID, issueDTO: IssueDTO): Mono<Issue> {
        return issueRepo.save(Issue(null, prjId, issueDTO))
    }

    fun updateIssue(prjId: UUID, id: UUID, issueDTO: IssueDTO): Mono<Issue> {
        val user = issueRepo.findById(id).filter{it.prjId == prjId}
        return user.map { it.applyIssueDTO(issueDTO) }
    }

    fun deleteIssue(prjId: UUID, id: UUID): Mono<Void> {
        return issueRepo.deleteById(id) // prjID komplett useless
    }

    fun Issue.applyIssueDTO(issueDTO: IssueDTO): Issue {
        this.message = issueDTO.message!!
        this.deadline = issueDTO.deadline
        this.userId = issueDTO.userId
        this.updateTime = LocalDateTime.now()
        this.globalRole = issueDTO.globalRole!!
        return this
    }

}
