package com.vmware.loginsight.send;

import java.util.HashMap;
import java.util.Map;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.message.processor.structured.StructuredSyslogMessageProcessor;
import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;
import org.productivity.java.syslog4j.impl.net.tcp.ssl.SSLTCPNetSyslogConfig;

public class SyslogClient {

	private SyslogIF syslog = null;
	private final String prefix;

	/**
	 * Init class for server at url:port and the given protocol. The "prefix" String will be added to every message
	 */
	public SyslogClient(String url, int port, LogInsightProtocol proto, String prefix) throws Exception {
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

		this.prefix = prefix;
	}

	/**
	 * Init class for server at url:port and the given protocol
	 */
	public SyslogClient(String url, int port, LogInsightProtocol proto) throws Exception { 
		this(url, port, proto, null);
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
		send(msg, l, fields, null, null);
	}

	/**
	 * Sends a syslog message to the syslog server that was provided when the class was instantiated
	 * 
	 * @param msg The syslog message text
	 * @param l Log level
	 * @param fields a Map<String, String> of key-value pairs that are appended to the message for easy parsing
	 * @param appName The name of the app emmitting the logs, if value is null the app is not set on the header of the message
	 * @param processId The pid of the process emmitting the logs, if value is null the pid is not set on the header of the message
	 * @throws Exception if something is wrong, likely - server unreachable
	 */
	public void send(String msg, LogLevel l, Map<String, String> fields, String appName, String processId) throws Exception {
		if (msg == null) {
			msg = "";
		}
		if (prefix != null) {
			msg = prefix + " " + msg;
		}

		Map<String, String> myFields;
		if (fields == null) {
			myFields = new HashMap<String, String>();
		} else {
			myFields = fields;
		}
		Map<String, Map<String, String>> outMap = new HashMap<String, Map<String, String>>();
		outMap.put("Fields", myFields);
		
		StructuredSyslogMessage message = new StructuredSyslogMessage("", outMap, msg);
		StructuredSyslogMessageProcessor processor = new StructuredSyslogMessageProcessor();
		syslog.setMessageProcessor(processor);

		if (appName != null) {
			processor.setApplicationName(appName);			
		}

		if (processId != null) {
			processor.setProcessId(processId);
		}
		
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
		syslog.flush();
	}
}
