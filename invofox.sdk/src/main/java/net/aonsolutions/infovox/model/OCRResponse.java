package net.aonsolutions.infovox.model;

import java.util.Optional;

public class OCRResponse {
	
	private Integer httpCode;
	private OCRDocument result;
	private OCRError error;
	
	public Optional<Integer> getHttpCode() {
		return Optional.ofNullable(httpCode);
	}
	public OCRResponse setHttpCode(Integer httpCode) {
		this.httpCode = httpCode;
		return this;
	}
	
	public Optional<OCRDocument> getResult() {
		return Optional.ofNullable(result);
	}
	public OCRResponse setResult(OCRDocument result) {
		this.result = result;
		return this;
	}
	
	public Optional<OCRError> getError() {
		return Optional.ofNullable(error);
	}
	public OCRResponse setError(OCRError error) {
		this.error = error;
		return this;
	}

}
