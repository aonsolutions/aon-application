package com.code.aon.ui.academy.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class CourseSearchListener extends ControllerSearchListenerEx {

	private CourseStatus[] courseStatuses;
	
	public CourseStatus[] getCourseStatuses() {
		return courseStatuses;
	}

	public void setCourseStatuses(CourseStatus[] courseStatuses) {
		this.courseStatuses = courseStatuses;
	}

	@Override
	protected void init() throws ManagerBeanException {
		CourseStatus[] defaultSalesStatus = {CourseStatus.ACTIVE};
		setCourseStatuses(defaultSalesStatus);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getCourseStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.COURSE_STATUS);
			addEnumToCriteria(criteria, status, getCourseStatuses());
		}
	}	
}