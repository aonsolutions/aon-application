package com.code.aon.ui.company.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the company maintenance.
 */
public class CompanyParentController extends BasicController implements ICompanyController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyParentController.class.getName());
	
	public static final String PRINT_HEADER_PARAM = "APP_PRINT_HEADER_PARAM";
	
	public static final String PRINT_RECORD_DATA_PARAM = "APP_PRINT_RECORD_DATA_PARAM";
	
	public static final String SALE_INVOICE_TEMPLATE_PARAM = "APP_SALE_INVOICE_TEMPLATE_PARAM";
	
	public static final String PRINT_LOGO_PARAM = "APP_PRINT_LOGO_PARAM";
	
	public static final String PRINT_NAME_PARAM = "APP_PRINT_NAME_PARAM";
	
	public static final String PRINT_NIF_PARAM = "APP_PRINT_NIF_PARAM";
	
	public static final String PRINT_ADDRESS_PARAM = "APP_PRINT_ADDRESS_PARAM";
	
	public static final String PRINT_INTERNET_DATA_PARAM = "APP_PRINT_INTERNET_DATA_PARAM";
	
	public static final String SMART_CARD_PARAM = "APP_SMART_CARD_PARAM";
	
	public static final String SALE_INVOICE_REPORT_KEY_PARAM = "REPORT_saleInvoice";
	
	public static final String SALE_INVOICE_REPORT_KEY = "saleInvoice";
	
	public static final String INVOICE_PRINT_REPORT_KEY = "invoicePrint";
	
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
	
	private SaleInvoiceTemplate saleInvoiceTemplate;
	
	private boolean printLogo;
	
	private ReportPrintOption printName;
	
	private ReportPrintOption printNif;
	
	private ReportPrintOption printAddress;
	
	private ReportPrintOption printInternetData;
	
	private boolean smartCard;
	
	private boolean customReportTemplate;

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
				onSelect(null);
				loadMainAddress();
			}else{
				this.onReset(null);
				((Company)this.getTo()).setDocumentType(DocumentType.CIF);
				initControllerData();
				this.mainAddress = new RegistryAddress();
				this.mainAddress.setRegistry(new Registry());
				this.mainAddress.setAddressType(AddressType.MAIN);
				this.mainAddress.setStreetType(StreetType.CL);
				this.mainAddress.setGeozone(new GeoZone());
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
	public void addressChanged(ValueChangeEvent event) throws ManagerBeanException {
		addressDirty = true;
	}

	private void loadMainAddress() throws ManagerBeanException {
		Company company = (Company)this.getModel().getRowData();
		this.mainAddress = RegistryInfo.getMainAddress(company);
		if (this.mainAddress == null) {
			this.mainAddress = new RegistryAddress();
			this.mainAddress.setRegistry(company);
			this.mainAddress.setAddressType(AddressType.MAIN);			
			this.mainAddress.setStreetType(StreetType.CL);
			this.mainAddress.setGeozone(new GeoZone());
		} else if (this.mainAddress.getStreetType() == null) {
			this.mainAddress.setStreetType(StreetType.CL);
		}
	}
	
	/**
	 * Obtains the address.
	 * 
	 * @return the registry address
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public RegistryAddress obtainAddress() throws ManagerBeanException{
		if(this.getTo() == null){
			this.onLoad();
		}
		IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), ((Registry)this.getTo()).getId());
		criteria.addOrder(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE));
		Iterator<ITransferObject> iter = rAddressBean.getList(criteria).iterator();
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
	

	public boolean isWithLogo() throws ManagerBeanException {
		Company company = obtainCompany();
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, company.getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		return registryAttachBean.getCount(criteria) > 0;
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
		RegistryAttachment attach = obtainCompanyLogo();
		if(attach != null){
			return new ByteArrayInputStream(attach.getData());
		}
		return null;
	}

	public RecordData getCompanyRecordData() throws ManagerBeanException{
		IManagerBean recordDataBean = BeanManager.getManagerBean(RecordData.class);
		Company company = (Company)getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(recordDataBean.getFieldName(IEntityAlias.RECORD_DATA_REGISTRY_ID), company.getId());
		Iterator<ITransferObject> iter = recordDataBean.getList(criteria, 0, 1).iterator();
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
	public RegistryAttachment obtainCompanyLogo() throws ManagerBeanException {
		if(this.getTo() == null){
			this.onLoad();
		}
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, ((Company)this.getTo()).getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.LOGO);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
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
	
	/**
	 * Obtains the web.
	 * 
	 * @return the registry media
	 */
	public RegistryMedia obtainWeb(){
		if(this.getWeb() == null){
			this.onLoad();
		}
		return this.getWeb();
	}
	
	/**
	 * Obtains the email.
	 * 
	 * @return the registry media
	 */
	public RegistryMedia obtainEmail(){
		if(this.getEmail()== null){
			this.onLoad();
		}
		return this.getEmail();
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
	
	public SaleInvoiceTemplate getSaleInvoiceTemplate(){
		return saleInvoiceTemplate;
	}
	
	public void setSaleInvoiceTemplate(SaleInvoiceTemplate saleInvoiceTemplate){
		this.saleInvoiceTemplate = saleInvoiceTemplate;
	}
	
	public String getSaleInvoiceTemplateValue(){
		return saleInvoiceTemplate==null?SaleInvoiceTemplate.DEFAULT.getValue():saleInvoiceTemplate.getValue();
	}
	
	public String getInvoicePrintTemplateValue(){
		return saleInvoiceTemplate==null?INVOICE_PRINT_REPORT_KEY:saleInvoiceTemplate.getValue().replaceFirst(SALE_INVOICE_REPORT_KEY, INVOICE_PRINT_REPORT_KEY);
	}
	
	public boolean isPrintLogo() {
		return printLogo;
	}

	public void setPrintLogo(boolean printLogo) {
		this.printLogo = printLogo;
	}

	public ReportPrintOption getPrintName() {
		return printName;
	}

	public void setPrintName(ReportPrintOption printName) {
		this.printName = printName;
	}

	public ReportPrintOption getPrintNif() {
		return printNif;
	}

	public void setPrintNif(ReportPrintOption printNif) {
		this.printNif = printNif;
	}

	public ReportPrintOption getPrintAddress() {
		return printAddress;
	}

	public void setPrintAddress(ReportPrintOption printAddress) {
		this.printAddress = printAddress;
	}

	public ReportPrintOption getPrintInternetData() {
		return printInternetData;
	}

	public void setPrintInternetData(ReportPrintOption printInternetData) {
		this.printInternetData = printInternetData;
	}

	public boolean isSmartCard() {
		return smartCard;
	}

	public void setSmartCard(boolean smartCard) {
		this.smartCard = smartCard;
	}
	
	public boolean isCustomReportTemplate() {
		return customReportTemplate;
	}

	public void setCustomReportTemplate(boolean customReportTemplate) {
		this.customReportTemplate = customReportTemplate;
	}

	/**
	 * Gets the child bean.
	 * 
	 * @return the child bean
	 */
	public String getChildBean() {
		return ICompanyConstants.COMPANY_ADDRESS_CONTROLLER_NAME;
	}

	/**
	 * Gets the master field name.
	 * 
	 * @return the master field name
	 */
	public String getMasterFieldName(){
		return IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID;
	}
	
	public void setHideHeaderContent( boolean value ) {
		ConfigurationController cc = AonUtil.getConfigurationController();
		cc.getProperties().put( ICommonConstants.HIDE_HEADER_LINKS, value );
		cc.getProperties().put( ICommonConstants.HIDE_MENU_BAR, value );
		if ( cc.getBean() != null ) {
			Map<String,Object> map = cc.getBean().get(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			if ( map != null ) {
				map.put(ICompanyConstants.SHOW_PANEL_TAB_SET, !value);	
			}			
		}
	}

	public boolean isHideHeaderContent() {
		if ( getTo() == null ) {
			this.onLoad();
		}
		setHideHeaderContent(isNew());
		return isNew();
	}	

	public boolean obtainPrintHeader() throws ManagerBeanException { 
		ApplicationParameter appParam = obtainApplicationParameter(PRINT_HEADER_PARAM);
		return (appParam == null?false:new Boolean(appParam.getValue()).booleanValue());
	}

	public boolean obtainPrintRecordData() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(PRINT_RECORD_DATA_PARAM);
		return (appParam == null?false:new Boolean(appParam.getValue()).booleanValue());
	}
	
	public SaleInvoiceTemplate obtainSaleInvoiceTemplate() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(SALE_INVOICE_TEMPLATE_PARAM);
		return (appParam == null?null:SaleInvoiceTemplate.getEnumByValue(appParam.getValue()));
	}
	
	public boolean obtainPrintLogo() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(PRINT_LOGO_PARAM);
		return (appParam == null?false:new Boolean(appParam.getValue()).booleanValue());
	}
	
	public ReportPrintOption obtainPrintName() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(PRINT_NAME_PARAM);
		return getReportPrintOptionValue(appParam);
	}
	
	public ReportPrintOption obtainPrintNif() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(PRINT_NIF_PARAM);
		return getReportPrintOptionValue(appParam);
	}
	
	public ReportPrintOption obtainPrintAddress() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(PRINT_ADDRESS_PARAM);
		return getReportPrintOptionValue(appParam);
	}
	
	public ReportPrintOption obtainPrintInternetData() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(PRINT_INTERNET_DATA_PARAM);
		return getReportPrintOptionValue(appParam);
	}
	
	public boolean obtainSmartCard() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(SMART_CARD_PARAM);
		return (appParam == null?false:new Boolean(appParam.getValue()).booleanValue());
	}
	
	public void searchCustomReportTemplate() throws ManagerBeanException {
		ApplicationParameter appParam = obtainApplicationParameter(SALE_INVOICE_REPORT_KEY_PARAM);
		setCustomReportTemplate(appParam != null);
	}

	private ReportPrintOption getReportPrintOptionValue(ApplicationParameter appParam) {
		return appParam == null?null:(appParam.getValue() == null?null:ReportPrintOption.values()[Integer.parseInt(appParam.getValue())]);
	}
	
	public ApplicationParameter obtainApplicationParameter(String paramName) throws ManagerBeanException{
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), paramName);
		Iterator<ITransferObject> iter = appParamBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (ApplicationParameter)iter.next();
		}
		return null;
	}

	/**
	 * Checks if is e invoice.
	 * 
	 * @return true, if is e invoice
	 */
	public boolean isEInvoice() {
		if(this.getTo() == null){
			this.onLoad();
		}
		return ( (Company) getTo()).isEInvoice();
	}

	public void onChangeDocument(ActionEvent event) {
		try {
			if (isNew()) {
				Company company = (Company) getTo();
				RegistryController.validateDocument(company, getPojoShortName(), getManagerBean());
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("unable to check Document.",e);
		}
	}	
	
}