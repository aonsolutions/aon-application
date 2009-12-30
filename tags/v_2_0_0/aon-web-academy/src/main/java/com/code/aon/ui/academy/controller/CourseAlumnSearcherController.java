package com.code.aon.ui.academy.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;


public class CourseAlumnSearcherController {

	/**
	 * @param event contains the customer document
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */
	public void addCourseExpression(ValueChangeEvent event)
		throws ManagerBeanException, ExpressionException {
	    if ((event.getNewValue() != null)
	    		&& (!"".equals(event.getNewValue().toString().trim()))
	    		) {
			Criteria criteria_course = new Criteria();
			IManagerBean bean_course = BeanManager.getManagerBean(Course.class);
			String identifier_course = bean_course.getFieldName(IAcademyAlias.COURSE_CODE);
			criteria_course.addEqualExpression(identifier_course, event.getNewValue());
			List list_course = bean_course.getList(criteria_course);
			Iterator iter_course = list_course.iterator();
			
			BasicController controller = getAlumnController();
	    	Criteria c = controller.getCriteria();
	    	
	    	if (iter_course.hasNext()){
				Course course = (Course)iter_course.next();
	    		
				Criteria criteria = new Criteria();
				IManagerBean bean = BeanManager.getManagerBean(CourseAlumn.class);
				String identifier = bean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID);
				criteria.addEqualExpression(identifier, course.getId());
				List list = bean.getList(criteria);
				Iterator iter = list.iterator();
		    	if (iter.hasNext()){
		    		Expression expression_or = null;
					while (iter.hasNext()){
						CourseAlumn courseAlumn = (CourseAlumn)iter.next();
						Object value = courseAlumn.getCustomer().getId(); 
						Expression expression_equal = ExpressionUtilities.getEqualExpression(controller.getFieldName(ICustomerAlias.CUSTOMER_ID), value);
						if (expression_or!=null){
							expression_or = ExpressionUtilities.getOrExpression(expression_or,expression_equal);
						}else{
							expression_or = expression_equal;
						}
					}
					c.addExpression(expression_or);
					controller.setCriteria(c);
		    	}else{
					Object value = new Integer(-1); 
					c.addEqualExpression(controller.getFieldName(ICustomerAlias.CUSTOMER_ID), value);
					controller.setCriteria(c);
		    	}
	    	}else{
				Object value = new Integer(-1); 
				c.addEqualExpression(controller.getFieldName(ICustomerAlias.CUSTOMER_ID), value);
				controller.setCriteria(c);
	    	}
		}
	}

	private BasicController getAlumnController() throws ManagerBeanException{
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{customer}");
        BasicController controller = (BasicController)vb.getValue(ctx);
        return controller;
	}
	
}
