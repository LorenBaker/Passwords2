package com.lbconsulting.password2.classes;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Here we show getting metadata for a directory and downloading a file in a
 * background thread, trying to show typical exception handling and flow of
 * control for an app that downloads a file from Dropbox.
 */

public class DownloadTextFile extends AsyncTask<Void, Long, String> {

    private final Context mContext;
    private final DropboxAPI<?> mDBApi;
    private final String mDropboxFullFilename;
    private final boolean mIsVerbose;
    private String mErrorMsg;

    private DownloadFinishedListener mCallback;

    public interface DownloadFinishedListener {
        void fileDownloadFinish(String fileContent);
    }

    public DownloadTextFile(Context context, DropboxAPI<?> api, String dropboxFullFilename, boolean isVerbose) {
        //, DownloadFinishedListener callback
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();

        mDBApi = api;
        mDropboxFullFilename = dropboxFullFilename;
        mIsVerbose = isVerbose;

        //mCallback = (DownloadFinishedListener) context;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (DownloadFinishedListener) context;
        } catch (ClassCastException e) {
            String errorMessage = context.toString() + " must implement DownloadFinishedListener";
            MyLog.e("DownloadTextFile", "DownloadTextFile: " + errorMessage);
            throw new ClassCastException(errorMessage);
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String filename = mDropboxFullFilename.substring(mDropboxFullFilename.lastIndexOf("/") + 1);
        MyLog.i("DownloadTextFile", "onPreExecute: STARTING download of " + filename);
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            Entry existingEntry = mDBApi.metadata(mDropboxFullFilename, 1, null, false, null);
            MyLog.i("DownloadTextFile", "doInBackground: File exists; " + existingEntry.bytes + " bytes; rev is now: " + existingEntry.rev);
            if (existingEntry.bytes == 0) {
                MyLog.e("DownloadTextFile", "doInBackground: File" + existingEntry.fileName() + " exists but is empty!");
                return "";
            }

            // The file exists ... download the latest version to a stream
            DropboxAPI.DropboxInputStream inputStream = mDBApi.getFileStream(mDropboxFullFilename, null);
            return IOUtils.toString(inputStream);

        } catch (DropboxUnlinkedException e) {
            // The AuthSession wasn't properly authenticated or user unlinked.
            MyLog.e("DownloadTextFile", "doInBackground: DropboxUnlinkedException - The AuthSession wasn't properly authenticated or user unlinked.");
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Download canceled";
            MyLog.i("DownloadTextFile", "doInBackground: " + mErrorMsg);
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                // won't happen since we don't pass in revision with metadata
                MyLog.e("DownloadTextFile", "doInBackground: " + "_304_NOT_MODIFIED");

            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
                MyLog.e("DownloadTextFile", "doInBackground: " + "_401_UNAUTHORIZED");

            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
                MyLog.e("DownloadTextFile", "doInBackground: " + "_403_FORBIDDEN");

            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be a thumbnail)
                MyLog.e("DownloadTextFile", "doInBackground: " + "_404_NOT_FOUND");

            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                // too many entries to return
                MyLog.e("DownloadTextFile", "doInBackground: " + "_406_NOT_ACCEPTABLE");

            } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                MyLog.e("DownloadTextFile", "doInBackground: " + "_415_UNSUPPORTED_MEDIA");

            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
                MyLog.e("DownloadTextFile", "doInBackground: " + "_507_INSUFFICIENT_STORAGE");

            } else {
                // Something else
                MyLog.e("DownloadTextFile", "doInBackground: " + "Unknown DropboxServerException");

            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
            MyLog.i("DownloadTextFile", "doInBackground: " + mErrorMsg);

        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
            MyLog.i("DownloadTextFile", "doInBackground: " + mErrorMsg);

        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
            MyLog.i("DownloadTextFile", "doInBackground: " + mErrorMsg);

        } catch (IOException e) {
            MyLog.i("DownloadTextFile", "doInBackground: IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }


    @Override
    protected void onPostExecute(String fileContentString) {

        mCallback.fileDownloadFinish(fileContentString);

        if (!fileContentString.isEmpty()) {
            MyLog.i("DownloadTextFile", "onPostExecute: File successfully downloaded.");
            if (mIsVerbose) {
                Toast.makeText(mContext, "File successfully downloaded.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Couldn't download it, so show an error
            MyLog.e("DownloadTextFile", "onPostExecute: ERROR: " + mErrorMsg);
            if (mIsVerbose) {
                showToast(mErrorMsg);
            }
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }


}
