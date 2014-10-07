package com.esferalia.aon.file.payroll.fan.data;


public class ETF {

	private String clave;
	private Integer fecha;
	private Integer hora;
	private String fichero;
	private Integer contador;
	private Integer contadorTotal;

	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
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
