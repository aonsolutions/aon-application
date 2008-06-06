/**
 * 
 */
package com.code.aon.ui.employee.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.myfaces.shared_impl.util.MessageUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Resource;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 28/08/2007
 *
 */
public class ExpendituresController extends GridController {

	public static final String MANAGER_BEAN_NAME = "expenditures";

	/** Working Place resources list. */
	DataModel resources;
	/** Indicates current resource of selected employee from resources list. */
	Resource resource;
	/** Working Place identifier. */
	Integer workPlaceId;
	
	/**
	 * @return the employees
	 */
	public DataModel getResources() {
		return resources;
	}

	/**
	 * @param employees the employees to set
	 */
	public void setResources(DataModel employees) {
		this.resources = employees;
	}

	/**
	 * @return the resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * @return the workPlaceId
	 */
	public Integer getWorkPlaceId() {
		return workPlaceId;
	}

	/**
	 * @param workPlaceId the workPlaceId to set
	 */
	public void setWorkPlaceId(Integer workPlaceId) {
		this.workPlaceId = workPlaceId;
	}

	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException {
		this.resources = new ListDataModel( new ArrayList<Resource>() );
		this.resource = null;
		this.workPlaceId = 0;
	}

	/**
	 * Gets called whenever a working place is selected.
	 * 
	 * @param event
	 */
	public void onSearchResources(ActionEvent event) {
		List<Resource> l = new ArrayList<Resource>();
		this.resources = new ListDataModel( l );
		HibernateUtil.setCloseSession( false );
        try {
			IManagerBean bean = BeanManager.getManagerBean( WorkPlace.class );
			Criteria criteria = new Criteria();
			String field = bean.getFieldName(ICompanyAlias.WORK_PLACE_ID);
			criteria.addEqualExpression( field, this.workPlaceId );
			Iterator iter = bean.getList(criteria).iterator();
			while (iter.hasNext()) {
				WorkPlace workPlace = (WorkPlace) iter.next();
				Iterator i = workPlace.getResources().iterator();
		        while (i.hasNext()) {
		        	Resource r = (Resource) i.next();
		        	if ( r.getEmployee().isActive() ) 
		        		l.add( r );
		        }
			}  
        } catch (ManagerBeanException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
        } finally {
			HibernateUtil.setCloseSession(true);
			HibernateUtil.closeSession();
		}
	}

	/**
	 * Gets called whenever an employee is selected.
	 * 
	 * @param event
	 */
    public void onSelectEmployee(ActionEvent event) {
    	this.resource = (Resource) this.resources.getRowData();
    	try {
			super.clearCriteria();
	    	super.getCriteria().addEqualExpression( getManagerBean().getFieldName( IEmployeeAlias.EXPENDITURES_RESOURCE), this.resource.getId() );
	    	super.onSearch( event );
		} catch (ManagerBeanException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		}
    }

	/**
	 * Gets called whenever an employee is unselected.
	 * 
	 * @param event
	 */
	public void onCancelEmployee(ActionEvent event) {
		this.resource = null;
		super.onCancel(event);
	}

}
