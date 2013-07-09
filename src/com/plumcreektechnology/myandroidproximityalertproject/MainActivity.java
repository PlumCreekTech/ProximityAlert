package com.plumcreektechnology.myandroidproximityalertproject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ProxAlertActivity palert = new ProxAlertActivity();
		fragAdder(palert);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.android_proximity_alert_project, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_settings:
			Settings pfrag = new Settings();
			fragReplacer(pfrag);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void fragAdder(Fragment frag){
		getFragmentManager().beginTransaction().add(R.id.parent, frag).commit();
	}
	
	public void fragReplacer(Fragment frag){
		getFragmentManager().beginTransaction().replace(R.id.parent, frag).addToBackStack(null).commit();
	}
	
}
