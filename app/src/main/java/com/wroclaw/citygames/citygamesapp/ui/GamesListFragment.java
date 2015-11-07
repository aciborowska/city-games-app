package com.wroclaw.citygames.citygamesapp.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Game;
import com.wroclaw.citygames.citygamesapp.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesListFragment extends Fragment {
    public static final String NAME = GamesListFragment.class.getCanonicalName();
    public static final String TAG = GamesListFragment.class.getName();
    public static final String TITLE = "Gry";

    private List<Game> gameList = new ArrayList<>();
    private ExpandableListView gameListView;
    private GameListAdapter gameListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    public static  GamesListFragment newInstance() {
        return new GamesListFragment();
    }

    public GamesListFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        gameListAdapter = new GameListAdapter(gameList, App.getCtx());
        gameListView = (ExpandableListView) getView().findViewById(R.id.games_list);
        gameListView.setAdapter(gameListAdapter);

        refreshData();

        getActivity().setTitle(GamesListFragment.TITLE);
    }

    private void refreshData(){
        Log.d(TAG, "refreshData");
        if(gameListView != null)
            gameList.clear();
        gameList.addAll(App.getGameDao().getAll());
        gameListAdapter.notifyDataSetChanged();
    }


    private final class GameListAdapter extends BaseExpandableListAdapter {

        private final List<Game> games;
        private final Context ctx;

        public GameListAdapter(List<Game> games, Context ctx){
            this.games=games;
            this.ctx=ctx;
        }

        @Override
        public int getGroupCount() {
            return games.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Game getGroup(int groupPosition) {
            return games.get(groupPosition);
        }

        @Override
        public Game getChild(int groupPosition, int childPosition) {
            return games.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return games.get(groupPosition).getGameId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return games.get(groupPosition).getGameId();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View v=convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.list_element_game, parent, false);
            }
            TextView scenarioName=(TextView)v.findViewById(R.id.game_name);
            TextView scenarioInfo = (TextView) v.findViewById(R.id.game_info);

            Game game = getGroup(groupPosition);
            String name = App.getScenarioDao().get(game.getScenarioId()).getName();
            scenarioName.setText(name);
            scenarioInfo.setText("Zdobyte punkty "+String.valueOf(game.getPoints()));
            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View v=convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.list_exp_element_game, parent, false);
            }
            v.setBackgroundColor(getResources().getColor(R.color.light_orange));
            TextView scenarioName=(TextView)v.findViewById(R.id.game_scenario_name);
            TextView collectedPoints=(TextView)v.findViewById(R.id.game_collected_points);
            TextView timeInGame=(TextView)v.findViewById(R.id.game_time_in_game);
            TextView team=(TextView)v.findViewById(R.id.game_team);

            Game game = getGroup(groupPosition);

            String name = App.getScenarioDao().get(game.getScenarioId()).getName();
            if(name!= null) scenarioName.setText(name);

            collectedPoints.setText(String.valueOf(game.getPoints()));

            Long start = game.getTimeStart();
            Long end = game.getTimeEnd();
            if(start != null && end != null && end-start>0) {
                Long millis = end - start;
                String timeString = String.format("%02d min, %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                );
                timeInGame.setText(timeString);
            }else timeInGame.setText("brak danych");

            Team playingTeam = App.getTeamDao().get(game.getTeamId());
            if(playingTeam.getName()!=null) team.setText(playingTeam.getName());
            else team.setText("team"+String.valueOf(playingTeam.getTeamId()));

            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }


}
