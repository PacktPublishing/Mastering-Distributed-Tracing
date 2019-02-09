package model;

import java.util.Collection;

import model.Span;

public class Trace {
    public String traceId;
    public Collection<Span> spans;
}