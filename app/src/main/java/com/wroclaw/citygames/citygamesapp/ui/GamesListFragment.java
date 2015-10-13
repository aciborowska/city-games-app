package com.wroclaw.citygames.citygamesapp.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.wroclaw.citygames.citygamesapp.model.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesListFragment extends Fragment {
    public static final String NAME = GamesListFragment.class.getCanonicalName();
    public static final String TAG = GamesListFragment.class.getName();
    public static final String TITLE = "Gry";
    public GamesListFragment() {
        // Required empty public constructor
    }

    private List<Game> gameList = new ArrayList<>();;
    private ListView gameListView;
    private GameListAdapter gameListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        gameListAdapter = new GameListAdapter(gameList, App.getCtx());
        gameListView = (ListView) getView().findViewById(R.id.games_list);
        gameListView.setAdapter(gameListAdapter);

        refreshData();

        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "omItemClick id: " + id);

            }
        });
    }

    private void refreshData(){
        Log.d(TAG, "refreshData");
        if(gameListView != null)
            gameList.clear();
        gameList.addAll(App.getGameDao().getAll());
        gameListAdapter.notifyDataSetChanged();
    }


    private final class GameListAdapter extends BaseAdapter {

        private final List<Game> games;
        private final Context ctx;

        public GameListAdapter(List<Game> games, Context ctx){
            this.games=games;
            this.ctx=ctx;
        }
        @Override
        public int getCount() {
            return games.size();
        }

        @Override
        public Game getItem(int position) {
            return games.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getGameId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.list_element_game, parent, false);
            }
            TextView scenarioName=(TextView)v.findViewById(R.id.game_name);
            TextView scenarioInfo = (TextView) v.findViewById(R.id.game_info);

            Game game = getItem(position);
            String name = App.getScenarioDao().get(game.getScenarioId()).getName();
            scenarioName.setText(name);
            scenarioInfo.setText("Zdobyte punkty "+String.valueOf(game.getPoints()));
            return v;
        }
    }


}
