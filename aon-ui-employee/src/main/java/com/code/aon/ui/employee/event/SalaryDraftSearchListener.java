package com.code.aon.ui.employee.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.employee.controller.SalaryDraftController;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class SalaryDraftSearchListener extends ControllerSearchListener {

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
		setPerson(new Person());
		setEnterprise(new Enterprise());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		SalaryDraftController c =  (SalaryDraftController) getController();
		if ((getPerson() != null) && (getPerson().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEmployeeAlias.CONTRACT_PERSON_ID), getPerson().getId());			
		}
		if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEmployeeAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
		}
		if ( criteria.getExpression() == null ) {
			throw new ManagerBeanException("Debe indicar algún criterio de búsqueda");
		}
		criteria.addLessThanOrEqualExpression(getFieldName(IEmployeeAlias.CONTRACT_START_DATE), c.getEndDate());
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEmployeeAlias.CONTRACT_END_DATE), c.getStartDate());
		Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IEmployeeAlias.CONTRACT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
	}

}