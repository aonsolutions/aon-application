package com.esferalia.aon.file.payroll.certificate.data;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class CuentaCotizacion {
	
	private static final String CUENTA_COTIZACION = "Cuenta_cotizacion";
	
	private Representante representante;
	private Empresa empresa;
	private List<Trabajador> listaTrabajadores;

	public Representante getRepresentante() {
		return representante;
	}
	public void setRepresentante(Representante representante) {
		this.representante = representante;
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	public List<Trabajador> getListaTrabajadores() {
		return listaTrabajadores;
	}
	public void setListaTrabajadores(List<Trabajador> listaTrabajadores) {
		this.listaTrabajadores = listaTrabajadores;
	}
	
	public Element getElement(Document xmldoc){
		Element cuentaCotizacion = xmldoc.createElement(CUENTA_COTIZACION);
		cuentaCotizacion.appendChild(getRepresentante().getElement(xmldoc));
		cuentaCotizacion.appendChild(getEmpresa().getElement(xmldoc));
		for(Trabajador t: getListaTrabajadores()){
			if(t!=null){
				cuentaCotizacion.appendChild(t.getElement(xmldoc));
			}
		}
		return cuentaCotizacion;
	}
	
}
