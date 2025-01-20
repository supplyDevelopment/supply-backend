package supply.server.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SendingMailService {

    private final JavaMailSender mailSender;

    private final String USER_REGISTRATION_TEXT = "Вы были зарегистрированы в системе Supply.\n" +
            " Вот ваши входные данные:\n" +
            "Логин: %s\n" +
            "Пароль: %s\n" +
            "Вы можете сменить пароль после входа в систему.";

    public void sendRegistrationEmail(String email, String password) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Регистрация в системе Supply");
        mailMessage.setText(String.format(USER_REGISTRATION_TEXT, email, password));
        mailSender.send(mailMessage);
    }

}
