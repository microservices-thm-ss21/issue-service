package de.thm.mni.microservices.gruppe6.template.model.message

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.util.*

/**
 * DTO = Data Transport Object
 */
class IssueDTO {
    var message: String? = null
    var userID: UUID? = null
    @JsonFormat(pattern = "dd.MM.yyyy")
    var deadline: LocalDate? = null
    var globalRole: String? = null
}
