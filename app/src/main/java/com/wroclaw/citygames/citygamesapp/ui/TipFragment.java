package com.wroclaw.citygames.citygamesapp.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Tip;
import com.wroclaw.citygames.citygamesapp.util.Gameplay;
import com.wroclaw.citygames.citygamesapp.util.ImageConverter;

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
    private ExpandableListView teamListView;
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
        teamListView = (ExpandableListView) getView().findViewById(R.id.tip_list);
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

    private final class TipListAdapter extends BaseExpandableListAdapter {

        private final List<Tip> tips;
        private final Context ctx;

        private TipListAdapter(List<Tip> teams, Context ctx) {
            this.tips = teams;
            this.ctx = ctx;
        }

        @Override
        public int getGroupCount() {
            return tipList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Tip getGroup(int groupPosition) {
            return tipList.get(groupPosition);
        }

        @Override
        public Tip getChild(int groupPosition, int childPosition) {
            return tipList.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return tipList.get(groupPosition).getTipId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return tipList.get(groupPosition).getTipId();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.list_element_tip, parent, false);
            }
            TextView tipNumber = (TextView) v.findViewById(R.id.tip_number_text_view);
            TextView tipCost = (TextView) v.findViewById(R.id.cost_tip_text_view);
            final Button buyTipButton = (Button) v.findViewById(R.id.buy_tip_button);
            buyTipButton.setTag(R.id.buttons, groupPosition);
            buyTipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = (int) v.getTag(R.id.buttons);
                    Log.d(TAG, "onClick: " + position);
                    Tip tip = tipList.get(position);
                    tip.setBought(true);
                    Gameplay.addPenaltyPoints(tip.getCost());
                    Toast.makeText(App.getCtx(), "Kupiono wskazówkę", Toast.LENGTH_SHORT).show();
                    buyTipButton.setVisibility(View.INVISIBLE);
                    refreshData();
                }
            });
            buyTipButton.setTag(R.id.buttons, groupPosition);
            Tip tip = getGroup(groupPosition);
            tipNumber.setText("Wskazówka nr "+String.valueOf(tip.getNumber()));
            tipCost.setText("Koszt: -" + String.valueOf(tip.getCost())+" punktów");
            if(tip.isBought()) {
                buyTipButton.setEnabled(false);
            }
            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.list_exp_element_tip, parent, false);
            }

            TextView tipDescription = (TextView) v.findViewById(R.id.tip_exp_text_view);
            ImageView tipImage = (ImageView) v.findViewById(R.id.exp_tip_image_view);
            Tip tip = getGroup(groupPosition);
            if(tip.isBought()) {
                tipDescription.setText(tip.getDescription());
                String filename = tip.getPicture();
                if(filename!=null && !filename.isEmpty()){
                    Bitmap bitmap = ImageConverter.loadBitmap(filename);
                    tipImage.setImageBitmap(bitmap);
                }
            }
            else tipDescription.setText("Aby skorzystać z wskazówki musiszą ją kupić!");
            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
