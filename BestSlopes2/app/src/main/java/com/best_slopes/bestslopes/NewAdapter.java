package com.best_slopes.bestslopes;

import android.widget.BaseAdapter;

/**
 * Created by Muratcan on 4/27/2016.
 *  For deleteable items.
 */
public abstract class NewAdapter extends BaseAdapter {
    abstract public void deleteItem(final int position);
}
