package de.thm.mni.microservices.gruppe6.issue.event

import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.core.JmsTemplate

@Configuration
class SenderConfig {
    @Value("\${spring.activemq.broker-url}")
    private var brokerUrl: String? = null

    @Bean
    fun senderActiveMQConnectionFactory(): ActiveMQConnectionFactory {
        val activeMQConnectionFactory = ActiveMQConnectionFactory()
        activeMQConnectionFactory.brokerURL = brokerUrl;
        return activeMQConnectionFactory
    }

    @Bean
    fun jmsTemplate() : JmsTemplate = JmsTemplate(senderActiveMQConnectionFactory())
}