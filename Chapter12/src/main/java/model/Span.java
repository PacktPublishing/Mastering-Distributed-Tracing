package model;

import java.io.Serializable;
import java.util.Map;

public class Span implements Serializable {
    public String traceId;
    public String spanId;
    public String serviceName;
    public String operationName;
    public long startTimeMicros;
    public Map<String, String> tags;
}