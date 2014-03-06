package com.code.aon.ui.academy.controller;

import java.io.Serializable;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.Course;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class CloseGroupController extends GroupSelectionController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(CloseGroupController.class);
	
	public void onClose(ActionEvent event){
		try {
			IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
			for( Serializable id : getCheckList() ) {
				Course course = (Course) courseBean.get(id);
				course.setStatus(CourseStatus.INACTIVE);
				courseBean.update(course);
			}
			updateCourseController(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to close the selected courses");
			LOGGER.error("Unable to close the selected courses", e);
			throw new AbortProcessingException(e);
		}
	}
	
}