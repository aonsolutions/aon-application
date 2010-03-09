package com.code.aon.ui.company.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the company maintenance.
 */
public class CompanyParentController extends BasicController implements ICompanyController {
	
	public static final String COMPANY_NAME = "company";
	
	public static final String printHeaderParam = "APP_PRINT_HEADER_PARAM";
	
	public static final String printRecordDataParam = "APP_PRINT_RECORD_DATA_PARAM";

	/** The attach. */
	private RegistryAttachment attach;
	
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

	private boolean printHeader;
	
	private boolean printRecordData;
	
    /**
     * The empty constructor.
     * 
     * @throws ManagerBeanException the manager bean exception
     */
    public CompanyParentController() throws ManagerBeanException {
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
	public RegistryAttachment getAttach() {
		return attach;
	}


	/**
	 * Sets the RegistryAttach.
	 * 
	 * @param attach the attachment
	 */
	public void setAttach(RegistryAttachment attach) {
		this.attach = attach;
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
	 * Checks if an image is attached.
	 * 
	 * @return true, if an image is attached
	 */
	public boolean isImageAttached(){
		if(this.attach != null){
			return true;
		}
		return false;
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
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onLoad(MenuEvent event){
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
				onSelect(null);
				loadMainAddress();
			}else{
				this.onReset(null);
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
	
	public boolean isWithLogo() throws ManagerBeanException {
		return !(obtainCompanyLogo() == null);
	}
	
	/**
	 * Gets the attach as input stream.
	 * 
	 * @return the attach as input stream
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws IOException the IO exception
	 */
	public InputStream getAttachAsInputStream() throws IOException, ManagerBeanException{
		File file = File.createTempFile("image", ".tmp");
		
		RegistryAttachment attach = obtainCompanyLogo();
		if(attach != null){
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(attach.getData());
			outputStream.close();
			return new FileInputStream(file);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public RecordData getCompanyRecordData() throws IOException, ManagerBeanException{
		IManagerBean recordDataBean = BeanManager.getManagerBean(RecordData.class);
		Company company = (Company)getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(recordDataBean.getFieldName(IRegistryAlias.RECORD_DATA_REGISTRY_ID), company.getId());
		Iterator iter = recordDataBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (RecordData)iter.next();
		}
		return null;
	}

	/**
	 * Obtains company logo.
	 * 
	 * @return the registry attachment
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unchecked")
	private RegistryAttachment obtainCompanyLogo() throws ManagerBeanException {
		if(this.getTo() == null){
			this.onLoad();
		}
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, ((Company)this.getTo()).getId());
		Iterator iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
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
	
	public boolean isPrintHeader() {
		return printHeader;
	}

	public void setPrintHeader(boolean printHeader) {
		this.printHeader = printHeader;
	}

	public boolean isPrintRecordData() {
		return printRecordData;
	}

	public void setPrintRecordData(boolean printRecordData) {
		this.printRecordData = printRecordData;
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
	
	/**
	 * If the company has been defined navigates to the homepage, 
	 * otherwise navigates to the company maintenance.
	 * 
	 * @return The url of the page that will be loaded
	 * 
	 * @throws ManagerBeanException
	 */
	public String getCompanyNavigation() throws ManagerBeanException {
		String context = 
			FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		if ( this.getModel().getRowCount() > 0 ) {
			return context + "/facelet/homepage/firstContent.faces";
		}
		this.onLoad();
		return context + "/facelet/registry/company/init.faces";
	}

	/** COMPANY_ADDRESS_CONTROLLER_NAME. */
	public static final String COMPANY_ADDRESS_CONTROLLER_NAME = "companyAddress";

	public boolean obtainPrintHeader() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(printHeaderParam);
		return (appParam == null?false:new Boolean(appParam.getValue()).booleanValue());
	}

	public boolean obtainPrintRecordData() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(printRecordDataParam);
		return (appParam == null?false:new Boolean(appParam.getValue()).booleanValue());
	}

	@SuppressWarnings("unchecked")
	public ApplicationParameter obtainApplicationParameter(String paramName) throws ManagerBeanException{
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), paramName);
		Iterator iter = appParamBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (ApplicationParameter)iter.next();
		}
		return null;
	}
}