package com.code.aon.ui.loader.pojo;

public class LoadedCompanyDirStaff implements ILoadedPojo{

	private String documento;
	private String nombre;
	private Integer socio;
	private Double porcentaje;
	
	@Override
	public String getIdentifier() {
		return null; 
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getSocio() {
		return socio;
	}

	public void setSocio(Integer esSocio) {
		this.socio = esSocio;
	}
	
	public boolean esSocio() {
		return (getSocio()==1);
	}

	public Double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(Double porcentaje) {
		this.porcentaje = porcentaje;
	}
		
}
