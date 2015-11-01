package com.wroclaw.citygames.citygamesapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Scenario;

public class ScenarioDetailsFragment extends Fragment {
    public static final String NAME = ScenarioDetailsFragment.class.getCanonicalName();
    public static final String TAG = ScenarioDetailsFragment.class.getName();
    public static final String TITLE = "Scenariusz";

    private Long scenarioId;

    public static  ScenarioDetailsFragment newInstance(Long scenarioId) {
        ScenarioDetailsFragment myFragment = new ScenarioDetailsFragment();
        Bundle args = new Bundle();
        args.putLong("scenarioId", scenarioId);
        myFragment.setArguments(args);
        return myFragment;
    }

    public ScenarioDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scenario_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        handleIntent();
        Scenario scenario = App.getScenarioDao().get(scenarioId);
        if(scenario!=null){
            TextView name = (TextView) getView().findViewById(R.id.scenario_details_name);
            name.setText(scenario.getName());
            TextView description = (TextView) getView().findViewById(R.id.scenario_details_description);
            description.setText(scenario.getDescription());
            TextView level = (TextView) getView().findViewById(R.id.scenario_details_level);
            level.setText(scenario.getLevel());
            TextView usersAmount = (TextView) getView().findViewById(R.id.scenario_details_users_amount);
            usersAmount.setText(String.valueOf(scenario.getUsersAmount()));
            TextView time = (TextView) getView().findViewById(R.id.scenario_details_time);
            time.setText(String.valueOf(scenario.getTime())+"h");
            TextView distance = (TextView) getView().findViewById(R.id.scenario_details_distance);
            distance.setText(String.valueOf(scenario.getDistanceKm()+"km"));
            getActivity().setTitle(scenario.getName());
        }
        else {
            Toast.makeText(App.getCtx(), "Brak danych o scenariuszu, pobierz dane ponownie", Toast.LENGTH_SHORT).show();

        }
        getActivity().setTitle(TITLE);
        handleIntent();
    }

    private void handleIntent() {
        Bundle bundle = getArguments();
        if(bundle!=null) {
            scenarioId = getArguments().getLong("scenarioId", Long.valueOf(-1));
        }
    }




}
