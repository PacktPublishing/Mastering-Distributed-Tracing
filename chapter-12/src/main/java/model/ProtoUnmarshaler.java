package model;

import java.io.IOException;

import com.google.protobuf.ByteString;
import com.google.protobuf.ByteString.ByteIterator;
import com.google.protobuf.util.Timestamps;

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema;

import io.jaegertracing.api_v2.Model;

public class ProtoUnmarshaler extends AbstractDeserializationSchema<Span> {

    private static final long serialVersionUID = 7952979116702613945L;

    @Override
    public Span deserialize(byte[] message) throws IOException {
        Model.Span protoSpan = Model.Span.parseFrom(message);
        return fromProto(protoSpan);
    }

    private Span fromProto(Model.Span protoSpan) {
        Span span = new Span();
        span.traceId = asHexString(protoSpan.getTraceId());
        span.spanId = asHexString(protoSpan.getSpanId());
        span.operationName = protoSpan.getOperationName();
        span.serviceName = protoSpan.getProcess().getServiceName();
        span.startTimeMicros = Timestamps.toMicros(protoSpan.getStartTime());
        return span;
    }

    private static final String HEXES = "0123456789ABCDEF";

    private String asHexString(ByteString id) {
        ByteIterator iterator = id.iterator();
        StringBuilder out = new StringBuilder();
        while (iterator.hasNext()) {
            byte b = iterator.nextByte();
            out.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return out.toString();
    }
}