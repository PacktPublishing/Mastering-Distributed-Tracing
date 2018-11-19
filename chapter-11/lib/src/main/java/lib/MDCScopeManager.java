package lib;

import org.slf4j.MDC;

import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.util.ThreadLocalScopeManager;

public class MDCScopeManager extends ThreadLocalScopeManager {

    @Override
    public Scope activate(Span span, boolean finishOnClose) {
        return new ScopeWrapper(super.activate(span, finishOnClose));
    }

    private static class ScopeWrapper implements Scope {
        private final Scope scope;
        private final String previousTraceId;
        private final String previousSpanId;
        private final String previousSampled;

        ScopeWrapper(Scope scope) {
            this.scope = scope;
            this.previousTraceId = lookup("trace_id");
            this.previousSpanId = lookup("span_id");
            this.previousSampled = lookup("trace_sampled");

            JaegerSpanContext ctx = (JaegerSpanContext) scope.span().context();
            String traceId = Long.toHexString(ctx.getTraceId());
            String spanId = Long.toHexString(ctx.getSpanId());
            String sampled = String.valueOf(ctx.isSampled());
            
            replace("trace_id", traceId);
            replace("span_id", spanId);
            replace("trace_sampled", sampled);
        }

        @Override
        public void close() {
            this.scope.close();
            replace("trace_id", previousTraceId);
            replace("span_id", previousSpanId);
            replace("trace_sampled", previousSampled);
        }

        @Override
        public Span span() {
            return this.scope.span();
        }
    }

    private static String lookup(String key) {
        return MDC.get(key);
    }

    private static void replace(String key, String value) {
        if (value == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, value);
        }
    }
}
