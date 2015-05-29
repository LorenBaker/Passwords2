package com.lbconsulting.password2.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.lbconsulting.password2.R;
import com.lbconsulting.password2.activities.MainActivity;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsUsers;
import com.lbconsulting.password2.classes_async.DownloadDecryptDataFile;

import de.greenrobot.event.EventBus;

/**
 * This fragment manages the 5 step app startup process
 * and subsequent user input for the app password
 * <p/>
 * Five step initial startup
 * 1	Get Dropbox folder (executed in FRAG_SETTINGS)
 * 2	Does Dropbox data file exists? (executed in this fragment: async task passwordsDataFileExists)
 * <p/>
 * Path A - Data file exists
 * 3A	Get app password (executed in this fragment)
 * 4A	Read file (executed in async task DownloadDecryptDataFile)
 * 5A	Select user (executed in FRAG_SETTINGS)
 * <p/>
 * Path B - Data file does not exist
 * 3B	Create new user (executed in FRAG_USER_SETTINGS)
 * 4B	Create app password (executed in FRAG_APP_PASSWORD_SETTINGS)
 * 5B	Save new data file
 */

public class AppPasswordFragment extends Fragment implements View.OnClickListener {

    // fragment state variables
    private static final String ARG_IS_CHANGING_PASSWORD = "isChangingPassword";
    private boolean mShowPasswordText = false;
    private clsUsers mActiveUser;

    private TextView tvProgressBarCaption;
    private TextView tvFirstTimeMessage;
    private EditText txtAppPassword;
    private Button btnDisplay;
    private Button btnOK;
    private Button btnOkReadPasswordFile;


    // Startup states
    public static final int STATE_STEP_1_SELECT_FOLDER = 10;
    public static final int STATE_STEP_2_DOES_FILE_EXIST = 20;

    public static final int STATE_STEP_3A_GET_APP_PASSWORD = 31;
    public static final int STATE_STEP_4A_READ_FILE = 41;
    public static final int STATE_STEP_5A_SELECT_USER = 51;

    public static final int STATE_STEP_3B_CREATE_NEW_USER = 32;
    public static final int STATE_STEP_4B_CREATE_APP_PASSWORD = 42;
    public static final int STATE_STEP_5B_SAVE_FILE = 52;

    public static final int STATE_PASSWORD_ONLY = 60;

    public static AppPasswordFragment newInstance(boolean isChangingPassword) {
        AppPasswordFragment fragment = new AppPasswordFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_CHANGING_PASSWORD, isChangingPassword);
        fragment.setArguments(args);
        return fragment;
    }

    public AppPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("AppPasswordFragment", "onCreate()");

        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("AppPasswordFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_app_password, container, false);

        tvProgressBarCaption = (TextView) rootView.findViewById(R.id.tvProgressBarCaption);
        tvFirstTimeMessage = (TextView) rootView.findViewById(R.id.tvFirstTimeMessage);

        txtAppPassword = (EditText) rootView.findViewById(R.id.txtAppPassword);
        btnDisplay = (Button) rootView.findViewById(R.id.btnDisplay);
        btnOK = (Button) rootView.findViewById(R.id.btnOK);
        btnOkReadPasswordFile = (Button) rootView.findViewById(R.id.btnOkReadPasswordFile);

        btnDisplay.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnOkReadPasswordFile.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("AppPasswordFragment", "onActivityCreated()");
        MySettings.setOnSaveInstanceState(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("AppPasswordFragment", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("AppPasswordFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_APP_PASSWORD);
        updateUI();
    }

    private void updateUI() {
        txtAppPassword.setText("");
        switch (MySettings.getStartupState()) {
            case STATE_PASSWORD_ONLY:
                MyLog.i("AppPasswordFragment", "updateUI(): State = STATE_PASSWORD_ONLY");
                showPasswordOnly();
                break;

            case STATE_STEP_1_SELECT_FOLDER:
                MyLog.i("AppPasswordFragment", "updateUI(): State = STATE_STEP_1_SELECT_FOLDER");
                // Select dropbox folder
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_SETTINGS, false));
                break;

            case STATE_STEP_2_DOES_FILE_EXIST:
                MyLog.i("AppPasswordFragment", "updateUI(): State = STATE_STEP_2_DOES_FILE_EXIST");
                // Select user
                new passwordsDataFileExists(getActivity()).execute();
                // Continues in passwordsDataFileExists onPostExecute()
                break;

/*            case STATE_STEP_3A_GET_APP_PASSWORD:
                MyLog.i("AppPasswordFragment", "updateUI(): State=STATE_STEP_3A_GET_APP_PASSWORD");
                // Select user
                showStep3A();
                MySettings.setStartupState(STATE_STEP_4A_READ_FILE);

                break;*/

/*            case STATE_STEP_4A_READ_FILE:
                MyLog.i("AppPasswordFragment", "updateUI(): State=STATE_STEP_4A_READ_FILE: Select User");

                break;*/

/*            case STATE_STEP_5A_SELECT_USER:

                break;*/


/*            case STATE_STEP_3B_CREATE_NEW_USER:
                MyLog.i("AppPasswordFragment", "updateUI(): State=STATE_STEP_3B_CREATE_NEW_USER: Select Password");
                // Get password from user
                showStep3B();
                break;*/

/*            case STATE_STEP_4B_CREATE_APP_PASSWORD:

                break;*/

            case STATE_STEP_5B_SAVE_FILE:
                // TODO: Implement save file
                break;
        }
    }

/*    public void onEvent(clsEvents.updateUI event) {
        // updateUI();
    }*/

    public void onEvent(clsEvents.onPasswordsDatabaseUpdated event) {
        // Startup step 4A complete - Passwords database successfully updated
        // Proceed with startup step 5A - Select user
        MySettings.setStartupState(STATE_STEP_5A_SELECT_USER);
        EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_SETTINGS, false));
    }

    public void onEvent(clsEvents.downLoadResults event) {
/*        mDownloadResults = event.getDownLoadResults();
        if (mDownloadResults == MainActivity.DOWNLOAD_RESULT_SUCCESS) {
            switch (mStartupState) {

                case STATE_PASSWORD_ONLY:
                case STATE_STEP_3B_CREATE_NEW_USER:
                    // we're all done
                    MySettings.setStartupState(STATE_PASSWORD_ONLY);
                    mStartupState = STATE_PASSWORD_ONLY;
                    EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));
                    break;

                case STATE_STEP_4A_READ_FILE:
                    updateUI();
                    break;
            }

        } else if (mDownloadResults == MainActivity.OPEN_AND_SAVE_FILE_RESULT_SUCCESS) {
            // we're all done
            MySettings.setStartupState(STATE_PASSWORD_ONLY);
            mStartupState = STATE_PASSWORD_ONLY;
            EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));

        } else {
            // reading password data failed
            String title = "Reading Datafile Failed";
            String message = "";
            switch (mDownloadResults) {

                case MainActivity.OPEN_FILE_RESULT_FAIL:
                    message = "Unable to open datafile.";
                    break;

                case MainActivity.DOWNLOAD_RESULT_FAIL_READING_ENCRYPTED_FILE:
                    message = "Unable to read encrypted datafile from Dropbox folder.";
                    break;

                case MainActivity.DOWNLOAD_RESULT_FAIL_DECRYPTING_FILE:
                    message = "Invalid password.\nPlease try again.";
                    break;

                case MainActivity.DOWNLOAD_RESULT_FAIL_PARSING_JSON:
                    message = "Unable to parse decrypted datafile.";
                    break;

                case MainActivity.OPEN_AND_SAVE_FILE_RESULT_FAIL:
                    title = "Saving Datafile Failed";
                    message = "Datafile size is zero!";
                    break;
            }
            MainActivity.showOkDialog(getActivity(), title, message);
            switch (mStartupState) {

                case STATE_PASSWORD_ONLY:
                    showPasswordOnly();
                    break;

                case STATE_STEP_3B_CREATE_NEW_USER:
                    showStep3B();
                    break;

                case STATE_STEP_4A_READ_FILE:
                    // go back to state 2a -- Get Password from user
                    MySettings.setStartupState(STATE_STEP_3A_GET_APP_PASSWORD);
                    mStartupState = STATE_STEP_3A_GET_APP_PASSWORD;
                    updateUI();
                    break;
            }
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("AppPasswordFragment", "onPause()");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MyLog.i("AppPasswordFragment", "onDestroy()");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnDisplay:
                if (mShowPasswordText) {
                    txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    btnDisplay.setText(getString(R.string.btnDisplay_setText_Display));
                } else {
                    txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnDisplay.setText(getString(R.string.btnDisplay_setText_Hide));
                }
                txtAppPassword.setSelection(txtAppPassword.getText().length());
                mShowPasswordText = !mShowPasswordText;
                break;

            case R.id.btnOK:
                Toast.makeText(getActivity(), "TO COME: btnOK Click", Toast.LENGTH_SHORT).show();

   /*             switch (mStartupState) {

                    case STATE_PASSWORD_ONLY:
                        if (dropboxFolderExists() && userSelected()) {
                            String appPassword = txtAppPassword.getText().toString().trim();
                            if (appPasswordIsValid(appPassword)) {
                                MySettings.setAppPassword(appPassword);
                                showProgressBarViews();
                                EventBus.getDefault().post(new clsEvents.openAndReadLabPasswordDataAsync());
                            }
                        } else {
                            MyLog.e("AppPasswordFragment", "onClick: from STATE_PASSWORD_ONLY (Get password from user). " +
                                    "Either dropbox folder does not exits OR user not selected.");
                        }
                        break;

                    case STATE_STEP_4A_READ_FILE:
                        if (dropboxFolderExists() && userSelected()) {
                            // open passwords list items fragment
                            MySettings.setStartupState(STATE_PASSWORD_ONLY);
                            mStartupState = STATE_PASSWORD_ONLY;
                            EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));
                        } else {
                            MyLog.e("AppPasswordFragment", "onClick: from STATE_STEP_4A_READ_FILE (Select User). " +
                                    "Either dropbox folder does not exits OR user not selected.");
                        }
                        break;

                    case STATE_STEP_3B_CREATE_NEW_USER:
                        if (dropboxFolderExists() && userSelected()) {
                            String appPassword = txtAppPassword.getText().toString().trim();
                            if (appPasswordIsValid(appPassword)) {
                                MySettings.setAppPassword(appPassword);
                                EventBus.getDefault().post(new clsEvents.openAndSaveLabPasswordDataAsync());
                            }

                        } else {
                            MyLog.e("AppPasswordFragment", "onClick: from STATE_STEP_3B_CREATE_NEW_USER (Get Password from User). " +
                                    "Either dropbox folder does not exits OR user not selected.");
                        }
                        break;
                }*/
                break;

            case R.id.btnOkReadPasswordFile:
                String appPassword = txtAppPassword.getText().toString().trim();
                if (appPasswordIsValid(appPassword)) {
                    hideKeyBoard(txtAppPassword);
                    showProgressBarViews();
                    MySettings.setAppPassword(appPassword);
                    // DownloadDecryptDataFile
                    DropboxAPI<AndroidAuthSession> dbAPI = MainActivity.getDropboxAPI();
                    new DownloadDecryptDataFile(getActivity(), dbAPI, MySettings.getDropboxFilename(), MySettings.isVerbose()).execute();
                }
                break;


            case R.id.btnSelectDropboxFolder:
                //Toast.makeText(getActivity(), "TO COME: btnSelectDropboxFolder Click", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_DROPBOX_LIST, false));
                break;
        }

    }
    private void hideKeyBoard(EditText txt) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);
    }

/*    private boolean userSelected() {
        boolean result = false;
        int activeUserID = MySettings.getActiveUserID();
        if (activeUserID > 0) {
            result = true;
        } else {
            String title = "Please Create A New User";
            String message = "Unable to continue; no users exist!";
            MainActivity.showOkDialog(getActivity(), title, message);
        }
        return result;
    }*/

/*    private boolean dropboxFolderExists() {
        boolean result = false;
        // check that there is a valid dropbox folder
        DbxFileSystem dbxFs = MainActivity.getDbxFs();
        DbxPath folderPath = new DbxPath(MySettings.getDropboxFolderName());
        if (dbxFs != null) {
            try {
                if (dbxFs.isFolder(folderPath)) {
                    result = true;
                } else {
                    String title = "Please Select Dropbox Folder";
                    String message = "Unable to continue; the Dropbox folder does not exist!";
                    MainActivity.showOkDialog(getActivity(), title, message);
                }

            } catch (DbxException e) {
                MyLog.e("AppPasswordFragment", "dropboxFolderExists: DbxException");
                e.printStackTrace();
            }
        }
        return result;

    }*/


    private boolean appPasswordIsValid(String password) {
        boolean result = false;
        // TODO: check for valid password
        if (!password.isEmpty()) {
            result = true;
        } else {
            String title = "Invalid Password";
            String message = "No password provided!";
            EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
        }
        return result;
    }

/*
    private boolean isUnique(String newUserName) {
        boolean result = true;
        if (MainActivity.getPasswordsData() != null) {
            for (clsUsers user : MainActivity.getPasswordsData().getUsers()) {
                if (user.getUserName().equalsIgnoreCase(newUserName)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
*/

    private void showProgressBarViews() {
        tvProgressBarCaption.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setVisibility(View.GONE);
        txtAppPassword.setVisibility(View.GONE);
        btnDisplay.setVisibility(View.GONE);
        btnOK.setVisibility(View.GONE);
        btnOkReadPasswordFile.setVisibility(View.GONE);
    }


    private void showPasswordOnly() {
        //EventBus.getDefault().post(new clsEvents.showProgressInActionBar(false));
        tvProgressBarCaption.setVisibility(View.GONE);
        tvFirstTimeMessage.setVisibility(View.GONE);

        txtAppPassword.setVisibility(View.VISIBLE);
        btnDisplay.setVisibility(View.VISIBLE);
        btnOK.setVisibility(View.VISIBLE);
        btnOkReadPasswordFile.setVisibility(View.GONE);
    }


/*    private void showStep4B_StartUpViews() {
        // Get password from user
        EventBus.getDefault().post(new clsEvents.showProgressInActionBar(false));
        tvProgressBarCaption.setVisibility(View.GONE);

        tvFirstTimeMessage.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step4B));

        txtAppPassword.setVisibility(View.VISIBLE);
        btnDisplay.setVisibility(View.VISIBLE);
        btnOK.setVisibility(View.VISIBLE);
        btnOkReadPasswordFile.setVisibility(View.GONE);
    }*/


    private class passwordsDataFileExists extends AsyncTask<Void, Void, Boolean> {

        private DropboxAPI<AndroidAuthSession> mDBApi;
        private String mDropboxFullFilename;
        private Context mContext;

        public passwordsDataFileExists(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyLog.i("passwordsDataFileExists", "onPreExecute");
            showProgressBarViews();
            mDBApi = MainActivity.getDropboxAPI();
            mDropboxFullFilename = MySettings.getDropboxFilename();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            MyLog.i("passwordsDataFileExists", "doInBackground");
            boolean result = false;
            try {
                DropboxAPI.Entry existingEntry = mDBApi.metadata(mDropboxFullFilename, 1, null, false, null);
                if (existingEntry != null && existingEntry.bytes > 0 && !existingEntry.isDeleted) {
                    result = true;
                }
            } catch (DropboxException e) {
                MyLog.e("ServiceHandler", "checkForRevisedDataFile: DropboxException " + e.getMessage());
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean dataFileExists) {
            super.onPostExecute(dataFileExists);
            MyLog.i("passwordsDataFileExists", "onPostExecute: dataFileExists = " + dataFileExists);
            if (dataFileExists) {
                // Heading down "Path A"
                // Get password from user
                // set the next step in the initial startup process
                MySettings.setStartupState(AppPasswordFragment.STATE_STEP_3A_GET_APP_PASSWORD);
                showStep3A_StartUpViews();
            } else {
                // Heading down "Path B"
                // set the next step in the initial startup process
                MySettings.setStartupState(STATE_STEP_3B_CREATE_NEW_USER);
                // Create new user...show FRAG_USER_SETTINGS
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_USER_SETTINGS, false));
            }
        }

        private void showStep3A_StartUpViews() {
            // Get password from user
            EventBus.getDefault().post(new clsEvents
                    .setActionBarTitle(mContext.getString(R.string.actionBarTitle_gettingStarted)));
            tvProgressBarCaption.setVisibility(View.GONE);

            tvFirstTimeMessage.setVisibility(View.VISIBLE);
            tvFirstTimeMessage.setText(mContext.getResources().getString(R.string.tvFirstTimeMessage_text_Step3A));

            txtAppPassword.setVisibility(View.VISIBLE);
            btnDisplay.setVisibility(View.VISIBLE);
            btnOK.setVisibility(View.GONE);
            btnOkReadPasswordFile.setVisibility(View.VISIBLE);
        }
    }
}
