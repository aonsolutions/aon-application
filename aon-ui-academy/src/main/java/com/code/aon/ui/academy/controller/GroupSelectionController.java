package com.code.aon.ui.academy.controller;

import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_CONTROLLER_NAME;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.event.ActionEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.entity.IEntityAlias;

public class GroupSelectionController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	protected void updateCourseController( ActionEvent event ) throws ManagerBeanException {
		updateCourseController(event, getCheckList());
	}

	protected void updateCourseController( ActionEvent event, Collection<Serializable> list ) throws ManagerBeanException {
		IController controller = FormUtil.getController(COURSE_CONTROLLER_NAME);
		controller.clearCriteria();
		Criteria criteria = controller.getCriteria();
		criteria.addInExpression(controller.getFieldName(IEntityAlias.COURSE_ID), list );
		controller.onSearch(event);
	}
	
}