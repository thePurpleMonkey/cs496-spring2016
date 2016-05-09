package com.best_slopes.bestslopes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import java.io.ByteArrayOutputStream;
import java.lang.String;

/**
 * Created by Basil Alzamil on 5/6/16.
 */
public class CameraActivity extends Activity {

    // Variables
    Button camera_button;
    private static final int REQUEST_CODE = 1888;
    static final int REQUEST_TAKE_PHOTO = 1;
    GridView myGrid;
    String test;
//    private String photoPath = "/Pictures/BestSlopes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trail);
        camera_button = (Button) findViewById(R.id.action_camera);
        camera_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);
            test = getRealPathFromURI(tempUri);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}

