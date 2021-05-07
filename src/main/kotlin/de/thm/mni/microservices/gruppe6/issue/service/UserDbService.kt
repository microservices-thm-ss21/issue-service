package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.User
import de.thm.mni.microservices.gruppe6.issue.model.persistence.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Component
class UserDbService(@Autowired val userRepo: UserRepository) {

    fun getAllUsers(): Flux<User> = userRepo.findAll()

    fun hasUser(userId: UUID): Mono<Boolean> = userRepo.existsById(userId)

    fun putUser(userId: UUID): Mono<User> = userRepo.save(User(userId))

    fun deleteUser(userId: UUID): Mono<Void> = userRepo.deleteById(userId)
}
