package com.vmware.loginsight.send;

import java.util.List;

public class LogUploadClient {
	public final String url;
	public final int port;
	public final LogInsigntProtocol proto;

	public LogUploadClient(String url, int port, LogInsigntProtocol proto) {
		this.url = url;
		this.port = port;
		this.proto = proto;
		
		assert (!proto.equals(LogInsigntProtocol.UNKNOWN));
	}
	
	private LogUploadClientReturnStatus sendMessageBySyslog(String msg, List<Field> fields, List<String> filters) {
		return null;
	}
	
	private LogUploadClientReturnStatus sendMessageByHttp(String msg, List<Field> fields, List<String> filters) {
		return null;
	}

	public LogUploadClientReturnStatus sendMessage(String msg, List<Field> fields, List<String> filters) {
		assert (msg != null);
		assert (fields != null);
		
		if (proto.isSyslog()) {
			return sendMessageBySyslog(msg, fields, filters);
		} else {
			return sendMessageByHttp(msg, fields, filters);
		}	
	}

	public enum LogInsigntProtocol {
		SYSLOG_TCP(1), SYSLOG_UDP(2), SYSLOG_TLS(3), HTTP(4),

		UNKNOWN(0);

		private int value;

		private LogInsigntProtocol(int value) {
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
	
	public enum LogUploadClientReturnStatus {
		FAIL(-1),
		SUCCESS(1);
		
		int value;
		private LogUploadClientReturnStatus(int value) {
			this.value = value;
		}
		
	}

	public static class Field {
		public String key;
		public String value;

		public Field(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}
}
