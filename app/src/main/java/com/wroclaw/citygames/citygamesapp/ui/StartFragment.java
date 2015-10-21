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
    public static final String TITLE = "Start";

    public static  StartFragment newInstance() {
        StartFragment myFragment = new StartFragment();
        return myFragment;
    }

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
        Button gamesButton = (Button) getView().findViewById(R.id.your_games_button);
        gamesButton.setOnClickListener(this);
        Button scenariosButton = (Button) getView().findViewById(R.id.scenarios_button);
        scenariosButton.setOnClickListener(this);
        Button rankButton = (Button) getView().findViewById(R.id.rank_button);
        rankButton.setOnClickListener(this);
        Button startGame = (Button) getView().findViewById(R.id.start_game_button);
        startGame.setOnClickListener(this);

        getActivity().setTitle(this.TITLE);
    }




    @Override
    public void onClick(View v) {
        Log.d(TAG,"On Click");
        int id = v.getId();
        FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        switch (id)
        {
            case R.id.start_game_button:
                tx.replace(R.id.navigation_drawer_frame, ScenariosListFragment.newInstance(true)).addToBackStack(TAG);
                tx.commit();
                break;
            case R.id.current_game_button:
                break;
            case R.id.yout_teams_button:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(getActivity(), TeamsListFragment.NAME)).addToBackStack(TAG);
                tx.commit();
                break;
            case R.id.your_games_button:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(getActivity(), GamesListFragment.NAME)).addToBackStack(TAG);
                tx.commit();
                break;
            case R.id.scenarios_button:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(getActivity(), ScenariosListFragment.NAME)).addToBackStack(TAG);
                tx.commit();
                break;
            case R.id.rank_button:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(getActivity(), RankFragment.NAME)).addToBackStack(TAG);
                tx.commit();
        }

    }
}
