package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.User
import de.thm.mni.microservices.gruppe6.issue.model.persistence.UserRepository
import de.thm.mni.microservices.gruppe6.lib.event.DataEventCode.*
import de.thm.mni.microservices.gruppe6.lib.event.UserDataEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class UserDbService(@Autowired val userRepo: UserRepository) {

    fun getAllUsers(): Flux<User> = userRepo.findAll()

    fun hasUser(userId: UUID): Mono<Boolean> = userRepo.existsById(userId)

    fun receiveUpdate(userEvent: UserDataEvent) {
        when (userEvent.code){
            CREATED -> userRepo.save(User(userEvent.id))
            DELETED -> userRepo.deleteById(userEvent.id)
            UPDATED -> {}
            else -> throw IllegalArgumentException("Unexpected code for userEvent: ${userEvent.code}")
        }
    }
}
