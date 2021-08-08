package de.thm.mni.microservices.gruppe6.issue.saga

import de.thm.mni.microservices.gruppe6.issue.model.persistence.IssueRepository
import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import de.thm.mni.microservices.gruppe6.lib.classes.projectService.ProjectId
import de.thm.mni.microservices.gruppe6.lib.event.EventTopic
import de.thm.mni.microservices.gruppe6.lib.event.ProjectSagaEvent
import de.thm.mni.microservices.gruppe6.lib.event.ProjectSagaStatus
import de.thm.mni.microservices.gruppe6.lib.event.SagaReferenceType
import org.slf4j.LoggerFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class ProjectDeleteIssuesSagaService(
    private val issueRepository: IssueRepository,
    private val projectRepository: ProjectRepository,
    private val jmsTemplate: JmsTemplate
) {
    private val sagaChapters: HashMap<UUID, ProjectDeleteIssuesSagaChapter> = HashMap()
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun receiveSagaEvent(sagaEvent: ProjectSagaEvent) {
        if (sagaEvent.referenceType != SagaReferenceType.PROJECT ||
            (!sagaChapters.containsKey(sagaEvent.referenceValue)
                    && sagaEvent.projectSagaStatus == ProjectSagaStatus.COMPLETE)) {
            logger.error("Received event does not reference any continued saga!")
            return
        }
        when (sagaEvent.projectSagaStatus) {
            ProjectSagaStatus.BEGIN -> deleteIssues(sagaEvent.referenceValue)
            ProjectSagaStatus.COMPLETE -> completeSaga(sagaEvent)
            else -> {} // ignore
        }
    }

    private fun completeSaga(sagaEvent: ProjectSagaEvent) {
        if (sagaEvent.success) {
            logger.info("Saga for project {} completed successfully!", sagaEvent.referenceValue)
            sagaChapters.remove(sagaEvent.referenceValue)
        } else {
            rollbackIssueDeletion(sagaEvent.referenceValue)
        }
    }

    private fun deleteIssues(projectId: UUID) {
        issueRepository
            .getIssuesByProjectId(projectId)
            .collectList()
            .doOnNext { issues ->
                sagaChapters.computeIfAbsent(projectId) { uuid ->
                    ProjectDeleteIssuesSagaChapter(ProjectId(uuid), issues)
                }
            }.flatMap {
                projectRepository.deleteById(projectId)
            }.doOnError {
                logger.error("Error on deleting issues for project {}!", projectId)
                jmsTemplate.convertAndSend(
                    EventTopic.SagaEvents.topic,
                    ProjectSagaEvent(projectId, ProjectSagaStatus.ISSUES_DELETED, false)
                )
            }.doOnSuccess {
                logger.error("Issues for project {} deleted!", projectId)
                jmsTemplate.convertAndSend(
                    EventTopic.SagaEvents.topic,
                    ProjectSagaEvent(projectId, ProjectSagaStatus.ISSUES_DELETED, true)
                )
            }.subscribe()
    }

    fun rollbackIssueDeletion(projectId: UUID) {
        logger.error("Rollback saga for project {}", projectId)
        val sagaChapter = sagaChapters[projectId]!!
        projectRepository
            .save(sagaChapter.project)
            .flatMapMany {
                issueRepository.saveAll(sagaChapter.projectIssues)
            }.subscribe()
        sagaChapters.remove(projectId)
    }

}