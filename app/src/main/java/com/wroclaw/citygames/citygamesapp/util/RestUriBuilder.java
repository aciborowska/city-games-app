package com.wroclaw.citygames.citygamesapp.util;


import android.net.Uri;

import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.ui.MainTaskActivity;

public class RestUriBuilder {

    public static String loginUri(String email, String password){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").encodedAuthority(Globals.MAIN_URL)
                .appendEncodedPath(Globals.LOGIN_URI)
                .appendEncodedPath(GCM.getGcmId()).
                appendEncodedPath("?email=" + email + "&password=" + password);
        return builder.build().toString();
    }

    public static String registerUri(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").encodedAuthority(Globals.MAIN_URL).appendEncodedPath(Globals.REGISTER_URI);
        return builder.build().toString();
    }

    public static String createGetNextTaskUri(String userAnswer) {
        return new Uri.Builder().scheme("http")
                .encodedAuthority(Globals.MAIN_URL)
                .appendPath(Globals.GAMEPLAY_URI)
                .appendPath(String.valueOf(Gameplay.getCurrentGame()))
                .appendEncodedPath(String.valueOf(Login.getPlayerId()) + "?taskId="
                        + String.valueOf(MainTaskActivity.currentTask.getTask().getTaskId())
                        + "&answer=" + userAnswer)
                .build()
                .toString();
    }

    public static  String getCurrentTask() {
        return new Uri.Builder().scheme("http").encodedAuthority(Globals.MAIN_URL)
                .appendPath(Globals.GAMEPLAY_URI)
                .appendPath(String.valueOf(Gameplay.getCurrentGame()))
                .appendEncodedPath("current_task?playerId=" + String.valueOf(Login.getPlayerId()))
                .build()
                .toString();
    }


    public static String signForTask(String taskId) {
        return new Uri.Builder().scheme("http")
                .encodedAuthority(Globals.MAIN_URL)
                .appendPath(Globals.GAMEPLAY_URI)
                .appendPath(String.valueOf(Gameplay.getCurrentGame()))
                .appendEncodedPath("signfortask?taskId=" + taskId + "&playerId=" + String.valueOf(Login.getPlayerId()))
                .build()
                .toString();
    }
}
