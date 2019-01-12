package com.github.vikes2.wifi_timer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startCrud(View view) {
        Intent myIntent = new Intent(this, FavouriteActivity.class);
        startActivity(myIntent);
    }
}
