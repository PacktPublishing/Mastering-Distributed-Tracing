package client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = { "lib", "client" })
public class ClientApp {
    private final String clientVersion;

    public ClientApp() {
        this.clientVersion = System.getProperty("client.version", "v1");
    }

    @Bean
    public lib.AppId appId() {
        return new lib.AppId("client-" + clientVersion);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    public CommandLineRunner runner() {
        return new Runner();
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApp.class, args);
    }
}
