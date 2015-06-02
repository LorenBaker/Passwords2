package com.lbconsulting.password2.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.lbconsulting.password2.activities.MainActivity;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsNetworkStatus;

import de.greenrobot.event.EventBus;

/**
 * This service poles Dropbox for changes in the Passwords data file
 */
public class UpdateService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private volatile boolean taskRunning = true;
    private volatile int INTERVAL;
    private volatile int mSlowInterval;
    private volatile int mFastInterval;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private String mDropboxFullFilename;

    private clsNetworkStatus getNetworkStatus() {
        boolean isWifiConnected;
        boolean isMobileConnected;
        boolean isOkToUseNetwork;
        NetworkInfo networkInfo;

        int userNetworkingPreference = MySettings.getNetworkPreference();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return null;
        }
        networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null) {
            return null;
        }

        isWifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        isMobileConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;

        // assume there is no network connection (mobile or Wi-Fi),
        isOkToUseNetwork = false;
        // Check for wi-fi network availability
        if (userNetworkingPreference == MySettings.NETWORK_WIFI_ONLY && isWifiConnected) {
            // The device is connected to wi-fi ... so,
            // Allow the download of data.
            isOkToUseNetwork = true;

            // Check for any network connection
        } else if (userNetworkingPreference == MySettings.NETWORK_ANY && (isWifiConnected||isMobileConnected)) {
            // The device is connected to a network ... so,
            // Allow the download of data.
            isOkToUseNetwork = true;
        }

        clsNetworkStatus result = new clsNetworkStatus(isMobileConnected, isWifiConnected, isOkToUseNetwork);
        MyLog.i("UpdateService", "getNetworkStatus:"
                + " userNetworkingPreference = " + userNetworkingPreference
                + "; isMobileConnected = " + isMobileConnected
                + "; isWifiConnected = " + isWifiConnected
                + "; isOkToUseNetwork = " + isOkToUseNetwork);
        return result;
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

        MyLog.i("UpdateService", "onCreate");
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSlowInterval = MySettings.getSyncPeriodicityMinutes();
        MyLog.i("UpdateService", "onStartCommand: Periodicity = " + mSlowInterval + " minutes.");

        // convert minutes to milliseconds
        mSlowInterval = mSlowInterval * 60000;
        mFastInterval = 15000; // 15 seconds
        if (mSlowInterval < mFastInterval) {
            mSlowInterval = 4 * mFastInterval;
        }
        INTERVAL = mSlowInterval;
        mDBApi = MainActivity.getDropboxAPI();
        mDropboxFullFilename = MySettings.getDropboxFilename();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MyLog.i("UpdateService", "onConfigurationChanged");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        MyLog.i("UpdateService", "onDestroy");
        taskRunning = false;
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO: Find "Failed to Update Toast
            while (taskRunning) {
                synchronized (this) {
                    while (taskRunning) {
                        //clsNetworkStatus status = MySettings.getNetworkStatus();
                        clsNetworkStatus status = getNetworkStatus();
                        if (status != null && status.isOkToUseNetwork()) {
                            if (!MySettings.getNetworkBusy()) {
                                // The network is not busy ...
                                // check back less frequently
                                INTERVAL = mSlowInterval;
                                checkForRevisedDataFile();
                            } else {
                                // The network is busy ...
                                // check back more frequently
                                MyLog.i("UpdateService: ", "Network BUSY.");
                                INTERVAL = mFastInterval;
                            }
                        } else {
                            MyLog.i("UpdateService", "Network NOT AVAILABLE");
                        }

                        try {
                            Thread.sleep(INTERVAL);
                        } catch (InterruptedException e) {
                            MyLog.e("UpdateService", "InterruptedException. " + e.getMessage());
                        }
                    }
                }
            }
        }

        private void checkForRevisedDataFile() {
            MyLog.i("UpdateService", "Checking for revised data file.");
            try {
                DropboxAPI.Entry dropboxEntry = mDBApi.metadata(mDropboxFullFilename, 1, null, false, null);
                if (dropboxEntry != null && dropboxEntry.bytes > 0 && !dropboxEntry.isDeleted) {
                    // the file exists
                    String rev = MySettings.getDropboxFileRev();
                    // download the data file if the file exist
                    // and if the file rev is unknown or,
                    // if the existing rev is different from the last download rev
                    MyLog.i("UpdateService", "current rev = " + rev + "; dropbox rev = " + dropboxEntry.rev);
                    if (rev.equals(MySettings.UNKNOWN) || !rev.equals(dropboxEntry.rev)) {
                        MyLog.i("UpdateService", "Data File Changed. Requesting download.");
                        EventBus.getDefault().post(new clsEvents.onDropboxDataFileChange());
                    } else {
                        MyLog.i("UpdateService", "No change in Passwords data file.");
                    }
                } else {
                    MyLog.i("UpdateService", "File not found.");
                }
            } catch (DropboxException e) {
                MyLog.e("UpdateService", "checkForRevisedDataFile: DropboxException " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


}