package com.example.helplight;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;


public class MapAct extends FragmentActivity {
	private GoogleMap map;
	String MAC="chris";
	private SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		getSharedPrefs();
		map=((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		getQueries();

	}
	@Override
	public void onResume(){
		super.onResume();
		getSharedPrefs();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMAC();
		//getSecureID();
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}
	public void getSharedPrefs(){
		prefs=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		MAC=prefs.getString("name", "");
	}
	public void getMAC(){
		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		MAC = info.getMacAddress();
	}
	public void getSecureID(){
		MAC=Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	}
	public void refresh(View v){
		map.clear();
		getQueries();
	}
	
	public List<ParseObject> getQueries(){
		List<ParseObject> p=new ArrayList<ParseObject>();
		ParseQuery<ParseObject> getLocations=ParseQuery.getQuery(MAC);
		getLocations.orderByAscending("times");
		final GoogleMap thisMap=map;
		getLocations.findInBackground(new FindCallback<ParseObject>(){


			@Override
			public void done(List<ParseObject> results, ParseException e) {
				// TODO Auto-generated method stub
				ParseObject last=null;
				for(ParseObject p:results){
					LatLng ln=new LatLng(Double.parseDouble(p.getString("latitude")),Double.parseDouble(p.getString("longitude")));
					thisMap.addMarker(
							new MarkerOptions().position(ln).title(p.getString("times")));
					if(last!=null){
						PolylineOptions line=
								new PolylineOptions().add(ln,
										new LatLng(Double.parseDouble(p.getString("latitude")),
												Double.parseDouble(p.getString("longitude")))
										)
										.width(5).color(Color.RED);

						map.addPolyline(line);
						last=p;
					}
					CameraUpdate point = CameraUpdateFactory.newLatLngZoom(ln,20.0f);
					map.moveCamera(point);
					map.animateCamera(point);
				}
			}
		}
				);
		return p; 
		}
	public void clearData(View v){ List<ParseObject> p=new ArrayList<ParseObject>();
	ParseQuery<ParseObject> getLocations=ParseQuery.getQuery(MAC);
	//getLocations.orderByAscending("times");
	getLocations.findInBackground(new FindCallback<ParseObject>(){
		@Override
		public void done(List<ParseObject> results, ParseException e){
			for(ParseObject p:results){
				p.deleteInBackground();
			}
		}
	});
		
	}
}
