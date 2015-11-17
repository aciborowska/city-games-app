package com.wroclaw.citygames.citygamesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.dao.TaskDao;
import com.wroclaw.citygames.citygamesapp.service.LocationService;

public class MainTaskActivity extends FragmentActivity {

    public static final String TAG = MainTaskActivity.class.getName();

    public static TaskDao currentTask = new TaskDao();


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
        // Zainicjalizuj ViewPager
        mainViewPager = (ViewPager) findViewById(R.id.view_pager);
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewPagerAdapter);
        mainViewPager.setCurrentItem(START_FRAGMENT);
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        setTitle("Gra");
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
            taskFragment = new TaskFragment();
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

}