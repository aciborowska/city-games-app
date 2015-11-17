package com.wroclaw.citygames.citygamesapp.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Game;
import com.wroclaw.citygames.citygamesapp.model.Scenario;
import com.wroclaw.citygames.citygamesapp.util.RankingCheckbox;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends Fragment {
    public static final String NAME = RankFragment.class.getCanonicalName();
    public static final String TAG = RankFragment.class.getName();
    public static final String TITLE = "Ranking";


    private List<Game> gameList = new ArrayList<>();
    private RankingAdapter rankingAdapter;
    private ListView rankingListView;
    private AlertDialog dialog;
    private ProgressBar progressBar;
    private GetGameTask getGameTask;
    private final int MODE_NEW = 0;
    private final int MODE_ADD = 1;


    private static final String GET_BEST_20  = new Uri.Builder()
            .scheme("http")
            .encodedAuthority(Globals.MAIN_URL)
            .appendEncodedPath(Globals.RANKING_BEST20_URI)
            .build().toString();

    public static  RankFragment newInstance() {
        RankFragment myFragment = new RankFragment();
        return myFragment;
    }

    public RankFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_rank, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rankingAdapter = new RankingAdapter(gameList, App.getCtx());
        rankingListView = (ListView) getView().findViewById(R.id.ranking_list);
        rankingListView.setAdapter(rankingAdapter);

        progressBar = (ProgressBar) getView().findViewById(R.id.ranking_progress_bar);
        getActivity().setTitle(RankFragment.TITLE);


        getGameTask = new GetGameTask(GET_BEST_20,MODE_NEW);
        progressBar.setVisibility(View.VISIBLE);
        getGameTask.execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_rank_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.action_rank_refresh:
                getGameTask = new GetGameTask(GET_BEST_20,MODE_NEW);
                getGameTask.execute();
                progressBar.setVisibility(View.VISIBLE);
                break;
            case R.id.action_rank_next_10:
                int size = gameList.size();
                String uri =new Uri.Builder()
                        .scheme("http")
                        .encodedAuthority(Globals.MAIN_URL)
                        .appendEncodedPath(Globals.RANKING_GET_NEXT_10 + "?current_amount=" + String.valueOf(size))
                        .build().toString();
                getGameTask = new GetGameTask(uri,MODE_ADD);
                getGameTask.execute();
                progressBar.setVisibility(View.VISIBLE);
                break;
            case R.id.action_rank_settings:
                final List<String> selectedItems=RankingCheckbox.get();
                List<Scenario> scenarios = App.getScenarioDao().getAll();
                final String scenarioNames[] = new String[scenarios.size()];
                boolean selected[] = new boolean[scenarios.size()];
                for(int i=0;i<scenarios.size();i++) {
                    scenarioNames[i]=scenarios.get(i).getName();
                    if(selectedItems.contains(scenarioNames[i])) selected[i]=true;
                    else selected[i]=false;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Filtruj ranking wg scenariuszy");
                builder.setMultiChoiceItems(scenarioNames, selected,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected,
                                                boolean isChecked) {
                                if (isChecked) {
                                    selectedItems.add(scenarioNames[indexSelected]);
                                } else if (selectedItems.contains(scenarioNames[indexSelected])) {
                                    int idx = selectedItems.indexOf(scenarioNames[indexSelected]);
                                    selectedItems.remove(idx);
                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                RankingCheckbox.save(selectedItems);
                                getGameTask = new GetGameTask(GET_BEST_20,MODE_NEW);
                                progressBar.setVisibility(View.VISIBLE);
                                getGameTask.execute();
                            }
                        })
                        .setNegativeButton("Wyczysc", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                RankingCheckbox.save(new ArrayList<String>());
                                getGameTask = new GetGameTask(GET_BEST_20,MODE_NEW);
                                progressBar.setVisibility(View.VISIBLE);
                                getGameTask.execute();
                            }
                        });


                dialog = builder.create();
                dialog.show();
                Resources resources = dialog.getContext().getResources();
                int color = resources.getColor(R.color.medium_orange); // your color here

                int titleDividerId = resources.getIdentifier("titleDivider", "id", "android");
                View titleDivider = dialog.getWindow().getDecorView().findViewById(titleDividerId);
                titleDivider.setBackgroundColor(color); // change divider color
        }
        return super.onOptionsItemSelected(item);
    }

    private class RankingAdapter extends BaseAdapter{
        private List<Game> games;
        private Context context;

        public RankingAdapter(List<Game> gamesList, Context context){
            this.games = gamesList;
            this.context= context;
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
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.list_element_rank, parent, false);
            }
            if (position % 2 == 1) {
                v.setBackgroundColor(getResources().getColor(R.color.light_orange));
            } else {
                v.setBackgroundColor(Color.WHITE);
            }
            TextView number = (TextView) v.findViewById(R.id.number);
            TextView teamName = (TextView) v.findViewById(R.id.ranking_team_name);
            TextView scenarioName = (TextView) v.findViewById(R.id.ranking_scenario_name);
            TextView points = (TextView) v.findViewById(R.id.ranking_points);

            Game game = getItem(position);
            number.setText(String.valueOf(position+1)+".");
            teamName.setText(game.getScenario().getDescription());
            scenarioName.setText(game.getScenario().getName());
            points.setText(String.valueOf(game.getPoints()));
            return v;
        }
    }

    private class GetGameTask extends AsyncTask<Void,Void,Game[]>{

        private String uri;
        private int mode;
        public GetGameTask(String uri,int mode){
            this.uri = uri;
            this.mode = mode;
        }
        @Override
        protected Game[] doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Log.d(TAG, uri);
            try {
                ResponseEntity<Game[]> responseEntity = restTemplate.postForEntity(uri, RankingCheckbox.get(),Game[].class);
                return responseEntity.getBody();
            } catch (final Exception e) {
                Log.d(TAG, "błąd połączenia");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Game[] games) {
            Log.d(TAG, "onPostExecute");
            getGameTask = null;
            progressBar.setVisibility(View.GONE);
            if (games != null) {
                refreshData(Arrays.asList(games),mode);
            } else {
                Toast.makeText(App.getCtx(), "Brak nowych pozycji", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refreshData(List<Game> games,int mode) {
        Log.d(TAG, "refreshData");
        if(mode==MODE_NEW) {
            if (rankingListView != null)
                gameList.clear();
            Toast.makeText(App.getCtx(),"Zsynchronizowano",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(App.getCtx(),"Pobrano kolejne 10 pozycji",Toast.LENGTH_SHORT).show();
        }
        gameList.addAll(games);
        rankingAdapter.notifyDataSetChanged();
    }

}
