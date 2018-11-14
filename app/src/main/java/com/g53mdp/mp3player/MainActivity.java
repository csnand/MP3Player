package com.g53mdp.mp3player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;



public class MainActivity extends AppCompatActivity {

    private PlayService pService;
    private boolean isBound = false;

    private ServiceConnection sConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.MyBinder mBinder = (PlayService.MyBinder) service;
            pService = mBinder.getService();
            isBound = true;

            Toast.makeText(MainActivity.this, "Play Service Bounded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            pService = null;
            Toast.makeText(MainActivity.this, "Play Service UnBounded", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void onPlayButtonClick(View view) {

        Bundle extras = new Bundle();
        extras.putBoolean("play", true);
        if (!isBound){
            Intent in = new Intent(MainActivity.this, PlayService.class);
            in.putExtras(extras);
            startForegroundService(in);
            bindService(in, sConnection, Context.BIND_AUTO_CREATE);
        } else {

        }

    }

    public void onPauseButtonClick(View view) {

        Bundle extras = new Bundle();
        extras.putBoolean("pause", true);
        if (!isBound){
            Intent in = new Intent(MainActivity.this, PlayService.class);
            in.putExtras(extras);
            startForegroundService(in);
            bindService(in, sConnection, Context.BIND_AUTO_CREATE);
        } else {

        }
    }

    public void onStopButtonClick(View view) {

        Bundle extras = new Bundle();
        extras.putBoolean("stop", true);
        if (!isBound){
            Intent in = new Intent(MainActivity.this, PlayService.class);
            in.putExtras(extras);
            startForegroundService(in);
            bindService(in, sConnection, Context.BIND_AUTO_CREATE);
        } else {

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

                Bundle extras = new Bundle();
                extras.putString("filePath", selectedFile.getAbsolutePath());

                if (!isBound){
                    Intent in = new Intent(MainActivity.this, PlayService.class);
                    in.putExtras(extras);
                    startForegroundService(in);
                    bindService(in, sConnection, Context.BIND_AUTO_CREATE);
                }

            } );

        initButtons();

    }


    private void initButtons(){

        Button playPause = (Button) findViewById (R.id.playButton);
        playPause.setBackgroundResource(R.drawable.play);

        Button pause = (Button) findViewById (R.id.pauseButton);
        pause.setBackgroundResource(R.drawable.pause);

        Button stop = (Button) findViewById (R.id.stopButton);
        stop.setBackgroundResource(R.drawable.stop);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("isBound", isBound);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        isBound = savedInstanceState.getBoolean("isBound");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {

        if (isBound){
            unbindService(sConnection);
            sConnection = null;
            isBound = false;
        }

        super.onDestroy();
    }
}
