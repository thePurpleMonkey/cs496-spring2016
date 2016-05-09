package com.best_slopes.bestslopes;

/**
 * Created by Muratcan a.k.a Jhon on 4/20/2016.
 *
 */

import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

public class ImageAdapter extends NewAdapter {
    private static String baseDir;
    private Context context;
    private static LayoutInflater inflater=null;
    private Trail trail;

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
         //   getDebuggingImages();
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

    public Object getItem(int position) {
        return this.trail.getImagePaths().get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageViewHolder holder = new ImageViewHolder();
        View cellView;
        cellView = inflater.inflate(R.layout.image_view_for_grid, null);
        holder.imageView = (ImageView) cellView.findViewById(R.id.imageViewForGrid);
        holder.setImageView(position);
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
                this.imageView.setOnLongClickListener(getOnLongClickListener(context, ImageAdapter.this));
                this.imagePath = adapter.trail.getImagePaths().get(position);
            }
            chooseBitmap();
            this.position = position;
            this.imageView.setClickable(true);
            setOnClickListener();
            this.imageView.setOnLongClickListener(getOnLongClickListener(context, ImageAdapter.this));
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


        // John: Common method for our adapters to delete items
        public View.OnLongClickListener getOnLongClickListener(final Context context, final NewAdapter adapter) {
            return new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    AlertDialog.Builder adb=new AlertDialog.Builder(context);
                    adb.setTitle("Delete?");
                    adb.setMessage("Are you sure you want to delete this image?");
                    final int positionToRemove = position;
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            adapter.deleteItem(positionToRemove);
                            new DatabaseContract.UpdateTrailTask(context).execute(trail);
                            adapter.notifyDataSetChanged();
                        }});
                    adb.show();
                    return false;
                }
            };
        }

        void openImage() {
            final File file = new File(baseDir + File.separator + this.imagePath);
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
    
    public void deleteItem(final int position) {
        trail.removeImagePath(position);
        notifyDataSetChanged();
    }

}