package com.develop.childtracking.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FcmNotifier {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void sendNotification(final String body, final String title, final String key) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("text", body);
                    dataJson.put("title", title);
                    dataJson.put("priority", "high");
                    json.put("notification", dataJson);
                    json.put("to", key);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=AIzaSyCIl77TDdVkVyfevLwIGmdCavvn2eBWi8Y")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.i("kunwar", finalResponse);
                } catch (Exception e) {

                    Log.i("kunwar", e.getMessage());
                }
                return null;
            }
        }.execute();

    }
}
