package de.thm.mni.microservices.gruppe6.template.controller

import de.thm.mni.microservices.gruppe6.template.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.template.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.template.service.IssueDbService
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

    @GetMapping("/{prjId}/{id}")
    fun getIssue(@PathVariable prjId: UUID, @PathVariable id: UUID): Mono<Issue> = issueService.getIssue(prjId, id)

    @PutMapping("/{prjId}")
    fun putIssue(@PathVariable prjId: UUID, @RequestBody issueDTO: IssueDTO): Mono<Issue> = issueService.putIssue(prjId, issueDTO)

    @PostMapping("/{prjId}/{id}")
    fun updateIssue(@PathVariable prjId: UUID, @PathVariable id: UUID, @RequestBody issueDTO: IssueDTO) = issueService.updateIssue(prjId, id, issueDTO)

    @DeleteMapping("/{prjId}/{id}")
    fun deleteIssue(@PathVariable prjId: UUID, @PathVariable id: UUID) = issueService.deleteIssue(prjId, id)
}
