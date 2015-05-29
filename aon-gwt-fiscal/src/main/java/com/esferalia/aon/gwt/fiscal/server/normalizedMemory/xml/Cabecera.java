package com.esferalia.aon.gwt.fiscal.server.normalizedMemory.xml;


public class Cabecera {
		
	private String CIF;
	
	private String RazonSocial;
	
	private String Descripcion;
	
	private String TipoCuestionario;
	
	private String IdiomaCuestionario;
	
	private Boolean MemoriaNormalizada;
	
	private Integer Ejercicio;

	public String getCIF() {
		return CIF;
	}

	public void setCIF(String cIF) {
		CIF = cIF;
	}

	public String getRazonSocial() {
		return RazonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		RazonSocial = razonSocial;
	}

	public String getDescripcion() {
		return Descripcion;
	}

	public void setDescripcion(String descripcion) {
		Descripcion = descripcion;
	}

	public String getTipoCuestionario() {
		return TipoCuestionario;
	}

	public void setTipoCuestionario(String tipoCuestionario) {
		TipoCuestionario = tipoCuestionario;
	}

	public String getIdiomaCuestionario() {
		return IdiomaCuestionario;
	}

	public void setIdiomaCuestionario(String idiomaCuestionario) {
		IdiomaCuestionario = idiomaCuestionario;
	}

	public Boolean getMemoriaNormalizada() {
		return MemoriaNormalizada;
	}

	public void setMemoriaNormalizada(Boolean memoriaNormalizada) {
		MemoriaNormalizada = memoriaNormalizada;
	}

	public Integer getEjercicio() {
		return Ejercicio;
	}

	public void setEjercicio(Integer ejercicio) {
		Ejercicio = ejercicio;
	}
	
}
