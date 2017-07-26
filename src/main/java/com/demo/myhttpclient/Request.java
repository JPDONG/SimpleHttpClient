package com.demo.myhttpclient;

import android.support.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dong on 2017/7/25.
 */

public class Request {

    public enum Method{GET,POST}

    @NonNull
    public Method requestMethod;

    @NonNull
    public String url;

    public Map<String,String> header;

    public Map<String,String> parameters;

    public String content;

    public ICallback callback;

    public Method getMethod() {
        return requestMethod;
    }

    public Request setMethod(Method requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Request setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Request setHeader(Map<String, String> header) {
        this.header = header;
        return this;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public ICallback getCallback() {
        return callback;
    }

    public void setCallback(ICallback callback) {
        this.callback = callback;
    }

    public Request addParameters(String key, String value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
        return this;
    }
}
