package supply.server.controller.entity.response;

import supply.server.data.company.Company;
import supply.server.data.user.User;

import java.util.List;

public record UserInfoResponse(
        List<String> permissions,
        String subscription_end_date
) {
    public UserInfoResponse(User user, Company company) {
        this(
                user.permissions().stream().map(Enum::name).toList(),
                company.subscriptionExpiresAt().toString()
        );
    }
}
