package com.code.aon.ui.employee.event;

import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;
import com.code.aon.company.resources.ResourceManager;
import com.code.aon.planner.core.Calendar;
import com.code.aon.ui.company.controller.CompanyUtil;
import com.code.aon.ui.employee.controller.EmployeeController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ResourceChangedListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = (EmployeeController) event.getController();
		try {
	    	HibernateUtil.setCloseSession( false );
	    	Resource r = ResourceManager.getResourceManager().createResource( ec.getEmployee() );
			ec.setResource( r );
			ec.setResourceDirty( true );
			ec.setWorkActivities( CompanyUtil.getSelectItemActivities( r.getWorkPlace() ) );
			ec.setActivityId( -1 );
		} catch (ManagerBeanException e) {
			 throw new ControllerListenerException( e.getMessage(), e );
		} finally {
	    	HibernateUtil.setCloseSession( true );
		}
	}

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = (EmployeeController) event.getController();
		try {
	    	HibernateUtil.setCloseSession( false );
	    	Resource r = ResourceManager.getResourceManager().createResource( ec.getEmployee() );
			ec.setResource( r );
			ec.setResourceDirty( true );
			ec.setWorkActivities( CompanyUtil.getSelectItemActivities( r.getWorkPlace() ) );
			ec.setActivityId( -1 );
		} catch (ManagerBeanException e) {
			 throw new ControllerListenerException( e.getMessage(), e );
		} finally {
	    	HibernateUtil.setCloseSession( true );
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = (EmployeeController) event.getController();
		try {
				ResourceManager.getResourceManager().addResource( ec.getResource() );
		} catch (ManagerBeanException e) {
			 throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = (EmployeeController) event.getController();
		try {
//TODO	Remove employee calendar if exists.
			Integer calendarId = ec.getEmployee().getCalendar();
			if ( calendarId != null ) {
				IManagerBean bean = BeanManager.getManagerBean( Calendar.class );
				Calendar to = new Calendar();
				to.setId( calendarId );
				bean.remove(to);
			}
			Resource resource = ec.getResource();
			resource.setEndingDate( new Date() );
			ResourceManager.getResourceManager().updateResource( resource );
		} catch (ManagerBeanException e) {
			 throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	/* 
 	 * Find a resource bound to selected Employee. Firstly finds the resource in working activity,
 	 * if it does not find, looks for it in the working place, otherwise initializes the resource
 	 * as a working place one.  
 	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = (EmployeeController) event.getController();
		try {
			Resource r = ResourceManager.getResourceManager().getResource( ec.getEmployee() );
			ec.setResource( r );
			ec.setResourceDirty( false );
			ec.setWorkActivities( CompanyUtil.findActivities( r.getWorkPlace().getId() ) );
			ec.setActivityId( (r.getWorkActivity() == null)? -1: r.getWorkActivity().getId() );
		} catch (ManagerBeanException e) {
			 throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		EmployeeController ec = (EmployeeController) event.getController();
		Employee employee = ec.getEmployee();
		if ( ec.isResourceDirty() ) {
			try {
				Resource r = ResourceManager.getResourceManager().getResource( employee );
				r.setEndingDate( new Date() );
				ResourceManager.getResourceManager().updateResource( r );
		    	r = ResourceManager.getResourceManager().createResource( employee );
		    	r.setWorkPlace( ec.getResource().getWorkPlace() );
		    	r.setWorkActivity( ec.getResource().getWorkActivity() );
		    	r.setStartingDate( new Date() );
		    	ec.setResource( r );
				ResourceManager.getResourceManager().addResource( r );
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException( e.getMessage(), e );
			}
		}
	}

}