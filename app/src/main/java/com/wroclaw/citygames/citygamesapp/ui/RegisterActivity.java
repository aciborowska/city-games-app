package com.wroclaw.citygames.citygamesapp.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Player;
import com.wroclaw.citygames.citygamesapp.util.Login;
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
                            newPlayerTask = new NewPlayerTask(email, password);
                            changeProgressView(true);
                            newPlayerTask.execute();
                    }
                }


            }
        });
    }
    private void changeProgressView(boolean show){
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class NewPlayerTask extends AsyncTask<Void, Void, Player> {

        private String email;
        private String password;
        boolean connection_error = false;

        public NewPlayerTask(String email, String password) {
            this.email = email;
            this.password = password;


        }

        @Override
        protected Player doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(Globals.MAIN_URL).appendEncodedPath(Globals.REGISTER_URI);
            String uri = builder.build().toString();
            Player player = new Player();
            player.setEmail(email);
            player.setPassword(password);
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
                Toast toast = Toast.makeText(getApplicationContext(), "Utworzono konto", Toast.LENGTH_LONG);
                toast.show();
                Login.login(player.getPlayerId(),getApplicationContext());
            } else if(connection_error){
                Toast toast = Toast.makeText(getApplicationContext(), "Błąd połączenia, spróbuj ponownie później", Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(), "Podany email istnieje już w bazie", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
