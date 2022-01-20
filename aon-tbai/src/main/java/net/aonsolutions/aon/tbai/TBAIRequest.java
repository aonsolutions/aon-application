package net.aonsolutions.aon.tbai;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;

import net.aonsolutions.aon.tbai.lroe.LROERequest;
import net.aonsolutions.aon.tbai.responses.TbaiResponse;

public class TBAIRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	DataRequest dataRequest;
	DataResponse dataResponse;
	TbaiResponse response;
	String requestUrl;
	String responseUrl;
	
	public DataRequest getDataRequest() {
		return dataRequest;
	}
	
	public TBAIRequest setDataRequest(DataRequest dataRequest) {
		this.dataRequest = dataRequest;
		return this;
	}
	
	public DataResponse getDataResponse() {
		return dataResponse;
	}
	
	public TBAIRequest setDataResponse(DataResponse dataResponse) {
		this.dataResponse = dataResponse;
		return this;
	}
	
	public TbaiResponse getResponse() {
		return response;
	}
	
	public TBAIRequest setResponse(TbaiResponse response) {
		this.response = response;
		return this;
	}
	
	
	public String getOperacion() {
		return "Alta";
	}
	
	public String getRequestUrl() {
		return requestUrl;
	}
	
	public TBAIRequest setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
		return this;
	}
	
	public String getResponseUrl() {
		return responseUrl;
	}
	
	public TBAIRequest setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
		return this;
	}
	
}
