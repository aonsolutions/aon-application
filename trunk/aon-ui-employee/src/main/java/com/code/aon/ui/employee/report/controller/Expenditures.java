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

import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.resources.Resource;
import com.code.aon.ui.company.controller.CompanyUtil;
import com.code.aon.ui.employee.util.Constants;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 12/09/2007
 *
 */
public class Expenditures implements ICollectionProvider {

	/** Expenditures report starting date */
	private Date startingDate; 
	/** Expenditures report ending date */
	private Date endingDate; 
	/** Selected working place */
	private WorkPlace workPlace;

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

	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException {
		setStartingDate( new Date() );
		setEndingDate( new Date() );
		this.workPlace = (WorkPlace) BeanManager.getManagerBean( WorkPlace.class ).createNewTo();
		this.workPlace.setId( -1 );
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

	/**
	 * This methods gets call after changing the working place.
	 *  
	 * @param event
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unused")
	public void workingPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( isDirtyValueChangeEvent( event ) ) {
			Integer workPlaceId = (Integer) event.getNewValue();
			this.workPlace = CompanyUtil.getWorkPlace( workPlaceId );
		}
	}

    /* (non-Javadoc)
	 * @see com.code.aon.common.ICollectionProvider#getCollection()
	 */
	public Collection getCollection() {
		String str = (this.workPlace.getId() != -1)? "resource.workPlace.id = :workplace AND ": Constants.EMPTY_STRING;
		String select = "FROM Expenditures expenditures, Resource resource" +
  						" WHERE " + str + "resource.id = expenditures.resource" +
  						" AND expenditures.date BETWEEN :startingDate AND :endingDate";
		Query query = HibernateUtil.getSession().createQuery( select );
		query.setDate( "startingDate", this.startingDate );
		query.setDate( "endingDate", this.endingDate );
		if (this.workPlace.getId() != -1)
			query.setInteger( "workplace", this.workPlace.getId() );
		ArrayList<ExpendituresBean> l = new ArrayList<ExpendituresBean>();
		List model = query.list();
		if ( model != null ) {
			Iterator iter = model.iterator();
			while (iter.hasNext()) {
				Object[] obj = (Object[]) iter.next();
				com.code.aon.employee.Expenditures exp = 
					(com.code.aon.employee.Expenditures) obj[0];
				ExpendituresBean eb = new ExpendituresBean();
				eb.setAmount( exp.getAmount() );
				eb.setDate( exp.getDate() );
				eb.setItem( exp.getItem() );
				Resource resource = (Resource) obj[1];
				eb.setWorkPlace( resource.getWorkPlace() );
				eb.setEmployee( resource.getEmployee() );
				l.add( eb );
			}
		}
		if ( l.size() <= 0 ) {
			l.add( new ExpendituresBean() );
		}
		return l;
	}

	private boolean isDirtyValueChangeEvent(ValueChangeEvent event) {
		Integer oldValue = (Integer) event.getOldValue();
		Integer newValue = (Integer) event.getNewValue();
		return oldValue == null || newValue.intValue() != oldValue.intValue();
	}

}
