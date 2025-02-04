package supply.server.controller.entity.request;

import jakarta.validation.constraints.NotNull;
import supply.server.data.utils.Email;

public record EmailPasswordRequest(
        @NotNull
        String email,
        @NotNull
        String password
) {
    public Email toEmail() {
        return new Email(email);
    }
}
