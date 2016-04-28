package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

public class CustomAdapter extends BaseAdapter {
    private Map<Integer, Trail> trails;
    Context context;
    private static LayoutInflater inflater=null;

    public CustomAdapter(MainActivity mainActivity) {
        context=mainActivity;
        this.trails = mainActivity.getAllTrails();
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return trails.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img1;
        ImageView img2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();

        View rowView;
        rowView = inflater.inflate(R.layout.trail_row, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img1=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.tv.setText(trails.get(position).getName());
        holder.img1.setImageResource(trails.get(position).getImageByDifficulty());

        holder.img2=(ImageView) rowView.findViewById(R.id.imageView2);
        holder.img2.setImageResource(R.drawable.arrow_enter_trail);

        /*Set ClickListener to arrow image */
        rowView.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startViewTrailActivity(position);
            }
        });

        /*Set ClickListener to row */
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startViewTrailActivity(position);
            }
        });


        return rowView;
    }

    private void startViewTrailActivity(int position){
        Intent intent = new Intent(context, ViewTrailActivity.class);
        Trail trail = trails.get(position);
        intent.putExtra("Trail_ID", trail.getId());
        context.startActivity(intent);
    }

}
