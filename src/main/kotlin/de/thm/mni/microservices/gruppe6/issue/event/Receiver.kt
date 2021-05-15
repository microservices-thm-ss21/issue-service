package de.thm.mni.microservices.gruppe6.issue.event

import de.thm.mni.microservices.gruppe6.issue.model.persistence.ProjectRepository
import de.thm.mni.microservices.gruppe6.issue.model.persistence.UserRepository
import de.thm.mni.microservices.gruppe6.issue.service.DataEventService
import de.thm.mni.microservices.gruppe6.issue.service.ProjectDbService
import de.thm.mni.microservices.gruppe6.issue.service.UserDbService
import de.thm.mni.microservices.gruppe6.lib.event.*
import org.apache.activemq.Message
import org.slf4j.LoggerFactory
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono
import java.util.*
import javax.jms.ObjectMessage

@Component
class Receiver(private val dataEventService: DataEventService) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @JmsListener(destination = "destination", containerFactory = "jmsListenerContainerFactory")
    fun receive(message: ObjectMessage) {

        when (message.`object`) {
            is DataEvent -> dataEventService.processDataEvent(message.`object` as DataEvent)
            is DomainEvent -> TODO()
        }

        message.`object`
        if (message.`object` is IssueEvent)

        logger.debug("----------------Message Received----------------")
        logger.debug(message.jmsMessageID)
        logger.debug("------------------------------------------------")
    }
}
