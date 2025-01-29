package supply.server.configuration.webFlux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class YooKassaClientConfig {

    @Value("${yoo.kassa.shop.id}")
    private String shopId;
    @Value("${yoo.kassa.shop.secret.key}")
    private String secretKey;

    @Bean
    public WebClient yooKassaClient() {
        return WebClient.builder()
                .baseUrl("https://api.yookassa.ru/v3")
                .defaultHeaders(headers -> headers.setBasicAuth(shopId, secretKey))
                .build();
    }

}
