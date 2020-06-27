package com.curic4t.soulmate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.curic4t.soulmate.Service.SoulMateService;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_PERMISSIONS = 100;
    Context context;
    Button startButton;

    //private String Permissions[]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent =getIntent();
        if(intent!=null){
            String notiId = intent.getStringExtra("MSG");
            if(notiId!=null){
                if (notiId.compareTo("END") == 0) {
                    Intent service = new Intent(this,SoulMateService.class);
                    stopService(service);
                    onDestroy();
                    ActivityCompat.finishAffinity(this);
                    finishAndRemoveTask();
                    android.os.Process.killProcess(android.os.Process.myPid());    // 앱 프로세스 종료

                }
            }
        }

        context = getApplicationContext();
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SoulMateService.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requestOverlayPermission();
                    startForegroundService(intent);
                    finish();

                } else {
                    startService(intent);
                }

            }
        });



    }

    public boolean requestPermission() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                    Log.d("tttttt", "1");
                } else {
                    Log.d("tttttt", "2");

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW},
                            RESULT_PERMISSIONS);
                }


            } else {
                Log.d("tttttt", "3");


            }
        } else {  // version 6 이하일때

            return true;
        }

        return true;
    }

    public void requestOverlayPermission() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Log.d("ddd","there's no permisson ");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, RESULT_PERMISSIONS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (RESULT_PERMISSIONS == requestCode) {
            Log.d("tttttt", String.valueOf(grantResults.length));
            Log.d("tttttt", String.valueOf(grantResults[0]));

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허가시
                //setInit();
            } else {
                // 권한 거부시
                Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
//                finish();

            }
            return;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_PERMISSIONS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Log.d("ddddd", "2323");
                    Intent intent = new Intent(getApplicationContext(), SoulMateService.class);
                    intent.putExtra("permissionChecked", "2000");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);

                    } else {
                        startService(intent);
                    }
                } else {
                    // permission not granted...
                }
            } else {
                //21,22
                Log.d("ddddd", "2121");

                startService(new Intent(getApplicationContext(), SoulMateService.class));

            }


        } else if (requestCode == 101) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //ActivityCompat.finishAffinity(this);
        //finishAndRemoveTask();
        //android.os.Process.killProcess(android.os.Process.myPid());    // 앱 프로세스 종료

    }
}

