/**
 * 
 */
package com.code.aon.web.employee.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;
import com.code.aon.company.resources.ResourceManager;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.ql.Criteria;
import com.code.aon.record.Position;
import com.code.aon.record.dao.IRecordAlias;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.util.AonUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 27/08/2007
 *
 */
public class Transfer {

	/** Tells if following events must be kept inside the new Calendar. */
	private boolean keepAppointments;
	/** Position description. */
	private String description;
	
	/**
	 * @return the keepAppointments
	 */
	public boolean isKeepAppointments() {
		return keepAppointments;
	}
	/**
	 * @param keepAppointments the keepAppointments to set
	 */
	public void setKeepAppointments(boolean keepAppointments) {
		this.keepAppointments = keepAppointments;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Transfers an employee from one place to another.
	 * 
	 * @param event
	 * @throws DAOException
	 * @throws ManagerBeanException
	 * @throws CalendarException
	 */
	@SuppressWarnings("unchecked")
	public void accept(ActionEvent event) throws DAOException, ManagerBeanException, CalendarException {
		EmployeeController ec = 
			(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
		Employee employee = (Employee) ec.getTo();
		IManagerBean bean = AonUtil.getManagerBean( Position.class );
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( bean.getFieldName( IRecordAlias.POSITION_EMPLOYEE_ID ), employee.getId() );
		criteria.addNullExpression( bean.getFieldName( IRecordAlias.POSITION_ENDING_DATE ) );
		List l = bean.getList( criteria );
//		HibernateUtil.setBeginTransaction( false );
//		HibernateUtil.setCloseSession( false );
		try {
//			HibernateUtil.beginTransaction();
			Integer oldCalendarId = employee.getCalendar();
//	Finishes employee position.
			if ( l.size() > 0 ) {
				Position position = (Position) l.get( 0 );
				position.setEndingDate( new Date() );
				position.setCalendar( oldCalendarId );
				bean.update( position );
			} else {
				Resource oldResource = ResourceManager.getResourceManager().getResource( employee );
				Position position = new Position();
				position.setEmployee( employee );
				position.setStartingDate( oldResource.getStartingDate() );
				position.setEndingDate( new Date() );
				position.setWorkPlace( oldResource.getWorkPlace() );
				position.setWorkActivity( oldResource.getWorkActivity() );
				position.setCalendar( oldCalendarId );
				bean.insert( position );
			}
//	Updates employee calendar.
			AonCalendar old = CalendarHelper.getCalendar( oldCalendarId );
			employee.setCalendar( null );
			ec.accept( event );
			AonCalendar aonCalendar = ec.getCalendarScheduleModel().getCalendar();
			if ( isKeepAppointments() ) {
				EventCategory[] categories = new EventCategory[EventCategory.values().length];
				categories[EventCategory.APPOINTMENT.ordinal()] = EventCategory.APPOINTMENT;
				categories[EventCategory.INCIDENCE.ordinal()] = EventCategory.INCIDENCE;
				ComponentList cl = old.getVEvents( categories );
				for (Iterator iter = cl.iterator(); iter.hasNext();) {
					VEvent vevent = (VEvent) iter.next();
					aonCalendar.getCalendar().getComponents().add( vevent );
				}
				ControllerUtil.getCalendarManagerBean().updateCalendar(aonCalendar);
			}
//	Adds a new employee position.
			Position position = new Position();
			position.setEmployee( employee );
			position.setDescription( this.description );
			position.setStartingDate( new Date() );
			position.setWorkPlace( ec.getResource().getWorkPlace() );
			position.setWorkActivity( ec.getResource().getWorkActivity() );
			bean.insert( position );
////	Commit Transaction.
//			HibernateUtil.commitTransaction();
//		} catch (DAOException e) {
//			HibernateUtil.rollbackTransaction();
		} catch (ManagerBeanException e) {
			HibernateUtil.rollbackTransaction();
			throw e;
		} catch (CalendarException e) {
			HibernateUtil.rollbackTransaction();
			throw e;
//		} finally {
//			HibernateUtil.setBeginTransaction( true );
//			HibernateUtil.setCloseSession( true );
		}
	}
}
