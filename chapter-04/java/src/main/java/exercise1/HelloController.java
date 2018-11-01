package exercise1;

import java.util.Optional;

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

    @GetMapping("/sayHello/{name}")
    public String sayHello(@PathVariable String name) {
        Person person = getPerson(name);
        String response = formatGreeting(person);
        return response;
    }

    private Person getPerson(String name) {
        Optional<Person> personOpt = personRepository.findById(name);
        if (personOpt.isPresent()) {
            return personOpt.get();
        }
        return new Person(name);
    }

    private String formatGreeting(Person person) {
        String response = "Hello, ";
        if (!person.getTitle().isEmpty()) {
            response += person.getTitle() + " ";
        }
        response += person.getName() + "!";
        if (!person.getDescription().isEmpty()) {
            response += " " + person.getDescription();
        }
        return response;
    }
}
