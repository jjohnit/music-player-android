package com.jjasan2.clipserver;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ClipServerService extends Service {

    protected final String TAG = "appDebugService";
    private int mStartID;

    protected int[] musicList = new int[] {
            R.raw.a_very_happy_christmas, R.raw.dance_with_me, R.raw.delightful,
            R.raw.hollidays, R.raw.sleepy_cat, R.raw.vintage_telephone_ringtone
    };

    private MediaPlayer mPlayer;

    private final ClipServerServices.Stub mBinder = new ClipServerServices.Stub() {

        @Override
        public boolean play(int songIndex) {
            Log.i(TAG, "songIndex is" + songIndex);
            if(songIndex > 0 && songIndex < musicList.length){
                // Stop playback is another song is already playing
                if (null != mPlayer) {
                    mPlayer.stop();
                }

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
    };

    @Override
    public void onCreate() {
        super.onCreate();

        if(mPlayer != null){
            // Stop Service when music has finished playing
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {

                    // stop Service if it was started with this ID
                    // Otherwise let other start commands proceed
                    stopSelf(mStartID);

                }
            });
        }

        Log.i(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mStartID = startId;
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
            mPlayer.release();

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