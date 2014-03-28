package com.vmware.loginsight;

import java.util.HashMap;
import java.util.Map;

import com.vmware.loginsight.send.SyslogClient;
import com.vmware.loginsight.logcat.LogCatListener;
import com.vmware.loginsight.logcat.LogCatReader;
import com.vmware.loginsight.logcat.LogEntry;
import com.vmware.loginsight.send.LogInsightProtocol;
import com.vmware.loginsight.send.LogLevel;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class LogUploaderService extends IntentService {

	public static final String EXTRA_LOG_INSIGHT_HOST = "Host";
	public static final String EXTRA_LOG_INSIGHT_PORT = "Port";

	public LogUploaderService() {
		this(LogUploaderService.class.getName());
	}

	public LogUploaderService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		 
		if (!LogCatReader.requestPermissions(getApplicationContext())) {
			Log.w(getClass().getName(),
					"Failed to acquire READ_LOGS permission, reading app-local logs only.");
		}
		final LogCatReader reader = new LogCatReader();
		try {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			String host,sPort;
			int port;
			sPort = preferences.getString("PORT", "514");
			try {
				port = Integer.parseInt(sPort);
			} catch (Exception e1) {
				port = 514;
			}
			

			final SyslogClient client = new SyslogClient(
					
					intent.getStringExtra(EXTRA_LOG_INSIGHT_HOST),
					intent.getIntExtra(EXTRA_LOG_INSIGHT_PORT, port),
					LogInsightProtocol.SYSLOG_UDP);
			reader.addListener(new LogCatListener() {

				@Override
				public void handleLogCatMessage(LogEntry entry) {
					Map<String, String> fields = new HashMap<>();
					fields.put("tag", entry.getTag());
					fields.put("log_time", entry.getTimestamp());
					fields.put("pid", entry.getPid());
					fields.put("tid", entry.getTid());
					try {
						client.send(entry.getMessage(),
								LogLevel.fromLogCatLevel(entry.getLogLevel()),
								fields);
					} catch (Exception e) {
						// Don't throw if this fails as we might create a log
						// cycle
					}
				}
			});
			reader.listen();
		} catch (Exception e) {
			Log.e(getClass().getName(),
					String.format("Unable to start logging agent: %s", e));
		}
	}
}
