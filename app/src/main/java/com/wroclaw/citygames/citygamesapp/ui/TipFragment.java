package com.wroclaw.citygames.citygamesapp.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Tip;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class TipFragment extends Fragment implements Observer{
    public static final String NAME = TipFragment.class.getCanonicalName();
    public static final String TAG = TipFragment.class.getName();
    public static final String TITLE = "Wskazówki";


    private TipListAdapter tipListAdapter;
    private final List<Tip> tipList = new ArrayList<>();
    private ListView teamListView;
    private TextView noTips;
    public TipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tip, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated");
        MainTaskActivity.currentTask.addObserver(this);
        noTips = (TextView) getView().findViewById(R.id.no_tips_textview);
        tipListAdapter = new TipListAdapter(tipList, App.getCtx());
        teamListView = (ListView) getView().findViewById(R.id.tip_list);
        teamListView.setAdapter(tipListAdapter);

        refreshData();
    }

    private void refreshData() {
        Log.d(TAG, "refreshData");
        if (tipListAdapter != null)
            tipList.clear();
        Set<Tip> tips = MainTaskActivity.currentTask.getTask().getTips();
        if(tips!=null) {
            noTips.setVisibility(View.GONE);
            tipList.addAll(MainTaskActivity.currentTask.getTask().getTips());
         }
        else {
            noTips.setVisibility(View.VISIBLE);
        }
        tipListAdapter.notifyDataSetChanged();
    }


    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG,"update");
        refreshData();
    }

    private final class TipListAdapter extends BaseAdapter {

        private final List<Tip> tips;
        private final Context ctx;

        private TipListAdapter(List<Tip> teams, Context ctx) {
            this.tips = teams;
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return tips.size();
        }

        @Override
        public Tip getItem(int position) {
            return tips.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getTipId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.list_element_tip, parent, false);
            }
            TextView tipNumber = (TextView) v.findViewById(R.id.tip_number_text_view);
            TextView tipCost = (TextView) v.findViewById(R.id.cost_tip_text_view);
            final Button buyTipButton = (Button) v.findViewById(R.id.buy_tip_button);
            final ImageView boughtImageView = (ImageView) v.findViewById(R.id.bought_tip_image_view);
            buyTipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boughtImageView.setVisibility(View.VISIBLE);
                    buyTipButton.setEnabled(false);
                }
            });
            buyTipButton.setTag(R.id.buttons, position);
            Tip tip = getItem(position);
            tipNumber.setText("Wskazówka nr "+String.valueOf(tip.getNumber()));
            tipCost.setText("Koszt: -" + String.valueOf(tip.getCost())+" punktów");
            if(tip.isBought()) {
                boughtImageView.setVisibility(View.VISIBLE);
                buyTipButton.setEnabled(false);
            }
            return v;
        }


    }
}
