package com.wroclaw.citygames.citygamesapp.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.App;
import com.wroclaw.citygames.citygamesapp.R;
import com.wroclaw.citygames.citygamesapp.ui.MainTaskActivity;

public class LocationService extends Service {

    private static final String TAG = LocationService.class.getName();

    private static final int LOCATION_TIME_INTERVAL = 1000; // in millis
    private static final int LOCATION_DIST_INTERVAL = 3; // in meters
    private double targetLongitude;
    private double targetLatitude;
    private LocationListener locationListener;
    private LocationManager locationManager;
    public LocationService() {
        Log.d(TAG, " locationService created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        return START_REDELIVER_INTENT;
    }

    protected void onHandleIntent(Intent intent) {
        String intentAction = intent.getStringExtra("service");
        if (intentAction == null) {
            Log.w(TAG, "onHandleIntent: intentAction is null");
        } else if (intentAction.equals(getString(R.string.startTrackingIntent))) {
            targetLongitude = MainTaskActivity.currentTask.getTask().getLongitude();
            targetLatitude = MainTaskActivity.currentTask.getTask().getLatitude();
            Log.d(TAG, "LocationService: targetLatitue " + String.valueOf(targetLatitude) + ", targetLongitude " + targetLongitude);
            startTracking();
        } else if (intentAction.equals(getString(R.string.stopTrackingIntent))) {
            stopTracking();
        } else {
            Log.w(TAG, "onHandleIntent: unknown intentAction");
        }
    }

    private void checkLocation(Location location) {
        if (location == null)
            Log.e(TAG, "checkLocation: no known location");
        else {
            Log.d(TAG, "checkLocation: location: " + location.toString());
            float result[] = new float[3];

            Location.distanceBetween(targetLatitude, targetLongitude, location.getLatitude(), location.getLongitude(), result);
            float distanceInMeters = result[0];
            boolean isWithinRange = distanceInMeters < 1000;
            if (isWithinRange) {
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(App.getCtx().getString(R.string.app_name))
                                .setSound(alarmSound)
                                .setContentText("Znajdujesz się blisko zadania").setAutoCancel(true);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(0, mBuilder.build());
                stopTracking();
            }
        }
    }

    private void startTracking() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            Log.d(TAG, "startTracking: provider: " + provider);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onLocationChanged: location: " + location);
                    checkLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.d(TAG, "onProviderEnabled");
                    provider = locationManager.getBestProvider(criteria, true);
                    if (provider != null) {
                        Log.d(TAG, "onProviderEnabled: provider: " + provider);
                        locationManager.requestLocationUpdates(provider, LOCATION_TIME_INTERVAL, LOCATION_DIST_INTERVAL, locationListener);
                    } else {
                        Log.w(TAG, "onProviderEnabled: no provider");
                    }
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.d(TAG, "onProviderDisabled");
                    provider = locationManager.getBestProvider(criteria, true);
                    if (provider != null) {
                        Log.d(TAG, "onProviderDisabled: provider: " + provider);
                        locationManager.requestLocationUpdates(provider, LOCATION_TIME_INTERVAL, LOCATION_DIST_INTERVAL, locationListener);
                    } else {
                        Log.w(TAG, "onProviderDisabled: no provider");
                    }
                }
            };
            locationManager.requestLocationUpdates(provider, LOCATION_TIME_INTERVAL, LOCATION_DIST_INTERVAL, locationListener);
        } else {
            Log.w(TAG, "startTracking: no provider avaliable");
        }
    }

    private void stopTracking() {
        Log.d(TAG, "stopTracking");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
        stopSelf();
    }
}