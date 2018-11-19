package lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.prometheus.PrometheusMeterRegistry;

@RestController
public class MetricsController {

    @Autowired
    private PrometheusMeterRegistry micrometerRegistry;

    @RequestMapping(value = "/metrics", produces = "application/json; charset=UTF-8")
    public String metrics() {
        return micrometerRegistry.scrape();
    }
}