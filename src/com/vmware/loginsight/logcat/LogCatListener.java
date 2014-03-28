package com.vmware.loginsight.logcat;

public interface LogCatListener {
	public void handleLogCatMessage(LogEntry entry);
}
