package com.lbconsulting.password2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.clsDropboxFolder;

import java.util.ArrayList;

/**
 * List view adapter for clsDbxFolder
 */
public class DropboxFoldersListViewAdapter extends ArrayAdapter<clsDropboxFolder> {

    private final Context mContext;
    private final ArrayList<clsDropboxFolder> mItems;

    // View lookup cache
    private static class ViewHolder {
        ImageView ivFolderIcon;
        TextView tvFolderName;
    }

    public DropboxFoldersListViewAdapter(Context context, ArrayList<clsDropboxFolder> items) {
        super(context, R.layout.row_lv_dropbox_folder, items);
        this.mContext = context;
        this.mItems = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        clsDropboxFolder record = mItems.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_lv_dropbox_folder, parent, false);
            viewHolder.ivFolderIcon = (ImageView) convertView.findViewById(R.id.ivFolderIcon);
            viewHolder.tvFolderName = (TextView) convertView.findViewById(R.id.tvFolderName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.tvFolderName.setText(record.toString());
        viewHolder.tvFolderName.setTag(record);

        switch (record.getIcon()){
            case clsDropboxFolder.UNSHARED_ICON:
                viewHolder.ivFolderIcon.setImageResource(R.drawable.ic_folder);
                break;

            case clsDropboxFolder.SHARED_ICON:
                viewHolder.ivFolderIcon.setImageResource(R.drawable.ic_folder_user);
                break;

            case clsDropboxFolder.UP_ARROW_ICON:
                viewHolder.ivFolderIcon.setImageResource(R.drawable.ic_up_arrow);
                break;

        }

        // Return the completed view to render on screen
        return convertView;
    }
}
