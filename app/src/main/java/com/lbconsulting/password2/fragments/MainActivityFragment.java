package com.lbconsulting.password2.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lbconsulting.password2.R;
import com.lbconsulting.password2.database.UsersTable;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        Button btnTest = (Button) rootView.findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testCreateNewUser();
            }
        });
        
        return  rootView;
                
    }

    private void testCreateNewUser() {
        long LorenBakerID = UsersTable.CreateNewUser(getActivity(), "LorenBaker");
        long PattyBakerID = UsersTable.CreateNewUser(getActivity(), "PattyBaker");
        long DaleBakerID = UsersTable.CreateNewUser(getActivity(), "DaleBaker");

        String LorenBakerName = UsersTable.getUserName(getActivity(), LorenBakerID);
        String PattyBakerName = UsersTable.getUserName(getActivity(), PattyBakerID);
        String DaleBakerName = UsersTable.getUserName(getActivity(), DaleBakerID);

        int lorenRecords = UsersTable.updateUserName(getActivity(), LorenBakerID, "LOREN");
        int pattyRecords = UsersTable.updateUserName(getActivity(), PattyBakerID, "Patty");
        int daleRecords = UsersTable.updateUserName(getActivity(), DaleBakerID, "Dale");

        LorenBakerName = UsersTable.getUserName(getActivity(), LorenBakerID);
        PattyBakerName = UsersTable.getUserName(getActivity(), PattyBakerID);
        DaleBakerName = UsersTable.getUserName(getActivity(), DaleBakerID);


        String temp = "";
    }
}
