package net.aonsolutions.aon.tbai.lroe;

import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;

import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROERequest {
	
	DataRequest dataRequest;
	DataResponse dataResponse;
	LROEInfo info;
	LROEResponse response;
	String requestUrl;
	String responseUrl;
	
	public LROERequest() {
	
	}

	public DataRequest getDataRequest() {
		return dataRequest;
	}

	public LROERequest setDataRequest(DataRequest dataRequest) {
		this.dataRequest = dataRequest;
		return this;
	}

	public DataResponse getDataResponse() {
		return dataResponse;
	}

	public LROERequest setDataResponse(DataResponse dataResponse) {
		this.dataResponse = dataResponse;
		return this;
	}

	public LROEInfo getInfo() {
		return info;
	}

	public LROERequest setInfo(LROEInfo info) {
		this.info = info;
		return this;
	}

	public LROEResponse getResponse() {
		return response;
	}

	public LROERequest setResponse(LROEResponse response) {
		this.response = response;
		return this;
	}

	public String getRequestUrl() {
		return requestUrl;
	}
	
	public LROERequest setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
		return this;
	}
	
	public String getResponseUrl() {
		return responseUrl;
	}
	
	public LROERequest setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
		return this;
	}
}
