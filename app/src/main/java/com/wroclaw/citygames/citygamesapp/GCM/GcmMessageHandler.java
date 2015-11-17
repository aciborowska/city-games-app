package com.wroclaw.citygames.citygamesapp.GCM;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.ui.MainTaskActivity;
import com.wroclaw.citygames.citygamesapp.util.Gameplay;

public class GcmMessageHandler extends IntentService {
    private static final String TAG = GcmMessageHandler.class.getName();
    // private Handler handler;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //  handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        String[] mes = extras.getString("message").split(";");
        if (mes.length > 1) {
            Log.d(TAG, "Rozpoczęto grę gameId " + mes[1] + " teamId " + mes[2]);
            if (Gameplay.getCurrentGame() == -1) {
                Log.d(TAG, "Zarejestrowano grę");
                Gameplay.registerGame(Long.valueOf(mes[1]), Long.valueOf(mes[2]));
            }
        }
        String message = mes[0];
        String title = extras.getString("title");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSound(alarmSound)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message).setAutoCancel(true);

        Intent resultIntent = new Intent(this, MainTaskActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainTaskActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, notificationBuilder.build());
        Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("title"));
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

}
