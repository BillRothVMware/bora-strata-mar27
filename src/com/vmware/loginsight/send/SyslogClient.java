package com.vmware.loginsight.send;

import java.util.HashMap;
import java.util.Map;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;
import org.productivity.java.syslog4j.impl.net.tcp.ssl.SSLTCPNetSyslogConfig;

public class SyslogClient {

	private SyslogIF syslog = null;

	public SyslogClient(String url, int port, LogInsightProtocol proto) throws Exception { 

		checkParam(url, "url");
		checkParam(proto, "protocol");

		if (proto.equals(LogInsightProtocol.SYSLOG_TCP)) {
			syslog = Syslog.getInstance("tcp");
		} else if (proto.equals(LogInsightProtocol.SYSLOG_UDP)) {
			syslog = Syslog.getInstance("udp");
		} else if (proto.equals(LogInsightProtocol.SYSLOG_TLS)) {
			SSLTCPNetSyslogConfig syslogConfig = new SSLTCPNetSyslogConfig(url, port);
			syslog = Syslog.createInstance("sslTcp", syslogConfig);
		} else {
			throw new Exception("Protocol " + proto.toString() + " is not supported. Use one of " +
					LogInsightProtocol.SYSLOG_TCP + ", " + LogInsightProtocol.SYSLOG_UDP + 
		                        " or " + LogInsightProtocol.SYSLOG_TLS);
		}

		syslog.getConfig().setUseStructuredData(true);
		syslog.getConfig().setHost(url);
		syslog.getConfig().setPort(port);
	}

	private void checkParam(Object param, String name) throws Exception {
		if (param == null) {
			throw new Exception("The " + name + " parameter is invalid, provide value = " + "null");
		}
	}

	public void send(String msg) throws Exception {
		send(msg, LogLevel.DEBUG, new HashMap<String, String>());
	}

	public void send(String msg, LogLevel l, Map<String, String> fields) throws Exception {
		checkParam(msg, "msg");

		Map<String, String> myFields;
		if (fields == null) {
			myFields = new HashMap<String, String>();
		} else {
			myFields = fields;
		}
		Map<String, Map<String, String>> outMap = new HashMap<String, Map<String, String>>();
		outMap.put("Fields", myFields);
		
		StructuredSyslogMessage message = new StructuredSyslogMessage("", outMap, msg);
		if (l.equals(LogLevel.ALERT)) {
			syslog.alert(message);
		} else if (l.equals(LogLevel.CRITICAL)) {
			syslog.critical(message);
		} else if (l.equals(LogLevel.DEBUG)) {
			syslog.debug(message);
		} else if (l.equals(LogLevel.ERROR)) {
			syslog.error(message);
		} else if (l.equals(LogLevel.INFO)) {
			syslog.info(message);
		} else if (l.equals(LogLevel.WARN)) {
			syslog.warn(message);
		}
	}
}
