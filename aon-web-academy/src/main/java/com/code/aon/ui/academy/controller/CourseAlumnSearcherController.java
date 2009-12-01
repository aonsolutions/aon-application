package com.code.aon.ui.academy.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;


public class CourseAlumnSearcherController {

	/** The table. */
	private String alumnController;

	/**
	 * Gets the table.
	 * 
	 * @return the table
	 */
	public String getAlumnController() {
		return alumnController;
	}

	/**
	 * Sets the table.
	 * 
	 * @param table the table
	 */
	public void setAlumnController(String alumnController) {
		this.alumnController = alumnController;
	}

	/**
	 * @param event contains the customer document
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */
	@SuppressWarnings("unchecked")
	public void addCourseExpression(ValueChangeEvent event)	throws ManagerBeanException, ExpressionException {
		IController controller = (IController)AonUtil.getController(getAlumnController());
		if ((event.getNewValue() != null) && (!"".equals(event.getNewValue().toString().trim()))) {
    		Expression courseExpression = null;

    		Criteria courseCriteria = new Criteria();
			IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
			courseCriteria.addExpression(courseBean.getFieldName(IAcademyAlias.COURSE_CODE), event.getNewValue().toString());
			courseCriteria.addEqualExpression(courseBean.getFieldName(IAcademyAlias.COURSE_STATUS), CourseStatus.ACTIVE);
			Iterator courseIter = courseBean.getList(courseCriteria).iterator();
	    	boolean notFound = true;
	    	while (courseIter.hasNext()){
	    		Course course = (Course)courseIter.next();
	    		
				Criteria courseAlumnCriteria = new Criteria();
				IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
				courseAlumnCriteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
				List courseAlumnList = courseAlumnBean.getList(courseAlumnCriteria);
		    	if (courseAlumnList.size() > 0){
		    		notFound = false;

					Iterator courseAlumnIter = courseAlumnList.iterator();
		    		while (courseAlumnIter.hasNext()) {
						CourseAlumn courseAlumn = (CourseAlumn)courseAlumnIter.next();
						Object value = courseAlumn.getCustomer().getId(); 
						Expression expression = ExpressionUtilities.getEqualExpression(controller.getFieldName(ICustomerAlias.CUSTOMER_ID), value);
						courseExpression = (courseExpression != null)?ExpressionUtilities.getOrExpression(courseExpression, expression):expression;
					}
		    	}
	    	}

	    	if (notFound) {
	    		controller.getCriteria().addEqualExpression(controller.getFieldName(ICustomerAlias.CUSTOMER_ID), new Integer(-1));
	    	} else {
		    	controller.getCriteria().addExpression(courseExpression);
	    	}
		}
	}

}
