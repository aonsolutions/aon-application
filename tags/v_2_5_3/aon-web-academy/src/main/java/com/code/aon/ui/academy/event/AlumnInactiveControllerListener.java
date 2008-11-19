package com.code.aon.ui.academy.event;

import java.util.Iterator;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlumnInactiveControllerListener extends ControllerAdapter {

	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Customer customer = (Customer)event.getController().getTo();
		if(customer.getStatus().equals(CustomerStatus.INACTIVE)){
			try {
				IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), customer.getId());
				Iterator iter = courseAlumnBean.getList(criteria).iterator();
				while(iter.hasNext()){
					CourseAlumn courseAlumn = (CourseAlumn)iter.next();
					courseAlumn.setStatus(CourseAlumnStatus.INACTIVE);
					courseAlumnBean.update(courseAlumn);
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException("Error updating courseAlumn info for customer with id:" + customer.getId(),e);
			}
		}
	}
}