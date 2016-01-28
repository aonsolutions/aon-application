package com.code.aon.ui.loader.pojo;

public class LoadedEnterpriseActivity implements ILoadedPojo{

	private String descripcion;	
	private Integer principal;
		
	@Override
	public String getIdentifier() {
		return null; 
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public Integer getPrincipal() {
		return principal;
	}

	public void setPrincipal(Integer principal) {
		this.principal = principal;
	}
	
	public boolean esPrincipal() {
		return (getPrincipal()==1);
	}
		
}
