package com.example.gpsaccelerometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start1(View view){
        Toast.makeText(this, "MESSAGE BOX :) TRY", Toast.LENGTH_SHORT).show();
    }
}