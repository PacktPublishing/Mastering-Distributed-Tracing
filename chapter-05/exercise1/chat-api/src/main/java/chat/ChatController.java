package chat;

import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lib.KafkaService;
import lib.Message;
import lib.RedisService;

@CrossOrigin(maxAge = 3600)
@RestController
public class ChatController {

    @Autowired
    KafkaTemplate<String, Message> kafkaTemplate;

    @Autowired
    RedisService redis;

    @Autowired
    KafkaService kafka;

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("send-to-kafka-");
        executor.initialize();
        return executor;
    }

    Executor executor1 = asyncExecutor();

    @Autowired
    Executor executor2;

    private String sendMode = System.getProperty("KSEND");

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public @ResponseBody List<Message> getMessages(@RequestParam(value = "room", defaultValue = "lobby") String room)
            throws Exception {
        List<Message> messages = redis.getMessages(room);
        System.out.println("Retrieved " + messages.size() + " messages.");
        return messages;
    }

    @RequestMapping(value = "/message", consumes = { "application/json" }, produces = {
            MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST)
    public ResponseEntity<Message> postMessage(@RequestBody Message msg) throws Exception {
        msg.init();
        System.out.println("Received message: " + msg);
        if ("async1".equals(sendMode)) {
            kafka.sendMessageAsync(msg, executor1);
            System.out.println("Message sent async (executor1) to Kafka");
        } else if ("async2".equals(sendMode)) {
            kafka.sendMessageAsync(msg, executor2);
            System.out.println("Message sent async (executor2) to Kafka");
        } else {
            kafka.sendMessage(msg);
            System.out.println("Message sent sync to Kafka");
        }
        return new ResponseEntity<Message>(msg, HttpStatus.OK);
    }
}