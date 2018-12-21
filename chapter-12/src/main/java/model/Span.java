package model;

import java.io.Serializable;

public class Span implements Serializable {
    public String traceId;
    public String spanId;
    public String serviceName;
    public String operationName;
    public long startTimeMicros;
}