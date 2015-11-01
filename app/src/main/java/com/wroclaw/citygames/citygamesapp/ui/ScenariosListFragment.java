package com.wroclaw.citygames.citygamesapp.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScenariosListFragment extends Fragment {
    public static final String NAME = ScenariosListFragment.class.getCanonicalName();
    public static final String TAG = ScenariosListFragment.class.getName();
    public static final String TITLE = "Scenariusze";

    private boolean startingGame;
    private final List<Scenario> scenarioList = new ArrayList<>();
    private ListView scenarioListView;
    private ScenarioListAdapter scenarioListAdapter;

    public static ScenariosListFragment newInstance(boolean startingGame) {
        ScenariosListFragment myFragment = new ScenariosListFragment();

        Bundle args = new Bundle();
        args.putBoolean("startingGame", startingGame);
        myFragment.setArguments(args);

        return myFragment;
    }

    public ScenariosListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scenarios, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        scenarioListAdapter = new ScenarioListAdapter(scenarioList, App.getCtx());
        scenarioListView = (ListView) getView().findViewById(R.id.scenario_list);
        scenarioListView.setAdapter(scenarioListAdapter);
        handleIntent();
        refreshData();

        getActivity().setTitle(ScenariosListFragment.TITLE);
    }

    protected void handleIntent() {
        Log.d(TAG, "handleIntent: " + startingGame);
        Bundle bundle = getArguments();
        if (bundle != null) {
            startingGame = getArguments().getBoolean("startingGame", false);
            if (startingGame) {
                scenarioListView.setOnItemClickListener(new ChooseScenario());
                getActivity().setTitle("Wybierz scenariusz");
                return;
            }
        }
        scenarioListView.setOnItemClickListener(new ScenarioDetails());
    }

    private void refreshData() {
        Log.d(TAG, "refreshData");
        if (scenarioListView != null)
            scenarioList.clear();
        scenarioList.addAll(App.getScenarioDao().getAll());
        Log.d(TAG, String.valueOf(scenarioList.size()));
        Log.d(TAG, scenarioList.get(0).getName());
        scenarioListAdapter.notifyDataSetChanged();
    }

    private final class ScenarioListAdapter extends BaseAdapter {
        private final List<Scenario> scenarios;
        private final Context ctx;

        private ScenarioListAdapter(List<Scenario> scenarios, Context ctx) {
            this.scenarios = scenarios;
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return scenarios.size();
        }

        @Override
        public Scenario getItem(int position) {
            return scenarios.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getScenarioId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.list_element_scenario, parent, false);
            }
            TextView scenarioName = (TextView) v.findViewById(R.id.sceanrio_name);
            TextView scenarioInfo = (TextView) v.findViewById(R.id.sceanrio_info);

            Scenario scenario = getItem(position);
            Log.d(TAG, scenario.getName());
            String name = scenario.getName();
            scenarioName.setText(name);
            scenarioInfo.setText(scenario.getLevel());
            return v;
        }
    }

    private class ScenarioDetails implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Scenario scenario = scenarioList.get(position);
            FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.navigation_drawer_frame, ScenarioDetailsFragment.newInstance(scenario.getScenarioId()))
                    .addToBackStack(null);
            tx.commit();
        }
    }

    private class ChooseScenario implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Scenario scenario = scenarioList.get(position);
            FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.navigation_drawer_frame, TeamsListFragment.newInstance(true, scenario.getScenarioId()));
            tx.commit();
            getActivity().setTitle(scenario.getName());
        }
    }

}
