package de.thm.mni.microservices.gruppe6.issue.event

import de.thm.mni.microservices.gruppe6.lib.event.ServiceEvent
import org.apache.activemq.command.ActiveMQTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component

@Component
class Sender(@Autowired val jmsTemplate: JmsTemplate) {
    fun send (event: ServiceEvent) = jmsTemplate.convertAndSend(ActiveMQTopic("destination"), event)
}
