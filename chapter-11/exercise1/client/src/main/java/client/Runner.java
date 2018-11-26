package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lib.ChaosMonkey;

@Service
public class Runner implements CommandLineRunner {
    private final static Logger logger = LoggerFactory.getLogger(Runner.class);

    private final double throughput;

    @Autowired
    private Tracer tracer;

    @Autowired
    private lib.CallPath callPath;

    @Autowired
    private ChaosMonkey chaosMonkey;

    @Autowired
    private RestTemplate restTemplate;

    public Runner() {
        this.throughput = Double.parseDouble(System.getProperty("throughout", "10"));
    }

    public void run(String... args) {
        while (true) {
            Span span = tracer.buildSpan("client").start();
            try (Scope scope = tracer.scopeManager().activate(span, false)) {
                callPath.append();
                chaosMonkey.maybeInjectFault();
                runQuery(restTemplate);
            }
            span.finish();
            sleep();
        }
    }

    private void runQuery(RestTemplate restTemplate) {
        try {
            String url = "http://localhost:8080/sayHello/Bender";
            logger.info("executing {}", url);
            restTemplate.getForObject(url, String.class);
        } catch (HttpServerErrorException e) {
            logger.error("error from server");
        }
    }

    private void sleep() {
        double sleep = (0.75 + 0.5 * Math.random()) / throughput;
        try {
            Thread.sleep((long) (sleep * 1000));
        } catch (InterruptedException e) {
            // skip
        }
    }
}