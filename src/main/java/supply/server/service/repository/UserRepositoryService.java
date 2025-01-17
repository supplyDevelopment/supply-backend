package supply.server.service.repository;

import lombok.AllArgsConstructor;
import supply.server.configuration.exception.DataNotFound;
import supply.server.configuration.exception.DbException;
import supply.server.data.PaginatedList;
import supply.server.data.Pagination;
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

    public User add(CreateUser createUser, UUID companyId) {
        User user;
        try {
            Optional<User> userOpt = rpUser.add(createUser, companyId);

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

    public User get(UUID userId, UUID companyId) {
        User user;
        try {
            Optional<User> userOpt = inMemoryRpUser.get(userId, companyId);

            if (userOpt.isEmpty()) {
                userOpt = rpUser.get(userId, companyId);
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                    inMemoryRpUser.add(user);
                } else {
                    throw new DataNotFound("User with id " + userId + " not found");
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
                userOpt = rpUser.get(email);
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

    public User updatePassword(UUID userId, String password, UUID companyId) {
        User user;
        try {
            Optional<User> userOpt = rpUser.updatePassword(userId, password, companyId);
            if (userOpt.isEmpty()) {
                throw new DbException("Failed to update password for user with id " + userId);
            } else {
                user = userOpt.get();
                inMemoryRpUser.add(user);
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return user;
    }

    public PaginatedList<User> getAll(String prefix, UUID companyId, Pagination pagination) {
        PaginatedList<User> users;
        try {
            users = rpUser.getAll(prefix, companyId, pagination);
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        return users;
    }

}
