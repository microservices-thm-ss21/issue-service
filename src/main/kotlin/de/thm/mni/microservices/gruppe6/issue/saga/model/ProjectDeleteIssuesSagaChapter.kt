package de.thm.mni.microservices.gruppe6.issue.saga.model

import de.thm.mni.microservices.gruppe6.lib.classes.issueService.Issue
import de.thm.mni.microservices.gruppe6.lib.classes.projectService.ProjectId

data class ProjectDeleteIssuesSagaChapter(val project: ProjectId, val projectIssues: List<Issue>)