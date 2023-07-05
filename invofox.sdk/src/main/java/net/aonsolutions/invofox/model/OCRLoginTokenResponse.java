package net.aonsolutions.invofox.model;

import java.util.Optional;

public class OCRLoginTokenResponse extends OCRResponse {

	private static final long serialVersionUID = 6447497857395944437L;
	
	private OCRLoginToken loginToken;

	@Override
	public OCRLoginTokenResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCRLoginTokenResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	public Optional<OCRLoginToken> getLoginToken() {
		return Optional.ofNullable(loginToken);
	}

	public OCRLoginTokenResponse setLoginToken(OCRLoginToken loginToken) {
		this.loginToken = loginToken;
		return this;
	}

}
