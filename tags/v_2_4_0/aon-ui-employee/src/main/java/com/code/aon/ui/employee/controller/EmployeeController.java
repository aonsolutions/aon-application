package com.code.aon.ui.employee.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.html.HtmlInputText;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;
import com.code.aon.cvitae.Curriculum;
import com.code.aon.cvitae.dao.ICVitaeAlias;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.company.controller.CompanyUtil;
import com.code.aon.ui.cvitae.controller.CurriculumController;
import com.code.aon.ui.employee.util.Constants;
import com.code.aon.ui.employee.util.Utils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.record.controller.ContractController;
import com.code.aon.ui.record.controller.RecordController;
import com.code.aon.ui.util.AonUtil;

public class EmployeeController extends BasicController {
	
	private static final long serialVersionUID = 7638824496547268973L;
	private static final Logger LOGGER = Logger.getLogger(EmployeeController.class.getName());
	private static final String REGISTRY_ADDRESS_ID = "raddress_id";
	private static final String REGISTRY_ADDRESS = "raddress";
	private static final String REGISTRY_ADDRESS_CITY = "city";
	
	public static final String MANAGER_BEAN_NAME = "employee";

	/** Employee current resource */
	private Resource resource;
	/** Tell if the resource can be updated */
	private boolean isResourceAllowed;
	/** Tell if the employee working place or activity has changed */
	private boolean isResourceDirty;
    /** Enables employee user creation. */
    private boolean userEnabled;
	/** Employee current activity identifier */
	private Integer activityId;
	/** Employee working activities */
    private List<SelectItem> activities;
    /** Indicates if searching active field have changed. */
    private boolean activeFieldChanged;

    /**
     * Return employee.
     * 
     * @return
     */
	public Employee getEmployee() {
		return (Employee) getTo();
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
	 * @return the activityId
	 */
	public Integer getActivityId() {
		return activityId;
	}

	/**
	 * @param activityId the activityId to set
	 */
	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	/**
	 * @return the isResourceAllowed
	 */
	public boolean isResourceAllowed() {
		return isResourceAllowed;
	}

	/**
	 * @param isResourceAllowed the isResourceAllowed to set
	 */
	public void setResourceAllowed(boolean isResourceAllowed) {
		this.isResourceAllowed = isResourceAllowed;
	}

	/**
	 * @return the isResourceDirty
	 */
	public boolean isResourceDirty() {
		return isResourceDirty;
	}

	/**
	 * @param isResourceDirty the isResourceDirty to set
	 */
	public void setResourceDirty(boolean isResourceDirty) {
		this.isResourceDirty = isResourceDirty;
	}

    /**
	 * @return the userEnabled
	 */
	public boolean isUserEnabled() {
		return userEnabled;
	}

	/**
	 * @param userEnabled the userEnabled to set
	 */
	public void setUserEnabled(boolean userEnabled) {
		this.userEnabled = userEnabled;
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

	@SuppressWarnings("unused")
	public void onSearch(MenuEvent event) throws ManagerBeanException {
		this.onEditSearch( (ActionEvent) event );
		this.onSearch( (ActionEvent) event );
	}

	@SuppressWarnings("unused")
	public void workingPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		this.isResourceDirty = true;
		Integer workPlaceId = (Integer) event.getNewValue();
		WorkPlace workPlace = CompanyUtil.getWorkPlace( workPlaceId );
		this.resource.setWorkPlace( workPlace );
		 // Find working place active activities, otherwise inactive.
		this.activities = CompanyUtil.findActivities( workPlaceId, ( (workPlace.isActive())? 1: -1) );
	}

	@SuppressWarnings("unused")
	public void workingActivityChanged(ValueChangeEvent event) throws ManagerBeanException {
		this.isResourceDirty = true;
		this.activityId = (Integer) event.getNewValue();
		WorkActivity workActivity = CompanyUtil.getWorkActivity( this.activityId );
		this.resource.setWorkActivity( workActivity );
	}

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.ISearchable#addExpression(javax.faces.event.ValueChangeEvent)
     */
    public void addActiveFieldExpression(ValueChangeEvent event) throws ManagerBeanException {
    	activeFieldChanged = true;
    	super.addExpression( event );
    }

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
    	activeFieldChanged = false;
		Employee employee = (Employee) getTo();
		employee.setActive( true );
	}

	@Override
	public void onSearch(ActionEvent event) {
		if ( !activeFieldChanged ) {
			try {
				Employee employee = (Employee) getTo();
				getCriteria().addEqualExpression( getManagerBean().getFieldName( ICompanyAlias.EMPLOYEE_ACTIVE ), employee.isActive() );
	        } catch (ManagerBeanException e) {
	        	Utils.addMessage( e.getMessage(), false );
	            throw new AbortProcessingException( e.getMessage(), e );
	        }
		}
		super.onSearch(event);
	}

	@Override
    public void onReset(ActionEvent event) {
    	IController addressController = AonUtil.getController( EmployeeAddressController.MANAGER_BEAN_NAME );
        addressController.onCancel(event);
        IController mediaController = AonUtil.getController( EmployeeMediaController.MANAGER_BEAN_NAME );
        mediaController.onCancel(event);
        super.onReset(event);
        setResourceAllowed( true );
        setUserEnabled( false );
        //	Initializes ContractController session bean.
        ContractController contract = (ContractController) AonUtil.getController(ContractController.MANAGER_BEAN_NAME);
        contract.setModel( null );
    }

	@Override
	public void onRemove(ActionEvent event) {
		// Employee termination.
		try {
			ControllerEvent evt = new ControllerEvent(this);
			controllerListenerSupport.fireBeforeBeanRemoved(evt);
			getEmployee().setActive( false );
			getManagerBean().update( getEmployee() );
			initializeModel();
			controllerListenerSupport.fireAfterBeanRemoved(evt);
		} catch (ControllerListenerException e) {
			LOGGER.severe(">>>> onRemove exception[" + e.getMessage() + "]");
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			LOGGER.severe(">>>> onRemove exception[" + e.getMessage() + "]");
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.resetTo();
	}

	@Override
    public void onSelect(ActionEvent event) {
    	IController addressController = AonUtil.getController( EmployeeAddressController.MANAGER_BEAN_NAME );
        addressController.onCancel(event); 
        IController mediaController = AonUtil.getController( EmployeeMediaController.MANAGER_BEAN_NAME );
        mediaController.onCancel(event);
        super.onSelect(event);
        setResourceAllowed( false );
//	TODO Employee employee = (Employee) getTo();setUserEnabled( employee.getUser().getId() != null );
        setUserEnabled( false ); 
    }

	@Override
	public void accept(ActionEvent event) {
		HibernateUtil.setBeginTransaction( false );
		HibernateUtil.setCloseSession( false );
		try {
			HibernateUtil.beginTransaction();
	    	if ( super.isNew() )
	    		look4DuplicatedDocument();
			try {
				super.accept(event);
			} catch (AbortProcessingException e) {
				//	Checks if social security number is unique.
				if ( e.getMessage().indexOf( "Duplicate entry" ) > -1 ) {
					String select = "FROM Employee employee WHERE socialSecurityNumber = :ssn";
					Query query = HibernateUtil.getSession().createQuery( select );
					query.setString( "ssn", this.getEmployee().getSocialSecurityNumber() );
					if ( query.uniqueResult() != null ) {
						Utils.addMessage( "aon_employee_unique_ssn_exception", true );
					}
				}
				throw e;
			}
			HibernateUtil.commitTransaction();
		} catch (AbortProcessingException e) {
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DAOException e1) {
			}
			throw e;
		} catch (DAOException e) {
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DAOException e1) {
			}
            throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			HibernateUtil.setBeginTransaction( true );
			HibernateUtil.setCloseSession( true );
		}
	}

	@SuppressWarnings("unused")
	public void onCurriculum(ActionEvent event) throws ManagerBeanException {
        CurriculumController cv = (CurriculumController) AonUtil.getController( CurriculumController.MANAGER_BEAN_NAME );
		Employee employee = (Employee) getTo(); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(cv.getManagerBean().getFieldName(ICVitaeAlias.CURRICULUM_ID), employee.getId());
		cv.setCriteria(criteria);
		cv.onSearch(event);
		if ( cv.getModel().getRowCount() > 0 ) {
			cv.getModel().setRowIndex(0);
			cv.onSelect(event);
		} else {
			cv.onReset(event);
			Curriculum c = (Curriculum) cv.getTo();
			c.setRegistry(employee.getRegistry());
			c.setPostcategory( true ); // We talk about an employee. 
		}
	}

	@SuppressWarnings("unused")
	public void onRecord(ActionEvent event) throws ManagerBeanException {
        RecordController record = (RecordController) AonUtil.getRegisteredBean( RecordController.MANAGER_BEAN_NAME );
        Employee employee = (Employee) getTo(); 
		HtmlInputText input = new HtmlInputText();
		input.setId("Employee_id");
		ValueChangeEvent vEvent = new ValueChangeEvent(input, null, employee.getId());
        record.employeeChanged(vEvent);
	}

	@SuppressWarnings("unused")
	public void onTracking(ActionEvent event) throws ManagerBeanException {
		IController tracking = AonUtil.getController( "tracking" );
		Employee employee = (Employee) getTo(); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( tracking.getManagerBean().getFieldName(IRegistryAlias.REGISTRY_NOTE_REGISTRY_ID), employee.getId() );
		tracking.setCriteria(criteria);
		tracking.onSearch( event );
	}

	@SuppressWarnings("unchecked")
	public void onChangeGeoZone(ValueChangeEvent event) throws ManagerBeanException {
    	if(event.getNewValue() != null){
    		IManagerBean geoZoneBean = BeanManager.getManagerBean(GeoZone.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(geoZoneBean.getFieldName(IGeoZoneAlias.GEO_ZONE_ID), event.getNewValue());
    		Iterator iter = geoZoneBean.getList(criteria).iterator();
        	IController addressController = AonUtil.getController( EmployeeAddressController.MANAGER_BEAN_NAME );
    		if(iter.hasNext()){
    			( (RegistryAddress) addressController.getTo() ).setGeozone((GeoZone)iter.next());
    		}
    	}
    }

	@SuppressWarnings("unchecked")
	@Override
	protected void customizeLookupMap(ILookupObject ito, Map<String, Object> map) {
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),map.get("Employee_id"));
			Iterator iter = rAddressBean.getList(criteria).iterator();
			if(iter.hasNext()){
				RegistryAddress rAddress = (RegistryAddress)iter.next();
				map.put(REGISTRY_ADDRESS_ID, rAddress.getId());
				map.put(REGISTRY_ADDRESS,((rAddress.getAddress() != null)?rAddress.getAddress():Constants.EMPTY_STRING) + " " + ((rAddress.getAddress2()!= null)?rAddress.getAddress2():Constants.EMPTY_STRING) + " " + ((rAddress.getAddress3()!=null)?rAddress.getAddress3():Constants.EMPTY_STRING) );
				map.put(REGISTRY_ADDRESS_CITY, rAddress.getCity() + " " + rAddress.getZip());
			}else{
				map.put(REGISTRY_ADDRESS, Constants.EMPTY_STRING );
				map.put(REGISTRY_ADDRESS_CITY, Constants.EMPTY_STRING);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error customizing lookup map",e);
		}
	}

	/**
	 * Looks for duplicated identity card number.
	 */
	private void look4DuplicatedDocument() {
		String select = "FROM Registry registry WHERE document = :document";
		Query query = HibernateUtil.getSession().createQuery( select );
		query.setString( "document", this.getEmployee().getRegistry().getDocument() );
		if ( query.uniqueResult() != null ) {
			Utils.addMessage( "aon_employee_unique_document_exception", true );
			throw new AbortProcessingException();
		}
	}

}