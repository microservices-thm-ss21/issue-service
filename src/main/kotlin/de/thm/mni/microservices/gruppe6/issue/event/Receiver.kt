package de.thm.mni.microservices.gruppe6.issue.event

import org.apache.activemq.Message
import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class Receiver {

    val logger = LoggerFactory.getLogger(this::class.java)

    @JmsListener(destination = "destination", containerFactory = "jmsListenerContainerFactory")
    fun receive(message: Message) {
        logger.debug("----------------Message Received----------------")
        logger.debug(message.jmsMessageID)
        logger.debug("------------------------------------------------")
    }
}
