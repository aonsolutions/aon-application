package com.esferalia.aon.file.payroll.certificate.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Empresa {

	private static final String DATOS_EMPRESA = "Datos_Empresa";
	private static final String CIF_NIF = "CIF_NIF";
	private static final String CCC = "CCC";

	private String cifNif;
	private String ccc;

	public String getCifNif() {
		return cifNif;
	}
	public void setCifNif(String cifNif) {
		this.cifNif = cifNif;
	}
	public String getCcc() {
		return ccc;
	}
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}
	
	public Element getElement(Document xmldoc){
		Element cifNif = xmldoc.createElement(CIF_NIF);
		cifNif.appendChild(xmldoc.createTextNode(getCifNif()));
		Element ccc = xmldoc.createElement(CCC);
		ccc.appendChild(xmldoc.createTextNode(getCcc()));
		
		Element empresa = xmldoc.createElement(DATOS_EMPRESA);
		empresa.appendChild(cifNif);
		empresa.appendChild(ccc);

		return empresa;
	}
	
}
