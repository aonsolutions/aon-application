package com.esferalia.aon.salary;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.data.IData;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.Payments;

public abstract class AbstractSalary implements ISalary {

	public AbstractSalary() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnterpriseName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnterpriseAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEnterpriseDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCcc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployeeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployeeDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getRegistration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSocialSecurityNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuoteGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getSeniorityDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SalaryType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getChargeDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getIssueDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFullTime() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer getTimeUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Payments getPayments() throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getTotalPayment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Deductions getDeductions() throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getSocialSecurityContributions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getTotalDeduction() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public <T extends IDeduction> Collection<T> getCostS() 
			throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public <T extends IPayment> Collection<T> getPaymentS()
			throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends IDeduction> Collection<T> getDeductionS()
			throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends IDeduction> Collection<T> getEmbargoS() throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public <T extends IData> Map<String, List<T>> getDataS() throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Double getTotalIrpf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getTotalLiquid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getTotalEnterprise() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getRemuneration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getExtraPayProration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getCommonBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getRawCommonBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getProfessionalBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getOvertimeBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getNonEstructuralOvertimeBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getIrpfBase() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Double getInKindIrpfBase() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Double getInMoneyIrpfBase() {
        	// TODO Auto-generated method stub
        	return null;
	}
	
	@Override
	public Costs getEnterpriseCosts() throws SalaryException {
		// TODO Auto-generated method stub
		return null;
	}

}
