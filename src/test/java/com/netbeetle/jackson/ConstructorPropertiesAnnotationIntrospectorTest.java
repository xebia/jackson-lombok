package com.netbeetle.jackson;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Value;
import org.junit.Before;
import org.junit.Test;

public class ConstructorPropertiesAnnotationIntrospectorTest {

    public static final String JSON = "{\"value\":42,\"specialInt\":\"24\",\"new_name\":\"foobar\"}";
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
        @JsonDeserialize(using = IntDeserializer.class)
        @JsonSerialize(using = IntSerializer.class)
        Integer specialInt;
    }

    private static class IntDeserializer extends JsonDeserializer<Integer> {
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

    private static class IntSerializer extends JsonSerializer<Integer> {
        @Override
        public void serialize(Integer value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value == -1 ? null : (value - 1) + "");
        }
    }

    private final ImmutablePojo instance = new ImmutablePojo("foobar", 42, 25);

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

    private static class LegacyPojo {
        private String name;
        private int value;
        Integer specialInt;

        private LegacyPojo(String name, int value, Integer specialInt) {
            this.name = name;
            this.value = value;
            this.specialInt = specialInt;
        }

        @JsonCreator
        public static LegacyPojo create(@JsonProperty("new_name") String name, @JsonProperty("value") int value,
                                  @JsonDeserialize(using = IntDeserializer.class)
                                  @JsonSerialize(using = IntSerializer.class)
                                  @JsonProperty("specialInt") Integer specialInt) {
            return new LegacyPojo(name, value, specialInt);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LegacyPojo legacyPojo = (LegacyPojo) o;

            if (value != legacyPojo.value) return false;
            if (name != null ? !name.equals(legacyPojo.name) : legacyPojo.name != null) return false;
            if (specialInt != null ? !specialInt.equals(legacyPojo.specialInt) : legacyPojo.specialInt != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + value;
            result = 31 * result + (specialInt != null ? specialInt.hashCode() : 0);
            return result;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        public Integer getSpecialInt() {
            return specialInt;
        }
    }

    @Test
    public void testDeserializeCompatibility() throws IOException {
        LegacyPojo output = mapperWithExtention.readValue(JSON, LegacyPojo.class);
        LegacyPojo instance = new LegacyPojo("foobar", 42, 25);
        assertThat(output, is(instance));
    }

    @Test
    public void testSerializeCompatibility() throws IOException {
        assertThat(mapperWithExtention.writeValueAsString(instance), is(JSON));
    }
}
