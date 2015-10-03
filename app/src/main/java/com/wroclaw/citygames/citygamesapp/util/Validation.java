package com.wroclaw.citygames.citygamesapp.util;

public class Validation {

    public static boolean checkEmail(String email){
        return email.contains("@");
    }

    public static boolean checkPassword(String password){
        return password.length() >= 4;
    }

    public static int validateRegistartion(String email, String password, String confirmed_password){
        if(!checkEmail(email)) return -1;
        if(!password.equals(confirmed_password)) return -2;
        if(!checkPassword(password)) return -3;
        return 1;
    }
}