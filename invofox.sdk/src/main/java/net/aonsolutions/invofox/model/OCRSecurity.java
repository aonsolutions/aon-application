package net.aonsolutions.invofox.model;

import java.io.Serializable;

public class OCRSecurity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String algorithm; // sha256 | sha512
	private String secret;
	
	public String getAlgorithm() {
		return algorithm;
	}
	
	public OCRSecurity setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
		return this;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public OCRSecurity setSecret(String secret) {
		this.secret = secret;
		return this;
	}
	
}
