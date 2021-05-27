package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

public class StatisticYears implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7941687589569519676L;
	
	private int year;	
	private StaticalData[] statsData = new StaticalData[12];
	
	private double totalSSEnterprise;
	private double totalConcepts;
	private double totalSSEmployee;
	private double totalIRPF;
	private double totalLiquid;
	
	public StatisticYears() {
		
		year = 0;
		totalSSEnterprise=0;
		totalConcepts=0;
		totalSSEmployee=0;
		totalIRPF=0;
		totalLiquid=0;
		
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
	
	public void setTotalSSEnterprise(double pSSEnterprise) {
		totalSSEnterprise += pSSEnterprise;		
	}
	public double getTotalSSEnterprise(){
		return totalSSEnterprise;
	}
	public void setTotalConcepts(double pTotalConcepts) {
		totalConcepts+=pTotalConcepts;
	}
	public double getTotalConcepts() {
		return totalConcepts;
	}
	public void setTotalSSEmployee(double pSSEmployee) {
		totalSSEmployee+=pSSEmployee;
	}
	public double getTotalSSEmployee(){
		return totalSSEmployee;
	}
	public void setTotalIRPF(double pTotalIRPF) {
		totalIRPF+=pTotalIRPF;
	}
	public double getTotalIRPF() {
		return totalIRPF;
	}
	public void setTotalLiquid(double pTotalLiquid) {
		totalLiquid+=pTotalLiquid;
	}
	public double getTotalLiquid() {
		return totalLiquid;
	}
	
}
