package com.vmware.loginsight.logcat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {

	/*
	 * From AOSP
	 * https://androidlogcatviewer.googlecode.com/svn/trunk/LogcatOfflineView/src/com/logcat/offline/view/ddmuilib/logcat/LogCatMessageParser.java
	 * 
	 * Copyright (C) 2011 The Android Open Source Project
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *      http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 */
    /**
     * This pattern is meant to parse the first line of a log message with the option
     * 'logcat -v long'. The first line represents the date, tag, severity, etc.. while the
     * following lines are the message (can be several lines).<br>
     * This first line looks something like:<br>
     * {@code "[ 00-00 00:00:00.000 <pid>:0x<???> <severity>/<tag>]"}
     * <br>
     * Note: severity is one of V, D, I, W, E, A? or F. However, there doesn't seem to be
     *       a way to actually generate an A (assert) message. Log.wtf is supposed to generate
     *       a message with severity A, however it generates the undocumented F level. In
     *       such a case, the parser will change the level from F to A.<br>
     * Note: the fraction of second value can have any number of digit.<br>
     * Note: the tag should be trimmed as it may have spaces at the end.
     */
	private static final Pattern sLogHeaderPattern = Pattern.compile(
            "^\\[\\s(\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d\\.\\d+)"
          + "\\s+(\\d*):\\s*(\\S+)\\s([VDIWEAF])/(.*)\\]$");
	/*
	 * End AOSP Code
	 */

	private String mTimestamp;
	private String mPid; 
	private String mTid; 
	private LogLevel mLogLevel;
	private String mTag;
	private String mMessage;
	
	private LogEntry() {
		// Hide constructor
	}
	
	public String getTimestamp() {
		return mTimestamp;
	}
	
	public String getPid() {
		return mPid;
	}
	
	public String getTid() {
		return mTid;
	}
	
	public LogLevel getLogLevel() {
		return mLogLevel;
	}
	
	public String getTag() {
		return mTag;
	}
	
	public String getMessage() {
		return mMessage;
	}
	
	public String toString() {
		return String.format("LogEntry[%s %s %s %s %s]%s", mTimestamp, mLogLevel, mTag, mPid, mTid, mMessage);
	}
	
	public static LogEntry create(String header, String message) {
		Matcher matcher = sLogHeaderPattern.matcher(header);
		if (!matcher.matches()) {
			return null;
		}
		LogEntry entry = new LogEntry();
		entry.mTimestamp = matcher.group(1);
		entry.mPid = matcher.group(2);
		entry.mTid = matcher.group(3);
		entry.mLogLevel = LogLevel.fromLetter(matcher.group(4));
		entry.mTag = matcher.group(5).trim();
		entry.mMessage = message;
		return entry;
	}
}
