package hello;

import java.net.URI;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.opentracing.Span;

@RestController
public class HelloController {

    private final String formatterUrl;

    public HelloController() {
        String host = System.getProperty("formatter.host", "localhost");
        String port = System.getProperty("formatter.port", "8080");
        formatterUrl = "http://" + host + ":" + port + "/formatGreeting";
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private io.opentracing.Tracer tracer;

    @GetMapping("/sayHello/{name}")
    public String sayHello(@PathVariable String name, @RequestHeader HttpHeaders headers) {
        System.out.println("Headers: " + headers);

        Span span = tracer.activeSpan();
        if (span != null) {
            span.setBaggageItem("user-agent", headers.getFirst(HttpHeaders.USER_AGENT));
        }

        String response = formatGreeting(name);
        return response;
    }

    private String formatGreeting(String name) {
        URI uri = UriComponentsBuilder //
                .fromHttpUrl(formatterUrl) //
                .queryParam("name", name) //
                .build(Collections.emptyMap());
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        return response.getBody();
    }
}
