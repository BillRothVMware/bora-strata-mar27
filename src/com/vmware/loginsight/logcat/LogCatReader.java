package com.vmware.loginsight.logcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class LogCatReader {
	private AbstractSet<LogCatListener> mListeners = new CopyOnWriteArraySet<>();
	private Process mLogCatProcess;

	public void addListener(LogCatListener listener) {
		mListeners.add(listener);
	}

	public void removeListener(LogCatListener listener) {
		mListeners.remove(listener);
	}

	public void clearListeners() {
		mListeners.clear();
	}

	public void listen() {
		String[] command = new String[] {"logcat", "-v", "long"};
		try {
			mLogCatProcess = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(mLogCatProcess.getInputStream()));
			//Discard first line, it's just a header
			reader.readLine();
			for (LogEntry entry = readLogEntry(reader);; entry = readLogEntry(reader)) {
				if (entry == null) {
					try {
						mLogCatProcess.exitValue();
						return;
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
}
