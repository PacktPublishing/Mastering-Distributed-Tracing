package exercise4b.formatter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import exercise4b.TracedController;
import io.opentracing.Scope;
import io.opentracing.Span;

@RestController
public class FController extends TracedController {

    @GetMapping("/formatGreeting")
    public String formatGreeting(@RequestParam String name, @RequestParam String title,
            @RequestParam String description, HttpServletRequest request) {
        Span span = startServerSpan("/formatGreeting", request);
        try (Scope scope = tracer.scopeManager().activate(span, false)) {
            String response = "Hello, ";
            if (!title.isEmpty()) {
                response += title + " ";
            }
            response += name + "!";
            if (!description.isEmpty()) {
                response += " " + description;
            }
            return response;
        } finally {
            span.finish();
        }
    }
}
