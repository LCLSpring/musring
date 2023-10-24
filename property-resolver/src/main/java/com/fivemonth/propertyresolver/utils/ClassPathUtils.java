package com.fivemonth.propertyresolver.utils;

import com.fivemonth.propertyresolver.InputStreamCallBack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class ClassPathUtils {

    public static <T> T readInputStream(String path, InputStreamCallBack<T> inputStreamCallBack){
        if (path.startsWith("/")){
            path = path.substring(1);
        }
        try(InputStream inputStream = getContextClassLoader().getResourceAsStream(path)) {
            if (inputStream == null){
                throw new FileNotFoundException("File not fount in path " + path);
            }
            return inputStreamCallBack.doWithInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    public static String readString(String path){
        return readInputStream(path,(input) -> {
            byte[] data = input.readAllBytes();
            return new String(data, StandardCharsets.UTF_8);
        });
    }

    static ClassLoader getContextClassLoader(){
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null){
            cl = ClassPathUtils.class.getClassLoader();
        }
        return cl;
    }
}
