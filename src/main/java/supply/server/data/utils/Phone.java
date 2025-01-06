package supply.server.data.utils;

import lombok.Getter;

@Getter
public class Phone {
    private final String phone;

    public Phone(String phone) {
        this.phone = phone;
    }
}
