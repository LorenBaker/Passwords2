package com.lbconsulting.password2.classes_async;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.database.ItemsTable;
import com.lbconsulting.password2.database.UsersTable;
import com.lbconsulting.password2.fragments.fragApplicationPassword;

import de.greenrobot.event.EventBus;


public class CreateNewDropboxFolder extends AsyncTask<Void, Void, String> {
    private final Context mContext;
    private final DropboxAPI<?> mDBApi;
    private final String mNewDropboxFolderPath;


    public interface folderFinishedListener {
        void onFolderDownloadComplete(Boolean result);
    }

    public CreateNewDropboxFolder(Context context, DropboxAPI<?> api, String selectedFolderPath, String newDropboxFolderPath) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();
        mDBApi = api;
        mNewDropboxFolderPath = selectedFolderPath + "/" + newDropboxFolderPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MyLog.i("CreateNewDropboxFolder", "onPreExecute");
        MySettings.setNetworkBusy(true);
    }

    @Override
    protected String doInBackground(Void... params) {

        String result = "Failed to created new Dropbox folder: \"" + mNewDropboxFolderPath + "\".";
        boolean newFolderExists = false;
        try {
            DropboxAPI.Entry existingEntry = mDBApi.metadata(mNewDropboxFolderPath, 1, null, false, null);
            if (existingEntry != null && existingEntry.isDir) {
                newFolderExists = true;
            }
        } catch (DropboxException e) {
            // Do nothing ... proposed folder does not exist.
        }

        if (!newFolderExists) {
            // if the proposed folder does NOT exist... create it.
            try {
                mDBApi.createFolder(mNewDropboxFolderPath);
                result = "Dropbox folder \"" + mNewDropboxFolderPath + "\" created.";
            } catch (DropboxException e) {
                MyLog.e("CreateNewDropboxFolder", "doInBackground: DropboxException: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            result = result + " The folder already exists!";
        }

        return result;
    }


    @Override
    protected void onPostExecute(String result) {
        MySettings.setNetworkBusy(false);

        if (result.startsWith("Fail")) {
            MyLog.e("CreateNewDropboxFolder", "onPostExecute: " + result);
            String title = "Failed to create folder";
            EventBus.getDefault().post(new clsEvents.showOkDialog(title, result));
        } else {
            MyLog.i("CreateNewDropboxFolder", "onPostExecute: " + result);
            String message = "Dropbox folder \"" + mNewDropboxFolderPath + "\" created.";
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

            // save the new folder's path
            MySettings.setDropboxFolderName(mNewDropboxFolderPath);

            if (MySettings.getStartupState() != fragApplicationPassword.STATE_STEP_1_SELECT_FOLDER) {
                // remove all users and items from the database
                UsersTable.deleteAllUsers(mContext);
                ItemsTable.deleteAllItems(mContext);

                MySettings.setActiveUserID(-1);
            }

            // set the next step in the initial startup process
            MySettings.setStartupState(fragApplicationPassword.STATE_STEP_2_DOES_FILE_EXIST);
            // return to FRAG_APP_PASSWORD
            EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_APP_PASSWORD, false));
        }
    }


}
