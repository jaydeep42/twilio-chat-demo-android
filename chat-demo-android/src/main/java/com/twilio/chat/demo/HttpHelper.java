package com.twilio.chat.demo;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class HttpHelper {

    private static final String TAG = "HttpHelper";

    private static String stringFromInputStream(InputStream is) throws IOException {
        char[] buf = new char[1024];
        StringBuilder out = new StringBuilder();

        Reader in = new InputStreamReader(is, "UTF-8");

        int bin;
        while ((bin = in.read(buf, 0, buf.length)) >= 0) {
            out.append(buf, 0, bin);
        }

        return out.toString();
    }

    public static String httpGet(final String username, String url) throws Exception {
        URL urlObj = new URL(url);

        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

        conn.setConnectTimeout(45000);
        conn.setReadTimeout(30000);
        conn.setDoInput(true);

        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream is = conn.getInputStream();
//            String      accessToken = stringFromInputStream(is);

            JSONObject object = new JSONObject(stringFromInputStream(is));
            String accessToken = object.get("token").toString();
            String identity = object.get("identity").toString();

            Log.e(TAG, "httpGet_identity: " + identity);
            Log.e(TAG, "httpGet_accesstoken: " + accessToken);

            is.close();
            conn.disconnect();
            return accessToken;
        } else {
            conn.disconnect();
            throw new Exception("Got error code " + responseCode + " from server");
        }
    }
}
