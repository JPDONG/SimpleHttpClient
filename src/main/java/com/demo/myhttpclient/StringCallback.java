package com.demo.myhttpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by dong on 2017/7/26.
 */

public abstract class StringCallback implements ICallback<String> {

    @Override
    public String parse(InputStream resultStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(resultStream));
            String line;
            while ((line = bufferReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
