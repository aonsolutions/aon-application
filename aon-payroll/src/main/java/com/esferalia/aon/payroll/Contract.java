package com.esferalia.aon.payroll;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ContractDB;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContractStatus;
import com.esferalia.aon.payroll.util.PayrollUtils;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.SalaryType;

@Entity
@Table(name="contract")
public class Contract extends ContractDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
		return getSsStatus() == ContractStatus.BLOCKED;
	}
	
	@Transient
	public boolean isPending() {
		return getSsStatus() == ContractStatus.PENDING;
	}
	
	@Transient
	public boolean isProcessed() {
		return getSsStatus() == ContractStatus.PROCESSED;
	}

	@Transient
	public boolean isSeniorityDateDifferent() {
		return !DateUtils.isSameDay(getStartDate(), getSeniorityDate());
	}
	
	@Transient
	public String getFullQuoteRegime() {
		return PayrollUtils.getInstance().getRegimeCode(this.getEnterpriseCCC()) + this.getEnterpriseCCC().getCcc();
	}
	
	@Transient
	@Deprecated
	public IContractSalaryCalculatorContext getSalaryCalculatorContext(int year, Month month, SalaryType salaryType) throws SalaryException {
		ContractSalaryCalculatorContext ctx = new ContractSalaryCalculatorContext(this,year, month,salaryType);
		return ctx;
	}
	
	@Transient
	public IContractSalaryCalculatorContext getSalaryCalculatorContext(Date startdate, Date endDate , SalaryType salaryType) throws SalaryException {
		ContractSalaryCalculatorContext ctx = new ContractSalaryCalculatorContext(this,startdate, endDate,salaryType);
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
	public String getCodInt() throws ManagerBeanException{
		IManagerBean bean = BeanManager
				.getManagerBean(ContractData.class);
		Criteria c = new Criteria();
		c.addEqualExpression(
				bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID),
				this.getId());
		c.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), ContractData.COD_INT);
		// TODO : Dates ????
		List<?> list = bean.getList(c);
		if ( list == null || list.size() == 0)
			return null;
		return ((ContractData) list.get(0)).getExpression();
	}

	@Transient 
	public Date getAdvanceNoticeDate() throws ManagerBeanException{
		// TODO : not valid at now (advance notice days are needed)
//		IManagerBean bean = BeanManager
//				.getManagerBean(ContractData.class);
//		Criteria c = new Criteria();
//		c.addEqualExpression(
//				bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID),
//				this.getId());
//		c.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), ContextVariable.ADVANCE_NOTICE_DAYS.getName());
//		List<?> list = bean.getList(c);
//		if ( list == null || list.size() == 0)
//			return null;
//		try {
//			String days = ((ContractData) list.get(0)).getExpression();
//			if(StringUtils.isNotBlank(days)){
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(this.getEndDate());
//				cal.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(days));
//				return cal.getTime();
//			}
//		} catch(Exception e){
//		}
		return null;
	}

}
