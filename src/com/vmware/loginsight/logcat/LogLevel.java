package com.vmware.loginsight.logcat;

public enum LogLevel {

	DEBUG("debug"), ASSERT("assert"), ERROR("error"), VERBOSE("verbose"), INFO("info"), WARN("warn");

	private String level;

	private LogLevel(String level) {
		this.level = level;
	}

	public String getValue() {
		return level;
	}
	
	public static LogLevel fromLetter(String letter) {
		if (letter.equals("V")) {
			return VERBOSE;
		} else if (letter.equals("D")) {
			return DEBUG;
		} else if (letter.equals("I")) {
			return INFO;
		} else if (letter.equals("W")) {
			return WARN;
		} else if (letter.equals("E")) {
			return ERROR;
		}
		return ASSERT;
	}
}
