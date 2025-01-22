package supply.server.data.utils;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Address implements Serializable {
    private final String address;

    public Address(String address) {
        this.address = address;
    }
}
