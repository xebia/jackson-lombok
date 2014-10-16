package com.netbeetle.jackson;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.junit.Before;
import org.junit.Test;

public class ConstructorPropertiesAnnotationIntrospectorTest {

    public static final String JSON = "{\"value\":42,\"new_name\":\"foobar\"}";
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

    private static class Pojo {
        private String name;
        private int value;

        private Pojo(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @JsonCreator
        public static Pojo create(@JsonProperty("new_name") String name, @JsonProperty("value") int value) {
            return new Pojo(name, value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pojo pojo = (Pojo) o;

            if (value != pojo.value) return false;
            if (name != null ? !name.equals(pojo.name) : pojo.name != null) return false;

            return true;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + value;
            return result;
        }
    }

    @Test
    public void testDeserializeCompatibility() throws IOException {
        Pojo output = mapperWithExtention.readValue(JSON, Pojo.class);
        Pojo instance = new Pojo("foobar", 42);
        assertThat(output, is(instance));
    }
    @Test
    public void testSerializeCompatibility() throws IOException {
        assertThat(mapperWithExtention.writeValueAsString(instance), is(JSON));
    }
}
