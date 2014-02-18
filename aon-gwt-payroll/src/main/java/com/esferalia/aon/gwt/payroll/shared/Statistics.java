package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.LinkedList;

public class Statistics implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9031263186810808872L;
	
	//private LinkedList<StaticalData> datos = new LinkedList<StaticalData>();
	private LinkedList<EnterpriseStatisticYears> years 
				= new LinkedList<EnterpriseStatisticYears>();
	
	/*
	 * Inicializo el LinkedList
	 */
	public Statistics() {

	}
	public void initializedListEnterpriseYears(int pCont) {
		
		for(int x=0; x<pCont; x++) {
			years.add(new EnterpriseStatisticYears());
		}
		
	}
	public void addYear(int pCont, int pYear) {
		years.get(pCont).setYear(pYear);
	}
	public LinkedList<EnterpriseStatisticYears> getEnterpriseStatisticYears() {
		return years;
	}

}
