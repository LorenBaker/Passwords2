package com.lbconsulting.password2.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.services.UpdateService;

import de.greenrobot.event.EventBus;

public class fragSettings_networking extends Fragment implements View.OnClickListener {


    private Button btnNetworkPreferences;
    private Button btnSyncPeriodicity;

    // Strings to Show In Dialog with Radio Buttons
    private String[] mSyncPreferenceList;
    private int mNetworkPreference;
    private String[] mUpdatePeriodicity_list;
    private int mSelectedPeriodicityPosition;

    public fragSettings_networking() {
        // Required empty public constructor
    }

    public static fragSettings_networking newInstance() {
        return new fragSettings_networking();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("fragSettings_networking", "onCreate()");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("fragSettings_networking", "onCreateView()");
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
        MyLog.i("fragSettings_networking", "onActivityCreated()");
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
        MyLog.i("fragSettings_networking", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragSettings_networking", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_SETTINGS_NETWORKING);
        updateUI();
    }

    private void updateUI() {
        try {
            mNetworkPreference = MySettings.getNetworkPreference();
            btnNetworkPreferences.setText(getActivity().getString(R.string.btnNetworkPreferences_text)
                    + mSyncPreferenceList[mNetworkPreference]);

            int mUpdatePeriodicityMinutes = MySettings.getSyncPeriodicityMinutes();

            switch (mUpdatePeriodicityMinutes) {
                case MySettings.NETWORK_UPDATE_1_MIN:
                    mSelectedPeriodicityPosition = 0;
                    break;

                case MySettings.NETWORK_UPDATE_5_MIN:
                    mSelectedPeriodicityPosition = 1;
                    break;

                case MySettings.NETWORK_UPDATE_10_MIN:
                    mSelectedPeriodicityPosition = 2;
                    break;

                case MySettings.NETWORK_UPDATE_20_MIN:
                    mSelectedPeriodicityPosition = 3;
                    break;

                case MySettings.NETWORK_UPDATE_30_MIN:
                    mSelectedPeriodicityPosition = 4;
                    break;
            }
            btnSyncPeriodicity.setText(getActivity().getString(R.string.btnSyncPeriodicity_text)
                    + mUpdatePeriodicity_list[mSelectedPeriodicityPosition].toLowerCase());
        } catch (Exception e) {
            MyLog.e("fragSettings_networking", "updateUI: Exception. " + e.getMessage());
            e.printStackTrace();
        }
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
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_HOME, false));
                return true;

            default:
                // Not implemented here
                return false;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragSettings_networking", "onPause()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("fragSettings_networking", "onDestroy()");
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
        builder.setSingleChoiceItems(mUpdatePeriodicity_list, mSelectedPeriodicityPosition, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                mSelectedPeriodicityPosition = position;
                switch (position) {
                    case 0:
                        // NETWORK_UPDATE_1_MIN
                        MySettings.setSyncPeriodicity(1);
                        break;

                    case 1:
                        // NETWORK_UPDATE_5_MIN
                        MySettings.setSyncPeriodicity(5);
                        break;

                    case 2:
                        // NETWORK_UPDATE_10_MIN
                        MySettings.setSyncPeriodicity(10);
                        break;

                    case 3:
                        // NETWORK_UPDATE_20_MIN
                        MySettings.setSyncPeriodicity(20);
                        break;

                    case 4:
                        // NETWORK_UPDATE_30_MIN
                        MySettings.setSyncPeriodicity(30);
                        break;
                }


                btnSyncPeriodicity.setText(getActivity().getString(R.string.btnSyncPeriodicity_text)
                        + mUpdatePeriodicity_list[position].toLowerCase());

                // restart Passwords update service so the new periodicity can take effect.
                Intent stopIntent = new Intent(getActivity(), UpdateService.class);
                getActivity().stopService(stopIntent);

                Intent startIntent = new Intent(getActivity(), UpdateService.class);
                getActivity().startService(startIntent);
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
