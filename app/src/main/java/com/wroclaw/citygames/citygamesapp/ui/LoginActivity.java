package com.wroclaw.citygames.citygamesapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.model.Player;
import com.wroclaw.citygames.citygamesapp.util.Login;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class LoginActivity extends Activity {

    private final String TAG = LoginActivity.class.getName();

    private UserLoginTask loginTask = null;
    private EditText emailView;
    private EditText passwordView;
    private View progressView;


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
                if(Login.ifLogin()) startStartFragment(false);
                attemptLogin();
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        progressView = findViewById(R.id.login_progress);
        showProgress(false);
        if(Login.ifLogin()) startStartFragment(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if(!Login.ifLogin()){
            passwordView.setText("");
            emailView.setText("");
            Log.d(TAG, "onResume - nie zalogowany");
        }
        else{
            emailView.setText(Login.getEmail());
            String fake_pass=null;
            for(int i=0;i<Login.getPasswordLength();i++) fake_pass+="a";
                passwordView.setText(fake_pass);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();

        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (loginTask != null) {
            return;
        }

        emailView.setError(null);
        passwordView.setError(null);

        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_incorrect_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            Login.logout();
            loginTask = new UserLoginTask(email, password);
            loginTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return (password.length() >= 4 && !password.contains("'"));
    }

    private void startStartFragment(boolean isNewLogin){
        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        intent.putExtra("isNewLogin",isNewLogin);
        startActivity(intent);
    }
    public void showProgress(final boolean show) {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    public class UserLoginTask extends AsyncTask<Void, Void, Player> {

        private final String username;
        private final String password;
        private boolean connection_error=false;

        UserLoginTask(String email, String password) {
            username = email;
            this.password = Login.md5(password);
        }

        @Override
        protected Player doInBackground(Void... params) {
            Log.d(TAG,"logowanie");
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(Globals.MAIN_URL).appendEncodedPath(Globals.LOGIN_URI).appendEncodedPath("?email=" + username+"&password="+password);
            String uri=builder.build().toString();
            Player player = null;
            try {
                Log.d(TAG,uri);
                player = restTemplate.getForObject(uri, Player.class);
            }catch(final Exception e){
                    connection_error=true;
                    e.printStackTrace();
                    Log.d(TAG, "błąd połączenia");
            }
            return player;
        }

        @Override
        protected void onPostExecute(final Player success) {
            loginTask = null;
            showProgress(false);

            if (success!=null) {
                Toast toast =Toast.makeText(getApplicationContext(),"Zalogowany",Toast.LENGTH_LONG);
                toast.show();
                Login.login(success.getPlayerId(),username,password.length());
                startStartFragment(true);
            } else if(connection_error) {
                Toast toast =Toast.makeText(getApplicationContext(),getResources().getString(R.string.connection_error),Toast.LENGTH_LONG);
                toast.show();

            }else {
                Log.d(TAG, "success == null");
                    passwordView.setError(getString(R.string.error_incorrect_password));
                    passwordView.requestFocus();
                }
        }

        @Override
        protected void onCancelled() {
            loginTask = null;
            showProgress(false);
        }
    }
}

