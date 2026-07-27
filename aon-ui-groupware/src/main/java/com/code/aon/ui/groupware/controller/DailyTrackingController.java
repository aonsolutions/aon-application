package com.code.aon.ui.groupware.controller;

import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.customer.Customer;
import com.code.aon.groupware.DailyTracking;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class DailyTrackingController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DailyTrackingController.class);
	
	public final static int CUSTOMER_TYPE = 0;
	public final static int COMPANY_TYPE = 1;
	public final static int GLOBAL_TYPE = 2;

	private boolean minuteModeEnabled = true;
	private boolean monitor;
	private List<SelectItem> projects;
	private List<SelectItem> activityTypes;
	private int registryType;
	private Customer customer;

	
	public boolean isMinuteModeEnabled() {
		return minuteModeEnabled;
	}
	public void setMinuteModeEnabled(boolean minuteModeEnabled) {
		this.minuteModeEnabled = minuteModeEnabled;
	}

	public boolean isMonitor() {
		return monitor;
	}
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}
	
	public int getRegistryType() {
		return registryType;
	}

	public void setRegistryType(int registryType) {
		this.registryType = registryType;
	}

	public void onSwicthMonitor(ActionEvent event) {
		onRefresh(event);
	}

	public void onRefresh(ActionEvent event) {
		super.onSearch(event);
	}

	public void onRegistryChanged(LookupChangeEvent event) {
		Registry registry = (Registry) event.getNewValue();
		registryChanged(registry);
		updateRegistrySelection(registry);
	}

	private void registryChanged( Registry registry ) {
		try {
			if (registry == null || registry.getId() == null) {
				getCurrent().setProject(null);
				loadProjects(null);
			} else {
				loadProjects(registry.getId());
			}
			Integer projectTypeId = null;
			if (getCurrent() != null && getCurrent().getProject() != null && getCurrent().getProject().getProjectType() != null) {
				projectTypeId = getCurrent().getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar la lista de proyectos.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onChangeProject(ActionEvent event) {
		try {
			Registry registry = null;
			if (getCurrent().getProject() != null) {
				registry = getCurrent().getProject().getRegistry();
			} else {
				registry = (Registry) BeanManager.getManagerBean(Registry.class).createNewTo();
			}
			getCurrent().setRegistry(registry);
			updateRegistrySelection(registry);
			LookupChangeEvent e = new LookupChangeEvent(event.getComponent(), registry);
			onRegistryChanged(e);
			Integer projectTypeId = null;
			if (getCurrent() != null && getCurrent().getProject() != null && getCurrent().getProject().getProjectType() != null) {
				projectTypeId = getCurrent().getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar registry.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public List<SelectItem> getProjects() throws ManagerBeanException {
		DailyTracking dt = (DailyTracking) this.getTo();
		if(dt.getRegistry() != null) {
			loadProjects(dt.getRegistry().getId());
		} else loadProjects(null);
		return projects;
	}

	public void loadProjects(Integer registryId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
		AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		projects = pcc.getProjects( registryId);
	}
	
	public List<SelectItem> getActivityTypes() throws ManagerBeanException {
		DailyTracking dt = (DailyTracking) this.getTo();
		if(dt != null && dt.getProject() != null && dt.getProject().getProjectType() != null) {
			loadActivityTypes(dt.getProject().getProjectType().getId());
		} else loadActivityTypes(null);
		return activityTypes;
	}
	public void loadActivityTypes(Integer projectTypeId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
			AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		if (projectTypeId != null) {
			activityTypes = pcc.getActivityTypes( projectTypeId );
		} else {
			activityTypes = new LinkedList<SelectItem>();	
		}
	}
	
	private DailyTracking getCurrent() {
		return (DailyTracking) getTo();
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void onCustomerChanged(LookupChangeEvent event) {
		Customer customer = (Customer) event.getNewValue();
		Registry registry = (customer!=null)?customer.getRegistry():null; 
		registryChanged( registry );
		getCurrent().setRegistry( registry );
	}
	
	public Company getCompany() {
		CompanyController cc = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
		return cc.obtainCompany();		
	}

	public void onRegistryTypeChanged( ActionEvent event ) throws ManagerBeanException {
		switch ( getRegistryType() ) {
			case CUSTOMER_TYPE:
				setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
				getCurrent().setRegistry(getCustomer().getRegistry());
				break;
			case COMPANY_TYPE:
				Company company = getCompany();
				registryChanged( company );
				getCurrent().setRegistry(company);
				break;
		}
	}
	
	private Customer getCustomer( Registry registry ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Customer.class);
		return (Customer) bean.get(registry.getId());
	}
	
	public void updateRegistrySelection( Registry registry ) {
		setRegistryType(GLOBAL_TYPE);
		if ( (registry != null) && (registry.getId() != null) ) {
			try {
				Customer customer = getCustomer(registry);
				if ( customer != null ) {
					setRegistryType(CUSTOMER_TYPE);
					setCustomer(customer);
				} else {
					Company company = getCompany();
					if ( ObjectUtils.equals(registry.getId(), company.getId()) ) {
						setRegistryType(COMPANY_TYPE);				
					}
				}
			} catch ( ManagerBeanException e ) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
}