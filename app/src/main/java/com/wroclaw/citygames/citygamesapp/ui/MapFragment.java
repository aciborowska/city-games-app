package com.wroclaw.citygames.citygamesapp.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends SupportMapFragment implements Observer{
    public static final String NAME =  MapFragment.class.getCanonicalName();
    public static final String TAG =  MapFragment.class.getName();
    public static final String TITLE = "Mapa";
    private Marker taskMarker;
    private GoogleMap mapView;
    @Override
    public void onCreate(Bundle arg0) {
        Log.d(TAG,"onCreate");
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
        setLocation();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }


    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG,"update");
        setLocation();
    }

    private void setLocation(){
        if(taskMarker!=null) taskMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        if(MainTaskActivity.currentTask!=null){
            float longitude = MainTaskActivity.currentTask.getTask().getLongitude();
            float latitude = MainTaskActivity.currentTask.getTask().getLatitude();
            Log.d(TAG,"Współrzędne "+String.valueOf(latitude)+" "+String.valueOf(longitude));
            LatLng location = new LatLng(latitude, longitude);
            markerOptions.position(location);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
            taskMarker = mapView.addMarker(markerOptions);
            mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
        }
    }
}


/*extends Fragment {
    public static final String NAME =  MapFragment.class.getCanonicalName();
    public static final String TAG =  MapFragment.class.getName();
    public static final String TITLE = "Mapa";

    private MapView mapView;
    private GoogleMap map;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        //View layout = super.onCreateView(inflater, view, savedInstance);
        FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.setBackgroundColor(
                getResources().getColor(android.R.color.transparent));
        ((ViewGroup) v).addView(frameLayout,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT
                )
        );

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            Log.e("Address Map", "Could not initialize google play", e);
        }

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) )
        {
            case ConnectionResult.SUCCESS:
                Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();
                mapView = (MapView) v.findViewById(R.id.map);
                mapView.onCreate(savedInstanceState);
                // Gets to GoogleMap from the MapView and does initialization stuff
                if(mapView!=null)
                {
                    map = mapView.getMap();
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    map.setMyLocationEnabled(true);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
                    map.animateCamera(cameraUpdate);
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                break;
            default: Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }




        // Updates the location and zoom of the MapView

        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}*/
