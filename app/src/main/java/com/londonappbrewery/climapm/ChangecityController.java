package com.londonappbrewery.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangecityController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);
        final EditText text = (EditText)findViewById(R.id.queryET);
        ImageButton button = (ImageButton)findViewById(R.id.backButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });
     text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
         @Override
         public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
             String newCity = text.getText().toString();
             Intent newCityIntent = new Intent(ChangecityController.this, WeatherController.class);
             newCityIntent.putExtra("name",newCity);
             startActivity(newCityIntent);
             finish();
             return false;
         }
     });

    }
}
