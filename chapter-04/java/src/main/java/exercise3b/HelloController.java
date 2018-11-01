package exercise3b;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import io.opentracing.Tracer;
import io.opentracing.Scope;
import io.opentracing.Span;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lib.people.Person;
import lib.people.PersonRepository;

@RestController
public class HelloController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private Tracer tracer;

    @GetMapping("/sayHello/{name}")
    public String sayHello(@PathVariable String name) {
        Span span = tracer.buildSpan("say-hello").start();
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
        Span span = tracer.buildSpan("get-person").start();
        try (Scope scope = tracer.scopeManager().activate(span, false)) {
            Optional<Person> personOpt = personRepository.findById(name);
            if (personOpt.isPresent()) {
                return personOpt.get();
            }
            return new Person(name);
        } finally {
            span.finish();
        }
    }

    private String formatGreeting(Person person) {
        Span span = tracer.buildSpan("format-greeting").start();
        try (Scope scope = tracer.scopeManager().activate(span, false)) {
            String response = "Hello, ";
            if (!person.getTitle().isEmpty()) {
                response += person.getTitle() + " ";
            }
            response += person.getName() + "!";
            if (!person.getDescription().isEmpty()) {
                response += " " + person.getDescription();
            }
            return response;
        } finally {
            span.finish();
        }
    }
}
