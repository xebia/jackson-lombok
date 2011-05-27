package com.netbeetle.jackson;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.Collections;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.introspect.NopAnnotationIntrospector;

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
    public String findPropertyNameForParam(AnnotatedParameter param)
    {
        JsonProperty property = param.getAnnotation(JsonProperty.class);
        if (property == null)
        {
            return null;
        }
        return property.value();
    }
}
