package com.vmware.loginsight.logcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractSet;
import java.util.concurrent.CopyOnWriteArraySet;

import android.content.Context;
import android.util.Log;

public class LogCatReader {
	private AbstractSet<LogCatListener> mListeners = new CopyOnWriteArraySet<>();
	private Process mLogCatProcess;
	private volatile boolean mRunning;
	private volatile boolean mStopped;

	public void addListener(LogCatListener listener) {
		mListeners.add(listener);
	}

	public void removeListener(LogCatListener listener) {
		mListeners.remove(listener);
	}

	public void clearListeners() {
		mListeners.clear();
	}
	
	public void stop() {
		mStopped = true;
	}
	
	public boolean isRunning() {
		return mRunning;
	}

	public void listen() {
		mRunning = true;
		String[] command = new String[] {"logcat", "-v", "long"};
		try {
			mLogCatProcess = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(mLogCatProcess.getInputStream()));
			//Discard first line, it's just a header
			reader.readLine();
			for (LogEntry entry = readLogEntry(reader); !mStopped; entry = readLogEntry(reader)) {
				if (entry == null) {
					try {
						mLogCatProcess.exitValue();
						break;
					} catch (IllegalThreadStateException e) {
						// Still running, we just got a bad message
						continue;
					}
				}
				for (LogCatListener listener : mListeners) {
					listener.handleLogCatMessage(entry);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mRunning = false;
		Log.i(getClass().getName(), "Stopped Listening.");
	}
	
	private static LogEntry readLogEntry(BufferedReader reader) throws IOException {
		String header = reader.readLine();
		StringBuilder message = new StringBuilder();
		// We use "long" format messages which may span multiple lines and are empty line delimited.
		for (String line = reader.readLine(); line != null && line.length() > 0; line = reader.readLine()) {
			if (message.length() > 0) {
				message.append("\n");
			}
			message.append(line);
		}
		return LogEntry.create(header, message.toString());
	}
	
	public static boolean requestPermissions(Context context) {
		if (context.getPackageManager().checkPermission(android.Manifest.permission.READ_LOGS, context.getPackageName()) == 0) {
			return true;
		}
		// Attempt to grant the READ_LOGS permission ourselves
		String[] command = new String[] {"su", "-c", String.format("pm grant %s android.permission.READ_LOGS", context.getPackageName())};
		try {
			// Successful exit means we're root
			return Runtime.getRuntime().exec(command).waitFor() == 0;
		} catch (InterruptedException | IOException e) {
			Log.e(LogCatReader.class.getName(), e.toString());
		}
		return false;
	}
}
