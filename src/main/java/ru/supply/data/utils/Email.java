package ru.supply.data.utils;

import lombok.Getter;
import ru.supply.configuration.exception.IncorrectInputException;

@Getter
public class Email {
    private final String email;

    public Email(String email) throws IncorrectInputException {
        if (!isValidEmail(email)) {
            throw new IncorrectInputException("Email is invalid");
        }
        this.email = email;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}
