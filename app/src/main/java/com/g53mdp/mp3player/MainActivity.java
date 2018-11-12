package com.g53mdp.mp3player;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;



public class MainActivity extends AppCompatActivity {

    private final String CHANNEL_ID = "100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        testNotification();
    }

    private void testNotification(){
        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "channel name";
            String desc = "channel description";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            channel.setDescription(desc);

            nManager.createNotificationChannel(channel);


            int NOTIFICATION_ID = 1;

            Intent nIntent = new Intent();

            nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, nIntent, 0);

            NotificationCompat.Builder mBuidler = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("notification title")
                    .setContentText("notificatio text")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            nManager.notify(NOTIFICATION_ID, mBuidler.build());
        }



    }


    private void init(){
        ListView dirList = (ListView) findViewById(R.id.dirListView);
        File musidDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Music/");
        if (musidDir == null) {
            musidDir = new File(Environment.getDataDirectory().getPath() + "/Music/");
        }
        File list[] = musidDir.listFiles();

        dirList.setAdapter(new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, list));
        dirList.setOnItemClickListener( (AdapterView<?> parent, View view, int position, long id) -> {
                File selectedFile = (File) (dirList.getItemAtPosition(position));
                Log.d("selected", selectedFile.getAbsolutePath() );
            } );
    }
}
