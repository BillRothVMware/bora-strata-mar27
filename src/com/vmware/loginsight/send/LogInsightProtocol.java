package com.vmware.loginsight.send;

public enum LogInsightProtocol {
	SYSLOG_TCP(1), SYSLOG_UDP(2), SYSLOG_TLS(3), HTTP(4),

	UNKNOWN(0);

	private int value;

	private LogInsightProtocol(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public boolean isSyslog() {
		return (this.equals(SYSLOG_TCP) || this.equals(SYSLOG_UDP) || this
				.equals(SYSLOG_TLS));
	}
}