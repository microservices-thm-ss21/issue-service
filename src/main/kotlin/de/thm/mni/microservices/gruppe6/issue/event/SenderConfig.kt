package de.thm.mni.microservices.gruppe6.issue.event

import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.core.JmsTemplate

@Configuration
class SenderConfig {
    val brokerUrl: String = "tcp://localhost:61616"

    @Bean
    fun senderActiveMQConnectionFactory(): ActiveMQConnectionFactory {
        val activeMQConnectionFactory = ActiveMQConnectionFactory()
        activeMQConnectionFactory.brokerURL = brokerUrl;
        return activeMQConnectionFactory
    }

    @Bean
    fun jmsTemplate() : JmsTemplate = JmsTemplate(senderActiveMQConnectionFactory())

    @Bean
    fun sender() : Sender = Sender(jmsTemplate())
}