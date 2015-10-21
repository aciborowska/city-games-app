package com.wroclaw.citygames.citygamesapp.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wroclaw.citygames.citygamesapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TipFragment extends Fragment {
    public static final String NAME = TipFragment.class.getCanonicalName();
    public static final String TAG = TipFragment.class.getName();
    public static final String TITLE = "Wskazówki";

    public TipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tip, container, false);
    }


}
