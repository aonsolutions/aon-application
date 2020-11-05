package es.translogia.tedi.baloo.toremove;

import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class OLD_TediResponse {

	private Integer responseCode;
	private String  responseMessage;
	private String content;
	
	public OLD_TediResponse(String content) {
		this.responseCode = HttpURLConnection.HTTP_OK;
		this.content = content;
	}

	public OLD_TediResponse(String responseMessage, Integer responseCode) {
		this.responseCode = responseCode==null?HttpURLConnection.HTTP_INTERNAL_ERROR:responseCode;
		this.responseMessage = responseMessage;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public OLD_TediResponse setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
		return this;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public OLD_TediResponse setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
		return this;
	}

	public String getContent() {
		return content;
	}

	public OLD_TediResponse setContent(String content) {
		this.content = content;
		return this;
	}
	public JSONObject getJSONObject() {
		return !isEmpty() ? new JSONObject(this.content) : new JSONObject();	
	}
	public JSONArray getJSONArray() {
		return !isEmpty() ? new JSONArray(this.content) : new JSONArray();	
	}
	
	private boolean isEmpty() {
		return this.content == null || this.content == "";
	}

	public boolean ok() {
		return this.responseCode == HttpURLConnection.HTTP_OK;
	}
	
}
