package com.example.gpsaccelerometer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Button startButton;
    TextView txtView;
    private boolean isClicked = true;
    int speedFinal;
    int speedStarting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = findViewById(R.id.startButton);
        txtView = findViewById(R.id.textView2);
        txtView.setText("- km/h");
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
                id++;
                startButton.setText("Stop");

                isClicked = false;
                checkMovement();
                // Toast Message.
                Toast.makeText(this, "Speed logging is enabled", Toast.LENGTH_SHORT).show();
            } else {
                //Stop getting Location's updates.
                txtView.setText("-- km/h");
                startButton.setText("Start");
                isClicked = true;
                // Toast Message.
                Toast.makeText(this, "Speed logging is disabled", Toast.LENGTH_SHORT).show();
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

    public void map1(View view) {
        startActivity(new Intent(MainActivity.this, MapsActivity.class));
    }

    @Override
    public void onLocationChanged(Location location) {

        if (!isClicked) {
            if (location == null) {
                // if you can't get speed because reasons :)
                Toast.makeText(this, "NULL LOCATION", Toast.LENGTH_SHORT).show();
            } else {
                //int speed=(int) ((location.getSpeed()) is the standard which returns m/s.
                //In this example i converted it to km/h.

                speedStarting = (int) ((location.getSpeed() * 3600) / 1000);
                lati = String.valueOf(location.getLatitude());
                longi = String.valueOf(location.getLongitude());
                Date date = new Date(location.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
                time =  sdf.format(date);
                txtView.setText(String.valueOf(speedStarting)+"km/h");
                checkAccel();
            }
        }
    }

    String time;
    public String lati , longi;

    void checkAccel() {
        int Du=speedStarting - speedFinal;
        if (Du > 10){
            txtView.setTextColor(Color.GREEN);
            Toast.makeText(this, "Accelerated from "+String.valueOf(speedFinal)+"km/h to "
                    +String.valueOf(speedStarting)+"km/h", Toast.LENGTH_SHORT).show();
            pusher(true);
            speedFinal = speedStarting;
        }
        else if (Du < -10){
            txtView.setTextColor(Color.RED);
            Toast.makeText(this, "Decelerated from "+String.valueOf(speedStarting)+"km/h to "
                    +String.valueOf(speedFinal)+"km/h", Toast.LENGTH_SHORT).show();
            pusher(false);
            speedFinal = speedStarting;
        }
    }

    int k =0;
    int id=2;
    DatabaseReference myRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://gpsaccelerometer-99b4c-default-rtdb.firebaseio.com/");
    void pusher(boolean type){
        System.out.println("pusher");
        // Write log to the database
        myRef = database.getReference("user"+id);
        DatabaseReference dbref1 = myRef.child("event"+k);
        DatabaseReference dbref2 = dbref1.child("speed");
        dbref2.setValue(speedFinal);
        DatabaseReference dbref3 = dbref1.child("time");
        dbref3.setValue(time);
        DatabaseReference dbref4 = dbref1.child("location");
        dbref4.setValue(lati+","+longi);
        DatabaseReference dbref5;
        if(type) {
            dbref5 = dbref1.child("accelType");
            dbref5.setValue("acceleration");
        }
        else {
            dbref5 = dbref1.child("accelType");
            dbref5.setValue("deceleration");
        }
        k++;
    }
    void checkMovement() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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