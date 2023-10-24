package com.fivemonth.propertyresolver.utils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamlUtils {

    public static Map<String,Object> loadYaml(String path){
        LoaderOptions loaderOptions = new LoaderOptions();
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer(dumperOptions);
        NoImplicitResolver noImplicitResolver = new NoImplicitResolver();
        Yaml yaml = new Yaml(new Constructor(loaderOptions), representer, dumperOptions, loaderOptions, noImplicitResolver);
        return ClassPathUtils.readInputStream(path,(input)->{
            return (Map<String, Object>) yaml.load(input);
        });
    }

    public static Map<String,Object> loadYamlAsPlainMap(String path){
        Map<String, Object> data = loadYaml(path);
        Map<String,Object> plain = new LinkedHashMap<>();
        convertTo(data,"",plain);
        return plain;
    }


    static void convertTo(Map<String,Object> source,String prefix,Map<String,Object> plain){
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if(value instanceof Map){
                Map<String,Object> subMap = (Map<String, Object>) value;
                convertTo(subMap,prefix+key+".",plain);
            } else if (value instanceof List) {
                plain.put(prefix + key,value);
            } else {
                plain.put(prefix+key,value.toString());
            }
        }
    }
}

class NoImplicitResolver extends Resolver{
    public NoImplicitResolver(){
        super();
        super.yamlImplicitResolvers.clear();
    }
}
