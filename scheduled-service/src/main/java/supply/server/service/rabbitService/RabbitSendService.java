package supply.server.service.rabbitService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class RabbitSendService {

    private final RabbitService rabbitService;

    public void sendSubscribeNotificationEmail(String email, long numberOfDays) {
        rabbitService.send(
                "subscribe.notification.exchange",
                "subscribe.notification",
                Map.of(
                        "email", email,
                        "password", numberOfDays
                )
        );
    }

}
