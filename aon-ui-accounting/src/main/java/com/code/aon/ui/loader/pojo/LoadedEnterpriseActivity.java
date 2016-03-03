package com.code.aon.ui.loader.pojo;

import java.util.Date;

public class LoadedEnterpriseActivity implements ILoadedPojo{

	private String descripcion;	
	private Integer principal;
	
	private String iaeSeccion;
	private String iaeEpigrafe;
	private Integer recargo;
	private Integer regimenIVA;
	private Integer regimenIRPF;
	private Date fechaAlta;
	
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

	public String getIaeSeccion() {
		return iaeSeccion;
	}

	public void setIaeSeccion(String iaeSeccion) {
		this.iaeSeccion = iaeSeccion;
	}

	public String getIaeEpigrafe() {
		return iaeEpigrafe;
	}

	public void setIaeEpigrafe(String iaeEpigrafe) {
		this.iaeEpigrafe = iaeEpigrafe;
	}

	public Integer getRecargo() {
		return recargo;
	}

	public void setRecargo(Integer recargo) {
		this.recargo = recargo;
	}
	
	public boolean esRecargo() {
		return (getRecargo()==1);
	}

	public Integer getRegimenIVA() {
		return regimenIVA;
	}

	public void setRegimenIVA(Integer regimenIVA) {
		this.regimenIVA = regimenIVA;
	}

	public Integer getRegimenIRPF() {
		return regimenIRPF;
	}

	public void setRegimenIRPF(Integer regimenIRPF) {
		this.regimenIRPF = regimenIRPF;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
		
}
