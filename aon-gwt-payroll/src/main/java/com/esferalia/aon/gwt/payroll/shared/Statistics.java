package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.LinkedList;

public class Statistics implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9031263186810808872L;
	
	//private LinkedList<StaticalData> datos = new LinkedList<StaticalData>();
	private LinkedList<StatisticYears> years 
				= new LinkedList<StatisticYears>();
	
	/*
	 * Inicializo el LinkedList
	 */
	public Statistics() {

	}
	public void initializedListYears(int pCont) {
		years.clear();
		for(int x=0; x<pCont; x++) {
			years.add(new StatisticYears());
		}
		
	}
	public void addYear(int pCont, int pYear) {
		years.get(pCont).setYear(pYear);
	}
	public LinkedList<StatisticYears> getStatisticYears() {
		return years;
	}
	
	public int getMultiplicador(){
		return 4;
	}
	public int getDividendo(){
		return 5;
	}

}
