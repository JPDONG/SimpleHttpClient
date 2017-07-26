package com.demo.myhttpclient;

import java.io.InputStream;

/**
 * Created by dong on 2017/7/26.
 */

public interface ICallback<T> {

    void onSuccess(T result);

    void onFailure(Exception e);

    T parse(InputStream resultStream);
}
