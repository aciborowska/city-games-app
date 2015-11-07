package com.wroclaw.citygames.citygamesapp.test;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.util.Login;

public class LoginTest extends AndroidTestCase {

    public final String password1MD5 = "1a1dc91c907325c69271ddf0c944bc72";
    public final String password2MD5 = "2189d28285a400ca225f563ca89d4d3b";

    Context context;

    public void setUp() throws Exception {
        super.setUp();
        context = new MockContext();
        assertNotNull(context);
        App.setCtx(context);
    }


    public void testMMD5EncryptionRegular(){
        String password1 = "pass";
        String password2 = "nygus";
        String result = Login.MD5Encryption(password1);
        assertTrue(result.equals(password1MD5));

        result = Login.MD5Encryption(password2);
        assertTrue(result.equals(password2MD5));
    }

    public void testMMD5EncryptionFail(){
        String password1 = "pas";
        String password2 = "passs";
        String result = Login.MD5Encryption(password1);
        assertFalse(result.equals(password1MD5));

        result = Login.MD5Encryption(password2);
        assertFalse(result.equals(password1MD5));
    }

    public void testMD5EncryptionNull(){
        String result = Login.MD5Encryption(null);
        assertTrue(result == null);
    }

    public void testMD5EncryptionEmpty(){
        String result = Login.MD5Encryption("");
        assertTrue(result==null);
    }
}
