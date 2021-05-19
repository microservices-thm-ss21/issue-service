package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.message.IssueDTO
import de.thm.mni.microservices.gruppe6.issue.model.persistence.Issue
import de.thm.mni.microservices.gruppe6.issue.model.persistence.IssueRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.annotation.Bean
import org.springframework.jms.core.JmsTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.jms.Message

@ExtendWith(MockitoExtension::class)
class IssueDbServiceTests(
    @Mock private val repository: IssueRepository,
    @Mock private val jmsTemplate: JmsTemplate,

    ) {
    private val service = IssueDbService(repository, jmsTemplate)

    @Bean
    fun jmsTemplate(): JmsTemplate? {
        return Mockito.mock(JmsTemplate::class.java)
    }

    private fun getTestIssueDTO(
        issue: Issue
    ): IssueDTO {
        val issueDTO = IssueDTO()
        issueDTO.message = issue.message
        issueDTO.assignedUserId = issue.assignedUserId
        issueDTO.projectId = issue.projectId
        issueDTO.deadline = issue.deadline
        return issueDTO
    }

    private fun getTestIssueDTO(
        projectId: UUID = UUID.randomUUID(),
        message: String = "xXRausAusDenSchulden69Xx",
        assignedUserId: UUID? = UUID.fromString("a443ffd0-f7a8-44f6-8ad3-87acd1e91042"),
        deadline: LocalDate? = null,
        globalRole: String = "SCHULDENBERATER"
    ): IssueDTO {
        val issueDTO = IssueDTO()
        issueDTO.message = message
        issueDTO.assignedUserId = assignedUserId
        issueDTO.projectId = projectId
        issueDTO.deadline = deadline
        issueDTO.globalRole = globalRole
        return issueDTO
    }

    private fun getTestIssue(
        issueDTO: IssueDTO
    ): Issue {
        return Issue(issueDTO).also { it.id = UUID.randomUUID() }
    }

    private fun getTestIssue(
        id: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        message: String = "xXRausAusDenSchulden69Xx",
        assignedUserId: UUID? = UUID.fromString("a443ffd0-f7a8-44f6-8ad3-87acd1e91042"),
        deadline: LocalDate? = null,
        createTime: LocalDateTime = LocalDateTime.now(),
        updateTime: LocalDateTime? = null
    ): Issue {
        return Issue(
            id,
            projectId,
            message,
            assignedUserId,
            deadline,
            createTime,
            updateTime
        )
    }

    private fun mockRepositorySave(issue: Issue) {
        Mockito.`when`(repository.save(any(Issue::class.java))).thenReturn(Mono.just(issue))
    }

    private fun mockJmsTemplate() {
        Mockito.`when`(jmsTemplate.convertAndSend(any(Message::class.java))).thenReturn(Unit)
    }

    @Test
    fun testShouldGetByID() {
        val id = UUID.randomUUID()
        val testIssue = getTestIssue(id)
        given(repository.findById(id)).willReturn(Mono.just(testIssue))

        StepVerifier
            .create(service.getIssue(id))
            .consumeNextWith { i ->
                assert(i.equals(testIssue))
            }
            .verifyComplete()
    }

    @Test
    fun testShouldCreate() {
        val testIssueDTO = getTestIssueDTO()
        val testIssue = getTestIssue(testIssueDTO)

        mockRepositorySave(testIssue)

        StepVerifier
            .create(service.putIssue(Mono.just(testIssueDTO)))
            .consumeNextWith { i ->
                assert(i == testIssue)
                Mockito.verify(repository).save(any())
            }
            .verifyComplete()
    }

    @Test
    fun testShouldDelete() {
        val id = UUID.randomUUID()
        val testIssue = getTestIssue(id)

        given(repository.findById(id)).willReturn(Mono.just(testIssue))
        given(repository.deleteById(testIssue.id!!)).willReturn(Mono.empty())


        assert(service.deleteIssue(id) is Mono<Void>) // Currently always true but when we implement exceptions this test will be necessary
    }

    @Test
    fun testShouldUpdate() {
        val issueId = UUID.randomUUID()
        val testIssueOld = getTestIssue(issueId)
        val testIssueNew = testIssueOld.copy(message = "This is a new message")
        val testIssueDTO = getTestIssueDTO(testIssueNew)


        given(repository.findById(issueId)).willReturn(Mono.just(testIssueOld))

        mockRepositorySave(testIssueNew)

        StepVerifier
            .create(service.putIssue(Mono.just(testIssueDTO)))
            .consumeNextWith { i ->
                run {
                    assert(i == testIssueNew)
                    assert(i != testIssueOld)
                }
                Mockito.verify(repository).save(any())
            }
            .verifyComplete()

    }

    @Test
    fun testShouldGetAllProjectIssues() {
        val prjId = UUID.randomUUID()

        val issues =
            listOf(getTestIssue(projectId = prjId), getTestIssue(projectId = prjId), getTestIssue(projectId = prjId))
        given(repository.getIssuesByProjectId(prjId)).willReturn(Flux.fromIterable(issues))

        val result = service.getAllProjectIssues(prjId).collectList().block()
        assert(result == issues)
    }

}
