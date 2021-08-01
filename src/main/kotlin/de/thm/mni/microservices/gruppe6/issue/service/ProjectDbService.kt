package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired

import de.thm.mni.microservices.gruppe6.lib.classes.projectService.ProjectId
import de.thm.mni.microservices.gruppe6.lib.event.ProjectDataEvent
import de.thm.mni.microservices.gruppe6.lib.event.DataEventCode.*
import org.springframework.stereotype.Component
import java.util.*
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

@Component
class ProjectDbService(@Autowired val projectRepo: ProjectRepository) {

    fun getAllProjects(): Flux<ProjectId> = projectRepo.findAll()

    fun hasProject(projectId: UUID): Mono<Boolean> = projectRepo.existsById(projectId)

    fun receiveUpdate(projectDataEvent: ProjectDataEvent) {
        when (projectDataEvent.code){
            CREATED -> projectRepo.saveProject(projectDataEvent.id).subscribe()
            DELETED -> projectRepo.deleteById(projectDataEvent.id).subscribe()
            UPDATED -> {}
            else -> throw IllegalArgumentException("Unexpected code for projectEvent: ${projectDataEvent.code}")
        }
    }
}
