package com.lbconsulting.password2.fragments;


import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.classes.clsUser;
import com.lbconsulting.password2.database.ItemsTable;
import com.lbconsulting.password2.database.UsersTable;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button btnTest = (Button) rootView.findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testCreateNewUser();
            }
        });

        return rootView;

    }

    private void testCreateNewUser() {

        long user1ID = UsersTable.CreateNewUser(getActivity(), 10, "User_1");
        if (user1ID < 0) {
            showItemInsertError(user1ID);
        }
        long user2ID = UsersTable.CreateNewUser(getActivity(), 20, "User_2");
        long user3ID = UsersTable.CreateNewUser(getActivity(), 30, "User_3");

        long item1ID = ItemsTable.CreateNewItem(getActivity(), user1ID, 101, "Item_1");
        long item2ID = ItemsTable.CreateNewItem(getActivity(), user2ID, 102, "Item_2");
        long item3ID = ItemsTable.CreateNewItem(getActivity(), user3ID, 103, "Item_3");
        long item4ID = ItemsTable.CreateNewItem(getActivity(), user1ID, 104, "ITEM_4");
        long item5ID = ItemsTable.CreateNewItem(getActivity(), user2ID, 105, "Item_5");
        long item6ID = ItemsTable.CreateNewItem(getActivity(), user3ID, 106, "Item_6");
        long item7ID = ItemsTable.CreateNewItem(getActivity(), user1ID, 107, "Item_7");
        long item8ID = ItemsTable.CreateNewItem(getActivity(), user2ID, 108, "Item_8");
        long item9ID = ItemsTable.CreateNewItem(getActivity(), user3ID, 109, "Item_9");

        Cursor allUsers = UsersTable.getAllUsersCursor(getActivity(), UsersTable.SORT_ORDER_USER_NAME);
        ArrayList<clsUser> usersList = new ArrayList<>();
        if (allUsers != null) {

            clsUser user;
            while (allUsers.moveToNext()) {
                user = new clsUser(
                        allUsers.getInt(allUsers.getColumnIndex(UsersTable.COL_USER_ID)),
                        allUsers.getString(allUsers.getColumnIndex(UsersTable.COL_USER_NAME)));
                if (user != null) {
                    usersList.add(user);
                }
            }
        }


        String temp = "";
    }

    private void showItemInsertError(long longErrorCode) {
        int errorCode = (int) longErrorCode;
        switch (errorCode) {
            case ItemsTable.ILLEGAL_ITEM_ID:

            break;

        }
    }

}
