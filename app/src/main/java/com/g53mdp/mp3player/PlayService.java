package com.g53mdp.mp3player;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.nio.BufferUnderflowException;

import static com.g53mdp.mp3player.MP3Player.MP3PlayerState.*;

public class PlayService extends Service {

    private final String CHANNEL_ID = "100";
    private int NOTIFICATION_ID = 1;
    private MP3Player player;

    private Messenger messenger;
    private Messenger replyToMessenger;


    static final int PLAY = 1;
    static final int STOP = 2;
    static final int PAUSE = 3;
    static final int GETPROGRESS = 4;
    static final int STARTNEWPLAY = 5;


    private final IBinder serviceBinder = new MyBinder();


    public class MyBinder extends Binder {
        PlayService getService(){
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }


    @Override
    public void onCreate() {
        messenger = new Messenger(new PlayHandler());
    }

    private class PlayHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            replyToMessenger = msg.replyTo;

            switch (msg.what){
                case PLAY:
                    player.play();
                    break;
                case PAUSE:
                    player.pause();
                    break;
                case STOP:
                    player.stop();
                    break;
                case STARTNEWPLAY:
                    player.stop();
                    String filePath = msg.getData().getString("filePath");
                    startNewPlay(filePath);

                    break;
                case GETPROGRESS:
                    getProgress();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initNotification(intent);

        return START_STICKY;
    }

    private void initNotification(Intent intent){

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "PlayService Channel";
            CharSequence cancelButton = "Cancel";
            String desc = "PlayService";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            channel.setDescription(desc);

            nManager.createNotificationChannel(channel);

            Intent nIntent = new Intent(PlayService.this, MainActivity.class);

            nIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, nIntent, 0);


            String filePath;
            if (intent == null){
                filePath = null;
            } else {
                filePath = intent.getExtras().getString("filePath");
            }
            NotificationCompat.Builder mBuidler = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("MP3 Player")
                    .setContentText(filePath)
                    .setContentIntent(pIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(0,cancelButton, null)
                    //set notification persistent
                    .setOngoing(true);

            nManager.notify(NOTIFICATION_ID, mBuidler.build());

            initPLayer(filePath);
        }
    }

    private void initPLayer(String filePath){

        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        player = new MP3Player();
        player.load(filePath);
        player.play();
    }

    private void getProgress(){
        Message replay = Message.obtain(null, GETPROGRESS, player.getProgress(), player.getDuration());

        try {
            replyToMessenger.send(replay);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void startNewPlay(String filePath){
        if (filePath == null){
            Log.d("start new play", "empty file path");
            return;
        }
        initPLayer(filePath);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }


    @Override
    public void onDestroy() {

        if (player != null){
            if (player.getState() == PLAYING || player.getState() == PAUSED){
                player.stop();
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        super.onDestroy();

    }
}
