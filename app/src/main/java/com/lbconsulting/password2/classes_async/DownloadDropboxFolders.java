package com.lbconsulting.password2.classes_async;


import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsDropboxFolder;
import com.lbconsulting.password2.classes.clsEvents;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Here we show getting metadata for a directory and downloading a file in a
 * background thread, trying to show typical exception handling and flow of
 * control for an app that downloads a file from Dropbox.
 */

public class DownloadDropboxFolders extends AsyncTask<Void, Long, Boolean> {

    private final Context mContext;
    private final DropboxAPI<?> mDBApi;
    private final String mDropboxFolderPath;
    private final HashMap<String, clsDropboxFolder> mFolderHashMap;

    public interface folderFinishedListener {
        void onFolderDownloadComplete(Boolean result);
    }

    public DownloadDropboxFolders(Context context, DropboxAPI<?> api, String dropboxFolderPath,
                                  HashMap<String, clsDropboxFolder> folderHashMap) {

        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();
        mDBApi = api;
        mDropboxFolderPath = dropboxFolderPath;
        mFolderHashMap = folderHashMap;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        /*try {
            mCallback = (folderFinishedListener) context;
        } catch (ClassCastException e) {
            String errorMessage = context.toString() + " must implement folderFinishedListener";
            MyLog.e("DownloadDropboxFolders", "DownloadDropboxFolders: " + errorMessage);
            throw new ClassCastException(errorMessage);
        }*/

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MySettings.setNetworkBusy(true);
        String folderName = mDropboxFolderPath.substring(mDropboxFolderPath.lastIndexOf("/") + 1);
        if(folderName.equals("/")){
            folderName="Dropbox";
        }
        MyLog.i("DownloadDropboxFolders", "onPreExecute: READING folder: " + folderName);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String mErrorMsg;
        try {
            Entry rootDirectory = mDBApi.metadata(mDropboxFolderPath, 10000, null, true, null);
            if (rootDirectory != null && rootDirectory.contents != null) {
                int icon = clsDropboxFolder.UNSHARED_ICON;
                boolean isShared = rootDirectory.icon.equals("folder_user");
                if (isShared) {
                    icon = clsDropboxFolder.SHARED_ICON;
                }
                clsDropboxFolder folder = new clsDropboxFolder(mDropboxFolderPath, icon);
                fillChildrenFolders(folder, rootDirectory.contents);
                if (!mFolderHashMap.containsKey(folder.getHashMapKey())) {
                    mFolderHashMap.put(folder.getHashMapKey(), folder);
                }
            }
            MyLog.i("DownloadDropboxFolders", "readFolder COMPLETE. mFolderHashMap size: " + mFolderHashMap.size());


        } catch (DropboxUnlinkedException e) {
            // The AuthSession wasn't properly authenticated or user unlinked.
            MyLog.e("DownloadDropboxFolders", "readFolder: DropboxUnlinkedException - The AuthSession wasn't properly authenticated or user unlinked.");
        } catch (DropboxPartialFileException e) {
            // We canceled the operation
            mErrorMsg = "Download canceled";
            MyLog.i("DownloadDropboxFolders", "readFolder: " + mErrorMsg);
        } catch (DropboxServerException e) {
            // Server-side exception.  These are examples of what could happen,
            // but we don't do anything special with them here.
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
                // won't happen since we don't pass in revision with metadata
                MyLog.e("DownloadDropboxFolders", "readFolder: " + "_304_NOT_MODIFIED");

            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
                // Unauthorized, so we should unlink them.  You may want to
                // automatically log the user out in this case.
                MyLog.e("DownloadDropboxFolders", "readFolder: " + "_401_UNAUTHORIZED");

            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
                // Not allowed to access this
                MyLog.e("DownloadDropboxFolders", "readFolder: " + "_403_FORBIDDEN");

            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
                // path not found (or if it was the thumbnail, can't be a thumbnail)
                MyLog.e("DownloadDropboxFolders", "readFolder: " + "_404_NOT_FOUND");

            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
                // too many entries to return
                MyLog.e("DownloadDropboxFolders", "readFolder: " + "_406_NOT_ACCEPTABLE");

            } else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
                MyLog.e("DownloadDropboxFolders", "readFolder: " + "_415_UNSUPPORTED_MEDIA");

            } else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
                // user is over quota
                MyLog.e("DownloadDropboxFolders", "readFolder: " + "_507_INSUFFICIENT_STORAGE");

            } else {
                // Something else
                MyLog.e("DownloadDropboxFolders", "readFolder: " + "Unknown DropboxServerException");

            }
            // This gets the Dropbox error, translated into the user's language
            mErrorMsg = e.body.userError;
            if (mErrorMsg == null) {
                mErrorMsg = e.body.error;
            }
            MyLog.e("DownloadDropboxFolders", "readFolder: " + mErrorMsg);

        } catch (DropboxIOException e) {
            // Happens all the time, probably want to retry automatically.
            mErrorMsg = "Network error.  Try again.";
            MyLog.i("DownloadDropboxFolders", "readFolder: " + mErrorMsg);

        } catch (DropboxParseException e) {
            // Probably due to Dropbox server restarting, should retry
            mErrorMsg = "Dropbox error.  Try again.";
            MyLog.i("DownloadDropboxFolders", "readFolder: " + mErrorMsg);

        } catch (DropboxException e) {
            // Unknown error
            mErrorMsg = "Unknown error.  Try again.";
            MyLog.i("DownloadDropboxFolders", "readFolder: " + mErrorMsg);

        }
        return true;
    }


    private void fillChildrenFolders(clsDropboxFolder folder, List<Entry> children) {
        if (children != null) {
            for (Entry child : children) {
                if (child.isDir && !child.isDeleted) {
                    boolean isShared = child.icon.equals("folder_user");
                    int icon = clsDropboxFolder.UNSHARED_ICON;
                    if (isShared) {
                        icon = clsDropboxFolder.SHARED_ICON;
                    }
                    clsDropboxFolder newChild = new clsDropboxFolder(child.path, icon);
                    folder.addChild(newChild);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        MyLog.d("DownloadDropboxFolders", "onPostExecute: result = " + result);
        MySettings.setNetworkBusy(false);
        EventBus.getDefault().post(new clsEvents.folderHashMapUpdated());

    }



}
