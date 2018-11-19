package hello;

import java.net.URI;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class HelloController {
    private final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    private final String formatterUrl;

    @Autowired
    private lib.CallPath callPath;

    @Autowired
    private lib.ChaosMonkey chaosMonkey;

    @Autowired
    private RestTemplate restTemplate;

    public HelloController() {
        String host = System.getProperty("formatter.host", "localhost");
        String port = System.getProperty("formatter.port", "8082");
        formatterUrl = "http://" + host + ":" + port + "/formatGreeting";
    }

    @GetMapping("/sayHello/{name}")
    public String sayHello(@PathVariable String name, @RequestHeader HttpHeaders headers) {
        logger.info("Name: {}", name);

        callPath.append();
        chaosMonkey.maybeFail();

        String response = formatGreeting(name);
        logger.info("Response: {}", response);
        return response;
    }

    private String formatGreeting(String name) {
        URI uri = UriComponentsBuilder //
                .fromHttpUrl(formatterUrl) //
                .queryParam("name", name) //
                .build(Collections.emptyMap());
        logger.info("Calling {}", uri);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            return response.getBody();
        } catch (HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new RuntimeException("Formatter error: " + responseBody);
        }
    }
}
