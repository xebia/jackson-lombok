package com.netbeetle.jackson;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.junit.Before;
import org.junit.Test;

public class ConstructorPropertiesAnnotationIntrospectorTest {

    public static final String JSON = "{\"new_name\":\"foobar\",\"value\":42}";
    public static final String INVALID_JSON = "{\"name\":\"foobar\",\"value\":42}";
    private ObjectMapper mapperWithExtention;

    @Before
    public void setUp() {
        mapperWithExtention = new ObjectMapper();
        mapperWithExtention.setAnnotationIntrospector(new ConstructorPropertiesAnnotationIntrospector());
    }

    @Value
    private static class ImmutablePojo {
        @JsonProperty("new_name")
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
    public void testJacksonAbleToSerialize() throws IOException {
        String json = mapperWithExtention.writeValueAsString(instance);
        assertThat(json, is(JSON));
    }

    @Test
    public void testJacksonAbleToDeserialize() throws IOException {
        ImmutablePojo output = mapperWithExtention.readValue(JSON, ImmutablePojo.class);
        assertThat(output, is(instance));
    }

    @Test(expected = JsonMappingException.class)
    public void testJacksonUnableToDeserializeInvalidJson() throws IOException {
        ImmutablePojo output = mapperWithExtention.readValue(INVALID_JSON, ImmutablePojo.class);
        assertThat(output, is(instance));
    }
}
