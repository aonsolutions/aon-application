package com.esferalila.aon.file.payroll.fdi.data;

public class ETF {

	private Integer clave;
	private Integer fecha;
	private Integer hora;
	private String fichero;
	private String identificacion;
	private Integer contador;
	private Integer contadorTotal;

	public Integer getClave() {
		return clave;
	}
	public void setClave(Integer clave) {
		this.clave = clave;
	}

	public Integer getFecha() {
		return fecha;
	}
	public void setFecha(Integer fecha) {
		this.fecha = fecha;
	}

	public Integer getHora() {
		return hora;
	}
	public void setHora(Integer hora) {
		this.hora = hora;
	}

	public String getFichero() {
		return fichero;
	}
	public void setFichero(String fichero) {
		this.fichero = fichero;
	}

	public String getIdentificacion() {
		return identificacion;
	}
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}
	public Integer getContador() {
		return contador;
	}
	public void setContador(Integer contador) {
		this.contador = contador;
	}
	public Integer getContadorTotal() {
		return contadorTotal;
	}
	public void setContadorTotal(Integer contadorTotal) {
		this.contadorTotal = contadorTotal;
	}
}
