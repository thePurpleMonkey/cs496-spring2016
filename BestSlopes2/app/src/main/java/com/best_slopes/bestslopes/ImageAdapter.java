package com.best_slopes.bestslopes;

/**
 * Created by Muratcan on 4/20/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
    private ArrayList<Bitmap> imageList;
    private boolean isImageFitToScreen;
    private ViewGroup.LayoutParams defaultParams;
    ImageView fullScreenContainer;

    public ImageAdapter(AppCompatActivity mainActivity) {
        this.context = mainActivity;
        this.imageList = imageList;
        this.defaultParams = new GridView.LayoutParams(375, 375);
        this.imageList = new ArrayList<Bitmap>();
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        for (int i = 1; i < 8; i+=1) {//urls[0]
            String imagePath = baseDir + File.separator +"DCIM/BestSlopes2/IMG_0" + i + ".JPG";
            Bitmap bitmap = pathImageToBitmap(imagePath);
            if (bitmap != null) {
                this.imageList.add(bitmap);
            }
        }
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

    public int getCount() {
        return this.imageList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(defaultParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setClickable(true);
            imageView.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    File file = new File("/sdcard/DCIM/BestSlopes2/IMG_01.JPG");
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

        imageView.setImageBitmap(this.imageList.get(position));
        return imageView;
    }

}