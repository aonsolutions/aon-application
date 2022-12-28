package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc.ContractConceptCalcType;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeContractPaymentsObject {
	
	// ----------------------------------------------- Variables 
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private ContractPaymentData contractPaymentData;
	private List<ContractConceptCalc> contractConceptCalcs;
	
	private Integer contractId;
	private Date contractStartDate;
	private Date contractEndDate;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractPaymentsObject(Integer contractId, Date contractStartDate, Date contractEndDate) {
		this.contractId = contractId;
		this.contractStartDate =  contractStartDate;
		this.contractEndDate = contractEndDate;
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
	
	public void getAvailablePayments(Consumer<List<Payment>> success, Consumer<Throwable> failure) {
		employeesService.getAvailablePayments(this.contractId, new AsyncCallback<List<Payment>>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(List<Payment> result) {
				success.accept(result);
			}
			
		});
	}
	
	public void createContractPayment(List<ContractConceptCalc> contractConceptCalcList, Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.createContractPayment(this.contractId, contractConceptCalcList, new AsyncCallback<Void>() {
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

	public List<ContractConceptCalc> getContractConceptCalcs(String yearValue, String monthValue, String paymentTypeValue) {
		this.contractConceptCalcs.clear();
		
		// Se filtra solo por tipo de variable sin fechas
		if(AonStringUtils.isBlank(yearValue)) {
			ContractConceptCalcType paymentType = getPaymentType(paymentTypeValue);
			
			for(ContractConceptCalc contractConceptCalc : contractPaymentData.getCcontractConceptCalcs()) {
				if(contractConceptCalc.getId() < 0)
					continue;
				
				if(null == paymentType || paymentType.equals(contractConceptCalc.getContractConceptCalcType()))
					this.contractConceptCalcs.add(contractConceptCalc);
			}
		// Se filtra por tipo de variable y fechas
		} else {
			Integer year = Integer.parseInt(yearValue);
			Date startDate = DateUtils.getFirstDayOfYear(year - 1900);
			Date endDate = DateUtils.getLastDayOfYear(year - 1900);
			
			if(AonStringUtils.isNotBlank(monthValue)) {
				Integer month = Integer.parseInt(monthValue);
				startDate.setMonth(month);
				startDate = DateUtils.getFirstDayOfMonth(startDate);
				endDate = DateUtils.getLastDayOfMonth(startDate);
			}
			
			ContractConceptCalcType paymentType = getPaymentType(paymentTypeValue);
			
			for(ContractConceptCalc contractConceptCalc : contractPaymentData.getCcontractConceptCalcs()) {
				if(contractConceptCalc.getId() < 0)
					continue;
				
				if(	(isInPeriod(startDate, endDate, contractConceptCalc.getStartDate(), contractConceptCalc.getEndDate()) ||
					(null != contractConceptCalc.getEndDate() && isInPeriod(startDate, endDate, contractConceptCalc.getEndDate(), contractConceptCalc.getEndDate())) ||
					(null == contractConceptCalc.getEndDate() && (isInPeriod(startDate, endDate, contractConceptCalc.getStartDate(), contractConceptCalc.getEndDate()) || DateUtils.isBeforeOrEquals(contractConceptCalc.getStartDate(), startDate))))
					&& (null == paymentType || paymentType.equals(contractConceptCalc.getContractConceptCalcType())))
					
					this.contractConceptCalcs.add(contractConceptCalc);
			}
		}
		
		this.contractConceptCalcs.sort((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
		Collections.reverse(this.contractConceptCalcs);
		
//		this.contractConceptCalcs.sort((o1, o2) -> compareString(o1, o2, getContractConceptCalcTypeShort(o1.getContractConceptCalcType()), getContractConceptCalcTypeShort(o2.getContractConceptCalcType())));	
		
		return this.contractConceptCalcs;
	}

	private boolean isInPeriod(Date start, Date end, Date varStart, Date varEnd) {
		return null == varStart || 
				(DateUtils.isAfterOrEquals(varStart, start) && DateUtils.isBeforeOrEquals(varStart, end)) || 
				(null == varEnd && DateUtils.isBeforeOrEquals(varStart, end)) ||
				(DateUtils.isBeforeOrEquals(varStart, start) && DateUtils.isAfterOrEquals(varEnd, end));
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
		ContractConceptCalcType type = contractConceptCalc.getContractConceptCalcType();
		
		if(type == ContractConceptCalcType.PAYMENT) {
			expression = !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") ?
					expression.replaceAll("HIDE.*; ", "") :
					"HIDE(\"<div>" + contractConceptCalc.getDescription() + " oculto desde Conceptos de c\u00E1lculo</div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\"); " + expression;
		} else {
			expression = !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") ?
					expression.replaceAll("HIDE.*; ", "") :
					"HIDE(\"<div>Oculto desde Conceptos de c\u00E1lculo</div><div>&nbsp;</div>\"); " + expression;
		}
		
		contractConceptCalc.setExpression(expression);
	}
	
	private ContractConceptCalcType getPaymentType(String paymentTypeValue) {
		switch (paymentTypeValue) {
		case "0":
			return ContractConceptCalcType.PAYMENT;
		case "1":
			return ContractConceptCalcType.DEDUCTION;
		case "2":
			return ContractConceptCalcType.COST;
		case "3":
			return ContractConceptCalcType.BONUS;
		case "4":
			return ContractConceptCalcType.EMBARGO;
		default:
			return null;
		}
	}
	
	// ----------------------------------------------- Getter
	
	public Date getContractStartDate() {
		return this.contractStartDate;
	}
	
	public Date getContractEndDate() {
		return this.contractEndDate;
	}
	
}

