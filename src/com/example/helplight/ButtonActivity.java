package com.example.helplight;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.format.Time;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseAnalytics;
public class ButtonActivity extends Activity implements LocationListener {
	private static final String NAME_PREF = "name";
	private static final String PASS_PREFS = "pass";
	String locs="";
	Location loc;
	boolean loop;
	long updateTime=0;
	float updateDist=(float) 0.1;
	private AlertDialog passAD;
	private AlertDialog nameGetter;
	private AlertDialog passGetter;
	String number;
	TextView text;
	boolean sendData=false;
	LocationManager locationManager;
	ImageButton bigBlue;
	String passCode="";
	TextView nameBox;
	TextView passBox;
	final int messageDismissal=0;
	ParseObject testObject;
	long passTime=5000;
	SharedPreferences prefs;
	int timesSent=0;
	String MAC="";
	//ParseObject testObject;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Parse.initialize(this, "sLrgOGkGmC2zfSbo7mNBeIBEnS4G94hX8YKoCcoC", "UO2qIeybCwGx2CRXtuayDKsFTn9indiIAf4IPU8q");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text=(TextView)findViewById(R.id.latView);
		getSharedPrefs();
		getName();
		getPass(); 
		//text.setText(passCode);
		testObject = new ParseObject(MAC);
		startLocation();
		bigBlue=(ImageButton)findViewById(R.id.bluebutton);
		setListeners();	
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}
	public void changeName(View v){
		MAC="";
		getName();
		sendData=false;
	}
	public void changePass(View v){
		passCode="";
		getPass();
	}
	public void getPass(){
		if(passCode.equals("")){
			AlertDialog.Builder alert=new AlertDialog.Builder(this);
			alert.setTitle("Enter New Password");
			alert.setMessage("Please Enter Your New Password");
			final EditText passGet=new EditText(this);
			passGet.setInputType(InputType.TYPE_CLASS_TEXT);
			passGet.setTransformationMethod(PasswordTransformationMethod.getInstance());
			alert.setView(passGet);
			alert.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					String value=passGet.getText().toString();
					setSharedPrefsPass(value);
					passCode=value;
					{
						
					}
				}
			});
			passGetter=alert.create();
			passGetter.show();
		}
	}
	public void fillFields(View v){
		nameBox=(TextView)findViewById(R.id.nameBox);
		nameBox.setText(MAC);
		passBox=(TextView)findViewById(R.id.passBox);
		passBox.setText(passCode+"a");
		
	}
	public void getName(){
		if(MAC.equals("")||MAC.equals("No Name Entered")){
			AlertDialog.Builder alert=new AlertDialog.Builder(this);
			alert.setTitle("Enter Name");
			alert.setMessage("Please Enter Your Name or an Identifier Tag");
			final EditText nameGet=new EditText(this);
			nameGet.setInputType(InputType.TYPE_CLASS_TEXT);
			nameGet.setTransformationMethod(PasswordTransformationMethod.getInstance());
			alert.setView(nameGet);
			alert.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					String value=nameGet.getText().toString();
					setSharedPrefsName(value);
					MAC=value;
					{
						
					}
				}
			});
			nameGetter=alert.create();
			nameGetter.show();
		}
	}
	public void stopSending(View v){
		sendData=false;
	}
	public void getSharedPrefs(){
		prefs=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		MAC=prefs.getString(NAME_PREF, "No Name Entered");
		passCode=prefs.getString(PASS_PREFS, "");
		System.out.println(prefs.getString(PASS_PREFS,""));
	}
	public void setSharedPrefsName(String name){
		prefs=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor editor=prefs.edit();
		editor.putString(NAME_PREF, name);
		editor.commit();
	}
	public void setSharedPrefsPass(String pass){
		prefs=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Editor editor=prefs.edit();
		editor.putString(PASS_PREFS, pass);
		editor.commit();
	}
	public void startLocation(){
		locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,updateTime,	updateDist,	this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,updateTime,updateDist,this);
	}
public void getMAC(){
	WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	WifiInfo info = manager.getConnectionInfo();
	MAC = info.getMacAddress();
}
public void getSecureID(){
	MAC=Secure.getString(getContentResolver(), Secure.ANDROID_ID);
}
	public void setListeners(){
		bigBlue.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
					startSending();
				if(event.getAction()==MotionEvent.ACTION_UP)
					showPasswordScreen();
				return false;
			}
		});
	}

	public void startSending(){
		text.setText("now sending data!!");
		sendData=true;
		//Toast.makeText(this, passCode, Toast.LENGTH_SHORT).show();
	}
	public void sendCoords(Location location){
		loc=location;
		Time now=new Time();
		now.setToNow();
		if(sendData){
			String str = "Latitude: "+loc.getLatitude()+"Longitude:"+loc.getLongitude();

			Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
			text.setText("now sending data");
			testObject.put("order", timesSent+"");
			testObject.put("latitude",(Double)loc.getLatitude()+"");
			testObject.put("longitude",(Double)loc.getLongitude()+"");
			testObject.saveInBackground();
			testObject=new ParseObject(MAC);
			timesSent++;
		}
		else text.setText("Not Sending Data");
	}
	public void showPasswordScreen(){
		Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
		createPassAlert();
	}
	public void createPassAlert(){
		AlertDialog.Builder alert=new AlertDialog.Builder(this);
		alert.setTitle("Enter Passkey");
		alert.setMessage("Enter your initially input passcode");
		final EditText pass=new EditText(this);
		pass.setInputType(InputType.TYPE_CLASS_NUMBER);
		pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
		alert.setView(pass);
		alert.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				String value=pass.getText().toString();
				if(!value.equals(passCode)){
					contactPolice();
				}
				else{sendData=false;}
			}
		});
		alert.setNegativeButton("CONTACT POLICE IMMEDIATELY", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				contactPolice();

			}
		});
		passAD=alert.create();
		passAD.show();
		passHandler.sendEmptyMessageDelayed(messageDismissal, passTime);
	}
	//Uses handler to close alert and contact police if password dialog is
	//open more than XXX amount of time. Processes message sent by passAD
	private Handler passHandler=new Handler(){
		public void handleMessage(android.os.Message msg){
			switch(msg.what){
			case  messageDismissal:
				if(passAD!=null&&passAD.isShowing()){
					passAD.dismiss();
					contactPolice();
				}
				break;
			default:
				break;
			}
		}
	};
	public void contactPolice(){
		AlertDialog.Builder popoAlert=new AlertDialog.Builder(this);
		popoAlert.setTitle("Police Contacted");
		popoAlert.setMessage("The police have been contacted and given your " +
				"location information");
		popoAlert.setPositiveButton("ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});

		// todo need to make it actually contact police
		popoAlert.show();
	}
	public void goManager(View v){
		final Intent managerIntent=new Intent(this,ManagerActivity.class);
		managerIntent.putExtra("EXIT",false);
		managerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(managerIntent);
	}
	public void goMap(View v){
		final Intent mapIntent=new Intent(this,MapAct.class);
		mapIntent.putExtra("EXIT",false);
		mapIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mapIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		sendCoords(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show(); 

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {


	}
}
