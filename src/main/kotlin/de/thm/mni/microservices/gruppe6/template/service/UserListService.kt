package de.thm.mni.microservices.gruppe6.template.service

import de.thm.mni.microservices.gruppe6.template.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.template.model.message.IssueDTO
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Deprecated("Dummy implementation before database connection was configured.")
@Component
class UserListService {

    private val issueList: MutableList<Issue> = ArrayList()
    private var nextId: Long = 0

    fun getAllUsers(): Flux<Issue> = Flux.fromIterable(issueList)

    fun putUser(issueDTO: IssueDTO): Long {
        issueList.add(Issue(nextId, issueDTO))
        return nextId++
    }

    fun updateUser(id: Long, issueDTO: IssueDTO): Issue {
        val user = issueList.find { it.id == id}!!
        user.username = issueDTO.username!!
        user.lastName = issueDTO.lastName!!
        user.name = issueDTO.name!!
        user.dateOfBirth = issueDTO.dateOfBirth!!
        user.globalRole = issueDTO.globalRole!!
        return user
    }

    fun deleteUser(id: Long): Boolean {
        return issueList.removeIf { it.id == id }
    }

}
