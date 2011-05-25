package com.esferalia.aon.ui.payroll.event.salary.draft;


import java.util.Calendar;
import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

public class SalaryDraftSearchListener extends ControllerSearchListener {

	private Person person;
	private Enterprise enterprise;
	private Date endDate; 
	private Month month;
	private int year;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}
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
		setYear(Calendar.getInstance().get(Calendar.YEAR));
		setMonth(Month.getMonthByValue(Calendar.getInstance().get(Calendar.MONTH)));
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		SalaryDraftController c =  (SalaryDraftController) getController();
		if ((getPerson() != null) && (getPerson().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IPayrollAlias.CONTRACT_PERSON_ID), getPerson().getId());			
		}
		if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IPayrollAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
		}
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.YEAR, getYear());
		startCal.set(Calendar.MONTH, getMonth().getValue());
		startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
		Calendar endCal = Calendar.getInstance();
		endCal.set(Calendar.YEAR, getYear());
		endCal.set(Calendar.MONTH, getMonth().getValue());
		endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		criteria.addLessThanOrEqualExpression(getFieldName(IPayrollAlias.CONTRACT_START_DATE), startCal.getTime());
		Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IPayrollAlias.CONTRACT_END_DATE), endCal.getTime());
		Expression expr2 = ExpressionUtilities.getNullExpression(getFieldName(IPayrollAlias.CONTRACT_END_DATE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		if ( criteria.getExpression() == null ) {
			throw new ManagerBeanException("Debe indicar algún criterio de búsqueda");
		}
		c.setIssueDate(endCal.getTime());
	}

}