package net.aonsolutions.aon.verifactu;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;

public class VerifactuResponse {
	
	private boolean error;
	private String errorMessage;
	private RespuestaRegFactuSistemaFacturacionType response;
	private byte[] bytes;
	
	public boolean isError() {
		return error;
	}
	public VerifactuResponse setError(boolean error) {
		this.error = error;
		return this;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public VerifactuResponse setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}
	
	public RespuestaRegFactuSistemaFacturacionType getResponse() {
		return response;
	}
	public VerifactuResponse setResponse(RespuestaRegFactuSistemaFacturacionType response) {
		this.response = response;
		return this;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	public VerifactuResponse setBytes(byte[] bytes) {
		this.bytes = bytes;
		return this;
	}

}
