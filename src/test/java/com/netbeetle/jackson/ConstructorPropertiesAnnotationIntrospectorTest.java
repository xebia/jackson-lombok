package com.netbeetle.jackson;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import lombok.Data;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.netbeetle.jackson.ConstructorPropertiesAnnotationIntrospector;

public class ConstructorPropertiesAnnotationIntrospectorTest
{
    @Data
    private static class ImmutablePojo
    {
        private final String name;
        private final int value;
    }

    private final ImmutablePojo instance = new ImmutablePojo("foobar", 42);

    @Test(expected = JsonMappingException.class)
    public void testJacksonUnableToDeserialize() throws JsonGenerationException,
        JsonMappingException, IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(instance);
        mapper.readValue(json, ImmutablePojo.class);
    }

    @Test
    public void testJacksonAbleToDeserialize() throws JsonGenerationException,
        JsonMappingException, IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        DeserializationConfig config = mapper.getDeserializationConfig();
        config.appendAnnotationIntrospector(new ConstructorPropertiesAnnotationIntrospector());
        String json = mapper.writeValueAsString(instance);
        ImmutablePojo output = mapper.readValue(json, ImmutablePojo.class);
        assertThat(output, equalTo(instance));
    }
}
