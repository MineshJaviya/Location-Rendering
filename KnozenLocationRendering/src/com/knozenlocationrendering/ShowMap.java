package com.knozenlocationrendering;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ShowMap extends FragmentActivity {

	//Object to hold the position
	private LatLng Mark;
	private GoogleMap map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_map);
		
		//Retrieve the name of user
		String name=getIntent().getExtras().getString("name");
		
		//Retrieve the latitude
		String latitude=getIntent().getExtras().getString("latitude");
		
		//Retrieve the longitude
		String longitude=getIntent().getExtras().getString("longitude");
		
		//Initialize the mark object
		Mark=new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
		
		//Get the map fragment from layout
		map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.show_map)).getMap();
		
		//Add the marker on the map
		map.addMarker(new MarkerOptions().position(Mark).title(name));
		
		// Move the camera instantly to Mark with a zoom of 15.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Mark, 15));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		this.finish();
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		this.finish();
		super.onDestroy();
	}
	
	
}
