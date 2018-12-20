package model;

import java.io.Serializable;
import java.util.List;

public class Span implements Serializable {
    public String traceId;
    public String spanId;
    public String serviceName;
    public String operationName;
    public List<SpanReference> references;
    public int flags;
    public long startTimeMicros;
    // tags_ = java.util.Collections.emptyList();
    // logs_ = java.util.Collections.emptyList();
    // process = "";
    // warnings_ = com.google.protobuf.LazyStringArrayList.EMPTY;
}