package supply.server.configuration.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRegistrationConfiguration {

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
    public DirectExchange userRegistrationExchange() {
        return new DirectExchange("user.registration.exchange");
    }

    @Bean
    public Binding userRegistrationBinding() {
        return BindingBuilder.bind(userRegistrationQueue())
                .to(userRegistrationExchange())
                .with("user.registration");
    }

    @Bean
    public Binding userRegistrationDlqBinding() {
        return BindingBuilder.bind(userRegistrationDlqQueue())
                .to(userRegistrationExchange())
                .with("user.registration.dlq");
    }

}
