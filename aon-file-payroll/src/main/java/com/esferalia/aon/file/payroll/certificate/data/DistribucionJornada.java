package com.esferalia.aon.file.payroll.certificate.data;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DistribucionJornada {
	
	private static final String DISTRIBUCION_JORNADAS = "DistribucionJornadas";
	
	private List<Periodo> listaPeriodos;

	public List<Periodo> getListaPeriodos() {
		return listaPeriodos;
	}

	public void setListaPeriodos(List<Periodo> listaPeriodos) {
		this.listaPeriodos = listaPeriodos;
	}

	public Element getElement(Document xmldoc) {
		Element distribucionJornadas = null;
		if(getListaPeriodos()!=null){
			distribucionJornadas = xmldoc.createElement(DISTRIBUCION_JORNADAS);
			for(Periodo p: getListaPeriodos()){
				distribucionJornadas.appendChild(p.getElement(xmldoc));
			}
		}
		return distribucionJornadas;
	}
	
}
