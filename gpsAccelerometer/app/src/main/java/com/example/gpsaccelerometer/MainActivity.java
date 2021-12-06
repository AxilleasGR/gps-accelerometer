package com.example.gpsaccelerometer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Button startButton;
    TextView txtView;
    TextView txtView1;
    TextView txtView2;
    private boolean isClicked = true;
    LocationManager locationManager;
    int speedFinal;
    int speedStarting;
    boolean speedSelector = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = findViewById(R.id.startButton);
        txtView = findViewById(R.id.textView2);
        txtView1 = findViewById(R.id.textView3);
        txtView2 = findViewById(R.id.textView4);

        // Location's Permission check on create of the App.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Ask for Location's Permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 234);
        }
        isLocationEnabled(this);

        // Lambda Expression for Enable button listener.
        startButton.setOnClickListener((View view) -> {

            // if Boolean variable isClicked is true.
            if (isClicked) {
//                //Provide Location's updates.
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                //locationManager.requestLocationUpdates(locationManager.);

                startButton.setText("Stop");

                //Background Color becomes green.
                getWindow().getDecorView().setBackgroundColor(Color.GREEN);

                isClicked = false;
                checkMovement();
                // Toast Message.
                Toast.makeText(this, "Speed capture is enabled", Toast.LENGTH_SHORT).show();
            } else {
                //Stop getting Location's updates.
                ///locationManager.removeUpdates((LocationListener) this);
                //actionBar.setTitle("Speedometer");
                //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#14C5DC")));
                //speedTextView.setText("--");

                //Background Color becomes white.
                getWindow().getDecorView().setBackgroundColor(Color.WHITE);

                startButton.setText("Start");

                isClicked = true;
                // Toast Message.
                Toast.makeText(this, "Speed capture is disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Checks if location service is enabled by the user.
    public void isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Locations is not Enabled")
                    .setMessage("Please go to Settings and enable your location.")
                    .setPositiveButton("Enable Location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
                        }
                    })
                    .show();
        }
    }


    public void start1(View view) {
        Toast.makeText(this, "MESSAGE BOX START :)", Toast.LENGTH_SHORT).show();
    }

    public void log1(View view) {
        Toast.makeText(this, "MESSAGE BOX LOG :)", Toast.LENGTH_SHORT).show();
    }

    public void map1(View view) {
        Toast.makeText(this, "MESSAGE BOX MAP :)", Toast.LENGTH_SHORT).show();
    }
    int temp=0;
    @Override
    public void onLocationChanged(Location location) {

        if (!isClicked) {
            if (location == null) {
                // if you can't get speed because reasons :)

                //Toast.makeText(this, "NULL LOCATION", Toast.LENGTH_SHORT).show();
            } else {
                //int speed=(int) ((location.getSpeed()) is the standard which returns meters per second. In this example i converted it to kilometers per hour

                speedStarting = (int) ((location.getSpeed() * 3600) / 1000);
                txtView.setText(String.valueOf(speedStarting));
                txtView2.setText(String.valueOf(speedFinal));
                checkAccel();
                //Toast.makeText(this, speedStr, Toast.LENGTH_SHORT).show();
            }
        }
    }
    void checkAccel() {
        int Du=speedStarting - speedFinal;
        txtView1.setText(String.valueOf(speedFinal - speedStarting));
        if (Du > 10){
            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            Toast.makeText(this, "accel", Toast.LENGTH_SHORT).show();
            speedFinal = speedStarting;
        }
        else if (Du < -10){
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            speedFinal = speedStarting;
        }
//        else if (Du == 0){
//            //getWindow().getDecorView().setBackgroundColor(Color.WHITE);
//        }
    }

    void checkMovement() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.onLocationChanged((Location) null);
    }
}