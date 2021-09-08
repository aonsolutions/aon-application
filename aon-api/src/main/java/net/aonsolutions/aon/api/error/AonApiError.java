package net.aonsolutions.aon.api.error;

public enum AonApiError {

	ROUTE_ERROR("La ruta introducida es incorrecta.");
	
	String message;
	
	private AonApiError(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public AonApiError setMessage(String message) {
		this.message = message;
		return this;
	}
}
