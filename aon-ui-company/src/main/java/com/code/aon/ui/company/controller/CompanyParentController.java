package com.code.aon.ui.company.controller;

import static com.code.aon.common.enumeration.AppParam.APP_FPAYMENT_TEMPLATE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_ITEM_TAG_BARCODE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_ITEM_TAG_TEMPLATE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_ITEM_TAG_TEXT_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_MANUFACT_TEMPLATE_TAG_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_ADDRESS_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_DISCOUNT_PRICE_APPLIED;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_HEADER_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_INTERNET_DATA_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_LOGO_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_NAME_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_NIF_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PRODUCT_CODE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_RECORD_DATA_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PROJECT_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_REFERENCE_CODE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_S_INVOICE_FOOTER_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_SMART_CARD_PARAM;
import static com.code.aon.ui.company.controller.ICompanyConstants.INVOICE_PRINT_REPORT_KEY;
import static com.code.aon.ui.company.controller.ICompanyConstants.SALE_INVOICE_REPORT_KEY;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.Company;
import com.code.aon.company.enumeration.FinancePaymentTemplate;
import com.code.aon.company.enumeration.ItemTagTemplate;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Domain;
import com.code.aon.config.Scope;
import com.code.aon.config.Tag;
import com.code.aon.config.UserScope;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.jaas.auth.AuthPrincipal;
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
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the company maintenance.
 */
public class CompanyParentController extends BasicController implements ICompanyController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyParentController.class.getName());
	
	/** The logo attach. */
	private RegistryAttachment logoAttach;

	/** The signature attach. */
	private RegistryAttachment signatureAttach;
	
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
	
	private ApplicationParameter customSaleInvoiceTemplateParam;
	
	private boolean printDiscountPriceApplied;
	
	private boolean printLogo;

	private boolean printProductCode;
	
	private boolean printReferenceCode;
	
	private boolean printProject;
	
	private ReportPrintOption printName;
	
	private ReportPrintOption printNif;
	
	private ReportPrintOption printAddress;
	
	private ReportPrintOption printInternetData;
	
	private boolean printSaleInvoiceFooter;

	private FinancePaymentTemplate financePaymentTemplate;
	
	private ItemTagTemplate itemTagTemplate;
	
	private String itemTagDefaultText;
	
	private String itemTagBarcodePattern;
	
	private Tag manufacturingOrderTemplateTag;
	
	private boolean smartCard;
	
	private boolean customReportTemplate;
	
	private boolean helpdeskEnabled;
	
	private List<IControllerListener> listenerClasses;
	
	private Scope scope;
	
	private boolean active;
	
    public CompanyParentController() {
    	this.listenerClasses = new LinkedList<IControllerListener>();
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
	 * Gets the logo RegistryAttach.
	 * 
	 * @return the logo attachment
	 */
	public RegistryAttachment getLogoAttach() {
		return logoAttach;
	}

	/**
	 * Sets the logo RegistryAttach.
	 * 
	 * @param attach the logo attachment
	 */
	public void setLogoAttach(RegistryAttachment logoAttach) {
		this.logoAttach = logoAttach;
	}

	/**
	 * Gets the signature RegistryAttach.
	 * 
	 * @return the signature attachment
	 */
	public RegistryAttachment getSignatureAttach() {
		return signatureAttach;
	}
	
	/**
	 * Sets the signature RegistryAttach.
	 * 
	 * @param attach the signature attachment
	 */
	public void setSignatureAttach(RegistryAttachment signatureAttach) {
		this.signatureAttach = signatureAttach;
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
	
	public List<SelectItem> getMunicipalities(){
		ResourceBundle bundle = ResourceBundle.getBundle(ICommonConstants.MUNICIPALITIES_BUNDLE_NAME);
		List<SelectItem> municipalities = new LinkedList<SelectItem>();
		if(this.getTo()!=null){
			RegistryAddress address = getMainAddress();
			if(address!=null && address.getGeozone()!=null && address.getGeozone().getCode()!=null) {
				TreeSet<String> tree = new TreeSet<String>(bundle.keySet());
				for(String key: tree){
					if(key.startsWith(address.getGeozone().getCode())){
						String name = bundle.getString(key);
						SelectItem item = new SelectItem(key, name);
						municipalities.add(item);
					}
				}
			}
		}
		return municipalities;
	}

	/**
	 * Checks if a logo image is attached.
	 * 
	 * @return true, if a logo image is attached
	 */
	public boolean isLogoImageAttached(){
		if(this.logoAttach != null){
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
		onLoad(true);
	}
	
	/**
	 * On load.
	 */
	public void onLoad( boolean activeListeners ) {
		try {
			if ( activeListeners && (listenerClasses != null) ) {
				addListeners(listenerClasses);
				listenerClasses = null;
			}
			initializeModel();
			if(this.getModel().getRowCount() > 0){
				if(this.getModel().getRowCount() > 1){
					LOGGER.error( "More than 1 company found in domain {}", DomainManager.getCurrentDomain());
				}
				this.getModel().setRowIndex(0);
				onSelect(null);
				loadMainAddress();
			}else{
				this.onReset(null);
				((Company)this.getTo()).setDocumentType(DocumentType.CIF);
				initControllerData();
				this.mainAddress = getEmpyMainAddress(new Registry());
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
	
	public static RegistryAddress getEmpyMainAddress( Registry registry ) {
		RegistryAddress mainAddress = new RegistryAddress();
		mainAddress.setDomain(registry.getDomain());
		mainAddress.setRegistry(registry);
		mainAddress.setAddressType(AddressType.MAIN);			
		mainAddress.setStreetType(StreetType.CL);
		mainAddress.setGeozone(new GeoZone());
		return mainAddress;
	}

	private void loadMainAddress() throws ManagerBeanException {
		Company company = (Company)this.getModel().getRowData();
		this.mainAddress = RegistryInfo.getMainAddress(company);
		if (this.mainAddress == null) {
			this.mainAddress = getEmpyMainAddress(company);
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
			this.onLoad(false);
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

	/**
	 * Gets the signature attach as input stream.
	 * 
	 * @return the signature attach as input stream
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws IOException the IO exception
	 */
	public InputStream getSignatureAttachAsInputStream() throws IOException, ManagerBeanException{
		RegistryAttachment attach = obtainCompanySignature();
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
			this.onLoad(false);
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
	 * Obtains company signature.
	 * 
	 * @return the registry attachment
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public RegistryAttachment obtainCompanySignature() throws ManagerBeanException {
		if(this.getTo() == null){
			this.onLoad(false);
		}
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, ((Company)this.getTo()).getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.SIGNATURE);
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
			this.onLoad(false);
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
			this.onLoad(false);
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
			this.onLoad(false);
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
			this.onLoad(false);
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
			this.onLoad(false);
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
	
	public ApplicationParameter getCustomSaleInvoiceTemplateParam() {
		if(customSaleInvoiceTemplateParam==null){
			customSaleInvoiceTemplateParam = new ApplicationParameter();
			customSaleInvoiceTemplateParam.setName(AppParam.REPORT_saleInvoice.getValue());
			customSaleInvoiceTemplateParam.setSystemParameter(false);
		}
		return customSaleInvoiceTemplateParam;
	}

	public void setCustomSaleInvoiceTemplateParam(
			ApplicationParameter customSaleInvoiceTemplateParam) {
		this.customSaleInvoiceTemplateParam = customSaleInvoiceTemplateParam;
	}

	public String getCustomSaleInvoiceTemplateName() {
		return getCustomSaleInvoiceTemplateParam().getValue();
	}
	
	public void setCustomSaleInvoiceTemplateName(
			String customSaleInvoiceTemplateName) {
		getCustomSaleInvoiceTemplateParam().setValue(customSaleInvoiceTemplateName);
	}
	
	public List<SelectItem> getCustomSaleInvoiceTemplateNames(){
		String REPORT_PATH = "/home/COMMON-RESOURCES/aon-report";
		File customDirectory = new File( REPORT_PATH );
		List<SelectItem> list = new LinkedList<SelectItem>();
		if ( customDirectory.exists() && customDirectory.canRead() ) {
			for(File file: customDirectory.listFiles()){
				if(file.isDirectory()){
					list.add(new SelectItem(file.getName(), file.getName()));
				}
			}
		}
		return list;
	}

	public boolean isPrintDiscountPriceApplied() {
		return printDiscountPriceApplied;
	}

	public void setPrintDiscountPriceApplied(boolean printDiscountPriceApplied) {
		this.printDiscountPriceApplied = printDiscountPriceApplied;
	}

	public boolean isPrintLogo() {
		return printLogo;
	}

	public void setPrintLogo(boolean printLogo) {
		this.printLogo = printLogo;
	}

	public boolean isPrintProductCode() {
		return printProductCode;
	}

	public void setPrintProductCode(boolean printProductCode) {
		this.printProductCode = printProductCode;
	}

	public boolean isPrintReferenceCode() {
		return printReferenceCode;
	}

	public void setPrintReferenceCode(boolean printReferenceCode) {
		this.printReferenceCode = printReferenceCode;
	}

	public boolean isPrintProject() {
		return printProject;
	}

	public void setPrintProject(boolean printProject) {
		this.printProject = printProject;
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
	
	public boolean isPrintSaleInvoiceFooter() {
		return printSaleInvoiceFooter;
	}

	public void setPrintSaleInvoiceFooter(boolean printSaleInvoiceFooter) {
		this.printSaleInvoiceFooter = printSaleInvoiceFooter;
	}

	public FinancePaymentTemplate getFinancePaymentTemplate() {
		return financePaymentTemplate;
	}

	public void setFinancePaymentTemplate(
			FinancePaymentTemplate financePaymentTemplate) {
		this.financePaymentTemplate = financePaymentTemplate;
	}

	public ItemTagTemplate getItemTagTemplate() {
		return itemTagTemplate;
	}

	public void setItemTagTemplate(ItemTagTemplate itemTagTemplate) {
		this.itemTagTemplate = itemTagTemplate;
	}

	public String getItemTagDefaultText() {
		return itemTagDefaultText;
	}

	public void setItemTagDefaultText(String itemTagDefaultText) {
		this.itemTagDefaultText = itemTagDefaultText;
	}
	
	public String getItemTagBarcodePattern() {
		return itemTagBarcodePattern;
	}

	public void setItemTagBarcodePattern(String itemTagBarcodePattern) {
		this.itemTagBarcodePattern = itemTagBarcodePattern;
	}

	public Tag getManufacturingOrderTemplateTag() {
		return manufacturingOrderTemplateTag;
	}

	public void setManufacturingOrderTemplateTag(Tag manufacturingOrderTemplateTag) {
		this.manufacturingOrderTemplateTag = manufacturingOrderTemplateTag;
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
	
	public boolean isHelpdeskEnabled() {
		return helpdeskEnabled;
	}

	public void setHelpdeskEnabled(boolean helpdeskEnabled) {
		this.helpdeskEnabled = helpdeskEnabled;
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

	public boolean obtainPrintHeader() throws ManagerBeanException { 
		return AppParamUtil.getValueAsBoolean(APP_PRINT_HEADER_PARAM);
	}

	public boolean obtainPrintRecordData() throws ManagerBeanException {
		return AppParamUtil.getValueAsBoolean(APP_PRINT_RECORD_DATA_PARAM);
	}
	
	public SaleInvoiceTemplate obtainSaleInvoiceTemplate() throws ManagerBeanException {
		String value = AppParamUtil.getValue(APP_SALE_INVOICE_TEMPLATE_PARAM);
		return (value == null?null:SaleInvoiceTemplate.getEnumByValue(value));
	}

	public boolean obtainPrintDiscountPriceApplied() throws ManagerBeanException {
		return AppParamUtil.getValueAsBoolean(APP_PRINT_DISCOUNT_PRICE_APPLIED);
	}
	
	public FinancePaymentTemplate obtainFinancePaymentTemplate() throws ManagerBeanException {
		String value = AppParamUtil.getValue(APP_FPAYMENT_TEMPLATE_PARAM);
		return (value == null?null:FinancePaymentTemplate.getEnumByValue(value));
	}
	
	public ItemTagTemplate obtainItemTagTemplate() throws ManagerBeanException {
		String value = AppParamUtil.getValue(APP_ITEM_TAG_TEMPLATE_PARAM);
		return (value == null?null:ItemTagTemplate.getEnumByValue(value));
	}

	public String obtainItemTagDefaultText() throws ManagerBeanException {
		String value = AppParamUtil.getValue(APP_ITEM_TAG_TEXT_PARAM);
		return value;
	}

	public String obtainItemTagBarcodePattern() throws ManagerBeanException {
		String value = AppParamUtil.getValue(APP_ITEM_TAG_BARCODE_PARAM);
		return value;
	}
	
	public Tag obtainManufacturingOrderTemplateTag() throws ManagerBeanException {
		String value = AppParamUtil.getValue(APP_MANUFACT_TEMPLATE_TAG_PARAM);
		if(NumberUtils.isNumber(value)){
			return (Tag) BeanManager.getManagerBean(Tag.class).get(Integer.parseInt(value));
		}
		return null;
	}
	
	public boolean obtainPrintLogo() throws ManagerBeanException {
		return AppParamUtil.getValueAsBoolean(APP_PRINT_LOGO_PARAM);
	}
	
	public boolean obtainPrintReferenceCode() throws ManagerBeanException {
		return AppParamUtil.getValueAsBoolean(APP_PRINT_REFERENCE_CODE_PARAM);
	}
	
	public boolean obtainPrintProject() throws ManagerBeanException {
		return AppParamUtil.getValueAsBoolean(APP_PRINT_PROJECT_PARAM);
	}
	
	public boolean obtainPrintProductCode() throws ManagerBeanException {
		return AppParamUtil.getValueAsBoolean(APP_PRINT_PRODUCT_CODE_PARAM);
	}
	
	public ReportPrintOption obtainPrintName() throws ManagerBeanException {
		return getReportPrintOptionValue(APP_PRINT_NAME_PARAM);
	}
	
	public ReportPrintOption obtainPrintNif() throws ManagerBeanException {
		return getReportPrintOptionValue(APP_PRINT_NIF_PARAM);
	}
	
	public ReportPrintOption obtainPrintAddress() throws ManagerBeanException {
		return getReportPrintOptionValue(APP_PRINT_ADDRESS_PARAM);
	}
	
	public ReportPrintOption obtainPrintInternetData() throws ManagerBeanException {
		return getReportPrintOptionValue(APP_PRINT_INTERNET_DATA_PARAM);
	}
	
	public boolean obtainPrintSaleInvoiceFooter() throws ManagerBeanException {
		return AppParamUtil.getValueAsBoolean(APP_PRINT_S_INVOICE_FOOTER_PARAM);
	}
	
	public boolean obtainSmartCard() throws ManagerBeanException {
		return AppParamUtil.getValueAsBoolean(APP_SMART_CARD_PARAM);
	}
	
	public void searchCustomReportTemplate() throws ManagerBeanException {
		customSaleInvoiceTemplateParam = AppParamUtil.getParameter(AppParam.REPORT_saleInvoice);
		setCustomReportTemplate( customSaleInvoiceTemplateParam!=null && StringUtils.isNotBlank(customSaleInvoiceTemplateParam.getValue()) );
	}
	
	public boolean obtainHelpdeskEnabled() throws ManagerBeanException {
		return AppParamUtil.getValueAsBoolean(AppParam.AON_HELPDESK_ENABLED);
	}

	private ReportPrintOption getReportPrintOptionValue(AppParam appParam) {
		Integer value = AppParamUtil.getValueAsInteger(appParam);
		if ( value != null ) {
			return ReportPrintOption.values()[value];
		}
		return null;
	}

	public boolean isEInvoice() {
		if(this.getTo() == null){
			this.onLoad(false);
		}
		return ((Company) getTo()).isEInvoice();
	}

	public boolean isSurcharge() {
		if(this.getTo() == null){
			this.onLoad(false);
		}
		return ((Company) getTo()).isSurcharge();
	}

	public boolean isWithholding() {
		if(this.getTo() == null){
			this.onLoad(false);
		}
		return ((Company) getTo()).isWithholding();
	}

	public boolean isWithholdingFarmer() {
		if(this.getTo() == null){
			this.onLoad(false);
		}
		return ((Company) getTo()).isWithholdingFarmer();
	}

	public void onChangeDocument(ActionEvent event) {
		try {
			if (isNevv()) {
				Company company = (Company) getTo();
				RegistryController.validateDocument(company, getPojoShortName(), getManagerBean());
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("unable to check Document.",e);
		}
	}
	
	public void onLoadGeozone(ActionEvent event){
		RegistryAddress address = getMainAddress();
		if(address!=null && address.getZip()!=null) {
			address.loadGeoZoneByZip();
		}
	}

	public void setBasicListenerClasses(List<IControllerListener> basicListenerClasses) {
		addListeners(basicListenerClasses);
	}	

	public void setListenerClasses(List<IControllerListener> listenerClasses) {
		this.listenerClasses.addAll(listenerClasses);
	}

	public void setOptionalListenerClasses(List<IControllerListener> listenerClasses) {
		this.listenerClasses.addAll(listenerClasses);
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<SelectItem> getDomainScopes() {
		List<SelectItem> scopes = new LinkedList<SelectItem>();
		try {
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			if ( DomainSwitcher.getDomainType(principal.getDomainId()) == DomainType.ADMIN ) {
				DomainSwitcher dw = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
				Integer domainId = dw.isChildDomain() ? dw.getParentDomainId() : dw.getDomainId();
				IManagerBean bean = BeanManager.getManagerBean(Scope.class);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SCOPE_DOMAIN), domainId);
				criteria.addOrder(bean.getFieldName(IEntityAlias.SCOPE_DESCRIPTION));
				for (ITransferObject ito : bean.getList(criteria)) {
					Scope scope = (Scope) ito;
					scopes.add( new SelectItem(scope, scope.getDescription()) );
				}				
			} else {
				IManagerBean bean = BeanManager.getManagerBean(UserScope.class);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), principal.getUserId());
				criteria.addOrder(bean.getFieldName(IEntityAlias.USER_SCOPE_SCOPE_DESCRIPTION));
				for (ITransferObject ito : bean.getList(criteria)) {
					Scope scope = ((UserScope) ito).getScope();
					scopes.add( new SelectItem(scope, scope.getDescription()) );
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error getting domain scopes. " + e.getMessage(), e);
		}
		return scopes;
	}	

	public String getLastAccessUser() throws ManagerBeanException {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Domain domain = (Domain) BeanManager.getManagerBean(Domain.class).get(ds.getDomainId());
		return domain.getLastAccessUser();
	}

	public Date getLastAccessDate() throws ManagerBeanException {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		Domain domain = (Domain) BeanManager.getManagerBean(Domain.class).get(ds.getDomainId());
		return domain.getLastAccessDate();
	}
	
}
