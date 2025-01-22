package supply.server.data.utils.user;

import lombok.Getter;
import supply.server.configuration.exception.IncorrectParameterException;

import java.io.Serializable;

@Getter
public class UserName implements Serializable {
    private final String firstName;
    private final String secondName;
    private final String lastName;

    public UserName(String firstName, String secondName, String lastName) {
        checkName(firstName);
        checkName(secondName);
        if (lastName != null) {
            checkName(lastName);
        }
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
    }

    private void checkName(String name) {
        if (name != null &&!name.chars().allMatch(Character::isLetter)) {
            throw new IncorrectParameterException("Not allowed to use non-letter chars in name: " + name);
        }
    }
}
