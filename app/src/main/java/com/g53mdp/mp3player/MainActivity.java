package com.g53mdp.mp3player;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.File;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }


    private void init(){

        ListView dirList = (ListView) findViewById(R.id.dirListView);

        File musicDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Music/");

        File list[] = musicDir.listFiles();

        dirList.setAdapter(new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, list));

        dirList.setOnItemClickListener( (AdapterView<?> myAdapter, View v, int myItemInt, long mylng) -> {

            File selectedFromList = (File) (dirList.getItemAtPosition(myItemInt));
            Log.d("selected From File", selectedFromList.getAbsolutePath());

        });
    }
}
