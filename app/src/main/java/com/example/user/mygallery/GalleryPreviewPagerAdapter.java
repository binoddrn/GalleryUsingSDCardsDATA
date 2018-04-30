package com.example.user.mygallery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.mygallery.util.Function;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GalleryPreviewPagerAdapter extends PagerAdapter{
    Context context;
    private ArrayList<String> data;
    LayoutInflater layoutInflater;

    public GalleryPreviewPagerAdapter(Context context,  ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }




    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view= layoutInflater.inflate(R.layout.swipe_layout,container,false);
        ImageView imageView=(ImageView)view.findViewById(R.id.GalleryPreviewImg);

        Glide.with(context)
                   .load(data.get(position))
                   .into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       container.removeView((View) object);
    }
}
