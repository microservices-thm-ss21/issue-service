package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import de.thm.mni.microservices.gruppe6.issue.model.persistence.Project
import de.thm.mni.microservices.gruppe6.lib.event.ProjectEvent
import org.springframework.stereotype.Component
import java.util.*
import reactor.core.publisher.Mono
import reactor.core.publisher.Flux

@Component
class ProjectDbService(@Autowired val projectRepo: ProjectRepository) {

    fun getAllProjects(): Flux<Project> = projectRepo.findAll()

    fun putProject(projectId: UUID): Mono<Project> = projectRepo.save(Project(projectId))

    fun deleteProject(projectId: UUID): Mono<Void> = projectRepo.deleteById(projectId)

    fun hasProject(projectId: UUID): Mono<Boolean> = projectRepo.existsById(projectId)

    fun receiveUpdate(projectEvent: ProjectEvent) {

    }
}
