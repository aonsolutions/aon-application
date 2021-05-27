package com.esferalia.aon.file.payroll.fan.data;


/**
 * elementos de datos de Totales.
 */
public class EDT {
	
	private String tipoElemento;
	private Integer clave;
	private Integer calificadorClave;
	private Integer base;
	private String indicadorFactorTipo;
	private Integer parteEnteraTipo;
	private Integer parteDecimalFactorTipo;
	private Integer importe;
	private String signo;

	public String getTipoElemento() {
		return tipoElemento;
	}
	public void setTipoElemento(String tipoElemento) {
		this.tipoElemento = tipoElemento;
	}
	public Integer getClave() {
		return clave;
	}
	public void setClave(Integer clave) {
		this.clave = clave;
	}
	public Integer getCalificadorClave() {
		return calificadorClave;
	}
	public void setCalificadorClave(Integer calificadorClave) {
		this.calificadorClave = calificadorClave;
	}
	public Integer getBase() {
		return base;
	}
	public void setBase(Integer base) {
		this.base = base;
	}
	public String getIndicadorFactorTipo() {
		return indicadorFactorTipo;
	}
	public void setIndicadorFactorTipo(String indicadorFactorTipo) {
		this.indicadorFactorTipo = indicadorFactorTipo;
	}
	public Integer getParteEnteraTipo() {
		return parteEnteraTipo;
	}
	public void setParteEnteraTipo(Integer parteEnteraTipo) {
		this.parteEnteraTipo = parteEnteraTipo;
	}
	public Integer getParteDecimalFactorTipo() {
		return parteDecimalFactorTipo;
	}
	public void setParteDecimalFactorTipo(Integer parteDecimalFactorTipo) {
		this.parteDecimalFactorTipo = parteDecimalFactorTipo;
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
	
}
