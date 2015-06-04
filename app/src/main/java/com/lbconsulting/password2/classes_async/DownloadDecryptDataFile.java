package com.lbconsulting.password2.classes_async;


import android.content.ContentValues;
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
import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.CryptLib;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsItem;
import com.lbconsulting.password2.classes.clsItemSort;
import com.lbconsulting.password2.classes.clsLabPasswords;
import com.lbconsulting.password2.classes.clsNetworkStatus;
import com.lbconsulting.password2.classes.clsUserValues;
import com.lbconsulting.password2.classes.clsUsers;
import com.lbconsulting.password2.classes.clsUtils;
import com.lbconsulting.password2.database.ItemsTable;
import com.lbconsulting.password2.database.NetworkLogTable;
import com.lbconsulting.password2.database.PasswordsContentProvider;
import com.lbconsulting.password2.database.UsersTable;
import com.lbconsulting.password2.fragments.fragApplicationPassword;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.greenrobot.event.EventBus;

/**
 * Here we show getting metadata for a directory and downloading a file in a
 * background thread, trying to show typical exception handling and flow of
 * control for an app that downloads a file from Dropbox.
 */

public class DownloadDecryptDataFile extends AsyncTask<Void, Void, Integer> {

    private final int FILE_DOWNLOAD_START = 0;
    private final int FILE_NOT_FOUND = -1;
    private final int FILE_FOUND_BUT_EMPTY = -2;
    private final int FILE_DELETED = -3;
    private final int APP_PASSWORD_KEY_IS_EMPTY = -4;
    private final int INVALID_PASSWORD = -5;
    private final int UNABLE_TO_PARSE_JASON = -6;
    private final int NOT_OK_TO_DOWNLOAD_FILE = -7;
    private final int FILE_DOWNLOAD_SUCCESS = 101;
    private final int DATABASE_UPDATED_SUCCESS = 102;

    private final Context mContext;
    private final DropboxAPI<?> mDBApi;
    private final String mDropboxFullFilename;
    private final boolean mIsVerbose;
    private clsNetworkStatus mNetworkStatus;
    private int mDownloadStatus = FILE_DOWNLOAD_START;
    private long mStartingTime;

    public DownloadDecryptDataFile(Context context, DropboxAPI<?> api, String dropboxFullFilename, boolean isVerbose) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();

        mDBApi = api;
        mDropboxFullFilename = dropboxFullFilename;
        mIsVerbose = isVerbose;
        mDownloadStatus = FILE_DOWNLOAD_START;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mStartingTime = System.currentTimeMillis();
        EventBus.getDefault().post(new clsEvents.showProgressInActionBar(true));
        MySettings.setNetworkBusy(true);
        String filename = mDropboxFullFilename.substring(mDropboxFullFilename.lastIndexOf("/") + 1);
        mNetworkStatus = clsUtils.getNetworkStatus(mContext, MySettings.getNetworkPreference());
        MyLog.i("DownloadDecryptDataFile", "onPreExecute: STARTING download of " + filename);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (mNetworkStatus.isOkToUseNetwork()) {
            String encryptedFileContent = readFile();
            if (!encryptedFileContent.isEmpty()) {
                String decryptedFileContent = decryptFile(encryptedFileContent);
                if (!decryptedFileContent.isEmpty()) {
                    clsLabPasswords passwordsData = parseJsonFile(decryptedFileContent);
                    if (passwordsData != null) {
                        updateSQLiteDatabase(passwordsData);
                    }
                }
            }
        } else {
            mDownloadStatus = NOT_OK_TO_DOWNLOAD_FILE;
        }
        return mDownloadStatus;
    }

    private String readFile() {
        String errorMsg;
        try {
            Entry existingEntry = mDBApi.metadata(mDropboxFullFilename, 1, null, false, null);
            if (existingEntry != null && existingEntry.bytes > 0 && !existingEntry.isDeleted) {
                MyLog.i("DownloadDecryptDataFile", "readFile: File exists; " + existingEntry.bytes + " bytes; rev = " + existingEntry.rev);
            } else {
                mDownloadStatus = FILE_NOT_FOUND;
                MySettings.setFileRev(MySettings.UNKNOWN);
                return "";
            }
            if (existingEntry.bytes == 0) {
                MyLog.e("DownloadDecryptDataFile", "readFile: File" + existingEntry.fileName() + " exists but is empty!");
                mDownloadStatus = FILE_FOUND_BUT_EMPTY;
                return "";
            }

            if (existingEntry.isDeleted) {
                MyLog.e("DownloadDecryptDataFile", "readFile: File" + existingEntry.fileName() + " has been deleted!");
                mDownloadStatus = FILE_DELETED;
                return "";
            }

            // The file exists ... download the latest version to a stream
            DropboxAPI.DropboxInputStream inputStream = mDBApi.getFileStream(mDropboxFullFilename, null);
            // save the file rev to be used to check for changes in the Passwords data file
            MySettings.setFileRev(existingEntry.rev);

            // log the download
            int networkUsed = -1;
            if (mNetworkStatus.isWifiConnected()) {
                networkUsed = NetworkLogTable.WI_FI;
            } else if (mNetworkStatus.isMobileConnected()) {
                networkUsed = NetworkLogTable.MOBILE;
            }
            NetworkLogTable.createNewLog(mContext, NetworkLogTable.DOWNLOAD, networkUsed, existingEntry);

            return IOUtils.toString(inputStream);

        } catch (DropboxUnlinkedException e) {
            // The AuthSession wasn't properly authenticated or user unlinked.
            MyLog.e("DownloadDecryptDataFile", "readFile: DropboxUnlinkedException - The AuthSession wasn't properly authenticated or user unlinked.");
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            errorMsg = "Download canceled";
            MyLog.i("DownloadDecryptDataFile", "readFile: " + errorMsg);
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
            errorMsg = e.body.userError;
            if (errorMsg == null) {
                errorMsg = e.body.error;
            }
            MyLog.i("DownloadDecryptDataFile", "readFile: " + errorMsg);

        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            errorMsg = "Network error.  Try again.";
            MyLog.i("DownloadDecryptDataFile", "readFile: " + errorMsg);

        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            errorMsg = "Dropbox error.  Try again.";
            MyLog.i("DownloadDecryptDataFile", "readFile: " + errorMsg);

        } catch (DropboxException e) {
            // Unknown error
            errorMsg = "Unknown error.  Try again.";
            MyLog.i("DownloadDecryptDataFile", "readFile: " + errorMsg);

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
            mDownloadStatus = APP_PASSWORD_KEY_IS_EMPTY;
            return "";
        }

        try {
            CryptLib mCrypt = new CryptLib();
            String iv = encryptedContents.substring(0, 16);
            String encryptedContentsWithoutIv = encryptedContents.substring(16);

            decryptedContents = mCrypt.decrypt(encryptedContentsWithoutIv, key, iv);
            decryptedContents = decryptedContents.trim();
            MyLog.i("DownloadDecryptDataFile", "decryptFile: Decrypted file length = " + decryptedContents.length() + " bytes.");
            if (decryptedContents.length() == 0) {
                mDownloadStatus = INVALID_PASSWORD;
                MySettings.resetEncryptionTestText();
            }

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
                mDownloadStatus = FILE_DOWNLOAD_SUCCESS;
                int numberOfUsers = passwordsData.getUsers().size();
                int numberOfItems = passwordsData.getPasswordItems().size();
                MyLog.d("DownloadDecryptDataFile", "parseJsonFile: found " + numberOfUsers + " Users; " + numberOfItems + " Items.");
            } else {
                mDownloadStatus = UNABLE_TO_PARSE_JASON;
            }
        } catch (JsonSyntaxException e) {
            MyLog.e("DownloadDecryptDataFile", "parseJsonFile: JsonSyntaxException: " + e.getMessage());
            mDownloadStatus = UNABLE_TO_PARSE_JASON;
            e.printStackTrace();
        }
        return passwordsData;
    }

    private void updateSQLiteDatabase(clsLabPasswords passwordsData) {

        // final int ITEM_NAME = 0;
        final int SOFTWARE_KEY_CODE = 0;
        final int COMMENTS = 1;
        final int CREDIT_CARD_ACCOUNT_NUMBER = 2;
        final int CREDIT_CARD_SECURITY_CODE = 3;
        final int CREDIT_CARD_EXPIRATION_MONTH = 4;
        final int CREDIT_CARD_EXPIRATION_YEAR = 5;
        final int GENERAL_ACCOUNT_NUMBER = 6;
        final int PRIMARY_PHONE_NUMBER = 7;
        final int ALTERNATE_PHONE_NUMBER = 8;
        final int WEBSITE_URL = 9;
        final int WEBSITE_USER_ID = 10;
        final int WEBSITE_PASSWORD = 11;

        MyLog.i("DownloadDecryptDataFile", "Found " + passwordsData.getUsers().size() + " users; "
                + passwordsData.getPasswordItems().size() + " items.");
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
            if (!userValues.hasData()) {
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
        //ArrayList<clsItemSort> sortingList = null;
        ArrayList<String> plainTextArray;

        for (clsItem item : passwordsData.getPasswordItems()) {
            if (item.getID() > lastItemID) {
                lastItemID = item.getID();
            }
           // sortingList = new ArrayList<>();
            plainTextArray = new ArrayList<>();

            //plainTextArray.add(item.getName());
            plainTextArray.add(item.getSoftwareKeyCode());
            plainTextArray.add(item.getComments());
            plainTextArray.add(item.getCreditCardAccountNumber());
            plainTextArray.add(item.getCardCreditSecurityCode());
            plainTextArray.add(item.getCreditCardExpirationMonth());
            plainTextArray.add(item.getCreditCardExpirationYear());
            plainTextArray.add(item.getGeneralAccountNumber());
            plainTextArray.add(item.getPrimaryPhoneNumber());
            plainTextArray.add(item.getAlternatePhoneNumber());
            plainTextArray.add(item.getWebsiteURL());
            plainTextArray.add(item.getWebsiteUserID());
            plainTextArray.add(item.getWebsitePassword());

            ArrayList<String> encryptedArray = clsUtils.encryptStrings(plainTextArray, MySettings.DB_KEY, false);
            long itemID = item.getID();

            if (!ItemsTable.itemIdExists(mContext, itemID)) {
                // the item is not in the database ... so add it
                long newItemID = ItemsTable.createNewItem(mContext,
                        item.getUser_ID(), itemID, item.getItemType_ID(), item.getName());
                if (newItemID != itemID) {
                    MyLog.e("DownloadDecryptDataFile", "updateSQLiteDatabase: ERROR creating item with ID = " + itemID);
                    // continue to the next item
                    continue;
                }
            }

            ContentValues cv = new ContentValues();
            cv.put(ItemsTable.COL_IS_IN_TABLE, 1);
            cv.put(ItemsTable.COL_ITEM_NAME, item.getName());
            cv.put(ItemsTable.COL_ITEM_TYPE_ID, item.getItemType_ID());
            cv.put(ItemsTable.COL_USER_ID, item.getUser_ID());
            cv.put(ItemsTable.COL_SOFTWARE_KEY_CODE, encryptedArray.get(SOFTWARE_KEY_CODE));
            cv.put(ItemsTable.COL_SOFTWARE_SUBGROUP_LENGTH, item.getSoftwareSubgroupLength());
            cv.put(ItemsTable.COL_COMMENTS, encryptedArray.get(COMMENTS));
            cv.put(ItemsTable.COL_CREDIT_CARD_ACCOUNT_NUMBER, encryptedArray.get(CREDIT_CARD_ACCOUNT_NUMBER));
            cv.put(ItemsTable.COL_CREDIT_CARD_SECURITY_CODE, encryptedArray.get(CREDIT_CARD_SECURITY_CODE));
            cv.put(ItemsTable.COL_CREDIT_CARD_EXPIRATION_MONTH, encryptedArray.get(CREDIT_CARD_EXPIRATION_MONTH));
            cv.put(ItemsTable.COL_CREDIT_CARD_EXPIRATION_YEAR, encryptedArray.get(CREDIT_CARD_EXPIRATION_YEAR));
            cv.put(ItemsTable.COL_GENERAL_ACCOUNT_NUMBER, encryptedArray.get(GENERAL_ACCOUNT_NUMBER));
            cv.put(ItemsTable.COL_PRIMARY_PHONE_NUMBER, encryptedArray.get(PRIMARY_PHONE_NUMBER));
            cv.put(ItemsTable.COL_ALTERNATE_PHONE_NUMBER, encryptedArray.get(ALTERNATE_PHONE_NUMBER));
            cv.put(ItemsTable.COL_WEBSITE_URL, encryptedArray.get(WEBSITE_URL));
            cv.put(ItemsTable.COL_WEBSITE_USER_ID, encryptedArray.get(WEBSITE_USER_ID));
            cv.put(ItemsTable.COL_WEBSITE_PASSWORD, encryptedArray.get(WEBSITE_PASSWORD));
            ItemsTable.updateItems(mContext, itemID, cv);

           // sortingList.add(new clsItemSort(item.getID(), item.getName()));
        }

/*        if (sortingList != null && sortingList.size() > 0) {
            Collections.sort(sortingList, new Comparator<clsItemSort>() {
                @Override
                public int compare(clsItemSort item1, clsItemSort item2) {
                    return item1.getItemName().compareTo(item2.getItemName());
                }
            });

            int sortKey = 0;
            for (clsItemSort item : sortingList) {
                ItemsTable.updateItemSortKey(mContext, item.getItemID(), sortKey);
                sortKey++;
            }
        }*/
        MySettings.setLastItemAndUserIDs(lastItemID, lastUserID);

        // remove any users or items that are no longer in the database
        int usersDeleted = UsersTable.deleteUsersNotInTable(mContext);
        MyLog.i("DownloadDecryptDataFile", "updateSQLiteDatabase: deleted " + usersDeleted + " users from the database.");
        int itemsDeleted = ItemsTable.deleteItemsNotInTable(mContext);
        MyLog.i("DownloadDecryptDataFile", "updateSQLiteDatabase: deleted " + itemsDeleted + " items from the items table.");

        mDownloadStatus = DATABASE_UPDATED_SUCCESS;
        PasswordsContentProvider.setSuppressChangeNotification(false);
    }

    @Override
    protected void onPostExecute(Integer result) {
        EventBus.getDefault().post(new clsEvents.showProgressInActionBar(false));
        String title = mContext.getString(R.string.downloadError_okDialogTitle);
        String message = "";

        long timeDelta = System.currentTimeMillis() - mStartingTime;
        float timeDeltaSeconds = (float) timeDelta;
        timeDeltaSeconds = timeDeltaSeconds / 1000;
        String strTimeDeltaSeconds = String.format("%.02f", timeDeltaSeconds);
        MyLog.i("DownloadDecryptDataFile", "onPostExecute: Execution time = " + strTimeDeltaSeconds + " seconds.");
        switch (result) {
            case DATABASE_UPDATED_SUCCESS:
                MyLog.i("DownloadDecryptDataFile", "onPostExecute: DATABASE_UPDATED_SUCCESS");
                EventBus.getDefault().post(new clsEvents.onPasswordsDatabaseUpdated());
                if (mIsVerbose) {
                    Toast.makeText(mContext,
                            mContext.getString(R.string.downloadSuccess_databaseUpdatedSuccess),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case FILE_NOT_FOUND:
                message = mContext.getString(R.string.downloadError_fileNotFound);
                break;

            case FILE_FOUND_BUT_EMPTY:
                message = mContext.getString(R.string.downloadError_fileFoundButEmpty);
                break;

            case FILE_DELETED:
                message = mContext.getString(R.string.downloadError_fileDeleted);
                break;

            case APP_PASSWORD_KEY_IS_EMPTY:
                message = mContext.getString(R.string.downloadError_passwordKeyIsEmpty);
                break;

            case INVALID_PASSWORD:
                message = mContext.getString(R.string.downloadError_invalidPassword);
                break;

            case UNABLE_TO_PARSE_JASON:
                message = mContext.getString(R.string.downloadError_unableToParseJason);
                break;

            case FILE_DOWNLOAD_SUCCESS:
                message = mContext.getString(R.string.downloadError_fileDownloadSuccess);
                break;

            case FILE_DOWNLOAD_START:
                message = mContext.getString(R.string.downloadError_fileDownloadStarted);
                break;

            case NOT_OK_TO_DOWNLOAD_FILE:
                message = mContext.getString(R.string.downloadError_notOkToDownloadFile);
                break;

        }
        if (!message.isEmpty()) {
            MyLog.e("DownloadDecryptDataFile", "onPostExecute: " + message);
            int startupState = MySettings.getStartupState();
            EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
            if (startupState != fragApplicationPassword.STATE_PASSWORD_ONLY) {
                MySettings.setStartupState(fragApplicationPassword.STATE_STEP_3A_GET_APP_PASSWORD);
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_APP_PASSWORD, false));
            }
        }

        MySettings.setNetworkBusy(false);
    }

/*    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }

    public interface DownloadFinishedListener {
        void onFileDownloadFinished(Boolean result);
    }*/


}
