package com.netbeetle.jackson;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.junit.Test;

public class ConstructorPropertiesAnnotationIntrospectorTest {
    @Value
    private static class ImmutablePojo {
        String name;
        int value;
    }

    private final ImmutablePojo instance = new ImmutablePojo("foobar", 42);

    @Test(expected = JsonMappingException.class)
    public void testJacksonUnableToDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(instance);
        mapper.readValue(json, ImmutablePojo.class);
    }

    @Test
    public void testJacksonAbleToDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new ConstructorPropertiesAnnotationIntrospector());
        String json = mapper.writeValueAsString(instance);
        assertThat(json, is("{\"name\":\"foobar\",\"value\":42}"));
        ImmutablePojo output = mapper.readValue(json, ImmutablePojo.class);
        assertThat(output, is(instance));
    }
}
