package de.thm.mni.microservices.gruppe6.template.model.persistence

import com.fasterxml.jackson.annotation.JsonFormat
import de.thm.mni.microservices.gruppe6.template.model.message.IssueDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Table("users")
data class Issue(
    @Id var id: UUID? = null,
    var message: String,
    var userID: UUID? = null,
    @JsonFormat(pattern = "dd.MM.yyyy")
    var deadline: LocalDate? = null,
    var createTime: LocalDateTime,
    var globalRole: String,
    var updateTime: LocalDateTime? = null
) {
    constructor(id: UUID, issueDTO: IssueDTO): this(
         id
        ,issueDTO.message!!
        ,issueDTO.userID
        ,issueDTO.deadline
        ,LocalDateTime.now()
        ,issueDTO.globalRole!!
        ,null
    )

}

