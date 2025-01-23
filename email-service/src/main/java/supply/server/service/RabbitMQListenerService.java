package supply.server.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class RabbitMQListenerService {

    private final SendingMailService sendingMailService;

    @RabbitListener(queues = "user.registration", containerFactory = "rabbitListenerContainerFactory")
    public void sendRegistrationEmail(@Payload Map<String, String> message) {
        String email = message.get("email");
        String password = message.get("password");
        try {
            sendingMailService.sendRegistrationEmail(email, password);
        } catch (Exception e) {
            log.error("Registration mail error {}", e.getMessage());
            throw e;
        }
    }

    @RabbitListener(queues = "subscribe.notification", containerFactory = "rabbitListenerContainerFactory")
    public void sendSubscribeNotificationEmail(@Payload Map<String, String> message) {
        String email = message.get("email");
        int daysLeft = Integer.parseInt(message.get("daysLeft"));
        try {
            sendingMailService.sendSubscribeNotificationEmail(email, daysLeft);
        } catch (Exception e) {
            log.error("Subscribe notification mail error {}", e.getMessage());
            throw e;
        }
    }
}
