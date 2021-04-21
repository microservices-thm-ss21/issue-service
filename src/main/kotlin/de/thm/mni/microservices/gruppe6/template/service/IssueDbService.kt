package de.thm.mni.microservices.gruppe6.template.service

import de.thm.mni.microservices.gruppe6.template.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.template.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.template.model.persistence.IssueRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class IssueDbService(@Autowired val issueRepo: IssueRepository) {

    /*
    issueService.getAllIssues()

  issueService.getAllProjectIssues(prjId)

   issueService.getIssue(prjId, id)

   issueService.putIssue(prjId, issueDTO)

     issueService.updateIssue(prjId, id, issueDTO)

     issueService.deleteIssue(prjId, id)
     */

    fun getAllProjectIssues(): Flux<Issue> = issueRepo.findAll()

    fun putIssue(issueDTO: IssueDTO): Mono<Issue> {
        return issueRepo.save(Issue(nextId++, issueDTO))
    }

    fun updateIssue(id: Long, issueDTO: IssueDTO): Mono<Issue> {
        val user = issueRepo.findById(id)
        return user.map { it.applyUserDTO(issueDTO) }
    }

    fun deleteIssue(id: Long): Mono<Void> {
        return issueRepo.deleteById(id)
    }

    fun Issue.applyUserDTO(issueDTO: IssueDTO): Issue {
        this.username = issueDTO.username!!
        this.lastName = issueDTO.lastName!!
        this.name = issueDTO.name!!
        this.dateOfBirth = issueDTO.dateOfBirth!!
        this.globalRole = issueDTO.globalRole!!
        return this
    }

}
