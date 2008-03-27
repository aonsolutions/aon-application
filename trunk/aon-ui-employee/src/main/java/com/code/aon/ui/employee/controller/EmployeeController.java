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
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.company.controller.CompanyUtil;
import com.code.aon.ui.cvitae.controller.CurriculumController;
import com.code.aon.ui.employee.util.Constants;
import com.code.aon.ui.employee.util.Utils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.record.controller.RecordController;
import com.code.aon.ui.util.AonUtil;

public class EmployeeController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(EmployeeController.class.getName());
	private static final String REGISTRY_ADDRESS_ID = "raddress_id";
	private static final String REGISTRY_ADDRESS = "raddress";
	private static final String REGISTRY_ADDRESS_CITY = "city";
	
	public static final String EMPLOYEE_ADDRESS_CONTROLLER_NAME = "employeeAddress";
	public static final String EMPLOYEE_MEDIA_CONTROLLER_NAME = "employeeMedia";
	public static final String MANAGER_BEAN_NAME = "employee";

//	/** Employee calendar */
//	private AonCalendar calendar;
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

//    /**
//     * Return Employee calendar.
//     * @return
//     */
//    public AonCalendar getAonCalendar() {
//    	return this.calendar;
//    }

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
//        clearCriteria();
//		this.onReset( (ActionEvent) event );
//		Employee employee = (Employee) getTo();
//		employee.setActive( true );
	}

	@SuppressWarnings("unused")
	public void workingPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		this.isResourceDirty = true;
		Integer workPlaceId = (Integer) event.getNewValue();
		WorkPlace workPlace = CompanyUtil.getWorkPlace( workPlaceId );
		this.resource.setWorkPlace( workPlace );
		this.activities = CompanyUtil.findActivities( workPlaceId );
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

    /* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onEditSearch(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
    	activeFieldChanged = false;
		Employee employee = (Employee) getTo();
		employee.setActive( true );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onSearch(javax.faces.event.ActionEvent)
	 */
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
    	IController addressController = AonUtil.getController(EMPLOYEE_ADDRESS_CONTROLLER_NAME);
        addressController.onCancel(event);
        IController mediaController = AonUtil.getController(EMPLOYEE_MEDIA_CONTROLLER_NAME);
        mediaController.onCancel(event);
        super.onReset(event);
        setResourceAllowed( true );
        setUserEnabled( false );
    }

    /* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#onRemove(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onRemove(ActionEvent event) {
//TODO Estudiar si borrar o darle de baja de la empresa.
		Registry registry = ( (Employee) getTo() ).getRegistry();
    	HibernateUtil.setCloseSession(false);
        HibernateUtil.setBeginTransaction(false);
        try {
			HibernateUtil.beginTransaction();
			super.onRemove(event);
            HibernateUtil.commitTransaction();
	        LOGGER.fine("Employee: [" + registry.getName() + " " + registry.getSurname() + "] removed.");
        } catch (DAOException e) {
        	try {
        		HibernateUtil.rollbackTransaction();
        	} catch (DAOException e1) {
        		LOGGER.severe("Can not rollback Transaction [" + e1.getMessage() + "]");
        	}
        	return;
        } finally {
			HibernateUtil.setCloseSession(true);
        	HibernateUtil.setBeginTransaction(true);
			HibernateUtil.closeSession();
		}
//        CalendarsController.getCalendarsController().setDirty( true );
	}

	@Override
    public void onSelect(ActionEvent event) {
    	IController addressController = AonUtil.getController(EMPLOYEE_ADDRESS_CONTROLLER_NAME);
        addressController.onCancel(event); 
        IController mediaController = AonUtil.getController(EMPLOYEE_MEDIA_CONTROLLER_NAME);
        mediaController.onCancel(event);
        super.onSelect(event);
        setResourceAllowed( false );
//	TODO Employee employee = (Employee) getTo();setUserEnabled( employee.getUser().getId() != null );
        setUserEnabled( false ); 
    }

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#accept(javax.faces.event.ActionEvent)
	 */
	@Override
	public void accept(ActionEvent event) {
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
//		CalendarsController.getCalendarsController().setDirty( true );
//        setResourceAllowed( false );
//		try {
//			getCalendarScheduleModel();
//		} catch (CalendarException e) {
//			Utils.addMessage( "aon_employee_calendar_creation_exception", true );
//            throw new AbortProcessingException(e.getMessage(), e);
//		} catch (ManagerBeanException e) {
//			Utils.addMessage( "aon_employee_calendar_creation_exception", true );
//            throw new AbortProcessingException(e.getMessage(), e);
//		}
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
    		if(iter.hasNext()){
    			((RegistryAddress)AonUtil.getController(EMPLOYEE_ADDRESS_CONTROLLER_NAME).getTo()).setGeozone((GeoZone)iter.next());
    		}
    	}
    }

//	public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
//		PlannerController planner = ControllerUtil.getPlannerController();
//		CalendarScheduleModel csm = getCalendarScheduleModel();
//		csm.setMode( ScheduleModel.WEEK );
//		planner.setMode( ScheduleModel.WEEK );
//		planner.setDate( new Date() );
//		planner.setScheduleModel(csm);
//		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
//		LinkedList<SelectItem> categories = new LinkedList<SelectItem>();
//		for (int i = 0; i < EventCategory.values().length; i++) {
//			if ( EventCategory.values()[i] != null 
//					&& EventCategory.values()[i] != EventCategory.WORK 
//					&& EventCategory.values()[i] != EventCategory.INCIDENCE) {
//				String name = EventCategory.values()[i].getName(locale);
//				SelectItem si = new SelectItem( EventCategory.values()[i], name );
//				categories.add(si);
//			}
//		}
//		planner.setCategories(categories);
//		planner.setOutcome( MANAGER_BEAN_NAME );
//		planner.setSpreadable( false );
//		planner.setEvents( new ListDataModel( new ArrayList<ScheduleEntry>() ) );
//		ControllerUtil.getWorkingTime().initialize( this.calendar, ( (Employee) getTo() ).getAgreementTime() );
//		ControllerUtil.getIncidences().initialize( this.resource, false );
//	}
//
//    /**
//     * Return Employee schedule model.
//     * 
//     * @return
//     * @throws CalendarException 
//     * @throws ManagerBeanException 
//     * @throws ExpressionException 
//     */
//	public CalendarScheduleModel getCalendarScheduleModel() throws CalendarException, ManagerBeanException {
//		Employee employee = (Employee) getTo();
//		if ( employee.getCalendar() == null ) {
//			CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
//			Resource r = (Resource) this.resource;
//			if ( r.getWorkActivity() != null ) {
////	Ask for working activity calendar, if not exist creates it and returns to the employee.
//				WorkActivityController wac = 
//					(WorkActivityController) AonUtil.getController( WorkActivityController.MANAGER_BEAN_NAME );
//				AonCalendar waCalendar = 
//					wac.getCalendarScheduleModel( r.getWorkActivity() ).getCalendar();
//				this.calendar = cmb.cloneCalendar( waCalendar );
//			} else {
////	Ask for working place calendar, if not exist creates it and returns to the employee.
//				WorkPlaceController wpc = 
//					(WorkPlaceController) AonUtil.getController( WorkPlaceController.MANAGER_BEAN_NAME );
//				AonCalendar wpCalendar = 
//					wpc.getCalendarScheduleModel( r.getWorkPlace() ).getCalendar();
//				this.calendar = cmb.cloneCalendar( wpCalendar );
//			}
//			String description = 
//				employee.getRegistry().getName() + " " + employee.getRegistry().getSurname(); 
//			this.calendar.setDescription( description );
//			this.calendar.setAddSpreadEventAllowed( true );
//			cmb.updateCalendar( this.calendar );
//			employee.setCalendar( this.calendar.getPrimaryKey() );
//			update();
//		} else {
//			this.calendar = CalendarHelper.getCalendar( employee.getCalendar() );
//		}
//        return new CalendarScheduleModel( this.calendar );
//	}

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