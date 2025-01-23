package supply.server.data.utils.company;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Tax implements Serializable {
    private final String tax;

    public Tax(String tax) {
        this.tax = tax;
    }
}
