Jackson Lombok
==================

This repository contains extension for [Jackson][1] which allows better interoperability with [Lombok][3] classes.

The `JacksonLombokAnnotationIntrospector` allows Jackson to serialize and deserialize classes with constructors that are annotated with the [java.beans.ConstructorProperties][2] annotations. [Lombok][3] will automatically add this annotation to the constructors it generates.

## Usage
Add the `JacksonLombokAnnotationIntrospector` to the Jackson `ObjectMapper` in the following manner.

``` java
new ObjectMapper()
    .setAnnotationIntrospector(new JacksonLombokAnnotationIntrospector());
```

Now you can serialize and deserialize Lombok objects with generated constructors like the following class.

``` java
@Value
private static class ImmutablePojo {
    @JsonProperty("new_name")
    String name;
    @JsonProperty
    String string;
}
```

##Installation
Add the following maven depency

``` xml
<dependency>
    <groupId>com.xebia</groupId>
    <artifactId>jackson-lombok</artifactId>
    <version>1.1</version>
</dependency>
```

##Licence
All files are provided under an MIT license.

[1]: http://jackson.codehaus.org/
[2]: http://download.oracle.com/javase/6/docs/api/java/beans/ConstructorProperties.html
[3]: http://projectlombok.org/
