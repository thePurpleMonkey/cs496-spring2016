package com.best_slopes.bestslopes;

/**
 * Created by Muratcan on 4/20/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> imagePaths;
    private ViewGroup.LayoutParams defaultParams;
    public ImageAdapter(AppCompatActivity mainActivity, Trail trail) {
        this.context = mainActivity;
        this.imagePaths = trail.getImagePaths();
        this.defaultParams = new GridView.LayoutParams(375, 375);
    }

    public int getCount() {
        return this.imagePaths.size();
    }

    public Object getItem(int position) {
        return this.imagePaths.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        final File file = new File(imagePaths.get(position));
        if (!file.exists()) {
            return null;
        }
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(defaultParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setClickable(true);
            imageView.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setDataAndType(uri, "image/*");
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            imageView = (ImageView) convertView;
        }
        if (position < imagePaths.size()) {
            imageView.setImageBitmap(pathImageToBitmap(imagePaths.get(position)));
        }
        else {
            imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.add_image));
        }
        return imageView;
    }

    private Bitmap pathImageToBitmap(String imagePath){
        Bitmap result = null;
        try {
            File f= new File(imagePath);
            boolean a = f.exists();
            if(f.exists()) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                result = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}