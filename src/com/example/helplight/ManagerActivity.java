package com.example.helplight;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.parse.Parse;
import com.parse.ParseAnalytics;
public class ManagerActivity extends Activity implements OnItemSelectedListener {
	Spinner spinner;
	String[] numbers;
	String prefNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.managenumbers);
		String[] contacts=getResources().getStringArray(R.array.contacts);
		 numbers=getResources().getStringArray(R.array.numbers);
		spinner=(Spinner) findViewById(R.id.spinner);
		spinner();
	}
	
	public void spinner(){
	ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.contacts, android.R.layout.simple_spinner_item);
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinner.setAdapter(adapter);
	spinner.setOnItemSelectedListener(this);
	}
	
public void returnBack(View v){
	final Intent mainIntent=new Intent(this,ButtonActivity.class);
	mainIntent.putExtra("PREFNUM", prefNumber);
	mainIntent.putExtra("EXIT",false);
	mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	startActivity(mainIntent);
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		int position=spinner.getSelectedItemPosition();
		prefNumber=numbers[position];
	}
//
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
}