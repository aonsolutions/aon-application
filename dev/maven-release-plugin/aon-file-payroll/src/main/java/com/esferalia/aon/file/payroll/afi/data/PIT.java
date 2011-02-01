package com.esferalia.aon.file.payroll.afi.data;

/**
 * periodo de incapacidad temporal
 */
public class PIT {

	private String tipoPrestacion;
	private String contingencia;
	private String formaPago;
	private String fechaBajaMedica;
	private String baseReguladora;

	public String getTipoPrestacion() {
		return tipoPrestacion;
	}
	public void setTipoPrestacion(String tipoPrestacion) {
		this.tipoPrestacion = tipoPrestacion;
	}
	public String getContingencia() {
		return contingencia;
	}
	public void setContingencia(String contingencia) {
		this.contingencia = contingencia;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public String getFechaBajaMedica() {
		return fechaBajaMedica;
	}
	public void setFechaBajaMedica(String fechaBajaMedica) {
		this.fechaBajaMedica = fechaBajaMedica;
	}
	public String getBaseReguladora() {
		return baseReguladora;
	}
	public void setBaseReguladora(String baseReguladora) {
		this.baseReguladora = baseReguladora;
	}

}
