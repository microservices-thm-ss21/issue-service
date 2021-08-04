package de.thm.mni.microservices.gruppe6.issue.model.persistence

import de.thm.mni.microservices.gruppe6.lib.classes.issueService.Issue
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.util.*

interface IssueRepository : ReactiveCrudRepository<Issue, UUID> {

    fun getIssuesByProjectId(project_id: UUID): Flux<Issue>

    fun getIssuesByAssignedUserId(userId: UUID): Flux<Issue>
}
