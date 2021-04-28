package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class Statistics implements Serializable {
	
	// ----------------------------------------------- Variables
	
	private LinkedList<StatisticYears> years = new LinkedList<StatisticYears>();
	
	// ----------------------------------------------- Constructor
	
	public Statistics() {}
	
	// ----------------------------------------------- Methods
	
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

}
