/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wroclaw.citygames.citygamesapp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Game;
import com.wroclaw.citygames.citygamesapp.model.Scenario;
import com.wroclaw.citygames.citygamesapp.model.Team;
import com.wroclaw.citygames.citygamesapp.util.ImageConverter;
import com.wroclaw.citygames.citygamesapp.util.Login;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NavigationDrawerActivity extends FragmentActivity {
    private static final String TAG = NavigationDrawerActivity.class.getName();
    public static String CURRENT_FRAGMENT_NAME = StartFragment.NAME;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ProgressBar progressBar;
    private ActionBarDrawerToggle drawerToggle;

    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] fragmentsTitles;
    private boolean isNewLogin = false;

    private CollectDataTask teamTask;
    private CollectScenarioTask scenarioTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        title = drawerTitle = getTitle();
        progressBar = (ProgressBar) findViewById(R.id.drawer_progress_bar);
        fragmentsTitles = getResources().getStringArray(R.array.nav_drawer_items);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                R.layout.drawer_list_item, fragmentsTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(title);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, StartFragment.TAG)).addToBackStack(TAG);
        tx.commit();
        if (savedInstanceState == null) {
            selectItem(0);
        }

        handleIntent(getIntent());
    }

    protected void handleIntent(Intent intent) {
        isNewLogin = intent.getBooleanExtra("isNewLogin", true);
        Log.d(TAG, "handleIntent: " + isNewLogin);
        if (isNewLogin) {
           startCollectingData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_navigation_drawer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_data_refresh:
                Toast.makeText(getApplicationContext(),R.string.toast_synchronization,Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                startCollectingData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startCollectingData() {
        App.getGameDao().deleteAll();
        App.getTeamDao().deleteAll();
        App.getScenarioDao().deleteAll();
        ImageConverter.cleanPhotoDir();
        teamTask = null;
        scenarioTask = null;
        teamTask = new CollectDataTask();
        scenarioTask = new CollectScenarioTask();
        scenarioTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        teamTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case 0:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, StartFragment.TAG));
                tx.commit();
                CURRENT_FRAGMENT_NAME = StartFragment.TITLE;
                break;
            case 1:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, TeamsListFragment.TAG)).addToBackStack("");
                tx.commit();
                CURRENT_FRAGMENT_NAME = TeamsListFragment.TITLE;
                break;
            case 2:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, GamesListFragment.TAG)).addToBackStack("");
                tx.commit();
                CURRENT_FRAGMENT_NAME = GamesListFragment.TITLE;
                break;
            case 3:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, ScenariosListFragment.TAG)).addToBackStack("");
                tx.commit();
                CURRENT_FRAGMENT_NAME = ScenariosListFragment.TITLE;
                break;
            case 4:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, RankFragment.TAG)).addToBackStack(TAG);
                tx.commit();
                CURRENT_FRAGMENT_NAME = RankFragment.TITLE;
                break;
            case 5:
                ImageConverter.cleanPhotoDir();
                App.getGameDao().deleteAll();
                App.getGameDao().deleteAll();
                App.getTeamDao().deleteAll();
                Login.logout();
                finish();
        }
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getActionBar().setTitle(this.title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (CURRENT_FRAGMENT_NAME.equals(StartFragment.TITLE)) {
            finish();
        } else {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, StartFragment.NAME));
            tx.commit();
            CURRENT_FRAGMENT_NAME = StartFragment.TITLE;
        }
    }

    public class CollectDataTask extends AsyncTask<Void, Void, Team[]> {
        public String TAG = CollectDataTask.class.getName();

        CollectDataTask() {
        }

        @Override
        protected Team[] doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(Globals.MAIN_URL).appendEncodedPath(Globals.COLLECT_DATA_URI).appendQueryParameter("playerId", String.valueOf(Login.getPlayerId()));
            String uri = builder.build().toString();
            try {
                ResponseEntity<Team[]> responseEntity = restTemplate.getForEntity(uri, Team[].class);
                return responseEntity.getBody();
            } catch (final Exception e) {
                Log.d(TAG, "błąd połączenia");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Team[] result) {
            Log.d(TAG, "onPostExecute");

            if (result != null) {
                List<Team> teams = new ArrayList<>();
                App.getGameDao().deleteAll();
                App.getTeamDao().deleteAll();

                teams.addAll(Arrays.asList(result));
                for (Team team : result) {
                    App.getTeamDao().save(team);
                    for (Game game : team.getGames()) {
                        game.setTeamId(team.getTeamId());
                        App.getGameDao().save(game);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_connection_error), Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onCancelled() {
            teamTask = null;
        }
    }

    public class CollectScenarioTask extends AsyncTask<Void, Void, Scenario[]> {
        public String TAG = CollectScenarioTask.class.getName();


        @Override
        protected Scenario[] doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(Globals.MAIN_URL).appendPath(Globals.SCENARIOS_URI);
            String uri = builder.build().toString();
            try {
                ResponseEntity<Scenario[]> responseEntity = restTemplate.getForEntity(uri, Scenario[].class);
                return responseEntity.getBody();
            } catch (final Exception e) {
                Log.d(TAG, "błąd połączenia");
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Scenario[] result) {
            Log.d(TAG, "onPostExecute");
            if (result != null) {
                List<Scenario> scenarios = new ArrayList<>();
                scenarios.addAll(Arrays.asList(result));
                App.getScenarioDao().deleteAll();
                App.getScenarioDao().saveAll(scenarios);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_connection_error), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            scenarioTask = null;
        }
    }
}