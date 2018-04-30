package com.example.user.mygallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.mygallery.util.Function;
import com.example.user.mygallery.util.MapComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class VideoListActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_KEY = 3;
    GridView galleryVideoGridView;
    LoadVideoAlbum loadVideoAlbum;

    ArrayList<HashMap<String, String>> albumVideoList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        galleryVideoGridView = (GridView) findViewById(R.id.galleryVideoGridView);


        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            galleryVideoGridView.setColumnWidth(Math.round(px));
        }


        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!Function.hasPermission(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadVideoAlbum = new VideoListActivity.LoadVideoAlbum();
                    loadVideoAlbum.execute();
                } else {
                    Toast.makeText(VideoListActivity.this, "you must accept permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        String[] PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!Function.hasPermission(this, PERMISSION)) {
            ActivityCompat.requestPermissions(this, PERMISSION, REQUEST_PERMISSION_KEY);
        } else {
            loadVideoAlbum = new VideoListActivity.LoadVideoAlbum();
            loadVideoAlbum.execute();
        }
    }

    class LoadVideoAlbum extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumVideoList.clear();
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

//            String[] projection = {MediaStore.Video.Media.DATA,
//                    MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATE_TAKEN};
            //MediaStore.Video.Media.DATA

            String[] projection = {MediaStore.Video.VideoColumns.DATA,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATE_TAKEN};

            Cursor cursorExternal = getContentResolver().query(uriExternal, projection,
                    "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            if (cursorExternal!=null)
            Log.e("cursorExternal",cursorExternal.toString());

            Cursor cursorInternal = getContentResolver().query(uriInternal, projection,
                    "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);

            if (cursorInternal!=null)
                Log.e("cursorExternal",cursorInternal.toString());

            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});

            while (cursor.moveToNext()) {
//                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
//                Log.e("path", path);
//                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
//                Log.e("album", album);
//                timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
//                Log.e("timestamp", timeStamp);
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA));
                Log.e("path", path);
                album = cursor.getString(cursor.getColumnIndexOrThrow(    MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                Log.e("album", album);
                timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));
                Log.e("timestamp", timeStamp);
                countVideo = Function.getVideoCount(getApplicationContext(), album);
                albumVideoList.add(Function.mappingInbox(album, path, timeStamp, Function.convertToTime(timeStamp), countVideo));
            }

            cursor.close();

            Collections.sort(albumVideoList, new MapComparator(Function.KEY_TIMESTAMP, "dsc"));
            return xml;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            VideoAlbumAdapter videoAlbumAdapter = new VideoAlbumAdapter(VideoListActivity.this, albumVideoList);
            galleryVideoGridView.setAdapter(videoAlbumAdapter);
            galleryVideoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(VideoListActivity.this, VideoDetailActivity.class);
                    intent.putExtra("name", albumVideoList.get(+position).get(Function.KEY_ALBUM));
                    Log.e("position",albumVideoList.get(+position).get(Function.KEY_ALBUM)+"");
                    startActivity(intent);
                }
            });
        }


        class VideoAlbumAdapter extends BaseAdapter {
            Activity activity;
            private ArrayList<HashMap<String, String>> data;

            public VideoAlbumAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
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
                VideoAlbumViewHolder videoAlbumViewHolder = null;
                if (convertView == null) {
                    videoAlbumViewHolder = new VideoAlbumViewHolder();
                    convertView = LayoutInflater.from(activity).inflate(R.layout.video_album_row, parent, false);
                    videoAlbumViewHolder.videoImageView = (ImageView) convertView.findViewById(R.id.galleryImage);
                    videoAlbumViewHolder.imageTitle = (TextView) convertView.findViewById(R.id.gallery_title);
                    videoAlbumViewHolder.imageCount = (TextView) convertView.findViewById(R.id.gallery_count);

                    convertView.setTag(videoAlbumViewHolder);
                } else {
                    videoAlbumViewHolder = (VideoAlbumViewHolder) convertView.getTag();
                }

                videoAlbumViewHolder.videoImageView.setId(position);
                videoAlbumViewHolder.imageTitle.setId(position);
                videoAlbumViewHolder.imageCount.setId(position);

                HashMap<String, String> song = new HashMap<String, String>();
                song = data.get(position);
                videoAlbumViewHolder.imageTitle.setText(song.get(Function.KEY_ALBUM));
                videoAlbumViewHolder.imageCount.setText(song.get(Function.KEY_COUNT));

                Glide.with(activity)
                        .load(new File(song.get(Function.KEY_PATH)))
                        .into(videoAlbumViewHolder.videoImageView);
                return convertView;
            }

        }

        class VideoAlbumViewHolder {
            ImageView videoImageView;
            TextView imageCount;
            TextView imageTitle;
        }
    }
}
