package com.vmware.loginsight;

import com.vmware.loginsight.logcat.LogCatListener;
import com.vmware.loginsight.logcat.LogCatReader;
import com.vmware.loginsight.logcat.LogEntry;
import com.vmware.loginsight.send.LogInsightProtocol;
import com.vmware.loginsight.send.LogUploadClient;

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
		final LogUploadClient uploadClient = new LogUploadClient("10.148.104.186", 514, LogInsightProtocol.SYSLOG_UDP); 
		reader.addListener(new LogCatListener() {
			
			@Override
			public void handleLogCatMessage(LogEntry entry) {
				uploadClient.sendMessage(entry.toString());
			}
		});
		reader.listen();
	}

}
