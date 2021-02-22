package com.esferalia.aon.in.payroll.csv;

public interface IEnterprisePayroll {
	
	public String getEmployee();
	public String getWorkplace();
	
	public Double getRaw();
	public Double getEmployeeSS();
	public Double getIrpf();
	public Double getLiquid();
	public Double getEnterpriseSS();
	public Double getTotalCost();
	public Double getTotalSS();
	public Double getBonuses();
	
	public Double getCgcBase();
	public Double getIrpfBase();
	
	public Double getCgc();
	public Double getUnemployment();
	public Double getJobTraining();

}
