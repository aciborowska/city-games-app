package com.wroclaw.citygames.citygamesapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wroclaw.citygames.citygamesapp.R;


public class StartFragment extends Fragment implements View.OnClickListener{

    public static final String TAG = StartFragment.class.getName();
    public static final String NAME = StartFragment.class.getCanonicalName();
    public StartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        Button teamsButton = (Button) getView().findViewById(R.id.yout_teams_button);
        teamsButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG,"On Click");
        int id = v.getId();
        FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        switch(id){
            case R.id.yout_teams_button:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(getActivity(), TeamsListFragment.NAME));
                tx.commit();
                break;
            case R.id.your_games_button:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(getActivity(), GamesListFragment.NAME));
                tx.commit();
                break;

        }
    }
}
