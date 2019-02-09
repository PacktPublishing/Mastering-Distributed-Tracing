package exercise4b.bigbrother;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import exercise4b.TracedController;
import io.opentracing.Scope;
import io.opentracing.Span;
import lib.people.Person;
import lib.people.PersonRepository;

@RestController
public class BBController extends TracedController {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/getPerson/{name}")
    public Person getPerson(@PathVariable String name, HttpServletRequest request) {
        Span span = startServerSpan("/getPerson", request);
        try (Scope scope = tracer.scopeManager().activate(span, false)) {
            Person person = loadPerson(name);
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("name", person.getName());
            fields.put("title", person.getTitle());
            fields.put("description", person.getDescription());
            span.log(fields);
            return person;
        } finally {
            span.finish();
        }
    }

    private Person loadPerson(String name) {
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
}
