package supply.server.service.rabbitService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class RabbitSendService {

    private final RabbitService rabbitService;

    public void sendUserRegistrationEmail(String email, String password) {
        rabbitService.send(
                "user.registration.exchange",
                "user.registration",
                Map.of(
                        "email", email,
                        "password", password
                )
        );
    }

}
