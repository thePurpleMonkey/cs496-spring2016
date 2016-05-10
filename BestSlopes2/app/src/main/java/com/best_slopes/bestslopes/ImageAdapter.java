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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ImageAdapter extends AdapterForClickables {
    private static String baseDir;
    private Context context;
    private static LayoutInflater inflater;
    private Trail trail;
    private ImageViewHolder currentHolder;

    public ImageAdapter(AppCompatActivity mainActivity, Trail trail, String paths[], int num_images) {
        this.context = mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.trail = trail;
        getDebuggingImages(paths, num_images);

            //loadImages();
    }

    private void loadImages() {
        if(baseDir == null)
            baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (trail.isNew()) {
            //getDebuggingImages();
        }
    }

    public void getDebuggingImages(String paths[], int num_images) {
        for (int i = 0; i < num_images; ++i) {
            trail.addImagePath(paths[i]);
        }
    }
    public int getCount() {
        return this.trail.getImagePaths().size()+1;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageViewHolder holder = new ImageViewHolder();
        View cellView;
        cellView = inflater.inflate(R.layout.image_view_for_grid, null);
        holder.imageView = (ImageView) cellView.findViewById(R.id.imageViewForGrid);
        holder.setImageView(position);
        currentHolder = holder;
        return cellView;
    }

    private class ImageViewHolder {
        private int position;
        private ImageView imageView;
        private String imagePath;
        private boolean isIcon;

        public void setImageView(int position) {
            ImageAdapter adapter = ImageAdapter.this;
            if (position >= adapter.getCount()-1) {
                this.isIcon = true;
            }
            else {
                this.imageView.setOnLongClickListener(getOnLongClickListenerToDelete(context, ImageAdapter.this, position));
                this.imagePath = adapter.trail.getImagePaths().get(position);
            }
            chooseBitmap();
            this.position = position;
            this.imageView.setClickable(true);
            this.imageView.setOnClickListener(getOnClickListener());
            this.imageView.setOnLongClickListener(getOnLongClickListenerToDelete(context, ImageAdapter.this, position));
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
    }

    void openImage() {
        final File file = new File(baseDir + File.separator + currentHolder.imagePath);
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

    @Override
    public void onClickListener(View v) {
        if (currentHolder.isIcon) {
            pickImage();
        }
        else {
            openImage();
        }
    }

    @Override
    public boolean onPositiveButtonOnLongClick(final Context context, final AdapterForClickables adapter, final int position) { return false; }

    @Override
    public void onPositiveButtonOnLongClickToDelete(Context context, AdapterForClickables adapter, int position) {
        trail.removeImagePath(position);
        new DatabaseContract.UpdateTrailTask(context).execute(trail);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditorActionListener(TextView v, int actionId, KeyEvent event) { }
}