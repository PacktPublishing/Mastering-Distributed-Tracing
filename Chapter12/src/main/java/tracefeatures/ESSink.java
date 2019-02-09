package tracefeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Requests;

public class ESSink {

    public static ElasticsearchSink<TraceSummary> build() {
        List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("127.0.0.1", 9200, "http"));

        ElasticsearchSink.Builder<TraceSummary> esSinkBuilder = new ElasticsearchSink.Builder<>(httpHosts,
                new ElasticsearchSinkFunction<TraceSummary>() {

                    @Override
                    public void process(TraceSummary summary, RuntimeContext ctx, RequestIndexer indexer) {
                        indexer.add(Requests.indexRequest()//
                            .index("trace-summaries") //
                            .type("trace-summaries") //
                            .id(summary.traceId) //
                            .source(asJson(summary)));
                    }
                });

        // configuration for the bulk requests; this instructs the sink to emit after
        // every element, otherwise they would be buffered
        esSinkBuilder.setBulkFlushMaxActions(1);

        return esSinkBuilder.build();
    }

    private static Map<String, Object> asJson(TraceSummary summary) {
        Map<String, Object> json = new HashMap<>();
        json.put("traceId", summary.traceId);
        json.put("@timestamp", summary.startTimeMillis);
        json.put("spanCounts", summary.spanCounts);
        if (summary.testName != null) {
            // null check is just a precaution, in case we don't receive the root span.
            json.put("testName", summary.testName);
        }
        return json;
    }
}