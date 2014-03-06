package com.code.aon.ui.academy.controller;

import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_CONTROLLER_NAME;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CourseAlumnController extends LinesController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseAlumnController.class);
	
	private IControllerListener customerFilter;
	
	public boolean isLimitReached() throws ManagerBeanException {
		IController courseController = FormUtil.getController(COURSE_CONTROLLER_NAME);
		Course course = (Course)courseController.getTo();
		if ( (course != null) && (course.getId() != null) ) {
			return isLimitReached(course);
		}
		return false;
	}

	public boolean isLimitReached( Course course ) {
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), course.getId());
			criteria.addEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			return getManagerBean().getCount(criteria) >= course.getAlumnLimit();
		} catch (ManagerBeanException e) {
			LOGGER.error("Error checking if course alumn limit", e);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getCustomerList() throws ManagerBeanException {
		IController controller = FormUtil.getController(COURSE_CONTROLLER_NAME);
		Serializable id = controller.getManagerBean().getId(controller.getTo());
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), id);
		String courseId = getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID);
		return getManagerBean().getList(new ProjectionList(Projection.property(courseId)), criteria);
	}
	
	public IControllerListener getCustomerFilter() {
		if ( this.customerFilter == null ) {
			this.customerFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {
						String courseId = controller.getFieldName(IEntityAlias.CUSTOMER_ID);
						for( Integer id : getCustomerList() ) {
							controller.getCriteria().addNotEqualExpression(courseId, id);	
						}
					} catch (ManagerBeanException e) {
						throw new ControllerListenerException(e);
					} 
				}		
			};
		}
		return this.customerFilter;
	}	
	
	public void onLoadAlumn(ActionEvent event) throws ManagerBeanException{
		CourseAlumn courseAlumn = (CourseAlumn) this.getModel().getRowData();
		CustomerController controller = (CustomerController)AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_CONTROLLER_NAME);
		controller.onLoad(event, courseAlumn.getCustomer().getId(), "course_form", "courseAlumn.onRefresAlumn");
	}

	public void onRefresAlumn(ActionEvent event){
		this.initializeModel();
	}
	
}