package de.thm.mni.microservices.gruppe6.issue.event

import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory


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
        factory.setPubSubDomain(true);
        factory.setConnectionFactory(receiverActiveMQConnectionFactory())
        return factory
    }
}