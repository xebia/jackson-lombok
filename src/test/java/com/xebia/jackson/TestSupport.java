package com.xebia.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TestSupport {
    public static class IntDeserializer extends JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            String value = jp.getText();
            try {
                return Integer.valueOf(value) + 1;

            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }

    public static class IntSerializer extends JsonSerializer<Integer> {
        @Override
        public void serialize(Integer value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value == -1 ? null : (value - 1) + "");
        }
    }
}
