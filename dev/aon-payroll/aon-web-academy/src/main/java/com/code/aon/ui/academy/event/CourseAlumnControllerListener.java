package com.code.aon.ui.academy.event;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.controller.AlumnCourseController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseAlumnControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(CourseAlumnControllerListener.class.getName());

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CourseAlumn courseAlumn = (CourseAlumn)event.getController().getTo();
		courseAlumn.setStatus(CourseAlumnStatus.ACTIVE);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseAlumn courseAlumn = (CourseAlumn)event.getController().getTo();
		if(existingAlumn(courseAlumn)){
			AonUtil.addErrorMessage("No se puede añadir el mismo alumno dos veces en un curso");
			throw new AbortProcessingException();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		if (event.getController() instanceof AlumnCourseController) {
			CourseAlumn courseAlumn = (CourseAlumn)event.getController().getTo();
			try {
				IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), courseAlumn.getCourse().getId());
				criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
				List courseAlumnList = courseAlumnBean.getList(criteria);
				Iterator iterator = courseAlumnList.iterator();
				if (iterator.hasNext()) {
					CourseAlumn to = (CourseAlumn)iterator.next();
					if (courseAlumnList.size() > to.getCourse().getAlumnLimit()) {
						AonUtil.addErrorMessage("Se ha excedido el límite de Alumnos para el Grupo " + to.getCourse().getDescription());
					}
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error checking course alumn limit", e);
			}
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CourseAlumn courseAlumn = (CourseAlumn)event.getController().getTo();
		if(!alumnChanged(courseAlumn) && existingAlumn(courseAlumn)){
			AonUtil.addErrorMessage("No se puede añadir el mismo alumno dos veces en un curso");
			throw new AbortProcessingException();
		}
	}

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_END_DATE), false);
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_CODE));
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_SURNAME));
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
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
	
	@SuppressWarnings("unchecked")
	private boolean alumnChanged(CourseAlumn courseAlumn) {
		try {
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_ID), courseAlumn.getId());
			Iterator iter = courseAlumnBean.getList(criteria).iterator();
			if(iter.hasNext()){
				CourseAlumn dbCourseAlumn = (CourseAlumn)iter.next();
				return dbCourseAlumn.getId().equals(courseAlumn.getId());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error checking if alumn has changed exists", e);
		}
		return false;
	}
}