package supply.server.data.utils.company;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Bil implements Serializable {
    private final String bil;

    public Bil(String bil) {
        this.bil = bil;
    }
}
