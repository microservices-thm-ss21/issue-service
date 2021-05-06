package de.thm.mni.microservices.gruppe6.template.model.persistence

import com.fasterxml.jackson.annotation.JsonFormat
import de.thm.mni.microservices.gruppe6.template.model.message.IssueDTO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Table("issues")
data class Issue(
    @Id var id: UUID? = null,
    var projectId: UUID,
    var message: String,
    var userId: UUID? = null,
    @JsonFormat(pattern = "dd.MM.yyyy")
    var deadline: LocalDate? = null,
    var globalRole: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime? = null
) {
    constructor(id: UUID?, projectId: UUID, issueDTO: IssueDTO): this(
         id
        ,projectId
        ,issueDTO.message!!
        ,issueDTO.userId
        ,issueDTO.deadline
        ,issueDTO.globalRole!!
        ,LocalDateTime.now()
        ,null
    )

}

