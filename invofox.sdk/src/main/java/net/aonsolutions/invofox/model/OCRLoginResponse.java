package net.aonsolutions.invofox.model;

import java.util.Optional;

public class OCRLoginResponse extends OCRResponse {

	private static final long serialVersionUID = 6447497857395944437L;
	
	private OCRLogin login;

	@Override
	public OCRLoginResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCRLoginResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	public Optional<OCRLogin> getLogin() {
		return Optional.ofNullable(login);
	}

	public OCRLoginResponse setLogin(OCRLogin login) {
		this.login = login;
		return this;
	}

}
