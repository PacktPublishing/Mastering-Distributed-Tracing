package giphy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lib.AppId;
import lib.GiphyService;
import lib.KafkaService;
import lib.Message;

@EnableKafka
@SpringBootApplication
@ComponentScan(basePackages = "lib")
public class App {
    @Bean
    public AppId appId() {
        return new AppId("giphy-service");
    }

    @Autowired
    GiphyService giphy;

    @Autowired
    KafkaService kafka;

    @Autowired
    Tracer tracer;

    @KafkaListener(topics = "message")
    public void process(@Payload Message message, @Headers MessageHeaders headers) throws Exception {
        Span span = kafka.startConsumerSpan("process", headers);
        try (Scope scope = tracer.scopeManager().activate(span, true)) {
            System.out.println("Received message: " + message.message);
            if (message.image == null && message.message.trim().startsWith("/giphy")) {
                String query = message.message.split("/giphy")[1].trim();
                System.out.println("Giphy requested: " + query);
                message.image = giphy.query(query);
                if (message.image != null) {
                    kafka.sendMessage(message);
                    System.out.println("Updated message, url=" + message.image);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }
}
