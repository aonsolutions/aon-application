package com.esferalia.aon.payroll.core.empresa;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.enumeration.FileStatus;

public class RemesaCertificadoEmpresaParams implements Serializable {

	private static final long serialVersionUID = -3353191952616724056L;

	private String empresa;
	private String documento;
	private String nombre;
	private String apellido;
	private String apellido2;
	private Date fecha;
	private Date fechaDesde;
	private Date fechaHasta;
	private FileStatus[] estados;
	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Date getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public Date getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
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
	public FileStatus[] getEstados() {
		return estados;
	}
	public void setEstados(FileStatus[] estados) {
		this.estados = estados;
	}
	
}
