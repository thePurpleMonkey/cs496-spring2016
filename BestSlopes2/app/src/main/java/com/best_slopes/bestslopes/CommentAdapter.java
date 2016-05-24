package com.best_slopes.bestslopes;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Muratcan a.k.a John on 4/25/2016.
 *
 */

public class CommentAdapter extends AdapterForClickables {
    Context context;
    private static LayoutInflater inflater=null;
    private Trail trail;
    private ListView listView;

    public CommentAdapter(AppCompatActivity activity, Trail trail, ListView listView) {
        context = activity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.trail = trail;
        this.listView = listView;
        updateListView();
    }

    @Override
    public int getCount() {
        return trail.getComments().length+1;
    }

    public class Holder {
        TextView textView;
        EditText editText;
        boolean isField;

        private View fillView(final int position) {
            View rowView;
            this.isField = position >= CommentAdapter.this.getCount() - 1;
            if (this.isField) {
                rowView = inflater.inflate(R.layout.comment_field, null);
                this.editText = (EditText) rowView.findViewById(R.id.commentField);
                editText.setOnEditorActionListener(getOnEditorActionListener());
            } else {
                rowView = inflater.inflate(R.layout.text_view_for_comments, null);
                this.textView = (TextView) rowView.findViewById(R.id.textView);
                this.textView.setText(trail.getComments()[position]);
                rowView.setOnLongClickListener(getOnLongClickListenerToDelete(context, CommentAdapter.this, position));
            }
            return rowView;
        }
    }

    public void onEditorActionListener(TextView v, int actionId, KeyEvent event){
        String newComment = v.getText().toString();
        if (newComment.equals("")) {
            return;
        }
        trail.addComment(newComment);

        new ServerComms.SendSingleTrailToServer().execute(trail);       //send trail to server again

        notifyDataSetChanged();
        v.clearFocus();
        if (!trail.isNew()) {
            new DatabaseContract.UpdateTrailTask(context).execute(trail);
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        updateListView();

    }

    private void updateListView() {
        setListViewHeightBasedOnChildren(listView);
        notifyDataSetChanged();
        this.listView.setSelection(this.getCount() - 1);
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        Log.e("Listview Size ", "" + listView.getCount());
        int totalHeight = 0;
        int a = 0;
        int itemLimit = this.getCount() < 4 ? this.getCount() : 4;
        for (int i = 0; i < itemLimit; i++) {
            View listItem = this.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            a = listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (this.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
        TextView tv = (TextView) ((AppCompatActivity)context).findViewById(R.id.commentsTitleText);
        tv.measure(0, 0);
        LinearLayout l = (LinearLayout) ((AppCompatActivity)context).findViewById(R.id.commentsLayout);
        l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, params.height+2* a));
        l.requestLayout();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        return holder.fillView(position);
    }

    @Override
    public void onClickListener(View v, int c) { }

    @Override
    public boolean onPositiveButtonOnLongClick(final Context context, final AdapterForClickables adapter, final int position) { return false; }

    @Override
    public void onPositiveButtonOnLongClickToDelete(Context context, AdapterForClickables adapter, int position) {
        trail.removeComment(position);

        new DatabaseContract.UpdateTrailTask(context).execute(trail);   //update DB
        new ServerComms.SendSingleTrailToServer().execute(trail);       //send trail to server again
        notifyDataSetChanged();
    }
}