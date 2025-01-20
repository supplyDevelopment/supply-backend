package supply.server.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange("user.registration.exchange");
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    public Queue userRegistrationQueue() {
        return QueueBuilder.durable("user.registration")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "user.registration.dlq")
                .build();
    }

    @Bean
    public Queue userRegistrationDlqQueue() {
        return new Queue("user.registration.dlq", true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("user.registration.exchange");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(userRegistrationQueue())
                .to(exchange())
                .with("user.registration");
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(userRegistrationDlqQueue())
                .to(exchange())
                .with("user.registration.dlq");
    }

}
