package com.nanotricks.flim.andropuprenewed.receiver;

/**
 * Created by user on 12/14/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.nanotricks.flim.andropuprenewed.Manifest;
import com.nanotricks.flim.andropuprenewed.services.PreVerifyService;
import com.nanotricks.logpup.L;

public class OnBootUp extends BroadcastReceiver {
    String unique_id="He",IMEI_Number_Holder="Ha";
    TelephonyManager telephonyManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        runtime_permissions(context);
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Intent preverify = new Intent(context, PreVerifyService.class);
                preverify.putExtra("boot","1");
                context.startService(preverify);
                Log.e("imei:"+IMEI_Number_Holder,"id"+unique_id);
            } else {
                Log.e("imei+no internet"+IMEI_Number_Holder,"id"+unique_id);
                //halt process
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean runtime_permissions(Context con) {
        if(Build.VERSION.SDK_INT >=23 && ContextCompat.checkSelfPermission(con,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(con, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
     return false;
    }
}
