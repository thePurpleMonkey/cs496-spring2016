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
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageAdapter extends AdapterForClickables {
    public static final int REQUEST_TAKE_PICTURE = 1453;
    private static String baseDir;
    private Context context;
    private static LayoutInflater inflater;
    private Trail trail;
    private ImageViewHolder currentHolder;

    public ImageAdapter(AppCompatActivity mainActivity, Trail trail) {
        baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.context = mainActivity;
        this.trail = trail;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            this.position = position;
            ImageAdapter.this.currentPosition = position;
            ImageAdapter.this.currentHolder = this;
            ImageAdapter adapter = ImageAdapter.this;
            if (this.position == adapter.getCount()-1) {
                this.isIcon = true;
            }
            else {
                this.imageView.setOnLongClickListener(getOnLongClickListenerToDelete(context, ImageAdapter.this, position));
                this.imagePath = adapter.trail.getImagePaths().get(position);
            }
            chooseBitmap();
            this.imageView.setClickable(true);
            this.imageView.setOnClickListener(getOnClickListener(position));
        }

        private void chooseBitmap() {
            if (this.isIcon) {
                Bitmap addImageIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.add_symbol);
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

    @Override
    public void onClickListener(View v, int currentPosition) {

        if (currentPosition == this.getCount()-1) {
            pickImage();
        }
        else {
            openImage(currentPosition);
        }
    }

    void openImage(int currentPosition) {
        final File file = new File(baseDir + File.separator + trail.getImagePaths().get(currentPosition));
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "image/*");
            context.startActivity(intent);
        }
    }

        private void pickImage() {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String path = baseDir + File.separator + "DCIM" + File.separator + "BestSlopes2";
            String name = "BS_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".JPG";
            File imagesFolder = new File(path, "Trail"+trail.getId());
            imagesFolder.mkdirs();

            File image = new File(imagesFolder,name);
            Uri uriSavedImage = Uri.fromFile(image);
            String fullP = "DCIM" + File.separator + "BestSlopes2" + File.separator +  "Trail"+trail.getId()+ File.separator + name;
            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
            camera_intent.putExtra("FULLIMGPATH", fullP);
            trail.addImagePath(fullP);
            (( AppCompatActivity)context).startActivityForResult(camera_intent, REQUEST_TAKE_PICTURE);
        }

    @Override
    public boolean onLongClickListener(final Context context, final AdapterForClickables adapter, final int position) { return false; }

    @Override
    public void onPositiveButtonOnLongClickToDelete(Context context, AdapterForClickables adapter, int position) {
        trail.removeImagePath(position);
        new DatabaseContract.UpdateTrailTask(context).execute(trail);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditorActionListener(TextView v, int actionId, KeyEvent event) { }
}