package com.esferalia.aon.file.payroll.afi.data;


public class ETF {

	private String identificador;
	private Integer clave;
	private Integer fecha;
	private Integer hora;
	private String fichero;
	private String prueba;
	private Integer contadorEmpresas;
	private Integer contadorSegmentos;

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
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

	public String getPrueba() {
		return prueba;
	}
	public void setPrueba(String prueba) {
		this.prueba = prueba;
	}
	public Integer getContadorEmpresas() {
		return contadorEmpresas;
	}
	public void setContadorEmpresas(Integer contadorEmpresas) {
		this.contadorEmpresas = contadorEmpresas;
	}
	public Integer getContadorSegmentos() {
		return contadorSegmentos;
	}
	public void setContadorSegmentos(Integer contadorSegmentos) {
		this.contadorSegmentos = contadorSegmentos;
	}
	
}
