package com.esferalia.aon.dsi.nominas.model;

public class Centro {
	
	private String codigo;
	private String situacion;
	
	// Id del centro de trabajo en AON (lo rellena el proceso del traspaso)
	private Integer workplace;   
	
	public String getCodigo() {
		return codigo;
	}
	public Centro setCodigo(String codigo) {
		this.codigo = codigo;
		return this;
	}
	public String getSituacion() {
		return situacion;
	}
	public Centro setSituacion(String situacion) {
		this.situacion = situacion;
		return this;
	}
	
	public Integer getWorkplace() {
		return workplace;
	}
	public Centro setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	
	@Override
	public String toString() {
		return "Centro [codigo=" + codigo + ", situacion=" + situacion + "]";
	}
	
}
