package com.code.aon.ui.company.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.calendar.controller.CalendarController;

public class EnterpriseController extends RegistryController implements ICompanyConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private RegistryInfo info = new RegistryInfo();

	private WorkPlace workplace;
	
	private RegistryDirStaff dirStaff;
	
	private boolean skipResetButton;
	
	private boolean skipRemoveButton;
	
	public boolean isSkipResetButton() {
		return skipResetButton;
	}

	public void setSkipResetButton(boolean skipResetButton) {
		this.skipResetButton = skipResetButton;
	}

	public boolean isSkipRemoveButton() {
		return skipRemoveButton;
	}

	public void setSkipRemoveButton(boolean skipRemoveButton) {
		this.skipRemoveButton = skipRemoveButton;
	}

	public RegistryAddress getMainAddress() {
		return info.getAddress();
	}
	
	public WorkPlace getWorkplace() {
		return workplace;
	}

	public void setWorkplace(WorkPlace workplace) {
		this.workplace = workplace;
	}

	public RegistryDirStaff getDirStaff() {
		return dirStaff;
	}

	public void setDirStaff(RegistryDirStaff dirStaff) {
		this.dirStaff = dirStaff;
	}
	    
    public RegistryMedia getPhone() {
		return info.getPhone();
	}

	public RegistryMedia getFax() {
		return info.getFax();
	}

	public RegistryMedia getEmail() {
		return info.getEmail();
	}

	public RegistryMedia getWeb() {
		return info.getWeb();
	}

	/**
     * Gets the addresses of the enterprise.
     * 
     * @return the addresses of the enterprise
     * @throws ManagerBeanException 
     */
    public List<SelectItem> getAddresses() throws ManagerBeanException {
    	LinkedList<SelectItem> addresses = new LinkedList<SelectItem>();
    	Enterprise enterprise = (Enterprise) getTo();
		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), enterprise.getRegistry().getId());
		criteria.addOrder(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS));
		List<ITransferObject> list = registryAddressBean.getList(criteria);
		for (ITransferObject to : list) {
			RegistryAddress rAddress = (RegistryAddress)to;
			addresses.add(new SelectItem(rAddress, rAddress.getShortAddress()));
		}
    	return addresses;
    }	
    
	public void reset() {
    	setWorkplace(null);
    	setDirStaff(null);
    	this.info.reset();
    	this.setSelectedTab(null);
	}
    
    public Enterprise getEnterprise() {
    	return (Enterprise) getTo();
    }
    
    public void initRegistryInfo() throws ManagerBeanException {
    	this.info.init( getEnterprise().getRegistry() );
    }
    
    public void saveMainAddress() throws ManagerBeanException {
    	IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
    	if(! StringUtils.isEmpty(getMainAddress().getAddress()) ){
    		bean.insertOrUpdate(getMainAddress());
    	}
    }
    
    public void initMainWorkPlace() throws ManagerBeanException {
    	WorkPlace workPlace = null;
    	IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId() );
    	if ( (getMainAddress() != null) && (getMainAddress().getId() != null) ) {
    		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_PLACE_ADDRESS_ID), getMainAddress().getId() );
    	}
    	List<ITransferObject> list = bean.getList(criteria);
    	if (! list.isEmpty() ) {
    		workPlace = (WorkPlace) list.get(0);	
    	}
		setWorkplace(workPlace);
    }
    
    public void initMainDirStaff() throws ManagerBeanException {
    	RegistryDirStaff dirStaff = null;
    	IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID), getEnterprise().getRegistry().getId() );
    	criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REPRESENTATIVE_LABOR), true );
    	List<ITransferObject> list = bean.getList(criteria);
    	if (! list.isEmpty() ) {
    		dirStaff = (RegistryDirStaff) list.get(0);	
    	}
    	setDirStaff(dirStaff);
    }
    
    /**
     * this method is no longer necessary
     * @param event
     */
    @Deprecated
    public void onTreeViewSelect(ActionEvent event){
    }
    
    /**
     * this method is no longer necessary
     * @param event
     */
    @Deprecated
    public void onBasicViewSelect(ActionEvent event){
    }
    
    public boolean isRegistryTypeLegal(){
    	return ((Enterprise)this.getTo()).getRegistry().getType()==RegistryType.LEGAL;
    }
 
	public void onLoadCalendar( ActionEvent event ) {
		// TODO implementar la busqueda del calendario. si la entidad no tiene calendario, 
		// buscar el calendario en sus entidades superiores: contract -> workplace -> enterprise -> agreement
		Enterprise e =(Enterprise)getTo();
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(ICompanyConstants.CALENDAR_CONTROLLER_NAME);
		controller.setEnterpriseName(e.getRegistry().getFullName());
		controller.setCalendarId(e.getCalendar().getId());
		controller.onInitialize(event);
	}	
	
	public void onActivate(ActionEvent event) {
		Enterprise enterprise = (Enterprise) getTo();
		DomainSwitcher switcher = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		switcher.select(enterprise.getDomain(), null );
	}
	
	public boolean isDomainEnterprise() {
		DomainSwitcher switcher = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return (switcher.isParentDomain() && switcher.isDomainManagementAvailable());
	}
	
	
}