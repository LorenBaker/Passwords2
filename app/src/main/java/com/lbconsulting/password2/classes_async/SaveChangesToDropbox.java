package com.lbconsulting.password2.classes_async;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.lbconsulting.password2.activities.MainActivity;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsItem;
import com.lbconsulting.password2.classes.clsItemValues;
import com.lbconsulting.password2.classes.clsLabPasswords;
import com.lbconsulting.password2.classes.clsNetworkStatus;
import com.lbconsulting.password2.classes.clsUsers;
import com.lbconsulting.password2.classes.clsUtils;
import com.lbconsulting.password2.database.ItemsTable;
import com.lbconsulting.password2.database.UsersTable;
import com.lbconsulting.password2.services.UploadService;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * This asyncTask:
 * 1.   Creates a clsLabPassword object from the database
 * 2.   Makes a JSON string from the clsLabPassword object
 * 3.   Initiates the UploadService that then encrypts and uploads
 * the data file to Dropbox
 */
public class SaveChangesToDropbox extends AsyncTask<Void, Void, String> {

    private Context mContext;
    private boolean mShowResultsDialog;

    public SaveChangesToDropbox(Context context, boolean showResultsDialog) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();
        mShowResultsDialog = showResultsDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MyLog.i("SaveChangesToDropbox", "onPreExecute");
    }

    @Override
    protected String doInBackground(Void... params) {
        MyLog.i("SaveChangesToDropbox", "doInBackground");
        String jsonFileString = "";
        // Create a clsLabPassword object from the data base
        clsLabPasswords passwordsData = createPasswordsData();
        if (passwordsData != null && passwordsData.getUsers().size() > 0) {
            // Make a JSON string from the clsLabPassword object
            jsonFileString = makeJsonString(passwordsData);
        }
        return jsonFileString;
    }

    private clsLabPasswords createPasswordsData() {
        clsLabPasswords passwordsData = new clsLabPasswords();

        fillUsers(passwordsData.getUsers());
        fillItems(passwordsData.getPasswordItems());

        return passwordsData;
    }

    private void fillUsers(ArrayList<clsUsers> users) {
        // get all users
        Cursor usersCursor = UsersTable.getAllUsersCursor(mContext, UsersTable.SORT_ORDER_USER_NAME);
        clsUsers user;
        int count = 0;
        if (usersCursor != null && usersCursor.getCount() > 0) {
            while (usersCursor.moveToNext()) {
                count++;
                user = new clsUsers();
                user.setUserID(usersCursor.getLong(usersCursor.getColumnIndex(UsersTable.COL_USER_ID)));
                user.setUserName(usersCursor.getString(usersCursor.getColumnIndex(UsersTable.COL_USER_NAME)));
                users.add(user);
            }
        }

        if (usersCursor != null) {
            usersCursor.close();
        }
        MyLog.i("SaveChangesToDropbox", "fillUsers: COMPLETE. " + count + " users.");
    }

    private void fillItems(ArrayList<clsItem> passwordItems) {
        // get all items
        Cursor itemsCursor = ItemsTable.getAllItemsCursor(mContext, ItemsTable.SORT_ORDER_ITEM_ID);
        clsItem item;
        clsItemValues itemValues;
        int count = 0;
        if (itemsCursor != null && itemsCursor.getCount() > 0) {
            while (itemsCursor.moveToNext()) {
                count++;
                itemValues = new clsItemValues(mContext, itemsCursor);
                item = new clsItem(itemValues.getItemID(), itemValues.getUserID());

                item.setName(itemValues.getItemName());
                item.setItemType_ID(itemValues.getItemTypeID());
                item.setSoftwareKeyCode(itemValues.getSoftwareKeyCode());
                item.setSoftwareSubgroupLength(itemValues.getSoftwareSubgroupLength());
                item.setComments(itemValues.getComments());
                item.setCreditCardAccountNumber(itemValues.getCreditCardAccountNumber());
                item.setCreditCardSecurityCode(itemValues.getCardCreditSecurityCode());
                item.setCreditCardExpirationMonth(itemValues.getCreditCardExpirationMonth());
                item.setCreditCardExpirationYear(itemValues.getCreditCardExpirationYear());
                item.setGeneralAccountNumber(itemValues.getGeneralAccountNumber());
                item.setPrimaryPhoneNumber(itemValues.getPrimaryPhoneNumber());
                item.setAlternatePhoneNumber(itemValues.getAlternatePhoneNumber());
                item.setWebsiteURL(itemValues.getWebsiteURL());
                item.setWebsiteUserID(itemValues.getWebsiteUserID());
                item.setWebsitePassword(itemValues.getWebsitePassword());

                passwordItems.add(item);
            }
        }

        if (itemsCursor != null) {
            itemsCursor.close();
        }
        MyLog.i("SaveChangesToDropbox", "fillItems: COMPLETE. " + count + " items.");
    }


    private String makeJsonString(clsLabPasswords passwordsData) {
        String jsonFileString = "";

        if (passwordsData != null && passwordsData.getUsers().size() > 0) {
            // Create JSON file string
            Gson gson = new Gson();
            jsonFileString = gson.toJson(passwordsData, clsLabPasswords.class);
        }
        return jsonFileString;
    }

    @Override
    protected void onPostExecute(String passwordsJsonString) {
        super.onPostExecute(passwordsJsonString);
        MyLog.i("SaveChangesToDropbox", "onPostExecute: Initiating UploadService");

        if (passwordsJsonString != null && !passwordsJsonString.isEmpty()) {
            // Initiate the UploadService
            String appPassword = MySettings.getSavedAppPassword();
            if (!appPassword.equals(MySettings.NOT_AVAILABLE)) {
                Intent uploadServiceIntent = new Intent(mContext, UploadService.class);
                uploadServiceIntent.putExtra(UploadService.ARG_DROPBOX_FILENAME, MySettings.getDropboxFilename());
                uploadServiceIntent.putExtra(UploadService.ARG_PASSWORD, appPassword);
                uploadServiceIntent.putExtra(UploadService.ARG_FILE_STRING, passwordsJsonString);
                uploadServiceIntent.putExtra(UploadService.ARG_APP_KEY, MainActivity.APP_KEY);
                uploadServiceIntent.putExtra(UploadService.ARG_APP_SECRET, MainActivity.APP_SECRET);
                uploadServiceIntent.putExtra(UploadService.ARG_ACCESS_TOKEN, MySettings.getDropboxAccessToken());
                uploadServiceIntent.putExtra(UploadService.ARG_NETWORKING_PREFERENCE, MySettings.getNetworkPreference());
                mContext.startService(uploadServiceIntent);

                clsNetworkStatus status = clsUtils.getNetworkStatus(mContext, MySettings.getNetworkPreference());

                if (mShowResultsDialog) {
                    String title = "Uploading file";
                    String msg = "A refreshed Passwords data file was sent to the background upload service.\n\n";
                    if (status.isOkToUseNetwork()) {
                        msg = msg + "More information can be found in the Network Log";
                    } else {
                        msg = msg + "The file will be uploaded when the network becomes available.";
                    }

                    EventBus.getDefault().post(new clsEvents.showOkDialog(title, msg));
                }

            } else {
                MyLog.e("SaveChangesToDropbox", "onPostExecute: Unable to initiate Upload service. The App password is NOT_AVAILABLE.");
            }
        }
    }
}
