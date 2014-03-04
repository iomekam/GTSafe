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
    /*
    Polygon polygon = mMap.addPolygon(new PolygonOptions()
            .add(new LatLng(33.785817, -84.407058), new LatLng(33.781573, -84.407058), new LatLng(33.781680, -84.391522), new LatLng(33.786103, -84.391909))
            .strokeColor(Color.BLACK)
            .fillColor(Color.argb(50, 200, 0, 0)));
    Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
            .add(new LatLng(33.781323, -84.407144), new LatLng(33.781288, -84.396501), new LatLng(33.777560, -84.396737), new LatLng(33.777542, -84.407208))
            .strokeColor(Color.BLACK)
            .fillColor(Color.argb(50, 0, 200, 0)));
    Polygon polygon2 = mMap.addPolygon(new PolygonOptions()
            .add(new LatLng(33.776365, -84.396157), new LatLng(33.781323, -84.396200), new LatLng(33.781288, -84.391351), new LatLng(33.776329, -84.391007))
            .strokeColor(Color.BLACK)
            .fillColor(Color.argb(50, 100, 100, 0)));
    Polygon polygon3 = mMap.addPolygon(new PolygonOptions()
            .add(new LatLng(33.776365, -84.396200), new LatLng(33.776686, -84.390879), new LatLng(33.771406, -84.390364), new LatLng(33.771620, -84.395986))
            .strokeColor(Color.BLACK)
            .fillColor(Color.argb(50, 0, 0, 200)));
    Polygon polygon4 = mMap.addPolygon(new PolygonOptions()
            .add(new LatLng(33.771620, -84.395986), new LatLng(33.776650, -84.396157), new LatLng(33.7772570,-84.406757))
            .strokeColor(Color.BLACK)
            .fillColor(Color.argb(50, 100, 0, 100)));
    // check if map is created successfully or not
     * 
     */
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
