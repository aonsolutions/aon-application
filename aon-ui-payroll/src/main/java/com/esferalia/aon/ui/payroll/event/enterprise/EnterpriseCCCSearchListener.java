package com.esferalia.aon.ui.payroll.event.enterprise;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.Enterprise;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class EnterpriseCCCSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
		IManagerBean cBean = BeanManager.getManagerBean(Customer.class);
		setCustomer((Customer) cBean.createNewTo());
		IManagerBean eBean = BeanManager.getManagerBean(Enterprise.class);
		setEnterprise((Enterprise) eBean.createNewTo());
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
