package com.code.aon.ui.loader.pojo;


public class LoadedModulesDetail implements ILoadedPojo{
	
	private Integer id;
	private String clave;
	private Integer linea;
	private Integer tipo;
	private String valor;
	private Double factor;
	private Double base;
	private String unidades;
	private Double valorMin;
	private Double valorMax;
	
	@Override
	public String getIdentifier() {
		return (getId() + "-" + getLinea());
	}

	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getClave() {
		return clave;
	}


	public void setClave(String clave) {
		this.clave = clave;
	}


	public Integer getLinea() {
		return linea;
	}


	public void setLinea(Integer linea) {
		this.linea = linea;
	}


	public Integer getTipo() {
		return tipo;
	}


	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}


	public String getValor() {
		return (valor==null?"":valor);
	}


	public void setValor(String valor) {
		this.valor = valor;
	}


	public Double getFactor() {
		return factor;
	}


	public void setFactor(Double factor) {
		this.factor = factor;
	}


	public Double getBase() {
		return base;
	}


	public void setBase(Double base) {
		this.base = base;
	}


	public String getUnidades() {
		return unidades;
	}


	public void setUnidades(String unidades) {
		this.unidades = unidades;
	}


	public Double getValorMin() {
		return valorMin;
	}


	public void setValorMin(Double valorMin) {
		this.valorMin = valorMin;
	}


	public Double getValorMax() {
		return valorMax;
	}


	public void setValorMax(Double valorMax) {
		this.valorMax = valorMax;
	}
	
	
}
