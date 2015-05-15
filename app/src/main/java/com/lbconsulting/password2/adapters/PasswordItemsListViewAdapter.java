package com.lbconsulting.password2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.clsItem;

import java.util.ArrayList;


/**
 * List view adapter for clsItem
 */
public class PasswordItemsListViewAdapter extends ArrayAdapter<clsItem> {

    private Context mContext;
    private ArrayList<clsItem> mItems;

    // View lookup cache
    private static class ViewHolder {
        TextView tvItemName;
    }

    public PasswordItemsListViewAdapter(Context context, ArrayList<clsItem> items) {
        super(context, R.layout.row_lv_password_item, items);
        this.mContext = context;
        this.mItems = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        clsItem record = mItems.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_lv_password_item, parent, false);
            viewHolder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.tvItemName.setText(record.getName());
        viewHolder.tvItemName.setTag(record);

        // Return the completed view to render on screen
        return convertView;
    }
}
