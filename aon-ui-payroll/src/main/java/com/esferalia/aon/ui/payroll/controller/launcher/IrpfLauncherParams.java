package com.esferalia.aon.ui.payroll.controller.launcher;

import java.util.Date;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
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
	private Date date;
	
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

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
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
		date = new Date();
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
