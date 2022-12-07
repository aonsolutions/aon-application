package com.esferalia.aon.in.payroll.csv;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.SalaryType;

public interface IEnterprisePayroll {
	
	public Integer getEmployeeId();
	
	public Date getStartDate();
	public Date getEndDate();
	
	public String getEmployee();
	public String getEmployeeNaf();
	public String getCcc();
	public String getWorkplace();
	public SalaryType getSalaryType();
	
	public Double getRaw();
	public Double getEmployeeSS();
	public Double getIrpf();
	public Double getInKind();
	public Double getLiquid();
	public Double getEnterpriseSS();
	public Double getTotalCost();
	public Double getTotalSS();
	public Double getBonuses();
	
	public Double getItCompensation();	
	
	public Double getFundae();
	public void setFundae(Double fundae);	
	
	public Double getCgcBase();
	public Double getIrpfBase();
	public Double getInkindIrpfBase();
	public Double getMoneyIrpfBase();
	
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
	
	public IEnterprisePayroll getOriginalPayroll();
	public IEnterprisePayroll setOriginalPayroll(IEnterprisePayroll originalPayroll);

}
