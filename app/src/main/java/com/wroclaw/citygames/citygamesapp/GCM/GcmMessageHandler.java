package com.wroclaw.citygames.citygamesapp.GCM;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.ui.MainTaskActivity;
import com.wroclaw.citygames.citygamesapp.util.Gameplay;

/**
 * Created by Aga on 2015-11-02.
 */
public class GcmMessageHandler extends IntentService {
    private static final String TAG  =GcmMessageHandler.class.getName();
    private Handler handler;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        String[] mes = extras.getString("message").split(";");
        if(mes.length>1) {
            Log.d(TAG,"Rozpoczęto grę gameId "+mes[1]+" teamId "+mes[2]);
            if(Gameplay.getCurrentGame()==-1) {
                Log.d(TAG,"Zarejestrowano grę");
                Gameplay.registerGame(Long.valueOf(mes[1]), Long.valueOf(mes[2]));
            }
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(extras.getString("title"))
                        .setContentText(mes[0]).setAutoCancel(true);

        Intent resultIntent = new Intent(this, MainTaskActivity.class);
        resultIntent.putExtra("isCurrent",true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainTaskActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        mNotificationManager.notify(0, mBuilder.build());
        Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("title"));

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

}
