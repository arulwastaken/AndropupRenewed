package com.nanotricks.flim.andropuprenewed.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by user on 12/14/2017.
 */

public class AndroPupService extends Service {
    String unique_id, n, IMEI_Number_Holder, simID;
    TelephonyManager telephonyManager;

    public AndroPupService() {
        super();
    }

    private LocationListener listener;
    private LocationManager locationManager;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String val = intent.getStringExtra("record");
        Log.e("AndroPup","Service");
        getData();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user/"+IMEI_Number_Holder);
        myRef.child("Lost/Activity").setValue("continue");
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("Latitude "+location.getLatitude(),"Longitude"+location.getLongitude());
                myRef.child("Lost/SimId").setValue(simID);
                myRef.child("Lost/SimNo").setValue(n);

                myRef.child("Lost/Location/Latitude").setValue(location.getLatitude());
                myRef.child("Lost/Location/Longitude").setValue(location.getLongitude());
                Log.e("OnLocation","Submited");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 100, listener);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Device is Found","status:"+dataSnapshot.child("Lost/Activity").getValue());
                String s=dataSnapshot.child("Lost/Activity").getValue().toString();
                if(s.equals("stop")){
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager !=null){
            locationManager.removeUpdates(listener);
        }
    }

    private void getData() {
        unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        IMEI_Number_Holder = telephonyManager.getDeviceId();
        n = telephonyManager.getLine1Number();
        simID = telephonyManager.getSimSerialNumber();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
    public class FirebaseUserDetails
    {
        private String mDisplayName;
        public String getmDisplayName() {
            return mDisplayName;
        }
        public void setmDisplayName(String mDisplayName) {
            this.mDisplayName = mDisplayName;
        }
    }
}
