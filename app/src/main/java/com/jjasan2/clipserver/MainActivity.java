package com.jjasan2.clipserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        // Start the foreground service
        Intent intent =new Intent(getApplicationContext(), ClipServerService.class);
        startService(intent);
    }

    public void onStart() {
        super.onStart();

        // Request permission for foreground service
        if (ActivityCompat.checkSelfPermission(
                this,"android.permission.FOREGROUND_SERVICE")
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {"android.permission.FOREGROUND_SERVICE"}, 1) ;
        }

        // Request permission for showing notification
        if (ActivityCompat.checkSelfPermission(
                this,"android.permission.POST_NOTIFICATIONS")
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {"android.permission.POST_NOTIFICATIONS"}, 0) ;
        }
    }

    public void onRequestPermissionsResult(int code, String[] permissions, int[] result) {
        super.onRequestPermissionsResult(code, permissions, result) ;
        if (result.length >0) {
            if (result[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Missing permissions to run foreground service", Toast.LENGTH_SHORT).show() ;
            }
        }
    }
}