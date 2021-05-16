package de.thm.mni.microservices.gruppe6.issue.event

import de.thm.mni.microservices.gruppe6.issue.service.DataEventService
import de.thm.mni.microservices.gruppe6.lib.event.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component
import javax.jms.ObjectMessage

@Component
class Receiver(private val dataEventService: DataEventService) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

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
