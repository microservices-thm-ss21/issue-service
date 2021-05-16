package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import de.thm.mni.microservices.gruppe6.lib.event.DataEvent
import de.thm.mni.microservices.gruppe6.lib.event.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class DataEventService(@Autowired val issueDbService: IssueDbService,
                       @Autowired val projectDbService: ProjectDbService,
                       @Autowired val userDbService: UserDbService) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)


    fun processDataEvent(dataEvent: Mono<DataEvent>) {
        dataEvent.map {
            when(it) {
                is ProjectEvent -> {
                    projectDbService.receiveUpdate(it)
                }
                is UserEvent -> {
                    userDbService.receiveUpdate(it)
                }
                is IssueEvent -> {}
                else -> error("Unexpected Event type: ${it?.javaClass}")
            }
        }

        // dataEvent.ofType(ProjectEvent::class.java).publish { projectDbService::receiveUpdate }

    }


}
