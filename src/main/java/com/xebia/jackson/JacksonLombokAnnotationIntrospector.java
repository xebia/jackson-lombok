package com.xebia.jackson;

import java.beans.ConstructorProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.BeanUtil;

public class JacksonLombokAnnotationIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public boolean hasCreatorAnnotation(Annotated annotated) {
        if (super.hasCreatorAnnotation(annotated)) {
            return true;
        } else if (!(annotated instanceof AnnotatedConstructor)) {
            return false;
        } else {
            AnnotatedConstructor annotatedConstructor = (AnnotatedConstructor) annotated;

            ConstructorProperties properties = getConstructorPropertiesAnnotation(annotatedConstructor);

            if (properties == null) {
                return false;
            } else {
                addJacksonAnnotationsToContructorParameters(annotatedConstructor);
                return true;
            }
        }
    }

    private void addJacksonAnnotationsToContructorParameters(AnnotatedConstructor annotatedConstructor) {
        ConstructorProperties properties = getConstructorPropertiesAnnotation(annotatedConstructor);
        for (int i = 0; i < annotatedConstructor.getParameterCount(); i++) {
            String name = properties.value()[i];
            AnnotatedParameter parameter = annotatedConstructor.getParameter(i);
            Field field = null;
            try {
                field = annotatedConstructor.getDeclaringClass().getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
            addJacksonAnnotationsToConstructorParameter(field, parameter, name);
        }
    }

    private void addJacksonAnnotationsToConstructorParameter(Field field, AnnotatedParameter parameter, String name) {
        if (field != null) {
            for (Annotation a : field.getAnnotations()) {
                if (a.annotationType().getName().startsWith("com.fasterxml")) {
                    if (a.annotationType() != JsonProperty.class) {
                        parameter.addOrOverride(a);
                    } else {
                        JsonProperty jp = (JsonProperty) a;
                        if (!jp.value().equals("")) {
                            name = jp.value();
                        }
                    }
                }
            }
        }

        JsonProperty jsonProperty =
                ProxyAnnotation.of(JsonProperty.class, Collections.singletonMap("value", name));
        parameter.addOrOverride(jsonProperty);
    }

    private ConstructorProperties getConstructorPropertiesAnnotation(AnnotatedConstructor annotatedConstructor) {
        Constructor<?> constructor = annotatedConstructor.getAnnotated();
        return constructor.getAnnotation(ConstructorProperties.class);
    }


    @Override
    public String findImplicitPropertyName(AnnotatedMember member) {
        JsonProperty property = member.getAnnotation(JsonProperty.class);
        if (property == null) {
            if (member instanceof AnnotatedMethod) {
                AnnotatedMethod method = (AnnotatedMethod) member;
                String fieldName = BeanUtil.okNameForGetter(method);
                return getJacksonPropertyName(member.getDeclaringClass(), fieldName);
            }
        } else if (!property.value().equals("")) {
            return property.value();
        }

        return null;
    }

    private String getJacksonPropertyName(Class<?> declaringClass, String fieldName) {
        if (fieldName != null) {
            try {
                Field field = declaringClass.getDeclaredField(fieldName);
                if (field != null) {
                    JsonProperty fieldProperty = field.getAnnotation(JsonProperty.class);
                    if (fieldProperty != null && !fieldProperty.value().equals("")) {
                        return fieldProperty.value();
                    }
                }
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }
}
