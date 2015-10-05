package com.wroclaw.citygames.citygamesapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wroclaw.citygames.citygamesapp.R;


public class StartFragment extends Fragment {

    public StartFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        return rootView;
    }





}
