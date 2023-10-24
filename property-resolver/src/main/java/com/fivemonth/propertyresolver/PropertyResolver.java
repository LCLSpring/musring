package com.fivemonth.propertyresolver;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;
import java.util.function.Function;

public class PropertyResolver {

    Logger logger = LoggerFactory.getLogger(getClass());
    Map<String,String> properties = new HashMap<>();
    Map<Class<?>, Function<String,Object>> coverters = new HashMap<>();

    public PropertyResolver(Properties props){
        properties.putAll(System.getenv());
        Set<String> names = props.stringPropertyNames();
        for(String name : names){
            properties.put(name,props.getProperty(name));
        }
        if (logger.isDebugEnabled()){
            Set<String> keySet = properties.keySet();
            for (String key : keySet){
                logger.debug("Property Resolve {} = {}",key,properties.get(key));
            }
        }
        coverters.put(String.class,s -> s);

        coverters.put(boolean.class,s -> Boolean.parseBoolean(s));
        coverters.put(Boolean.class,s -> Boolean.valueOf(s));

        coverters.put(byte.class,s -> Byte.parseByte(s));
        coverters.put(Byte.class, s -> Byte.valueOf(s));

        coverters.put(short.class, s -> Short.parseShort(s));
        coverters.put(Short.class,s -> Short.valueOf(s));

        coverters.put(int.class,s -> Integer.parseInt(s));
        coverters.put(Integer.class, s -> Integer.valueOf(s));

        coverters.put(long.class, s -> Long.parseLong(s));
        coverters.put(Long.class, s -> Long.valueOf(s));

        coverters.put(float.class, s -> Float.parseFloat(s));
        coverters.put(Float.class, s -> Float.valueOf(s));

        coverters.put(double.class, s -> Double.parseDouble(s));
        coverters.put(Double.class, s -> Double.valueOf(s));

        coverters.put(LocalDate.class, s -> LocalDate.parse(s));
        coverters.put(LocalTime.class, s -> LocalTime.parse(s));
        coverters.put(LocalDateTime.class, s -> LocalDateTime.parse(s));
        coverters.put(ZonedDateTime.class, s -> ZonedDateTime.parse(s));
        coverters.put(Duration.class, s -> Duration.parse(s));
        coverters.put(ZoneId.class, s -> ZoneId.of(s));
    }

    public boolean containProperty(String key){
        return properties.containsKey(key);
    }

    @Nullable
    public String getProperty(String key){
        PropertyExpr keyExpr = parsePropertyExpr(key);
        if(keyExpr != null){
            if (keyExpr.defaultValue() != null){
                return getProperty(keyExpr.key(),keyExpr.defaultValue());
            } else {
                return getRequiredProperty(keyExpr.key());
            }
        }
        String value = properties.get(key);
        if (value != null){
            return parseValue(value);
        }
        return null;
    }

    public String getProperty(String key, String s) {
        String value = getProperty(key);
        return value == null ? parseValue(s) : value;
    }

    public <T> T getProperty(String key,Class<T> targetType){
        String value = getProperty(key);
        return value == null ? null : covert(targetType,value);
    }

    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        String value = getProperty(key);
        if (value == null){
            return defaultValue;
        }
        return covert(targetType,value);
    }

    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        return Objects.requireNonNull(value, "property " + key + " not found");
    }

    public <T> T getRequiredProperty(String key,Class<T> targetType){
        T value = getProperty(key,targetType);
        return Objects.requireNonNull(value, "property " + key + " not found");
    }

    public <T> T covert(Class<T> targetType, String value) {
        Function<String, Object> fn = coverters.get(targetType);
        if (fn == null){
            throw new IllegalArgumentException("Unsupport type: " + targetType.getName());
        }
        return (T) fn.apply(value);
    }

    public String parseValue(String value) {
        PropertyExpr expr = parsePropertyExpr(value);
        if (expr == null) {
            return value;
        }
        if (expr.defaultValue() != null){
            return getProperty(expr.key(), expr.defaultValue());
        } else {
            return getRequiredProperty(expr.key());
        }
    }



    PropertyExpr parsePropertyExpr(String key){
        if (key.startsWith("${") && key.endsWith("}")){
            int n = key.indexOf(":");
            if (n == -1){
                String k = notEmpty(key.substring(2,key.length()-1));
                return new PropertyExpr(k,null);
            } else {
                String k = notEmpty(key.substring(2,n));
                return new PropertyExpr(k,key.substring(n+1,key.length()-1));
            }
        }
        return null;
    }

    String notEmpty(String key){
        if (key.isEmpty()){
            throw new IllegalArgumentException("Invalid key " + key);
        }
        return key;
    }
}

record PropertyExpr(String key,String defaultValue){

}
