package com.esferalia.aon.file.payroll.cra.data;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * TRB - Datos de TRabajador
 *
 */
public class TRB {
	
	private String naf;
	
	private List<CRE> creList;

	public String getNaf() {
		return naf;
	}

	public void setNaf(String naf) {
		this.naf = naf;
	}

	public List<CRE> getCreList() {
		if(creList==null){
			creList = new LinkedList<CRE>();
		}
		return creList;
	}

	public void setCreList(List<CRE> creList) {
		this.creList = creList;
	}
	
	
		
}
