package com.libq.videocutpreviewviewdemo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.libq.videocutpreview.VideoCutView;
import com.libq.videocutpreview.VideoThumbnailView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks{

    private VideoCutView cut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         cut = (VideoCutView) findViewById(R.id.cut);

        String[] perms = {Manifest.permission.INTERNET,Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE};


        if (EasyPermissions.hasPermissions(this, perms)) {
            initImage();
        } else {
            // 没有申请过权限，现在去申请
            EasyPermissions.requestPermissions(this, "QUAN XIAN SHEN QING",
                    10, perms);
        }

        //本地文件
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override //申请成功时调用
    public void onPermissionsGranted(int requestCode, List<String> list) {
        //请求成功执行相应的操作
        initImage();


    }

    @Override //申请失败时调用
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // 请求失败，执行相应操作

    }
    private void initImage(){
       cut.getSuitImageCount(new VideoCutView.GetImageCountCallback() {
            @Override
            public void invoke(int count) {
                String root =Environment.getExternalStorageDirectory().getAbsolutePath();
                //本地图片

                String imgPath = root+ File.separator +"Pictures/03 演示图片.jpg";
                ArrayList<String> paths = new ArrayList<>();
                for(int i = 0 ; i<=count ;i++){
                    paths.add(imgPath);
                    //"http://p3.wmpic.me/article/2017/11/08/1510105952_KopFLXPj.jpg"
                }
                cut.setImageUrls(paths, new VideoCutView.ImageLoadStrategyLinstener() {
                    @Override
                    public void onLoad(ArrayList<String> urls, ArrayList<ImageView> ivs) {
                        for(int i=0;i<urls.size();i++){
                            File file = new File(urls.get(i));
                            Glide.with(MainActivity.this)
                                    .load(file)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(ivs.get(i));
                        }
                    }
                });
            }
        });

       cut.setVideoDuration(10000);
       cut.setCutMinDuration(3000);
       cut.setOnVideoPlayIntervalChangeListener(new VideoCutView.OnVideoPlayIntervalChangeListener() {
           @Override
           public void onChange(int startTime, int endTime) {
               Log.e("Main","###### start ="+startTime+"   end = "+endTime);
           }
       });


    }


}
