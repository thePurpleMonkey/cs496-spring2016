package com.best_slopes.bestslopes;

/**
 * Created by Muratcan a.k.a Jhon on 4/20/2016.
 *
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
    private ArrayList<String> imageViews;
    public ImageAdapter(AppCompatActivity mainActivity, Trail trail) {
        this.context = mainActivity;
        getImageViews(trail.getImagePaths());
    }

    private void getImageViews(ArrayList<String> imagePaths) {
        imageViews = new ArrayList<>(imagePaths);
        imageViews.add("ADD_IMAGE_ICON");
//        for (String imagePath : imagePaths) {
//            imageViews.add(new MyImageView(imagePath).getImageView());
//        }
//        imageViews.add(new MyImageView("ADD_IMAGE_ICON").getImageView());
    }
    public int getCount() {
        return this.imageViews.size();
    }

    public Object getItem(int position) {
        return this.imageViews.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {
            imageView = new MyImageView(imageViews.get(position)).getImageView();
        } else {
            imageView = (ImageView) convertView;
        }
        return imageView;
    }

    private class MyImageView {
        private ViewGroup.LayoutParams defaultParams;
        private ImageView imageView;
        private String imagePath;
        private boolean isIcon;

        public MyImageView(final String imagePath) {
            this.defaultParams = new GridView.LayoutParams(375, 375);
            this.imagePath = imagePath;
            this.isIcon = imagePath.equals("ADD_IMAGE_ICON");
            this.imageView = new ImageView(context);
            this.imageView.setLayoutParams(defaultParams);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.imageView.setClickable(true);
            chooseBitmap();
            setOnClickListener();

        }

        private void chooseBitmap() {
            if (this.isIcon) {
                Bitmap addImageIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.add_image);
                this.imageView.setImageBitmap(addImageIcon);
            }
            else {
                this.imageView.setImageBitmap(getBitmapFromPath());
            }

        }

        private Bitmap getBitmapFromPath(){
            Bitmap result = null;
            try {
                File f= new File(this.imagePath);
                if(f.exists()) {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    result = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        private void setOnClickListener() {
            this.imageView.setOnClickListener(new View.OnClickListener() {
                //@Override
                public void onClick(View v) {
                    if (isIcon) {
                        pickImage();
                    }
                    else {
                        openImage();
                    }
                }
            });
        }

        void openImage() {
            final File file = new File(this.imagePath);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "image/*");
                context.startActivity(intent);
            }
        }

        private void pickImage() {
            // TODO: Open camera or file browser to add image
        }

        public ImageView getImageView() { return this.imageView; }
    }

}