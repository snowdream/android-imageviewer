package com.github.snowdream.android.apps.imageviewer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.github.snowdream.android.util.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        view = LayoutInflater.from(context).inflate(R.layout.viewpager_item, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

//        if (imageUri.endsWith("gif")) {
//            try {
//                GifDrawable gifDrawable =  new GifDrawable( "/path/anim.gif" );
//                imageView.setImageDrawable(gifDrawable);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }else
        {
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            final PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);

            imageLoader.displayImage(imageUri, imageView, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            Log.i("onLoadingStarted");
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
                            if (imageUri.endsWith("gif")) {
                                try {
                                    Uri uri = Uri.parse(imageUri);
                                    GifDrawable gifDrawable = new GifDrawable(uri.getPath());
                                    imageView.setImageDrawable(gifDrawable);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (imageUri.endsWith("svg")) {
                                imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                try {
                                    Uri uri = Uri.parse(imageUri);
                                    File svgFile = new File(uri.getPath());

                                    SVG svg = SVG.getFromInputStream(new FileInputStream(svgFile));
                                    Drawable svgDrawable = new PictureDrawable(svg.renderToPicture());
                                    imageView.setImageDrawable(svgDrawable);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (SVGParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            attacher.update();
                            progressBar.setVisibility(View.GONE);
                        }

                        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            Log.i("onLoadingComplete");
                            if (imageUri.endsWith("gif")) {
                                try {
                                    Uri uri = Uri.parse(imageUri);
                                    GifDrawable gifDrawable = new GifDrawable(uri.getPath());
                                    imageView.setImageDrawable(gifDrawable);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (imageUri.endsWith("svg")) {
                                imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                try {
                                    Uri uri = Uri.parse(imageUri);
                                    File svgFile = new File(uri.getPath());

                                    SVG svg = SVG.getFromInputStream(new FileInputStream(svgFile));
                                    Drawable svgDrawable = new PictureDrawable(svg.renderToPicture());
                                    imageView.setImageDrawable(svgDrawable);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (SVGParseException e) {
                                    e.printStackTrace();
                                }
                            }
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
