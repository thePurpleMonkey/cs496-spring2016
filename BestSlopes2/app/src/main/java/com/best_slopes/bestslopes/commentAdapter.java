package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Muratcan a.k.a John on 4/25/2016.
 *
 */

public class CommentAdapter extends NewAdapter {
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
        return trail.getCommentsList().size()+1;
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
        TextView textView;
        EditText editText;
        boolean isField;
        private View fillView(final int position) {
            View rowView;
            this.isField = position >= CommentAdapter.this.getCount()-1;
            if (this.isField) {
                rowView = inflater.inflate(R.layout.comment_field, null);
                this.editText = (EditText) rowView.findViewById(R.id.commentField);
                setOnEditorActionListener();
            } else {
                rowView = inflater.inflate(R.layout.text_view_for_comments, null);
                this.textView = (TextView) rowView.findViewById(R.id.textView);
                this.textView.setText(trail.getCommentsList().get(position));
                rowView.setOnLongClickListener(getOnLongClickListener(context, CommentAdapter.this, position));
            }
            return rowView;
        }

        private void setOnEditorActionListener() {
            editText.setOnEditorActionListener(
                    new EditText.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                    actionId == EditorInfo.IME_ACTION_DONE ||
                                    event.getAction() == KeyEvent.ACTION_DOWN &&
                                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                if (!event.isShiftPressed()) {
                                    addComment(editText, v);
                                    updateListView();
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
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
                            new DatabaseContract.UpdateTrailTask(context).execute(trail);
                            adapter.notifyDataSetChanged();
                        }});
                    adb.show();
                    return false;
                }
            };
        }

    }

    private void addComment(EditText editText, View v) {
        String newComment = editText.getText().toString();
        if (newComment.equals("")) {
            return;
        }
        trail.addComment(newComment);
        notifyDataSetChanged();
        editText.clearFocus();
        if (!trail.isNew()) {
            new DatabaseContract.UpdateTrailTask(context).execute(trail);
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

    public void deleteItem(final int position) {
        trail.removeComment(position);
        new DatabaseContract.UpdateTrailTask(context).execute(trail);
        notifyDataSetChanged();
    }
}