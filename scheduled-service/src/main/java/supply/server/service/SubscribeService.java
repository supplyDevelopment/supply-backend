package supply.server.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import supply.server.data.Subscribe;
import supply.server.service.rabbitService.RabbitSendService;
import supply.server.service.rabbitService.RabbitService;
import supply.server.service.repository.SubscribeRepositoryService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class SubscribeService {

    private final SubscribeRepositoryService subscribeRepositoryService;
    private final RabbitSendService rabbitSendService;

    @Scheduled(cron = "0 10 0 * * ?")
    public void checkSubscribe() {
        try {
            List<Subscribe> subscribes = subscribeRepositoryService.getAll();
            LocalDate now = LocalDate.now();

            for (Subscribe subscribe : subscribes) {
                long daysLeft = ChronoUnit.DAYS.between(now, subscribe.expirationDate());

                if (daysLeft == 7 || daysLeft == 1) {
                    for (String email : subscribe.contactEmails()) {
                        rabbitSendService.sendSubscribeNotificationEmail(
                                email,
                                daysLeft
                        );
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while checking subscribes", e);
        }
    }

}
