package com.immortplanet.drawlove;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tom on 4/30/17.
 */

public class HttpRequest extends AsyncTask{

    class HttpResult {
        public int status;
        public JSONObject obj;

        public HttpResult(int status, JSONObject  obj){
            this.status = status;
            this.obj = obj;
        }
    }

//    public static String DOMAIN = "http://drawlove.immortplanet.com/";

    public static String DOMAIN = "http://10.0.2.2:8001";

    String method;
    String route;
    JSONObject jsonObject;
    HttpCallback onSuccess;
    HttpCallback onError;

    public HttpRequest(String method, String route, JSONObject jsonObject, HttpCallback onSuccess, HttpCallback onError) {
        this.method = method;
        this.route = route;
        this.jsonObject = jsonObject;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        HttpResult result = null;
        try {
            URL url = new URL(DOMAIN + route);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput("POST".equals(method));
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.connect();

            if ("POST".equals(method) && jsonObject != null) {
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(jsonObject.toString());
                wr.flush();
                wr.close();
            }

            BufferedReader br;
            if (httpURLConnection.getResponseCode() == 200){
                br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));
            }
            else{
                br = new BufferedReader(new InputStreamReader((httpURLConnection.getErrorStream())));
            }
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            JSONObject responseObject = new JSONObject(sb.toString());
            result = new HttpResult(httpURLConnection.getResponseCode(), responseObject);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Object o) {
        HttpResult result = (HttpResult)o;
        if (result != null) {
            if (result.status == 200) {
                if (onSuccess != null) onSuccess.finished(result.obj);
            } else {
                if (onError != null) onError.finished(result.obj);
            }
        }
        else{
            if (onError != null) onError.finished(null);
        }
    }
}
