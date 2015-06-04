package com.lbconsulting.password2.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsNetworkStatus;
import com.lbconsulting.password2.classes.clsUtils;
import com.lbconsulting.password2.database.NetworkLogTable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import de.greenrobot.event.EventBus;

/**
 * This intent service encrypts and then uploads
 * the the provided file string to the provided Dropbox folder.
 */
public class UploadService extends IntentService {

    private final int INTERVAL = 30000; // 30 seconds
    private volatile boolean mTaskRunning = true;
    private volatile boolean mUploadingDataFile = false;
    private volatile String mFileRev = "";
    private volatile int mUploadAttempts;
    private final int MAX_UPLOAD_ATTEMPTS = 3;


    private String mDropboxFilename;
    private String mFilename;
    private String mPassword;
    private String mFileString;
    private String mAppKey;
    private String mAppSecret;
    private String mAccessToken;
    private int mUserNetworkingPreference;
    private DropboxAPI<AndroidAuthSession> mDBApi = null;
    private clsNetworkStatus mNetworkStatus;
    private String mEncryptedFileString;
    //private boolean mIsEncryptionComplete = false;


    private static final String UNKNOWN = "UNKNOWN";
    public static final String ARG_DROPBOX_FILENAME = "argDropboxFilename";
    public static final String ARG_PASSWORD = "argPassword";
    public static final String ARG_FILE_STRING = "argFileString";
    public static final String ARG_APP_KEY = "argAppKey";
    public static final String ARG_APP_SECRET = "argAppSecret";
    public static final String ARG_ACCESS_TOKEN = "argAccessToken";
    public static final String ARG_NETWORKING_PREFERENCE = "argNetworkingPreference";


    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public UploadService() {
        super("UPLOAD Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i("UPLOAD Service", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.i("UPLOAD Service", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        MyLog.i("UPLOAD Service", "onHandleIntent");

        mDropboxFilename = intent.getStringExtra(ARG_DROPBOX_FILENAME);
        mFilename = mDropboxFilename.substring(mDropboxFilename.lastIndexOf("/") + 1);
       // String parentPath = getParentPath(mDropboxFilename);
        //mDropboxFilename = parentPath + "/test" + mFilename;

        mPassword = intent.getStringExtra(ARG_PASSWORD);
        mFileString = intent.getStringExtra(ARG_FILE_STRING);
        mAppKey = intent.getStringExtra(ARG_APP_KEY);
        mAppSecret = intent.getStringExtra(ARG_APP_SECRET);
        mAccessToken = intent.getStringExtra(ARG_ACCESS_TOKEN);
        mUserNetworkingPreference = intent.getIntExtra(ARG_NETWORKING_PREFERENCE, MySettings.NETWORK_ANY);

        AppKeyPair appKeys = new AppKeyPair(mAppKey, mAppSecret);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        if (!mAccessToken.equals(UNKNOWN)) {
            session.setOAuth2AccessToken(mAccessToken);
            mDBApi = new DropboxAPI<>(session);
        }
        if (mDBApi == null) {
            MyLog.e("UPLOAD Service", "Upload failed. mDBApi is null.");
            return;
        }

        if (mFileString == null || mFileString.isEmpty()) {
            MyLog.e("UPLOAD Service", "Upload failed. File string does not exist.");
            return;
        }
        // Encrypt the file string
        mEncryptedFileString = clsUtils.encryptString(mFileString, mPassword, true);
        if (!mEncryptedFileString.isEmpty()) {
            mFileString = null;
        } else {
            MyLog.e("UPLOAD Service", "Upload failed. Unable to encrypt file.");
            return;
        }

        mTaskRunning = true;
        MyLog.i("UPLOAD Service", "UPLOAD TASK RUNNING.");
        while (mTaskRunning) {
            mNetworkStatus = getNetworkStatus();
            if (mNetworkStatus != null && mNetworkStatus.isOkToUseNetwork() && !mUploadingDataFile) {
                uploadToDropbox();
            } else {
                try {
                    MyLog.i("UPLOAD Service", "UPLOAD TASK SLEEPING " + INTERVAL / 1000 + " seconds.");
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    MyLog.i("UPLOAD Service", "InterruptedException. " + e.getMessage());
                }
            }
        }
        if (mFileRev.isEmpty()) {
            MyLog.e("UPLOAD Service", "UPLOAD FAILED ... TASK COMPLETE.");
        } else {
            MyLog.i("UPLOAD Service", "UPLOAD TASK COMPLETE: Rev = " + mFileRev);
        }
    }

    private clsNetworkStatus getNetworkStatus() {
        boolean isWifiConnected;
        boolean isMobileConnected;
        boolean isOkToUseNetwork;
        NetworkInfo networkInfo;

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
        if (mUserNetworkingPreference == MySettings.NETWORK_WIFI_ONLY && isWifiConnected) {
            // The device is connected to wi-fi ... so,
            // Allow the download of data.
            isOkToUseNetwork = true;

            // Check for any network connection
        } else if (mUserNetworkingPreference == MySettings.NETWORK_ANY && (isWifiConnected || isMobileConnected)) {
            // The device is connected to a network ... so,
            // Allow the download of data.
            isOkToUseNetwork = true;
        }

        clsNetworkStatus result = new clsNetworkStatus(isMobileConnected, isWifiConnected, isOkToUseNetwork);
        MyLog.i("UPLOAD Service", "getNetworkStatus:"
                + " userNetworkingPreference = " + mUserNetworkingPreference
                + "; isMobileConnected = " + isMobileConnected
                + "; isWifiConnected = " + isWifiConnected
                + "; isOkToUseNetwork = " + isOkToUseNetwork);
        return result;
    }

    public String getParentPath(String path) {
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        if (parentPath.equals("")) {
            parentPath = "/";
        }
        return parentPath;
    }

    private void uploadToDropbox() {

        boolean successfulUpload = false;
        try {
            MyLog.i("UPLOAD Service", "STARTING UPLOAD");
            mUploadingDataFile = true;
            InputStream inputStream = new ByteArrayInputStream(mEncryptedFileString.getBytes("UTF-8"));

            // TODO: Remove TEST from Dropbox filename
           // mDropboxFilename = getParentPath(mDropboxFilename) + "/TEST" + mFilename;

            DropboxAPI.Entry response = mDBApi.putFileOverwrite(mDropboxFilename, inputStream, mEncryptedFileString.length(), null);
            mFileRev = response.rev;
            EventBus.getDefault().post(new clsEvents.onFileRevChange(mFileRev));

            int network = -1;
            if (mNetworkStatus != null) {
                if (mNetworkStatus.isWifiConnected()) {
                    network = NetworkLogTable.WI_FI;
                } else if (mNetworkStatus.isMobileConnected()) {
                    network = NetworkLogTable.MOBILE;
                }
            }
            NetworkLogTable.createNewLog(this, NetworkLogTable.UPLOAD, network, response);
            MyLog.i("UPLOAD Service", "uploadToDropbox: SUCCESS. File size = " + response.bytes + "bytes. Rev = " + response.rev);
            successfulUpload = true;

        } catch (UnsupportedEncodingException e) {
            MyLog.e("UPLOAD Service", "uploadToDropbox UnsupportedEncodingException: " + e.getMessage());
            e.printStackTrace();

        } catch (DropboxException e) {
            MyLog.e("UPLOAD Service", "uploadToDropbox DropboxException: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            MyLog.e("UPLOAD Service", "uploadToDropbox Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            mUploadAttempts++;
            if (successfulUpload) {
                mEncryptedFileString = null;
                mTaskRunning = false;
            } else {
                if (mUploadAttempts <= MAX_UPLOAD_ATTEMPTS) {
                    // try to upload again
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        MyLog.i("UPLOAD Service", "InterruptedException. " + e.getMessage());
                    }
                } else {
                    // upload failed the maximum allowable number of times
                    mEncryptedFileString = null;
                    mTaskRunning = false;
                    mFileRev = "";
                    MyLog.e("UPLOAD Service", "UPLOAD FAILED " + mUploadAttempts + " times. STOPPING UPLOAD.");
                }
            }
            mUploadingDataFile = false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("UPLOAD Service", "onDestroy");
    }
}