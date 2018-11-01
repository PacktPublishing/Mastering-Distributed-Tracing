package exercise4a.formatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;

@RestController
public class FController {

    @Autowired
    private Tracer tracer;

    @GetMapping("/formatGreeting")
    public String formatGreeting(@RequestParam String name, @RequestParam String title,
            @RequestParam String description) {
        Span span = tracer.buildSpan("/formatGreeting").start();
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
