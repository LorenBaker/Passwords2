package com.lbconsulting.password2.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;

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

import de.greenrobot.event.EventBus;

/**
 * This service poles Dropbox for changes in the Passwords data file
 */
public class PasswordsUpdateService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private volatile boolean taskRunning = true;
    private volatile int INTERVAL;
    private volatile int mSlowInterval;
    private volatile int mFastInterval;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private String mDropboxFullFilename;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            MyLog.i("ServiceHandler", "handleMessage");
            while (taskRunning) {
                synchronized (this) {
                    while (taskRunning) {
                        try {
                            if (!MySettings.getNetworkBusy()) {
                                // The network is not busy ...
                                // check back less frequently
                                //MyLog.i("ServiceHandler", "handleMessage: Network NOT busy.");
                                INTERVAL = mSlowInterval;
                                checkForRevisedDataFile();
                            } else {
                                // The network is busy ...
                                // check back more frequently
                                MyLog.i("ServiceHandler", "handleMessage: Network BUSY.");
                                INTERVAL = mFastInterval;
                            }
                            Thread.sleep(INTERVAL);

                        } catch (InterruptedException e) {
                            MyLog.e("ServiceHandler", "handleMessage: InterruptedException. " + e.getMessage());
                        }
                    }
                }
            }
        }

        private void checkForRevisedDataFile() {
            MyLog.i("ServiceHandler", "Checking for revised data file.");
            try {
                DropboxAPI.Entry existingEntry = mDBApi.metadata(mDropboxFullFilename, 1, null, false, null);
                if (existingEntry != null) {
                    String rev = MySettings.getDropboxFileRev();
                    // download the data file if the file exist
                    // and if the file rev is unknown or,
                    // if the existing rev is different from the last download rev
                    if (rev.equals(MySettings.UNKNOWN) || !rev.equals(existingEntry.rev)) {
                        EventBus.getDefault().post(new clsEvents.onDropboxDataFileChange());
                    } else {
                        MyLog.i("ServiceHandler", "No change in Passwords data file.");
                    }
                }
            } catch (DropboxException e) {
                MyLog.e("ServiceHandler", "checkForRevisedDataFile: DropboxException " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

        MyLog.i("PasswordsUpdateService", "onCreate");
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
        MyLog.i("PasswordsUpdateService", "onStartCommand: Periodicity = " + mSlowInterval + " minutes.");

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
        MyLog.i("PasswordsUpdateService", "onConfigurationChanged");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        MyLog.i("PasswordsUpdateService", "onDestroy");
        taskRunning = false;
    }


}