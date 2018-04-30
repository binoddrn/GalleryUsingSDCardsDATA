package com.example.user.mygallery;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class SingleVideoAdapter extends PagerAdapter {
    LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<String> data;
    private MediaController mediaController;


    public SingleVideoAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.video_swipe_layout, container, false);
        // ImageView imageView=(ImageView)view.findViewById(R.id.GalleryPreviewImg);

        VideoView videoView = (VideoView) view.findViewById(R.id.simpleVideoView);


        if (mediaController == null) {
            mediaController = new MediaController(context);
            mediaController.setAnchorView(videoView);
        }
        videoView.setVideoURI(Uri.parse(data.get(position)));

        Log.e("videoUrl", Uri.parse(data.get(position)) + "");


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(context, "Thank You...!!!", Toast.LENGTH_LONG).show();
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(context, "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show();
                return false;
            }
        });


        container.addView(view);

        return view;

    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }
}
