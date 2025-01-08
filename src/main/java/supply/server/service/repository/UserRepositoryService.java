package supply.server.service.repository;

import lombok.AllArgsConstructor;
import supply.server.configuration.exception.DataNotFound;
import supply.server.configuration.exception.DbException;
import supply.server.data.user.CreateUser;
import supply.server.data.user.InMemoryRpUser;
import supply.server.data.user.RpUser;
import supply.server.data.user.User;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class UserRepositoryService {

    private final RpUser rpUser;

    private final InMemoryRpUser inMemoryRpUser;

    public User add(CreateUser createUser) {
        User user;
        try {
            Optional<User> userOpt = rpUser.add(createUser);

            if (userOpt.isPresent()) {
                user = userOpt.get();
                inMemoryRpUser.add(user);
            } else {
                throw new DbException("Failed to add user");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return user;
    }

    public User get(UUID id) {
        User user;
        try {
            Optional<User> userOpt = inMemoryRpUser.get(id);

            if (userOpt.isEmpty()) {
                userOpt = rpUser.get(id);
                if (userOpt.isEmpty()) {
                    throw new DataNotFound("User with id " + id + " not found");
                } else {
                    user = userOpt.get();
                    inMemoryRpUser.add(user);
                }
            }
            user = userOpt.get();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return user;
    }

    public User get(String email) {
        User user;
        try {
            Optional<User> userOpt = inMemoryRpUser.get(email);

            if (userOpt.isEmpty()) {
                userOpt = rpUser.getByEmail(email);
                if (userOpt.isEmpty()) {
                    throw new DataNotFound("User with email " + email + " not found");
                } else {
                    user = userOpt.get();
                    inMemoryRpUser.add(user);
                }
            }
            user = userOpt.get();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return user;
    }

}