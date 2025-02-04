package supply.server.controller.entity.response;

import supply.server.data.company.Company;
import supply.server.data.user.User;

public record UserProfileResponse(
        User user,
        Company company
) {
}
