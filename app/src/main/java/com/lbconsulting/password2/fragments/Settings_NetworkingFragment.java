package com.lbconsulting.password2.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes.clsUtils;

import de.greenrobot.event.EventBus;

public class Settings_NetworkingFragment extends Fragment implements View.OnClickListener {


    private Button btnNetworkPreferences;
    private Button btnSyncPeriodicity;

    // Strings to Show In Dialog with Radio Buttons
    private  String[] mSyncPreferenceList;
    private int mNetworkPreference;
    private  String[] mUpdatePeriodicity_list;
    private int mUpdatePeriodicity;

    public Settings_NetworkingFragment() {
        // Required empty public constructor
    }

    public static Settings_NetworkingFragment newInstance() {
        return new Settings_NetworkingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("Settings_NetworkingFragment", "onCreate()");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("Settings_NetworkingFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_settings_networking, container, false);

        btnNetworkPreferences = (Button) rootView.findViewById(R.id.btnNetworkPreferences);
        btnSyncPeriodicity = (Button) rootView.findViewById(R.id.btnSyncPeriodicity);

        btnNetworkPreferences.setOnClickListener(this);
        btnSyncPeriodicity.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("Settings_NetworkingFragment", "onActivityCreated()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            MySettings.setOnSaveInstanceState(false);
        }

        EventBus.getDefault().post(new clsEvents
                .setActionBarTitle("Networking Settings"));

        mSyncPreferenceList = getActivity().getResources().getStringArray(R.array.syncPreference_list);
        mUpdatePeriodicity_list = getActivity().getResources().getStringArray(R.array.updatePeriodicity_list);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("Settings_NetworkingFragment", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("Settings_NetworkingFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_SETTINGS);
        updateUI();
    }

    private void updateUI() {
        mNetworkPreference = MySettings.getNetworkPreference();
        btnNetworkPreferences.setText(getActivity().getString(R.string.btnNetworkPreferences_text)
                + mSyncPreferenceList[mNetworkPreference]);

        mUpdatePeriodicity=MySettings.getSyncPeriodicity();
        btnSyncPeriodicity.setText(getActivity().getString(R.string.btnSyncPeriodicity_text)
                + mUpdatePeriodicity_list[mUpdatePeriodicity].toLowerCase());
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
        MyLog.i("Settings_NetworkingFragment", "onPause()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("Settings_NetworkingFragment", "onDestroy()");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnNetworkPreferences:
                //Toast.makeText(getActivity(), "btnNetworkPreferences Clicked", Toast.LENGTH_SHORT).show();
                showNetworkPreferenceDialog();
                break;

            case R.id.btnSyncPeriodicity:
                //Toast.makeText(getActivity(), "btnSyncPeriodicity Clicked", Toast.LENGTH_SHORT).show();
                showSyncPeriodicityDialog();
                break;

        }

    }

    private void showSyncPeriodicityDialog() {
        // Creating and Building the Dialog
        Dialog syncPeriodicityDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Check for updates");
        builder.setSingleChoiceItems(mUpdatePeriodicity_list, mUpdatePeriodicity, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                MySettings.setSyncPeriodicity(position);
                btnSyncPeriodicity.setText(getActivity().getString(R.string.btnSyncPeriodicity_text)
                        + mUpdatePeriodicity_list[position].toLowerCase());
            }

        });

        // Set the action buttons
        builder.setPositiveButton(R.string.btnOK_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // dialog dismissed
            }
        });

        syncPeriodicityDialog = builder.create();
        syncPeriodicityDialog.show();

    }

    private void showNetworkPreferenceDialog() {
        // Creating and Building the Dialog
        Dialog networkPreferenceDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Use network");
        builder.setSingleChoiceItems(mSyncPreferenceList, mNetworkPreference, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                MySettings.setNetworkPreference(position);
                btnNetworkPreferences.setText(getActivity().getString(R.string.btnNetworkPreferences_text)
                        + mSyncPreferenceList[position]);

                // Update setIsOkToUseNetwork
                clsUtils.setIsOkToUseNetwork(getActivity());
            }

        });

        // Set the action buttons
        builder.setPositiveButton(R.string.btnOK_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // dialog dismissed
            }
        });

        networkPreferenceDialog = builder.create();
        networkPreferenceDialog.show();
    }


}
