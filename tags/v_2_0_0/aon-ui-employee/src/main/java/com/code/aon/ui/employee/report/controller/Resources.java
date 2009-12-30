/**
 * 
 */
package com.code.aon.ui.employee.report.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.resources.Resource;
import com.code.aon.record.Position;
import com.code.aon.ui.company.controller.CompanyUtil;
import com.code.aon.ui.employee.util.Constants;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 12/09/2007
 *
 */
public class Resources implements ICollectionProvider {

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
    /** Report collection */
    private List<ResourceBean> collection;

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
		setWorkActivities( CompanyUtil.getSelectItemActivities( this.workPlace ) );
		setEmployeeId( -1 );
		setEmployees( CompanyUtil.getSelectItemEmployees( this.workPlace ) );
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
		setEndingDate( (Date) event.getNewValue() );
	}

	@SuppressWarnings("unused")
	public void workingPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( isDirtyValueChangeEvent( event ) ) {
			Integer workPlaceId = (Integer) event.getNewValue();
			try {
		    	HibernateUtil.setCloseSession( false );
				this.workPlace = CompanyUtil.getWorkPlace( workPlaceId );
				setWorkActivities( CompanyUtil.getSelectItemActivities( this.workPlace ) );
				setEmployees( CompanyUtil.getSelectItemEmployees( this.workPlace ) );
				this.workActivity = (WorkActivity) BeanManager.getManagerBean( WorkActivity.class ).createNewTo();
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
				setEmployees( CompanyUtil.getSelectItemEmployees( this.workActivity ) );
			} finally {
		    	HibernateUtil.setCloseSession( true );
			}
		}
	}

    /* (non-Javadoc)
	 * @see com.code.aon.common.ICollectionProvider#getCollection()
	 */
	public Collection getCollection() {
		this.collection = new ArrayList<ResourceBean>();
		try {
			String workPlaceWhere = (this.workPlace.getId() != -1)? " workPlace = :workPlaceId AND ": Constants.EMPTY_STRING;
			String workActivityWhere = (this.workActivity.getId() != -1)? " workActivity = :workActivityId AND ": Constants.EMPTY_STRING;
			String employeeWhere = (this.employeeId != -1)? " employee = :employeeId AND ": Constants.EMPTY_STRING;
			String select = "FROM Resource resource " + 
							" WHERE" + workPlaceWhere + workActivityWhere + employeeWhere + 
							" startingDate >= :startingDate" +
							" AND (endingDate <= :endingDate OR endingDate is null)" +
							" ORDER BY workPlace, workActivity";
			Query query = HibernateUtil.getSession().createQuery( select );
			if ( this.workPlace.getId() != -1 ) {
				query.setInteger( "workPlaceId", this.workPlace.getId() );
			}
			if ( this.workActivity.getId() != -1 ) {
				query.setInteger( "workActivityId", this.workActivity.getId() );
			}
			if ( this.employeeId != -1 ) {
				query.setInteger( "employeeId", this.employeeId );
			}
			query.setDate( "startingDate", startingDate );
			query.setDate( "endingDate", endingDate );
			Iterator iter = query.list().iterator();
			while (iter.hasNext()) {
				Resource r = (Resource) iter.next();
				ResourceBean rb = new ResourceBean( r );
				Position p = null;
				try { 
					p = getEmployeeSelectedPosition( r );
				} catch (NonUniqueResultException e) {
					e.printStackTrace();
				}
				if ( p != null )
					rb.setPosition( p.getDescription() );
				try {
					if ( p != null )
						p = getEmployeePreviousPosition( r, p.getId() );
				} catch (NonUniqueResultException e) {
					e.printStackTrace();
					p = null;
				}
				if ( p != null ) {
					System.out.println( r.getId() + " "  + p.getWorkPlace() );
					rb.setOrigin( p.getWorkPlace().getDescription() + ( (p.getWorkActivity() != null)? "-" + p.getWorkActivity().getDescription(): "" ) );
				}
				this.collection.add( rb ); 
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		if ( this.collection.size() <= 0 ) {
			try {
				ResourceBean rb = new ResourceBean( (Resource) BeanManager.getManagerBean( Resource.class ).createNewTo() );
				this.collection.add( rb );
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			} 
		}
		return this.collection;
	}

	/**
	 * Retrieves Employee selected position.
	 * 
	 * @param r
	 * @return
	 * @throws ManagerBeanException
	 */
	private Position getEmployeeSelectedPosition(Resource r) throws ManagerBeanException {
		String str = (r.getWorkActivity()	 != null)? "workActivity = :workActivityId": "workActivity is null";
		String select = "FROM Position position" +
  						" WHERE employee = :employeeId" + 
						" AND workPlace = :workPlaceId AND " + str +
  						" AND startingDate = :startingDate";
		Query query = HibernateUtil.getSession().createQuery( select );
		query.setInteger( "employeeId", r.getEmployee().getId() );
		query.setInteger( "workPlaceId", r.getWorkPlace().getId() );
		if ( r.getWorkActivity() != null )
			query.setInteger( "workActivityId", r.getWorkActivity().getId() );
		query.setDate( "startingDate", r.getStartingDate() );
		return (Position) query.uniqueResult();
	}

	/**
	 * Retrieves Employee previous selected position.
	 * 
	 * @param r
	 * @param currentPositionId
	 * @return
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	private Position getEmployeePreviousPosition(Resource r, Integer currentPositionId) 
				throws ManagerBeanException {
		String select = "FROM Position position" +
  						" WHERE employee = :employeeId" + 
  						" AND endingDate = :startingDate" +
  						" ORDER BY id DESC";
		Query query = HibernateUtil.getSession().createQuery( select );
		query.setInteger( "employeeId", r.getEmployee().getId() );
		query.setDate( "startingDate", r.getStartingDate() );
		List<Position> l = query.list();
		if ( l.size() > 0 ) {
			if ( l.size() > 1 ) {
				int id = -1, index = 0;
				Iterator<Position> iter = l.iterator();
				while (iter.hasNext() && id < 0) {
					Position p = iter.next();
					id = currentPositionId - p.getId();
					index++; 
				}
				return l.get( index );
			}
			return l.get( 0 );
		}
		return null;
	}

	private boolean isDirtyValueChangeEvent(ValueChangeEvent event) {
		Integer oldValue = (Integer) event.getOldValue();
		Integer newValue = (Integer) event.getNewValue();
		return oldValue == null || newValue.intValue() != oldValue.intValue();
	}
}
