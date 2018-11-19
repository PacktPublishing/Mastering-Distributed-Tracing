package lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.opentracing.Tracer;

@Service
public class ChaosMonkey {
    private final static Logger logger = LoggerFactory.getLogger(ChaosMonkey.class);

    private final String failureLocation;
    private final double failureRate;

    @Autowired
    private Tracer tracer;

    @Autowired
    private AppId app;

    public ChaosMonkey() {
        this.failureLocation = System.getProperty("failure.location", "");
        this.failureRate = Double.parseDouble(System.getProperty("failure.rate", "0"));
    }

    public void maybeFail() {
        io.opentracing.Span span = tracer.activeSpan();
        String fail = span.getBaggageItem("fail");
        if (app.name.equals(fail)) {
            logger.warn("simulating failure");
            throw new RuntimeException("simulated failure in " + app.name);
        }
    }

    public void maybeInjectFault() {
        if (Math.random() < failureRate) {
            io.opentracing.Span span = tracer.activeSpan();
            span.setBaggageItem("fail", failureLocation);
        }
    }
}