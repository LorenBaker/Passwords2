package com.lbconsulting.password2.classes_async;


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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lbconsulting.password2.classes.CryptLib;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsItem;
import com.lbconsulting.password2.classes.clsItemValues;
import com.lbconsulting.password2.classes.clsLabPasswords;
import com.lbconsulting.password2.classes.clsUserValues;
import com.lbconsulting.password2.classes.clsUsers;
import com.lbconsulting.password2.database.ItemsTable;
import com.lbconsulting.password2.database.PasswordsContentProvider;
import com.lbconsulting.password2.database.UsersTable;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Here we show getting metadata for a directory and downloading a file in a
 * background thread, trying to show typical exception handling and flow of
 * control for an app that downloads a file from Dropbox.
 */

public class DownloadDecryptDataFile extends AsyncTask<Void, Long, Boolean> {

    private final Context mContext;
    private final DropboxAPI<?> mDBApi;
    private final String mDropboxFullFilename;
    // TODO: Create verbose messages
    private final boolean mIsVerbose;
    private String mErrorMsg;

    private DownloadFinishedListener mCallback;

    public interface DownloadFinishedListener {
        void onFileDownloadFinished(Boolean result);
    }

    public DownloadDecryptDataFile(Context context, DropboxAPI<?> api, String dropboxFullFilename, boolean isVerbose) {
        // folderFinishedListener callback
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();

        mDBApi = api;
        mDropboxFullFilename = dropboxFullFilename;
        mIsVerbose = isVerbose;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (DownloadFinishedListener) context;
        } catch (ClassCastException e) {
            String errorMessage = context.toString() + " must implement folderFinishedListener";
            MyLog.e("DownloadDecryptDataFile", "DownloadDecryptDataFile: " + errorMessage);
            throw new ClassCastException(errorMessage);
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String filename = mDropboxFullFilename.substring(mDropboxFullFilename.lastIndexOf("/") + 1);
        MyLog.i("DownloadDecryptDataFile", "onPreExecute: STARTING download of " + filename);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result = false;
        String encryptedFileContent = readFile();
        if (!encryptedFileContent.isEmpty()) {
            String decryptedFileContent = decryptFile(encryptedFileContent);
            if (!decryptedFileContent.isEmpty()) {
                clsLabPasswords passwordsData = parseJsonFile(decryptedFileContent);
                if (passwordsData != null) {
                    updateSQLiteDatabase(passwordsData);
                    result = true;
                }
            }
        }
        return result;
    }

    private String readFile() {
        try {
            Entry existingEntry = mDBApi.metadata(mDropboxFullFilename, 1, null, false, null);
            // TODO: Store rev to be able to retrieve past data files
            MyLog.i("DownloadDecryptDataFile", "readFile: File exists; " + existingEntry.bytes + " bytes; rev is now: " + existingEntry.rev);
            if (existingEntry.bytes == 0) {
                MyLog.e("DownloadDecryptDataFile", "readFile: File" + existingEntry.fileName() + " exists but is empty!");
                return "";
            }

            // The file exists ... download the latest version to a stream
            DropboxAPI.DropboxInputStream inputStream = mDBApi.getFileStream(mDropboxFullFilename, null);
            return IOUtils.toString(inputStream);

        } catch (DropboxUnlinkedException e) {
            // The AuthSession wasn't properly authenticated or user unlinked.
            MyLog.e("DownloadDecryptDataFile", "readFile: DropboxUnlinkedException - The AuthSession wasn't properly authenticated or user unlinked.");
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Download canceled";
            MyLog.i("DownloadDecryptDataFile", "readFile: " + mErrorMsg);
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                // won't happen since we don't pass in revision with metadata
                MyLog.e("DownloadDecryptDataFile", "readFile: " + "_304_NOT_MODIFIED");

            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
                MyLog.e("DownloadDecryptDataFile", "readFile: " + "_401_UNAUTHORIZED");

            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
                MyLog.e("DownloadDecryptDataFile", "readFile: " + "_403_FORBIDDEN");

            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be a thumbnail)
                MyLog.e("DownloadDecryptDataFile", "readFile: " + "_404_NOT_FOUND");

            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                // too many entries to return
                MyLog.e("DownloadDecryptDataFile", "readFile: " + "_406_NOT_ACCEPTABLE");

            } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                MyLog.e("DownloadDecryptDataFile", "readFile: " + "_415_UNSUPPORTED_MEDIA");

            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
                MyLog.e("DownloadDecryptDataFile", "readFile: " + "_507_INSUFFICIENT_STORAGE");

            } else {
                // Something else
                MyLog.e("DownloadDecryptDataFile", "readFile: " + "Unknown DropboxServerException");

            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
            MyLog.i("DownloadDecryptDataFile", "readFile: " + mErrorMsg);

        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
            MyLog.i("DownloadDecryptDataFile", "readFile: " + mErrorMsg);

        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
            MyLog.i("DownloadDecryptDataFile", "readFile: " + mErrorMsg);

        } catch (IOException e) {
            MyLog.i("DownloadDecryptDataFile", "readFile: IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private String decryptFile(String encryptedContents) {
        MyLog.i("DownloadDecryptDataFile", "decryptFile: START");
        String decryptedContents = "";
        String key = MySettings.getAppPasswordKey();
        if (key.isEmpty()) {
            return "";
        }

        try {
            CryptLib mCrypt = new CryptLib();
            String iv = encryptedContents.substring(0, 16);
            String encryptedContentsWithoutIv = encryptedContents.substring(16);

            decryptedContents = mCrypt.decrypt(encryptedContentsWithoutIv, key, iv);
            decryptedContents = decryptedContents.trim();
            MyLog.i("DownloadDecryptDataFile", "decryptFile: Decrypted file length = " + decryptedContents.length() + " bytes.");

        } catch (InvalidKeyException e) {
            MyLog.e("DownloadDecryptDataFile", "decryptFile: InvalidKeyException");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            MyLog.e("DownloadDecryptDataFile", "decryptFile: NoSuchPaddingException");
            e.printStackTrace();
        } catch (BadPaddingException e) {
            MyLog.e("DownloadDecryptDataFile", "decryptFile: BadPaddingException");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            MyLog.e("DownloadDecryptDataFile", "decryptFile: NoSuchAlgorithmException");
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            MyLog.e("DownloadDecryptDataFile", "decryptFile: IllegalBlockSizeException");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            MyLog.e("DownloadDecryptDataFile", "decryptFile: UnsupportedEncodingException");
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            MyLog.e("DownloadDecryptDataFile", "decryptFile: InvalidAlgorithmParameterException");
            e.printStackTrace();
        }

        return decryptedContents;
    }

    private clsLabPasswords parseJsonFile(String decryptedFileContent) {
        MyLog.i("DownloadDecryptDataFile", "parseJsonFile: START");
        clsLabPasswords passwordsData = null;
        Gson gson = new Gson();
        try {
            passwordsData = gson.fromJson(decryptedFileContent, clsLabPasswords.class);

            if (passwordsData != null) {
                int numberOfUsers = passwordsData.getUsers().size();
                int numberOfItems = passwordsData.getPasswordItems().size();
                MyLog.d("DownloadDecryptDataFile", "parseJsonFile: found " + numberOfUsers + " Users; " + numberOfItems + " Items.");
            }
        } catch (JsonSyntaxException e) {
            MyLog.e("DownloadDecryptDataFile", "parseJsonFile: JsonSyntaxException: " + e.getMessage());
            e.printStackTrace();
        }
        return passwordsData;
    }

    private void updateSQLiteDatabase(clsLabPasswords passwordsData) {
        MyLog.i("DownloadDecryptDataFile", "updateSQLiteDatabase: START");
        PasswordsContentProvider.setSuppressChangeNotification(true);

        // set all users and all items to "not in table"
        UsersTable.setAllUsersInTable(mContext, false);
        ItemsTable.setAllItemsInTable(mContext, false);


        // update the last userID and itemID
        long lastUserID = 0;
        clsUserValues userValues;
        for (clsUsers user : passwordsData.getUsers()) {
            if (user.getUserID() > lastUserID) {
                lastUserID = user.getUserID();
            }

            userValues = new clsUserValues(mContext, user.getUserID());
            if (userValues != null && !userValues.hasData()) {
                // insert new user into the UsersTable
                UsersTable.createNewUser(mContext, user.getUserID(), user.getUserName());
                userValues = new clsUserValues(mContext, user.getUserID());
            }
            // as needed, update user fields
            if (!user.getUserName().equals(userValues.getUserName())) {
                userValues.putUserName(user.getUserName());
            }
            userValues.update();
        }

        long lastItemID = 0;
        clsItemValues itemValues;
        for (clsItem item : passwordsData.getPasswordItems()) {
            if (item.getID() > lastItemID) {
                lastItemID = item.getID();
            }

            itemValues = new clsItemValues(mContext, item.getID());
            if (itemValues != null && !itemValues.hasData()) {
                // insert new item into the ItemsTable
                ItemsTable.CreateNewItem(mContext, item.getID(), item.getUser_ID(), item.getName());
                itemValues = new clsItemValues(mContext, item.getID());
            }
            // as needed, update item fields

            if (!item.getName().equals(itemValues.getItemName())) {
                itemValues.putName(item.getName());
            }

            if (item.getItemType_ID() != itemValues.getItemTypeID()) {
                itemValues.putItemTypeID(item.getItemType_ID());
            }

            if (item.getUser_ID() != itemValues.getUserID()) {
                itemValues.putUserID(item.getUser_ID());
            }

            if (!item.getSoftwareKeyCode().equals(itemValues.getSoftwareKeyCode())) {
                itemValues.putSoftwareKeyCode(item.getSoftwareKeyCode());
            }

            if (item.getSoftwareSubgroupLength() != itemValues.getSoftwareSubgroupLength()) {
                itemValues.putSoftwareSubgroupLength(item.getSoftwareSubgroupLength());
            }

            if (!item.getComments().equals(itemValues.getComments())) {
                itemValues.putComments(item.getComments());
            }

            if (!item.getCreditCardAccountNumber().equals(itemValues.getCreditCardAccountNumber())) {
                itemValues.putCreditCardAccountNumber(item.getCreditCardAccountNumber());
            }

            if (!item.getCardCreditSecurityCode().equals(itemValues.getCardCreditSecurityCode())) {
                itemValues.putCreditCardSecurityCode(item.getCardCreditSecurityCode());
            }

            if (!item.getCreditCardExpirationMonth().equals(itemValues.getCreditCardExpirationMonth())) {
                itemValues.putCreditCardExpirationMonth(item.getCreditCardExpirationMonth());
            }

            if (!item.getCreditCardExpirationYear().equals(itemValues.getCreditCardExpirationYear())) {
                itemValues.putCreditCardExpirationYear(item.getCreditCardExpirationYear());
            }

            if (!item.getGeneralAccountNumber().equals(itemValues.getGeneralAccountNumber())) {
                itemValues.putGeneralAccountNumber(item.getGeneralAccountNumber());
            }

            if (!item.getPrimaryPhoneNumber().equals(itemValues.getPrimaryPhoneNumber())) {
                itemValues.putPrimaryPhoneNumber(item.getPrimaryPhoneNumber());
            }

            if (!item.getAlternatePhoneNumber().equals(itemValues.getAlternatePhoneNumber())) {
                itemValues.putAlternatePhoneNumber(item.getAlternatePhoneNumber());
            }

            if (!item.getWebsiteURL().equals(itemValues.getWebsiteURL())) {
                itemValues.putWebsiteURL(item.getWebsiteURL());
            }

            if (!item.getWebsiteUserID().equals(itemValues.getWebsiteUserID())) {
                itemValues.putWebsiteUserID(item.getWebsiteUserID());
            }

            if (!item.getWebsitePassword().equals(itemValues.getWebsitePassword())) {
                itemValues.putWebsitePassword(item.getWebsitePassword());
            }

            itemValues.update();
        }

        MySettings.setLastItemAndUserIDs(lastItemID, lastUserID);

        // remove any users or items that are no longer in the database
        int usersDeleted = UsersTable.deleteUsersNotInTable(mContext);
        MyLog.i("DownloadDecryptDataFile", "updateSQLiteDatabase: deleted " + usersDeleted + " records from the database.");
        int itemsDeleted = ItemsTable.deleteItemsNotInTable(mContext);
        MyLog.i("DownloadDecryptDataFile", "updateSQLiteDatabase: deleted " + itemsDeleted + " items from the items table.");

        PasswordsContentProvider.setSuppressChangeNotification(false);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        MyLog.d("DownloadDecryptDataFile", "onPostExecute: result = " + result);
        mCallback.onFileDownloadFinished(result);

/*        if (!fileContentString.isEmpty()) {
            MyLog.i("DownloadDecryptDataFile", "onPostExecute: File successfully downloaded.");
            if (mIsVerbose) {
                Toast.makeText(mContext, "File successfully downloaded.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Couldn't download it, so show an error
            MyLog.e("DownloadDecryptDataFile", "onPostExecute: ERROR: " + mErrorMsg);
            if (mIsVerbose) {
                showToast(mErrorMsg);
            }
        }*/
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }


}
