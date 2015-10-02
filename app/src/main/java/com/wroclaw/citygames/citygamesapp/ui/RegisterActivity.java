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

import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Player;
import com.wroclaw.citygames.citygamesapp.utils.Validation;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RegisterActivity extends Activity {

    private final String TAG = RegisterActivity.class.getName();
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = (EditText) findViewById(R.id.register_email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.register_password_edit_text);
        confirmPasswordEditText = (EditText) findViewById(R.id.register_confirm_password_edit_text);

        registerButton = (Button) findViewById(R.id.register_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                    emailEditText.setError(getResources().getString(R.string.need_to_be_filled));
                    passwordEditText.setError(getResources().getString(R.string.need_to_be_filled));
                    confirmPasswordEditText.setError(getResources().getString(R.string.need_to_be_filled));
                }
                else {
                    int validation = Validation.validateRegistartion(email,password,confirmPassword);
                    switch(validation){
                        case -1: emailEditText.setError(getResources().getString(R.string.email_error)); break;
                        case -2: passwordEditText.setError(getResources().getString(R.string.diffrent_passwords));
                            confirmPasswordEditText.setError(getResources().getString(R.string.diffrent_passwords));
                            break;
                        case -3: passwordEditText.setError(getResources().getString(R.string.password_too_short)); break;
                        case 1:
                            emailEditText.setError(null);
                            passwordEditText.setError(null);
                            confirmPasswordEditText.setError(null);

                            NewPlayerTask newPlayerTask = new NewPlayerTask(email, password);
                            newPlayerTask.execute();
                    }
                }


            }
        });
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

    public class NewPlayerTask extends AsyncTask<Void,Void,Player>{

        String email;
        String password;

        public NewPlayerTask(String email, String password){
            this.email=email;
            this.password=password;


        }
        //TODO zaimplementować żadanie restowe
        @Override
        protected Player doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(Globals.MAIN_URL).appendEncodedPath(Globals.LOGIN_URI);
            String uri=builder.build().toString();
            Player player = null;
            try {
              //  player = restTemplate.getForObject(uri, Player.class);
            }catch(final Exception e){
                Log.d(TAG, "błąd połączenia");
            }
            return player;
        }

        @Override
        protected void onPostExecute(Player player) {
            super.onPostExecute(player);
        }
    }
}
