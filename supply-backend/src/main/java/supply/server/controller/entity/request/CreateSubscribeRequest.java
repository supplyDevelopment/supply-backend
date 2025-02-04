package supply.server.controller.entity.request;

import jakarta.validation.constraints.NotNull;
import supply.server.data.subscribe.CreateSubscribe;

public record CreateSubscribeRequest(
        @NotNull
        int amount,
        @NotNull
        int monthsCount
) {
    public CreateSubscribe toCreateSubscribe() {
        return new CreateSubscribe(amount, monthsCount);
    }
}
