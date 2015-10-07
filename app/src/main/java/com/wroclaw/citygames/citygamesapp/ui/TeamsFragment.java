package com.wroclaw.citygames.citygamesapp.ui;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Team;
import com.wroclaw.citygames.citygamesapp.util.Login;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamsFragment extends Fragment {


    private GetTeamsTask teamTask;
    private static final String TAG = TeamsFragment.class.getName();
    private TeamListAdapter teamListAdapter;
    private final List<Team> teamList = new ArrayList<>();
    private ListView teamListView;

    public TeamsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        teamListAdapter = new TeamListAdapter(teamList, App.getCtx());
        teamListView = (ListView) getView().findViewById(R.id.team_list);
        teamListView.setAdapter(teamListAdapter);

        refreshData();

        teamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "omItemClick id: " + id);

            }
        });
    }

    private void refreshData(){
        Log.d(TAG, "refreshData");
        if(teamList.isEmpty()) {
            teamTask = new GetTeamsTask();
            teamTask.execute();
            Log.d(TAG,"pobieranie danych");
        }
    }

    private final class TeamListAdapter extends BaseAdapter{

        private final List<Team> teams;
        private final Context ctx;

        private TeamListAdapter(List<Team> teams, Context ctx){
            this.teams=teams;
            this.ctx=ctx;
        }

        @Override
        public int getCount() {
            return teams.size();
        }

        @Override
        public Team getItem(int position) {
            return teams.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getTeamId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.list_element_team, parent, false);
            }
            TextView teamName=(TextView)v.findViewById(R.id.team_name);
            TextView teamPlayers = (TextView) v.findViewById(R.id.team_info);

            Team team = getItem(position);
            String name = team.getName()!=null?team.getName():"team"+String.valueOf(team.getTeamId());
            teamName.setText(name);
            teamPlayers.setText("Pomyśle jeszcze co tu będzie");
            return v;
        }
    }

    public class GetTeamsTask extends AsyncTask<Void, Void, Team[]> {
        private boolean connection_error = false;

        GetTeamsTask() {
        }

        @Override
        protected Team[] doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(Globals.MAIN_URL).appendEncodedPath(Globals.PLAYER_TEAMS_URI).appendQueryParameter("id", String.valueOf(Login.getCredentials()));
            String uri = builder.build().toString();
            try {
                ResponseEntity<Team[]> responseEntity = restTemplate.getForEntity(uri, Team[].class);
                return responseEntity.getBody();
            } catch (final Exception e) {
                connection_error = true;
                Log.d(TAG, "błąd połączenia");
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Team[] result) {
            Log.d(TAG,"onPostExecute");
            teamTask = null;
            if (result != null) {
                List<Team> teams = new ArrayList<>();
                teams.addAll(Arrays.asList(result));
                App.getTeamDao().saveAll(teams);
                if(teamListAdapter != null)
                    teamList.clear();
                    teamList.addAll(App.getTeamDao().getAll());
                    teamListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            teamTask = null;
        }
    }
}
