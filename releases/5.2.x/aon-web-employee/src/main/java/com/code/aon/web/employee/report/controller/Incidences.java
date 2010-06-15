/**
 * 
 */
package com.code.aon.web.employee.report.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.calendar.CalendarUtil;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.IEntityVisitor;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.resources.Employee;
import com.code.aon.planner.IEvent;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.planner.core.IncidenceType;
import com.code.aon.planner.core.dao.IPlannerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.CompanyUtil;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.planner.util.PlannerUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 12/09/2007
 *
 */
public class Incidences implements ICollectionProvider, IEntityVisitor {

	/** Incidences report starting date */
	private Date startingDate; 
	/** Incidences report ending date */
	private Date endingDate; 
	/** Selected working place */
	private WorkPlace workPlace;
	/** Selected working activity */
	private WorkActivity workActivity;
	/** Working activities */
    private List<SelectItem> activities;
	/** Selected employee identifier */
	private Integer employeeId;
	/** Employees */
    private List<SelectItem> employees;
    /** Incidence Type to report */
    Integer incidenceType;
	/** Incidence Types */
	private List<SelectItem> incidenceTypes;
    /** Incidence Compute to report */
	private Integer incidenceCompute;
    /** Report collection */
    private List<IncidenceBean> collection;

    /**
	 * @return the endingDate
	 */
	public Date getEndingDate() {
		return endingDate;
	}

	/**
	 * @param endingDate the endingDate to set
	 */
	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
	}

	/**
	 * @return the startingDate
	 */
	public Date getStartingDate() {
		return startingDate;
	}

	/**
	 * @param startingDate the startingDate to set
	 */
	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	/**
	 * @return the workActivity
	 */
	public WorkActivity getWorkActivity() {
		return workActivity;
	}

	/**
	 * @param workActivity the workActivity to set
	 */
	public void setWorkActivity(WorkActivity workActivity) {
		this.workActivity = workActivity;
	}

	/**
	 * @return the workPlace
	 */
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	/**
	 * @param workPlace the workPlace to set
	 */
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	/**
	 * @return the activities
	 */
	public List<SelectItem> getWorkActivities() {
		return this.activities;
	}
 
	/**
	 * @param activities the activities to set
	 */
	public void setWorkActivities(List<SelectItem> activities) {
		this.activities = activities;
	}

	/**
	 * @return the employeeId
	 */
	public Integer getEmployeeId() {
		return employeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return the employees
	 */
	public List<SelectItem> getEmployees() {
		return employees;
	}

	/**
	 * @param employees the employees to set
	 */
	public void setEmployees(List<SelectItem> employees) {
		this.employees = employees;
	}

	/**
	 * @return the incidenceType
	 */
	public Integer getIncidenceType() {
		return incidenceType;
	}

	/**
	 * @param incidenceType the incidenceType to set
	 */
	public void setIncidenceType(Integer incidenceType) {
		this.incidenceType = incidenceType;
	}

	/**
	 * @return the incidenceTypes
	 */
	public List<SelectItem> getIncidenceTypes() {
		if ( this.incidenceTypes == null ) {
			this.incidenceTypes = com.code.aon.ui.planner.Utils.getIncidenceTypes();
			this.incidenceType = (Integer) ( (SelectItem) this.incidenceTypes.get(0) ).getValue();
		}
		return incidenceTypes;
	}

	/**
	 * @return the incidenceCompute
	 */
	public Integer getIncidenceCompute() {
		return incidenceCompute;
	}

	/**
	 * @param incidenceCompute the incidenceCompute to set
	 */
	public void setIncidenceCompute(Integer incidenceCompute) {
		this.incidenceCompute = incidenceCompute;
	}

	/**
	 * @return the incidence compute types.
	 */
	public List<SelectItem> getIncidenceComputeTypes() {
		return com.code.aon.ui.planner.Utils.getIncidenceComputeTypes();
	}

	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException {
		this.workPlace = (WorkPlace) BeanManager.getManagerBean( WorkPlace.class ).createNewTo();
		this.workPlace.setId( -1 );
		this.workActivity = (WorkActivity) BeanManager.getManagerBean( WorkActivity.class ).createNewTo();
		this.workActivity.setId( -1 );
		setStartingDate( new Date() );
		setEndingDate( new Date() );
		// Find working place active activities and employees, otherwise inactive.
		int active = ( this.workPlace.isActive() )? 1: -1;
		setWorkActivities( CompanyUtil.getSelectItemActivities( this.workPlace, active ) );
		setEmployeeId( -1 );
		setEmployees( CompanyUtil.getSelectItemEmployees( this.workPlace, active ) );
		setIncidenceType( -1 );
		setIncidenceCompute( 99 );
	}

	/**
	 * This methods gets call after changing starting date.
	 *  
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void startingDateChanged(ValueChangeEvent event) throws ManagerBeanException {
		this.startingDate = (Date) event.getNewValue();
	}

	/**
	 * This methods gets call after changing ending date.
	 *  
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void endingDateChanged(ValueChangeEvent event) throws ManagerBeanException {
		this.endingDate = (Date) event.getNewValue();
	}

	@SuppressWarnings("unused")
	public void workingPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( isDirtyValueChangeEvent( event ) ) {
			Integer workPlaceId = (Integer) event.getNewValue();
			try {
		    	HibernateUtil.setCloseSession( false );
				this.workPlace = CompanyUtil.getWorkPlace( workPlaceId );
				// Find working place active activities and employees, otherwise inactive.
				int active = ( this.workPlace.isActive() )? 1: -1;
				setWorkActivities( CompanyUtil.getSelectItemActivities( this.workPlace, active ) );
				setEmployees( CompanyUtil.getSelectItemEmployees( this.workPlace, active ) );
				this.workActivity = (WorkActivity) BeanManager.getManagerBean( WorkActivity.class ).createNewTo();
				setEmployeeId( -1 );
			} finally {
		    	HibernateUtil.setCloseSession( true );
			}
		}
	}

	@SuppressWarnings("unused")
	public void employeeChanged(ValueChangeEvent event) throws ManagerBeanException {
		this.employeeId = (Integer) event.getNewValue();
	}

	@SuppressWarnings("unused")
	public void workingActivityChanged(ValueChangeEvent event) throws ManagerBeanException {
		Integer newValue = (Integer) event.getNewValue();
		if ( isDirtyValueChangeEvent( event ) && newValue != -1 ) {
			try {
		    	HibernateUtil.setCloseSession( false );
				this.workActivity = CompanyUtil.getWorkActivity( (Integer) event.getNewValue() );
				// Find active or inactive working activities.
				int active = ( this.workActivity.isActive() )? 1: -1;
				setEmployees( CompanyUtil.getSelectItemEmployees( this.workActivity, active ) );
			} finally {
		    	HibernateUtil.setCloseSession( true );
			}
		}
	}

//	****************** ICollectionProvider methods implementation ****************************
	@SuppressWarnings("unchecked")
//	@Override
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
	}

	@SuppressWarnings("unchecked")
//	@Override
	public Collection getCollection() {
		this.collection = new ArrayList<IncidenceBean>();
		try {
			if ( this.workPlace.getId() == -1 ) {
				List workPlaces = CompanyUtil.getWorkPlaces();
				Iterator iter = workPlaces.iterator();
				while (iter.hasNext()) {
					((WorkPlace) iter.next()).accept( this );
				}
			} else {
				CompanyUtil.getWorkPlace( this.workPlace.getId() ).accept( this );
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		if ( this.collection.size() <= 0 ) {
			IncidenceBean ib = new IncidenceBean();
			ib.setWorkPlace( this.workPlace );
			ib.setWorkActivity( this.workActivity );
			this.collection.add( new IncidenceBean() ); 
		}
		return this.collection;
	}
//	****************** ICollectionProvider methods implementation ****************************

//	****************** IEntityVisitor methods implementation *********************************
	@SuppressWarnings("unchecked")
//	@Override
	public void visitEmployee(Employee employee) {
		try {
			AonCalendar aonCalendar = CalendarHelper.getCalendar( employee.getCalendar() );
			if ( aonCalendar != null ) {
				ComponentList cl = null;
				cl = aonCalendar.getVEvents( this.startingDate, this.endingDate, EventCategory.INCIDENCE );
				Iterator iter = cl.iterator();
				while (iter.hasNext()) {
					VEvent event = (VEvent) iter.next();
					IncidenceBean ib = new IncidenceBean();
					ib.setWorkPlace( this.wp );
					ib.setWorkActivity( this.wa );
					ib.setEmployee( employee );
					Date start = CalendarUtil.getDate( (DateTime)event.getStartDate().getDate() );
					Date end = CalendarUtil.getDate( (DateTime)event.getEndDate().getDate() );
				    IEvent ievent = (IEvent) PlannerUtil.getScheduleEntry( event, start, end );
				    if ( this.incidenceType != -1 || this.incidenceCompute != 99 ) {
				    	Integer identifier = getIncidenceId( ievent.getDescription() );
					    if ( this.incidenceType != -1 && this.incidenceCompute != 99 ) {
				    		if ( this.incidenceType == identifier ) {
				    			IncidenceType it = getIncidenceType( identifier );
								if ( this.incidenceCompute == it.getCompute() ) {
									ib.setEvent( ievent );
								}
				    		}
					    } else {
					    	if ( this.incidenceType != -1 && this.incidenceCompute == 99 ) {
					    		if ( this.incidenceType == identifier ) {
									ib.setEvent( ievent );
					    		}
					    	} else {
				    			IncidenceType it = getIncidenceType( identifier );
								if ( this.incidenceCompute == it.getCompute() ) {
									ib.setEvent( ievent );
								}
					    	}
					    } 
				    } else {
						ib.setEvent( ievent );
				    }
					this.collection.add( ib );
				}
			}
		} catch (CalendarException e) {
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
//	@Override
	public void visitWorkActivity(WorkActivity wa) {
		this.wa = wa;
		if ( this.employeeId.intValue() == -1 ) {
			Iterator iter = this.wa.getEmployees().iterator();
			while (iter.hasNext()) {
				((Employee) iter.next()).accept(this);
			}
		} else {
			Iterator iter = this.wa.getEmployees().iterator();
			while (iter.hasNext()) {
				Employee employee = (Employee) iter.next();
				if ( this.employeeId.compareTo( employee.getId() ) == 0 ) {
					employee.accept( this );
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
//	@Override
	public void visitWorkPlace(WorkPlace wp) {
		this.wp = wp;
		this.wa = this.workActivity;
		if ( this.employeeId.intValue() == -1 ) {
			Iterator iter = this.wp.getEmployees().iterator();
			while (iter.hasNext()) {
				((Employee) iter.next()).accept(this);
			}
		} else {
			Iterator iter = this.wp.getEmployees().iterator();
			while (iter.hasNext()) {
				Employee employee = (Employee) iter.next();
				if ( this.employeeId.compareTo( employee.getId() ) == 0 ) {
					employee.accept( this );
					break;
				}
			}
		}
		if ( this.workActivity.getId() == -1 ) {
			Iterator iter = this.wp.getActivities().iterator();
			while (iter.hasNext()) {
				((WorkActivity) iter.next()).accept(this);
			}
		} else {
			this.workActivity.accept( this );
		}
	}
//	****************** Ends of IEntityVisitor methods implementation *************************

	private WorkPlace wp;
	private WorkActivity wa;

	private boolean isDirtyValueChangeEvent(ValueChangeEvent event) {
		Integer oldValue = (Integer) event.getOldValue();
		Integer newValue = (Integer) event.getNewValue();
		return oldValue == null || newValue.intValue() != oldValue.intValue();
	}

	/**
	 * Returns incidence identifier.
	 * 
	 * @param incidence
	 * @return
	 */
	private Integer getIncidenceId(String incidence) {
		int start = incidence.indexOf( '(' );
		int end = incidence.indexOf( ')' );
		try {
			return Integer.parseInt( incidence.substring( start, end ) );
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Returns an <code>IncidenceType</code> instance.
	 * 
	 * @param identifier
	 * @return
	 * @throws ManagerBeanException
	 */
	private IncidenceType getIncidenceType(Integer identifier) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( IncidenceType.class );
		Criteria criteria = new Criteria();
		String field = bean.getFieldName( IPlannerAlias.INCIDENCE_TYPE_ID );
		criteria.addEqualExpression( field, identifier );
		return (IncidenceType) bean.getList( criteria ).get( 0 );
	}

}
