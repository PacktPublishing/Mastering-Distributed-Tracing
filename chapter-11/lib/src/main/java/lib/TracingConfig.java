package lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.opentracing.Tracer;
import io.opentracing.contrib.metrics.prometheus.PrometheusMetricsReporter;
import io.prometheus.client.CollectorRegistry;

@org.springframework.context.annotation.Configuration
public class TracingConfig {

    @Autowired
    AppId app;

    @Bean
    public CollectorRegistry prometheus() {
        return new CollectorRegistry();
    }

    @Bean
    public PrometheusMeterRegistry micrometerRegistry(CollectorRegistry collector) {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry( //
                PrometheusConfig.DEFAULT, collector, Clock.SYSTEM);
        io.micrometer.core.instrument.Metrics.addRegistry(registry);
        return registry;
    }

    @Bean
    public io.opentracing.Tracer tracer(CollectorRegistry collector) {
        Configuration configuration = Configuration.fromEnv(app.name);
        Tracer jaegerTracer = configuration.getTracerBuilder() //
                .withSampler(new ConstSampler(true)) //
                .withScopeManager(new MDCScopeManager()) //
                .build();

        PrometheusMetricsReporter reporter = PrometheusMetricsReporter //
                .newMetricsReporter() //
                .withCollectorRegistry(collector) //
                .withConstLabel("service", app.name) //
                .withBaggageLabel("callpath", "") //
                .build();
        return io.opentracing.contrib.metrics.Metrics.decorate(jaegerTracer, reporter);
    }
}