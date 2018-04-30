package com.example.user.mygallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnDrawerActivity;
    private Button btnVideoActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDrawerActivity=findViewById(R.id.btnGoToNavigationDrawer);
        btnVideoActivity=findViewById(R.id.btnGoToVideoActivity);
        btnDrawerActivity.setOnClickListener(this);
        btnVideoActivity.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        if (id == R.id.btnGoToNavigationDrawer){
            Intent intent=new Intent(this,NavigationDrawerActivity.class);
            startActivity(intent);
        }

        if (id == R.id.btnGoToVideoActivity){
            Intent intent=new Intent(this,VideoListActivity.class);
            startActivity(intent);
        }
    }
}
