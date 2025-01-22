package supply.server.data.utils;

import lombok.Getter;
import supply.server.configuration.exception.IncorrectParameterException;

import java.io.Serializable;

@Getter
public class Phone implements Serializable {
    private final String phone;

    public Phone(String phone) {
        if (!isValidPhone(phone)) {
            throw new IncorrectParameterException("Phone is invalid " + phone);
        }
        this.phone = phone;
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^(\\+7|8)\\d{10}$");
    }
}