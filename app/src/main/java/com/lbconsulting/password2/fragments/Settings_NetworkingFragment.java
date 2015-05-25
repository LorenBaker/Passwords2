package com.lbconsulting.password2.fragments;


import android.app.Fragment;
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

import de.greenrobot.event.EventBus;

public class Settings_NetworkingFragment extends Fragment implements View.OnClickListener {


    private Button btnNetworkPreferences;
    private Button btnSyncPeriodicity;


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
                Toast.makeText(getActivity(), "btnNetworkPreferences Clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnSyncPeriodicity:
                Toast.makeText(getActivity(), "btnSyncPeriodicity Clicked", Toast.LENGTH_SHORT).show();

                break;

        }

    }


}
