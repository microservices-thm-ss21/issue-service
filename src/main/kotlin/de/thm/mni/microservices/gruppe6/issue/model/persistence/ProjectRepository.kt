package de.thm.mni.microservices.gruppe6.issue.model.persistence

import de.thm.mni.microservices.gruppe6.lib.classes.projectService.ProjectId
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.*

interface ProjectRepository : ReactiveCrudRepository<ProjectId, UUID> {

    @Query("INSERT INTO projectIds VALUES (:projectId)")
    fun saveProject(projectId: UUID): Mono<Void>
}
