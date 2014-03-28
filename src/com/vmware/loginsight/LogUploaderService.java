package com.vmware.loginsight;

import com.vmware.loginsight.logcat.LogCatListener;
import com.vmware.loginsight.logcat.LogCatReader;
import com.vmware.loginsight.logcat.LogEntry;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class LogUploaderService extends IntentService {
	
	public LogUploaderService() {
		this(LogUploaderService.class.getName());
	}

	public LogUploaderService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		if (!LogCatReader.requestPermissions(getApplicationContext())) {
			Log.w(getClass().getName(), "Failed to acquire READ_LOGS permission, reading app-local logs only.");
		}
		final LogCatReader reader = new LogCatReader();
		reader.addListener(new LogCatListener() {
			
			@Override
			public void handleLogCatMessage(LogEntry entry) {
			}
		});
		reader.listen();
	}

}
