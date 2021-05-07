package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.Project
import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import java.util.*

@ExtendWith(MockitoExtension::class)
class ProjectDbServiceTests(
    @Mock private val repository: ProjectRepository
) {

    private val service = ProjectDbService(repository)


    private fun getTestProject(id: UUID) : Project {
        return Project(
            id
        )
    }

    private fun mockRepositorySave(id: UUID) {
        Mockito.`when`(repository.save(any<Project>())).then {
            val hopefullyProject = it.arguments.first()
            if (hopefullyProject is Project) {
                hopefullyProject.projectId = id
            }
            Mono.just(hopefullyProject)
        }
    }

    @Test
    fun testShouldHave(){

    }

    @Test
    fun testShouldCreate() {
        /*
        val id = UUID.randomUUID()
        val testIssue = getTestProject(id)

        mockRepositorySave(id)

        StepVerifier
            .create(service.create(testIssue))
            .consumeNextWith { i ->
                assert(i.name == testIssue.name)
                assert(i.description == testIssue.description)
                assert(i.id != null)
                Mockito.verify(repository).save(testIssue)
            }
            .verifyComplete()
*/
    }

    @Test
    fun testShouldDelete() {

    }

    @Test
    fun testShouldGetAll() {

    }

}
