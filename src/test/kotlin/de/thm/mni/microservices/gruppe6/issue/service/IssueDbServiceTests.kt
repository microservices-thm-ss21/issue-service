package de.thm.mni.microservices.gruppe6.issue.service


import de.thm.mni.microservices.gruppe6.issue.model.persistence.IssueRepository
import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import de.thm.mni.microservices.gruppe6.issue.model.persistence.UserRepository
import de.thm.mni.microservices.gruppe6.issue.requests.Requester
import de.thm.mni.microservices.gruppe6.lib.classes.issueService.Issue
import de.thm.mni.microservices.gruppe6.lib.classes.issueService.IssueDTO
import de.thm.mni.microservices.gruppe6.lib.classes.userService.GlobalRole
import de.thm.mni.microservices.gruppe6.lib.classes.userService.User
import de.thm.mni.microservices.gruppe6.lib.exception.ServiceException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.jms.core.JmsTemplate
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class IssueDbServiceTests(
    @Mock private val issueRepo: IssueRepository,
    @Mock private val sender: JmsTemplate,
    @Mock val requester: Requester,
    @Mock val userRepo: UserRepository,
    @Mock val projectRepo: ProjectRepository

) {

    private val issueDbService = IssueDbService(issueRepo, sender, requester, userRepo, projectRepo)

    private fun getTestIssueDTO(assignedUserId: UUID, projectId: UUID): IssueDTO {
        val issueDTO = IssueDTO()
        issueDTO.message = "Message"
        issueDTO.assignedUserId = assignedUserId
        issueDTO.projectId = projectId
        return issueDTO
    }

    private fun getTestIssue(
        issueDTO: IssueDTO,
        creatorId: UUID
    ): Issue {
        return Issue(issueDTO, creatorId).also { it.id = UUID.randomUUID() }
    }

    private fun createTestUser(): User {
        return User(
            UUID.randomUUID(), "username", "Password", "name", "lastName", "email",
            LocalDate.now(), LocalDateTime.now(), GlobalRole.ADMIN.name
        )
    }

    @Test
    fun testGetIssue() {
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val issueDTO = getTestIssueDTO(userId, projectId)
        val issue = getTestIssue(issueDTO, userId)
        given(issueRepo.findById(issue.id!!)).willReturn(Mono.just(issue))

        val returnedIssue = issueDbService.getIssue(issue.id!!).block()
        assertThat(returnedIssue).isNotNull
        assertThat(returnedIssue).isEqualTo(issue)
    }

    @Test
    fun testCreateIssue() {
        val user = createTestUser()
        val projectId = UUID.randomUUID()
        val issueDTO = getTestIssueDTO(user.id!!, projectId)
        val issue = getTestIssue(issueDTO, user.id!!)
        val service = spy(issueDbService)

        given(issueRepo.existsById(issue.id!!)).willReturn(Mono.just(true))
        given(userRepo.existsById(user.id!!)).willReturn(Mono.just(true))
        given(projectRepo.existsById(projectId)).willReturn(Mono.just(true))
        given(issueRepo.save(any())).willReturn(Mono.just(issue))
        doReturn(Mono.just(true)).`when`(service).sendMemberRequest(projectId, user.id!!)

        val returnedIssue = service.createIssue(issueDTO, user).block()
        assertThat(returnedIssue).isNotNull
        assertThat(returnedIssue).isEqualTo(issue)

    }

    @Test
    fun testUpdateIssue() {
        val user = createTestUser()
        user.id = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val issueDTO = getTestIssueDTO(user.id!!, projectId)
        val issue = getTestIssue(issueDTO, user.id!!)
        val newIssueDTO = issueDTO.copy()
        newIssueDTO.message = "new"
        val newIssue = getTestIssue(newIssueDTO, user.id!!)

        val service = spy(issueDbService)

        given(userRepo.existsById(user.id!!)).willReturn(Mono.just(true))
        given(issueRepo.findById(issue.id!!)).willReturn(Mono.just(issue))
        given(userRepo.existsById(user.id!!)).willReturn(Mono.just(true))
        given(projectRepo.existsById(projectId)).willReturn(Mono.just(true))
        given(issueRepo.save(any())).willReturn(Mono.just(newIssue))
        doReturn(Mono.just(issue)).`when`(service).checkProjectMember(issue, user)

        val createdIssue = service.updateIssue(issue.id!!, issueDTO, user).block()

        assertThat(createdIssue).isNotNull
        assertThat(createdIssue!!.message == newIssue.message)
        assertThat(createdIssue.id == newIssue.id)
        assertThat(createdIssue.assignedUserId == newIssue.assignedUserId)
    }

    @Test
    fun testDeleteIssue() {
        val service = spy(issueDbService)
        val user = createTestUser()
        user.id = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val issueDTO = getTestIssueDTO(user.id!!, projectId)
        val issue = getTestIssue(issueDTO, user.id!!)
        given(issueRepo.findById(issue.id!!)).willReturn(Mono.just(issue))
        given(issueRepo.deleteById(issue.id!!)).willReturn(Mono.empty())
        doReturn(Mono.just(issue)).`when`(service).checkProjectMember(issue, user)

        service.deleteIssue(issue.id!!, user).block()

        verify(issueRepo, times(1)).findById(issue.id!!)
        verify(issueRepo, times(1)).deleteById(issue.id!!)
        verify(service, times(1)).checkProjectMember(issue, user)
    }

    @Test
    fun testCheckProjectMember1() {
        val user = createTestUser()
        user.id = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val issueDTO = getTestIssueDTO(user.id!!, projectId)
        val issue = getTestIssue(issueDTO, user.id!!)
        val returnedIssue = issueDbService.checkProjectMember(issue, user).block()
        assertThat(returnedIssue).isNotNull
        assertThat(returnedIssue!!.message == issue.message)
        assertThat(returnedIssue.id == issue.id)
        assertThat(returnedIssue.assignedUserId == issue.assignedUserId)
    }

    @Test
    fun testCheckProjectMember2() {
        val user = createTestUser()
        user.id = UUID.randomUUID()
        user.globalRole = GlobalRole.USER.name
        val projectId = UUID.randomUUID()
        val issueDTO = getTestIssueDTO(user.id!!, projectId)
        val issue = getTestIssue(issueDTO, UUID.randomUUID())
        given(
            requester.forwardGetRequestMono(
                "http://project-service:8082",
                "api/projects/${issue.projectId}/members/${user.id}/exists",
                Boolean::class.java
            )
        )
            .willReturn(Mono.just(true))

        val returnedIssue = issueDbService.checkProjectMember(issue, user).block()
        assertThat(returnedIssue).isNotNull
        assertThat(returnedIssue!!.message == issue.message)
        assertThat(returnedIssue.id == issue.id)
        assertThat(returnedIssue.assignedUserId == issue.assignedUserId)

        verify(requester, times(1)).forwardGetRequestMono(
            "http://project-service:8082",
            "api/projects/${issue.projectId}/members/${user.id}/exists",
            Boolean::class.java
        )
    }

    @Test
    fun testCheckProjectMember3() {
        val user = createTestUser()
        user.id = UUID.randomUUID()
        user.globalRole = GlobalRole.USER.name
        val projectId = UUID.randomUUID()
        val issueDTO = getTestIssueDTO(user.id!!, projectId)
        val issue = getTestIssue(issueDTO, UUID.randomUUID())
        given(
            requester.forwardGetRequestMono(
                "http://project-service:8082",
                "api/projects/${issue.projectId}/members/${user.id}/exists",
                Boolean::class.java
            )
        )
            .willReturn(Mono.just(false))
        var error: Throwable? = null
        try {
            issueDbService.checkProjectMember(issue, user).block()
        } catch (e: Throwable) {
            error = e
        }

        assertThat(error != null)
        assertThat(error is ServiceException)
        assertThat((error as ServiceException).status.value() == HttpStatus.FORBIDDEN.value())

        verify(requester, times(1)).forwardGetRequestMono(
            "http://project-service:8082",
            "api/projects/${issue.projectId}/members/${user.id}/exists",
            Boolean::class.java
        )
    }
}
