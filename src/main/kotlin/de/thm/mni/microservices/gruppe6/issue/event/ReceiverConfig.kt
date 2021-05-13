package de.thm.mni.microservices.gruppe6.issue.event

import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType


@Configuration
@EnableJms
class ReceiverConfig{
    @Value("\${spring.activemq.broker-url}")
    private var brokerUrl: String? = null

    @Bean
    fun receiverActiveMQConnectionFactory(): ActiveMQConnectionFactory {
        val activeMQConnectionFactory = ActiveMQConnectionFactory()
        activeMQConnectionFactory.brokerURL = brokerUrl;
        return activeMQConnectionFactory
    }

    @Bean
    fun jmsListenerContainerFactory(): DefaultJmsListenerContainerFactory {
        val factory = DefaultJmsListenerContainerFactory()
        factory.setPubSubDomain(true)
        factory.setConnectionFactory(receiverActiveMQConnectionFactory())
        factory.setMessageConverter(jacksonJmsMessageConverter())
        return factory
    }

    fun jacksonJmsMessageConverter(): MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.setTargetType(MessageType.TEXT)
        converter.setTypeIdPropertyName("_type")
        return converter
    }
}