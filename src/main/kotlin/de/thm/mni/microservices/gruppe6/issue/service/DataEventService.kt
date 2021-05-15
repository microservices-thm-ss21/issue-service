package de.thm.mni.microservices.gruppe6.issue.service

import de.thm.mni.microservices.gruppe6.lib.event.DataEvent
import de.thm.mni.microservices.gruppe6.lib.event.DataEventCode
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DataEventService {

    fun processDataEvent(dataEvent: DataEvent) {

    }

    fun updateDatabase(repository: ReactiveCrudRepository<Any, UUID>, eventCode: DataEventCode, id: UUID) {
        when(eventCode) {
            DataEventCode.CREATED -> TODO() // dbService.put(id)
            DataEventCode.UPDATED -> TODO()
            DataEventCode.DELETED -> TODO()
        }
    }

}
