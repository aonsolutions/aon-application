package com.esferalia.aon.ui.payroll.event.enterprise;

import java.util.Iterator;
import java.util.Set;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseActivity;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class EnterpriseCCCSearchListener extends ControllerSearchListener {

	private Customer customer;
	private Enterprise enterprise;
	
	private short year;
	private Month startMonth;
	private Month endMonth;
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	public short getYear() {
		return year;
	}
	public void setYear(short year) {
		this.year = year;
	}
	public Month getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(Month startMonth) {
		this.startMonth = startMonth;
	}
	public Month getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(Month endMonth) {
		this.endMonth = endMonth;
	}
	
	
	@Override
	protected void init() throws ManagerBeanException {
		// TODO Auto-generated method stub
		super.init();
	}
	
	@Override
	protected void completeCriteria(Criteria criteria)
			throws ManagerBeanException, ExpressionException {
		if ((enterprise != null) && (enterprise.getId() != null)) {
		}
		super.completeCriteria(criteria);
	}

}
