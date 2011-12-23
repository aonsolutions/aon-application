package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class ContractSearchListener extends ControllerSearchListener {

	private Person person;
	private Enterprise enterprise;
	private Date endDate; 
	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

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
			criteria.addEqualExpression(getFieldName(IEntityAlias.CONTRACT_PERSON_ID), getPerson().getId());			
		}
		if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
		}
//		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE), new Date());
//		Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.CONTRACT_END_DATE));
//		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
	}

}