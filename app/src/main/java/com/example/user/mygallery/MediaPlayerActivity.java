package com.example.user.mygallery;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.user.mygallery.util.Function;

import java.io.File;
import java.util.ArrayList;

public class MediaPlayerActivity extends AppCompatActivity {
    VideoView videoView;
    MediaController mediaController;
    RecyclerView recyclerView;
    private ArrayList<String> data;
    ViewPager viewPager;
    SingleVideoAdapter singleVideoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);



        Intent intent = getIntent();
        data = intent.getStringArrayListExtra("path");


        videoView = (VideoView) findViewById(R.id.simpleVideoView);
        viewPager=(ViewPager) findViewById(R.id.view_pager);



        singleVideoAdapter=new SingleVideoAdapter(this,data);
        viewPager.setAdapter(singleVideoAdapter);

        // recyclerView=(RecyclerView)findViewById(R.id.recyclerView_video);







//        videoView.setMediaController(mediaController);
//        for (int i = 0; i < data.size(); i++) {
//            videoView.setVideoURI(Uri.parse(data.get(i)));
//            videoView.start();
//        }





    }
}
