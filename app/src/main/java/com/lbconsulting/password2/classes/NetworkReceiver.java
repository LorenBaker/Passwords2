package com.lbconsulting.password2.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class receives network changes in network connectivity
 */
public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
     clsUtils.setIsOkToUseNetwork(context);
    }


}