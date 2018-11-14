package com.g53mdp.mp3player;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class PlayService extends Service {

    private final String CHANNEL_ID = "100";
    private int NOTIFICATION_ID = 1;
    private MP3Player player;

    private final IBinder serviceBinder = new MyBinder();

    public class MyBinder extends Binder {
        PlayService getService(){
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "PlayService Channel";
            String desc = "PlayService";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            channel.setDescription(desc);

            nManager.createNotificationChannel(channel);

            Intent nIntent = new Intent(PlayService.this, MainActivity.class);

            nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, nIntent, 0);


            String filePath = intent.getExtras().getString("filePath");

            NotificationCompat.Builder mBuidler = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("MP3 Player")
                    .setContentText(filePath)
                    .setContentIntent(pIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    //set notification persistent
                    .setOngoing(true);

            nManager.notify(NOTIFICATION_ID, mBuidler.build());

            initPLayer(filePath);

        }

        return START_STICKY;
    }

    private void initPLayer(String filePath){
        player = new MP3Player();
        player.load(filePath);
        player.play();
    }

    @Override
    public void onDestroy() {

        if (player.getState() == MP3Player.MP3PlayerState.PLAYING || player.getState() == MP3Player.MP3PlayerState.PAUSED){
            player.stop();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        super.onDestroy();

    }
}
