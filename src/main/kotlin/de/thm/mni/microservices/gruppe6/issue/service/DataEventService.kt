package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.lib.event.DataEvent
import de.thm.mni.microservices.gruppe6.lib.event.IssueEvent
import de.thm.mni.microservices.gruppe6.lib.event.ProjectEvent
import de.thm.mni.microservices.gruppe6.lib.event.UserEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DataEventService(
    @Autowired val issueDbService: IssueDbService, // Included to maintain consistency
    @Autowired val projectDbService: ProjectDbService,
    @Autowired val userDbService: UserDbService
) {

    @Throws(IllegalStateException::class)
    fun processDataEvent(dataEvent: Mono<DataEvent>) {

        dataEvent.subscribe {
            when (it) {
                is ProjectEvent -> projectDbService::receiveUpdate
                is UserEvent -> userDbService::receiveUpdate
                is IssueEvent -> {/* Do nothing with own events */ }
                else -> error("Unexpected Event type: ${it?.javaClass}")
            }
        }
    }


}
