package com.vmware.loginsight.send;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.message.processor.structured.StructuredSyslogMessageProcessor;
import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;
import org.productivity.java.syslog4j.impl.net.tcp.ssl.SSLTCPNetSyslogConfig;

public class APIClient {

	private final String url;
	private final int port = 9000;
	private final String prefix;
	private HttpClient httpClient = new DefaultHttpClient();

	/**
	 * Init class for server at url:port and the given protocol. The "prefix"
	 * String will be added to every message
	 */
	public APIClient(String url, int port, LogInsightProtocol proto,
			String prefix) throws Exception {
		assert (proto == LogInsightProtocol.HTTP);
		assert (url != null);

		this.url = url;
		this.prefix = prefix;
	}

	/**
	 * Init class for server at url:port and the given protocol
	 */
	public APIClient(String url, int port, LogInsightProtocol proto)
			throws Exception {
		this(url, port, proto, null);
	}

	public void send(String msg) throws Exception {
		send(msg, LogLevel.DEBUG, new HashMap<String, String>());
	}

	public void send(String msg, LogLevel l, Map<String, String> fields)
			throws Exception {
		send(msg, l, fields, null, null);
	}

	/**
	 * Sends a syslog message to the syslog server that was provided when the
	 * class was instantiated
	 * 
	 * @param msg
	 *            The syslog message text
	 * @param l
	 *            Log level
	 * @param fields
	 *            a Map<String, String> of key-value pairs that are appended to
	 *            the message for easy parsing
	 * @param appName
	 *            The name of the app emmitting the logs, if value is null the
	 *            app is not set on the header of the message
	 * @param processId
	 *            The pid of the process emmitting the logs, if value is null
	 *            the pid is not set on the header of the message
	 * @throws Exception
	 *             if something is wrong, likely - server unreachable
	 */
	public void send(String msg, LogLevel l, Map<String, String> fields,
			String appName, String processId) throws Exception {
		try {
			HttpPost request = new HttpPost(
					url
							+ ":"
							+ port
							+ "/api/v1/messages/ingest/3F2504E0-4F89-11D3-9A0C-0305E82C3301");
			JSONObject textObj = new JSONObject();
			
			textObj.put("text", getSyslogMessage(msg));

			textObj.put("timestamp", Calendar.getInstance().getTimeInMillis());

			JSONArray jaFields = new JSONArray();
			for (Map.Entry<String, String> entry : fields.entrySet()) {
				JSONObject obj = new JSONObject();
				obj.put(entry.getKey(), entry.getValue());
				jaFields.put(obj);
			}

			textObj.put("fields", jaFields);

			JSONArray ja = new JSONArray();
			ja.put(textObj);

			JSONObject messageObj = new JSONObject();
			messageObj.put("messages", ja);

			StringEntity params = new StringEntity(messageObj.toString());
			System.out.println(messageObj.toString());
			request.addHeader("Content-Type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);

			System.out.println(response.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	private String getSyslogMessage(String msg) {
		String message = msg == null? "": msg;
		
		if (prefix != null) {
			message = prefix + " " + msg;
		}
		
		return message;
	}
}
