package exercise5;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.opentracing.Scope;
import io.opentracing.Span;
import lib.people.Person;

@RestController
public class HelloController extends TracedController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/sayHello/{name}")
    public String sayHello(@PathVariable String name, HttpServletRequest request) {
        Span span = startServerSpan("/sayHello", request);
        try (Scope scope = tracer.scopeManager().activate(span, false)) {
            Person person = getPerson(name);
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("name", person.getName());
            fields.put("title", person.getTitle());
            fields.put("description", person.getDescription());
            span.log(fields);

            String response = formatGreeting(person);
            span.setTag("response", response);

            return response;
        } finally {
            span.finish();
        }
    }

    private Person getPerson(String name) {
        String url = "http://localhost:8081/getPerson/" + name;
        URI uri = UriComponentsBuilder.fromHttpUrl(url).build(Collections.emptyMap());
        return get("get-person", uri, Person.class, restTemplate);
    }

    private String formatGreeting(Person person) {
        URI uri = UriComponentsBuilder //
                .fromHttpUrl("http://localhost:8082/formatGreeting") //
                .queryParam("name", person.getName()) //
                .queryParam("title", person.getTitle()) //
                .queryParam("description", person.getDescription()) //
                .build(Collections.emptyMap());
        return get("format-greeting", uri, String.class, restTemplate);
    }
}
