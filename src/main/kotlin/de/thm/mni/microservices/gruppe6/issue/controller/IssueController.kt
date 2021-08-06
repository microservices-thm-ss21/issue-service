package de.thm.mni.microservices.gruppe6.issue.controller

import de.thm.mni.microservices.gruppe6.issue.service.IssueDbService
import de.thm.mni.microservices.gruppe6.lib.classes.authentication.ServiceAuthentication
import de.thm.mni.microservices.gruppe6.lib.classes.issueService.Issue
import de.thm.mni.microservices.gruppe6.lib.classes.issueService.IssueDTO
import de.thm.mni.microservices.gruppe6.lib.exception.coverUnexpectedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
            .onErrorResume { Mono.error(coverUnexpectedException(it)) }

    @GetMapping("project/{projectId}")
    fun getAllProjectIssues(@PathVariable projectId: UUID): Flux<Issue> =
        issueService.getAllProjectIssues(projectId)
                .onErrorResume { Mono.error(coverUnexpectedException(it)) }

    @GetMapping("user/{userId}")
    fun getAllAssignedIssues(@PathVariable userId: UUID): Flux<Issue> = issueService.getAllAssignedIssues(userId)
            .onErrorResume { Mono.error(coverUnexpectedException(it)) }

    @GetMapping("{issueId}")
    fun getIssue(@PathVariable issueId: UUID): Mono<Issue> =
        issueService.getIssue(issueId)
                .onErrorResume { Mono.error(coverUnexpectedException(it)) }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun createIssue(@RequestBody issueDTO: IssueDTO, auth: ServiceAuthentication): Mono<Issue> =
        issueService.createIssue(issueDTO, auth.user!!)
                .onErrorResume { Mono.error(coverUnexpectedException(it)) }

    @PutMapping("{issueId}")
    fun updateIssue(
        @PathVariable issueId: UUID,
        @RequestBody issueDTO: IssueDTO,
        auth: ServiceAuthentication
    ): Mono<Issue> = issueService.updateIssue(issueId, issueDTO, auth.user!!)
            .onErrorResume { Mono.error(coverUnexpectedException(it)) }

    @DeleteMapping("{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteIssue(@PathVariable issueId: UUID, auth: ServiceAuthentication): Mono<Void> =
        issueService.deleteIssue(issueId, auth.user!!)
                .onErrorResume { Mono.error(coverUnexpectedException(it)) }
                .flatMap { Mono.empty() }
}
