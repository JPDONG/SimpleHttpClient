package com.demo.myhttpclient;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.demo.myhttpclient.Request.Method.GET;
import static com.demo.myhttpclient.Request.Method.POST;

/**
 * Created by dong on 2017/7/25.
 */

public class HttpUrlConnectionUtil {

    private static final String TAG = "HttpUrlConnection";

    public static InputStream execute(final Request request) {
        Callable<InputStream> callable = new Callable<InputStream>() {
            @Override
            public InputStream call() throws Exception {
                switch (request.getMethod()) {
                    case GET:
                        return get(request);
                    case POST:
                        return post(request);
                }
                return null;
            }
        };
        FutureTask futureTask = new FutureTask(callable);
        new Thread(futureTask).start();
        try {
            return (InputStream) futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void enqueue(Request request) {
        new ConnectionTask().executeOnExecutor(new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>()), request);
    }

    private static InputStream post(Request request) {
        HttpURLConnection connection = null;
        int responseCode = 0;
        try {
            connection = (HttpURLConnection) new URL(request.getUrl()).openConnection();
            connection.setRequestMethod(String.valueOf(POST));
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            writeParameters(request, connection);
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (responseCode == 200) {
            try {
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void writeParameters(Request request, HttpURLConnection connection) throws IOException {
        Map<String, String> data = request.getParameters();
        if (data == null || data.size() == 0) {
            return;
        }
        DataOutputStream output = new DataOutputStream(connection.getOutputStream());
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            stringBuilder.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&");
        }
        String temp = stringBuilder.toString();
        output.writeBytes(temp.substring(0, temp.length() - 1));
        output.flush();
        output.close();
    }

    private static InputStream get(Request request) {
        HttpURLConnection connection = null;
        int reponseCode = 0;
        try {
            connection = (HttpURLConnection) new URL(request.getUrl()).openConnection();
            connection.setRequestMethod(String.valueOf(request.getMethod()));
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setInstanceFollowRedirects(true);
            //connection.connect();
            reponseCode = connection.getResponseCode();
            Log.d(TAG, "get: reponse code = " + reponseCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (reponseCode == 200) {
            String responseHeader = getResponseHeader(connection);
            //Log.d(TAG, "get: " + responseHeader);
            try {
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getResponseHeader(HttpURLConnection connection) {
        Map<String, List<String>> responseHeaderMap = connection.getHeaderFields();
        int size = responseHeaderMap.size();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String key = connection.getHeaderFieldKey(i);
            String value = connection.getHeaderField(i);
            stringBuilder.append(key + " : " + value + "\n");
        }
        return stringBuilder.toString();
    }

    static class ConnectionTask extends AsyncTask<Object, Void, Object> {

        Request request;

        @Override
        protected Object doInBackground(Object... params) {
            request = (Request) params[0];
            ICallback callback = request.getCallback();
            if (callback == null) {
                return null;
            }
            switch (request.getMethod()) {
                case GET:
                    return callback.parse(HttpUrlConnectionUtil.get(request));
                case POST:
                    return callback.parse(HttpUrlConnectionUtil.post(request));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            ICallback callback = request.getCallback();
            if (o instanceof Exception) {
                callback.onFailure((Exception) o);
            } else {
                callback.onSuccess(o);
            }
        }
    }
}
