package lib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.opentracing.Tracer;

@Service
public class CallPath {

    @Autowired
    private Tracer tracer;

    @Autowired
    private AppId app;

    public void append() {
        io.opentracing.Span span = tracer.activeSpan();
        String currentPath = span.getBaggageItem("callpath");
        if (currentPath == null) {
            currentPath = app.name;
        } else {
            currentPath += "->" + app.name;
        }
        span.setBaggageItem("callpath", currentPath);
    }
}