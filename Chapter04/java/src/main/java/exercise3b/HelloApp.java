package exercise3b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;

@EnableJpaRepositories("lib.people") 
@EntityScan("lib.people")
@SpringBootApplication
public class HelloApp {

    @Bean
    public io.opentracing.Tracer initTracer() {
        SamplerConfiguration samplerConfig = new SamplerConfiguration().withType("const").withParam(1);
        ReporterConfiguration reporterConfig = new ReporterConfiguration().withLogSpans(true);
        return new Configuration("java-3-hello").withSampler(samplerConfig).withReporter(reporterConfig).getTracer();
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloApp.class, args);
    }
}
