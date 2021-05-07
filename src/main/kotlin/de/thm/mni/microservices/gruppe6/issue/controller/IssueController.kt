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

    @GetMapping("/{projectId}")
    fun getAllProjectIssues(@PathVariable projectId: UUID): Flux<Issue> = issueService.getAllProjectIssues(projectId)

    @GetMapping("/{projectId}/{issueId}")
    fun getIssue(@PathVariable projectId: UUID, @PathVariable issueId: UUID): Mono<Issue> =
        issueService.getIssue(projectId, issueId)

    @PutMapping("/{projectId}")
    fun putIssue(@PathVariable projectId: UUID, @RequestBody issueDTO: IssueDTO): Mono<Issue> =
        issueService.putIssue(projectId, issueDTO)

    @PostMapping("/{projectId}/{issueId}")
    fun updateIssue(
        @PathVariable projectId: UUID,
        @PathVariable issueId: UUID,
        @RequestBody issueDTO: IssueDTO
    ): Mono<Issue> = issueService.updateIssue(projectId, issueId, issueDTO)

    @DeleteMapping("/{projectId}/{issueId}")
    fun deleteIssue(@PathVariable projectId: UUID, @PathVariable issueId: UUID): Mono<Void> =
        issueService.deleteIssue(projectId, issueId)
}
