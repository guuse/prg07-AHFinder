package com.example.guusmobileapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloaderTask extends AsyncTask<Void, Void, JSONObject> {

    private HttpURLConnection mHttpUrl;
    private WeakReference<FindActivity> mainReference;


    public DownloaderTask(FindActivity main) {
        super();

        mainReference = new WeakReference<>(main);
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        HttpURLConnection con = null;
        JSONObject result = new JSONObject();

        try {
            URL url = new URL(
                    FindActivity.REQUEST_URI);
            con = (HttpURLConnection) url.openConnection();

            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("GET");

            con.setDoInput(true);

            con.connect();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder("");
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            reader.close();

            result = new JSONObject(sb.toString());

        } catch (IOException e) {
            Log.e(FindActivity.LOG_TAG, "IOException", e);
        } catch (JSONException e) {
            Log.e(FindActivity.LOG_TAG, "JSONException", e);
        } catch (Exception e) {
            Log.d(FindActivity.LOG_TAG, "Something went wrong... ", e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        // All done

        Log.d(FindActivity.LOG_TAG, "Response: " + result.toString());

        return result;

    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        mainReference.get().updateAfterRequest(jsonObject);
    }
}
