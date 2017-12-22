package com.nanotricks.flim.andropuprenewed.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nanotricks.flim.andropuprenewed.R;
import com.nanotricks.flim.andropuprenewed.services.PreVerifyService;
import com.nanotricks.logpup.L;

public class InfoActivity extends AppCompatActivity {

    EditText name, email, phno;
    String unique_id,n,IMEI_Number_Holder,simID,u_name,u_email,u_phno,manufacturer,model;
    TelephonyManager telephonyManager;
    private ProgressDialog progressDialog;
    ProgressBar pb;
    Button sub;
    int val=0;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setId();
        sub = findViewById(R.id.Submit);
        sub.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    sub.setTextColor(0);
                    onSubmit(view);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    sub.setTextColor(3);
                }
                return false;
            }

        });
        L.d("sd");
    }

    public void onSubmit(View view) {

        getR();
        int t = setValidation();
        //Validation
        if (t != 0) {
            try {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("user").child(IMEI_Number_Holder);

                myRef.child("Name").setValue(u_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("VA", ":" + val);
                        myRef.child("PhoneNo").setValue(u_phno).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                myRef.child("PhoneNo").setValue(u_phno).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        myRef.child("Email").setValue(u_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                myRef.child("Uid").setValue(unique_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        myRef.child("Model").setValue(manufacturer+" "+model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.e("completed","success");
                                                                finish();
                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

//                hide app icon and set this activity null
//                Toast.makeText(getApplicationContext(), "Submission success", Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "Submission Faild Check your data", Toast.LENGTH_LONG).show();s
            }
            catch (Exception e){
                e.printStackTrace();
            }
            Log.d("connected" + IMEI_Number_Holder, "well" + unique_id + " and no;" + n + " sim serial no" + simID +" value="+val);
        }
    }

    public int setValidation(){
        int n=0;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(u_name.length()>2
                &&u_email.matches(emailPattern)
                &&u_phno.length()>9 ){
            ++n;
        }
        else{
            if(u_name.length()<3)
                name.setError("Name Must Be above 3 words");
            if (!u_email.matches(emailPattern))
                email.setError("Enter valid Email id");
            if(u_phno.length()<9)
                phno.setError("Enter Valid Phone Number");
        }

       return n;
    }
    void setId() {
        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        phno = findViewById(R.id.etPhno);
    }
    @SuppressLint({"HardwareIds", "NewApi"})
    public void getR(){
        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        u_name = name.getText().toString();
        u_email = email.getText().toString().trim();
        u_phno = phno.getText().toString();
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