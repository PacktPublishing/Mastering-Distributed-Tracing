package chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import lib.AppId;

@SpringBootApplication
@ComponentScan(basePackages = {"lib", "chat"})
public class App {
    @Bean
    public AppId appId() {
        return new AppId("chat-api");
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}