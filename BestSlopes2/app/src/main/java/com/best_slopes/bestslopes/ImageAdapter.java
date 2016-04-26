package com.best_slopes.bestslopes;

/**
 * Created by Muratcan a.k.a Jhon on 4/20/2016.
 *
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
        this.context = mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loadImages(trail);
    }

    private void loadImages(Trail trail) {
        if(baseDir == null)
            baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!trail.isNew()) {
            this.imagePaths = new ArrayList<>(trail.getImagePaths());
            getDebuggingImages(); // JUST FOR DEBUGGING
        }
        else {
            this.imagePaths = new ArrayList<>();
        }
        this.imagePaths.add(ADD_IMAGE_ICON);
    }

    private void getDebuggingImages() {
        this.imagePaths = new ArrayList<>();
        for (int i = 1; i < 8; i += 1) {
            imagePaths.add("DCIM/BestSlopes2/IMG_0" + i + ".JPG");
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
                File f= new File(baseDir + File.separator + this.imagePath);
                if(f.exists()) {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    result = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
                    result = getRoundedCornerBitmap(result, 34);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
        private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
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