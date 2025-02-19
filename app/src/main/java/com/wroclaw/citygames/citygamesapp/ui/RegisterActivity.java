package com.wroclaw.citygames.citygamesapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends Activity {

    private final String TAG = RegisterActivity.class.getName();
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private View progressView;
    private NewPlayerTask newPlayerTask;
    private GcmRegister gcmRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = (EditText) findViewById(R.id.register_email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.register_password_edit_text);
        confirmPasswordEditText = (EditText) findViewById(R.id.register_confirm_password_edit_text);
        progressView = findViewById(R.id.login_progress_register);

        registerButton = (Button) findViewById(R.id.register_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    emailEditText.setError(getResources().getString(R.string.error_field_required));
                    passwordEditText.setError(getResources().getString(R.string.error_field_required));
                    confirmPasswordEditText.setError(getResources().getString(R.string.error_field_required));
                } else {
                    int validation = Validation.validateRegistartion(email, password, confirmPassword);
                    switch (validation) {
                        case -1:
                            emailEditText.setError(getResources().getString(R.string.error_invalid_email));
                            break;
                        case -2:
                            passwordEditText.setError(getResources().getString(R.string.diffrent_passwords));
                            confirmPasswordEditText.setError(getResources().getString(R.string.diffrent_passwords));
                            break;
                        case -3:
                            passwordEditText.setError(getResources().getString(R.string.password_too_short));
                            break;
                        case 1:
                            emailEditText.setError(null);
                            passwordEditText.setError(null);
                            confirmPasswordEditText.setError(null);
                            if (Login.ifLogin()) Login.logout();
                            if (GCM.getGcmId().isEmpty()) {
                                gcmRegister = new GcmRegister();
                                gcmRegister.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                            }
                            newPlayerTask = new NewPlayerTask(email, password);
                            changeProgressView(true);
                            newPlayerTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }
                }


            }
        });
    }
    private void changeProgressView(boolean show){
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    private void startStartFragment(){
        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        startActivity(intent);
    }

    public class NewPlayerTask extends AsyncTask<Void, Void, Player> {

        private String email;
        private String password;
        boolean connection_error = false;

        public NewPlayerTask(String email, String password) {
            this.email = email;
            this.password = Login.MD5Encryption(password);


        }

        @Override
        protected Player doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            String uri = RestUriBuilder.registerUri();
            Player player = new Player();
            player.setEmail(email);
            player.setPassword(password);
            player.setDeviceId(GCM.getGcmId());
            Player created = null;
            try {
                created = restTemplate.postForObject(uri, player, Player.class);
            } catch (final Exception e) {
                e.printStackTrace();
                Log.d(TAG, "błąd połączenia");
                connection_error = true;
            }
            return created;
        }

        @Override
        protected void onPostExecute(Player player) {
            newPlayerTask=null;
            changeProgressView(false);

            if (player != null) {
                if(player.getPlayerId()==-101){
                    Toast.makeText(getApplicationContext(),"Podany email jest zajęty", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            getApplication().getString(R.string.toast_account_registered),
                            Toast.LENGTH_LONG).show();
                    Login.login(player.getPlayerId(), email);
                    startStartFragment();
                }
            } else if(connection_error){
                Toast.makeText(getApplicationContext(),
                        getApplication().getString(R.string.toast_connection_error),
                        Toast.LENGTH_LONG).show();
            }
            else {
               Toast.makeText(getApplicationContext(),
                        getApplication().getString(R.string.toast_email_exist_in_db),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
