package supply.server.config.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRegistrationConfiguration {

    @Bean
    public Queue subscribeNotificationQueue() {
        return QueueBuilder.durable("subscribe.notification")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "subscribe.notification.dlq")
                .build();
    }

    @Bean
    public Queue subscribeNotificationDlqQueue() {
        return new Queue("subscribe.notification.dlq", true);
    }

    @Bean
    public DirectExchange subscribeNotificationExchange() {
        return new DirectExchange("subscribe.notification.exchange");
    }

    @Bean
    public Binding subscribeNotificationBinding() {
        return BindingBuilder.bind(subscribeNotificationQueue())
                .to(subscribeNotificationExchange())
                .with("subscribe.notification");
    }

    @Bean
    public Binding subscribeNotificationDlqBinding() {
        return BindingBuilder.bind(subscribeNotificationDlqQueue())
                .to(subscribeNotificationExchange())
                .with("subscribe.notification.dlq");
    }

}
