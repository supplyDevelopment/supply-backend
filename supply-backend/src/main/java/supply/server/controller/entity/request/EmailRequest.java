package supply.server.controller.entity.request;

import jakarta.validation.constraints.NotNull;
import supply.server.data.utils.Email;

public record EmailRequest(
        @NotNull
        String email
) {
    public Email toEmail() {
        return new Email(email);
    }
}
