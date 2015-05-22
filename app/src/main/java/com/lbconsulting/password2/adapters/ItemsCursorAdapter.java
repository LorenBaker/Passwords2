package com.lbconsulting.password2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.database.ItemsTable;

/**
 * CursorAdapter to populate a list view of item names
 */
public class ItemsCursorAdapter extends CursorAdapter {

    Context mContext;

    public ItemsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mContext = context;
        MyLog.i("ItemsCursorAdapter", "ItemsCursorAdapter constructor.");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_lv_password_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) {
            return;
        }

        TextView tvItemName = (TextView) view.findViewById(R.id.tvItemName);
        String itemName = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NAME));
        tvItemName.setText(itemName);

    }
}