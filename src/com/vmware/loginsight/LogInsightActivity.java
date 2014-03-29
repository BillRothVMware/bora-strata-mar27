package com.vmware.loginsight;
//FIXME: Insert Headers
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

	private PlaceholderFragment frag;
	private Intent serviceIntent;
	private String gLogTag;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_insight);
		String host;
		
		host = initializePreferences();
		
		gLogTag = getString(R.string.LogTag);
		Logd("onCreate main activity");
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, frag = new PlaceholderFragment()).commit();
		}
		serviceIntent = new Intent(getApplicationContext(), LogUploaderService.class);
		// TODO: set based on prefs
		//
		serviceIntent.putExtra(LogUploaderService.EXTRA_LOG_INSIGHT_HOST, "10.148.104.186");
		startService(serviceIntent);
		
		String ip = getipAddress();
		set_text_line1(ip);
	}
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Logd(key + " changed");
		//
		//TODO: Add settings update when you get a chance method.
		//    stop service 
		stopService(serviceIntent);
		//
		// do something
		//
		startService(serviceIntent);
		
	}
	
	
	/**
	 * Initialize the preferences on first run, or load them into cache when already present.
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
	 * A placeholder fragment containing a simple view.
	 * FIXME: Delete
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}
        private View rootView;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_log_insight,
					container, false);
			return rootView;
		}
		View getRootView() {return rootView;}
	}
	public void set_text_line1(String msg){
		Logd("IP appears to be " + msg);
		//TODO: Fix need to grab text view and aset it with ip address
		//
//		TextView line1 = (TextView) frag.getRootView().findViewById(R.id.textline1);
//		if(line1 == null) return;
//		line1.setText(msg);
		
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
					if (!inetAddress.isLoopbackAddress()) {
						String ipaddress=inetAddress.getHostAddress().toString();
						if(ipaddress.contains("::"))
							continue;
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
}
