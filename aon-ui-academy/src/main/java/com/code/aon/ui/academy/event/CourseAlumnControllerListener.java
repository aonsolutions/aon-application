package com.code.aon.ui.academy.event;

import static com.code.aon.ui.academy.controller.IAcademyConstants.BUNDLE_NAME;
import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_ALUMN_LIMIT;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CourseAlumnControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CourseAlumnControllerListener.class);
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseAlumn courseAlumn = (CourseAlumn)event.getController().getTo();
		if (existingAlumn(courseAlumn)) {
			String message = AonUtil.getMessage(BUNDLE_NAME, COURSE_ALUMN_LIMIT);
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
	}

	private boolean existingAlumn(CourseAlumn courseAlumn) {
		try {
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), courseAlumn.getCourse().getId());
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), courseAlumn.getCustomer().getId());
			return courseAlumnBean.getCount(criteria) > 0;
		} catch (ManagerBeanException e) {
			LOGGER.error("Error checking if courseAlumn exists", e);
		}
		return false;
	}

}