package com.esferalia.aon.ui.payroll.controller.launcher;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;

public class SalaryLauncherParams {

	private static final String ENTERPRISE_ALIAS = "enterprise_registry.id";
	private static final String PERSON_ALIAS = "person_registry.id";

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
		return CommonUtil.getMonthLastDay(CommonUtil.getDate(getIssueYear(), getIssueMonth().getValue(), 1));
	}

	public void initialize() {
		setPerson(new Person());
		setEnterprise(new Enterprise());
		Date date = new Date();
		setIssueMonth(Month.getMonthByValue(CommonUtil.getMonth(date)));
		setIssueYear(CommonUtil.getYear(date));
	}

	public Criteria getCriteria() {
		Criteria criteria = new Criteria();
		if (getEnterprise() != null && getEnterprise().getId() != null) {
			criteria = new Criteria();
			criteria.addEqualExpression(ENTERPRISE_ALIAS, getEnterprise().getId());
		}
		if (getPerson() != null && getPerson().getId() != null) {
			criteria = criteria == null ? new Criteria() : criteria;
			criteria.addEqualExpression(PERSON_ALIAS, getPerson().getId());
		}
		return criteria;
	}
}
