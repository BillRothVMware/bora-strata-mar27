package com.vmware.loginsight;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

@SuppressWarnings("unused")
public class LogInsightActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_insight);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		Intent intent = new Intent(getApplicationContext(), LogUploaderService.class);
		intent.putExtra(LogUploaderService.EXTRA_LOG_INSIGHT_HOST, "10.148.104.186");
		startService(intent);
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
			// Put in dialog here 
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
