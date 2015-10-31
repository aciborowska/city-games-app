package com.wroclaw.citygames.citygamesapp.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Team;
import com.wroclaw.citygames.citygamesapp.util.Login;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamsListFragment extends Fragment {
    public static final String NAME = TeamsListFragment.class.getCanonicalName();
    public static final String TAG = TeamsListFragment.class.getName();
    public static final String TITLE = "Drużyny";
    private SignTask signTask;

    private TeamListAdapter teamListAdapter;
    private final List<Team> teamList = new ArrayList<>();
    private ListView teamListView;

    public static TeamsListFragment newInstance(boolean startingGame, Long scenarioId) {
        TeamsListFragment myFragment = new TeamsListFragment();
        Bundle args = new Bundle();
        args.putBoolean("startingGame", startingGame);
        args.putLong("scenarioId", scenarioId);
        myFragment.setArguments(args);
        return myFragment;
    }

    public TeamsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        teamListAdapter = new TeamListAdapter(teamList, App.getCtx());
        teamListView = (ListView) getView().findViewById(R.id.team_list);
        teamListView.setAdapter(teamListAdapter);

        getActivity().setTitle(TITLE);
        refreshData();
        handleIntent();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_teams_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.action_add_team:
                Log.d(TAG, "ADD");
                new TeamDialog(getActivity(), "Stwórz drużynę", 3).show();
                break;
            case R.id.action_sing_to_team:
                Log.d(TAG, "SIGN IN");
                new TeamDialog(getActivity(), "Dołącz do drużyny", 1).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleIntent() {
        Bundle bundle = getArguments();
        if(bundle!=null) {
            boolean startingGame = bundle.getBoolean("startingGame", false);
            Long scenarioId = getArguments().getLong("scenarioId", Long.valueOf(-1));
            if (startingGame && scenarioId > 0) {
                teamListView.setOnItemClickListener(new ChooseTeam(scenarioId));
                return;
            }
        }
        teamListView.setOnItemClickListener(new RegularItemClickLListener());
    }

    public class TeamDialog extends Dialog {

        private EditText teamName;
        private EditText password;
        private String title;
        private int operation;

        // Empty constructor required for DialogFragment
        public TeamDialog(Activity a, String title, int operation) {
            super(a, R.style.Theme_CustomDialog);
            this.title = title;
            this.operation = operation;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            setContentView(R.layout.dialog_add_team);
            teamName = (EditText) findViewById(R.id.team_name_dialog_edit_text);
            password = (EditText) findViewById(R.id.team_password_dialog_edit_text);
            final Resources res = getContext().getResources();
            final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
            final View titleDivider = findViewById(titleDividerId);
            if (titleDivider != null) {
                titleDivider.setBackgroundColor(res.getColor(R.color.strong_orange));
            }
            teamName.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            setTitle(title);
            Button cancel = (Button) findViewById(R.id.cancel_button_dialog);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            Button ok = (Button) findViewById(R.id.ok_button_dialog);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (teamName.getText().equals("")) {
                        teamName.setError("Wprowadź nazwę drużyny");
                        return;
                    }
                    if (password.getText().equals("")) {
                        password.setError("Wprowadź hasło");
                        return;
                    }
                    password.setError(null);
                    teamName.setError(null);
                    String name = teamName.getText().toString();
                    String pass = password.getText().toString();

                    Team team = new Team();
                    team.setName(name);
                    team.setPassword(pass);
                    team.setSingular(0);

                    signTask = new SignTask(operation);
                    signTask.execute(team);
                    dismiss();
                }
            });
        }
    }

    private void refreshData() {
        Log.d(TAG, "refreshData");
        if (teamListAdapter != null)
            teamList.clear();
        teamList.addAll(App.getTeamDao().getAll());
        teamListAdapter.notifyDataSetChanged();
    }

    private final class TeamListAdapter extends BaseAdapter implements View.OnClickListener {

        private final List<Team> teams;
        private final Context ctx;

        private TeamListAdapter(List<Team> teams, Context ctx) {
            this.teams = teams;
            this.ctx = ctx;
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
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.list_element_team, parent, false);
            }
            TextView teamName = (TextView) v.findViewById(R.id.team_name);
            TextView teamPlayers = (TextView) v.findViewById(R.id.team_info);
            ImageButton signoutButton = (ImageButton) v.findViewById(R.id.button_team_signout);

            signoutButton.setOnClickListener(this);
            signoutButton.setTag(R.id.buttons, position);
            Team team = getItem(position);
            String name = team.getName() != null ? team.getName() : "team" + String.valueOf(team.getTeamId());
            teamName.setText(name);
            teamPlayers.setText("");
            return v;
        }

        @Override
        public void onClick(View v) {
            final int position = (int) v.getTag(R.id.buttons);
            Log.d(TAG, "WYPISZ: " + position);
            showConfiramtionDialog(teamList.get(position));
        }
    }

    private void showConfiramtionDialog(final Team team) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Wypisz z drużyny")
                .setMessage("Czy chcesz odejść z drużyny")
                .setIcon(R.drawable.ic_action_remove)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        signTask = new SignTask(2);
                        signTask.execute(team);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();

        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(getResources().getColor(R.color.strong_orange));
    }

    private class SignTask extends AsyncTask<Team, Void, Team> {
        public String TAG = SignTask.class.getName();
        int operation;

        public SignTask(int operation) {
            this.operation = operation;
        }

        @Override
        protected Team doInBackground(Team... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            switch (operation) {
                case 1:
                    builder.scheme("http").encodedAuthority(Globals.MAIN_URL)
                            .appendPath(Globals.SIGNIN_URI)
                            .appendQueryParameter("playerId", String.valueOf(Login.getCredentials()))
                            .appendQueryParameter("teamId", String.valueOf(params[0].getTeamId()));
                    String uri = builder.build().toString();
                    Team responseEntity = restTemplate.postForObject(uri, null, Team.class);
                    return responseEntity;
                case 2:
                    builder.scheme("http").encodedAuthority(Globals.MAIN_URL)
                            .appendEncodedPath(Globals.SIGNOUT_URI)
                            .appendQueryParameter("playerId", String.valueOf(Login.getCredentials()))
                            .appendQueryParameter("teamId", String.valueOf(params[0].getTeamId()));
                    uri = builder.build().toString();
                    responseEntity = restTemplate.postForObject(uri, null, Team.class);
                    return responseEntity;
                case 3:
                    builder.scheme("http").encodedAuthority(Globals.MAIN_URL)
                            .appendPath(Globals.CREATE_TEAM_URI)
                            .appendQueryParameter("playerId", String.valueOf(Login.getCredentials()));
                    uri = builder.build().toString();
                    responseEntity = restTemplate.postForObject(uri, params[0], Team.class);
                    return responseEntity;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Team team) {
            super.onPostExecute(team);
            signTask = null;
            if (team != null) {
                if (operation == 2) {
                    App.getTeamDao().delete(team);
                    teamList.remove(team);
                } else {
                    App.getTeamDao().save(team);
                    teamList.add(team);
                }
                refreshData();

                Toast.makeText(getContext(), "Wykonano", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Wystąpił błąd, spróbuj ponownie później", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ChooseTeam implements AdapterView.OnItemClickListener {

        private Long scenarioId;

        public ChooseTeam(Long scenarioId) {
            this.scenarioId = scenarioId;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Team team = teamList.get(position);
            Intent intent = new Intent(getActivity(),MainTaskActivity.class);
            intent.putExtra("playerId",Login.getCredentials());
            intent.putExtra("scenarioId",scenarioId);
            intent.putExtra("teamId",team.getTeamId());
            startActivity(intent);
        }
    }

    private class RegularItemClickLListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "omItemClick id: " + id);
        }
    }
}
