package com.esferalia.aon.in.payroll.csv;

public interface IEnterprisePayroll {
	
	public String getEmployee();
	public String getWorkplace();
	public String getSalaryType();
	
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
	public Double getCgp();
	public Double getUnemployment();
	public Double getJobTraining();
	public Double getAdvancedPayment();
	public Double getOtherDeductions();
	public Double getEstruc();
	public Double getNoEstruct();
	public Double getEmbargos();
	
	public Double getCgcEnterprise();
	public Double getCgpEnterprise();
	public Double getUnemploymentEnterprise();
	public Double getJobTrainingEnterprise();
	public Double getFogasaEnterprise();
	public Double getEstrucEnterprise();
	public Double getNoEstructEnterprise();

}
