package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.UserRepository
import de.thm.mni.microservices.gruppe6.lib.classes.userService.UserId
import de.thm.mni.microservices.gruppe6.lib.event.DataEventCode.*
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
class UserDbServiceTests(
    @Mock private val repository: UserRepository
) {

    private val service = UserDbService(repository)


    private fun getTestUser(id: UUID) : UserId {
        return UserId(
            id
        )
    }

    private fun mockRepositorySave(id: UUID) {
        Mockito.`when`(repository.save(any())).then {
            val hopefullyUser = it.arguments.first()
            if (hopefullyUser is UserId) {
                hopefullyUser.id = id
            }
            Mono.just(hopefullyUser)
        }
    }

    @Test
    fun testShouldHave(){
        val id = UUID.randomUUID()
        given(repository.existsById(id)).willReturn(Mono.just(true))

        StepVerifier
            .create(service.hasUser(id))
            .consumeNextWith{
                    i ->
                assert(i)
            }
            .verifyComplete()
    }

    /*@Test
    fun testShouldCreate() {
        val id = UUID.randomUUID()
        val testUser = getTestUser(id)
        mockRepositorySave(id)

        StepVerifier
            .create(service.putUser(id))
            .consumeNextWith{
                    i ->
                assert(i.userId == testUser.userId)
                Mockito.verify(repository).save(testUser)
            }
            .verifyComplete()
    }*/

    /*@Test
    fun testShouldDelete() {
        val id = UUID.randomUUID()
        val testUser = getTestUser(id)

        given(repository.findById(id)).willReturn(Mono.just(testUser))
        given(repository.deleteById(testUser.userId)).willReturn(Mono.empty())
        assert(service.deleteUser(id) is Mono<Void>) // Currently always true but when we implement exceptions this test will be necessary
    }*/

    @Test
    fun testShouldGetAll() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val id3 = UUID.randomUUID()

        val users = listOf(getTestUser(id1), getTestUser(id2), getTestUser(id3))
        given(repository.findAll()).willReturn(Flux.fromIterable(users))

        val result = service.getAllUsers().collectList().block()
        assert(result == users)
    }

}
