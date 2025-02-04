package supply.server.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SendingMailService {

    private final JavaMailSender mailSender;

    private final String USER_REGISTRATION_TEXT = "Вы были зарегистрированы в системе Supply.\n" +
            " Вот ваши входные данные:\n" +
            "Логин: %s\n" +
            "Пароль: %s\n" +
            "Вы можете сменить пароль после входа в систему.";

    private final String SUBSCRIBE_NOTIFICATION_TEXT = "До конца подписки осталось %d дней!";

    public void sendRegistrationEmail(String email, String password) {
        int maxAttempts = 2;
        int attemptCount = 0;
        boolean emailSent = false;

        while (attemptCount < maxAttempts && !emailSent) {
            try {
                attemptCount++;
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(email);
                mailMessage.setFrom("supply.plus@mail.ru");
                mailMessage.setSubject("Регистрация в системе Supply");
                mailMessage.setText(String.format(USER_REGISTRATION_TEXT, email, password));
                mailSender.send(mailMessage);
                emailSent = true;
            } catch (MailSendException e) {
                if (attemptCount >= maxAttempts) {
                    log.error("Email sending failed after {} attempts", maxAttempts);
                } else {
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    public void sendSubscribeNotificationEmail(String email, long daysLeft) {
        int maxAttempts = 2;
        int attemptCount = 0;
        boolean emailSent = false;

        while (attemptCount < maxAttempts && !emailSent) {
            try {
                attemptCount++;
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(email);
                mailMessage.setFrom("supply.plus@mail.ru");
                mailMessage.setSubject("Уведомление о подписке");
                mailMessage.setText(String.format(SUBSCRIBE_NOTIFICATION_TEXT, daysLeft));
                mailSender.send(mailMessage);
                emailSent = true;
            } catch (MailSendException e) {
                if (attemptCount >= maxAttempts) {
                    log.error("Email sending failed after {} attempts", maxAttempts);
                } else {
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

}
