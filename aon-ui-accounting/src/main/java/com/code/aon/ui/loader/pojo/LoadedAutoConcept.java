package com.code.aon.ui.loader.pojo;

public class LoadedAutoConcept implements ILoadedPojo{

	private String descripcion;
	
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
	
}
