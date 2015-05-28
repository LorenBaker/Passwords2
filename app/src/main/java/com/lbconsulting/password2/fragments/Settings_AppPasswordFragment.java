package com.lbconsulting.password2.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;

import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

public class Settings_AppPasswordFragment extends Fragment implements View.OnClickListener {

    private Button btnChangeAppPassword;
    private Button btnSelectPasswordLongevity;

    private EditText txtAppPassword;
    private EditText txtConfirmAppPassword;
    private Button btnSave;
    private ImageView ivCheckMark;

    private Button btnPasswordDisplay;
    private Button btnConfirmPasswordDisplay;
    private boolean mShowPasswordText = false;
    private boolean mShowConfirmPasswordText = false;

    private TextView tvFirstTimeMessage;
    private int mStartupState;


    public Settings_AppPasswordFragment() {
        // Required empty public constructor
    }

    public static Settings_AppPasswordFragment newInstance() {
        return new Settings_AppPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("Settings_AppPasswordFragment", "onCreate()");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("Settings_AppPasswordFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_settings_app_password, container, false);

        btnChangeAppPassword = (Button) rootView.findViewById(R.id.btnChangeAppPassword);
        btnSelectPasswordLongevity = (Button) rootView.findViewById(R.id.btnSelectPasswordLongevity);

        btnChangeAppPassword.setOnClickListener(this);
        btnSelectPasswordLongevity.setOnClickListener(this);

        tvFirstTimeMessage = (TextView) rootView.findViewById(R.id.tvFirstTimeMessage);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("Settings_AppPasswordFragment", "onActivityCreated()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            MySettings.setOnSaveInstanceState(false);
        }

        EventBus.getDefault().post(new clsEvents.setActionBarTitle("App Password Settings"));

        mStartupState = MySettings.getAppPasswordState();
        switch (mStartupState) {
            case AppPasswordFragment.STATE_STEP_4B_CREATE_APP_PASSWORD:
                btnChangeAppPassword.setVisibility(View.VISIBLE);
                btnSelectPasswordLongevity.setVisibility(View.GONE);
                tvFirstTimeMessage.setVisibility(View.VISIBLE);
                break;

            default:
                btnChangeAppPassword.setVisibility(View.VISIBLE);
                btnSelectPasswordLongevity.setVisibility(View.VISIBLE);
                tvFirstTimeMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("Settings_AppPasswordFragment", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("Settings_AppPasswordFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_APP_PASSWORD_SETTINGS);
        updateUI();
    }

    private void updateUI() {

        switch (mStartupState){
            case AppPasswordFragment.STATE_STEP_3B_CREATE_NEW_USER:
                tvFirstTimeMessage.setText(
                        getActivity().getString(R.string.tvFirstTimeMessage_text_Step3B)
                                + getActivity().getString(R.string.invalid_password_message1));

                btnChangeAppPassword.setText("Create Application Password");
                break;

            default:
                int passwordLongevity = (int) MySettings.getPasswordLongevity() / 60000;
                String longevityDescription = getLongevityDescription(passwordLongevity);
                btnSelectPasswordLongevity
                        .setText(getActivity().getString(R.string.btnSelectPasswordLongevity_text)
                                + longevityDescription);
        }
    }

    private String getLongevityDescription(int longevity) {
        String description;
        switch (longevity) {
            case 5:
                description = "5 min";
                break;
            case 15:
                description = "15 min";
                break;
            case 30:
                description = "30 min";
                break;
            case 60:
                description = "1 hr";
                break;
            case 240:
                description = "4 hrs";
                break;
            case 480:
                description = "8 hrs";
                break;
            default:
                description = "None";
                break;
        }
        return description;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here

            case android.R.id.home:
                EventBus.getDefault().post(new clsEvents.PopBackStack());
                return true;

            default:
                // Not implemented here
                return false;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("Settings_AppPasswordFragment", "onPause()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("Settings_AppPasswordFragment", "onDestroy()");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnChangeAppPassword:
                //Toast.makeText(getActivity(), "TO COME: btnChangeAppPassword", Toast.LENGTH_SHORT).show();

                // custom dialog
                final Dialog changePasswordDialog = new Dialog(getActivity());
                changePasswordDialog.setContentView(R.layout.dialog_app_change_password);

                switch (mStartupState){
                    case AppPasswordFragment.STATE_STEP_4B_CREATE_APP_PASSWORD:
                        changePasswordDialog.setTitle("Create Password");
                        break;

                    default:
                        changePasswordDialog.setTitle("Change Password");
                }

                // set the custom dialog components - text, image and button
                txtAppPassword = (EditText) changePasswordDialog.findViewById(R.id.txtAppPassword);
                txtConfirmAppPassword = (EditText) changePasswordDialog.findViewById(R.id.txtConfirmAppPassword);
                ivCheckMark = (ImageView) changePasswordDialog.findViewById(R.id.ivCheckMark);
                ivCheckMark.setImageResource(R.drawable.btn_check_buttonless_off);

                txtAppPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        validatePasswordsAreTheSame();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                txtConfirmAppPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        validatePasswordsAreTheSame();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                btnPasswordDisplay = (Button) changePasswordDialog.findViewById(R.id.btnPasswordDisplay);
                btnConfirmPasswordDisplay = (Button) changePasswordDialog.findViewById(R.id.btnConfirmPasswordDisplay);

                btnPasswordDisplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mShowPasswordText) {
                            txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);

                            btnPasswordDisplay.setText(getString(R.string.btnDisplay_setText_Display));
                        } else {
                            txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            btnPasswordDisplay.setText(getString(R.string.btnDisplay_setText_Hide));
                        }
                        txtAppPassword.setSelection(txtAppPassword.getText().length());
                        mShowPasswordText = !mShowPasswordText;
                    }
                });

                btnConfirmPasswordDisplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mShowConfirmPasswordText) {
                            txtConfirmAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);

                            btnConfirmPasswordDisplay.setText(getString(R.string.btnDisplay_setText_Display));
                        } else {
                            txtConfirmAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            btnConfirmPasswordDisplay.setText(getString(R.string.btnDisplay_setText_Hide));
                        }
                        txtConfirmAppPassword.setSelection(txtConfirmAppPassword.getText().length());
                        mShowConfirmPasswordText = !mShowConfirmPasswordText;
                    }
                });


                Button btnCancel = (Button) changePasswordDialog.findViewById(R.id.btnCancel);
                switch (mStartupState) {
                    case AppPasswordFragment.STATE_STEP_4B_CREATE_APP_PASSWORD:
                        btnCancel.setVisibility(View.INVISIBLE);
                        break;

                    default:
                        btnCancel.setVisibility(View.VISIBLE);
                }


                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // do nothing, close the dialog
                        changePasswordDialog.dismiss();
                    }
                });

                btnSave = (Button) changePasswordDialog.findViewById(R.id.btnSave);
                btnSave.setEnabled(false);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String appPassword = txtAppPassword.getText().toString().trim();
                        if (appPasswordIsValid(appPassword)) {
                            MySettings.setAppPassword(appPassword);
                            Toast.makeText(getActivity(), "Password \"" + appPassword + "\" saved.", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
                            changePasswordDialog.dismiss();
                        }
                    }
                });

                changePasswordDialog.show();

                break;

            case R.id.btnSelectPasswordLongevity:
                //Toast.makeText(getActivity(), "TO COME: btnSelectPasswordLongevity", Toast.LENGTH_SHORT).show();

                // Strings to Show In Dialog with Radio Buttons
                final String[] items = getActivity().getResources().getStringArray(R.array.longevityItems_list);
                long passwordLongevity = MySettings.getPasswordLongevity();
                int longevity = (int) passwordLongevity / 60000;
                int selectedLongevityPosition;
                switch (longevity) {
                    case 5:
                        selectedLongevityPosition = 1;
                        break;
                    case 15:
                        selectedLongevityPosition = 2;
                        break;
                    case 30:
                        selectedLongevityPosition = 3;
                        break;
                    case 60:
                        selectedLongevityPosition = 4;
                        break;
                    case 240:
                        selectedLongevityPosition = 5;
                        break;
                    case 480:
                        selectedLongevityPosition = 6;
                        break;
                    default:
                        selectedLongevityPosition = 0;
                        break;
                }

                // Creating and Building the Dialog
                Dialog longevityDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getActivity().getString(R.string.PasswordLongevityDialog_title));
                builder.setSingleChoiceItems(items, selectedLongevityPosition, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        int newLongevity = 5;

                        switch (position) {
                            case 0:
                                newLongevity = -1;
                                break;

                            case 1:
                                newLongevity = 5;
                                break;
                            case 2:
                                newLongevity = 15;
                                break;
                            case 3:
                                newLongevity = 30;
                                break;
                            case 4:
                                newLongevity = 60;
                                break;
                            case 5:
                                newLongevity = 240;
                                break;
                            case 6:
                                newLongevity = 480;
                                break;
                        }

                        String newLongevityDescription = getLongevityDescription(newLongevity);
                        long longevity = newLongevity * 60000;
                        MySettings.setPasswordLongevity(longevity);
                        btnSelectPasswordLongevity.setText(getActivity()
                                .getString(R.string.btnSelectPasswordLongevity_text)
                                + newLongevityDescription);
                    }

                });

                // Set the action buttons
                builder.setPositiveButton(R.string.btnOK_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog dismissed
                    }
                });

                longevityDialog = builder.create();
                longevityDialog.show();
                break;
        }

    }

    private void validatePasswordsAreTheSame() {
        String password = txtAppPassword.getText().toString().trim();
        String confirmAppPassword = txtConfirmAppPassword.getText().toString().trim();

        if (password.equals(confirmAppPassword) && !password.isEmpty()) {
            btnSave.setEnabled(true);
            ivCheckMark.setImageResource(R.drawable.btn_check_buttonless_on);
        } else {
            btnSave.setEnabled(false);
            ivCheckMark.setImageResource(R.drawable.btn_check_buttonless_off);
        }
    }

    private boolean appPasswordIsValid(String password) {

        // TODO: Add more tests for a valid password ??
        String title = "Invalid Password";
        String message = getActivity().getString(R.string.invalid_password_message1);
        if (password.isEmpty()) {
            message = getActivity().getString(R.string.invalid_password_message2) + message;
            EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
            return false;
        }

        if (password.length() < 10) {
            message = getActivity().getString(R.string.invalid_password_message3) + password.length()
                    + getActivity().getString(R.string.invalid_password_message4) + message;
            EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
            return false;
        }

        int combinations = 0;
        if (Pattern.compile("[0-9]").matcher(password).find()) {
            combinations = combinations + 10;
        }

        if (Pattern.compile("[a-z]").matcher(password).find()) {
            combinations = combinations + 26;
        }

        if (Pattern.compile("[A-Z]").matcher(password).find()) {
            combinations = combinations + 26;
        }

        if (combinations == 62) {
            return true;
        } else {
            EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
            return false;
        }


    }
}
