package edu.oregonstate.cs489.humphrmi.bestslopes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;

public class EditTrail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trail);

        final ImageView imageView = (ImageView) findViewById(R.id.edit_difficulty_image);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.edit_difficulty_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_difficulty_easy:
                        imageView.setImageResource(R.drawable.easy);
                        break;

                    case R.id.radio_difficulty_medium:
                        imageView.setImageResource(R.drawable.medium);
                        break;

                    case R.id.radio_difficulty_hard:
                        imageView.setImageResource(R.drawable.difficult);
                        break;

                    default:
                        imageView.setImageResource(R.drawable.default_image);
                        break;
                }
            }
        });
    }
}
