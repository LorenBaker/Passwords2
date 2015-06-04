package com.lbconsulting.password2.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.adapters.NetworkLogCursorAdapter;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.database.NetworkLogTable;

import de.greenrobot.event.EventBus;

/**
 * This fragment shows lists of Password Items
 */
public class fragNetworkLog extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {


    private  static final int NETWORK_LOG = 20;

    private ListView lvNetworkLog;

    private LoaderManager mLoaderManager = null;
    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<Cursor> mNetworkLogCallbacks;
    private NetworkLogCursorAdapter mNetworkLogCursorAdapter;

    public fragNetworkLog() {

    }

    public static fragNetworkLog newInstance() {
        return new fragNetworkLog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("fragNetworkLog", "onCreate()");
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragNetworkLog", "onActivityCreated()");

        MySettings.setOnSaveInstanceState(false);

        MySettings.setActiveFragmentID(MySettings.FRAG_NETWORK_LOG);

        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(NETWORK_LOG, null, mNetworkLogCallbacks);

        EventBus.getDefault().post(new clsEvents.setActionBarTitle("Network Log"));

        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("fragNetworkLog", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("fragNetworkLog", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_network_log, container, false);

        lvNetworkLog = (ListView) rootView.findViewById(R.id.lvNetworkLog);
        mNetworkLogCursorAdapter = new NetworkLogCursorAdapter(getActivity(), null, 0, "NetworkLog");
        mNetworkLogCallbacks = this;
        lvNetworkLog.setAdapter(mNetworkLogCursorAdapter);
        return rootView;
    }


    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("fragNetworkLog", "onEvent.updateUI()");
        updateUI();
    }

    private void updateUI() {
        mLoaderManager.restartLoader(NETWORK_LOG, null, mNetworkLogCallbacks);
    }


    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("fragNetworkLog", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_NETWORK_LOG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragNetworkLog", "onPause()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("fragNetworkLog", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_network_log, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_purge_log:
                Toast.makeText(getActivity(), "TO COME: action_purge_log", Toast.LENGTH_SHORT).show();
                return true;

            case android.R.id.home:
                EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_HOME, false));
                return true;

            default:
                // Not implemented here
                return false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader = null;
        String sortOrder = NetworkLogTable.SORT_ORDER_DATE;
        switch (id) {
            case NETWORK_LOG:
                MyLog.i("fragNetworkLog", "onCreateLoader. Loading NETWORK_LOG");
                cursorLoader = NetworkLogTable.getAllLogsCursorLoader(getActivity(), sortOrder);
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        // The asynchronous load is complete and the newCursor is now available for use.
        switch (loader.getId()) {
            case NETWORK_LOG:
                MyLog.i("fragNetworkLog", "onLoadFinished NETWORK_LOG");
                mNetworkLogCursorAdapter.swapCursor(newCursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case NETWORK_LOG:
                MyLog.i("fragNetworkLog", "onLoaderReset USER_CREDIT_CARD_ITEMS");
                mNetworkLogCursorAdapter.swapCursor(null);
                break;
        }
    }
}
