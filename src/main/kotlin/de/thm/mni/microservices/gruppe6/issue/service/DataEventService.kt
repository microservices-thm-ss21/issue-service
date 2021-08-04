package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.lib.event.DataEvent
import de.thm.mni.microservices.gruppe6.lib.event.IssueDataEvent
import de.thm.mni.microservices.gruppe6.lib.event.ProjectDataEvent
import de.thm.mni.microservices.gruppe6.lib.event.UserDataEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * Implements a service which handles all dataEvents incoming
 */
@Service
class DataEventService(
    @Autowired val projectDbService: ProjectDbService,
    @Autowired val userDbService: UserDbService
) {

    /**
     * Processes all the dataEvents
     * @param dataEvent
     */
    @Throws(IllegalStateException::class)
    fun processDataEvent(dataEvent: Mono<DataEvent>) {
        dataEvent.subscribe {
            when (it) {
                is ProjectDataEvent -> {
                    projectDbService.receiveUpdate(it)
                }
                is UserDataEvent -> {
                    userDbService.receiveUpdate(it)
                }
                is IssueDataEvent -> {/* Do nothing with own events */
                }
                else -> error("Unexpected Event type: ${it?.javaClass}")
            }
        }
    }


}
