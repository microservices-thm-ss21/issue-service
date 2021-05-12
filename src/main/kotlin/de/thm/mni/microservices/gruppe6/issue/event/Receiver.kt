package de.thm.mni.microservices.gruppe6.issue.event

import de.thm.mni.microservices.gruppe6.lib.event.ServiceEvent
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class Receiver {
    @JmsListener(destination = "destination", containerFactory = "jmsListenerContainerFactory")
    fun receive(event: ServiceEvent) {
        print(""+ event.eventCode)
    }
}