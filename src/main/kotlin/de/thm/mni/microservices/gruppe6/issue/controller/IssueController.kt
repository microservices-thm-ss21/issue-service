package de.thm.mni.microservices.gruppe6.issue.controller

import de.thm.mni.microservices.gruppe6.issue.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.issue.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.issue.service.IssueDbService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/issues")
class IssueController(@Autowired val issueService: IssueDbService) {

    @GetMapping("/")
    fun getAllIssues(): Flux<Issue> = issueService.getAllIssues()

    @GetMapping("/{prjId}")
    fun getAllProjectIssues(@PathVariable prjId: UUID): Flux<Issue> = issueService.getAllProjectIssues(prjId)

    @GetMapping("/{id}")
    fun getIssue(@PathVariable id: UUID): Mono<Issue> = issueService.getIssue(id)

    @PutMapping("/{prjId}")
    fun putIssue(@PathVariable prjId: UUID, @RequestBody issueDTO: IssueDTO): Mono<Issue> = issueService.putIssue(prjId, issueDTO)

    @PostMapping("/{prjId}/{id}")
    fun updateIssue(@PathVariable prjId: UUID, @PathVariable id: UUID, @RequestBody issueDTO: IssueDTO) = issueService.updateIssue(prjId, id, issueDTO)

    @DeleteMapping("/{prjId}/{id}")
    fun deleteIssue(@PathVariable prjId: UUID, @PathVariable id: UUID) = issueService.deleteIssue(prjId, id)
}
