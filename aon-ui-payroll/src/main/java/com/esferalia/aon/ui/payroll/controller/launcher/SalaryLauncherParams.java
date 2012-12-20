package com.esferalia.aon.ui.payroll.controller.launcher;

import java.util.Date;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class SalaryLauncherParams {

	private static final String ENTERPRISE_ALIAS = "enterprise_registry.id";
	private static final String PERSON_ALIAS = "person_registry.id";

//	private Enterprise enterprise;
	private Person person;
	private Month issueMonth;
	private int issueYear;
	private SalaryType salaryType;
	private Date startDate;
	private Date endDate;

	public SalaryLauncherParams() {
		initialize();
	}

//	public Enterprise getEnterprise() {
//		return enterprise;
//	}
//
//	public void setEnterprise(Enterprise enterprise) {
//		this.enterprise = enterprise;
//	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Month getIssueMonth() {
		return issueMonth;
	}

	public void setIssueMonth(Month issueMonth) {
		this.issueMonth = issueMonth;
		calculatePeriod();
	}

	public int getIssueYear() {
		return issueYear;
	}

	public void setIssueYear(int issueYear) {
		this.issueYear = issueYear;
		calculatePeriod();
	}
	
	public SalaryType getSalaryType() {
		return salaryType;
	}

	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public boolean isStartDateDisabled(){
		if(getSalaryType()==SalaryType.SALARY){
			return true;
		} else if(getSalaryType()==SalaryType.EXTRA){
			return true;
		} else if(getSalaryType()==SalaryType.DELAY){
			return false;
		} else if(getSalaryType()==SalaryType.SETTLE){
			return true;
		}
		return true;
	}
	public boolean isEndDateDisabled(){
		if(getSalaryType()==SalaryType.SALARY){
			return true;
		} else if(getSalaryType()==SalaryType.EXTRA){
			return true;
		} else if(getSalaryType()==SalaryType.DELAY){
			return true;
		} else if(getSalaryType()==SalaryType.SETTLE){
			return false;
		}
		return true;
	}
	
	public boolean isMonthDisabled(){
		if(getSalaryType()==SalaryType.SALARY){
			return false;
		} else if(getSalaryType()==SalaryType.EXTRA){
			return false;
		} else if(getSalaryType()==SalaryType.DELAY){
			return true;
		} else if(getSalaryType()==SalaryType.SETTLE){
			return true;
		}
		return true;
	}

	public boolean isYearDisabled(){
		return isMonthDisabled();
	}

	private void calculatePeriod() {
		setStartDate(CommonUtil.getDate(getIssueYear(), getIssueMonth().getValue(), 1));
		setEndDate(CommonUtil.getMonthLastDay(CommonUtil.getDate(getIssueYear(), getIssueMonth().getValue(), 1)));
	}

	public void initialize() {
		try {
			IManagerBean personBean = BeanManager.getManagerBean(Person.class);
			setPerson((Person) personBean.createNewTo());
			IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
//			setEnterprise((Enterprise) enterpriseBean.createNewTo());
		} catch (ManagerBeanException e) {
			String msg = "Error de inicializazión";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		Date date = new Date();
		setIssueMonth(Month.getMonthByValue(CommonUtil.getMonth(date)));
		setIssueYear(CommonUtil.getYear(date));
		setSalaryType(SalaryType.SALARY);
		calculatePeriod();
	}

	public Criteria getCriteria() {
		Criteria criteria = new Criteria();
//		if (getEnterprise() != null && getEnterprise().getId() != null) {
//			criteria = new Criteria();
//			criteria.addEqualExpression(ENTERPRISE_ALIAS, getEnterprise().getId());
//		}
		if (getPerson() != null && getPerson().getId() != null) {
			criteria = criteria == null ? new Criteria() : criteria;
			criteria.addEqualExpression(PERSON_ALIAS, getPerson().getId());
		}
		return criteria;
	}
}
