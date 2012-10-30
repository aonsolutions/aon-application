package com.code.aon.ui.academy.controller;

import java.sql.Connection;

import javax.faces.event.ActionEvent;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.CourseInstructor;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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

	private void ensureInstructor( TaskHolder taskHolder ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		Connection connection = HibernateUtil.getSQLConnection(sessionFactoryName);
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object> h = new ScalarHandler();
			Long count  = (Long) run.query( connection,
				    "SELECT count(registry) FROM instructor WHERE registry =?", h, taskHolder.getId());
			if ( count == 0 ) {
				LOGGER.error( "Creating instructor for task holder {}", taskHolder.getId() );
				 int inserts = run.update( connection,
						 "INSERT INTO instructor VALUES (?,null,0,1)", taskHolder.getId() );
				 if ( inserts == 0 ) {
					 LOGGER.error( "Error creating instructor for task holder {}", taskHolder.getId() );
				 }
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}			
	}
	
	@Override
	public void accept(ActionEvent event) {
		CourseInstructor ci = (CourseInstructor) getTo();
		ensureInstructor(taskHolder);
		ci.setEmployee(getTaskHolder().getId());
		super.accept(event);
	}	
	
}