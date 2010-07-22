package com.esferalia.aon.file.payroll.certificate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.esferalia.aon.file.payroll.certificate.data.CuentaCotizacion;

public class Certificate {

	private static final String CERTIFICADO_EMPRESA = "Certificado_empresa";
	
	private Integer fecha;
	private Integer hora;
	private String fichero;
	
	private List<CuentaCotizacion> cuentaCotizacion;

	public Certificate(String cif){
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String f =  formatter.format(date);
		fecha = Integer.parseInt(f);
		formatter = new SimpleDateFormat("HHmm");
		String t =  formatter.format(date);
		hora = Integer.parseInt(t);
		fichero =  cif+fecha.toString()+hora.toString();
	}
	
	public Integer getFecha() {
		return fecha;
	}
	public void setFecha(Integer fecha) {
		this.fecha = fecha;
	}

	public Integer getHora() {
		return hora;
	}
	public void setHora(Integer hora) {
		this.hora = hora;
	}
	
	public String getFichero() {
		return fichero;
	}
	public void setFichero(String fichero) {
		this.fichero = fichero;
	}
	
	public List<CuentaCotizacion> getCuentaCotizacion() {
		return cuentaCotizacion;
	}

	public void setCuentaCotizacion(List<CuentaCotizacion> cuentaCotizacion) {
		this.cuentaCotizacion = cuentaCotizacion;
	}
	
	public Element getElement(Document xmldoc){
		Element certificadoEmpresa = xmldoc.createElement(CERTIFICADO_EMPRESA);
		return certificadoEmpresa;
	}
	public void fillElement(Document xmldoc,Element certificadoEmpresa){
		for(CuentaCotizacion cc: getCuentaCotizacion()){
			certificadoEmpresa.appendChild(cc.getElement(xmldoc));
		}
	}
	
}
