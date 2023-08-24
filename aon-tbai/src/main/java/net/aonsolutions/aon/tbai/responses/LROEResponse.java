package net.aonsolutions.aon.tbai.responses;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONObject;
import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROEResponse implements Serializable {
	
	public static final String LROE_RESPONSE_ID = "eus-bizkaia-n3-identificativo";
	public static final String LROE_RESPONSE_CODE = "eus-bizkaia-n3-codigo-respuesta";
	public static final String LROE_RESPONSE_TYPE = "eus-bizkaia-n3-tipo-respuesta";
	public static final String LROE_RESPONSE_MESSAGE = "eus-bizkaia-n3-mensaje-respuesta";
	
	private Date date;
	private Integer code;
	private String responseStatus;
	private byte[] data;
	private JSONObject json;
	private DataRequest dataRequest;
	
	public LROEResponse(JSONObject json) {
		this.json = json;
		this.date = new Date();
		this.code = JsonUtils.getInteger(json, "responseCode");
		this.responseStatus = isError() ? "error" : "ok";
	}
	
	public LROEResponse(JSONObject json, byte[] data) {
		this.json = json;
		this.date = new Date();
		this.code = JsonUtils.getInteger(json, "responseCode");
		this.responseStatus = JsonUtils.getString(json, LROE_RESPONSE_TYPE);
		this.data = data;
	}
	
	public Date getDate() {
		return date;
	}
	
	public LROEResponse setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public Integer getCode() {
		return code;
	}

	public LROEResponse setCode(Integer code) {
		this.code = code;
		return this;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public LROEResponse setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
		return this;
	}

	public byte[] getData() {
		return data;
	}

	public LROEResponse setData(byte[] data) {
		this.data = data;
		return this;
	}

	public JSONObject getJson() {
		return json;
	}
	
	public LROEResponse setJson(JSONObject json) {
		this.json = json;
		return this;
	}
	
	public DataRequest getDataRequest() {
		if(dataRequest == null) {
			dataRequest = new DataRequest();
		}
		return dataRequest;
	}
	
	public LROEResponse setDataRequest(DataRequest dataRequest) {
		this.dataRequest = dataRequest;
		return this;
	}
	
	public boolean isOk() {
		return !isError();
	}
	
	public String getResponseDataStr() {
		try {
			Document d = XMLUtils.getDocument(getData());
			return XMLUtils.documentToString(d);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean isError() {
		return JsonUtils.getboolean(getJson(), "error")
			|| "Incorrecto".equalsIgnoreCase(JsonUtils.getString(getJson(), LROE_RESPONSE_TYPE));
	}
	
	public String getErrorMessage() {
		String message = JsonUtils.getString(getJson(), "errorMessage");
		if(AonStringUtils.isBlank(message)) {
			String lroeResponseCode = JsonUtils.getString(getJson(), LROE_RESPONSE_CODE);
			String lroeResponseMessage = JsonUtils.getString(getJson(), LROE_RESPONSE_MESSAGE);
			message = lroeResponseCode + " - " + lroeResponseMessage;
		}
		return message;
	}
	
	public String getErrorCode() {
		return JsonUtils.getString(getJson(), "errorCode");
	}
}
