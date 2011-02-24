package com.esferalia.aon.ui.payroll.controller.launcher;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;


public class SalaryLauncherParams {

	private Enterprise enterprise;
	private Person person;
	private Month issueMonth;
	private int issueYear;
	

	public SalaryLauncherParams() {
		initialize();
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
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
	}
	public int getIssueYear() {
		return issueYear;
	}
	public void setIssueYear(int issueYear) {
		this.issueYear = issueYear;
	}
	
	public Date getStartDate() {
		return CommonUtil.getDate(getIssueYear(), getIssueMonth().getValue(), 1);
	}
	public Date getEndDate() {
		return CommonUtil.getMonthLastDay( CommonUtil.getDate(getIssueYear(), getIssueMonth().getValue(), 1));
	}
	
	public void initialize() {
		setPerson(new Person());
		setEnterprise(new Enterprise());
		Date date = new Date();
		setIssueMonth(Month.getMonthByValue( CommonUtil.getMonth(date)));
		setIssueYear( CommonUtil.getYear(date) );
	}
}
