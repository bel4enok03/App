package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;

import android.util.Log;

public class WeatherActivity extends AppCompatActivity {

    private EditText cityInput;
    private TextView weatherTextView;
    private Button getWeatherButton;
    private Button mainMenuButton;

    private static final String TAG = "WeatherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityInput = findViewById(R.id.cityInput);
        weatherTextView = findViewById(R.id.weatherTextView);
        getWeatherButton = findViewById(R.id.getWeatherButton);
        mainMenuButton = findViewById(R.id.mainMenuButton);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityInput.getText().toString();
                Log.d(TAG, "Введите город: " + city);
                getWeather(city);
            }
        });

        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getWeather(String city) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    String apiKey = getString(R.string.api_key);
                    String encodedCity = URLEncoder.encode(city, "UTF-8");
                    String weatherUrl = getString(R.string.weather_url, encodedCity, apiKey);
                    URL url = new URL(weatherUrl);
                    Log.d(TAG, "URL for API call: " + url.toString());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "HTTP response code: " + responseCode);

                    BufferedReader rd;
                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    String line;
                    StringBuilder result = new StringBuilder();
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    rd.close();

                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "API response: " + result.toString());
                        parseWeatherData(result.toString());
                    } else {
                        Log.e(TAG, "Error response from API: " + result.toString());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in getWeather: ", e);
                } finally {
                    if(conn != null) {
                        conn.disconnect();
                    }
                }
            }
        });
        thread.start();
    }

    private void parseWeatherData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);

            String weather = getString(R.string.weather, jsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));
            double temp = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            String temperature = getString(R.string.temperature, temp);
            double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
            String windSpeedString = getString(R.string.wind_speed, windSpeed);
            int humidity = jsonObject.getJSONObject("main").getInt("humidity");
            String humidityString = getString(R.string.humidity, humidity);

            final String weatherDetails = weather + "\n" + temperature + "\n" + windSpeedString + "\n" + humidityString;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weatherTextView.setText(weatherDetails);
                    Log.d(TAG, "Weather set in TextView: " + weatherDetails);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in parseWeatherData: ", e);
        }
    }
}
