package com.lbconsulting.password2.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Here we show uploading a file in a background thread, trying to show
 * typical exception handling and flow of control for an app that uploads a
 * file from Dropbox.
 */
public class UploadTextFile extends AsyncTask<Void, Long, Boolean> {

    private final DropboxAPI<?> mApi;
    private final  String mDropboxFullFilename;
    private final String mFileContent;
    private final boolean mIsVerbose;

    private final long mFileLen;
    //private UploadRequest mRequest;
    private final Context mContext;

    private String mErrorMsg;


    public UploadTextFile(Context context, DropboxAPI<?> api, String fullDropboxFilename, String fileContent, boolean isVerbose) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();

        mFileLen = fileContent.length();
        mApi = api;
        mDropboxFullFilename = fullDropboxFilename;
        mFileContent = fileContent;
        mIsVerbose = isVerbose;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String filename = mDropboxFullFilename.substring(mDropboxFullFilename.lastIndexOf("/") + 1);
        MyLog.i("UploadTextFile", "onPreExecute: STARTING upload of " + filename);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
            if (!mFileContent.isEmpty()) {
                InputStream fileContentStream = new ByteArrayInputStream(mFileContent.getBytes(StandardCharsets.UTF_8));
                UploadRequest mRequest = mApi.putFileOverwriteRequest(mDropboxFullFilename, fileContentStream, mFileLen, null);
                if (mRequest != null) {
                    mRequest.upload();
                    return true;
                }
            } else{
                String filename = mDropboxFullFilename.substring(mDropboxFullFilename.lastIndexOf("/") + 1);
                mErrorMsg = "Upload aborted. File: \"" + filename + "\" is empty!";
            }

        } catch (DropboxUnlinkedException e) {
            //// This session wasn't authenticated properly or user unlinked
            mErrorMsg = "This app wasn't authenticated properly.";
            MyLog.e("UploadTextFile", "doInBackground: DropboxUnlinkedException - " + mErrorMsg);
        } catch (DropboxFileSizeException e) {
            // File size too big to upload via the API
            mErrorMsg = "This file is too big to upload";
            MyLog.e("UploadTextFile", "doInBackground: DropboxUnlinkedException - " + mErrorMsg);
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Upload canceled";
            MyLog.e("UploadTextFile", "doInBackground: DropboxUnlinkedException - " + mErrorMsg);
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
                MyLog.e("UploadTextFile", "doInBackground: DropboxServerException: " + "_401_UNAUTHORIZED");

            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
                MyLog.e("UploadTextFile", "doInBackground: DropboxServerException: " + "_403_FORBIDDEN");

            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be thumbnailed)
                MyLog.e("UploadTextFile", "doInBackground: DropboxServerException: " + "_404_NOT_FOUND");

            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
                MyLog.e("UploadTextFile", "doInBackground: DropboxServerException: " + "_507_INSUFFICIENT_STORAGE");

            } else {
                // Something else
                MyLog.e("UploadTextFile", "doInBackground: DropboxServerException: " + "Unknown error");

            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
            MyLog.e("UploadTextFile", "doInBackground: DropboxIOException - " + mErrorMsg);

        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
            MyLog.e("UploadTextFile", "doInBackground: DropboxParseException - " + mErrorMsg);

        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
            MyLog.e("UploadTextFile", "doInBackground: DropboxException - " + mErrorMsg);

        }
        return false;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            String filename = mDropboxFullFilename.substring(mDropboxFullFilename.lastIndexOf("/") + 1);
            MyLog.i("UploadTextFile", "onPostExecute: File successfully uploaded.");
            if (mIsVerbose) {
                showToast("File \"" + filename + "\" successfully uploaded.");
            }
        } else {
            MyLog.e("UploadTextFile", "onPostExecute: ERROR: " + mErrorMsg);
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
