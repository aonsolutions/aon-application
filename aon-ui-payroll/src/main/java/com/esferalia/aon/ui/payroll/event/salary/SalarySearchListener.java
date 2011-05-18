package com.esferalia.aon.ui.payroll.event.salary;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class SalarySearchListener extends ControllerSearchListener {

	private Person person;
	
	private Enterprise enterprise;
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	@Override
	protected void init() throws ManagerBeanException {
		IManagerBean personBean = BeanManager.getManagerBean(Person.class);
		setPerson((Person) personBean.createNewTo());
		IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
		setEnterprise((Enterprise) enterpriseBean.createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ((getPerson() != null) && (getPerson().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IPayrollAlias.SALARY_CONTRACT_PERSON_ID), getPerson().getId());			
		}
		if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IPayrollAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
		}
	}

}