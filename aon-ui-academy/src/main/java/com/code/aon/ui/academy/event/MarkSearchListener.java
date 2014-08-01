package com.code.aon.ui.academy.event;

import com.code.aon.academy.Course;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class MarkSearchListener extends ControllerSearchListenerEx {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Customer customer;
	
	private Course course;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setCourse((Course)BeanManager.getManagerBean(Course.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if ( (getCustomer() != null) && (getCustomer().getId() != null) ) {
			String field = getController().getFieldName(IEntityAlias.MARK_ALUMN_CUSTOMER_ID);
			criteria.addEqualExpression(field, getCustomer().getId());			
		}
		if ( (getCourse() != null) && (getCourse().getId() != null) ) {
			String field = getController().getFieldName(IEntityAlias.MARK_ALUMN_COURSE_ID);
			criteria.addEqualExpression(field, getCourse().getId());			
		}
	}

}