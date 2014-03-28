package com.vmware.loginsight;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.preference.PreferenceManager;

@SuppressWarnings("unused")
public class LogInsightActivity extends Activity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("StrataDroid","onCreate main activity");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_insight);
		String host;
		
		host = initializePreferences();
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		Intent intent = new Intent(getApplicationContext(), LogUploaderService.class);
		// TODO: set based on prefs
		//
		intent.putExtra(LogUploaderService.EXTRA_LOG_INSIGHT_HOST, "10.148.104.186");
		startService(intent);
		
	}
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d("StrataDroid",key + " changed");
		//
		//TODO: Add settings update when you get a chance method.
		//    stop service 
		
	}
	
	
	private String initializePreferences() {
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		// TODO: Pull this from the prefs file
		//
		String ip = prefs.getString("SERVER_NAME", "10.148.104.186");
		String proto = prefs.getString("PROTOCOL","udp");
		
		int x;
		x=12;
		// now set up the listener
		prefs.registerOnSharedPreferenceChangeListener(this);
		return ip;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_insight, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
		
		case R.id.action_settings:
			Log.d("tag","Calling setting dialog");
			Intent i = new Intent(this,SettingsActivity.class);
			startActivityForResult(i,1);
			return true;
		case R.id.action_about:
			//
			//TODO: Put in dialog here 
			// see sample: http://developer.android.com/guide/topics/ui/dialogs.html
			//
			Log.d("tag","Calling about dialog");
			int x;
			x=12;
			return true;
			
			default:
				return super.onOptionsItemSelected(item);
		} // switch
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_log_insight,
					container, false);
			return rootView;
		}
	}

}
