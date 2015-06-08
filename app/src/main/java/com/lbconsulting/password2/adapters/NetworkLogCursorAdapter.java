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
import com.lbconsulting.password2.database.NetworkLogTable;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * CursorAdapter to populate a list view of Network Logs
 */
public class NetworkLogCursorAdapter extends CursorAdapter {

    private final SimpleDateFormat mDateFormatter;
    private final NumberFormat mNumberFormatter;

    public NetworkLogCursorAdapter(Context context, Cursor c, int flags, String NetworkLogsTitle) {
        super(context, c, flags);
        mDateFormatter = new SimpleDateFormat("M/d/yy   hh:mm a");
        mNumberFormatter = NumberFormat.getInstance();
        MyLog.i("NetworkLogCursorAdapter", "NetworkLogCursorAdapter constructor. " + NetworkLogsTitle);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.row_lv_network_log, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        TextView tvRev = (TextView) view.findViewById(R.id.tvRev);
        TextView tvAction = (TextView) view.findViewById(R.id.tvAction);
        TextView tvNetwork = (TextView) view.findViewById(R.id.tvNetwork);
        TextView tvBytes = (TextView) view.findViewById(R.id.tvBytes);

        long dateMills = cursor.getLong(cursor.getColumnIndex(NetworkLogTable.COL_DATE_TIME));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMills);
        String date = mDateFormatter.format(calendar.getTime());

        String rev = "Rev: " + cursor.getString(cursor.getColumnIndex(NetworkLogTable.COL_REV));

        int intAction = cursor.getInt(cursor.getColumnIndex(NetworkLogTable.COL_ACTION_STYLE));
        String action = "D";
        if (intAction == NetworkLogTable.UPLOAD) {
            action = "U";
        }

        int intNetwork = cursor.getInt(cursor.getColumnIndex(NetworkLogTable.COL_NETWORK));
        String network = "Wi-Fi";
        if (intNetwork == NetworkLogTable.MOBILE) {
            network = "Mobile";
        }

        String strBytes;
        long bytes = cursor.getLong(cursor.getColumnIndex(NetworkLogTable.COL_BYTES));
        if (bytes < 1024) {
            strBytes = mNumberFormatter.format(bytes) + " bytes";
        } else {
            bytes = bytes / 1024;
            strBytes = mNumberFormatter.format(bytes) + " KB";
        }

        tvDate.setText(date);
        tvRev.setText(rev);
        tvAction.setText(action);
        tvNetwork.setText(network);
        tvBytes.setText(strBytes);
    }
}