package com.wroclaw.citygames.citygamesapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.dao.TaskDao;
import com.wroclaw.citygames.citygamesapp.model.Game;
import com.wroclaw.citygames.citygamesapp.service.LocationService;
import com.wroclaw.citygames.citygamesapp.util.Gameplay;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

public class MainTaskActivity extends FragmentActivity {

    public static final String TAG = MainTaskActivity.class.getName();

    public static TaskDao currentTask = null;
    private RegisterGame registerGameTask;
    private boolean isCurrent;

    private ProgressBar progressBar;
    private ViewPager mainViewPager;
    private MainViewPagerAdapter mainViewPagerAdapter;

    public static final int TASK_FRAGMENT_NUM = 1;
    public static final int TIP_FRAGMENT_NUM = 0;
    public static final int MAP_FRAGMENT_NUM = 2;
    private static final int START_FRAGMENT = TASK_FRAGMENT_NUM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_task);
        Log.d(TAG, "onCreate");
        currentTask = new TaskDao();
        progressBar = (ProgressBar) findViewById(R.id.register_game_progress_bar);
        handleIntent(getIntent());
        // Zainicjalizuj ViewPager
        mainViewPager = (ViewPager) findViewById(R.id.view_pager);
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewPagerAdapter);
        mainViewPager.setCurrentItem(START_FRAGMENT);
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        setTitle("Gra");
    }

    /**
     * Metoda obsługująca nadejście nowej intencji.
     * W tej metodzie powinnien znaleźć się wszelki kod wypełniający aktywność danymi, które
     * zależą od intencji lub od jej braku!
     *
     * @param intent intencja
     */
    protected void handleIntent(Intent intent) {
        Log.d(TAG, "handle intent");
        if (intent != null) {
            isCurrent = intent.getBooleanExtra("isCurrent", false);
            if(Gameplay.getCurrentGame()!=-1) isCurrent = true;
            Log.d(TAG,"isCurrent = "+isCurrent);
            if (!isCurrent) {
                Long playerId = intent.getLongExtra("playerId", (long) -1);
                Long teamId = intent.getLongExtra("teamId", (long) -1);
                Long scenarioId = intent.getLongExtra("scenarioId", (long) -1);
                if (playerId != -1 & teamId != -1 & scenarioId != -1) {
                    progressBar.setVisibility(View.VISIBLE);
                    registerGameTask = new RegisterGame(scenarioId, playerId, teamId);
                    registerGameTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    try {
                        registerGameTask.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Log.e(TAG, "nie przekazano id gracz, scenariusza lub drużyny");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private final class MainViewPagerAdapter extends FragmentPagerAdapter {

        private final TaskFragment taskFragment;
        private final TipFragment tipFragment;
        private final MapFragment mapFragment;

        private MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
            taskFragment = TaskFragment.newInstance(isCurrent);
            tipFragment = new TipFragment();
            mapFragment = new MapFragment();
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case TASK_FRAGMENT_NUM:
                    return taskFragment;
                case TIP_FRAGMENT_NUM:
                    return tipFragment;
                case MAP_FRAGMENT_NUM:
                    return mapFragment;
            }
            Log.e(TAG, "Przełączono na nie zaimplementowany fragment");
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d(TAG, "getPageTitle " + position);
            switch (position) {
                case TASK_FRAGMENT_NUM:
                    return TaskFragment.TITLE;
                case TIP_FRAGMENT_NUM:
                    return TipFragment.TITLE;
                case MAP_FRAGMENT_NUM:
                    return MapFragment.TITLE;
            }
            Log.e(TAG, "Tekst dla wybranej zakładki: " + position + " nie jest zaimplementowany");
            return null;
        }
    }

    public class RegisterGame extends AsyncTask<Void, Void, Game> {

        private Long scenarioId;
        private Long playerId;
        private Long teamId;

        public RegisterGame(Long scenarioId, Long playerId, Long teamId) {
            this.scenarioId = scenarioId;
            this.playerId = playerId;
            this.teamId = teamId;
        }

        @Override
        protected Game doInBackground(Void... params) {
            Log.d(TAG, "Rozpocznamy rozgrywke....");
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(Globals.MAIN_URL)
                    .appendPath(Globals.GAMEPLAY_URI)
                    .appendPath(Globals.REGISTER_GAME_URI)
                    .appendQueryParameter("leaderId", String.valueOf(playerId))
                    .appendQueryParameter("scenarioId", String.valueOf(scenarioId))
                    .appendQueryParameter("teamId", String.valueOf(teamId));
            String uri = builder.build().toString();
            Game game = null;
            try {
                Log.d(TAG, "rejestracja gry: " + uri);
                game = restTemplate.postForObject(uri, null, Game.class);
                if(game!=null) Gameplay.registerGame(game.getGameId(), teamId);
            } catch (final Exception e) {
                Log.d(TAG, "błąd połączenia");
                e.printStackTrace();
            }
            return game;
        }

        @Override
        protected void onPostExecute(Game game) {
            super.onPostExecute(game);
            registerGameTask = null;
            progressBar.setVisibility(View.GONE);
            if (game == null) {
                Toast.makeText(App.getCtx(), "Wystąpił błąd, spróbuj ponownie później", Toast.LENGTH_SHORT).show();
                finish();
            } else if (game.getGameId() > 0) {
                Gameplay.registerGame(game.getGameId(), teamId);
                App.getGameDao().save(game);
                Log.d(TAG, "zarejestrowano nową grę " + game.getGameId());
                Toast.makeText(App.getCtx(), "Zarejestrowano grę", Toast.LENGTH_SHORT).show();
            } else if (game.getGameId() < 0) {
                Toast.makeText(App.getCtx(), "Wybrana drużyna ma za mało graczy!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e(TAG, "Nieznany błąd");
            }
        }
    }


}