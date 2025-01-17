package supply.server.service.rabbitService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitListenService {

    @RabbitListener(queues = "user.registration.dlq")
    public void handleUserRegistrationException(Map<String, String> message) {

    }

}
