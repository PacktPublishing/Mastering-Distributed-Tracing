package hello;

import java.net.URI;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class HelloController2 {

    private final String formatterUrl;

    public HelloController2() {
        String host = System.getProperty("formatter.host", "localhost");
        String port = System.getProperty("formatter.port", "8080");
        formatterUrl = "http://" + host + ":" + port + "/formatGreeting";
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/sayHello2/{name}")
    public String sayHello(@PathVariable String name, @RequestHeader HttpHeaders headers) {
        System.out.println("Headers: " + headers);

        String response = formatGreeting(name, copyHeaders(headers));
        return response;
    }

    private String formatGreeting(String name, HttpHeaders tracingHeaders) {
        URI uri = UriComponentsBuilder //
                .fromHttpUrl(formatterUrl) //
                .queryParam("name", name) //
                .build(Collections.emptyMap());

        ResponseEntity<String> response = restTemplate.exchange( //
                uri, HttpMethod.GET, new HttpEntity<>(tracingHeaders), //
                String.class);
        return response.getBody();
    }

    private final static String[] tracingHeaderKeys = { //
            "x-request-id", //
            "x-b3-traceid", //
            "x-b3-spanid", //
            "x-b3-parentspanid", //
            "x-b3-sampled", //
            "x-b3-flags", //
            "x-ot-span-context" //
    };

    private HttpHeaders copyHeaders(HttpHeaders headers) {
        HttpHeaders tracingHeaders = new HttpHeaders();
        for (String key : tracingHeaderKeys) {
            String value = headers.getFirst(key);
            if (value != null) {
                tracingHeaders.add(key, value);
            }
        }
        return tracingHeaders;
    }
}
