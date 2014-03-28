package com.vmware.loginsight.send;

import java.util.ArrayList;
import java.util.List;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
//import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;

public class LogUploadClient {
	public final String url;
	public final int port;
	public final LogInsightProtocol proto;
	private SyslogIF syslog = null;

	public LogUploadClient(String url, int port, LogInsightProtocol proto) {
		this.url = url;
		this.port = port;
		this.proto = proto;
		
		assert (!proto.equals(LogInsightProtocol.UNKNOWN));
		
		if (proto.equals(LogInsightProtocol.SYSLOG_TCP)) {
			syslog = Syslog.getInstance("tcp");
		} else if (proto.equals(LogInsightProtocol.SYSLOG_UDP)) {
			syslog = Syslog.getInstance("udp");
		} else {
			System.err.println(proto + " not supported. Exiting...");
			System.exit(1);
		}
		syslog.getConfig().setHost(url);
		syslog.getConfig().setPort(port);
	}
	
	private LogUploadClientReturnStatus sendMessageBySyslog(String msg, List<Field> fields, List<String> filters) {
		//StructuredSyslogMessage ssm = new StructuredSyslogMessage("", null, "");
		
		try {
			syslog.info(msg);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return LogUploadClientReturnStatus.FAIL;
		}
		return LogUploadClientReturnStatus.SUCCESS;
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

	public static class Field {
		public String key;
		public String value;

		public Field(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}

	public void sendMessage(String string) {
		sendMessage(string, new ArrayList<Field>(), null);
		
	}
}
