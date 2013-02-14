package com.esferalia.aon.gwt.payroll.server;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Deduction;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Payment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.utils.ReportUtils;
import com.google.gwt.dom.client.Style.Clear;

public class SalaryDraftBuilder implements ISalaryBuilder, ContractSalaryCalculator.IListener {
	
	static class PaymentComparator implements Comparator<Payment> {
		@Override
		public int compare(Payment arg0, Payment arg1) {
			int compareTo = arg0.getType().compareTo(arg1.getType());
			if ( compareTo != 0 ) {
				return compareTo;
			}
			String description0 = arg0.getDescription();
			String description1 = arg1.getDescription();
			if ( description0 == null ) {
				return description1 == null ? 0 : -1;
			}
			return description0.compareTo(description1);
		}
	}
	

	static class DeductionComparator implements Comparator<Deduction> {
		public int compare(Deduction arg0, Deduction arg1) {
			int compareTo = arg0.getType().compareTo(arg1.getType());
			if ( compareTo != 0 ) {
				return compareTo;
			}
			String description0 = arg0.getDescription();
			String description1 = arg1.getDescription();
			if ( description0 == null ) {
				return description1 == null ? 0 : -1;
			}
			return description0.compareTo(description1);
			
		};
	}


	private SalaryDraft salaryDraft;

	public SalaryDraftBuilder(SalaryDraft salaryDraft) {
		this.salaryDraft = salaryDraft;
	}

	@Override
	public ISalary getSalary() {
		Collections.sort(salaryDraft.getPayments(), new PaymentComparator());
		Collections.sort(salaryDraft.getDeductions(), new DeductionComparator());
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createNewSalary() {
		salaryDraft.clearContext();
		salaryDraft.clearErrors();
		salaryDraft.clearPayments();
		salaryDraft.clearDeductions();
	}

	@Override
	public void setContract(Object contract) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCcc(String ccc) {
		salaryDraft.setEnterpriseCCC(ccc);

	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
		salaryDraft.setEnterpriseName(enterpriseName);

	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		salaryDraft.setEnterpriseAddress(enterpriseAddress);
	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		salaryDraft.setEnterpriseDocument(enterpriseDocument);
	}

	@Override
	public void setRegistration(Integer registration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEmployeeName(String employeeName) {
		salaryDraft.setEmployeeName(employeeName);
	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		salaryDraft.setEmployeeDocument(employeeDocument);
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		salaryDraft.setEmployeeSS(socialSecurityNumber);
	}

	@Override
	public void setCategory(String category) {
		salaryDraft.setEmployeeAgreementCategory(category);
	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		salaryDraft.setEmployeeQuoteGroup(quoteGroup);
	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		salaryDraft.setEmployeeSeniorityDate(seniorityDate);
	}

	@Override
	public void setType(SalaryType type) {
		salaryDraft.setType(Type.values()[type.ordinal()]);
	}

	@Override
	public void setIssueDate(Date issueDate) {
		salaryDraft.setIssueDate(issueDate);
	}

	@Override
	public void setChargeDate(Date chargeDate) {
		salaryDraft.setChargeDate(chargeDate);
	}

	@Override
	public void setStartDate(Date startDate) {
		salaryDraft.setStartDate(startDate);
	}

	@Override
	public void setEndDate(Date endDate) {
		salaryDraft.setEndDate(endDate);
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setItBase(Double itBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCgcBase(Double cgcBase) {
		salaryDraft.setCgcBase(cgcBase);
	}

	@Override
	public void setCgpBase(Double cgpBase) {
		salaryDraft.setCgpBase(cgpBase);
	}

	@Override
	public void setRemuneration(Double remuneration) {
		salaryDraft.setRemuneration(remuneration);
	}

	@Override
	public void setProExtBase(Double proExtBase) {
		salaryDraft.setProrationBase(proExtBase);
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		salaryDraft.setIrpfBase(irpfBase);
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		salaryDraft.sethExtraBase(hExtraBase);
	}

	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		salaryDraft.setNonHExtraBase(nonHExtraBase);
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		salaryDraft.setTotalLiquid(totalLiquid);
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		salaryDraft.setTotalPayment(totalPayment);
	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		salaryDraft.setTotalDeduction(totalDeduction);
	}

	@Override
	public void setSocialSecurityContributions(
			Double socialSecurityContributions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addBonus(String concept, Double amount, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEmbargo(Integer embargo, Double amount, String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCost(DeductionType type, String concept, Double amount,
			String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPayment(PaymentType type, String concept, Double amount,
			String description, String expression, Map<String, Object> context) {
		addContext(context);
		salaryDraft.addPayment( Payment.Type.values()[type.ordinal()], description, amount);
	}

	@Override
	public void addDeduction(DeductionType type, String concept, Double amount,
			String description, String expression) {
		salaryDraft.addDeduction(Deduction.Type.values()[type.ordinal()], description, amount);

	}
	
	@Override
	public void setListener(ISalaryBuilderListener listener) {
		// TODO Auto-generated method stub

	}

	// ContractSalaryCalculator.IListener methods


	@Override
	public void onCheckError(String message) {
		salaryDraft.addError(message);
	}

	@Override
	public void onInvalidData(String variableName, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckError(IContractPayment payment, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInvalidData(IContractPayment payment, String variableName,
			String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckError(IContractDeduction deduction, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInvalidData(IContractDeduction deduction,
			String variableName, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckError(IContractBonus bonus, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInvalidData(IContractBonus bonus, String variableName,
			String message) {
		// TODO Auto-generated method stub
		
	}
	
	private void addContext(Map<String, Object> context ) {
		for (Entry<String, Object> entry : context.entrySet()) {
			salaryDraft.addVariable(entry.getKey(), entry.getValue(), null, null);
		}
	}
	

}
