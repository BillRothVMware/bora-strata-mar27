package com.vmware.loginsight.send;

public enum LogLevel {
	DEBUG("debug"), CRITICAL("critical"), ERROR("error"), ALERT("alert"), INFO("info"), WARN("warn");

	private String level;

	private LogLevel(String level) {
		this.level = level;
	}

	public String getValue() {
		return level;
	}
	
	public LogLevel fromLogCatLevel(com.vmware.loginsight.logcat.LogLevel level) {
		switch(level) {
		case VERBOSE:
			return ALERT;
		case DEBUG:
			return DEBUG;
		case INFO:
			return INFO;
		case WARN:
			return WARN;
		case ERROR:
			return ERROR;
		case ASSERT:
			return CRITICAL;
		}
		return INFO;
	}
}