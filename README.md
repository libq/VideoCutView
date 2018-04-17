# VideoCutView 视频裁剪view 带帧预览
![image](https://github.com/libq/VideoCutView/blob/master/view.png)

# 1.配置
step 1:  project  build.gradle
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


```
step 2: module build.gradle

```
	dependencies {
	        compile 'com.github.libq:VideoCutView:1.0.0'
	}

```


# 2.使用

## java 

```

cut.getSuitImageCount(new VideoCutView.GetImageCountCallback() {
            @Override
            public void invoke(int count) {
                //本地图片
                String imgPath = "xxx.jpg";
                ArrayList<String> paths = new ArrayList<>();
                for(int i = 0 ; i<=count ;i++){
                    paths.add(imgPath);
                }
                cut.setImageUrls(paths);
            }
        });

       cut.setVideoDuration(10000);
       cut.setCutMinDuration(3000);
       cut.setOnCutBorderScrollListener(new VideoThumbnailView.OnCutBorderScrollListener() {
            @Override
            public void onScrollBorder(float start, float end) {
                Log.e("Main","###### start ="+start+"   end = "+end);
            }
        });
```


## xml
```
 <com.libq.videocutpreview.VideoCutView
        android:id="@+id/cut"
        android:layout_width="match_parent"
        android:layout_height="80dp"
        app:draw_cursor="false"
        app:video_duration="10000"
        app:top_bottom_border_width="10dp"
        app:slider_width="10dp"
        app:drag_area_width="10dp"
        app:cut_min_duration="1000"
        app:border_color="@color/colorAccent"
        />
```

