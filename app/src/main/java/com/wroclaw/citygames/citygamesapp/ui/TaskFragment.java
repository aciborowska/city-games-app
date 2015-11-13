package com.wroclaw.citygames.citygamesapp.ui;


import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Game;
import com.wroclaw.citygames.citygamesapp.model.Task;
import com.wroclaw.citygames.citygamesapp.util.Gameplay;
import com.wroclaw.citygames.citygamesapp.util.ImageConverter;
import com.wroclaw.citygames.citygamesapp.util.RestUriBuilder;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Observable;
import java.util.Observer;

import static com.wroclaw.citygames.citygamesapp.R.id.task_picture;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment implements View.OnClickListener, Observer {

    public static final String NAME = TaskFragment.class.getCanonicalName();
    public static final String TAG = TaskFragment.class.getName();
    public static final String TITLE = "Zadanie";

    private TextView description;
    private TextView question;
    private TextView answer;
    private Button answerA;
    private Button answerB;
    private Button answerC;
    private ImageView picture;
    private GetNextTask getNextTask;
    private GetGame getGame;
    private ProgressBar progressBar;

    public static TaskFragment newInstance(boolean isCurrent) {
        TaskFragment myFragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putBoolean("isCurrent", isCurrent);
        myFragment.setArguments(args);
        return myFragment;
    }


    public TaskFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_task_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.action_task_refresh:
                getNextTask = new GetNextTask(RestUriBuilder.getCurrentTask());
                getNextTask.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(MainTaskActivity.currentTask!=null)
            MainTaskActivity.currentTask.addObserver(this);
        picture = (ImageView) getView().findViewById(task_picture);
        description = (TextView) getView().findViewById(R.id.task_description);
        question = (TextView) getView().findViewById(R.id.task_question);
        answer = (TextView) getView().findViewById(R.id.answer_edit_text);
        answerA = (Button) getView().findViewById(R.id.answer_a_button);
        answerA.setOnClickListener(this);
        answerB = (Button) getView().findViewById(R.id.answer_b_button);
        answerB.setOnClickListener(this);
        answerC = (Button) getView().findViewById(R.id.answer_c_button);
        answerC.setOnClickListener(this);
        progressBar = (ProgressBar) getView().findViewById(R.id.task_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        handleIntent();

    }

    private void handleIntent() {
        Log.d(TAG, "handleIntent");
        this.getNextTask = new GetNextTask(RestUriBuilder.getCurrentTask());
        getNextTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

    }



    @Override
    public void onClick(View v) {
        Log.d(TAG, "On Click");
        int id = v.getId();
        Button clicked = (Button) v.findViewById(id);
        if (clicked != null) {
            if (MainTaskActivity.currentTask != null)
                if (MainTaskActivity.currentTask.getTask().getTaskId() == Globals.CHOICE_TASK) {
                    String choice = clicked.getText().toString();
                    String taskId = MainTaskActivity.currentTask.getIdForTask(choice);
                    String getNextTask = RestUriBuilder.signForTask(taskId);
                    this.getNextTask = new GetNextTask(getNextTask);
                    this.getNextTask.execute();
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    String userAnswer = clicked.getText().toString();
                    if (userAnswer.equals("Wyślij odpowiedź"))
                        userAnswer = answer.getText().toString();
                    String getNextTask = RestUriBuilder.createGetNextTaskUri(userAnswer);
                    this.getNextTask = new GetNextTask(getNextTask);
                    this.getNextTask.execute();
                    progressBar.setVisibility(View.VISIBLE);
                }
            else {
                getGame = new GetGame();
                getGame.execute(Gameplay.getCurrentGame());

            }
        }
    }

    private void refreshData() {
        Log.d(TAG, "refreshData ");
        Task task = MainTaskActivity.currentTask.getTask();
        if(task.getTaskId()== Globals.UPDATE_TASK){
            Toast.makeText(App.getCtx(),"Zadanie jest nieaktualne, pobieram nowe...",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            getNextTask = new GetNextTask(RestUriBuilder.getCurrentTask());
            getNextTask.execute();
            return;
        }
        cleanView();

        if (task.getPicture() != null) {
            picture.setImageResource(android.R.color.transparent);
            Bitmap taskImage = ImageConverter.loadBitmap(task.getPicture());
            picture.setImageBitmap(taskImage);

        } else {
            picture.setImageResource(R.drawable.question1);
        }

        String questionText = task.getQuestion();
        String descriptionText = task.getDescription();
        if (description != null) description.setText(descriptionText);
        if (questionText != null) {
            if (task.getTaskId() == Globals.CHOICE_TASK) questionText = questionText.split("#")[0];
            String[] makeQuestion = questionText.split(";");
            question.setText(makeQuestion[0]);
            int amount = makeQuestion.length;
            if (amount > 1) {
                Log.d(TAG, "liczba zadań: " + amount);
                answer.setVisibility(View.GONE);
                amount -= 1;
                switch (amount) {
                    case 3:
                        answerC.setVisibility(View.VISIBLE);
                        answerC.setText(makeQuestion[3]);
                    case 2:
                        answerB.setVisibility(View.VISIBLE);
                        answerB.setText(makeQuestion[2]);
                    case 1:
                        answerA.setVisibility(View.VISIBLE);
                        answerA.setText(makeQuestion[1]);
                }
            } else {
                answer.setVisibility(View.VISIBLE);
                answerA.setVisibility(View.VISIBLE);
                answerA.setText("Wyślij odpowiedź");
                answerB.setVisibility(View.GONE);
                answerC.setVisibility(View.GONE);
            }
        }
    }

    private void cleanView(){
        answerA.setVisibility(View.GONE);
        answerB.setVisibility(View.GONE);
        answerC.setVisibility(View.GONE);
        answer.setVisibility(View.GONE);
        question.setText("");
        description.setText("");
        picture.setImageResource(R.drawable.question1);
    }
    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "update ");
        refreshData();
    }

    public class GetNextTask extends AsyncTask<Void, Void, Task> {

        private String uri;

        public GetNextTask(String uri) {
            this.uri = uri;
        }

        @Override
        protected Task doInBackground(Void... params) {
            Log.d(TAG, "Pobieranie zadania...");
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Task task = null;
            try {
                Log.d(TAG, uri);
                task = restTemplate.getForObject(uri, Task.class);
            } catch (final Exception e) {
                Log.d(TAG, "błąd połączenia");
                e.printStackTrace();
            }
            return task;
        }

        @Override
        protected void onPostExecute(Task task) {
            getNextTask = null;
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "Pobrano " + task.toString());
            if (task != null) {
                if (task.getTaskId() == Globals.FINISH_TASK) {
                    MainTaskActivity.currentTask=null;
                    String descriptionText = task.getDescription();
                    if (description != null) description.setText(descriptionText);
                    question.setText("");
                    answerA.setVisibility(View.VISIBLE);
                    answerA.setText("Koniec");
                    answerB.setVisibility(View.GONE);
                    answerC.setVisibility(View.GONE);
                } else {
                    MainTaskActivity.currentTask.setTask(task);
                }
            }
        }
    }

    public class GetGame extends AsyncTask<Long, Void, Game> {

        @Override
        protected Game doInBackground(Long... params) {
            Log.d(TAG, "Pobieranie zakończonej gry");
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Game game = null;
            try {
                String getGame = new Uri.Builder().scheme("http")
                        .encodedAuthority(Globals.MAIN_URL)
                        .appendPath(Globals.GAMEPLAY_URI)
                        .appendEncodedPath(Globals.GET_FINISHED_GAME_URI)
                        .appendQueryParameter("gameId", String.valueOf(Gameplay.getCurrentGame()))
                        .appendQueryParameter("penaltyPoints", String.valueOf(Gameplay.getPenaltyPoints()))
                        .build()
                        .toString();
                Log.d(TAG, getGame);
                game = restTemplate.getForObject(getGame, Game.class);
            } catch (final Exception e) {
                Log.d(TAG, "błąd połączenia");
                e.printStackTrace();
            }
            return game;
        }

        @Override
        protected void onPostExecute(Game game) {
            getGame = null;
            if (game != null) {
                game.setTeamId(Gameplay.getCurrentGameTeam());
                App.getGameDao().update(game);
                Log.d(TAG, "zapisano grę " + game.toString());
                Gameplay.endGame();
                getActivity().finish();
            } else {
                Toast.makeText(App.getCtx(), App.getCtx().getString(R.string.toast_connection_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
