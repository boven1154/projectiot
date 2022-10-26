package com.example.iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureGroupInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    WebView tempGraphView;
    private static final String SWITCH1_URL = "https://api.thingspeak.com/update?api_key=F2D1OHKTU0YHGPFH&field1=";
    private static final String TEMPERATURE_GRAPH_URL = "https://thingspeak.com/channels/1905374/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){

            tempGraphView = findViewById(R.id.tempGraph);
            tempGraphView.loadUrl(TEMPERATURE_GRAPH_URL);
            tempGraphView.getSettings().setJavaScriptEnabled(true);

        }else {

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    FetchThingspeakTask field1 = new FetchThingspeakTask();

                    FetchThingspeakTask field2 = new FetchThingspeakTask();

                }
            }, 0, 10000);
        }
    }

public void onSwitch1(View v){
        String FIELD1_URL;
        boolean on = ((Switch) v).isChecked();
        if(on) {
            FIELD1_URL = SWITCH1_URL + "1";
        }else{
            FIELD1_URL = SWITCH1_URL + "0";
        }
        FetchThingspeakTask field3 = new FetchThingspeakTask();
        field3.execute(FIELD1_URL);
        disableSwitch(15000);
    }


    private void disableSwitch(int delay_ms){
        final Switch Switch1View = findViewById(R.id.switch2);

        Switch1View.setEnabled(false);
        Switch1View.postDelayed(new Runnable() {
            @Override
            public void run() {
                Switch1View.setEnabled(true);
            }
        },delay_ms);


    }

    private class FetchThingspeakTask extends AsyncTask<String, Void, String>{
        protected String doInBackground(String... urls){
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line= bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();

                }finally {
                    urlConnection.disconnect();
                }
            }catch (Exception e){
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
        protected void onPostExecute(String response){
            if(response == null){
                Toast.makeText(MainActivity.this, "There was an error!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                if (channel.has("field3")){
                    String status = channel.getString("field3");
                    if (!status.equals("null")){
                        String message;
                        if (status.equals("1")){
                            message = getText(R.string.on)+ " " + getText(R.string.light1);
                        }else {
                            message = getText(R.string.off)+ " " + getText(R.string.light1);
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}