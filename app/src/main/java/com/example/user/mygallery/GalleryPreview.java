package com.example.user.mygallery;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class GalleryPreview extends AppCompatActivity {

    ImageView GalleryPreviewImg;

    ViewPager viewPager;
    GalleryPreviewPagerAdapter galleryPreviewPagerAdapter;
    int pos;
    private ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_preview);


        Intent intent = getIntent();
        data = intent.getStringArrayListExtra("path");
        Log.e("data", data.size() + "");


        int position = intent.getIntExtra("position", 0);
        Log.e("positionIntent", position + "");
        //  String position = intent.getExtras().getString("position");
        //  Log.e("positionString",position);

        //    pos = Integer.valueOf(position);
        // Log.e("positionInt",pos+"");


        GalleryPreviewImg = (ImageView) findViewById(R.id.GalleryPreviewImg);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        galleryPreviewPagerAdapter = new GalleryPreviewPagerAdapter(this, data);
        viewPager.setAdapter(galleryPreviewPagerAdapter);
        viewPager.setCurrentItem(position);
        setGalleryTitle(position + 1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.e("viewpagerPosition", position + "");
                setGalleryTitle(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


//        Glide.with(GalleryPreview.this)
//                .load(new File(path))
//                .into(GalleryPreviewImg);
    }


    public void setGalleryTitle(int position) {
        setTitle(String.format(Locale.getDefault(), "%d / %d", position, galleryPreviewPagerAdapter.getCount()));
    }
}
