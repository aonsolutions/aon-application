package com.esferalia.aon.in.payroll.csv;

public interface IEnterprisePayroll {
	
	public String getEmployee();
	public String getWorkplace();
	
	public double getRaw();
	public double getEmployeeSS();
	public double getIrpf();
	public double getLiquid();
	public double getEnterpriseSS();
	public double getTotalCost();
	public double getTotalSS();
	public double getBonuses();
	
	public double getCgcBase();
	public double getIrpfBase();

}
