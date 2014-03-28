package com.vmware.loginsight;

import java.util.HashMap;
import java.util.Map;

import com.vmware.loginsight.send.SyslogClient;
import com.vmware.loginsight.logcat.LogCatListener;
import com.vmware.loginsight.logcat.LogCatReader;
import com.vmware.loginsight.logcat.LogEntry;
import com.vmware.loginsight.send.LogInsightProtocol;
import com.vmware.loginsight.send.LogLevel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.Service;
import android.os.IBinder;
import android.util.Log;

public class LogUploaderService extends Service {

	public static final String EXTRA_LOG_INSIGHT_HOST = "Host";
	public static final String EXTRA_LOG_INSIGHT_PORT = "Port";

	private LogCatReader mLogCatReader;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (mLogCatReader != null) {
			mLogCatReader.stop();
			mLogCatReader = null;
		}
		if (!LogCatReader.requestPermissions(getApplicationContext())) {
			Log.w(getClass().getName(),
					"Failed to acquire READ_LOGS permission, reading app-local logs only.");
		}
		String host = intent.getStringExtra(EXTRA_LOG_INSIGHT_HOST);
		if (host == null) {
			stopSelf();
			return Service.START_NOT_STICKY;
		}
		final LogCatReader reader = new LogCatReader();
		mLogCatReader = reader;
		try {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			int port;
			try {
				port = Integer.parseInt(preferences.getString("PORT", "514"));
			} catch (Exception e1) {
				port = 514;
			}

			final SyslogClient client = new SyslogClient(host,
					intent.getIntExtra(EXTRA_LOG_INSIGHT_PORT, port),
					LogInsightProtocol.SYSLOG_UDP);
			reader.addListener(new LogCatListener() {

				@Override
				public void handleLogCatMessage(LogEntry entry) {
					Map<String, String> fields = new HashMap<>();
					fields.put("log_time", entry.getTimestamp());
					fields.put("tid", entry.getTid());
					try {
						client.send(entry.getMessage(),
								LogLevel.fromLogCatLevel(entry.getLogLevel()),
								fields, entry.getTag(), entry.getPid());
					} catch (Exception e) {
						// Don't throw if this fails as we might create a log
						// cycle
					}
				}
			});
			runReader(reader);
		} catch (Exception e) {
			Log.e(getClass().getName(),
					String.format("Unable to start logging agent: %s", e));
		}
		return Service.START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		if (mLogCatReader != null) {
			mLogCatReader.stop();
		}
		super.onDestroy();
	}

	public void runReader(final LogCatReader reader) throws Exception {
		new Thread() {
			@Override
			public void run() {
				reader.listen();
			}
		}.start();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// We don't support binding
		return null;
	}
}
