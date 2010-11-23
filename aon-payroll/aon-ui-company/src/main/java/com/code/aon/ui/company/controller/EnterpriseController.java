package com.code.aon.ui.company.controller;


import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.CNAE;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseActivity;
import com.code.aon.company.EnterpriseCCC;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.enumeration.CCCType;
import com.code.aon.company.enumeration.EnterpriseActivityType;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;

public class EnterpriseController extends RegistryController implements ICompanyConstants {
	
	private EnterpriseActivity activity;
	
	private RegistryAddress mainAddress;
	private WorkPlace workplace;
	private RegistryDirStaff dirStaff;
    private RegistryMedia phone;
	private RegistryMedia fax;
    private RegistryMedia email;
    private RegistryMedia web;
	
	private EnterpriseCCC ccc;
	
	private boolean activityDirty;
	
	private boolean cccDirty;
	
	private boolean treeView;
	
	private boolean showActivityNode;

    public boolean isTreeView() {
		return treeView;
	}

	public void setTreeView(boolean treeView) {
		this.treeView = treeView;
	}
	
	public boolean isShowActivityNode() {
		return showActivityNode;
	}

	public EnterpriseActivity getActivity() {
		return activity;
	}

	public void setActivity(EnterpriseActivity activity) {
		this.activity = activity;
	}
	
	public RegistryAddress getMainAddress() {
		return mainAddress;
	}
	
	public void setMainAddress(RegistryAddress mainAddress) {
		this.mainAddress = mainAddress;
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
	
	public RegistryMedia getPhone(){
    	if(phone==null){
    		phone = new RegistryMedia();
    		phone.setMediaType(MediaType.FIXED_PHONE);
    	}
    	return phone;
    }
	
    public RegistryMedia getFax(){
    	if(fax==null){
    		fax = new RegistryMedia();
    		fax.setMediaType(MediaType.FAX);
    	}
    	return fax;
    }
    
    public RegistryMedia getEmail(){
    	if(email==null){
    		email = new RegistryMedia();
    		email.setMediaType(MediaType.EMAIL);
    	}
    	return email;
    }
    
    public RegistryMedia getWeb(){
    	if(web==null){
    		web = new RegistryMedia();
    		web.setMediaType(MediaType.WEB);
    	}
    	return web;
    }
    
    public void setPhone(RegistryMedia phone) {
    	this.phone = phone;
    }
    
    public void setFax(RegistryMedia fax) {
    	this.fax = fax;
    }
    public void setEmail(RegistryMedia email) {
    	this.email = email;
    }
    public void setWeb(RegistryMedia web) {
    	this.web = web;
    }

//    public boolean isTelephone(){
//    	return !getTelephone().getValue().isEmpty();
//    }
//    public boolean isFax(){
//    	return !getFax().getValue().isEmpty();
//    }
//    public boolean isEmail(){
//    	return !getEmail().getValue().isEmpty();
//    }
//    public boolean isWeb(){
//    	return !getWeb().getValue().isEmpty();
//    }


	public EnterpriseCCC getCCC() {
		return ccc;
	}

	public void setCCC(EnterpriseCCC ccc) {
		this.ccc = ccc;
	}
	
	public boolean isActivityDirty() {
		return activityDirty;
	}

	public void setActivityDirty(boolean activityDirty) {
		this.activityDirty = activityDirty;
	}

	public boolean isCCCDirty() {
		return cccDirty;
	}

	public void setCCCDirty(boolean cccDirty) {
		this.cccDirty = cccDirty;
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
		criteria.addEqualExpression(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), enterprise.getRegistry().getId());
		criteria.addOrder(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS));
		List<ITransferObject> list = registryAddressBean.getList(criteria);
		for (ITransferObject to : list) {
			RegistryAddress rAddress = (RegistryAddress)to;
			addresses.add(new SelectItem(rAddress, rAddress.getAddress()));
		}
    	return addresses;
    }	
    
    /**
     * Gets the CCCs of the enterprise.
     * 
     * @return the CCCs of the enterprise
     * @throws ManagerBeanException 
     */
    public List<SelectItem> getCCCs() throws ManagerBeanException {
    	LinkedList<SelectItem> cccs = new LinkedList<SelectItem>();
    	Enterprise enterprise = (Enterprise) getTo();
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
		criteria.addOrder(bean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_CCC));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			EnterpriseCCC ccc = (EnterpriseCCC)to;
			cccs.add(new SelectItem(ccc, ccc.getCCC()));
		}
    	return cccs;
    }	    

	private void loadMainActivity() throws ManagerBeanException {
		Enterprise enterprise = (Enterprise) getTo();
		IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(activityBean.getFieldName(ICompanyAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
		criteria.addEqualExpression(activityBean.getFieldName(ICompanyAlias.ENTERPRISE_ACTIVITY_TYPE), EnterpriseActivityType.PRINCIPAL);
		List<ITransferObject> activities = activityBean.getList(criteria);
		if (! activities.isEmpty() ) {
			setActivity( (EnterpriseActivity) activities.get(0) );
			IManagerBean cccBean = BeanManager.getManagerBean(EnterpriseCCC.class);
			Criteria cccCriteria = new Criteria();
			cccCriteria.addEqualExpression(cccBean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_ACTIVITY_ID), getActivity().getId());
			cccCriteria.addEqualExpression(cccBean.getFieldName(ICompanyAlias.ENTERPRISE_CCC_TYPE), CCCType.PRINCIPAL);
			List<ITransferObject> cccs = cccBean.getList(cccCriteria);
			if (! cccs.isEmpty() ) {
				setCCC( (EnterpriseCCC) cccs.get(0) );
			}
			this.showActivityNode = (activities.size() > 1) || (cccs.size() > 1);
		}
	}    
	
	public void saveMainActivity() throws ManagerBeanException {
		Enterprise enterprise = (Enterprise) getTo();
		if ( isActivityDirty() ) {
			IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
			getActivity().setEnterprise(enterprise);
			activityBean.insertOrUpdate(getActivity());
			setActivityDirty(false);
		}
		if ( isCCCDirty() ) {
			IManagerBean cccBean = BeanManager.getManagerBean(EnterpriseCCC.class);	
			getCCC().setActivity(getActivity());
			if ( getCCC().getGeozone() == null ) {
				getCCC().setGeozone(getMainAddress().getGeozone());
			}
			cccBean.insertOrUpdate(getCCC());
			setCCCDirty(false);
		}
	}    	
	
	public void resetMainActivity() {
		setActivity( new EnterpriseActivity() );
		getActivity().setType( EnterpriseActivityType.PRINCIPAL );
		getActivity().setCnae( new CNAE() );
		setCCC( new EnterpriseCCC() );    		
		getCCC().setType( CCCType.PRINCIPAL );
    	setActivityDirty(false);
    	setCCCDirty(false);		
	}
    
    public void initMainActiviy() throws ManagerBeanException {
    	this.showActivityNode = false;
    	resetMainActivity();
    	if (! isNew() ) {
    		loadMainActivity();
    	}
    }
    
    private void resetMedias() {
    	Registry r = ((Enterprise)this.getTo()).getRegistry();
    	setPhone(new RegistryMedia());
    	getPhone().setMediaType(MediaType.FIXED_PHONE);
    	getPhone().setRegistry(r);
		setFax(new RegistryMedia());
		getFax().setMediaType(MediaType.FAX);
		getFax().setRegistry(r);
		setEmail(new RegistryMedia());
		getEmail().setMediaType(MediaType.EMAIL);
		getEmail().setRegistry(r);
		setWeb(new RegistryMedia());
		getWeb().setMediaType(MediaType.WEB);
		getWeb().setRegistry(r);
    }
    public void initMedias() throws ManagerBeanException {
    	resetMedias();
    	BasicController controller = (BasicController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_MEDIA_CONTROLLER_NAME);
		for(ITransferObject to: controller.getWrappedList()){
			RegistryMedia rm = (RegistryMedia)to;
			if(rm.getMediaType()==MediaType.FIXED_PHONE){
				setPhone(rm);
			} else if(rm.getMediaType()==MediaType.FAX){
				setFax(rm);
			} else if(rm.getMediaType()==MediaType.EMAIL){
				setEmail(rm);
			} else if(rm.getMediaType()==MediaType.WEB){
				setWeb(rm);
			}
		}
    }

	public void saveMedias() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
    	if(!getPhone().getValue().isEmpty()){
    		bean.insertOrUpdate(getPhone());
    	}
    	if(!getFax().getValue().isEmpty()){
    		bean.insertOrUpdate(getFax());
    	}
    	if(!getEmail().getValue().isEmpty()){
    		bean.insertOrUpdate(getEmail());
    	}
    	if(!getWeb().getValue().isEmpty()){
    		bean.insertOrUpdate(getWeb());
    	}
    }
    
    public void initMainAddress() throws ManagerBeanException {
    	BasicController controller = (BasicController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_ADDRESS_CONTROLLER_NAME);
    	if(!controller.getWrappedList().isEmpty()){
    		setMainAddress( (RegistryAddress) controller.getWrappedList().get(0));
    	} else {
    		setMainAddress(new RegistryAddress());
    		getMainAddress().setRegistry(((Enterprise)this.getTo()).getRegistry());
    	}
    }
    public void saveMainAddress() throws ManagerBeanException {
    	IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
    	if(!getMainAddress().getAddress().isEmpty()){
    		bean.insertOrUpdate(getMainAddress());
    	}
    }
    
    public void initMainWorkPlace() throws ManagerBeanException {
    	BasicController controller = (BasicController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_WORK_PLACE_CONTROLLER_NAME);
		if(!controller.getWrappedList().isEmpty()){
			setWorkplace((WorkPlace) controller.getWrappedList().get(0));
		} else {
			setWorkplace(new WorkPlace());
			getWorkplace().setEnterprise((Enterprise)this.getTo());
		}
    }
    public void saveMainWorkPlace() throws ManagerBeanException {
    	IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
    	if(!getWorkplace().getDescription().isEmpty()){
    		bean.insertOrUpdate(getWorkplace());
    	}
    }
    
    public void initMainDirStaff() throws ManagerBeanException {
    	BasicController controller = (BasicController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_DIR_STAFF_CONTROLLER_NAME);
		if(!controller.getWrappedList().isEmpty()){
			setDirStaff((RegistryDirStaff) controller.getWrappedList().get(0));
		} else {
			setDirStaff(new RegistryDirStaff());
			getDirStaff().setRegistry(((Enterprise)this.getTo()).getRegistry());
			getDirStaff().setRepresentativeLabor(true);
		}
    }
    public void saveMainDirStaff() throws ManagerBeanException {
    	IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
    	if(!getDirStaff().getDocument().isEmpty()){
    		bean.insertOrUpdate(getDirStaff());
    	}
    }
    
    public void onCNAEChanged( LookupChangeEvent event ) {
    	setActivityDirty(true);
    	if ( event.getNewValue() != null ) {
    		CNAE cnae = (CNAE) event.getNewValue();
    		getActivity().setDescription( cnae.getTitle() );
    	}
    }

    public void activityChanged( ValueChangeEvent event ) {
    	setActivityDirty(true);
    }
    
    public void cccChanged( ValueChangeEvent event ) {
    	setCCCDirty(true);
    }
    
    /*
    private GeoZone getMainGeoZone( Enterprise enterprise ) throws ManagerBeanException {
    	GeoZone geoZone = null;
		IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), enterprise.getRegistry().getId());
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
		List<ITransferObject> addresses = bean.getList(criteria);
		if (! addresses.isEmpty() ) {
			geoZone = ((RegistryAddress) addresses.get(0)).getGeozone();
		}
    	return geoZone;
    }
    */
    
    public void onTreeViewSelect(ActionEvent event){
    	setTreeView(true);
    }
    
    public void onBasicViewSelect(ActionEvent event){
    	setTreeView(false);
    }
    
    public boolean isRegistryTypeLegal(){
    	return ((Enterprise)this.getTo()).getRegistry().getType()==RegistryType.LEGAL;
    }
    
    
    
}