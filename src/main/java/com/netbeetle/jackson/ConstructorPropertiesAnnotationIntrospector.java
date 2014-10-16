package com.netbeetle.jackson;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;

public class ConstructorPropertiesAnnotationIntrospector extends NopAnnotationIntrospector
{
    @Override
    public boolean hasCreatorAnnotation(Annotated a)
    {
        if (!(a instanceof AnnotatedConstructor))
        {
            return false;
        }

        AnnotatedConstructor ac = (AnnotatedConstructor) a;

        Constructor<?> c = ac.getAnnotated();
        ConstructorProperties properties = c.getAnnotation(ConstructorProperties.class);

        if (properties == null)
        {
            return false;
        }

        for (int i = 0; i < ac.getParameterCount(); i++)
        {
            String name = properties.value()[i];
            JsonProperty jsonProperty =
                ProxyAnnotation.of(JsonProperty.class, Collections.singletonMap("value", name));
            ac.getParameter(i).addOrOverride(jsonProperty);
        }
        return true;
    }

    @Override
    public String findImplicitPropertyName(AnnotatedMember member) {
        JsonProperty property = member.getAnnotation(JsonProperty.class);
        if (property == null) {
            return null;
        }
        return property.value();
    }
}
