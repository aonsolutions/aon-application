package com.code.aon.ui.loader.pojo;


public class LoadedModules implements ILoadedPojo{

	private Integer id;
	private Integer ejercicio;
	private Integer agricola;
	private String epigrafe;
	private String descripcion;	
	private Double maxPersonas;
	private Double maxImporte;
	private Double porcentaje;
	private String codigoActividad;
	
	@Override
	public String getIdentifier() {
		return getId().toString();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getEjercicio() {
		return ejercicio;
	}

	public void setEjercicio(Integer ejercicio) {
		this.ejercicio = ejercicio;
	}

	public Integer getAgricola() {
		return agricola;
	}
	
	public boolean esAgricola() {
		return (this.agricola == 1);
	}

	public void setAgricola(Integer agricola) {
		this.agricola = agricola;
	}

	public String getEpigrafe() {
		return epigrafe;
	}

	public void setEpigrafe(String epigrafe) {
		this.epigrafe = epigrafe;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getMaxPersonas() {
		return maxPersonas;
	}

	public void setMaxPersonas(Double maxPersonas) {
		this.maxPersonas = maxPersonas;
	}

	public Double getMaxImporte() {
		return maxImporte;
	}

	public void setMaxImporte(Double maxImporte) {
		this.maxImporte = maxImporte;
	}

	public Double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(Double porcentaje) {
		this.porcentaje = porcentaje;
	}

	public String getCodigoActividad() {
		return codigoActividad;
	}

	public void setCodigoActividad(String codigoActividad) {
		this.codigoActividad = codigoActividad;
	}

	
}
