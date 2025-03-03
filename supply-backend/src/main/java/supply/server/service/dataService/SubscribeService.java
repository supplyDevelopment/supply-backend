package supply.server.service.dataService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import supply.server.configuration.exception.PaymentException;
import supply.server.data.company.Company;
import supply.server.data.subscribe.CreateSubscribe;
import supply.server.data.subscribe.*;

import java.util.Optional;
import java.util.UUID;

@Service
public class SubscribeService extends UserService {

    @Value("${yoo.kassa.redirect.url}")
    private String redirectURL;
    private final WebClient yooKassaClient;
    @Value("${cookie.domain}")
    private String cookieDomain;

    public SubscribeService(RepositoryService repository, WebClient yooKassaClient) {
        super(repository);
        this.yooKassaClient = yooKassaClient;
    }

    // after this method cookie with payment id should be set by method setPaymentCookie
    public Subscribe createSubscribePayment(CreateSubscribe createSubscribe) {
        String requestBody = String
                .format("""
                            {
                                "amount": {
                                    "value": "%d.00",
                                    "currency": "RUB"
                                },
                                "capture": true,
                                "confirmation": {
                                    "type": "redirect",
                                    "return_url": "%s"
                                },
                                "description": "%d"
                            }
                        """, createSubscribe.amount(), redirectURL, createSubscribe.monthsCount());
        return yooKassaClient.post()
                .uri("/payments")
                .header("Idempotence-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Subscribe.class).block();
    }

    // after this method cookie with payment id should be deleted by method deletePaymentCookie
    public Company extendSubscription(String paymentId) {
        Subscribe subscribe = yooKassaClient.get()
                .uri("/payments/" + paymentId)
                .retrieve()
                .bodyToMono(Subscribe.class).block();

        if (subscribe.status().equals("succeeded")) {
            return repository.getCompany().extendSubscription(Integer.parseInt(subscribe.description()), user().companyId());
        }
        throw new PaymentException("Payment not completed");
    }

    public Optional<String> extractIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("payment".equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public void setPaymentCookie(HttpServletResponse response, String id) {
        ResponseCookie cookie = ResponseCookie.from("payment", id)
                .sameSite("None")
                .secure(true)
                .path("/")
                .domain(cookieDomain)
                .maxAge(86400)
                .httpOnly(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void deletePaymentCookie(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("payment".equals(cookie.getName())) {
                    ResponseCookie deleteCookie = ResponseCookie.from("payment", null)
                            .sameSite("None")
                            .secure(true)
                            .path("/")
                            .domain(cookieDomain)
                            .maxAge(0)
                            .httpOnly(true)
                            .build();
                    response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
                    break;
                }
            }
        }
    }

}
