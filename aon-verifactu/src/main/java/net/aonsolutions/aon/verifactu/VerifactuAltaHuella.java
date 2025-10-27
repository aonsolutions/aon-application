package net.aonsolutions.aon.verifactu;

import com.esferalia.aon.watson.server.codec.AonDigestUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class VerifactuAltaHuella {
	
	private static final String ID_EMISOR_FACTURA = "IDEmisorFactura"; 
	private static final String NUM_SERIE_FACTURA = "NumSerieFactura";
	private static final String FECHA_EXPEDICION_FACTURA = "FechaExpedicionFactura";
	private static final String TIPO_FACTURA = "TipoFactura";
	private static final String CUOTA_TOTAL = "CuotaTotal";
	private static final String IMPORTE_TOTAL = "ImporteTotal";
	private static final String HUELLA = "Huella";
	private static final String FECHA_HORA_HUSO_GEN_REGISTRO = "FechaHoraHusoGenRegistro";

	private String iDEmisorFactura; 
	private String numSerieFactura;
	private String fechaExpedicionFactura;
	private String tipoFactura;
	private String cuotaTotal;
	private String importeTotal;
	private String previousHuella;
	private String fechaHoraHusoGenRegistro;
	
	String getIDEmisorFactura() {
		return iDEmisorFactura;
	}
	VerifactuAltaHuella setIDEmisorFactura(String iDEmisorFactura) {
		this.iDEmisorFactura = iDEmisorFactura;
		return this;
	}
	
	String getNumSerieFactura() {
		return numSerieFactura;
	}
	VerifactuAltaHuella setNumSerieFactura(String numSerieFactura) {
		this.numSerieFactura = numSerieFactura;
		return this;
	}
	
	String getFechaExpedicionFactura() {
		return fechaExpedicionFactura;
	}
	VerifactuAltaHuella setFechaExpedicionFactura(String fechaExpedicionFactura) {
		this.fechaExpedicionFactura = fechaExpedicionFactura;
		return this;
	}
	
	String getTipoFactura() {
		return tipoFactura;
	}
	VerifactuAltaHuella setTipoFactura(String tipoFactura) {
		this.tipoFactura = tipoFactura;
		return this;
	}
	
	String getCuotaTotal() {
		return cuotaTotal;
	}
	VerifactuAltaHuella setCuotaTotal(String cuotaTotal) {
		this.cuotaTotal = cuotaTotal;
		return this;
	}
	
	String getImporteTotal() {
		return importeTotal;
	}
	VerifactuAltaHuella setImporteTotal(String importeTotal) {
		this.importeTotal = importeTotal;
		return this;
	}
	
	String getPreviousHuella() {
		return previousHuella;
	}
	VerifactuAltaHuella setPreviousHuella(String previousHuella) {
		this.previousHuella = previousHuella;
		return this;
	}
	
	String getFechaHoraHusoGenRegistro() {
		return fechaHoraHusoGenRegistro;
	}
	VerifactuAltaHuella setFechaHoraHusoGenRegistro(String fechaHoraHusoGenRegistro) {
		this.fechaHoraHusoGenRegistro = fechaHoraHusoGenRegistro;
		return this;
	}
	
	private String pair( String name, String value) {
		return name + AonStringUtils.EQUAL + value; 
	}
	String format() {
		return new StringBuilder()
			.append(pair(ID_EMISOR_FACTURA, getIDEmisorFactura()))
			.append(AonStringUtils.AMPERSAND)
			.append(pair(NUM_SERIE_FACTURA, getNumSerieFactura()))
			.append(AonStringUtils.AMPERSAND)
			.append(pair(FECHA_EXPEDICION_FACTURA, getFechaExpedicionFactura()))
			.append(AonStringUtils.AMPERSAND)
			.append(pair(TIPO_FACTURA, getTipoFactura()))
			.append(AonStringUtils.AMPERSAND)
			.append(pair(CUOTA_TOTAL, getCuotaTotal()))
			.append(AonStringUtils.AMPERSAND)
			.append(pair(IMPORTE_TOTAL, getImporteTotal()))
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
