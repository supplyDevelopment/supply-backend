package supply.server.controller.end_point;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import supply.server.controller.entity.request.CreateSubscribeRequest;
import supply.server.data.subscribe.PaymentLink;
import supply.server.data.subscribe.Subscribe;
import supply.server.service.dataService.SubscribeService;

import java.util.Optional;


@RestController
@RequestMapping("/profile/subscribe")
@AllArgsConstructor
@Tag(name = "Subscription controller" , description = "Creation and payment of subscription")
public class SubscriptionController {

    private final SubscribeService subscribeService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/payment")
    public ResponseEntity<PaymentLink> payment(
            @RequestBody @Valid @NotNull CreateSubscribeRequest createSubscribeRequest,
            HttpServletResponse response
    ) {
        Subscribe subscribe = subscribeService
                .createSubscribePayment(createSubscribeRequest.toCreateSubscribe());
        subscribeService.setPaymentCookie(response, subscribe.id());

        return ResponseEntity.ok(new PaymentLink(subscribe.confirmation().confirmation_url()));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/post_payment")
    public ResponseEntity<?> postPayment(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> paymentId = subscribeService.extractIdFromCookie(request);
        if (paymentId.isPresent()) {
            subscribeService.extendSubscription(paymentId.get());
            subscribeService.deletePaymentCookie(response, request);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

}
