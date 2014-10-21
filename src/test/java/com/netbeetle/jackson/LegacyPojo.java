package com.netbeetle.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class LegacyPojo {
    @JsonProperty("new_name")
    private String name;
    private int value;
    Integer specialInt;
    private String empty;

    public LegacyPojo(String name, String empty, int value, Integer specialInt) {
        this.name = name;
        this.empty = empty;
        this.value = value;
        this.specialInt = specialInt;
    }

    @JsonCreator
    public static com.netbeetle.jackson.LegacyPojo create(@JsonProperty("new_name") String name,
                                                          @JsonProperty("empty") String empty,
                                                          @JsonProperty("value") int value,
                                                          @JsonDeserialize(using = TestSupport.IntDeserializer.class)
                                                          @JsonSerialize(using = TestSupport.IntSerializer.class)
                                                          @JsonProperty("specialInt") Integer specialInt) {
        return new com.netbeetle.jackson.LegacyPojo(name, empty, value, specialInt);
    }

    public String getName() {
        return name;
    }

    public String getEmpty() {
        return empty;
    }

    public int getValue() {
        return value;
    }

    public Integer getSpecialInt() {
        return specialInt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        com.netbeetle.jackson.LegacyPojo that = (com.netbeetle.jackson.LegacyPojo) o;

        if (value != that.value) return false;
        if (empty != null ? !empty.equals(that.empty) : that.empty != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (specialInt != null ? !specialInt.equals(that.specialInt) : that.specialInt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (empty != null ? empty.hashCode() : 0);
        result = 31 * result + value;
        result = 31 * result + (specialInt != null ? specialInt.hashCode() : 0);
        return result;
    }
}
