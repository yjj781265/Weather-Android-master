package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "e72ca729af228beabd5d20e3b7749713";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE =123;

    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;



    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
     LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WeatherController.this, ChangecityController.class);
                startActivity(myIntent);
            }
        });




        // TODO: Add an OnClickListener to the changeCityButton here:

    }


    // TODO: Add onResume() here:
    protected  void onResume(){
        super.onResume();
        Log.d("Weater","OnResume() called");
        Intent myIntent = this.getIntent();
        String newCity = myIntent.getStringExtra("name");
        if(newCity!=null) {
            getWeatherForNewCity(newCity);
        }
        else {
            Log.d("Weather","Getting Current Location Weather info");
            getWeatherForCurrentLocation();
        }


    }



    // TODO: Add getWeatherForNewCity(String city) here:
private void getWeatherForNewCity(String cityName){
    RequestParams params = new RequestParams();
    params.put("q",cityName);
    params.put("appid",APP_ID);
    letDoSomeNetWorking(params);

}


// TODO: Add getWeatherForCurrentLocation() here:
private void getWeatherForCurrentLocation(){
    mLocationManager = (LocationManager)getSystemService(this.LOCATION_SERVICE);

    mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Weater","onLocationChanged() callback received");
            String longitude = String.valueOf(location.getLongitude());
            String latiude = String.valueOf(location.getLatitude());
            RequestParams params = new RequestParams();
            params.put("lat",latiude);
            params.put("lon",longitude);
            params.put("appid",APP_ID);
            letDoSomeNetWorking(params);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(WeatherController.this,provider.toUpperCase() +" is Enabled",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Weather","No Location found");
            Toast.makeText(WeatherController.this,"GPS is Disabled, Please enable the Location Services",Toast.LENGTH_LONG).show();


        }
    };

    if (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        return;
    }
    mLocationManager.requestLocationUpdates(LOCATION_PROVIDER,MIN_TIME,MIN_DISTANCE,mLocationListener);



}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Weather","Permission granted");
            }else{
                Log.d("Weather","Permission not granted");
            }
        }
    }



    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letDoSomeNetWorking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("Weather","Success"+response.toString());
                WeatherDataModel WeatherData = WeatherDataModel.fromJson(response);
                updateUI(WeatherData);
            }

            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){
                Log.d("Weather","Failed "+ throwable.toString());
                Toast.makeText(WeatherController.this, "Request Failed ",Toast.LENGTH_SHORT);

            }


        });

    }



    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weather){
        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());
        int imageid = getResources().getIdentifier(weather.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(imageid);
    }


    // TODO: Add onPause() here:


    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager!=null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
