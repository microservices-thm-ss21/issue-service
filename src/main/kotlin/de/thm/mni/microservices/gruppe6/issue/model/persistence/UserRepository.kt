package de.thm.mni.microservices.gruppe6.issue.model.persistence

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

interface UserRepository: ReactiveCrudRepository<User, UUID>
