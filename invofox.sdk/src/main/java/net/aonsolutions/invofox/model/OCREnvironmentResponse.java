package net.aonsolutions.invofox.model;

import java.util.Optional;

public class OCREnvironmentResponse extends OCRResponse {

	private static final long serialVersionUID = 6447497857395944437L;
	
	private OCREnvironment environment;

	@Override
	public OCREnvironmentResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCREnvironmentResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	public Optional<OCREnvironment> getEnvironment() {
		return Optional.ofNullable(environment);
	}

	public OCREnvironmentResponse setEnvironment(OCREnvironment environment) {
		this.environment = environment;
		return this;
	}

}
