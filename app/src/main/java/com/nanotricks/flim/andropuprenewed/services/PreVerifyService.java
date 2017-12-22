package com.nanotricks.flim.andropuprenewed.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nanotricks.flim.andropuprenewed.activity.InfoActivity;
import com.nanotricks.logpup.L;

/**
 * Created by user on 12/18/2017.
 */

public class PreVerifyService extends Service {
    String unique_id,n,IMEI_Number_Holder,simID;
    TelephonyManager telephonyManager;
    Context context;
    public PreVerifyService() {
        super();

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            //TODO do something useful

            Log.e("Preverify","service");

            String val = intent.getStringExtra("boot");
            getValue();
            fetchInfoImei();
            return Service.START_NOT_STICKY;
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
        public IBinder onBind(Intent intent) {
            //TODO for communication return IBinder implementation
            return null;
        }

        public void fetchInfoImei(){
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("user");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(IMEI_Number_Holder)) {
                        // run some code
                        fetchInfoId();
                        Log.e("Value","child is there:"+dataSnapshot.hasChild(IMEI_Number_Holder));
                    }
                    else {
                        Intent i=new Intent(getApplicationContext(), InfoActivity.class);
                        startActivity(i);
                        onDestroy();
                        Log.e("Imei not found","result:"+dataSnapshot.hasChild(IMEI_Number_Holder));
                    }
                    Log.e("Second Check","child"+dataSnapshot.hasChild(IMEI_Number_Holder));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    public void fetchInfoId(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user/"+IMEI_Number_Holder+"/Uid");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.getValue().equals(unique_id)){
                    //TO hide  app in launcher
                    /*PackageManager p = getPackageManager();
                    ComponentName componentName = new ComponentName(getApplicationContext(), com.nanotricks.flim.andropuprenewed.activity.InfoActivity.class);
                    p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);*/
                    Intent in=new Intent(getApplicationContext(),AndroPupService.class);
                    in.putExtra("record","success");
                    startService(in);
                    Log.e("Device Lost:"+dataSnapshot.getValue(),"call APS"+unique_id);
                }
                else if(dataSnapshot.getValue()==null){
                    Log.e("null","found");
                }
                else {
                    Log.e("Id","Found So bubye");
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


        public void getValue(){
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
    }
