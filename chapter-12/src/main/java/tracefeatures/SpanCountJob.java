package tracefeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import model.ProtoUnmarshaler;
import model.Span;
import model.Trace;

public class SpanCountJob {

    private static Time traceSessionWindow = Time.seconds(5);

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "tracefeatures");

        FlinkKafkaConsumer<Span> consumer = new FlinkKafkaConsumer<>(//
                "jaeger-spans", //
                new ProtoUnmarshaler(), properties);

        // replay Kafka stream from beginning, useful for testing
        consumer.setStartFromEarliest();

        DataStream<Span> spans = env.addSource(consumer).name("spans");
        DataStream<Trace> traces = aggregateSpansToTraces(spans);
        DataStream<TraceSummary> spanCounts = countSpansByService(traces);

        spanCounts.print();
        spanCounts.addSink(ESSink.build());

        // execute program
        env.execute("Span Count Job");
    }

    private static DataStream<Trace> aggregateSpansToTraces(DataStream<Span> spans) {
        return spans //
                .keyBy((KeySelector<Span, String>) span -> span.traceId)
                .window(ProcessingTimeSessionWindows.withGap(traceSessionWindow))
                .apply(new WindowFunction<Span, Trace, String, TimeWindow>() {
                    @Override
                    public void apply(String traceId, TimeWindow window, Iterable<Span> spans, Collector<Trace> out)
                            throws Exception {
                        List<Span> spanList = new ArrayList<>();
                        spans.forEach(spanList::add);

                        Trace trace = new Trace();
                        trace.traceId = traceId;
                        trace.spans = spanList;
                        out.collect(trace);
                    }
                });
    }

    private static DataStream<TraceSummary> countSpansByService(DataStream<Trace> traces) {
        return traces.map(SpanCountJob::traceToSummary);
    }

    private static TraceSummary traceToSummary(Trace trace) throws Exception {
        Map<String, Integer> counts = new HashMap<>();
        long startTime = 0;
        String testName = null;
        for (Span span : trace.spans) {
            String opKey = span.serviceName + "::" + span.operationName;
            Integer count = counts.get(opKey);
            if (count == null) {
                count = 1;
            } else {
                count += 1;
            }
            counts.put(opKey, count);
            if (startTime == 0 || startTime > span.startTimeMicros) {
                startTime = span.startTimeMicros;
            }
            String v = span.tags.get("test_name");
            if (v != null) {
                testName = v;
            }
        }
        TraceSummary summary = new TraceSummary();
        summary.traceId = trace.traceId;
        summary.spanCounts = counts;
        summary.startTimeMillis = startTime / 1000; // to milliseconds
        summary.testName = testName;
        return summary;
    }
}
