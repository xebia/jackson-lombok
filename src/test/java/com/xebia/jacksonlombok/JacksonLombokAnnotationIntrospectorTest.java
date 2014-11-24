package com.xebia.jacksonlombok;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Value;
import org.junit.Before;
import org.junit.Test;

public class JacksonLombokAnnotationIntrospectorTest {


    public static final String JSON = "{\"empty\":\"\",\"value\":42,\"specialInt\":\"24\",\"new_name\":\"foobar\"}";
    //Has a different attribute order
    public static final String LEGACY_JSON = "{\"new_name\":\"foobar\",\"empty\":\"\",\"value\":42,\"specialInt\":\"24\"}";
    public static final String INVALID_JSON = "{\"name\":\"foobar\",\"empty\":\"\",\"value\":42}";
    private ObjectMapper mapperWithExtention;

    @Before
    public void setUp() {
        mapperWithExtention = new ObjectMapper();
        mapperWithExtention.setAnnotationIntrospector(new JacksonLombokAnnotationIntrospector());
    }

    @Value
    private static class ImmutablePojo {
        @JsonProperty("new_name")
        String name;
        @JsonProperty
        String empty;
        int value;
        @JsonDeserialize(using = TestSupport.IntDeserializer.class)
        @JsonSerialize(using = TestSupport.IntSerializer.class)
        Integer specialInt;
    }

    private final ImmutablePojo instance = new ImmutablePojo("foobar", "", 42, 25);

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

    @Test
    public void testDeserializeCompatibility() throws IOException {
        LegacyPojo output = mapperWithExtention.readValue(JSON, LegacyPojo.class);
        LegacyPojo instance = new LegacyPojo("foobar", "", 42, 25);
        assertThat(output, is(instance));
    }

    @Test
    public void testSerializeCompatibility() throws IOException {
        LegacyPojo instance = new LegacyPojo("foobar", "", 42, 25);
        assertThat(mapperWithExtention.writeValueAsString(instance), is(LEGACY_JSON));
    }
}
