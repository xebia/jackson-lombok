package com.netbeetle.jackson;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.BeanUtil;

public class ConstructorPropertiesAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public boolean hasCreatorAnnotation(Annotated a) {
        if (super.hasCreatorAnnotation(a)) {
            return true;
        } else if (!(a instanceof AnnotatedConstructor)) {
            return false;
        } else {
            AnnotatedConstructor ac = (AnnotatedConstructor) a;

            Constructor<?> c = ac.getAnnotated();
            ConstructorProperties properties = c.getAnnotation(ConstructorProperties.class);

            if (properties == null) {
                return false;
            }

            for (int i = 0; i < ac.getParameterCount(); i++) {
                String name = properties.value()[i];
                try {
                    Field field = ac.getDeclaringClass().getDeclaredField(name);
                    if (field != null) {
                        JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                        if (annotation != null) {
                            name = annotation.value();
                        }
                    }
                } catch (NoSuchFieldException ignored) {
                }
                JsonProperty jsonProperty =
                        ProxyAnnotation.of(JsonProperty.class, Collections.singletonMap("value", name));
                ac.getParameter(i).addOrOverride(jsonProperty);
            }
            return true;
        }
    }


    @Override
    public String findImplicitPropertyName(AnnotatedMember member) {
        JsonProperty property = member.getAnnotation(JsonProperty.class);

        if (property == null) {
            if (member instanceof AnnotatedMethod) {
                AnnotatedMethod method = (AnnotatedMethod) member;
                String fieldName = BeanUtil.okNameForGetter(method);
                try {
                    if (fieldName != null) {
                        Field field = member.getDeclaringClass().getDeclaredField(fieldName);
                        if (field != null) {
                            JsonProperty fieldProperty = field.getAnnotation(JsonProperty.class);
                            if (fieldProperty != null) {
                                return fieldProperty.value();
                            }
                        }
                    }
                } catch (NoSuchFieldException ignored) {
                }

            }
            return null;
        }
        return property.value();
    }
}
