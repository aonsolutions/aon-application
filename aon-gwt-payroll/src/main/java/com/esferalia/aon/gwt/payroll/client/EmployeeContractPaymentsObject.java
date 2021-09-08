package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc.ContractConceptCalcType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeContractPaymentsObject {
	
	// ----------------------------------------------- Variables 
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private ContractPaymentData contractPaymentData;
	private List<ContractConceptCalc> contractConceptCalcs;
	
	private Integer contractId;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractPaymentsObject(Integer contractId) {
		this.contractId = contractId;
		this.contractConceptCalcs = new ArrayList<>();
	}

	// ----------------------------------------------- DataBase.Methods
	
	public void getContractPayements(Consumer<ContractPaymentData> success, Consumer<Throwable> failure) {
		employeesService.getContractPayements(this.contractId, new AsyncCallback<ContractPaymentData>() {
			@Override
			public void onSuccess(ContractPaymentData contractPaymentDataIn) {
				contractPaymentData = contractPaymentDataIn;
				success.accept(contractPaymentData);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void updateContractPayments(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.updateContractPayments(this.contractId, this.contractPaymentData, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void createContractPayment(ContractConceptCalc contractConceptCalc, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.createContractPayment(this.contractId, contractConceptCalc, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}

	public List<ContractConceptCalc> getContractConceptCalcs(Integer year) {
		this.contractConceptCalcs.clear();
		Date startDate = DateUtils.getFirstDayOfYear(year - 1900);
		Date endDate = DateUtils.getLastDayOfYear(year - 1900);
		
		for(ContractConceptCalc contractConceptCalc : contractPaymentData.getCcontractConceptCalcs()) {
			if(contractConceptCalc.getId() < 0)
				continue;
			
			if(	isInPeriod(startDate, endDate, contractConceptCalc.getStartDate()) ||
				(null != contractConceptCalc.getEndDate() && isInPeriod(startDate, endDate, contractConceptCalc.getEndDate())) ||
				(null == contractConceptCalc.getEndDate() && (isInPeriod(startDate, endDate, contractConceptCalc.getStartDate()) || DateUtils.isBeforeOrEquals(contractConceptCalc.getStartDate(), startDate))))
				
				this.contractConceptCalcs.add(contractConceptCalc);
		}
		
		this.contractConceptCalcs.sort((o1, o2) -> compareString(o1, o2, getContractConceptCalcTypeShort(o1.getContractConceptCalcType()), getContractConceptCalcTypeShort(o2.getContractConceptCalcType())));	
		
		return this.contractConceptCalcs;
	}

	private boolean isInPeriod(Date start, Date end, Date date) {
		return null == date || (DateUtils.isAfterOrEquals(date, start) && DateUtils.isBeforeOrEquals(date, end));
	}

	public void deleteContractConceptCalc(ContractConceptCalc contractConceptCalcIN) {
		for(ContractConceptCalc contractConceptCalc : contractPaymentData.getCcontractConceptCalcs()){
			if(contractConceptCalc.getId().equals(contractConceptCalcIN.getId())) {
				Integer id = contractConceptCalc.getId();
				contractConceptCalc.setId(id * -1);
			}
		}
	}

	public void showHideContractConceptCalc(ContractConceptCalc contractConceptCalc) {
		String expression = contractConceptCalc.getExpression();
		
		if(!AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE"))
			expression = expression.replace("HIDE\\(.*\\); ", "");
		else
			expression = "HIDE(\"<div>" + contractConceptCalc.getDescription() + " oculto desde Conceptos de c\u00E1lculo</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\"); " + expression;
		
		
		contractConceptCalc.setExpression(expression);
	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s2.compareTo(s1);
	}
	
	private String getContractConceptCalcTypeShort(ContractConceptCalcType contractConceptCalcType) {
		switch (contractConceptCalcType) {
			case PAYMENT:
				return "P";
			case DEDUCTION:
				return "D";
			case BONUS:
				return "B";
			case COST:
				return "C";
			default:
				return "N/D";
		}
	}
	
}

