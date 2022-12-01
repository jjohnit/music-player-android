package com.jjasan2.clipserver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class ClipServerService extends Service {

    protected final String TAG = "appDebugService";

    // Id to recognise the instance of service (for stopping)
    private int mStartID;
    Notification notification;
    // Unique id for the notification
    private static final String CHANNEL_ID = "Clip server notification" ;

    // The list of music files
    protected int[] musicList = new int[] {
            R.raw.a_very_happy_christmas, R.raw.dance_with_me, R.raw.delightful,
            R.raw.hollidays, R.raw.sleepy_cat, R.raw.vintage_telephone_ringtone
    };

    private MediaPlayer mPlayer;

    private final ClipServerServices.Stub mBinder = new ClipServerServices.Stub() {

        @Override
        public boolean startService() {
            startForeground(1, notification);
            return true;
        }

        @Override
        public boolean play(int songIndex) {
            Log.i(TAG, "songIndex is " + songIndex);
            if(songIndex > 0 && songIndex <= musicList.length){
                // Stop playback if another song is already playing
//                if (mPlayer != null) {
//                    mPlayer.stop();
//                }

                mPlayer = MediaPlayer.create(getBaseContext(), musicList[songIndex - 1]);
                mPlayer.start();
                return true;
            }
            return  false;
        }

        @Override
        public boolean pause() {
            if(mPlayer != null && mPlayer.isPlaying()){
                mPlayer.pause();
                return true;
            }
            return false;
        }

        @Override
        public boolean resume() {
            if(mPlayer != null){
                mPlayer.start();
                return true;
            }
            return false;
        }

        @Override
        public boolean stop() {
            if(mPlayer != null && mPlayer.isPlaying()){
                mPlayer.stop();
                return true;
            }
            return false;
        }

        @Override
        public boolean stopService() {
            if(mPlayer != null){
                mPlayer.stop();
            }

            stopSelf(mStartID);
            return true;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        // For creating the notification for the foreground service
        this.createNotificationChannel();

        notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true).setContentTitle("Clip server running")
                .build();

        startForeground(1, notification);

        if(mPlayer != null){
            // Stop Service when music has finished playing
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // un bind from the client when the playback stops
                    stopSelf(mStartID);
                }
            });
        }

        Log.i(TAG, "Service created");
    }

    // For creating the notification channel for the foreground service
    private void createNotificationChannel() {
        CharSequence name = "Clip server notification";
        String description = "The channel for clip server notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel ;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        super.onStartCommand(intent, flags, startId);
        mStartID = startId;
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);

        if (null != mPlayer) {
            mPlayer.stop();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mPlayer) {
            mPlayer.stop();
            mPlayer.release();
        }

        Log.i(TAG, "Service destroyed");
    }
}