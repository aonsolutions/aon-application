package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractDeduction;
import com.esferalia.aon.gwt.payroll.shared.ContractPayment;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeContractPaymentsObject {
	
	// ----------------------------------------------- Variables 
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private ContractPaymentData contractPaymentData;
	private List<ContractPayment> contractPayments;
	private List<ContractDeduction> contractDeductions;
	
	private Integer contractId;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractPaymentsObject(Integer contractId) {
		this.contractId = contractId;
		this.contractPayments = new ArrayList<ContractPayment>();
		this.contractDeductions = new ArrayList<ContractDeduction>();
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

	public List<ContractPayment> getContractPayments(Integer year) {
		this.contractPayments.clear();
		Date startDate = DateUtils.getFirstDayOfYear(year - 1900);
		Date endDate = DateUtils.getLastDayOfYear(year - 1900);
		
		for(ContractPayment contractPayment : contractPaymentData.getContractPayments()) {
			if(contractPayment.getId() < 0)
				continue;
			
			if(isInPeriod(startDate, endDate, contractPayment.getStartDate()))
				this.contractPayments.add(contractPayment);
			else if (null != contractPayment.getEndDate() && isInPeriod(startDate, endDate, contractPayment.getEndDate()))
				this.contractPayments.add(contractPayment);
			else if (null == contractPayment.getEndDate() && (isInPeriod(startDate, endDate, contractPayment.getStartDate()) || DateUtils.isBeforeOrEquals(contractPayment.getStartDate(), startDate)))
				this.contractPayments.add(contractPayment);
		}
		
		return this.contractPayments;
	}

	public List<ContractDeduction> getContractDeductions(Integer year) {
		this.contractDeductions.clear();
		Date startDate = DateUtils.getFirstDayOfYear(year - 1900);
		Date endDate = DateUtils.getLastDayOfYear(year - 1900);
		
		for(ContractDeduction contractDeduction : contractPaymentData.getContractDeductions()) {
			if(contractDeduction.getId() < 0)
				continue;
			
			if(isInPeriod(startDate, endDate, contractDeduction.getStartDate()))
				this.contractDeductions.add(contractDeduction);
			else if (null != contractDeduction.getEndDate() && isInPeriod(startDate, endDate, contractDeduction.getEndDate()))
				this.contractDeductions.add(contractDeduction);
			else if (null == contractDeduction.getEndDate() && (isInPeriod(startDate, endDate, contractDeduction.getStartDate()) || DateUtils.isBeforeOrEquals(contractDeduction.getStartDate(), startDate)))
				this.contractDeductions.add(contractDeduction);
		}
			
		return this.contractDeductions;
	}
	
	private boolean isInPeriod(Date start, Date end, Date date) {
		return null == date || (DateUtils.isAfterOrEquals(date, start) && DateUtils.isBeforeOrEquals(date, end));
	}

	public void deleteDeduction(ContractDeduction contractDeductionIn) {
		for(ContractDeduction contractDeduction : contractPaymentData.getContractDeductions()){
			if(contractDeduction.getId() == contractDeductionIn.getId()) {
				Integer id = contractDeduction.getId();
				contractDeduction.setId(id * -1);
			}
		}
	}

	public void deletePayment(ContractPayment contractPaymentIn) {
		for(ContractPayment contractPayment : contractPaymentData.getContractPayments()){
			if(contractPayment.getId() == contractPaymentIn.getId()) {
				Integer id = contractPayment.getId();
				contractPayment.setId(id * -1);
			}
		}
	}
	
}

