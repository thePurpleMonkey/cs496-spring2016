package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends NewAdapter {
    private ArrayList<Trail> trails;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapter(MainActivity mainActivity, ArrayList<Trail> trails) {
        context=mainActivity;
        this.trails = trails;
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
        Trail trail = trails.get(position);
        holder.tv.setText(trail.getName());
        holder.img1.setImageResource(trail.getImageByDifficulty());

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

        //TODO: John, we need to call getOnLongClickListener() for this row and button, but I don't get the adapter
        return rowView;
    }

    private void startViewTrailActivity(int position){
        Intent intent = new Intent(context, ViewTrailActivity.class);
        Trail trail = trails.get(position);
        intent.putExtra("Trail_ID", trail.getId());
        context.startActivity(intent);
    }


    // John: Common method for our adapters to delete items
    public View.OnLongClickListener getOnLongClickListener(final Context context, final NewAdapter adapter, final int position) {
        return new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                AlertDialog.Builder adb=new AlertDialog.Builder(context);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + position);
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.deleteItem(positionToRemove);
                        adapter.notifyDataSetChanged();
                    }});
                adb.show();
                return false;
            }
        };
    }

    public void deleteItem(final int position) {
        new DatabaseContract.DeleteTrailTask(context).execute(new Long(trails.get(position).getId()));
        trails.remove(position);
        notifyDataSetChanged();
    }
}
