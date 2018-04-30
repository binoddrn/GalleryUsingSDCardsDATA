package com.example.user.mygallery;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.mygallery.util.Function;
import com.example.user.mygallery.util.MapComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class VideoDetailActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> albumVideoDetail = new ArrayList<HashMap<String, String>>();
    LoadVideoDetail loadVideoDetail;
    String album_name = "";
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        gridView = findViewById(R.id.galleryDetailVideoGridView);

        Intent intent = getIntent();
        album_name = intent.getStringExtra("name");
        setTitle(album_name);


        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            gridView.setColumnWidth(Math.round(px));
        }


        loadVideoDetail = new VideoDetailActivity.LoadVideoDetail();
        loadVideoDetail.execute();
    }


    class LoadVideoDetail extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            albumVideoDetail.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            String xml = "";
            String path = null;
            String album = null;
            String timeStamp = null;
            String countVideo = null;

            Uri uriExternal = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Video.VideoColumns.DATA,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_TAKEN};
//
//            Cursor cursorExternal = getContentResolver().query(uriExternal, projection,
//                    "_data IS NOT NULL) GROUP BY (bucket_display_name",
//                    null, null);
//
//            Cursor cursorInternal = getContentResolver().query(uriInternal, projection,
//                    "_data IS NOT NULL) GROUP BY (bucket_display_name",
//                    null, null);


            Cursor cursorExternal = getContentResolver()
                    .query(uriExternal, projection, "bucket_display_name = \"" + album_name + "\"",
                            null, null);
            Cursor cursorInternal = getContentResolver()
                    .query(uriInternal, projection, "bucket_display_name = \"" + album_name + "\"",
                            null, null);

            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});

            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA));
                Log.e("path", path);
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                Log.e("album", album);
                timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));
                Log.e("timestamp", timeStamp);
                countVideo = Function.getCount(getApplicationContext(), album);
                albumVideoDetail.add(Function.mappingInbox(album, path, timeStamp, Function.convertToTime(timeStamp), null));
            }
            cursor.close();
            Collections.sort(albumVideoDetail, new MapComparator(Function.KEY_TIMESTAMP, "dsc"));


            return xml;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            VideoDetailAdapter videoDetailAdapter = new VideoDetailAdapter(VideoDetailActivity.this, albumVideoDetail);
            gridView.setAdapter(videoDetailAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(VideoDetailActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VideoDetailActivity.this, MediaPlayerActivity.class);
//                    intent.putExtra("path", albumList.get(+position).get(Function.KEY_PATH));
                    ArrayList<String> imagePathList = new ArrayList<>();
                    for (HashMap<String, String> data : albumVideoDetail) {
                        String path = data.get(Function.KEY_PATH);
                        imagePathList.add(path);
                    }
                    intent.putStringArrayListExtra("path", imagePathList);
                    startActivity(intent);
                }
            });
        }
    }


    class VideoDetailAdapter extends BaseAdapter {
        Activity activity;
        private ArrayList<HashMap<String, String>> data;

        public VideoDetailAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
            this.activity = activity;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            VideoDetailViewHolder videoDetailViewHolder = null;
            if (convertView == null) {
                videoDetailViewHolder = new VideoDetailViewHolder();
                convertView = LayoutInflater.from(activity).inflate(R.layout.single_video_row, parent, false);

                videoDetailViewHolder.imageView = (ImageView) convertView.findViewById(R.id.galleryVideoImage);
                convertView.setTag(videoDetailViewHolder);
            } else {
                videoDetailViewHolder = (VideoDetailViewHolder) convertView.getTag();
            }


            videoDetailViewHolder.imageView.setId(position);
            HashMap<String, String> song = new HashMap<String, String>();
            song = data.get(position);

            Glide.with(activity)
                    .load(song.get(Function.KEY_PATH))
                    .into(videoDetailViewHolder.imageView);

            return convertView;
        }
    }


    class VideoDetailViewHolder {
        ImageView imageView;
    }
}
