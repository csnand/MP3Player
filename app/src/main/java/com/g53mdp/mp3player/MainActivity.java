package com.g53mdp.mp3player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;



public class MainActivity extends AppCompatActivity {


    private Messenger messenger;
    private Messenger replayMessenger;


    private class replayHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PlayService.GETPROGRESS:
                    int progress = msg.arg1;
                    int duration = msg.arg2;

                    TextView trackProgress = (TextView)findViewById(R.id.trackProgress);
                    trackProgress.setText(Integer.toString(progress) + "/" + Integer.toString(duration));
                    Log.d("progress in replymessage", Integer.toString(progress));

                    String filePath = msg.getData().getString("filePath");
                    if (filePath != null){
                        TextView trackName = (TextView) findViewById(R.id.trackName);
                        trackName.setText(filePath);
                        Log.d("filePath in replymessage", filePath);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private ServiceConnection sConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);

            Toast.makeText(MainActivity.this, "Play Service Connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
            Toast.makeText(MainActivity.this, "Play Service UnConnected", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        replayMessenger = new Messenger(new replayHandler());

        setContentView(R.layout.activity_main);
        init();

        updateProgressThread.run();
    }


    private Handler h = new Handler();
    private Runnable updateProgressThread = new Runnable() {
        @Override
        public void run() {

            Message msg= Message.obtain(null, PlayService.GETPROGRESS, 0, 0);
            msg.replyTo = replayMessenger;

            if (messenger != null) {
                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            h.postDelayed(this, 1000);
        }
    };


    public void onPlayButtonClick(View view) {

        if (messenger == null){
            return;
        }

        Message msg = Message.obtain(null, PlayService.PLAY, 0, 0);
        msg.replyTo = replayMessenger;

        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onPauseButtonClick(View view) {

        if (messenger == null){
            return;
        }

        Message msg = Message.obtain(null, PlayService.PAUSE, 0, 0);

        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onStopButtonClick(View view) {

        if (messenger == null){
            return;
        }

        Message msg = Message.obtain(null, PlayService.STOP, 0, 0);

        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        TextView trackName = (TextView) findViewById(R.id.trackName);
        trackName.setText("Stopped");

        TextView trackProgress = (TextView) findViewById(R.id.trackProgress);
        trackProgress.setText("0/0");
    }



    private void init(){

        if (messenger == null){
                    Intent in = new Intent(MainActivity.this, PlayService.class);
                    startService(in);
                    bindService(in, sConnection, Context.BIND_AUTO_CREATE);
                }

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

                TextView trackName = (TextView) findViewById(R.id.trackName);
                trackName.setText(selectedFile.getName());

                Bundle extras = new Bundle();
                extras.putString("filePath", selectedFile.getAbsolutePath());

                    Message msg = Message.obtain(null, PlayService.STARTNEWPLAY, 0, 0);
                    msg.setData(extras);

                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

            } );

        initButtons();

    }


    private void initButtons(){

        TextView trackName = (TextView) findViewById(R.id.trackName);
        trackName.setText("No Track Played");

        TextView trackProgress = (TextView) findViewById(R.id.trackProgress);
        trackProgress.setText("0/0");

        Button playPause = (Button) findViewById (R.id.playButton);
        playPause.setBackgroundResource(R.drawable.play);

        Button pause = (Button) findViewById (R.id.pauseButton);
        pause.setBackgroundResource(R.drawable.pause);

        Button stop = (Button) findViewById (R.id.stopButton);
        stop.setBackgroundResource(R.drawable.stop);

    }

    @Override
    protected void onDestroy() {

        if (messenger != null){
            unbindService(sConnection);
            sConnection = null;
        }

        h.removeCallbacks(updateProgressThread);

        super.onDestroy();
    }
}
