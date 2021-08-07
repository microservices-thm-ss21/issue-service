package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import de.thm.mni.microservices.gruppe6.lib.classes.projectService.ProjectId
import de.thm.mni.microservices.gruppe6.lib.event.*
import de.thm.mni.microservices.gruppe6.lib.event.DataEventCode.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

/**
 * Implements the functionality used to process projects
 */
@Component
class ProjectDbService(
    @Autowired val projectRepo: ProjectRepository,
    @Autowired val sender: JmsTemplate
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

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
            DELETED -> deleteProjectIssues(projectDataEvent.id)
            UPDATED -> {
            }
            else -> throw IllegalArgumentException("Unexpected code for projectEvent: ${projectDataEvent.code}")
        }
    }

    private fun deleteProjectIssues(projectId: UUID) {
        projectRepo
            .deleteById(projectId)
            .publishOn(Schedulers.boundedElastic())
            .doOnError {
                logger.error("Error on deleting issues!", it)
                sender.convertAndSend(
                    EventTopic.SagaEvents.topic,
                    DeletedIssuesSagaEvent(SagaReferenceType.PROJECT, projectId, false)
                )
            }.doOnSuccess {
                logger.debug("Deleted issues for project {}", projectId)
                sender.convertAndSend(
                    EventTopic.SagaEvents.topic,
                    DeletedIssuesSagaEvent(SagaReferenceType.PROJECT, projectId, true)
                )
            }.subscribe()
    }
}
