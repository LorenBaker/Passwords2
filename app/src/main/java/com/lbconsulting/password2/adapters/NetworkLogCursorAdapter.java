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
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsUtils;
import com.lbconsulting.password2.database.NetworkLogTable;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * CursorAdapter to populate a list view of Network Logs
 */
public class NetworkLogCursorAdapter extends CursorAdapter {

    Context mContext;
    SimpleDateFormat mDateFormatter;
    NumberFormat mNumberFormatter;

    public NetworkLogCursorAdapter(Context context, Cursor c, int flags, String NetworkLogsTitle) {
        super(context, c, flags);
        this.mContext = context;
         mDateFormatter = new SimpleDateFormat("M/d/yy   hh:mm a");
        mNumberFormatter = NumberFormat.getInstance();
        MyLog.i("NetworkLogCursorAdapter", "NetworkLogCursorAdapter constructor. " + NetworkLogsTitle);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_lv_network_log, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvRev = (TextView) view.findViewById(R.id.tvRev);
        TextView tvAction = (TextView) view.findViewById(R.id.tvAction);
        TextView tvBytes = (TextView) view.findViewById(R.id.tvBytes);

        long dateMills = cursor.getLong(cursor.getColumnIndex(NetworkLogTable.COL_DATE_TIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMills);

        String date = mDateFormatter.format(calendar.getTime());
        String rev = "Rev: " + cursor.getString(cursor.getColumnIndex(NetworkLogTable.COL_REV));
        int intAction = cursor.getInt(cursor.getColumnIndex(NetworkLogTable.COL_ACTION_STYLE));
        String action ="Down";
        if(intAction==NetworkLogTable.UPLOAD){
            action="Upload";
        }

        long bytes = cursor.getLong(cursor.getColumnIndex(NetworkLogTable.COL_BYTES))/1024;
        String strBytes = mNumberFormatter.format(bytes)+" KB";

        tvDate.setText(date);
        tvRev.setText(rev);
        tvAction.setText(action);
        tvBytes.setText(strBytes);
    }
}