package com.esferalia.aon.occam.api.model.finance.nordigen;

import com.esferalia.aon.watson.error.AonCoreException;

public class NordigenException extends AonCoreException {
	
	private static final long serialVersionUID = -6384010753448554063L;

	private NordigenResponse response;

	public NordigenException() {
		super();
	}
	
	public NordigenException(String message) {
		super(message);
	}

	public NordigenException(NordigenResponse response) {
		super(response == null ? null : response.getDetail());
		this.response = response;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public NordigenResponse getResponse() {
		return response;
	}
}
