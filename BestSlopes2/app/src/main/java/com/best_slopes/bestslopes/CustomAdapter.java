package com.best_slopes.bestslopes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends AdapterForClickables {
    private ArrayList<Trail> trails;
    Activity mainActivity;
    Context context;
    private static LayoutInflater inflater=null;

    public CustomAdapter(MainActivity mainActivity, ArrayList<Trail> trails) {
        context=mainActivity;
        this.mainActivity = mainActivity;
        this.trails = trails;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return trails.size();
    }

    public class Holder
    {
        TextView tv;
        ImageView img1;
        ImageView img2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        setCurrentPosition(position);
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
        holder.img2.setOnClickListener(getOnClickListener(position));
        /* OTHER THINGS HERE */

        /*Set ClickListener to row */
        rowView.setOnClickListener(getOnClickListener(position));

        /*Set ClickOnLongListener to row */
        rowView.setOnLongClickListener(getOnLongClickListenerToDelete(context, CustomAdapter.this, position));

        return rowView;
    }

    @Override
    public void onClickListener(View v, int currentPosition) {
        Intent intent = new Intent(context, ViewTrailActivity.class);
        Trail trail = trails.get(currentPosition);
        intent.putExtra("Trail_ID", trail.getId());
        context.startActivity(intent);
    }

    @Override
    public boolean onLongClickListener(Context context, AdapterForClickables adapter, int position) {
        return false;
    }

    @Override
    public void onPositiveButtonOnLongClickToDelete(final Context context, AdapterForClickables adapter, int position) {
        new DatabaseContract.DeleteTrailTask(context).execute(new Long(trails.get(position).getId()));

        //Delete trail from server!
        ServerComms.DeleteTrailFromServer deleteServer = new ServerComms.DeleteTrailFromServer(context, mainActivity);
        deleteServer.execute(trails.get(position));
//        Toast toast = Toast.makeText(context,
//                "Deleting trail from server",
//                Toast.LENGTH_SHORT);
//        toast.show();


        trails.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void onEditorActionListener(TextView v, int actionId, KeyEvent event) { }
}
