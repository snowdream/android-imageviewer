package com.github.snowdream.android.apps.imageviewer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.snowdream.android.util.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;

import uk.co.senab.photoview.PhotoViewAttacher;


public class MainActivity extends ActionBarActivity {
    private ImageView imageView = null;
    private String imageUri = null;
    private ImageLoader imageLoader = null;
    private PhotoViewAttacher attacher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initData();
        loadData();
    }

    public void initUI() {
        imageView = (ImageView) findViewById(R.id.imageView);
        attacher = new PhotoViewAttacher(imageView);
    }

    public void initData() {
        imageLoader = ImageLoader.getInstance();

        imageUri = "http://www.zhuti.org/uploads/allimg/1103/1-11031GK1320-L.jpg";
    }

    public void loadData() {
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
//                .showImageOnFail(R.drawable.ic_error) // resource or drawable
//                .resetViewBeforeLoading(false)  // default
//                .delayBeforeLoading(1000)
//                .cacheInMemory(false) // default
//                .cacheOnDisc(false) // default
//                .preProcessor(...)
//        .postProcessor(...)
//        .extraForDownloader(...)
//        .considerExifParams(false) // default
//                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
//                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
//                .decodingOptions(...)
//        .displayer(new SimpleBitmapDisplayer()) // default
//                .handler(new Handler()) // default
//                .build();
        DisplayImageOptions options = new DisplayImageOptions.Builder().build();
        imageLoader.displayImage(imageUri, imageView, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        Log.i("onLoadingStarted");
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        Log.e("onLoadingFailed");
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Log.i("onLoadingComplete");
                        attacher.update();
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        Log.w("onLoadingCancelled");
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        Log.i("onProgressUpdate " + current + "/" + "total");
                    }
                }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
