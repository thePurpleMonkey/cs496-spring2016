package com.best_slopes.bestslopes;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Muratcan a.k.a John on 4/25/2016.
 *
 */

public class CommentAdapter extends BaseAdapter {
    static final private String ADD_COMMENT_STRING = "#$%#$%#$%";
    Context context;
    private static LayoutInflater inflater=null;
    private ArrayList<String> comments;
    private Trail trail;
    private ListView listView;
    public CommentAdapter(ViewTrailActivity activity, Trail trail, ListView listView) {
        context = activity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.trail = trail;
        this.listView = listView;
        loadComments();
    }

    private void loadComments() {
        this.comments = this.trail.getCommentsList();
        this.comments.add(ADD_COMMENT_STRING);
    }
    @Override
    public int getCount() {
        return comments.size();
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
        private View fillView(final int position) {
            View rowView;
            if (comments.get(position).equals(ADD_COMMENT_STRING)) {
                rowView = inflater.inflate(R.layout.comment_field, null);
                this.editText = (EditText) rowView.findViewById(R.id.commentField);
                setOnEditorActionListener();

            } else {
                rowView = inflater.inflate(R.layout.text_view_for_comments, null);
                this.textView = (TextView) rowView.findViewById(R.id.textView);
                this.textView.setText(comments.get(position));
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
                                    String newComment = editText.getText().toString();
                                    trail.addComment(newComment);
                                    comments.remove(ADD_COMMENT_STRING);
                                    comments.add(newComment);
                                    comments.add(ADD_COMMENT_STRING);
                                    editText.clearFocus();
                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                    notifyDataSetChanged();
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        return holder.fillView(position);
    }

}