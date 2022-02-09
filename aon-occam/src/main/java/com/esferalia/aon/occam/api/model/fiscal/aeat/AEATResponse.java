package com.esferalia.aon.occam.api.model.fiscal.aeat;

import java.io.Serializable;
import java.util.LinkedList;

public class AEATResponse implements Serializable {
	
	private static final long serialVersionUID = 4399256175462074749L;

	private LinkedList<String> errores;

	private String formaPago;
	private String codigoSeguroVerificacion;
	private String fecha;
	private String hora;
	private String expediente;
	private String nifPresentador;
	private String apellidosNombrePresentador;
	private String tipoRepresentacion;
	private String nifDeclarante;
	private String apellidosNombreDeclarante;
	private String modelo;
	private String ejercicio;
	private String periodo;
	private String justificante;
	private String nrcPago;
	private String importeAIngresar;
	private String idioma;
	private String urlPdf;
	private String presentacionLotes;

	public boolean isWrong() {
		return getErrores() != null || getErrores().isEmpty();
	}
	
	public boolean isCorrect() {
		return !isWrong();
	}
	public LinkedList<String> getErrores() {
		return errores;
	}
	public AEATResponse setErrores(LinkedList<String> errores) {
		this.errores = errores;
		return this;
	}
	public void addError(String msg) {
		if (getErrores() == null) {
			setErrores(new LinkedList<>());
		}
		getErrores().add(msg);
	}

	public String getFormaPago() {
		return formaPago;
	}
	public AEATResponse setFormaPago(String formaPago) {
		this.formaPago = formaPago;
		return this;
	}

	public String getCodigoSeguroVerificacion() {
		return codigoSeguroVerificacion;
	}
	public AEATResponse setCodigoSeguroVerificacion(String codigoSeguroVerificacion) {
		this.codigoSeguroVerificacion = codigoSeguroVerificacion;
		return this;
	}

	public String getFecha() {
		return fecha;
	}
	public AEATResponse setFecha(String fecha) {
		this.fecha = fecha;
		return this;
	}

	public String getHora() {
		return hora;
	}
	public AEATResponse setHora(String hora) {
		this.hora = hora;
		return this;
	}

	public String getExpediente() {
		return expediente;
	}
	public AEATResponse setExpediente(String expediente) {
		this.expediente = expediente;
		return this;
	}

	public String getNifPresentador() {
		return nifPresentador;
	}
	public AEATResponse setnIFPresentador(String nifPresentador) {
		this.nifPresentador = nifPresentador;
		return this;
	}

	public String getApellidosNombrePresentador() {
		return apellidosNombrePresentador;
	}
	public AEATResponse setApellidosNombrePresentador(String apellidosNombrePresentador) {
		this.apellidosNombrePresentador = apellidosNombrePresentador;
		return this;
	}

	public String getTipoRepresentacion() {
		return tipoRepresentacion;
	}
	public AEATResponse setTipoRepresentacion(String tipoRepresentacion) {
		this.tipoRepresentacion = tipoRepresentacion;
		return this;
	}

	public String getNifDeclarante() {
		return nifDeclarante;
	}
	public AEATResponse setNifDeclarante(String nifDeclarante) {
		this.nifDeclarante = nifDeclarante;
		return this;
	}

	public String getApellidosNombreDeclarante() {
		return apellidosNombreDeclarante;
	}
	public AEATResponse setApellidosNombreDeclarante(String apellidosNombreDeclarante) {
		this.apellidosNombreDeclarante = apellidosNombreDeclarante;
		return this;
	}

	public String getModelo() {
		return modelo;
	}
	public AEATResponse setModelo(String modelo) {
		this.modelo = modelo;
		return this;
	}

	public String getEjercicio() {
		return ejercicio;
	}
	public AEATResponse setEjercicio(String ejercicio) {
		this.ejercicio = ejercicio;
		return this;
	}

	public String getPeriodo() {
		return periodo;
	}
	public AEATResponse setPeriodo(String periodo) {
		this.periodo = periodo;
		return this;
	}

	public String getJustificante() {
		return justificante;
	}
	public AEATResponse setJustificante(String justificante) {
		this.justificante = justificante;
		return this;
	}

	public String getNrcPago() {
		return nrcPago;
	}
	public AEATResponse setNrcPago(String nrcPago) {
		this.nrcPago = nrcPago;
		return this;
	}

	public String getImporteAIngresar() {
		return importeAIngresar;
	}
	public AEATResponse setImporteAIngresar(String importeAIngresar) {
		this.importeAIngresar = importeAIngresar;
		return this;
	}

	public String getIdioma() {
		return idioma;
	}
	public AEATResponse setIdioma(String idioma) {
		this.idioma = idioma;
		return this;
	}

	public String getUrlPdf() {
		return urlPdf;
	}
	public AEATResponse setUrlPdf(String urlPdf) {
		this.urlPdf = urlPdf;
		return this;
	}

	public String getPresentacionLotes() {
		return presentacionLotes;
	}
	public AEATResponse setPresentacionLotes(String presentacionLotes) {
		this.presentacionLotes = presentacionLotes;
		return this;
	}

}
