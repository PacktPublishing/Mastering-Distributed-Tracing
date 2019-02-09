package tracefeatures;

import java.io.Serializable;
import java.util.Map;

public class TraceSummary implements Serializable {
    public String traceId;
    public long startTimeMillis;
    public Map<String, Integer> spanCounts;
    public String testName;
}
