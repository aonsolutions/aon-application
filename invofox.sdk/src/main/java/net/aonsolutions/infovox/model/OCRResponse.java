package net.aonsolutions.infovox.model;

import java.util.List;
import java.util.Optional;

public class OCRResponse {
	
	private Integer httpCode;
	private List<OCRDocument> result;
	private OCRError error;
	private Integer skip; 
	private Integer limit;
	private Integer count;
	
	public Optional<Integer> getHttpCode() {
		return Optional.ofNullable(httpCode);
	}
	public OCRResponse setHttpCode(Integer httpCode) {
		this.httpCode = httpCode;
		return this;
	}
	
	public Optional<List<OCRDocument>> getResult() {
		return Optional.ofNullable(result);
	}
	public OCRResponse setResult(List<OCRDocument> result) {
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
	
	public Optional<Integer> getSkip() {
		return Optional.ofNullable(skip);
	}
	public OCRResponse setSkip(Integer skip) {
		this.skip = skip;
		return this;
	}
	
	public Optional<Integer> getLimit() {
		return Optional.ofNullable(limit);
	}
	public OCRResponse setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}
	
	public Optional<Integer> getCount() {
		return Optional.ofNullable(count);
	}
	public OCRResponse setCount(Integer count) {
		this.count = count;
		return this;
	}

	
}
