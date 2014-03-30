package com.vmware.loginsight;
//FIXME: Insert Headers
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import com.google.analytics.tracking.android.EasyTracker;

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
import android.widget.TextView;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * @author bill roth
 * @since 3/27/2014
 *
 */
@SuppressWarnings("unused")
public class LogInsightActivity extends Activity implements OnSharedPreferenceChangeListener {

	//private PlaceholderFragment frag;
	private Intent serviceIntent;
	private String gLogTag;
	private String gIP; // My ipaddress
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_insight);
		String host;
		
		host = initializePreferences();
		
		gLogTag = getString(R.string.LogTag);
		Logd("onCreate main activity");
		
		serviceIntent = new Intent(getApplicationContext(), LogUploaderService.class);

		serviceIntent.putExtra(LogUploaderService.EXTRA_LOG_INSIGHT_HOST, host);
		startService(serviceIntent);
		
		gIP = getipAddress();
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Logd(key + " changed");
		//
		//TODO: Add settings update when you get a chance method.
		//    stop service 
		stopService(serviceIntent);
		//
		// Restarting the service reinitializes it to the new global settings.
		//
		startService(serviceIntent);
		
	}
	
	
	/**
	 * Initialize the preferences on first run, or load them into cache when already present.
	 * 
	 * @author broth
	 * @return String the IP we we thing were running from.
	 */
	private String initializePreferences() {
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
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
			Logd("Calling setting dialog");
			Intent i = new Intent(this,SettingsActivity.class);
			startActivityForResult(i,1);
			return true;
		case R.id.action_about:
			//
			//TODO: Put in dialog here 
			// see sample: http://developer.android.com/guide/topics/ui/dialogs.html
			//
			Logd("Calling about dialog");
			int x;
			x=12;
			return true;
			
			default:
				return super.onOptionsItemSelected(item);
		} // switch
	}

	/**
	 * @return IP Address String
	 * TODO: See if VPN, return;
	 */
	public String getipAddress() { 
		
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = (NetworkInterface) en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
						String ipaddress=inetAddress.getHostAddress().toString();
						Logd("ip address" + ipaddress);
						return ipaddress;
					}
				}
			}
		} catch (Exception ex) {
			Logd("Socket exception in GetIP Address of Utilities " + ex.toString());

		}
		return "0.0.0.0"; 
	}
	
	  @Override
	  public void onStart() {
	    super.onStart();
	     // The rest of your onStart() code.
	    EasyTracker.getInstance().activityStart(this); // Add this method.
	    Logd("onStart in Main Activity");
	    setText(gIP);
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    // The rest of your onStop() code.
	    EasyTracker.getInstance().activityStop(this); // Add this method.
	    Logd("onStop in Main Activity");
	  }
	  /**
	 * @param msg Log message
	 * @return voi
	 * @author bill roth
	 * @since 3/29/2014
	 */
	private void Logd(String msg) {
		  Log.d(gLogTag,msg);
	  }
	
	public void setText(String msg) {
		TextView line1 = (TextView) findViewById(R.id.textView1);
		line1.setText(msg);
	}
}
