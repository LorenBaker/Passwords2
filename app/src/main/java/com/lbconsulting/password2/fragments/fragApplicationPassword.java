package com.lbconsulting.password2.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.lbconsulting.password2.R;
import com.lbconsulting.password2.activities.MainActivity;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsUtils;
import com.lbconsulting.password2.classes_async.DownloadDecryptDataFile;
import com.lbconsulting.password2.services.PasswordsUpdateService;

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
 * 3B	Create new user (executed in FRAG_SETTINGS_USER)
 * 4B	Create app password (executed in FRAG_SETTINGS_APP_PASSWORD)
 * 5B	Save new data file
 */

public class fragApplicationPassword extends Fragment implements View.OnClickListener {

    // fragment state variables
    private boolean mShowPasswordText = false;
    // private int mLastActiveFragmentID;

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
    //public static final int STATE_STEP_4A_READ_FILE = 41;
    public static final int STATE_STEP_5A_SELECT_USER = 51;

    public static final int STATE_STEP_3B_CREATE_NEW_USER = 32;
    public static final int STATE_STEP_4B_CREATE_APP_PASSWORD = 42;
    //private static final int STATE_STEP_5B_SAVE_FILE = 52;

    public static final int STATE_PASSWORD_ONLY = 60;
    public static final int STATE_VALIDATING_PASSWORD = 70;

    public static fragApplicationPassword newInstance() {
        return new fragApplicationPassword();
    }

    public fragApplicationPassword() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("fragApplicationPassword", "onCreate()");
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("fragApplicationPassword", "onCreateView()");
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
        MyLog.i("fragApplicationPassword", "onActivityCreated()");
        MySettings.setOnSaveInstanceState(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("fragApplicationPassword", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragApplicationPassword", "onResume()");
        // don't save this fragment as the Active fragment.
        updateUI();
    }

    private void updateUI() {
        txtAppPassword.setText("");
        switch (MySettings.getStartupState()) {
            case STATE_PASSWORD_ONLY:
                MyLog.i("fragApplicationPassword", "updateUI(): State = STATE_PASSWORD_ONLY");
                MySettings.setStartupState(STATE_VALIDATING_PASSWORD);
                showPasswordOnly();
                break;

            case STATE_VALIDATING_PASSWORD:
                MyLog.i("fragApplicationPassword", "updateUI(): State = STATE_VALIDATING_PASSWORD");
                showPasswordOnly();
                break;

            case STATE_STEP_1_SELECT_FOLDER:
                MyLog.i("fragApplicationPassword", "updateUI(): State = STATE_STEP_1_SELECT_FOLDER");
                // Select dropbox folder
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_SETTINGS, false));
                break;

            case STATE_STEP_2_DOES_FILE_EXIST:
                MyLog.i("fragApplicationPassword", "updateUI(): State = STATE_STEP_2_DOES_FILE_EXIST");
                // Select user
                new passwordsDataFileExists(getActivity()).execute();
                // Continues in passwordsDataFileExists onPostExecute()
                break;

            case STATE_STEP_3A_GET_APP_PASSWORD:
                showStep3A_StartUpViews();
                break;
        }
    }

    public void onEvent(clsEvents.onPasswordsDatabaseUpdated event) {
        // Startup step 4A complete - Passwords database successfully updated
        // Proceed with startup step 5A - Select user
        MySettings.setStartupState(STATE_STEP_5A_SELECT_USER);
        EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_SETTINGS, false));
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragApplicationPassword", "onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MyLog.i("fragApplicationPassword", "onDestroy()");
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
                String appPassword = txtAppPassword.getText().toString().trim();
                if (clsUtils.appPasswordHasValidSyntax(getActivity(), appPassword)) {

                    if (MySettings.isPasswordValid(appPassword)) {
                        hideKeyBoard(txtAppPassword);
                        MySettings.setAppPassword(appPassword);
                        MySettings.setStartupState(STATE_PASSWORD_ONLY);
                        startPasswordsUpdateService();
                        EventBus.getDefault().post(new clsEvents.showFragment(MySettings.getActiveFragmentID(), false));
                    } else {
                        String title = "Invalid Password";
                        String message = "The provided password cannot decrypt the data file!\n\nPlease provide a valid password.";
                        EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
                    }
                }

                break;

            case R.id.btnOkReadPasswordFile:
                appPassword = txtAppPassword.getText().toString().trim();
                if (clsUtils.appPasswordHasValidSyntax(getActivity(), appPassword)) {

                    // if unable to read the data file ... then setEncryptionTestText is reset
                    MySettings.setEncryptionTestText(appPassword);
                    hideKeyBoard(txtAppPassword);
                    showProgressBarViews();
                    MySettings.setAppPassword(appPassword);
                    // DownloadDecryptDataFile
                    DropboxAPI<AndroidAuthSession> dbAPI = MainActivity.getDropboxAPI();
                    new DownloadDecryptDataFile(getActivity(), dbAPI, MySettings.getDropboxFilename(), MySettings.isVerbose()).execute();
                }
                break;

            case R.id.btnSelectDropboxFolder:
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_DROPBOX_LIST, false));
                break;
        }
    }

    private void startPasswordsUpdateService() {
        Intent intent = new Intent(getActivity(), PasswordsUpdateService.class);
        getActivity().startService(intent);
    }

    private void hideKeyBoard(EditText txt) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txt.getWindowToken(), 0);
    }

    private void showProgressBarViews() {
        tvProgressBarCaption.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setVisibility(View.GONE);
        txtAppPassword.setVisibility(View.GONE);
        btnDisplay.setVisibility(View.GONE);
        btnOK.setVisibility(View.GONE);
        btnOkReadPasswordFile.setVisibility(View.GONE);
    }

    private void showPasswordOnly() {
        tvProgressBarCaption.setVisibility(View.GONE);
        tvFirstTimeMessage.setVisibility(View.GONE);

        txtAppPassword.setVisibility(View.VISIBLE);
        btnDisplay.setVisibility(View.VISIBLE);
        btnOK.setVisibility(View.VISIBLE);
        btnOkReadPasswordFile.setVisibility(View.GONE);
    }

    private void showStep3A_StartUpViews() {
        // Get password from user
        EventBus.getDefault().post(new clsEvents
                .setActionBarTitle(getString(R.string.actionBarTitle_gettingStarted)));
        tvProgressBarCaption.setVisibility(View.GONE);

        tvFirstTimeMessage.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step3A));

        txtAppPassword.setVisibility(View.VISIBLE);
        btnDisplay.setVisibility(View.VISIBLE);
        btnOK.setVisibility(View.GONE);
        btnOkReadPasswordFile.setVisibility(View.VISIBLE);
    }

    private class passwordsDataFileExists extends AsyncTask<Void, Void, Boolean> {

        private DropboxAPI<AndroidAuthSession> mDBApi;
        private String mDropboxFullFilename;
        private final Context mContext;

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
                MySettings.setStartupState(fragApplicationPassword.STATE_STEP_3A_GET_APP_PASSWORD);
                showStep3A_StartUpViews();
            } else {
                // Heading down "Path B"
                // set the next step in the initial startup process
                MySettings.setStartupState(STATE_STEP_3B_CREATE_NEW_USER);
                // Create new user...show FRAG_SETTINGS_USER
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_SETTINGS_USER, false));
            }
        }


    }
}
