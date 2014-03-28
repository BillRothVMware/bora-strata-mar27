package com.vmware.loginsight.send;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogIF;

//import org.productivity.java.syslog4j.impl.message.structured.StructuredSyslogMessage;

public class LogUploadClient {
	public final String url;
	public final int port;
	public final LogInsightProtocol proto;
	private SyslogIF syslog = null;

	public static UUID uuid = UUID.randomUUID();

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

	private LogUploadClientReturnStatus sendMessageBySyslog(String msg,
			List<Field> fields, List<String> filters) {
		// StructuredSyslogMessage ssm = new StructuredSyslogMessage("", null,
		// "");

		try {
			syslog.info(msg);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return LogUploadClientReturnStatus.FAIL;
		}
		return LogUploadClientReturnStatus.SUCCESS;
	}

	private LogUploadClientReturnStatus sendMessageByHttp(String msg,
			List<Field> fields, List<String> filters) {
		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpPost request = new HttpPost(url + ":" + port
					+ "/api/v1/messages/ingest/" + uuid);
			JSONObject textObj = new JSONObject();
			textObj.put("text", msg);
			textObj.put("timestamp", Calendar.getInstance().getTimeInMillis());

			JSONArray jaFields = new JSONArray();
			for (Field field : fields) {
				jaFields.put(field.getJsonObject());
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

			return response.getStatusLine().getStatusCode() == 200 ? LogUploadClientReturnStatus.SUCCESS
					: LogUploadClientReturnStatus.FAIL;

		} catch (Exception e) {
			e.printStackTrace();
			return LogUploadClientReturnStatus.FAIL;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public LogUploadClientReturnStatus sendMessage(String msg,
			List<Field> fields, List<String> filters) {
		assert (msg != null);
		assert (fields != null);

		if (proto.isSyslog()) {
			return sendMessageBySyslog(msg, fields, filters);
		} else {
			return sendMessageByHttp(msg, fields, filters);
		}
	}

	public static class Field {
		public String name;
		public String content;
		public Integer startPosition;
		public Integer len;

		public Field(String name, String content, String startPosition,
				String len) {
			this.name = name;
			this.content = content;
			try {
				this.startPosition = Integer.parseInt(startPosition);
				this.len = Integer.parseInt(len);
			} catch (Exception e) {
				this.startPosition = null;
				this.len = null;
			}
		}

		public JSONObject getJsonObject() {
			JSONObject json = new JSONObject();
			try {
				json.put("name", name);
				json.put("content", content);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			return json;
		}
	}

	public void sendMessage(String string) {
		sendMessage(string, new ArrayList<Field>(), null);

	}
}
