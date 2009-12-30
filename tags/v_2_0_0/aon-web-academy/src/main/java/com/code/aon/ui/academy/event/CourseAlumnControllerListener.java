package com.code.aon.ui.academy.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseAlumnControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(CourseAlumnControllerListener.class.getName());

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseAlumn courseAlumn = (CourseAlumn)event.getController().getTo();
		if(existingAlumn(courseAlumn)){
			AonUtil.addErrorMessage("No se puede añadir el mismo alumno dos veces en un curso");
			throw new AbortProcessingException();
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CourseAlumn courseAlumn = (CourseAlumn)event.getController().getTo();
		if(existingAlumn(courseAlumn)){
			AonUtil.addErrorMessage("No se puede añadir el mismo alumno dos veces en un curso");
			throw new AbortProcessingException();
		}
	}

	@SuppressWarnings("unchecked")
	private boolean existingAlumn(CourseAlumn courseAlumn) {
		try {
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), courseAlumn.getCourse().getId());
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), courseAlumn.getCustomer().getId());
			if(courseAlumnBean.getCount(criteria) > 0){
				return true;
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error checking if courseAlumn exists", e);
		}
		return false;
	}
}