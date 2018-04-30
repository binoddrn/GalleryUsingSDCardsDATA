package com.example.user.mygallery;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
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

import com.bumptech.glide.Glide;
import com.example.user.mygallery.util.Function;
import com.example.user.mygallery.util.MapComparator;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.jsibbold.zoomage.AutoResetMode.UNDER;

public class AlbumActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    String album_name = "";
    private GridView galleryGridView;
    private LoadAlbum loadAlbumTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);


        Intent intent = getIntent();
        album_name = intent.getStringExtra("name");
        setTitle(album_name);


        galleryGridView = (GridView) findViewById(R.id.galleryGridView);


        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        loadAlbumTask = new LoadAlbum();
        loadAlbumTask.execute();

    }

    class LoadAlbum extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            String xml = "";
            String path = null;
            String album = null;
            String timeStamp = null;
            String photoCount = null;
            Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.MediaColumns.DATE_MODIFIED};

//            Cursor cursorExternal = getContentResolver()
//                    .query(uriExternal, projection,
//                            "_data IS NOT NULL)GROUP BY(bucket_display_name", null, null);
//
//            Cursor cursorInternal = getContentResolver()
//                    .query(uriInternal, projection,
//                            "_data IS NOT NULL)GROUP BY(bucket_display_name", null, null);

            Cursor cursorExternal = getContentResolver()
                    .query(uriExternal, projection, "bucket_display_name = \"" + album_name + "\"",
                            null, null);
            Cursor cursorInternal = getContentResolver()
                    .query(uriInternal, projection, "bucket_display_name = \"" + album_name + "\"",
                            null, null);

            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});
            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                albumList.add(Function.mappingInbox(album, path, timeStamp, Function.convertToTime(timeStamp), null));
            }

            cursor.close();
            Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "dsc"));


            return xml;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            SingleAlbumAdapter singleAlbumAdapter = new SingleAlbumAdapter(AlbumActivity.this, albumList);
            galleryGridView.setAdapter(singleAlbumAdapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(AlbumActivity.this, GalleryPreview.class);
//                    intent.putExtra("path", albumList.get(+position).get(Function.KEY_PATH));
                    ArrayList<String> imagePathList = new ArrayList<>();

                    for (HashMap<String, String> data : albumList) {
                        String path = data.get(Function.KEY_PATH);
//                        String pos=data.get(position);
//                        Log.e("pos",pos);
                        imagePathList.add(path);
                       // imagePathList.add(pos);
                    }
                    intent.putStringArrayListExtra("path", imagePathList);

                    Log.e("position",position+"");
                    intent.putExtra("position",position);
//                    intent.putExtra("position",pos);
                    startActivity(intent);

//
//                    GalleryPreviewFragment galleryPreviewFragment=GalleryPreviewFragment.newInstance(albumList.get(+position).get(Function.KEY_PATH));
//
//
//                  //  ((AppCompatActivity) getBaseContext()).getSupportFragmentManager().beginTransaction()
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.frameLayout, galleryPreviewFragment,"galleryPreviewFragment")
//                            .addToBackStack("directionFragment")
//                            .commit();
//
////                    TestFragment testFragment=new TestFragment();
////                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
////                    transaction.commit();
                }
            });
        }
    }

    class SingleAlbumAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<HashMap<String, String>> data;

        public SingleAlbumAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
            this.activity = activity;
            this.data = data;
        }
//
//        @NonNull
//        @Override
     //   public Object instantiateItem(@NonNull ViewGroup container, int position) {
           // ZoomageView imageView = new ZoomageView(container.getContext());
//            imageView.setRestrictBounds(false);
//            imageView.setAnimateOnReset(true);
           // imageView.setAutoResetMode(UNDER);
//            imageView.setAutoCenter(true);
//            imageView.setZoomable(true);
//            imageView.setTranslatable(true);
//            imageView.setScaleRange(0.6f, 8f);
//
//            Glide.with(activity)
//                    .load(new File(data.get(position).get(Function.KEY_PATH)))
//                    .into(imageView);
//            return imageView;
    //   }

        @Override
        public int getCount() {
            return data.size();
        }

//        @Override
//        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//            return view == object;
//        }


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
            SingleAlbumViewHolder singleAlbumViewHolder = null;
            if (convertView == null) {
                singleAlbumViewHolder = new SingleAlbumViewHolder();
                convertView = LayoutInflater.from(activity).inflate(R.layout.single_album_row, parent, false);

                singleAlbumViewHolder.imageView = (ImageView) convertView.findViewById(R.id.galleryImage);
                convertView.setTag(singleAlbumViewHolder);
            } else {
                singleAlbumViewHolder = (SingleAlbumViewHolder) convertView.getTag();

            }

            singleAlbumViewHolder.imageView.setId(position);
            HashMap<String, String> song = new HashMap<String, String>();
            song = data.get(position);
            Glide.with(activity)
                    .load(new File(song.get(Function.KEY_PATH)))
                    .into(singleAlbumViewHolder.imageView);


            return convertView;
        }
    }


    class SingleAlbumViewHolder {
        ImageView imageView;
    }
}
