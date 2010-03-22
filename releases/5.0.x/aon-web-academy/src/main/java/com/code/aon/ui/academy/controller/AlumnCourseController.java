package com.code.aon.ui.academy.controller;

import javax.faces.model.DataModel;

import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.LinesController;

public class AlumnCourseController extends LinesController {

	private boolean active;
	private boolean inactive;

	@Override
	public void initModel() {
		active = false;
		inactive = false;
		super.initModel();
	}

	public DataModel getActiveCourseModel() throws ManagerBeanException {
		if (!active) {
			setActiveMode(true);
			setModel(null);
			clearCriteria();
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), ((Customer)getMasterController().getTo()).getId());
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseStatus.ACTIVE);
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_STATUS), CourseAlumnStatus.ACTIVE);
		}
		return getModel();
	}

	public DataModel getInactiveCourseModel() throws ManagerBeanException {
		if (!inactive) {
			setActiveMode(false);
			setModel(null);
			clearCriteria();
			getCriteria().addEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), ((Customer)getMasterController().getTo()).getId());
			Expression courseInactive = ExpressionUtilities.getEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseStatus.INACTIVE);
			Expression alumnInactive = ExpressionUtilities.getEqualExpression(getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_STATUS), CourseAlumnStatus.INACTIVE);
			getCriteria().addExpression(ExpressionUtilities.getOrExpression(courseInactive, alumnInactive));
		}
		return getModel();
	}

	private void setActiveMode(boolean active) {
		this.active = active;
		this.inactive = !active;
	}

}
