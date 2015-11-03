package com.wroclaw.citygames.citygamesapp.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wroclaw.citygames.citygamesapp.App;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends SupportMapFragment implements Observer {
    public static final String NAME = MapFragment.class.getCanonicalName();
    public static final String TAG = MapFragment.class.getName();
    public static final String TITLE = "Mapa";
    private Marker taskMarker;
    private GoogleMap mapView;

    @Override
    public void onCreate(Bundle arg0) {
        Log.d(TAG, "onCreate");
        super.onCreate(arg0);
    }

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup arg1,
                             Bundle arg2) {
        Log.d(TAG, "onCreateView");
        return super.onCreateView(mInflater, arg1, arg2);
    }

    @Override
    public void onInflate(Activity arg0, AttributeSet arg1, Bundle arg2) {
        Log.d(TAG, "onInflate");
        super.onInflate(arg0, arg1, arg2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        MainTaskActivity.currentTask.addObserver(this);
        mapView = getMap();
        mapView.setMyLocationEnabled(true);
        mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setLocation(true);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }


    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "update");
        setLocation(false);
    }

    private void setLocation(boolean start) {
        if (taskMarker != null) taskMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        if (MainTaskActivity.currentTask != null) {
            double longitude = MainTaskActivity.currentTask.getTask().getLongitude();
            double latitude = MainTaskActivity.currentTask.getTask().getLatitude();
            if (longitude != 0 && latitude != 0) {
                if(!start)Toast.makeText(App.getCtx(), "Zobacz zadanie na mapie", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Współrzędne " + String.valueOf(latitude) + " " + String.valueOf(longitude));
                LatLng location = new LatLng(latitude, longitude);
                markerOptions.position(location);
                markerOptions.title(MainTaskActivity.currentTask.getTask().getDescription());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
                taskMarker = mapView.addMarker(markerOptions);
                mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
            } else {
                if(!start)Toast.makeText(App.getCtx(), "Zadanie bez lokalizacji", Toast.LENGTH_SHORT).show();
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);

                Location myLocation = locationManager.getLastKnownLocation(provider);

                if(myLocation!=null ){
                    latitude = myLocation.getLatitude();
                    longitude = myLocation.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    mapView.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mapView.animateCamera(CameraUpdateFactory.zoomTo(16));
                }
                else Toast.makeText(App.getCtx(),"Usługa lokalizacji wyłączona",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
