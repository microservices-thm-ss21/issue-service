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
@CrossOrigin
class IssueController(@Autowired val issueService: IssueDbService) {

    @GetMapping("")
    fun getAllIssues(): Flux<Issue> = issueService.getAllIssues()

    @GetMapping("/project/{projectId}")
    fun getAllProjectIssues(@PathVariable projectId: UUID): Flux<Issue> = issueService.getAllProjectIssues(projectId)

    @GetMapping("/{issueId}")
    fun getIssue(@PathVariable issueId: UUID): Mono<Issue> =
        issueService.getIssue(issueId)

    @PutMapping("")
    fun putIssue(@RequestBody issueDTO: IssueDTO): Mono<Issue> =
        issueService.putIssue(issueDTO)

    @PostMapping("{issueId}")
    fun updateIssue(
        @PathVariable issueId: UUID,
        @RequestBody issueDTO: IssueDTO
    ): Mono<Issue> = issueService.updateIssue(issueId, issueDTO)

    @DeleteMapping("/{issueId}")
    fun deleteIssue(@PathVariable issueId: UUID): Mono<Void> =
        issueService.deleteIssue(issueId)
}
