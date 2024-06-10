package net.aonsolutions.invofox.model;

import java.util.Optional;

public class OCRApiKeyResponse extends OCRResponse {

	private static final long serialVersionUID = 1L;
	
	private OCRApiKey apikey;

	@Override
	public OCRApiKeyResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCRApiKeyResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	@Override
	public OCRApiKeyResponse setSkip(Integer skip) {
		super.setSkip(skip);
		return this;
	}

	@Override
	public OCRApiKeyResponse setLimit(Integer limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public OCRApiKeyResponse setCount(Integer count) {
		super.setCount(count);
		return this;
	}

	public Optional<OCRApiKey> getApikey() {
		return Optional.ofNullable(apikey);
	}

	public OCRApiKeyResponse setApiKey(OCRApiKey apikey) {
		this.apikey = apikey;
		return this;
	}
}
