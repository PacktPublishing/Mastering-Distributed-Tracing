package formatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"lib", "formatter"})
public class FApp {

    @Bean
    public lib.AppId appId() {
        return new lib.AppId("formatter-1");
    }

    public static void main(String[] args) {
        System.setProperty("server.port", "8082");
        SpringApplication.run(FApp.class, args);
    }
}
