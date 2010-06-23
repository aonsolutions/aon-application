package com.esferalia.aon.file.payroll.certificate.data;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Representante {
	
	private static final String DATOS_REPRESENTANTE = "Datos_Representante";
	private static final String CIF_NIF = "CIF_NIF";
	private static final String NOMBRE = "Nombre";
	private static final String APELLIDO1 = "Apellido1";
	private static final String APELLIDO2 = "Apellido2";
	private static final String CARGO = "Cargo";

	private String cifNif;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String cargo;

	public String getCifNif() {
		return cifNif;
	}
	public void setCifNif(String cifNif) {
		this.cifNif = cifNif;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido1() {
		return apellido1;
	}
	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}
	public String getApellido2() {
		return apellido2;
	}
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public Element getElement(Document xmldoc) {
		Element representante = xmldoc.createElement(DATOS_REPRESENTANTE);

		Element cifNif = xmldoc.createElement(CIF_NIF);
		cifNif.appendChild(xmldoc.createTextNode(getCifNif()));
		representante.appendChild(cifNif);

		Element nombre = xmldoc.createElement(NOMBRE);
		nombre.appendChild(xmldoc.createTextNode(getNombre()));
		representante.appendChild(nombre);
		
		Element apellido1 = xmldoc.createElement(APELLIDO1);
		apellido1.appendChild(xmldoc.createTextNode(getApellido1()));
		representante.appendChild(apellido1);
		
		Element apellido2 = null;
		if(!StringUtils.isBlank(getApellido2())){
			apellido2 = xmldoc.createElement(APELLIDO2);
			apellido2.appendChild(xmldoc.createTextNode(getApellido2()));
			representante.appendChild(apellido2);
		}
		
		Element cargo = null;
		if(!StringUtils.isBlank(getCargo())){
			cargo = xmldoc.createElement(CARGO);
			cargo.appendChild(xmldoc.createTextNode(getCargo()));
			representante.appendChild(cargo);
		}

		return representante;
	}
	
}
