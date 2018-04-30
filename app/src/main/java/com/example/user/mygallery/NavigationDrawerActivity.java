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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final int REQUEST_PERMISSION_KEY = 1;
    GridView galleryGridView;
    LoadAlbum loadAlbumTask;
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        galleryGridView = (GridView) findViewById(R.id.galleryGridView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //start
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!Function.hasPermission(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadAlbumTask = new LoadAlbum();
                    loadAlbumTask.execute();
                } else {
                    Toast.makeText(NavigationDrawerActivity.this, "you must accept permission", Toast.LENGTH_LONG).show();
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
            loadAlbumTask = new LoadAlbum();
            loadAlbumTask.execute();
        }
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
            String countPhoto = null;

            Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

            Cursor cursorExternal = getContentResolver().query(uriExternal, projection,
                    "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);

            Cursor cursorInternal = getContentResolver().query(uriInternal, projection,
                    "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);

            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});

            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                Log.e("path",path);
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                Log.e("album",album);
                timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                Log.e("timeStamp",timeStamp);
                countPhoto = Function.getCount(getApplicationContext(), album);

                albumList.add(Function.mappingInbox(album, path, timeStamp, Function.convertToTime(timeStamp), countPhoto));

            }
                cursor.close();
                Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "dsc"));
            return xml;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            AlbumAdapter albumAdapter = new AlbumAdapter(NavigationDrawerActivity.this, albumList);
            galleryGridView.setAdapter(albumAdapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(NavigationDrawerActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NavigationDrawerActivity.this, AlbumActivity.class);
                    intent.putExtra("name", albumList.get(+position).get(Function.KEY_ALBUM));
                    Log.e("imagePosition",albumList.get(+position).get(Function.KEY_ALBUM)+"");
                    startActivity(intent);
                }
            });
        }
    }


    class AlbumAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<HashMap<String, String>> data;

        public AlbumAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
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
            AlbumViewHolder albumViewHolder = null;
            if (convertView == null) {
                albumViewHolder = new AlbumViewHolder();
                convertView = LayoutInflater.from(activity).inflate(R.layout.album_row, parent, false);
                albumViewHolder.imageView = (ImageView) convertView.findViewById(R.id.galleryImage);
                albumViewHolder.imageTitle = (TextView) convertView.findViewById(R.id.gallery_title);
                albumViewHolder.imageCount = (TextView) convertView.findViewById(R.id.gallery_count);

                convertView.setTag(albumViewHolder);
            } else {
                albumViewHolder = (AlbumViewHolder) convertView.getTag();
            }
            albumViewHolder.imageView.setId(position);
            albumViewHolder.imageTitle.setId(position);
            albumViewHolder.imageCount.setId(position);

            HashMap<String, String> song = new HashMap<String, String>();
            song = data.get(position);
            albumViewHolder.imageTitle.setText(song.get(Function.KEY_ALBUM));
            albumViewHolder.imageCount.setText(song.get(Function.KEY_COUNT));

            Glide.with(activity)
                    .load(new File(song.get(Function.KEY_PATH)))
                    .into(albumViewHolder.imageView);

            return convertView;
        }
    }


    class AlbumViewHolder {
        ImageView imageView;
        TextView imageCount;
        TextView imageTitle;
    }
}
