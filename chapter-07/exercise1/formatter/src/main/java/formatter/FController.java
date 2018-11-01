package formatter;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FController {

    private final String template;

    public FController() {
        if (Boolean.getBoolean("professor")) {
            template = "Good news, %s! If anyone needs me I'll be in the Angry Dome!";
        } else {
            template = "Hello, puny human %s! Morbo asks: how do you like running on Kubernetes?";
        }
        System.out.println("Using template: " + template);
    }

    @GetMapping("/formatGreeting")
    public String formatGreeting(@RequestParam String name, @RequestHeader HttpHeaders headers) {
        System.out.println("Headers: " + headers);

        return String.format(template, name);
    }
}
