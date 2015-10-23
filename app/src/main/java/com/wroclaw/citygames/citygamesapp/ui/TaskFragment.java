package com.wroclaw.citygames.citygamesapp.ui;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Task;
import com.wroclaw.citygames.citygamesapp.util.Gameplay;
import com.wroclaw.citygames.citygamesapp.util.Login;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment implements View.OnClickListener {

    public static final String NAME = TaskFragment.class.getCanonicalName();
    public static final String TAG = TaskFragment.class.getName();
    public static final String TITLE = "Zadanie";

    private TextView description;
    private TextView question;
    private Button answerA;
    private Button answerB;
    private Button answerC;
    private ImageView picture;
    private GetNextTask getNextTask;
    private ProgressBar progressBar;


    public static TaskFragment newInstance() {
        TaskFragment myFragment = new TaskFragment();
        return myFragment;
    }


    public TaskFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        description = (TextView) getView().findViewById(R.id.task_description);
        question = (TextView) getView().findViewById(R.id.task_question);
        answerA = (Button) getView().findViewById(R.id.answer_a_button);
        answerA.setOnClickListener(this);
        answerB = (Button) getView().findViewById(R.id.answer_b_button);
        answerB.setOnClickListener(this);
        answerC = (Button) getView().findViewById(R.id.answer_c_button);
        answerC.setOnClickListener(this);
        progressBar = (ProgressBar) getView().findViewById(R.id.task_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        if (MainTaskActivity.currentTask.getTaskId() == -1) {
            String GET_NEXT_TASK = new Uri.Builder().scheme("http").encodedAuthority(Globals.MAIN_URL)
                    .appendPath(Globals.GAMEPLAY_URI)
                    .appendPath(String.valueOf(Gameplay.getCurrentGame()))
                    .appendEncodedPath(String.valueOf(Login.getCredentials()) + "?taskId="
                            + String.valueOf(MainTaskActivity.currentTask.getTaskId())
                            + "&answer=").build().toString();
            getNextTask = new GetNextTask(GET_NEXT_TASK + "?");
        } else {
            String GET_CURRENT_TASK = new Uri.Builder().scheme("http").encodedAuthority(Globals.MAIN_URL)
                    .appendPath(Globals.GAMEPLAY_URI)
                    .appendPath(String.valueOf(Gameplay.getCurrentGame()))
                    .appendEncodedPath("current_task").build().toString();
            getNextTask = new GetNextTask(GET_CURRENT_TASK);
        }
        getNextTask.execute();
    }


    protected void handleIntent() {

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "On Click");
        int id = v.getId();
        Button clicked = (Button) v.findViewById(id);
        if (clicked != null) {
            String answer = clicked.getText().toString();
            String GET_NEXT_TASK = new Uri.Builder().scheme("http").encodedAuthority(Globals.MAIN_URL)
                    .appendPath(Globals.GAMEPLAY_URI)
                    .appendPath(String.valueOf(Gameplay.getCurrentGame()))
                    .appendEncodedPath(String.valueOf(Login.getCredentials()) + "?taskId="
                            + String.valueOf(MainTaskActivity.currentTask.getTaskId())
                            + "&answer=").build().toString();
            getNextTask = new GetNextTask(GET_NEXT_TASK + answer);
            getNextTask.execute();
            progressBar.setVisibility(View.VISIBLE);
        }
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
            Log.d(TAG, "Pobrano");
            if (task != null) {
                if (task.getTaskId() == -300) {
                    MainTaskActivity.currentTask=null;
                    Gameplay.endGame();
                    String descriptionText = task.getDescription();
                    if (description != null) description.setText(descriptionText);
                    answerA.setVisibility(View.GONE);
                    answerB.setVisibility(View.GONE);
                    answerC.setVisibility(View.GONE);
                } else {
                    MainTaskActivity.currentTask = task;
                    String questionText = task.getQuestion();
                    String descriptionText = task.getDescription();
                    if (description != null) description.setText(descriptionText);
                    if (questionText != null) {
                        String[] makeQuestion = questionText.split(";");
                        question.setText(makeQuestion[0]);
                        if (makeQuestion.length > 2) {
                            answerA.setVisibility(View.VISIBLE);
                            answerA.setText(makeQuestion[1]);
                            answerB.setVisibility(View.VISIBLE);
                            answerB.setText(makeQuestion[2]);
                            answerC.setVisibility(View.VISIBLE);
                            answerC.setText(makeQuestion[3]);
                        } else {
                            //TODO pole na wpisanie odpowiedzi
                        }
                    }
                }
            }
        }
    }


}
