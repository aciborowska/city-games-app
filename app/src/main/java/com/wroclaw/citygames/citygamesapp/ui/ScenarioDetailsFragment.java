package com.wroclaw.citygames.citygamesapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wroclaw.citygames.citygamesapp.R;

public class ScenarioDetailsFragment extends Fragment {
    public static final String NAME = ScenarioDetailsFragment.class.getCanonicalName();
    public static final String TAG = ScenarioDetailsFragment.class.getName();
    public static final String TITLE = "Scenariusz";

    public static  ScenarioDetailsFragment newInstance() {
        ScenarioDetailsFragment myFragment = new ScenarioDetailsFragment();
        return myFragment;
    }

    public ScenarioDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scenario_details, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
