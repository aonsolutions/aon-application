package com.code.aon.ui.employee.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.employee.ContractType;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class ContractSearchListener extends ControllerSearchListener {

	private Person person;
	
	private Enterprise enterprise;
	
	private ContractType contractType; 
	
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

	public ContractType getContractType() {
		return contractType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setPerson(new Person());
		setEnterprise(new Enterprise());
		setContractType(new ContractType());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ((getPerson() != null) && (getPerson().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEmployeeAlias.CONTRACT_PERSON_ID), getPerson().getId());			
		}
		if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
		}
		if ((getContractType() != null) && (getContractType().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEmployeeAlias.CONTRACT_CONTRACT_TYPE_ID), getContractType().getId());			
		}
	}

}