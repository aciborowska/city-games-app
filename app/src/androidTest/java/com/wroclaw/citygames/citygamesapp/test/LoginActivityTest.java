package com.wroclaw.citygames.citygamesapp.test;


import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.ui.LoginActivity;
import com.wroclaw.citygames.citygamesapp.util.Login;

public class LoginActivityTest  extends ActivityInstrumentationTestCase2<LoginActivity> {
    private LoginActivity loginActivity;
    private EditText emailView;
    private EditText passwordView;
    private View progressView;

    public LoginActivityTest(Class<LoginActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loginActivity = getActivity();
        passwordView = (EditText) loginActivity.findViewById(R.id.password);
        emailView = (EditText) loginActivity.findViewById(R.id.email);
        progressView = loginActivity.findViewById(R.id.login_progress);
    }

    public void testPreconditions() {
        assertNotNull("loginActivity null", loginActivity);
        assertNotNull("passwordTextField null", passwordView);
        assertNotNull("emailTextField null", emailView);
        assertNotNull("progressLogin null",progressView);
    }

    public void testPasswordField_labelText() {
        final String expected = "";
        final String actual = passwordView.getText().toString();
        assertEquals(expected, actual);
    }

    public void testEmailField_labelText() {
        final String expected = "";
        final String actual = emailView.getText().toString();
        assertEquals(expected, actual);
    }

    public void testProgressView_labelText() {
        assertTrue(progressView.getVisibility() == View.GONE);
    }

    public void testLogin(){
        Long playerId = new Long(1);
        String email = "mail@mail";
        assertNotNull(App.getCtx());
        Login.login(playerId, email);
        assertTrue(Login.getPlayerId() == playerId);
        assertTrue(Login.getEmail().equals(email));
        assertTrue(Login.ifLogin());
    }

    public void testLoginNullValues(){
        Long playerId = null;
        String email = null;
        Login.login(playerId,email);
        assertTrue(Login.getPlayerId() == playerId);
        assertTrue(Login.getEmail().equals(email));
        assertTrue(Login.ifLogin());
    }

    public void testLogout(){
        Login.logout();
        assertTrue(Login.getPlayerId() == (long) -1);
        assertTrue(Login.getEmail().equals(""));
        assertFalse(Login.ifLogin());
    }
}
