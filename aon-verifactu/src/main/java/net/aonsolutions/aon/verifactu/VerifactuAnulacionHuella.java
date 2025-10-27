package net.aonsolutions.aon.verifactu;

import com.esferalia.aon.watson.server.codec.AonDigestUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class VerifactuAnulacionHuella {
	 
	private static final String ID_EMISOR_FACTURA_ANULADA = "IDEmisorFacturaAnulada"; 
	private static final String NUM_SERIE_FACTURA_ANULADA = "NumSerieFacturaAnulada";
	private static final String FECHA_EXPEDICION_FACTURA_ANULADA = "FechaExpedicionFacturaAnulada";
	private static final String HUELLA = "Huella";
	private static final String FECHA_HORA_HUSO_GEN_REGISTRO = "FechaHoraHusoGenRegistro";
	
	private String iDEmisorFacturaAnulada; 
	private String numSerieFacturaAnulada;
	private String fechaExpedicionFacturaAnulada;
	private String previousHuella;
	private String fechaHoraHusoGenRegistro;
	
	String getiDEmisorFacturaAnulada() {
		return iDEmisorFacturaAnulada;
	}
	VerifactuAnulacionHuella setiDEmisorFacturaAnulada(String iDEmisorFacturaAnulada) {
		this.iDEmisorFacturaAnulada = iDEmisorFacturaAnulada;
		return this;
	}
	
	String getNumSerieFacturaAnulada() {
		return numSerieFacturaAnulada;
	}
	VerifactuAnulacionHuella setNumSerieFacturaAnulada(String numSerieFacturaAnulada) {
		this.numSerieFacturaAnulada = numSerieFacturaAnulada;
		return this;
	}

	String getFechaExpedicionFacturaAnulada() {
		return fechaExpedicionFacturaAnulada;
	}
	VerifactuAnulacionHuella setFechaExpedicionFacturaAnulada(String fechaExpedicionFacturaAnulada) {
		this.fechaExpedicionFacturaAnulada = fechaExpedicionFacturaAnulada;
		return this;
	}

	String getPreviousHuella() {
		return previousHuella;
	}
	VerifactuAnulacionHuella setPreviousHuella(String previousHuella) {
		this.previousHuella = previousHuella;
		return this;
	}
	
	String getFechaHoraHusoGenRegistro() {
		return fechaHoraHusoGenRegistro;
	}
	VerifactuAnulacionHuella setFechaHoraHusoGenRegistro(String fechaHoraHusoGenRegistro) {
		this.fechaHoraHusoGenRegistro = fechaHoraHusoGenRegistro;
		return this;
	}
	
	private String pair( String name, String value) {
		return name + AonStringUtils.EQUAL + value; 
	}
	String format() {
		return new StringBuilder()
			.append(pair(ID_EMISOR_FACTURA_ANULADA, getiDEmisorFacturaAnulada()))
			.append(AonStringUtils.AMPERSAND)
			.append(pair(NUM_SERIE_FACTURA_ANULADA, getNumSerieFacturaAnulada()))
			.append(AonStringUtils.AMPERSAND)
			.append(pair(FECHA_EXPEDICION_FACTURA_ANULADA, getFechaExpedicionFacturaAnulada()))
			.append(AonStringUtils.AMPERSAND)
			.append(pair(HUELLA, getPreviousHuella()))
			.append(AonStringUtils.AMPERSAND)
			.append(pair(FECHA_HORA_HUSO_GEN_REGISTRO, getFechaHoraHusoGenRegistro()))
			.toString()
			;
	}
	
	String digest() {
		return AonStringUtils.upperCase( AonDigestUtils.sha256Hex(format()) );	
	}
}
