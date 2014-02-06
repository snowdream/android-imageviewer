package com.github.snowdream.android.apps.imageviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.github.snowdream.android.util.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.List;

/**
 * Created by snowdream on 2/5/14.
 */
public class ImageViewerPagerAdapter extends PagerAdapter {
    private DisplayImageOptions options = null;
    private List<String> list = null;
    private ImageLoader imageLoader = null;
    private Context context = null;

    private ImageViewerPagerAdapter() {
    }

    ImageViewerPagerAdapter(Context context, List<String> list, DisplayImageOptions options) {
        this.list = list;
        this.options = options;
        this.context = context;
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }

        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String imageUri = list.get(position);
        View view = null;
        /*if (imageUri.endsWith("gif")) {

        }else if(imageUri.endsWith("svg")){

        } else*/ {
            view = LayoutInflater.from(context).inflate(R.layout.viewpager_item, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            final PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);

            imageLoader.displayImage(imageUri, imageView, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            Log.i("onLoadingStarted");
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "Input/Output error";
                                    break;
                                case DECODING_ERROR:
                                    message = "Image can't be decoded";
                                    break;
                                case NETWORK_DENIED:
                                    message = "Downloads are denied";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "Out Of Memory error";
                                    break;
                                case UNKNOWN:
                                    message = "Unknown error";
                                    break;
                            }
                            Log.e("onLoadingFailed:" + message);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            Log.i("onLoadingComplete");
                            attacher.update();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            Log.w("onLoadingCancelled");
                            progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            Log.i("onProgressUpdate " + current + "/" + "total");
                        }
                    }
            );
            container.addView(view);
        }
        return view;
    }
}
