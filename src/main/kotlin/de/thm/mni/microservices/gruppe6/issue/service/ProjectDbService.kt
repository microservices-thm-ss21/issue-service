package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import de.thm.mni.microservices.gruppe6.lib.classes.projectService.ProjectId
import de.thm.mni.microservices.gruppe6.lib.event.DataEventCode.*
import de.thm.mni.microservices.gruppe6.lib.event.ProjectDataEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

/**
 * Implements the functionality used to process projects
 */
@Component
class ProjectDbService(@Autowired val projectRepo: ProjectRepository) {

    /**
     * Returns all projectIds
     * @return flux of projectIds
     */
    fun getAllProjects(): Flux<ProjectId> = projectRepo.findAll()

    /**
     * Checks if projectId exists in database
     * @param projectId
     * @return boolean
     */
    fun hasProject(projectId: UUID): Mono<Boolean> = projectRepo.existsById(projectId)

    /**
     * Handles all the incoming ProjectDataEvents
     * @param projectDataEvent
     */
    fun receiveUpdate(projectDataEvent: ProjectDataEvent) {
        when (projectDataEvent.code) {
            CREATED -> projectRepo.saveProject(projectDataEvent.id).subscribe()
            DELETED -> projectRepo.deleteById(projectDataEvent.id).subscribe()
            UPDATED -> {
            }
            else -> throw IllegalArgumentException("Unexpected code for projectEvent: ${projectDataEvent.code}")
        }
    }
}
