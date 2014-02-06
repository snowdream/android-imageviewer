/*
 * Copyright (C) 2014 Snowdream Mobile <yanghui1986527@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.snowdream.android.apps.imageviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import com.github.snowdream.android.util.Log;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;


public class ImageViewerActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener
        , PhotoViewAttacher.OnViewTapListener {
    private String imageUri = null;
    private List<String> imageUrls = null;
    private String fileName = null;
    private ImageLoader imageLoader = null;
    private ShareActionProvider shareActionProvider = null;
    private int imageMode = 0; //0 multiple, 1,single
    private int imagePosition = 0;
    ViewPager viewPager = null;

    public static class Extra {
        public static final String IMAGES = "com.github.snowdream.android.apps.imageviewer.IMAGES";
        public static final String IMAGE_POSITION = "com.github.snowdream.android.apps.imageviewer.IMAGE_POSITION";
        public static final String IMAGE_MODE = "com.github.snowdream.android.apps.imageviewer.IMAGE_MODE";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initData();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this); // Add this method.
    }

    public void initUI() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOnPageChangeListener(this);
    }

    public void initData() {
        imageLoader = ImageLoader.getInstance();
        imageUrls = new ArrayList<String>();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                imageUrls = bundle.getStringArrayList(Extra.IMAGES);
                imagePosition = bundle.getInt(Extra.IMAGE_POSITION, 0);
                imageMode = bundle.getInt(Extra.IMAGE_MODE, 0);
                Log.i("The snowdream bundle path of the image is: " + imageUri);
            }

            Uri uri = (Uri) intent.getData();
            if (uri != null) {
                imageUri = uri.getPath();
                fileName = uri.getLastPathSegment();
                getSupportActionBar().setSubtitle(fileName);
                Log.i("The path of the image is: " + imageUri);

                File file = new File(imageUri);

                imageUri = "file://" + imageUri;
                File dir = file.getParentFile();
                if (dir != null) {
                    FileFilter fileFilter = new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            if (f != null) {
                                String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.encode(f.getAbsolutePath()));
                                if (!TextUtils.isEmpty(extension)) {
                                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                                    if (!TextUtils.isEmpty(mimeType) && mimeType.contains("image")) {
                                        return true;
                                    }
                                }
                            }
                        return false;
                    }
                } ;

                File[] files = dir.listFiles(fileFilter);

                if (files != null && files.length > 0) {
                    int size = files.length;

                    for (int i = 0; i < size; i++) {
                        imageUrls.add("file://" + files[i].getAbsolutePath());
                    }
                    imagePosition = imageUrls.indexOf(imageUri);
                    imageMode = 1;
                    Log.i("Image Position:" + imagePosition);
                }

            } else {
                imageUrls.add("file://" + imageUri);
                imagePosition = 0;
                imageMode = 0;
            }
        }
    }

    else

    {
        Log.w("The intent is null!");
    }

}

    public void loadData() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(false)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        viewPager.setAdapter(new ImageViewerPagerAdapter(this, imageUrls, options));
        viewPager.setCurrentItem(imagePosition);
    }

    public void onViewTap(View view, float x, float y) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar.isShowing()) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem itemShare = menu.findItem(R.id.action_share);
        // Get the provider and hold onto it to set/change the share intent.
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(itemShare);
        // Set history different from the default before getting the action
        // view since a call to MenuItemCompat.getActionView() calls
        // onCreateActionView() which uses the backing file name. Omit this
        // line if using the default share history file is desired.
        shareActionProvider.setShareHistoryFileName("snowdream_android_imageviewer_share_history.xml");
        Intent shareIntent = createShareIntent();
        if (shareIntent != null) {
            doShare(shareIntent);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_edit) {
            doEdit();
        } else if (id == R.id.action_share) {
            Intent shareIntent = createShareIntent();
            if (shareIntent != null) {
                doShare(shareIntent);
            }
            return true;
        } else if (id == R.id.action_settings) {
            doSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareIntent() {
        Intent shareIntent = null;

        if (!TextUtils.isEmpty(imageUri)) {
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            Uri uri = Uri.parse(imageUri);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        return shareIntent;
    }

    public void doShare(Intent shareIntent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    public void doSettings() {
        if (!TextUtils.isEmpty(imageUri)) {
            Uri uri = Uri.parse(imageUri);
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(uri, "image/jpg");
            intent.putExtra("mimeType", "image/jpg");
            startActivityForResult(Intent.createChooser(intent, getText(R.string.action_settings)), 200);
        }
    }

    public void doEdit() {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i("onPageScrolled");
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("onPageSelected position:" + position);
        if (imageUrls != null && imageUrls.size() > position) {
            imageUri = imageUrls.get(position);

            Uri uri = Uri.parse(imageUri);
            fileName = uri.getLastPathSegment();
            getSupportActionBar().setSubtitle(fileName);
            Log.i("The path of the image is: " + imageUri);
        }


        Intent shareIntent = createShareIntent();
        if (shareIntent != null) {
            doShare(shareIntent);
        }
        Log.i("onPageSelected imageUri:" + imageUri);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.i("onPageScrollStateChanged state:" + state);
    }

}
