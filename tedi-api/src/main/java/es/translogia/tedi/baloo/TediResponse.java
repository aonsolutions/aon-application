package es.translogia.tedi.baloo;

import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class TediResponse {

	private Integer responseCode;
	private String  responseMessage;
	private String content;
	
	public TediResponse(String content) {
		this.responseCode = HttpURLConnection.HTTP_OK;
		this.content = content;
	}

	public TediResponse(String responseMessage, Integer responseCode) {
		this.responseCode = responseCode==null?HttpURLConnection.HTTP_INTERNAL_ERROR:responseCode;
		this.responseMessage = responseMessage;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public TediResponse setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
		return this;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public TediResponse setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
		return this;
	}

	public String getContent() {
		return content;
	}

	public TediResponse setContent(String content) {
		this.content = content;
		return this;
	}
	public JSONObject getJSONObject() {
		return this.content != null ? new JSONObject(this.content) : new JSONObject();	
	}
	public JSONArray getJSONArray() {
		return this.content != null ? new JSONArray(this.content) : new JSONArray();	
	}

	public boolean ok() {
		return this.responseCode == HttpURLConnection.HTTP_OK;
	}
	
}
