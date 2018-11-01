package exercise4b;

import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.tag.Tags;

public class TracedController {
    @Autowired
    protected Tracer tracer;

    protected Span startServerSpan(String operationName, HttpServletRequest request) {
        HttpServletRequestExtractAdapter carrier = new HttpServletRequestExtractAdapter(request);
        SpanContext parent = tracer.extract(Format.Builtin.HTTP_HEADERS, carrier);
        Span span = tracer.buildSpan(operationName).asChildOf(parent).start();
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_SERVER);
        return span;
    }

    /**
     * Execute HTTP GET request.
     */
    protected <T> T get(String operationName, URI uri, Class<T> entityClass, RestTemplate restTemplate) {
        Span span = tracer.buildSpan(operationName).start();
        try (Scope scope = tracer.scopeManager().activate(span, false)) {
            Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
            Tags.HTTP_URL.set(span, uri.toString());
            Tags.HTTP_METHOD.set(span, "GET");

            HttpHeaders headers = new HttpHeaders();
            HttpHeaderInjectAdapter carrier = new HttpHeaderInjectAdapter(headers);
            tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, carrier);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(uri, HttpMethod.GET, entity, entityClass).getBody();
        } finally {
            span.finish();
        }
    }

    private static class HttpServletRequestExtractAdapter implements TextMap {
        private final Map<String, String> headers;

        HttpServletRequestExtractAdapter(HttpServletRequest request) {
            this.headers = new LinkedHashMap<>();
            Enumeration<String> keys = request.getHeaderNames();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = request.getHeader(key);
                headers.put(key, value);
            }
        }

        @Override
        public Iterator<Entry<String, String>> iterator() {
            return headers.entrySet().iterator();
        }

        @Override
        public void put(String key, String value) {
            throw new UnsupportedOperationException();
        }
    }

    private static class HttpHeaderInjectAdapter implements TextMap {
        private final HttpHeaders headers;

        HttpHeaderInjectAdapter(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override
        public Iterator<Entry<String, String>> iterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void put(String key, String value) {
            headers.set(key, value);
        }
    }

}
