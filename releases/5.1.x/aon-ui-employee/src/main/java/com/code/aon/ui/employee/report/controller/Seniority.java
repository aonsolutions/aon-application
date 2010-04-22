/**
 * 
 */
package com.code.aon.ui.employee.report.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;
import com.code.aon.record.Contract;
import com.code.aon.record.dao.IRecordAlias;
import com.code.aon.ui.employee.util.Constants;
import com.code.aon.ui.employee.util.Utils;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 12/09/2007
 *
 */
public class Seniority implements ICollectionProvider {

	/** Incidences report starting date */
	private Date startingDate; 
    /** Incidence Compute to report */
	private Integer seniorityType;
    /** Report collection */
    private List<SeniorityBean> collection;

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
	 * @return the seniorityType
	 */
	public Integer getSeniorityType() {
		return seniorityType;
	}

	/**
	 * @param seniorityType the seniorityType to set
	 */
	public void setSeniorityType(Integer seniorityType) {
		this.seniorityType = seniorityType;
	}

	/**
	 * @return the seniority types.
	 */
	public List<SelectItem> getSeniorityTypes() {
		return Utils.getSeniorityTypes();
	}

	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException {
		setStartingDate( new Date() );
		setSeniorityType( -1 );
	}

	/**
	 * This methods gets call after changing date.
	 *  
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void dateChanged(ValueChangeEvent event) throws ManagerBeanException {
		this.startingDate = (Date) event.getNewValue();
	}

	/**
	 * This methods gets call after changing seniority type.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	public void seniorityChanged(ValueChangeEvent event) throws ManagerBeanException {
		this.seniorityType = (Integer) event.getNewValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		this.collection = new ArrayList<SeniorityBean>();
		try {
			Date date = this.startingDate;
			IManagerBean bean = BeanManager.getManagerBean( Employee.class );
			Iterator iter = bean.getList( null ).iterator();
			while (iter.hasNext()) {
				Employee employee = (Employee) iter.next();
				SeniorityBean sb = new SeniorityBean();
				sb.setEmployee( employee );
				List l = getEmployeeContracts( employee.getId() );
				if ( l.size() > 0 ) {
					String baseName = 
						AonUtil.getConfigurationController().getApplicationBundles().get( Constants.APPLICATION_BUNDLE_BASE_NAME );
					ResourceBundle bundle = ResourceBundle.getBundle( baseName );
					Date contractFirstDate = fillContractFirstDate( l, sb );
					fillContractEndingDate( l, sb );
					Integer seniorityPeriod = getSeniorityPeriod( date, contractFirstDate );
					sb.setSeniority( seniorityPeriod );
					if ( seniorityPeriod > 0 ) {
						switch (seniorityType) {
							case 0:
								if ( seniorityPeriod.intValue() % 3 == 0 ) {
									sb.setSeniorityType( bundle.getString( "record_seniority_3" )  );
									this.collection.add( sb );
								}
								break;
							case 1:
								if ( seniorityPeriod.intValue() % 5 == 0 ) {
									sb.setSeniorityType( bundle.getString( "record_seniority_5" )  );
									this.collection.add( sb );
								}
								break;
							default:
								if ( seniorityPeriod.intValue() % 3 == 0 )
									sb.setSeniorityType( bundle.getString( "record_seniority_3" )  );
								else if ( seniorityPeriod.intValue() % 5 == 0 )
									sb.setSeniorityType( bundle.getString( "record_seniority_5" )  );
								if ( seniorityPeriod.intValue() % 3 == 0 || seniorityPeriod.intValue() % 5 == 0 ) {
									this.collection.add( sb );
								}
								break;
						}
					}
				}
			}
			if ( this.collection.size() <= 0 ) {
				SeniorityBean sb = new SeniorityBean();
				sb.setEmployee( (Employee) BeanManager.getManagerBean( Employee.class ).createNewTo() );
				this.collection.add( sb ); 
			}
		} catch (ManagerBeanException e) {
		}
		return this.collection;
	}

	private Integer getSeniorityPeriod(Date date, Date contractStartingDate) {
		Calendar current = Calendar.getInstance();
		current.setTime( date );
		Calendar first = Calendar.getInstance();
		first.setTime( contractStartingDate );
		int month = first.get( Calendar.MONTH ) - current.get( Calendar.MONTH );
		return (month != 0 && month != 1)? -1: current.get( Calendar.YEAR ) - first.get( Calendar.YEAR );
	}

	/**
	 * Retrieves Last contract ending date.
	 * 
	 * @param l
	 * @param sb
	 */
	@SuppressWarnings("unchecked")
	private void fillContractEndingDate(List l, SeniorityBean sb) {
		Contract contract = (Contract) l.get( l.size() - 1  );
		sb.setEndingDate( contract.getEndingDate() );
	}

	/**
	 * Retrieves First contract starting date.
	 * 
	 * @param l
	 * @param sb
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Date fillContractFirstDate(List l, SeniorityBean sb) {
		Contract contract = (Contract) l.get( 0 );
		sb.setStartingDate( contract.getStartingDate() );
		return contract.getStartingDate();
	}

	/**
	 * Retrieves Employee contract list.
	 * 
	 * @param id
	 * @return
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	private List getEmployeeContracts(Integer id) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( Contract.class );
		Criteria criteria = new Criteria();
		String identifier = bean.getFieldName( IRecordAlias.CONTRACT_EMPLOYEE_ID );
		criteria.addEqualExpression( identifier, id );
		identifier = bean.getFieldName( IRecordAlias.CONTRACT_STARTING_DATE );
		criteria.addOrder( identifier );
		return bean.getList( criteria );
	}

}
