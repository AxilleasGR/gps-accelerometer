package com.example.gpsaccelerometer;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gpsaccelerometer.databinding.ActivityMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationAndAccel();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationAndAccel();
        drawMarkers();
    }

    void drawMarkers(){
            for (int i = 0; i < locs.size(); i++) {
                String title;
                String snippet;
                float markerColor;
                String[] arrOfStr = locs.get(i).split(",");
                if (accel.get(i).equals("acceleration")) {
                    title = "acceleration";
                    markerColor = BitmapDescriptorFactory.HUE_AZURE;
                }
                else {
                    title = "deceleration";
                    markerColor = BitmapDescriptorFactory.HUE_MAGENTA;
                }
                snippet = "speed before the event was : "+ speed.get(i);
                createMarker(Double.parseDouble(arrOfStr[0]), Double.parseDouble(arrOfStr[1]), title, snippet,markerColor);
                LatLng focus = new LatLng(Double.parseDouble(arrOfStr[0]), Double.parseDouble(arrOfStr[1]));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(focus));
            }
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet,float color) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
    }

    ArrayList<String> locs = new ArrayList<String>();
    ArrayList<String> accel = new ArrayList<String>();
    ArrayList<String> speed = new ArrayList<String>();
    void getLocationAndAccel() {
        FirebaseDatabase.getInstance("https://gpsaccelerometer-99b4c-default-rtdb.firebaseio.com/").getReference().
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Object user = snapshot.getValue(Object.class);
                    for(DataSnapshot snapshot1 : snapshot.getChildren())
                    {
                        String loc = String.valueOf(snapshot1.child("location").getValue());
                        String accl = String.valueOf(snapshot1.child("accelType").getValue());
                        String speedo = String.valueOf(snapshot1.child("speed").getValue());
                        locs.add(loc);
                        accel.add(accl);
                        speed.add(speedo);
                    }
                    System.out.println(locs);
                    drawMarkers();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}