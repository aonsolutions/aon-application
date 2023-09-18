package net.aonsolutions.aon.tbai.exceptions;

public enum TBAIError {
	
	AON_001("AON_001", "El documento es un dato Obligatorio."),
	AON_002("AON_002", "La series es un dato Obligatorio.");

	String code;
	String message;
	
	private TBAIError(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
	

}
