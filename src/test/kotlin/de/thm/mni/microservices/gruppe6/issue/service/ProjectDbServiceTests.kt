package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import de.thm.mni.microservices.gruppe6.lib.classes.projectService.ProjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
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


    private fun getTestProject(id: UUID): ProjectId {
        return ProjectId(
            id
        )
    }


    @Test
    fun testShouldHave() {
        val id = UUID.randomUUID()
        given(repository.existsById(id)).willReturn(Mono.just(true))

        StepVerifier
            .create(service.hasProject(id))
            .consumeNextWith { i ->
                assert(i)
            }
            .verifyComplete()
    }

    @Test
    fun testShouldGetAll() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()

        val projects = listOf(getTestProject(id1), getTestProject(id2), getTestProject(id3))
        given(repository.findAll()).willReturn(Flux.fromIterable(projects))

        val result = service.getAllProjects().collectList().block()
        assert(result == projects)
    }

}
