package de.thm.mni.microservices.gruppe6.issue.controller

import de.thm.mni.microservices.gruppe6.issue.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.issue.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.issue.service.IssueDbService
import de.thm.mni.microservices.gruppe6.lib.classes.userService.User
import de.thm.mni.microservices.gruppe6.lib.exception.ServiceException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/issues")
@CrossOrigin
class IssueController(@Autowired val issueService: IssueDbService) {

    // toDo: remove when jwt works
    val jwtUser = User(
        UUID.fromString("a443ffd0-f7a8-44f6-8ad3-87acd1e91042")
        ,"Peter_Zwegat"
        ,"password"
        , "Peter"
        , "Zwegat"
        ,"peter.zwegat@mni.thm.de"
        , LocalDate.now()
        , LocalDateTime.now()
        ,"USER"
        ,null)

    @GetMapping("")
    fun getAllIssues(): Flux<Issue> = issueService.getAllIssues()

    @GetMapping("project/{projectId}")
    fun getAllProjectIssues(@PathVariable projectId: UUID): Flux<Issue> =
        issueService.getAllProjectIssues(projectId)

    @GetMapping("user/{userId}")
    fun getAllAssignedIssues(@PathVariable userId: UUID): Flux<Issue> = issueService.getAllAssignedIssues(userId)

    @GetMapping("{issueId}")
    fun getIssue(@PathVariable issueId: UUID): Mono<Issue> =
        issueService.getIssue(issueId).switchIfEmpty(Mono.error(ServiceException(HttpStatus.NOT_FOUND)))

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun createIssue(@RequestBody issueDTO: IssueDTO): Mono<Issue> =
        // toDo JWT USER ID as creator ID
        issueService.createIssue(issueDTO, jwtUser.id!!).onErrorResume { Mono.error(ServiceException(HttpStatus.CONFLICT, cause = it)) }

    @PutMapping("{issueId}")
    fun updateIssue(
        @PathVariable issueId: UUID,
        @RequestBody issueDTO: IssueDTO
        // toDo JWT USER ID as creator ID
    ): Mono<Issue> = issueService.updateIssue(issueId, issueDTO, jwtUser).onErrorResume { Mono.error(ServiceException(HttpStatus.CONFLICT, cause = it)) }

    @DeleteMapping("{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteIssue(@PathVariable issueId: UUID): Mono<Void> =
        // toDo JWT USER ID as deleter ID
        issueService.deleteIssue(issueId, jwtUser)



    @GetMapping("user/{userId}")
    fun getAllAssignedIssues(@PathVariable userId: UUID): Flux<Issue> = issueService.getAllAssignedIssues(userId)
}
