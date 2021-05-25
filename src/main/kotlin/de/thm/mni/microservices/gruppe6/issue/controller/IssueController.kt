package de.thm.mni.microservices.gruppe6.issue.controller

import de.thm.mni.microservices.gruppe6.issue.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.issue.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.issue.service.IssueDbService
import de.thm.mni.microservices.gruppe6.lib.exception.ServiceException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Controller
@RequestMapping("/api/issues")
@CrossOrigin
class IssueController(@Autowired val issueService: IssueDbService) {

    @GetMapping("")
    fun getAllIssues(): Flux<Issue> = issueService.getAllIssues()

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun createIssue(@RequestBody issueDTO: IssueDTO): Mono<Issue> =
        issueService.createIssue(issueDTO).onErrorResume { Mono.error(ServiceException(HttpStatus.CONFLICT, it)) }

    @GetMapping("{issueId}")
    fun getIssue(@PathVariable issueId: UUID): Mono<Issue> =
        issueService.getIssue(issueId).switchIfEmpty(Mono.error(ServiceException(HttpStatus.NOT_FOUND)))

    @PutMapping("{issueId}")
    fun updateIssue(
        @PathVariable issueId: UUID,
        @RequestBody issueDTO: IssueDTO
    ): Mono<Issue> = issueService.updateIssue(issueId, issueDTO).onErrorResume { Mono.error(ServiceException(HttpStatus.CONFLICT, it)) }

    @DeleteMapping("{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteIssue(@PathVariable issueId: UUID): Mono<Void> =
        issueService.deleteIssue(issueId)

    @GetMapping("project/{projectId}")
    fun getAllProjectIssues(@PathVariable projectId: UUID): Flux<Issue> = issueService.getAllProjectIssues(projectId)

}
