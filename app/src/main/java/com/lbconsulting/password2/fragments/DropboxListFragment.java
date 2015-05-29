package com.lbconsulting.password2.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.lbconsulting.password2.R;
import com.lbconsulting.password2.activities.MainActivity;
import com.lbconsulting.password2.adapters.DropboxFoldersListViewAdapter;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsDropboxFolder;
import com.lbconsulting.password2.classes.clsEvents;
import com.lbconsulting.password2.classes_async.CreateNewDropboxFolder;
import com.lbconsulting.password2.classes_async.DownloadDropboxFolders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.greenrobot.event.EventBus;


/**
 * This fragment show dropbox folders
 */
public class DropboxListFragment extends Fragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {


    private TextView tvFolderName;
    private ProgressBar pbDropboxList;
    private ListView lvFolders;

    private Button btnCancel;

    private DropboxAPI<AndroidAuthSession> mDBApi;
    private HashMap<String, clsDropboxFolder> mFolderHashMap;
    private String mSelectedFolderPath;

    private int mStartupState;

    public DropboxListFragment() {

    }

    public static DropboxListFragment newInstance() {
        return new DropboxListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("DropboxListFragment", "onCreate");
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("DropboxListFragment", "onActivityCreated()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        EventBus.getDefault().register(this);
        mDBApi = MainActivity.getDropboxAPI();
        mStartupState = MySettings.getStartupState();
        mFolderHashMap = new HashMap<>();
        mSelectedFolderPath = "/";
        new DownloadDropboxFolders(getActivity(), mDBApi, mSelectedFolderPath, mFolderHashMap).execute();
        showProgressBar();

        MySettings.setOnSaveInstanceState(false);
    }

    private void showProgressBar() {
        pbDropboxList.setVisibility(View.VISIBLE);
        lvFolders.setVisibility(View.INVISIBLE);
    }

    private void hideProgressBar() {
        pbDropboxList.setVisibility(View.GONE);
        lvFolders.setVisibility(View.VISIBLE);
    }

    public void onEvent(clsEvents.folderHashMapUpdated event) {
        updateUI();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("DropboxListFragment", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("DropboxListFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_dropbox_list, container, false);

        tvFolderName = (TextView) rootView.findViewById(R.id.tvFolderName);
        pbDropboxList = (ProgressBar) rootView.findViewById(R.id.pbDropboxList);

        lvFolders = (ListView) rootView.findViewById(R.id.lvFolders);
        lvFolders.setOnItemClickListener(this);

        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        Button btnSelect = (Button) rootView.findViewById(R.id.btnSelect);
        btnCancel.setOnClickListener(this);
        btnSelect.setOnClickListener(this);

        return rootView;
    }

    private void updateUI() {
        if (mStartupState == AppPasswordFragment.STATE_STEP_1_SELECT_FOLDER) {
            btnCancel.setEnabled(false);
        }
        hideProgressBar();
        if (mFolderHashMap != null && mFolderHashMap.containsKey(mSelectedFolderPath)) {
            clsDropboxFolder selectedFolder = mFolderHashMap.get(mSelectedFolderPath);
            String selectedFolderDisplayName = selectedFolder.getFolderDisplayName();

            Collections.sort(selectedFolder.getChildren(), new Comparator<clsDropboxFolder>() {
                @Override
                public int compare(clsDropboxFolder folder1, clsDropboxFolder folder2) {
                    return folder1.getFolderDisplayName().toUpperCase().compareTo(folder2.getFolderDisplayName().toUpperCase());
                }
            });

            tvFolderName.setText(selectedFolderDisplayName);

            ArrayList<clsDropboxFolder> displayList = cloneList(selectedFolder.getChildren());

            if (!selectedFolderDisplayName.equals("Dropbox")) {
                clsDropboxFolder upArrowFolder = new clsDropboxFolder(selectedFolder.getFolderParentPath(), clsDropboxFolder.UP_ARROW_ICON);
                displayList.add(0, upArrowFolder);
            }

            DropboxFoldersListViewAdapter mDropboxFoldersListViewAdapter =
                    new DropboxFoldersListViewAdapter(getActivity(), displayList);
            lvFolders.setAdapter(mDropboxFoldersListViewAdapter);
        } else {
            String msg = "Unable to load folder. mFolderHashMap does not contain \"" + mSelectedFolderPath + "\".";
            MyLog.e("DropboxListFragment", "updateUI: " + msg);
        }

    }

    private ArrayList<clsDropboxFolder> cloneList(ArrayList<clsDropboxFolder> list) {
        ArrayList<clsDropboxFolder> clone = new ArrayList<>(list.size());
        for (clsDropboxFolder item : list) clone.add(item);
        return clone;
    }

    @Override
    public void onResume() {
        MyLog.i("DropboxListFragment", "onResume()");
        super.onResume();
        MySettings.setActiveFragmentID(MySettings.FRAG_DROPBOX_LIST);
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("DropboxListFragment", "onPause()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("DropboxListFragment", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnCancel:
                //Toast.makeText(getActivity(), "btnCancel Clicked", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new clsEvents.PopBackStack());
                break;

            case R.id.btnSelect:
                //Toast.makeText(getActivity(), "btnSelect Clicked", Toast.LENGTH_SHORT).show();
                selectFolder(mSelectedFolderPath);
                break;

        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tvFolderName = (TextView) view.findViewById(R.id.tvFolderName);
        if (tvFolderName != null) {
            clsDropboxFolder dropboxFolder = (clsDropboxFolder) tvFolderName.getTag();
            if (dropboxFolder != null) {
                if (dropboxFolder.isUpFolder()) {
                    mSelectedFolderPath = dropboxFolder.getFolderPath();
                    updateUI();
                } else {
                    mSelectedFolderPath = dropboxFolder.getFolderPath();
                    if (mFolderHashMap.containsKey(mSelectedFolderPath)) {
                        updateUI();
                    } else {
                        new DownloadDropboxFolders(getActivity(), mDBApi, dropboxFolder.getHashMapKey(), mFolderHashMap).execute();
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_dropbox_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here
            case R.id.action_new:
                //Toast.makeText(getActivity(), "action_new Clicked", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder newDropboxFolderDialog = new AlertDialog.Builder(getActivity());

                newDropboxFolderDialog.setTitle(getActivity().getString(R.string.newDropboxFolderDialog_title));
                newDropboxFolderDialog.setMessage("");

                // Set an EditText view to get user input
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.dialog_edit_text, null);
                final EditText txtNewFolderName = (EditText) view.findViewById(R.id.txtBox);
                txtNewFolderName.setHint(getActivity().getString(R.string.newDropboxFolderEditText_hint));
                newDropboxFolderDialog.setView(view);

                newDropboxFolderDialog.setPositiveButton(getActivity()
                                .getString(R.string.newDropboxFolderDialogPositiveButton_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newFolderName = txtNewFolderName.getText().toString().trim();

                                // TODO: Can you make a new Dropbox folder if you're off line?
                                new CreateNewDropboxFolder(getActivity(), mDBApi, mSelectedFolderPath, newFolderName).execute();
                                dialog.dismiss();
                                EventBus.getDefault().post(new clsEvents.PopBackStack());
                            }
                        });

                newDropboxFolderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.dismiss();
                    }
                });

                newDropboxFolderDialog.show();
                return true;

            case android.R.id.home:
                EventBus.getDefault().post(new clsEvents.PopBackStack());
                return true;

            default:
                // Not implemented here
                return false;
        }
    }

    private void selectFolder(String newFolderPath) {
        MySettings.setDropboxFolderName(newFolderPath);

        if (MySettings.getStartupState() == AppPasswordFragment.STATE_STEP_1_SELECT_FOLDER) {
            // set the next step in the initial startup process
            MySettings.setStartupState(AppPasswordFragment.STATE_STEP_2_DOES_FILE_EXIST);
            // return to FRAG_APP_PASSWORD
            EventBus.getDefault().post(new clsEvents.showFragment(MySettings.FRAG_APP_PASSWORD, false));

        } else {
            EventBus.getDefault().post(new clsEvents.PopBackStack());
        }
    }


}
