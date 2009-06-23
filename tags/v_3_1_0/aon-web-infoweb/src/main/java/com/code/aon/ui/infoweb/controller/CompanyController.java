package com.code.aon.ui.infoweb.controller;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.config.User;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class CompanyController extends FileController {

	public static final String COMPANY_NAME = "company";
	
	/** COMPANY_ADDRESS_CONTROLLER_NAME. */
	public static final String COMPANY_ADDRESS_CONTROLLER_NAME = "companyAddress";
	
	/** The attach. */
	private RegistryAttachment attachment;
	
	/** The phone. */
	private RegistryMedia phone;

	/** The fax. */
	private RegistryMedia fax;

	/** The email. */
	private RegistryMedia email;

	/** The web. */
	private RegistryMedia web;
	
	/** The main address. */
	private RegistryAddress mainAddress;

	/** Determines if the address has been changed and it hasn't been saved yet. */
	private boolean addressDirty;

	/** Determines if the phone has been changed and it hasn't been saved yet. */
	private boolean phoneDirty;

	/** Determines if the fax has been changed and it hasn't been saved yet. */
	private boolean faxDirty;

	/** Determines if the email has been changed and it hasn't been saved yet. */
	private boolean emailDirty;

	/** Determines if the web has been changed and it hasn't been saved yet. */
	private boolean webDirty;
	

	public CompanyController() {
		super();
	}
	
	/**
     * Gets the company label.
     * 
     * @return the company label
     */
    public String getCompanyLabel(){
    	Company c = obtainCompany();
    	return c.getName();
    }
    
	/**
	 * Gets the RegistryAttach.
	 * 
	 * @return the attachment
	 */
	public RegistryAttachment getAttachment() {
		return attachment;
	}

	public static String getCOMPANY_ADDRESS_CONTROLLER_NAME() {
		return COMPANY_ADDRESS_CONTROLLER_NAME;
	}

	/**
	 * Sets the RegistryAttach.
	 * 
	 * @param attach the attachment
	 */
	public void setAttachment(RegistryAttachment attachment) {
		this.attachment = attachment;
	}

	/**
	 * Checks if is the address is dirty.
	 * 
	 * @return true, if the address is dirty
	 */
	public boolean isAddressDirty() {
		return addressDirty;
	}

	/**
	 * Checks if the email is dirty.
	 * 
	 * @return true, if the email is dirty
	 */
	public boolean isEmailDirty() {
		return emailDirty;
	}

	/**
	 * Checks if the fax is dirty.
	 * 
	 * @return true, if the fax is dirty
	 */
	public boolean isFaxDirty() {
		return faxDirty;
	}

	/**
	 * Checks if the phone is dirty.
	 * 
	 * @return true, if the phone is dirty
	 */
	public boolean isPhoneDirty() {
		return phoneDirty;
	}

	/**
	 * Checks if the web is dirty.
	 * 
	 * @return true, if the web is dirty
	 */
	public boolean isWebDirty() {
		return webDirty;
	}

	/**
	 * Gets the phone.
	 * 
	 * @return the phone
	 */
	public RegistryMedia getPhone() {
		return phone;
	}

	/**
	 * Sets the phone.
	 * 
	 * @param phone the phone
	 */
	public void setPhone(RegistryMedia phone) {
		this.phone = phone;
	}

	/**
	 * Gets the email.
	 * 
	 * @return the email
	 */
	public RegistryMedia getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 * 
	 * @param email the email
	 */
	public void setEmail(RegistryMedia email) {
		this.email = email;
	}

	/**
	 * Gets the fax.
	 * 
	 * @return the fax
	 */
	public RegistryMedia getFax() {
		return fax;
	}

	/**
	 * Sets the fax.
	 * 
	 * @param fax the fax
	 */
	public void setFax(RegistryMedia fax) {
		this.fax = fax;
	}

	/**
	 * Gets the web.
	 * 
	 * @return the web
	 */
	public RegistryMedia getWeb() {
		return web;
	}

	/**
	 * Sets the web.
	 * 
	 * @param web the web
	 */
	public void setWeb(RegistryMedia web) {
		this.web = web;
	}
	
    /**
     * Gets the main address.
     * 
     * @return the main address
     */
    public RegistryAddress getMainAddress() {
		return mainAddress;
	}

	/**
	 * Sets the main address.
	 * 
	 * @param mainAddress the main address
	 */
	public void setMainAddress(RegistryAddress mainAddress) {
		this.mainAddress = mainAddress;
	}

	/**
	 * On accept. Resets the flags of dirty
	 * 
	 * @param event the event
	 * 
	 * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
	 */
	public void onAccept(ActionEvent event) {
		accept(event);
		resetDirty();
	}

	/**
	 * On load.
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onLoad(ActionEvent event) {
		onLoad();
	}
	
	/**
	 * On load.
	 */
	private void onLoad(){
		try {
			initializeModel();
			if(this.getModel().getRowCount() > 0){
				this.getModel().setRowIndex(0);
				super.onSelect(null);
				loadMainAddress();
			}else{
				super.onReset(null);
				initControllerData();
				this.mainAddress = new RegistryAddress();
				this.mainAddress.setRegistry(new Registry());
				this.mainAddress.setGeozone(new GeoZone());
				this.mainAddress.setAddressType(AddressType.MAIN);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		resetDirty();		
	}
	
	/**
	 * Reset all the flags.
	 */
	private void resetDirty(){
		addressDirty = false;
		phoneDirty = false;
		faxDirty = false;
		emailDirty = false;
		webDirty = false;
	}

	/**
	 * Phone changed. Sets the flag phoneDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void phoneChanged(ValueChangeEvent event) throws ManagerBeanException {
		phoneDirty = true;
	}

	/**
	 * Fax changed. Sets the flag faxDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void faxChanged(ValueChangeEvent event) throws ManagerBeanException {
		faxDirty = true;
	}

	/**
	 * Email changed. Sets the flag emailDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void emailChanged(ValueChangeEvent event) throws ManagerBeanException {
		emailDirty = true;
	}

	/**
	 * Web changed. Sets the flag webDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void webChanged(ValueChangeEvent event) throws ManagerBeanException {
		webDirty = true;
	}

	/**
	 * Address changed. Sets the flag addressDirty = true
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void addressChanged(ValueChangeEvent event) throws ManagerBeanException {
		addressDirty = true;
	}

	/**
	 * On addresses. Loads the addresses of the company
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onAddresses(ActionEvent event){
		loadAddresses();
	}

	/**
	 * Loads the addresses of the company.
	 */
	public void loadAddresses(){
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			IController master = this;
			IController detail = getDetailController();
			ITransferObject to = master.getTo();
			String reg = detail.getFieldName(getMasterFieldName());
			Criteria criteria = new Criteria();
			Serializable id = master.getManagerBean().getId( to );			
			criteria.addEqualExpression(reg, id);
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.DELEGATION);
			detail.setCriteria(criteria);
			detail.initializeModel();
		} catch (ManagerBeanException e) {
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void loadMainAddress() throws ManagerBeanException {
		Integer id = ((Company)this.getModel().getRowData()).getId();
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
		Iterator iter = rAddressBean.getList(criteria).iterator();
		if(iter.hasNext()){
			this.mainAddress = (RegistryAddress)iter.next();
		}else{
			this.mainAddress = new RegistryAddress();
			this.mainAddress.setRegistry(((Company)this.getModel().getRowData()));
			this.mainAddress.setGeozone(new GeoZone());
			this.mainAddress.setAddressType(AddressType.MAIN);
		}
	}
	
	/**
	 * Obtains the address.
	 * 
	 * @return the registry address
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	public RegistryAddress obtainAddress() throws ManagerBeanException{
		if(this.getTo() == null){
			this.onLoad();
		}
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), ((Registry)this.getTo()).getId());
		Iterator iter = rAddressBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAddress)iter.next();
		}
		return null;
	}
	
	/**
	 * Inits the controller data.
	 */
	private void initControllerData() {
		this.email = new RegistryMedia();
		this.phone = new RegistryMedia();
		this.fax = new RegistryMedia();
		this.web = new RegistryMedia();
	}
	
	/**
	 * Gets the detail controller.
	 * 
	 * @return the detail controller
	 */
	public IController getDetailController() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Application app = ctx.getApplication();
		ValueBinding vb = app.createValueBinding("#{" + getChildBean() + "}");
		return (IController) vb.getValue(ctx);
	}
	
	/**
	 * Obtains the company.
	 * 
	 * @return the company
	 */
	public Company obtainCompany(){
		if(this.getTo() == null){
			this.onLoad();
		}
		return (Company)this.getTo();
	}
	
	/**
	 * Obtains the phone.
	 * 
	 * @return the registry media
	 */
	public RegistryMedia obtainPhone(){
		if(this.getPhone() == null){
			this.onLoad();
		}
		return this.getPhone();
	}

	/**
	 * Obtains the fax.
	 * 
	 * @return the registry media
	 */
	public RegistryMedia obtainFax(){
		if(this.getFax() == null){
			this.onLoad();
		}
		return this.getFax();
	}
	
	/**
	 * Retrieves the whole <code>GeoZone</code> object when the lookup field changes.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	public void onChangeGeoZone(ValueChangeEvent event) throws ManagerBeanException {
    	if(event.getNewValue() != null){
    		IManagerBean geoZoneBean = BeanManager.getManagerBean(GeoZone.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(geoZoneBean.getFieldName(IGeoZoneAlias.GEO_ZONE_ID), event.getNewValue());
    		Iterator iter = geoZoneBean.getList(criteria).iterator();
    		if(iter.hasNext()){
    			((RegistryAddress)AonUtil.getController(COMPANY_ADDRESS_CONTROLLER_NAME).getTo()).setGeozone((GeoZone)iter.next());
    		}
    	}
    }
	
	public String getCompanyNavigation() throws ManagerBeanException {
		if(this.mainAddress == null){
			this.onLoad();
		}
		return "/facelet/registry/company/form.xhtml";
	}
	
	@Override
	public void updateFile(String fileName, byte[] data, MimeType mimeType) {
		RegistryAttachment attach = getAttachment();
		attach.setData(data);
		attach.setMimeType(mimeType);
	}
	
	/**
	 * Gets the child bean.
	 * 
	 * @return the child bean
	 */
	public String getChildBean() {
		return COMPANY_ADDRESS_CONTROLLER_NAME;
	}

	/**
	 * Gets the master field name.
	 * 
	 * @return the master field name
	 */
	public String getMasterFieldName(){
		return IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID;
	}

    @SuppressWarnings("unchecked")
    public String getCompanyName() throws ManagerBeanException {
        try {
	    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
	        List companyList = companyBean.getList(null);
	        if (companyList.size() > 0) {
	            Company company = (Company)companyList.get(0);
	            return company.getName();
	        }
	        return null;
        }
        catch (Exception e) {
        	return null;
        }
    }

    public String getLoggedUserName() {
    	try {
    		UserUtils uu = new UserUtils();
	        User user = uu.getLoggedUser();
	        return user.getName();
    	}
    	catch (Exception e) {
    	}
    	return null;
    }

    
    public String getCurrentDate() {
        DateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");

        return formatter.format(new Date()).toUpperCase();
    }

}