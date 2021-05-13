package de.thm.mni.microservices.gruppe6.issue.event

import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType

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
    fun jmsTemplate() : JmsTemplate {
        val jmsTemplate = JmsTemplate(senderActiveMQConnectionFactory())
        jmsTemplate.isPubSubDomain = true
        jmsTemplate.messageConverter = jacksonJmsMessageConverter()
        return jmsTemplate
    }

    fun jacksonJmsMessageConverter(): MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.setTargetType(MessageType.TEXT)
        converter.setTypeIdPropertyName("_type")
        return converter
    }
}