package lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.opentracing.Tracer;
import io.opentracing.contrib.redis.lettuce.TracingStatefulRedisConnection;

@Configuration
public class RedisConfig {
    @Autowired
    Tracer tracer;

    @Bean
    public StatefulRedisConnection<String, String> redisConn() {
        RedisClient client = RedisClient.create("redis://localhost");
        return new TracingStatefulRedisConnection<>( //
                client.connect(), tracer, false);
    }

    @Autowired
    StatefulRedisConnection<String, String> redisConn;

    @Bean
    public RedisCommands<String, String> redisClientSync() {
        return redisConn.sync();
    }
}
