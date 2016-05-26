package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Muratcan on 4/27/2016.
 *  For deleteable items.
 */
public abstract class AdapterForClickables extends BaseAdapter {
    protected int currentPosition;
    abstract public void onClickListener(View v, int currentPosition);
    abstract public boolean onLongClickListener(final Context context, final AdapterForClickables adapter, final int position);
    abstract public void onPositiveButtonOnLongClickToDelete(final Context context, final AdapterForClickables adapter, final int position);
    abstract public void onEditorActionListener(TextView v, int actionId, KeyEvent event);

//    private String onLongClickMessageToDelete = "Are you sure you want to delete this";
//    private String onLongClickTitleToDelete = "Delete?";
    private String onLongClickMessageToDelete = "Don't you want to vote for me?";
    private String onLongClickTitleToDelete = "VOTE?";

    public View.OnClickListener getOnClickListener(final int currentPos) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener(v, currentPos);
            }
        };
    }

    public View.OnLongClickListener getOnLongClickListener(final Context context, final AdapterForClickables adapter, final int position) {
        return new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return onLongClickListener(context, adapter, position);
            }
        };
    }

    public View.OnLongClickListener getOnLongClickListenerToDelete(final Context context, final AdapterForClickables adapter, final int position) {
        return new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                AlertDialog.Builder adb=new AlertDialog.Builder(context);
                adb.setTitle(onLongClickTitleToDelete);
                adb.setMessage(onLongClickMessageToDelete);
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onPositiveButtonOnLongClickToDelete(context, adapter, position);
                    }});
                adb.show();

                return false;
            }
        };
    }


    public TextView.OnEditorActionListener getOnEditorActionListener() {
        return new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (!event.isShiftPressed()) {
                        onEditorActionListener(v,  actionId, event);
                        return true;
                    }
                }
                return false;
            }
        };
    }
    public String getMessageToDeleteOnLongClick() {
        return onLongClickMessageToDelete;
    }

    public void setMessageToDeleteOnLongClick(String onLongClickMessage) {
        this.onLongClickMessageToDelete = onLongClickMessage;
    }

    public String getTitleToDeleteOnLongClick() {
        return onLongClickTitleToDelete;
    }

    public void setTitleOnLongClick(String onLongClickTitleToDelete) {
        this.onLongClickTitleToDelete = onLongClickTitleToDelete;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
