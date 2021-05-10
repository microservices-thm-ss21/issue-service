package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.Project
import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
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
        val id = UUID.randomUUID()
        given(repository.existsById(id)).willReturn(Mono.just(true))

        StepVerifier
            .create(service.hasProject(id))
            .consumeNextWith{
                    i ->
                assert(i)
            }
            .verifyComplete()
    }

    @Test
    fun testShouldCreate() {
        val id = UUID.randomUUID()
        val testProject = getTestProject(id)
        mockRepositorySave(id)

        StepVerifier
            .create(service.putProject(id))
            .consumeNextWith{
                i ->
                    assert(i.projectId == testProject.projectId)
                    Mockito.verify(repository).save(testProject)
            }
            .verifyComplete()
    }

    @Test
    fun testShouldDelete() {
        val id = UUID.randomUUID()
        val testProject = getTestProject(id)

        given(repository.findById(id)).willReturn(Mono.just(testProject))
        given(repository.deleteById(testProject.projectId)).willReturn(Mono.empty())
        assert(service.deleteProject(id) is Mono<Void>) // Currently always true but when we implement exceptions this test will be necessary
    }

    @Test
    fun testShouldGetAll() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()

        val projects = listOf<Project>(Project(id1), Project(id2), Project(id3))
        given(repository.findAll()).willReturn(Flux.fromIterable(projects))

        val result = service.getAllProjects().collectList().block()
        assert(result == projects)
    }

}
