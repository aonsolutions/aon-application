package com.code.aon.ui.company.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.resources.Employee;
import com.code.aon.ui.company.controller.EmployeeController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.event.RegistryFormListener;
import com.code.aon.ui.util.AonUtil;

public class EmployeeeFormListener extends RegistryFormListener {
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanCreated(event);
		EmployeeController controller = (EmployeeController) AonUtil.getRegisteredBean(ICompanyConstants.EMPLOYEE_CONTROLLER_NAME);
		controller.setWorkActivity( new WorkActivity() );
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanSelected(event);
		EmployeeController controller = (EmployeeController) AonUtil.getRegisteredBean(ICompanyConstants.EMPLOYEE_CONTROLLER_NAME);
		Employee employee = (Employee) controller.getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(WorkActivity.class);
			Integer id = employee.getCalendar();
			WorkActivity wa = null;
			if ( id != null ) {
				wa = (WorkActivity) bean.get(id);
			} else {
				wa = new WorkActivity();
			}
			controller.setWorkActivity(wa);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		super.beforeBeanAdded(event);
		updateWorkActivity(event);		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		super.beforeBeanUpdated(event);
		updateWorkActivity(event);
	}
	
	private void updateWorkActivity( ControllerEvent event ) {
		Employee employee = (Employee) event.getController().getTo();
		EmployeeController controller = (EmployeeController) AonUtil.getRegisteredBean(ICompanyConstants.EMPLOYEE_CONTROLLER_NAME);
		employee.setCalendar( controller.getWorkActivity().getId() );
		ensureCalendarId( employee.getCalendar() );
	}
	
	private void ensureCalendarId( Integer id ) {
		String name = HibernateUtil.getSessionFactoryName();
		SessionFactory sf = HibernateUtil.getSessionFactory(name);
		Session session = sf.openSession();
		session.beginTransaction();
		Connection connection = session.connection();
		try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO calendar (id) VALUES (?)");
			stmt.setInt(1, id);
			stmt.execute();
			session.getTransaction().commit();
		} catch (SQLException e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			if ( connection != null ) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			session.close();	
		}
	}

}