package com.fivemonth.propertyresolver;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface InputStreamCallBack<T> {

    T doWithInputStream(InputStream stream) throws IOException;
}
