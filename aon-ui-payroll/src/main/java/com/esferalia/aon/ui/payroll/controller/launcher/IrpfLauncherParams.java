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

public class IrpfLauncherParams {

	private static final String ENTERPRISE_ALIAS = "enterprise_registry.id";
	private static final String PERSON_ALIAS = "person_registry.id";
	final static String ENTERPRISE_REGISTRY_COLUMN_NAME = "enterprise.registry";
	final static String PERSON_REGISTRY_COLUMN_NAME = "person.registry";

	private Enterprise enterprise;
	private Person person;
	private Integer year;
	private Month month;
	
	public IrpfLauncherParams() {
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
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}
	
	public Date getDate() {
		return CommonUtil.getDate(getYear(), getMonth().ordinal(), 1);
	}

	public void initialize() {
		try {
			IManagerBean personBean = BeanManager.getManagerBean(Person.class);
			setPerson((Person) personBean.createNewTo());
			IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
			setEnterprise((Enterprise) enterpriseBean.createNewTo());
		} catch (ManagerBeanException e) {
			String msg = "Error de inicializazión";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setYear(CommonUtil.getYear(new Date()));
		setMonth(Month.getMonthByValue(CommonUtil.getMonth(new Date())));
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
