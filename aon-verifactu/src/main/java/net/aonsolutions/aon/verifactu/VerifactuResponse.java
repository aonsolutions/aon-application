package net.aonsolutions.aon.verifactu;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestaconsultalr.RespuestaConsultaFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.EstadoEnvioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.EstadoRegistroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;

public class VerifactuResponse {
	
	private RespuestaRegFactuSistemaFacturacionType response;
	private RespuestaConsultaFactuSistemaFacturacionType queryResponse;
	private byte[] bytes;
	
	public RespuestaRegFactuSistemaFacturacionType getResponse() {
		return response;
	}
	public VerifactuResponse setResponse(RespuestaRegFactuSistemaFacturacionType response) {
		this.response = response;
		return this;
	}
	
	public RespuestaConsultaFactuSistemaFacturacionType getQueryResponse() {
		return queryResponse;
	}
	public VerifactuResponse setQueryResponse(RespuestaConsultaFactuSistemaFacturacionType queryResponse) {
		this.queryResponse = queryResponse;
		return this;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	public VerifactuResponse setBytes(byte[] bytes) {
		this.bytes = bytes;
		return this;
	}
	
	public boolean isIncorrecto(){
		return response == null || response.getEstadoEnvio() == EstadoEnvioType.INCORRECTO; 
	}
	
	public boolean isIncorrecta( Integer invoiceId ){
		if (isIncorrecto()) return true;
		return AonCollectionUtils.stream(response.getRespuestaLinea())
			.filter(rl -> AonStringUtils.equals(AonNumberUtils.toString(invoiceId),rl.getRefExterna()) )
			.anyMatch(rl -> rl.getEstadoRegistro() == EstadoRegistroType.INCORRECTO)
		;
	}
	public boolean isCorrecta( Integer invoiceId ){
		return !isIncorrecta(invoiceId);
	}
}
