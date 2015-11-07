package com.wroclaw.citygames.citygamesapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Player;
import com.wroclaw.citygames.citygamesapp.task.GcmRegister;
import com.wroclaw.citygames.citygamesapp.util.GCM;
import com.wroclaw.citygames.citygamesapp.util.Login;
import com.wroclaw.citygames.citygamesapp.util.RestUriBuilder;
import com.wroclaw.citygames.citygamesapp.util.Validation;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class LoginActivity extends Activity {

    private final String TAG = LoginActivity.class.getName();

    private UserLoginTask loginTask = null;
    private EditText emailView;
    private EditText passwordView;
    private View progressView;
    private GcmRegister gcmRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailView = (EditText) findViewById(R.id.email);
        emailView.setTypeface(Typeface.SERIF);
        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setTypeface(Typeface.SERIF);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {

                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signinButton = (Button) findViewById(R.id.sign_in_button);
        signinButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Login.ifLogin()) startStartFragment(false);
                attemptLogin();
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        progressView = findViewById(R.id.login_progress);
        progressView.setVisibility(View.GONE);
        if (Login.ifLogin()) startStartFragment(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Login.ifLogin()) {
            passwordView.setText("");
            emailView.setText("");
        } else {
            emailView.setText(Login.getEmail());
            passwordView.setText("xxxxxxxx");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void attemptLogin() {
        if (loginTask != null) {
            return;
        }

        emailView.setError(null);
        passwordView.setError(null);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (password.isEmpty() || !Validation.checkPassword(password)) {
            passwordView.setError(getString(R.string.error_incorrect_password));
            focusView = passwordView;
            cancel = true;
        }

        if (email.isEmpty()) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            progressView.setVisibility(View.VISIBLE);
            Login.logout();
            if (GCM.getGcmId().isEmpty()) {
                gcmRegister = new GcmRegister();
                gcmRegister.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
            loginTask = null;
            loginTask = new UserLoginTask(email, password);
            loginTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    private void startStartFragment(boolean isNewLogin) {
        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        intent.putExtra("isNewLogin", isNewLogin);
        startActivity(intent);
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Player> {

        private final String email;
        private final String password;
        private boolean connection_error = false;

        UserLoginTask(String email, String password) {
            this.email = email;
            this.password = Login.MD5Encryption(password);
        }

        @Override
        protected Player doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            String uri = RestUriBuilder.loginUri(email,password);
            Player player = null;
            try {
                Log.d(TAG, "logowanie" + uri);
                player = restTemplate.getForObject(uri, Player.class);
            } catch (final Exception e) {
                connection_error = true;
                e.printStackTrace();
                Log.d(TAG, "błąd połączenia");
            }
            return player;
        }

        @Override
        protected void onPostExecute(final Player success) {
            progressView.setVisibility(View.GONE);
            if (success != null) {
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.toast_login),
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "AAAAAA "+String.valueOf(success.getPlayerId()));
                Login.login(success.getPlayerId(), email);
                startStartFragment(true);
            } else if (connection_error) {
                Toast.makeText(getApplicationContext(),
                        getApplicationContext().getString(R.string.toast_connection_error),
                        Toast.LENGTH_LONG).show();


            } else {
                Log.d(TAG, "błędne dane do logowania");
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            progressView.setVisibility(View.GONE);
        }
    }
}

