package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class StatisticYears implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7941687589569519676L;
	
	private int year;	
	private StaticalData[] statsData = new StaticalData[12];
	
	public StatisticYears() {
		
		year = 0;
		
		for(int x=0; x<12; x++) {
			statsData[x] = new StaticalData();
		}		
	}
	public StaticalData getStatsData(int pIndex) {
		return statsData[pIndex];
	}
	public int getYear() {
		return year;
	}	
	public void setYear(int pYear) {
		this.year = pYear;
	}
	public int getStatsDataLength() {
		return statsData.length;
	}
	
}
