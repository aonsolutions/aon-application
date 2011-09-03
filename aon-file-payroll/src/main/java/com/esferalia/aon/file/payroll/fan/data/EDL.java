package com.esferalia.aon.file.payroll.fan.data;


/**
 * elementos de datos. linea.
 */
public class EDL {
	
	private String tipoElementoDatos;
	private Integer clave;
	private Integer elemento;
	private Integer importe;
	private String signo;
	private Integer tipoResolucion;
	private Integer fechaResolucion;
	private Integer inicioPeriodo;
	private Integer finPeriodo;
	private String referencia;

	public String getTipoElementoDatos() {
		return tipoElementoDatos;
	}
	public void setTipoElementoDatos(String tipoElementoDatos) {
		this.tipoElementoDatos = tipoElementoDatos;
	}
	public Integer getClave() {
		return clave;
	}
	public void setClave(Integer clave) {
		this.clave = clave;
	}
	public Integer getElemento() {
		return elemento;
	}
	public void setElemento(Integer elemento) {
		this.elemento = elemento;
	}
	public Integer getImporte() {
		return importe;
	}
	public void setImporte(Integer importe) {
		this.importe = importe;
	}
	public String getSigno() {
		return signo;
	}
	public void setSigno(String signo) {
		this.signo = signo;
	}
	public Integer getTipoResolucion() {
		return tipoResolucion;
	}
	public void setTipoResolucion(Integer tipoResolucion) {
		this.tipoResolucion = tipoResolucion;
	}
	public Integer getFechaResolucion() {
		return fechaResolucion;
	}
	public void setFechaResolucion(Integer fechaResolucion) {
		this.fechaResolucion = fechaResolucion;
	}
	public Integer getInicioPeriodo() {
		return inicioPeriodo;
	}
	public void setInicioPeriodo(Integer inicioPeriodo) {
		this.inicioPeriodo = inicioPeriodo;
	}
	public Integer getFinPeriodo() {
		return finPeriodo;
	}
	public void setFinPeriodo(Integer finPeriodo) {
		this.finPeriodo = finPeriodo;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	
}
