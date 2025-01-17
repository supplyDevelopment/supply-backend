package supply.server.data.utils;

import lombok.Getter;
import supply.server.configuration.exception.IncorrectInputException;
import supply.server.configuration.exception.IncorrectParameterException;

@Getter
public class Email {
    private final String email;

    public Email(String email) throws IncorrectInputException {
        if (!isValidEmail(email)) {
            throw new IncorrectParameterException("Email is invalid " + email);
        }
        this.email = email;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}
