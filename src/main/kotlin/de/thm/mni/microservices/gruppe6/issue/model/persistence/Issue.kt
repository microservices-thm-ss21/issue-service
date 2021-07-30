package de.thm.mni.microservices.gruppe6.issue.model.persistence

import com.fasterxml.jackson.annotation.JsonFormat
import de.thm.mni.microservices.gruppe6.issue.model.message.IssueDTO
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
    var assignedUserId: UUID? = null,
    var creatorId: UUID,
    @JsonFormat(pattern = "dd.MM.yyyy")
    var deadline: LocalDate? = null,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime? = null
) {
    constructor(issueDTO: IssueDTO, creatorId: UUID): this(
         null
        ,issueDTO.projectId!!
        ,issueDTO.message!!
        ,issueDTO.assignedUserId
        ,creatorId
        ,issueDTO.deadline
        ,LocalDateTime.now()
        ,null
    )

}

