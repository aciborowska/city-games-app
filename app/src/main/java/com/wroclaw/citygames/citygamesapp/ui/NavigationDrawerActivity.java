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
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Game;
import com.wroclaw.citygames.citygamesapp.model.Scenario;
import com.wroclaw.citygames.citygamesapp.model.Team;
import com.wroclaw.citygames.citygamesapp.util.Login;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationDrawerActivity extends FragmentActivity {
    private static final String TAG=NavigationDrawerActivity.class.getName();
    private static final String NAME = NavigationDrawerActivity.class.getCanonicalName();
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private CharSequence drawerTitle;
    private CharSequence title;
    private String[] fragmentsTitles;
    private boolean isNewLogin=false;

    private CollectDataTask teamTask;
    private CollectScenarioTask scenarioTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        title = drawerTitle = getTitle();
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
        tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, StartFragment.NAME)).addToBackStack(TAG);
        tx.commit();
        if (savedInstanceState == null) {
            selectItem(0);
        }

        handleIntent(getIntent());
    }

    protected void handleIntent(Intent intent) {
        isNewLogin= intent.getBooleanExtra("isNewLogin", true);
        Log.d(TAG, "handleIntent: " + isNewLogin);
        if(isNewLogin){
            teamTask = new CollectDataTask();
            scenarioTask = new CollectScenarioTask();
            scenarioTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            teamTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            /*case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        switch (position)
        {
            case 0:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, StartFragment.NAME));
                tx.commit();
                break;
            case 1:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, TeamsListFragment.NAME)).addToBackStack("");
                tx.commit();
                break;
            case 2:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, GamesListFragment.NAME)).addToBackStack("");
                tx.commit();
                break;
            case 3:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, ScenariosListFragment.NAME)).addToBackStack("");
                tx.commit();
                break;
            case 4:
                tx.replace(R.id.navigation_drawer_frame, Fragment.instantiate(NavigationDrawerActivity.this, RankFragment.NAME)).addToBackStack(TAG);
                tx.commit();
                break;
            case 5:
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

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public class CollectDataTask extends AsyncTask<Void, Void, Team[]> {
        public String TAG = CollectDataTask.class.getName();

        CollectDataTask() {}

        @Override
        protected Team[] doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(Globals.MAIN_URL).appendEncodedPath(Globals.COLLECT_DATA_URI).appendQueryParameter("playerId", String.valueOf(Login.getCredentials()));
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
                for(Team team:result){
                    App.getTeamDao().save(team);
                    for(Game game:team.getGames()) {
                        game.setTeamId(team.getTeamId());
                        App.getGameDao().save(game);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            teamTask = null;
        }
    }

    public class CollectScenarioTask extends AsyncTask<Void, Void, Scenario[]> {
        public String TAG = CollectScenarioTask.class.getName();
        private boolean connection_error = false;

        CollectScenarioTask() {}

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
                connection_error = true;
                Log.d(TAG, "błąd połączenia");
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Scenario[] result) {
            Log.d(TAG,"onPostExecute");
            if (result != null) {
                List<Scenario> scenarios = new ArrayList<>();
                scenarios.addAll(Arrays.asList(result));
                App.getScenarioDao().deleteAll();
                App.getScenarioDao().saveAll(scenarios);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            scenarioTask = null;
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");

    }
}