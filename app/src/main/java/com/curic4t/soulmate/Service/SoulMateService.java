package com.curic4t.soulmate.Service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.curic4t.soulmate.CustomImageView;
import com.curic4t.soulmate.MainActivity;
import com.curic4t.soulmate.PermissionCheck;
import com.curic4t.soulmate.R;

public class SoulMateService extends Service {
    private static SoulMateService soulMateService;
    private WindowManager wm;
    private View mView;
    private ImageView mainImageView;
    private  WindowManager.LayoutParams params = null;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";

    private int displayX;
    private int displayY;
//    private  SoulMateService(){}
    public static SoulMateService getInstance(){
        return soulMateService;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            checkFinishMessage(intent);
        }

        Log.d("ddddd", "sssonStartCommand");
        if (mainImageView == null) {
            mainImageView = (ImageView) mView.findViewById(R.id.mainImageView);
        } else {
            if (mainImageView.isAttachedToWindow()) {
                wm.removeView(mainImageView);
            }

        }
        initSetting();

        wm.addView(mainImageView, params);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d("ddddd", "ssssonCreate");

        if(soulMateService==null){
            soulMateService=this;
        }


        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display=wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        displayX=point.x;
        displayY=point.y;




        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            //| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                           // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    , PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    //| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    , PixelFormat.TRANSLUCENT);
        }
        params.gravity = Gravity.LEFT | Gravity.TOP;
        mView = inflate.inflate(R.layout.service_layout, null);
        mainImageView = (ImageView) mView.findViewById(R.id.mainImageView);

    }


    @Override
    public void onDestroy() {
        Log.d("ddddd", "ssssonDestroy");

        super.onDestroy();
        if (wm != null) {
            if (mView != null) {
                wm.removeView(mView);
                mView = null;
            }
            wm = null;
        }
        stopForeground(true);
        stopSelf();
        Context context = this;

        //finishAndRemoveTask();
        android.os.Process.killProcess(android.os.Process.myPid());    // 앱 프로세스 종료

    }


    public void initSetting() {
        if (mainImageView != null) {
            Log.d("ddd","view setting");
            mainImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("dddd","ddddd");
                }
            });
            mainImageView.setOnTouchListener(new View.OnTouchListener() {

                private float prevX;
                private float prevY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("ddd","touched");

//                    try {
//                        Thread.sleep(10);
//
//                    } catch (InterruptedException e) {
//
//                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: // 처음 위치를 기억해둔다.
                            prevX = event.getRawX();
                            prevY = event.getRawY();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            float rawX = event.getRawX(); // 절대 X 좌표 값을 가져온다.
                            float rawY = event.getRawY(); // 절대 Y 좌표값을 가져온다.

                            // 이동한 위치에서 처음 위치를 빼서 이동한 거리를 구한다.
                            float x = rawX - prevX;
                            float y = rawY - prevY;
                            if (params != null) {
                                params.x += (int) x;
                                params.y += (int) y;
                                Log.d("dddddd", params.x + " " + params.y);

                                wm.updateViewLayout(mainImageView, params);
                            }

                            prevX = rawX;
                            prevY = rawY;
                            break;
                    }
                    return false;
                }
            });
            mainImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });

        }
//        Thread tread = new Thread() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Log.d("dddddd", "asdasd");
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//
//                    }
//                }
//            }
//
//        };
//        tread.start();

        showNotification();
    }

    public void showNotification() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("MSG", "END");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentTitle("SoulMate");
        builder.setContentText("터치 시 종료합니다.");
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(1, notification);
        //notificationManager.notify(1, notification);


    }

    public void checkFinishMessage(Intent intent) {
        if (intent != null) {
            String notiId = intent.getStringExtra("MSG");
            if (notiId != null) {
                if (notiId.compareTo("END") == 0) {
                    onDestroy();
                    Log.d("dddd", "asdasd");
                }
            }
        }

    }

}
