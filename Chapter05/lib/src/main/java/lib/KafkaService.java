package lib;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import io.opentracing.References;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

@Service
public class KafkaService {
    private static final String TOPIC = "message";

    @Autowired
    Tracer tracer;

    @Autowired
    KafkaTemplate<String, Message> kafkaTemplate;

    public void sendMessage(Message message) throws Exception {
        ProducerRecord<String, Message> record = new ProducerRecord<>(TOPIC, message);
        kafkaTemplate.send(record).get();
    }

    public Span startConsumerSpan(String name, MessageHeaders headers) {
        TextMap carrier = new MessageHeadersExtractAdapter(headers);
        SpanContext parent = tracer.extract(Format.Builtin.TEXT_MAP, carrier);
        return tracer.buildSpan(name) //
                .addReference(References.FOLLOWS_FROM, parent) //
                .start();
    }

    public void sendMessageAsync(Message message, Executor executor) throws Exception {
        CompletableFuture.supplyAsync(() -> {
            ProducerRecord<String, Message> record = new ProducerRecord<>(TOPIC, message);
            kafkaTemplate.send(record);
            return message.id;
        }, executor).get();
    }

    /**
     * An adapter from Spring MessageHeaders to OpenTracing TextMap carrier. It
     * relies on the behavior of
     * io.opentracing.contrib.kafka.spring.TracingConsumerFactory that stores the
     * context of the "receive" span in the headers with "second_span_" prefix.
     */
    private static class MessageHeadersExtractAdapter implements TextMap {

        private final Map<String, String> map = new HashMap<>();

        MessageHeadersExtractAdapter(MessageHeaders headers) {
            for (Map.Entry<String, Object> header : headers.entrySet()) {
                if (!header.getKey().startsWith("second_span_")) {
                    continue;
                }
                if (!(header.getValue() instanceof byte[])) {
                    continue;
                }
                String key = header.getKey().replaceFirst("^second_span_", "");
                String value = new String((byte[]) header.getValue(), StandardCharsets.UTF_8);
                map.put(key, value);
            }
        }

        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return map.entrySet().iterator();
        }

        @Override
        public void put(String key, String value) {
            throw new UnsupportedOperationException("should only be used with Tracer.extract()");
        }
    }
}
