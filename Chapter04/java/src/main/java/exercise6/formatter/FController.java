package exercise6.formatter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FController {

    @GetMapping("/formatGreeting")
    public String formatGreeting(@RequestParam String name, @RequestParam String title,
            @RequestParam String description) {
        String response = "Hello, ";
        if (!title.isEmpty()) {
            response += title + " ";
        }
        response += name + "!";
        if (!description.isEmpty()) {
            response += " " + description;
        }
        return response;
    }
}
