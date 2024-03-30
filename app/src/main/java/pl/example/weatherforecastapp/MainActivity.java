package pl.example.weatherforecastapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    EditText textInputLayout;
    TextView textView;
    private final String url = "http://api.openweathermap.org/geo/1.0/direct?";
    private final String appid = "6b19c6b85668b0aa881c3d9a392fcbf8";
    double lat,lon;
    DecimalFormat df = new DecimalFormat("#.##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textInputLayout = findViewById(R.id.editText1);
        textView = findViewById(R.id.textView);
    }

    public void getWeather(View view) {
        String tempUrl = "";
        String city = textInputLayout.getText().toString().trim();

        if(city.equals("")){
            textView.setText("City field must be filled!!!");
        }else{
            tempUrl = url +"q="+ city + "&appid=" +appid;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response",response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            lat = jsonObject.getDouble("lat");
                            lon = jsonObject.getDouble("lon");
                            System.out.println(lat);
                            System.out.println(lon);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), volleyError.toString().trim(),Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
        if(true) //Trzeba dwukrotnie zrobic jsona jak to zrobic bardziej wydajnie czy dać  to wczesniej?
        {

            String tempUrlLoc = "https://api.openweathermap.org/data/2.5/weather?";
            tempUrlLoc+="lat="+lat+"&lon="+lon+"&appid="+appid;
            System.out.println(tempUrlLoc);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrlLoc, new Response.Listener<String>() {
                @Override
                public void onResponse(String response2) {
                    Log.d("response",response2);
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response2);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp")-273.15;
                        double feelsLike = jsonObjectMain.getDouble("feels_like")-273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        float speedWind = jsonObjectWind.getInt("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        int cloudy = jsonObjectClouds.getInt("all"); //cloudiness percantage
                        //String cityName = jsonResponse.getString("name"); Problemy z pobraniem city z json-a
                        //System.out.println(description+ " "+ temp+" "+pressure+ " "+ humidity+ " "+ speedWind+ " "+cloudy+" "+city);
                        output = "City="+city+"\n"
                                +"description="+description+"\n"
                                +"tempreature="+ temp+"\n "
                                +"feels like="+ feelsLike+"\n "
                                +"Pressure="+pressure+"\n"
                                +"Humidity="+humidity+"\n"
                                +"Speed wind="+speedWind+"\n"
                                +"Cloudy="+cloudy+"\n";
                        textView.setText(output);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), volleyError.toString().trim(),Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}