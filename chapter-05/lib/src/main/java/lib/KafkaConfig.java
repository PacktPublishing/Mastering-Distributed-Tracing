package lib;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.spring.TracingConsumerFactory;
import io.opentracing.contrib.kafka.spring.TracingProducerFactory;

@Configuration
public class KafkaConfig {

    @Autowired
    AppId app;

    @Autowired
    Tracer tracer;

    @Bean
    public Object kafkaListenerContainerFactory() throws Exception {
        ConcurrentKafkaListenerContainerFactory<String, Message> factory = //
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate() throws Exception {
        return new KafkaTemplate<>(producerFactory());
    }

    private String clientId() throws Exception {
        return InetAddress.getLocalHost().getHostName() + "-" + app.name;
    }

    private ConsumerFactory<String, Message> consumerFactory() throws Exception {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, app.name);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");

        return new TracingConsumerFactory<>( //
                new DefaultKafkaConsumerFactory<String, Message>( //
                        props, //
                        new StringDeserializer(), //
                        new JsonDeserializer<>(Message.class)));
    }

    private ProducerFactory<String, Message> producerFactory() throws Exception {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId());
        ProducerFactory<String, Message> producer = //
                new DefaultKafkaProducerFactory<String, Message>(props, //
                        new StringSerializer(), //
                        new JsonSerializer<Message>());
        return new TracingProducerFactory<String, Message>(producer, tracer);
    }
}
