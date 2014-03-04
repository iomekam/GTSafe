package com.example.gtsafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gtsafe.library.MapHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class CrimeMapActivity extends Activity {
private GoogleMap mMap;
private MapHelper helper;
private LatLngBounds coords = new LatLngBounds(
    new LatLng(33.770836, -84.407272), new LatLng(33.786638, -84.390492));

protected void onCreate(Bundle savedInstanceState) {


super.onCreate(savedInstanceState);
setContentView(R.layout.activity_map);

try {
    // Loading map
    initilizeMap();

} catch (Exception e) {
    e.printStackTrace();
}
}

private void initilizeMap() {
if (mMap == null) {
    mMap = ((MapFragment) getFragmentManager().findFragmentById(
            R.id.map)).getMap();
    mMap.setMyLocationEnabled(true);
    helper = new MapHelper(mMap);
    mMap = helper.populateZones();
    mMap.setOnMapClickListener(new OnMapClickListener() {

        @Override
        public void onMapClick(LatLng point) {
            //Log.d("Map","Map clicked");
            //marker.remove();
            //drawMarker(point);
        	
        }
    });

    if (mMap == null) {
        Toast.makeText(getApplicationContext(),
                "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                .show();
    }
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords.getCenter(), 15));
}
    else{
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords.getCenter(), 15));
}
}
@Override
protected void onResume() {
super.onResume();
initilizeMap();
}



}
