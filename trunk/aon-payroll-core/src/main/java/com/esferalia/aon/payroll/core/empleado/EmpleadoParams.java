package com.esferalia.aon.payroll.core.empleado;

import java.io.Serializable;

public class EmpleadoParams implements Serializable {

	private static final long serialVersionUID = -8655656513255427338L;
	
	private String documento;
	private String numSS;
	private String nombre;
	private String apellido;
	private String apellido2;
	private String empresa;
	
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getNumSS() {
		return numSS;
	}
	public void setNumSS(String numSS) {
		this.numSS = numSS;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getApellido2() {
		return apellido2;
	}
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}
	public String getEmpresa() {
		return empresa;
	}
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	
}
