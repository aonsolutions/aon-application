package com.code.aon.ui.academy.controller;

import javax.faces.model.DataModel;

import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.form.LinesController;

public class AlumnCourseController extends LinesController {

	private boolean active;
	private boolean all;

	@Override
	public void initModel() {
		active = false;
		all = false;
		super.initModel();
	}

	public DataModel getActiveCourseModel() throws ManagerBeanException {
		if (!active) {
			setActiveMode(true);
			setModel(null);
			clearCriteria();
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), ((Customer)getMasterController().getTo()).getId());
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_STATUS), CourseStatus.ACTIVE);
		}
		return getModel();
	}

	public DataModel getAllCourseModel() throws ManagerBeanException {
		if (!all) {
			setActiveMode(false);
			setModel(null);
			clearCriteria();
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), ((Customer)getMasterController().getTo()).getId());
		}
		return getModel();
	}

	private void setActiveMode(boolean active) {
		this.active = active;
		this.all = !active;
	}

}
