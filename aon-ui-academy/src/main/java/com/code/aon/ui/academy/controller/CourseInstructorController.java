package com.code.aon.ui.academy.controller;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.CourseInstructor;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.ui.form.LinesController;

public class CourseInstructorController extends LinesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CourseInstructorController.class);
	
	private TaskHolder taskHolder;
	
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}
	
	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	private TaskHolder getTaskHolder( Integer id ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(TaskHolder.class);
			return (TaskHolder) bean.get(id);
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e );
		}
		return null;
	}

	private TaskHolder getCurrentTaskHolder() {
		CourseInstructor ci = (CourseInstructor) getTo();
		return getTaskHolder(ci.getEmployee());
	}
	
	public String getInstructorName() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			CourseInstructor ci = (CourseInstructor) getModel().getRowData();
			TaskHolder th = getTaskHolder(ci.getEmployee());
			return th.getRegistry().getName();
		}
		return null;
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		setTaskHolder( getCurrentTaskHolder() );
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		try {
			setTaskHolder( (TaskHolder) BeanManager.getManagerBean(TaskHolder.class).createNewTo() );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e );
		}
	}

	@Override
	public void accept(ActionEvent event) {
		CourseInstructor ci = (CourseInstructor) getTo();
		ci.setEmployee(getTaskHolder().getId());
		super.accept(event);
	}	
	
}