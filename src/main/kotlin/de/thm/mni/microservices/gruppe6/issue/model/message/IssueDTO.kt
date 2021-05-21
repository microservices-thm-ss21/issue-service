package de.thm.mni.microservices.gruppe6.issue.model.message

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.util.*

/**
 * DTO = Data Transport Object
 */
data class IssueDTO (
    var message: String? = null,
    var assignedUserId: UUID? = null,
    var projectId: UUID? = null,
    @JsonFormat(pattern = "dd.MM.yyyy")
    var deadline: LocalDate? = null
)
