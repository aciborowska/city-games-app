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
import android.view.MenuItem;

import com.wroclaw.citygames.citygamesapp.R;

public class MainTaskActivity extends FragmentActivity {

    private static final String TAG = MainTaskActivity.class.getName();

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Obiekt "kartownika" pozwalający na przechodzenie między fragmentami aktywności za
     * pomocą gestów
     */
    private ViewPager mainViewPager;
    /**
     * Obiekt adaptera na potrzeby "kartownika"
     */
    private MainViewPagerAdapter mainViewPagerAdapter;

    /*
    Stałe na potrzeby przekazywania parametrów przez Intencje
     */
    /**
     * Klucz do wyboru aktywnego fragmentu
     */
    public static final String SET_FRAGMENT = "SET_FRAGMENT";
    /**
     * Numer fragmentu ze zdjęciami
     */
    public static final int TASK_FRAGMENT_NUM = 1;
    /**
     * Numer fragmentu głównego
     */
    public static final int TIP_FRAGMENT_NUM = 0;
    /**
     * Numer fragmentu z podróżami
     */
    public static final int MAP_FRAGMENT_NUM = 2;

    /**
     * Numer startowego fragmentu
     */
    private static final int START_FRAGMENT = TASK_FRAGMENT_NUM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_task);

        Log.d(TAG, "onCreate");

        // Zainicjalizuj ViewPager
        mainViewPager = (ViewPager) findViewById(R.id.view_pager);
        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewPagerAdapter);

        handleIntent(getIntent());
        setTitle("Gra");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");

        handleIntent(intent);
    }


    /**
     * Metoda obsługująca nadejście nowej intencji.
     * W tej metodzie powinnien znaleźć się wszelki kod wypełniający aktywność danymi, które
     * zależą od intencji lub od jej braku!
     * @param intent intencja
     */
    protected void handleIntent(Intent intent) {
        // Załaduj fragment o numerze przekazanym przez intencję lub domyślny
        if(intent != null) {
            int setPage = intent.getIntExtra(SET_FRAGMENT, START_FRAGMENT);
            mainViewPager.setCurrentItem(setPage);

            // Przekaż do fragmentu extrasy jeśli są dostępne
            Bundle bundle = intent.getExtras();
            if(bundle != null) {

            }
        } else {
            // Załaduj domyślne dane w przypadku braku intencji
            mainViewPager.setCurrentItem(START_FRAGMENT);
        }
    }

    //Każdy fragment tworzy swoje menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d(TAG, "onOptionsItemSelected");
        //Pobieranie wybranej opcji menu
        int id = item.getItemId();
        return true;
    }

    /**
     * Klasa implementująca adapter dla "kartownika"
     */
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
            switch(i) {
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
            switch(position) {
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