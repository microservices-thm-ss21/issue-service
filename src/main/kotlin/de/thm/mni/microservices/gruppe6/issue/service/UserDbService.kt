package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.UserRepository
import de.thm.mni.microservices.gruppe6.lib.classes.userService.UserId
import de.thm.mni.microservices.gruppe6.lib.event.DataEventCode.*
import de.thm.mni.microservices.gruppe6.lib.event.UserDataEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class UserDbService(@Autowired val userRepo: UserRepository) {

    fun getAllUsers(): Flux<UserId> = userRepo.findAll()

    fun hasUser(userId: UUID): Mono<Boolean> = userRepo.existsById(userId)

    fun receiveUpdate(userDataEvent: UserDataEvent) {
        when (userDataEvent.code){
            CREATED -> userRepo.saveUser(userDataEvent.id).subscribe()
            DELETED -> userRepo.deleteById(userDataEvent.id).subscribe()
            UPDATED -> {}
            else -> throw IllegalArgumentException("Unexpected code for userDataEvent: ${userDataEvent.code}")
        }
    }
}
