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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private final String TAG = LoginActivity.class.getName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setTypeface(Typeface.SERIF);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setTypeface(Typeface.SERIF);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                //attemptLogin();
                Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(intent);
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

        mProgressView = findViewById(R.id.login_progress);
        showProgress(false);
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
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return (password.length() > 4 && !password.contains("'"));
    }

    public void showProgress(final boolean show) {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    public class UserLoginTask extends AsyncTask<Void, Void, Player> {

        private final String username;
        private final String password;
        private boolean connection_error=false;

        UserLoginTask(String email, String password) {
            username = email;
            this.password = password;
        }

        @Override
        protected Player doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").encodedAuthority(Globals.MAIN_URL).appendEncodedPath(Globals.LOGIN_URI).appendQueryParameter("username",username).appendQueryParameter("password", password);
            String uri=builder.build().toString();
            Player player = null;
            try {
                player = restTemplate.getForObject(uri, Player.class);
            }catch(final Exception e){
                    connection_error=true;
                Log.d(TAG, "błąd połączenia");
            }
            return player;
        }

        @Override
        protected void onPostExecute(final Player success) {
            mAuthTask = null;
            showProgress(false);

            if (success!=null) {
                Toast toast =Toast.makeText(getApplicationContext(),"Zalogowany",Toast.LENGTH_LONG);
                toast.show();
                Login.login(success.getPlayerId());
            } else if(connection_error) {
                Toast toast =Toast.makeText(getApplicationContext(),getResources().getString(R.string.connection_error),Toast.LENGTH_LONG);
                toast.show();

            }else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

