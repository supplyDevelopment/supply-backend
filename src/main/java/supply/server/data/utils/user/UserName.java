package supply.server.data.utils.user;

import lombok.Getter;

import java.util.Optional;

@Getter
public class UserName {
    private final String firstName;
    private final String secondName;
    private final Optional<String> lastName;

    public UserName(String firstName, String secondName, String lastName) {
        this.firstName = firstName;
        this.secondName = secondName;
        if (lastName == null) {
            this.lastName = Optional.empty();
        } else {
            this.lastName = Optional.of(lastName);
        }
    }
}
