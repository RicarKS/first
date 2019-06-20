package com.example.ksmusic_md;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    List<String> mPermissionList = new ArrayList<>();
    private final int mRequestCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        ImageView imageView = (ImageView)findViewById(R.id.splash_image);
        Glide.with(this).load(R.drawable.splash).into(imageView);
        initPermission();
        handler.sendEmptyMessageDelayed(0,3000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            enterHome();
            super.handleMessage(msg);
        }
    };

    public void enterHome(){
        Intent intent = new Intent(SplashActivity.this,MusicListActivity.class);
        startActivity(intent);
        finish();
    }

    private void initPermission(){
        mPermissionList.clear();
        for(int i = 0; i < permissions.length; i++){
            if (ContextCompat.checkSelfPermission(this,permissions[i]) != PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(permissions[i]);
            }
        }
        if(mPermissionList.size() > 0){
            ActivityCompat.requestPermissions(this,permissions,mRequestCode);
        } else {
            MusicUtils.initMusicList();
            MusicUtils.initSongList();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        boolean hasPermissionsDismiss = false;
        if(mRequestCode == requestCode){
            for (int i = 0; i <grantResults.length; i++){
                if (grantResults[i] == -1){
                    hasPermissionsDismiss = true;
                }
                if (hasPermissionsDismiss) {
                    Toast.makeText(SplashActivity.this, "您拒绝了权限，即将退出", Toast.LENGTH_SHORT).show();
                }else{
                    MusicUtils.initMusicList();
                    MusicUtils.initSongList();
                }
            }
        }
    }
}
