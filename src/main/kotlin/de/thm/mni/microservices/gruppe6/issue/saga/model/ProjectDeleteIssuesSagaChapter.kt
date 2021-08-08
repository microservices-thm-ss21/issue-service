package de.thm.mni.microservices.gruppe6.issue.saga.model

import de.thm.mni.microservices.gruppe6.lib.classes.issueService.Issue
import de.thm.mni.microservices.gruppe6.lib.classes.projectService.ProjectId

/**
 * Data class holding the data for a compensating transaction
 * of the ProjectDeletedSaga orchestrated by the project-service.
 */
data class ProjectDeleteIssuesSagaChapter(
    val project: ProjectId,
    val projectIssues: List<Issue>
)