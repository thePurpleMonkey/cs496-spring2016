package com.best_slopes.bestslopes;

/**
 * Created by Muratcan a.k.a Jhon on 4/20/2016.
 *
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    static final private String ADD_IMAGE_ICON = "#$%#$%#$%";
    private ArrayList<String> imagePaths;
    private static String baseDir;
    private Context context;
    private static LayoutInflater inflater=null;

    public ImageAdapter(AppCompatActivity mainActivity, Trail trail) {
        if(baseDir == null)
            baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.context = mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imagePaths = new ArrayList<>(trail.getImagePaths());
        getDebuggingImages(); // JUST FOR DEBUGGING
        this.imagePaths.add(ADD_IMAGE_ICON);
    }

    private void getDebuggingImages() {
        this.imagePaths = new ArrayList<>();
        for (int i = 1; i < 8; i += 1) {
            imagePaths.add(baseDir + File.separator + "DCIM/BestSlopes2/IMG_0" + i + ".JPG");
        }
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
        ImageViewHolder holder = new ImageViewHolder();
        View cellView;
        cellView = inflater.inflate(R.layout.image_view_for_grid, null);
        holder.imageView = (ImageView) cellView.findViewById(R.id.imageViewForGrid);
        holder.setImageView(this.imagePaths.get(position));
        return cellView;
    }

    private class ImageViewHolder {
        private ViewGroup.LayoutParams defaultParams;
        private ImageView imageView;
        private String imagePath;
        private boolean isIcon;

        public void setImageView(final String imagePath) {
            this.imagePath = imagePath;
            this.isIcon = imagePath.equals(ADD_IMAGE_ICON);
            chooseBitmap();
            this.imageView.setClickable(true);
            setOnClickListener();

        }

        private void chooseBitmap() {
            if (this.isIcon) {
                Bitmap addImageIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.add_image);
                this.imageView.setImageBitmap(addImageIcon);
                this.imageView.setBackgroundColor(Color.TRANSPARENT);
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