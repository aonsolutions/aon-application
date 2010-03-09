/**
 * 
 */
package com.code.aon.ui.company.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 03/10/2007
 *
 */
@SuppressWarnings("unchecked")
public class CompanyUtil {

	/**
	 * Returns a list with company working places.
	 * 
	 * @return
	 * @throws ManagerBeanException
	 */
	public static final List getWorkPlaces() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( WorkPlace.class );
		return bean.getList( null );
	}

	/**
	 * Returns the <code>WorkPlace</code> instance.
	 * 
	 * @param workPlaceId
	 * @return
	 * @throws ManagerBeanException
	 */
	public static final WorkPlace getWorkPlace(Integer workPlaceId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( WorkPlace.class );
		Criteria criteria = new Criteria();
		String field = bean.getFieldName( ICompanyAlias.WORK_PLACE_ID );
		criteria.addEqualExpression( field, workPlaceId );
		List l = bean.getList(criteria);
		if ( l.size() > 0 ) {
			return (WorkPlace) l.get(0);
		}
		WorkPlace wp = (WorkPlace) BeanManager.getManagerBean( WorkPlace.class ).createNewTo();
		wp.setId( -1 );
		return wp;
	}

	/**
	 * Returns the <code>WorkActivity</code> instance.
	 * 
	 * @param workActivityId
	 * @return
	 * @throws ManagerBeanException
	 */
	public static final WorkActivity getWorkActivity(Integer workActivityId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( WorkActivity.class );
		Criteria criteria = new Criteria();
		String field = bean.getFieldName( ICompanyAlias.WORK_ACTIVITY_ID );
		criteria.addEqualExpression( field, workActivityId );
		List l = bean.getList(criteria);
		return ( l.size() > 0 )? (WorkActivity) l.get(0): (WorkActivity) BeanManager.getManagerBean( WorkActivity.class ).createNewTo();
	}

	/**
	 * Find working activities bound to the selected working place identifier. 
	 * 
	 * @param workPlaceId
	 * @param active
	 * @throws ManagerBeanException
	 */
	public static final List<SelectItem> findActivities(Integer workPlaceId, int active) 
				throws ManagerBeanException {
		List<SelectItem> activities = new ArrayList<SelectItem>();
		activities.add( new SelectItem( -1, "" ) );
		Criteria criteria = new Criteria(); 
		IManagerBean bean = BeanManager.getManagerBean( WorkActivity.class );
		if ( workPlaceId != null ) {
			String identifier = bean.getFieldName( ICompanyAlias.WORK_ACTIVITY_WORK_PLACE_ID );
			criteria.addEqualExpression( identifier, workPlaceId );
			Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
			while (iter.hasNext()) {
				WorkActivity activity = (WorkActivity) iter.next();
				if ( isVisible( active, activity.isActive() ) ) {
					SelectItem item = new SelectItem( activity.getId(), activity.getDescription() );
					activities.add(item);
				}
			}
		}
		return activities;
	}

	/**
	 * Find working activities bound to the selected working place.
	 * 
	 * @param workPlace
	 * @param active
	 * @return
	 */
	public static final List<SelectItem> getSelectItemActivities(WorkPlace workPlace, int active) {
		List<SelectItem> activities = new ArrayList<SelectItem>();
		activities.add( new SelectItem( -1, "" ) );
		Iterator<WorkActivity> iter = workPlace.getActivities().iterator();
		while (iter.hasNext()) {
			WorkActivity activity = iter.next();
			if ( isVisible( active, activity.isActive() ) ) {
				SelectItem item = new SelectItem( activity.getId(), activity.getDescription() );
				activities.add(item);
			}
		}
		return activities;
	}

	/**
	 * Find employees bound to the selected working place and inside its activities. 
	 * If no working place is selected, all employees inside the company are retrieved. 
	 * 
	 * @param workPlace
	 * @param active
	 * @return
	 */
	public static final List<SelectItem> getSelectItemEmployees(WorkPlace workPlace, int active) {
		List<SelectItem> employees = new ArrayList<SelectItem>();
		employees.add( new SelectItem( -1, "" ) );
		if ( workPlace.getId() > -1 ) {
			Iterator empIter = workPlace.getEmployees().iterator();
			while (empIter.hasNext()) {
				Employee employee = (Employee) empIter.next();
				String name = 
					employee.getRegistry().getName() + " " + employee.getRegistry().getSurname();
				if ( isVisible( active, employee.isActive() ) ) {
					SelectItem item = new SelectItem( employee.getId(), name );
					employees.add(item);
				}
			}
			Iterator<WorkActivity> actIter = workPlace.getActivities().iterator();
			while (actIter.hasNext()) {
				WorkActivity workActivity = actIter.next();
				List<SelectItem> l = getSelectItemEmployees( workActivity, active );
				l.remove(0);
				employees.addAll( l );
			}
		} else {
			try {
				IManagerBean bean = BeanManager.getManagerBean( Employee.class );
				Iterator iter = bean.getList( null ).iterator();
				while (iter.hasNext()) {
					Employee employee = (Employee) iter.next();
					String name = 
						employee.getRegistry().getName() + " " + employee.getRegistry().getSurname();
					if ( isVisible( active, employee.isActive() ) ) {
						SelectItem item = new SelectItem( employee.getId(), name );
						employees.add(item);
					}
				}
			} catch (ManagerBeanException e) {
			}
		}
		return employees;
	}

	/**
	 * Find employees bound to the selected working activity.
	 * 
	 * @param workActivity
	 * @param active
	 * @return
	 */
	public static final List<SelectItem> getSelectItemEmployees(WorkActivity workActivity, int active) {
		List<SelectItem> employees = new ArrayList<SelectItem>();
		employees.add( new SelectItem( -1, "" ) );
		Iterator iter = workActivity.getEmployees().iterator();
		while (iter.hasNext()) {
			Employee employee = (Employee) iter.next();
			String name = 
				employee.getRegistry().getName() + " " + employee.getRegistry().getSurname();
			if ( isVisible( active, employee.isActive() ) ) {
				SelectItem item = new SelectItem( employee.getId(), name );
				employees.add(item);
			}
		}
		return employees;
	}

	/**
	 * Return true if the object(employee, resource, workingPlace, workingActivity) can be shown, 
	 * false otherwise. Depending on:
	 * active == 1, shows active working activities.
	 * active == 0, shows active and inactive working activities. 
	 * active == -1 shows inactive working activities.
	 * 
	 * @param active
	 * @param isActive
	 * @return
	 */
	private static final boolean isVisible(int active, boolean isActive) {
		return ( active == 0 || ( (isActive && active == 1) || (!isActive && active == -1) ) );
	}
}
