package com.esferalia.aon.payroll;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.entity.master.ContractDB;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;

@Entity
@Table(name="contract")
public class Contract extends ContractDB {

	private static final long serialVersionUID = 1L;
	
	private Set<ContractPayment> contractPayments = new HashSet<ContractPayment>();
	private Set<ContractDeduction> contractDeductions = new HashSet<ContractDeduction>();

	@OneToMany(mappedBy = "contract", cascade={CascadeType.REMOVE})
	public Set<ContractPayment> getContractPayments() {
		return this.contractPayments;
	}
	public void setContractPayments( Set<ContractPayment> contractPayments ) {
		this.contractPayments = contractPayments;
	}
	
	@OneToMany(mappedBy = "contract", cascade={CascadeType.REMOVE})
	public Set<ContractDeduction> getContractDeductions() {
		return this.contractDeductions;
	}
	public void setContractDeductions( Set<ContractDeduction> contractDeductions ) {
		this.contractDeductions = contractDeductions;
	}
	
	@Transient
	public boolean isBlocked() {
		return getStatus() == ContractStatus.BLOCKED;
	}
	
	@Transient
	public boolean isPending() {
		return getStatus() == ContractStatus.PENDING;
	}
	
	@Transient
	public boolean isProcessed() {
		return getStatus() == ContractStatus.PROCESSED;
	}

	@Transient
	public boolean isSeniorityDateDifferent() {
		return !DateUtils.isSameDay(getStartDate(), getSeniorityDate());
	}
	
	@Transient
	@Deprecated
	public ISalaryCalculatorContext getSalaryCalculatorContext(int year, Month month, SalaryType salaryType) throws SalaryException {
		ISalaryCalculatorContext ctx = new ContractSalaryCalculatorContext(this,year, month,salaryType);
		return ctx;
	}
	
	@Transient
	public ISalaryCalculatorContext getSalaryCalculatorContext(Date startdate, Date endDate , SalaryType salaryType) throws SalaryException {
		ISalaryCalculatorContext ctx = new ContractSalaryCalculatorContext(this,startdate, endDate,salaryType);
		return ctx;
	}

	@Transient
	public ISalaryCalculatorContext getSalaryCalculatorContext(Date startDate, Date endDate, Date issueDate) throws SalaryException {
			// TODO : It's verry, very tricky and old. I hate this.  
			java.util.Calendar  calendar = java.util.Calendar.getInstance();
			calendar.setTime(issueDate);
			int year = calendar.get(java.util.Calendar.YEAR);
			Month month = Month.getMonthByValue(calendar.get(java.util.Calendar.MONTH));
			ISalaryCalculatorContext ctx = new ContractSalaryCalculatorContext(this, year, month , SalaryType.SALARY);
			return ctx;
	}
	
	
	@Transient
	public boolean isActive(Date start, Date end ) {
		if ( getStartDate().after(end))
			return false;
		if ( getEndDate() == null ) 
			return true;
		if ( getEndDate().before(start) )
			return false;
		return true;
	}
	
	@Transient
	public ContractModel getModel(){
		return null;
	}
	
	@Transient
	public void setModel(ContractModel model){
		
	}

}
