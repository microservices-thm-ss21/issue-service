package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.lib.event.DataEvent
import de.thm.mni.microservices.gruppe6.lib.event.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DataEventService(@Autowired val projectDbService: ProjectDbService,
                       @Autowired val userDbService: UserDbService) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun processDataEvent(dataEvent: Mono<DataEvent>) {
        dataEvent.ofType(ProjectEvent::class.java)
            .doOnNext { logger.debug("ProjectEvent {}", it.id) }
            .subscribe { projectDbService.receiveUpdate(it) }

        dataEvent.ofType(UserEvent::class.java)
            .doOnNext { logger.debug("UserEvent {}", it.id) }
            .subscribe { userDbService.receiveUpdate(it) }
    }

}
