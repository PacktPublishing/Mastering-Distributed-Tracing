package exercise6;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lib.people.Person;

@RestController
public class HelloController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Tracer tracer;

    @GetMapping("/sayHello/{name}")
    public String sayHello(@PathVariable String name) {
        Span span = tracer.activeSpan();
        Person person = getPerson(name);
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("name", person.getName());
        fields.put("title", person.getTitle());
        fields.put("description", person.getDescription());
        span.log(fields);

        String response = formatGreeting(person);
        span.setTag("response", response);

        return response;
    }

    private Person getPerson(String name) {
        String url = "http://localhost:8081/getPerson/" + name;
        ResponseEntity<Person> response = restTemplate.getForEntity(url, Person.class);
        return response.getBody();
    }

    private String formatGreeting(Person person) {
        URI uri = UriComponentsBuilder //
                .fromHttpUrl("http://localhost:8082/formatGreeting") //
                .queryParam("name", person.getName()) //
                .queryParam("title", person.getTitle()) //
                .queryParam("description", person.getDescription()) //
                .build(Collections.emptyMap());
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        return response.getBody();
    }
}
