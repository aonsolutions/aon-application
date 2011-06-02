package com.esferalia.aon.ui.payroll.controller.salary;

import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.DismissCause;

public class SettleParams {
	
	private static int YEAR_MAX_DAYS = 365;
	
	private Contract contract;
	private Date suspensionDate;
	private Date seniorityDate;
	private DismissCause dismissCause;
	private Integer compensationDays;
	private Double compensation;
	private Double dayAmount;
	private Integer daysPerYear;
	private Integer pendingVacation;
	private Double vacationDayAmount;
	private Double vacationAmount;
	
	private Double lastBaseSalary;
	private Integer lastSalaryMonthDays;
	
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public Date getSuspensionDate() {
		return suspensionDate;
	}
	public void setSuspensionDate(Date suspensionDate) {
		this.suspensionDate = suspensionDate;
		if(getSeniorityDate()!=null && suspensionDate!=null && getDismissCause()!=null ){
			setCompensationDays( new Double((getYears()*getDaysPerYear())).intValue() );
		}
	}
	public Date getSeniorityDate() {
		return seniorityDate;
	}
	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
	}
	public DismissCause getDismissCause() {
		return dismissCause;
	}
	public void setDismissCause(DismissCause dismissCause) {
		this.dismissCause = dismissCause;
		if(dismissCause!=null){
			setDaysPerYear(dismissCause.getCompensationDaysPerYear());
		}
	}
	public Integer getCompensationDays() {
		return compensationDays;
	}
	public void setCompensationDays(Integer compensationDays) {
		this.compensationDays = compensationDays;
		if(compensationDays!=null){
			setCompensation(CommonUtil.round(compensationDays*getDayAmount()));
		}
	}
	public Double getCompensation() {
		return compensation;
	}
	public void setCompensation(Double compensation) {
		this.compensation = compensation;
	}
	public Double getDayAmount() {
		if(dayAmount==null && getLastBaseSalary()!=null){
			dayAmount = CommonUtil.round(getLastBaseSalary()/getLastSalaryMonthDays());
		}
		return dayAmount;
	}
	public void setDayAmount(Double dayAmount) {
		this.dayAmount = dayAmount;
	}
	public Integer getDaysPerYear() {
		return daysPerYear;
	}
	public void setDaysPerYear(Integer daysPerYear) {
		this.daysPerYear = daysPerYear;
		if(getSeniorityDate()!=null && getSuspensionDate()!=null && getDismissCause()!=null ){
			setCompensationDays( new Double((getYears()*getDaysPerYear())).intValue() );
		}
	}
	public Integer getPendingVacation() {
		return pendingVacation;
	}
	public void setPendingVacation(Integer pendingVacation) {
		this.pendingVacation = pendingVacation;
	}
	public Double getVacationDayAmount() {
		if(vacationDayAmount==null && getLastBaseSalary()!=null){
			vacationDayAmount = CommonUtil.round(getLastBaseSalary()/getLastSalaryMonthDays());
		}
		return vacationDayAmount;
	}
	public void setVacationDayAmount(Double vacationDayAmount) {
		this.vacationDayAmount = vacationDayAmount;
	}
	public Double getVacationAmount() {
		return vacationAmount;
	}
	public void setVacationAmount(Double vacationAmount) {
		this.vacationAmount = vacationAmount;
	}
	public Double getLastBaseSalary() {
		if(getContract()!=null){
			searchBaseSalary();
		}
		return lastBaseSalary;
	}
	public void setLastBaseSalary(Double lastBaseSalary) {
		this.lastBaseSalary = lastBaseSalary;
	}
	public Integer getLastSalaryMonthDays() {
		return lastSalaryMonthDays;
	}
	public void setLastSalaryMonthDays(Integer lastSalaryMonthDays) {
		this.lastSalaryMonthDays = lastSalaryMonthDays;
	}
	
	public Integer getDays() {
		if(getSuspensionDate()!=null && getSeniorityDate()!=null){
			return differenceBetweenDates(getSeniorityDate(), getSuspensionDate());
		}
		return null;
	}
	public Double getYears() {
		if(getDays()!=null){
			return CommonUtil.round(new Double(getDays())/YEAR_MAX_DAYS);
		}
		return null;
	}
	public Integer getLimit(){
		return getDismissCause().getMaximunMonths();
	}
	
	private Integer differenceBetweenDates(Date from, Date to) {
		Integer diffDays = new Integer(0);
		final Double MS_PER_DAY = new Double(1000 * 60 * 60 * 24);
		if(from.before(to)) {
			diffDays = (int)((Math.floor((to.getTime() - from.getTime()) / MS_PER_DAY + 0.5d) + 1));
		}
		return diffDays;
	}
	
	private void searchBaseSalary() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.SALARY_CONTRACT_ID), getContract().getId());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.SALARY_START_DATE), false);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				setLastBaseSalary(((Salary)list.get(0)).getRemuneration());
				setLastSalaryMonthDays(((Salary)list.get(0)).getTimeUnits());
			}
		} catch (ManagerBeanException e1) {
			setLastBaseSalary(null);
		}
	}

	
}
