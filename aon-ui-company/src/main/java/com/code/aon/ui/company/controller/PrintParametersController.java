package com.code.aon.ui.company.controller;

import static com.code.aon.common.enumeration.AppParam.APP_PRINT_ADDRESS_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_DISCOUNT_PRICE_APPLIED;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_INTERNET_DATA_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_NAME_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_NIF_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_REFERENCE_CODE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_S_INVOICE_FOOTER_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_LOGO_MAX_SIZE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_SALE_INVOICE_FOOTER_LOPD;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_SALE_INVOICE_FOOTER_TEXT;
import static com.code.aon.ui.common.ICommonMessages.FILE_UPLOAD_ELEMENT;
import static com.code.aon.ui.company.controller.ICompanyConstants.IMAGE_MAX_SIZE;
import static com.code.aon.ui.company.controller.ICompanyConstants.INVOICE_PRINT_REPORT_KEY;
import static com.code.aon.ui.company.controller.ICompanyConstants.SALE_INVOICE_REPORT_KEY;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Company;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.sun.faces.util.MessageFactory;


public class PrintParametersController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PrintParametersController.class.getName());

	private Company company;
	private SaleInvoiceParams saleInvoiceParams;
	private ReportBackground reportBackground;
	private SaleInvoiceFooter saleInvoiceFooter;

	
	public Company getCompany() {
		return company;
	}
		
	public SaleInvoiceParams getSaleInvoiceParams() {
		if(saleInvoiceParams==null){
			saleInvoiceParams = new SaleInvoiceParams();
		}
		return saleInvoiceParams;
	}
	
	public boolean isSaleInvoiceDefault() {
		Integer domainId = DomainManager.getCurrentDomain();
		String domainName = AonUtil.getDomainName();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		com.esferalia.aon.occam.api.model.ApplicationParameter appParam = AON.getApplicationParameter(domainName, domainId, login, com.esferalia.aon.occam.api.model.type.AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM);

		com.esferalia.aon.occam.api.model.ApplicationParameter personalized = AON.getApplicationParameter(domainName, domainId, login, com.esferalia.aon.occam.api.model.type.AppParam.REPORT_saleInvoice);
		
		return personalized.isEmpty() && ( appParam.getValue() == null || "default".equalsIgnoreCase(appParam.getValue()));
	}
	private List<SelectItem> saleInvoiceTemplates;

	public List<SelectItem> getSaleInvoiceTemplates() {
		if(saleInvoiceTemplates == null || saleInvoiceTemplates.isEmpty()) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			saleInvoiceTemplates = new LinkedList<>();
		
			String name = SaleInvoiceTemplate.DEFAULT.getName(locale);
			SelectItem item = new SelectItem(SaleInvoiceTemplate.DEFAULT, name);
			saleInvoiceTemplates.add(item);
		
			if(getSaleInvoiceParams().getSaleInvoiceTemplate() != null 
					&& !SaleInvoiceTemplate.DEFAULT.equals(getSaleInvoiceParams().getSaleInvoiceTemplate())) {
				String name2 = getSaleInvoiceParams().getSaleInvoiceTemplate().getName(locale);
				SelectItem item2 = new SelectItem(getSaleInvoiceParams().getSaleInvoiceTemplate(), name2);
				saleInvoiceTemplates.add(item2);
			}
		}
		return saleInvoiceTemplates;
	}
	
	public SaleInvoiceFooter getSaleInvoiceFooter() {
		if(saleInvoiceFooter==null){
			saleInvoiceFooter = new SaleInvoiceFooter();
			saleInvoiceFooter.init();
		}
		return saleInvoiceFooter;
	}
	
	public ReportBackground getReportBackground() {
		if(reportBackground==null){
			reportBackground = new ReportBackground();
			reportBackground.init();
		}
		return reportBackground;
	}
	
	public String getSaleInvoiceFooterText() {
		return getSaleInvoiceFooter().getText();
	}

	
	public void onInit(ActionEvent event) {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		this.onInit(((Company) controller.getTo()).getRegistry().getDomain());
	}	

	public void onInit(int domainId) {
		loadCompany(domainId);
		getSaleInvoiceParams().init();
		getReportBackground().init();
		getSaleInvoiceFooter().init();
	}	

	public void accept(ActionEvent event) {
		getSaleInvoiceParams().accept();
		getSaleInvoiceFooter().accept();
		getReportBackground().accept();
	}

	private void loadCompany(int domainId) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Company.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COMPANY_DOMAIN),
					domainId);
			List<ITransferObject> list = bean.getList(criteria);
			if(list==null || list.isEmpty() ){
				throw new AbortProcessingException("****ERROR: No Company found with domainId = "+domainId+" !!!!!");
			} else if(list.size()>1){
				throw new AbortProcessingException("****ERROR: Multiple Companyes found in domainId = "+domainId+" !!!!!");
			} else {
				company = (Company) list.get(0);
				
				checkLoadedCompany(domainId);
				
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	/**
	 * 	WARN WHEN COMPANY OF SESSION DOES NOT MATCH WITH THE LOADED COMPANY 
	 * @param domainId 
	 */
	private void checkLoadedCompany(int domainId) {
		try {
			CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			Company sessionCompany = (Company) controller.getTo();
			if(company!=null && sessionCompany!=null){
				if(Integer.compare(company.getId(), sessionCompany.getId())!=0){
					StringBuilder sb = new StringBuilder("### IMPORTANT ### ");
					sb.append("COMPANY OF SESSION (");
					sb.append(sessionCompany.getName());
					sb.append(") DOES NOT MATCH WITH THE LOADED COMPANY (");
					sb.append(company.getName());
					sb.append(") WITH domainId ");
					sb.append(domainId);
					LOGGER.warn(sb.toString());
				}
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
		}
	}

	private byte[] getData(IAttachment attach) {
		String domainName = AonUtil.getDomainName();
		Integer domainId = attach.getDomain();
		String user = AonUtil.getAuthPrincipal().getShortName();
		byte[] data = null;
		if (attach != null && attach.getData() != null) {
			data = attach.getData();
		} else if(attach != null && attach.getDriveId()!=null){
			data = DriveUtils.getByteFile(domainName, domainId, user, attach.getDriveId(), attach.getId());
		}
		if (data == null)
			data = "".getBytes();
		if (protectAgainstCrossData(data, attach, domainName, domainId, user))
			data = "".getBytes();
		return data;
	}

	private void deleteDriveData(IAttachment attach) {
		if(attach != null && attach.getDriveId()!=null && !"".equals(attach.getDriveId())){
			try {
				IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
				attach.setDriveId(null);
				bean.update(attach);
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage());
			}
			DriveUtils.deleteFile(AonUtil.getDomainName(), 
					attach.getDomain(), AonUtil.getAuthPrincipal().getShortName(),
					attach.getDriveId());
		}
	}
	
	private boolean protectAgainstCrossData(byte[] data, IAttachment attach, String domainName, Integer domainId, String user) {
		String value = new String(data);
		if (value.startsWith("<?xml")) {
			String msg = "INVOICE_FOOTER TEXT: CROSSED VALUE!!! [domain_name: " + domainName + "; domain_id: "
					+ domainId + "; logged_user: " + user + "; attach_id: " + attach.getId() + "; drive_id: "
					+ attach.getDriveId() + "]";
			String msg2 = "\nVALUE FOUND: " + value;
			LOGGER.error(msg + msg2);
			sendEmail(domainName, domainId, user, "ERROR", "InvoiceFooterText", msg, "error", value, "soporte@aonsolutions.es");
			return true;
		}
		return false;
	}
	
	public String onSampleReportSaleInvoice() throws ManagerBeanException {
		return onPrintSampleReport("saleInvoice");
	}
	public String onSampleReportOffer() throws ManagerBeanException {
		return onPrintSampleReport("offer");
	}
	public String onSampleReportDelivery() throws ManagerBeanException {
		return onPrintSampleReport("delivery");
	}
	public String onSampleReportSales() throws ManagerBeanException {
		return onPrintSampleReport("sales");
	}
	
	public String onPrintSampleReport(String beanName) throws ManagerBeanException {
		IController controller = (IController) AonUtil
				.getRegisteredBean(beanName);
		ITransferObject to = controller.getManagerBean().createNewTo();
		ReportManager report = (ReportManager) AonUtil.getRegisteredBean("report");
		report.setCollectionProvider(new ICollectionProvider() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
				return getCollection();
			}
			
			@SuppressWarnings("rawtypes")
			@Override
			public Collection getCollection() {
				List<ITransferObject> list = new LinkedList<>();
				list.add(to);
				return list;
			}
		});
		return report.onExecute();
	}
	
	
	/*
	 * SALE INVOICE PARAMS
	 */
	public class SaleInvoiceParams implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private final String CUSTOM_REPORT_TEMPLATE_PATH = "/home/COMMON-RESOURCES/aon-report";
		
		private SaleInvoiceTemplate saleInvoiceTemplate;
		private ApplicationParameter customSaleInvoiceTemplateParam;
		private boolean customReportTemplate;
		private ReportPrintOption printName;
		private ReportPrintOption printNif;
		private ReportPrintOption printAddress;
		private ReportPrintOption printInternetData;
		private boolean printDiscountPriceApplied;
		private boolean printReferenceCode;
		private boolean printProductPackage;
		
		public SaleInvoiceTemplate getSaleInvoiceTemplate(){
			return saleInvoiceTemplate;
		}
		
		public void setSaleInvoiceTemplate(SaleInvoiceTemplate saleInvoiceTemplate){
			this.saleInvoiceTemplate = saleInvoiceTemplate;
		}
		
		public String getSaleInvoiceTemplateValue(){
			return saleInvoiceTemplate==null ? SaleInvoiceTemplate.DEFAULT.getValue() : saleInvoiceTemplate.getValue();
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
			File customDirectory = new File( CUSTOM_REPORT_TEMPLATE_PATH );
			List<SelectItem> list = new LinkedList<>();
			if ( customDirectory.exists() && customDirectory.canRead() ) {
				for(File file: customDirectory.listFiles()){
					if(file.isDirectory()){
						list.add(new SelectItem(file.getName(), file.getName()));
					}
				}
			}
			return list;
		}
		
		public boolean isCustomReportTemplate() {
			return customReportTemplate;
		}

		public void setCustomReportTemplate(boolean customReportTemplate) {
			this.customReportTemplate = customReportTemplate;
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
		
		public boolean isPrintDiscountPriceApplied() {
			return printDiscountPriceApplied;
		}

		public void setPrintDiscountPriceApplied(boolean printDiscountPriceApplied) {
			this.printDiscountPriceApplied = printDiscountPriceApplied;
		}
		
		public boolean isPrintReferenceCode() {
			return printReferenceCode;
		}

		public void setPrintReferenceCode(boolean printReferenceCode) {
			this.printReferenceCode = printReferenceCode;
		}
		
		public boolean isPrintProductPackage() {
			return printProductPackage;
		}

		public void setPrintProductPackage(boolean printProductPackage) {
			this.printProductPackage = printProductPackage;
		}

		public void init() {
			setSaleInvoiceTemplate(obtainSaleInvoiceTemplate());
			searchCustomReportTemplate();

			setPrintName(getReportPrintOptionValue(APP_PRINT_NAME_PARAM));
			setPrintNif(getReportPrintOptionValue(APP_PRINT_NIF_PARAM));
			setPrintAddress(getReportPrintOptionValue(APP_PRINT_ADDRESS_PARAM));
			setPrintInternetData(getReportPrintOptionValue(APP_PRINT_INTERNET_DATA_PARAM));
			
			setPrintReferenceCode(AppParamUtil.getValueAsBoolean(APP_PRINT_REFERENCE_CODE_PARAM));
			setPrintDiscountPriceApplied(AppParamUtil.getValueAsBoolean(APP_PRINT_DISCOUNT_PRICE_APPLIED));
			setPrintProductPackage(AppParamUtil.getValueAsBoolean(AppParam.APP_PRINT_PRODUCT_PACKAGE_PARAM));
		}
		
		public void accept() {
			if(isCustomReportTemplate()
					&& StringUtils.isNotBlank(getCustomSaleInvoiceTemplateParam().getValue())){
				AppParamUtil.insertParameter(AppParam.REPORT_saleInvoice, getCustomSaleInvoiceTemplateParam().getValue());
				AppParamUtil.insertParameter(AppParam.REPORT_invoicePrint, getCustomSaleInvoiceTemplateParam().getValue());
				AppParamUtil.insertParameter(AppParam.REPORT_invoiceDetailDefault, getCustomSaleInvoiceTemplateParam().getValue());
				AppParamUtil.insertParameter(AppParam.REPORT_invoiceTaxBreakDown, getCustomSaleInvoiceTemplateParam().getValue());
				AppParamUtil.insertParameter(AppParam.REPORT_invoiceFinancesDefault, getCustomSaleInvoiceTemplateParam().getValue());
				AppParamUtil.insertParameter(AppParam.REPORT_invoicePrepaymentsDefault, getCustomSaleInvoiceTemplateParam().getValue());
				setSaleInvoiceTemplate(SaleInvoiceTemplate.DEFAULT);
			} else {
				AppParamUtil.removeParameter(AppParam.REPORT_saleInvoice);
				AppParamUtil.removeParameter(AppParam.REPORT_invoicePrint);
				AppParamUtil.removeParameter(AppParam.REPORT_invoiceDetailDefault);
				AppParamUtil.removeParameter(AppParam.REPORT_invoiceTaxBreakDown);
				AppParamUtil.removeParameter(AppParam.REPORT_invoiceFinancesDefault);
				AppParamUtil.removeParameter(AppParam.REPORT_invoicePrepaymentsDefault);
				setCustomReportTemplate(false);
			}
			updateParam(APP_SALE_INVOICE_TEMPLATE_PARAM, getSaleInvoiceTemplate());		
			updateParam(APP_PRINT_NAME_PARAM, getPrintName());
			updateParam(APP_PRINT_NIF_PARAM, getPrintNif());
			updateParam(APP_PRINT_ADDRESS_PARAM, getPrintAddress());
			updateParam(APP_PRINT_INTERNET_DATA_PARAM, getPrintInternetData());
			AppParamUtil.insertParameter(APP_PRINT_INTERNET_DATA_PARAM, (getPrintInternetData() != null) ? String.valueOf(getPrintInternetData().ordinal()) : null);
			
			AppParamUtil.insertParameter(AppParam.APP_PRINT_DISCOUNT_PRICE_APPLIED, isPrintDiscountPriceApplied());
			AppParamUtil.insertParameter(APP_PRINT_REFERENCE_CODE_PARAM, isPrintReferenceCode());
			AppParamUtil.insertParameter(AppParam.APP_PRINT_PRODUCT_PACKAGE_PARAM, isPrintProductPackage());
			
		}
		
		public void searchCustomReportTemplate() {
			customSaleInvoiceTemplateParam = AppParamUtil.getParameter(AppParam.REPORT_saleInvoice);
			setCustomReportTemplate( customSaleInvoiceTemplateParam!=null && StringUtils.isNotBlank(customSaleInvoiceTemplateParam.getValue()) );
		}
		
		public SaleInvoiceTemplate obtainSaleInvoiceTemplate() {
			String value = AppParamUtil.getValue(APP_SALE_INVOICE_TEMPLATE_PARAM);
			return (value == null?null:SaleInvoiceTemplate.getEnumByValue(value));
		}

		private ReportPrintOption getReportPrintOptionValue(AppParam appParam) {
			Integer value = AppParamUtil.getValueAsInteger(appParam);
			if ( value != null ) {
				return ReportPrintOption.values()[value];
			}
			return null;
		}
		
		private void updateParam(AppParam appParam, ReportPrintOption value) {
			AppParamUtil.insertParameter(appParam, (value != null) ? String.valueOf(value.ordinal()) : null);
		}
		
		private void updateParam(AppParam appParam, SaleInvoiceTemplate value) {
			AppParamUtil.insertParameter(appParam, (value != null) ? value.getValue() : null);
		}
		
	}
	
	
	/*
	 * SALE INVOICE FOOTER
	 */
	public class SaleInvoiceFooter implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private boolean printSaleInvoiceFooter;
		
		private String text;

		private RegistryAttachment attach;
		
		public boolean isPrintSaleInvoiceFooter() {
			return printSaleInvoiceFooter;
		}

		public void setPrintSaleInvoiceFooter(boolean printSaleInvoiceFooter) {
			this.printSaleInvoiceFooter = printSaleInvoiceFooter;
		}
		
		public RegistryAttachment getAttach() {
			return attach;
		}
		
		public void resetAttach() {
			attach = new RegistryAttachment();
		}
		
		private void init() {
			setPrintSaleInvoiceFooter(AppParamUtil.getValueAsBoolean(APP_PRINT_S_INVOICE_FOOTER_PARAM));
			
			setText("");

			try {
				IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.INVOICE_FOOTER_TEXT);
				List<ITransferObject> list = bean.getList(criteria);
				if(list!=null && !list.isEmpty()){
					attach = (RegistryAttachment) list.get(0);
					setText(new String(getData(attach)));
				} else {
					attach = new RegistryAttachment();
					setText("");
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error on SaleInvoiceFooter -> init()");
				LOGGER.error(e.getMessage());
			}
		}
		
		public void accept() {
			boolean enabled = printSaleInvoiceFooter && text!=null && !"".equals(text.trim());
			AppParamUtil.insertParameter(APP_PRINT_S_INVOICE_FOOTER_PARAM, enabled);
			if(isPrintSaleInvoiceFooter()){
				completeAttachInfo();
			} else {
				completeAttachInfo(null);
				getSaleInvoiceFooter().setText("");
			}
			
			try {
				IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
				String text = getSaleInvoiceFooter().getText();
				if(attach.getId()==null){
					attach = (RegistryAttachment) bean.insert(attach);
				} else {
					if(text==null || "".equals(text)){
						bean.remove(attach);
					} else {
						bean.update(attach);
					}
					deleteDriveData(attach);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error on SaleInvoiceFooter -> accept()");
				LOGGER.error(e.getMessage());
			}
			
		}
		
		private void completeAttachInfo() {
			completeAttachInfo(getText().getBytes());
		}

		private void completeAttachInfo(byte[] data) {
			attach.setRegistry(getCompany());
			attach.setAttachDate(new Date());
			attach.setConfidential(false);
			attach.setData(data);
			attach.setRegistryAttachmentType(RegistryAttachmentType.INVOICE_FOOTER_TEXT);
			attach.setDescription(AonUtil.getMessage(COMPANY_SALE_INVOICE_FOOTER_TEXT));
			attach.setMimeType(MimeType.MIME_TXT);
		}
		
		public String getText() {
			if ( StringUtils.isEmpty(text) ){
				SaleInvoiceTemplate template = getSaleInvoiceParams().getSaleInvoiceTemplate();
				try {
					if ( template == SaleInvoiceTemplate.GTA && isGarageDomainType()){
						setText(obtainGtaLOPD());
					} else if ( template == SaleInvoiceTemplate.HOTEL && isHotelDomainType()){
						setText(obtainHotelLOPD());
					}
				} catch (ManagerBeanException e) {
					setText(null);
				}
			}
			return text;
		}
		
		public void setText(String text) {
			this.text = text;
		}
		
		public String getGtaDefaultText() {
			try {
				return obtainGtaLOPD();
			} catch (ManagerBeanException e) {
				String msg = "Se ha producido un error de lectura. Vuelva a intentarlo pasados unos segundos.";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
		}
		
		public String getHotelDefaultText() {
			try {
				return obtainHotelLOPD();
			} catch (ManagerBeanException e) {
				String msg = "Se ha producido un error de lectura. Vuelva a intentarlo pasados unos segundos.";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
		}
		
		public boolean isGarageDomainType() {
			return isDomainType(Module.GARAGE);
		}
		
		public boolean isHotelDomainType() {
			return isDomainType(Module.HOTEL);
		}
		
		public boolean isDomainType(Module module) {
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
			Integer domainId = ds.getDomainId();
			if ( domainId != null ) {
				Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
				try {
					return AuditManager.hasModule(domainId, appId, module);
				} catch (Throwable th) {
					return false;
				}	
			}	
			return false;
		}
		
		public boolean isGarageSaleInvoiceTemplate() {
			return getSaleInvoiceParams().getSaleInvoiceTemplate() == SaleInvoiceTemplate.GTA;
		}
		
		public boolean isHotelSaleInvoiceTemplate() {
			return getSaleInvoiceParams().getSaleInvoiceTemplate() == SaleInvoiceTemplate.HOTEL;
		}
		
		public void createLOPD(ActionEvent event) {
			CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			Company company = controller.obtainCompany();
			String companyName = "";
			String companyFullAddress = "";
			try {
				companyName = company.getName();
				companyFullAddress = company.getDefaultAddress().getFullAddress();
			} catch (ManagerBeanException e) {
				String msg = "Se ha producido un error de lectura. Vuelva a intentarlo pasados unos segundos.";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
			setText(AonUtil.getMessage(COMPANY_SALE_INVOICE_FOOTER_LOPD, companyName, companyFullAddress));
		}
		
		private String obtainGtaLOPD() throws ManagerBeanException {
			CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			RegistryAddress address = controller.obtainAddress();
			Company company = controller.obtainCompany();
			StringBuffer buffer = new StringBuffer();
			buffer.append(AonUtil.getMessage(ICommonMessages.GTA_FOOTER_TEXT_1)).append(" ");
			buffer.append(company.getName()).append(" ");
			buffer.append(AonUtil.getMessage(ICommonMessages.GTA_FOOTER_TEXT_2)).append("\n");
			buffer.append(AonUtil.getMessage(ICommonMessages.TAS_LEGAL_TEXT_1)).append(" ");
			buffer.append(company.getName()).append(" ");
			buffer.append(AonUtil.getMessage(ICommonMessages.TAS_LEGAL_TEXT_2)).append(" ");
			buffer.append(company.getName()).append(", ");
			buffer.append(address.getFullAddress()).append(" - ");
			buffer.append(address.getZip()).append(" ");
			buffer.append(address.getCity());
			buffer.append(" (").append(address.getGeozone().getName()).append(") ");
			buffer.append(AonUtil.getMessage(ICommonMessages.TAS_LEGAL_TEXT_3));
			return buffer.toString();
		}
		
		private String obtainHotelLOPD() throws ManagerBeanException {
			CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			RegistryAddress address = controller.obtainAddress();
			Company company = controller.obtainCompany();
			String nameParam = company.getName();
			nameParam += StringUtils.isNotBlank(company.getAlias())?" ("+company.getAlias()+")":"";
			String addressParam = address.getZip();
			addressParam += ", " + address.getCity();
			addressParam += ", " + address.getFullAddress();
			return AonUtil.getMessage(ICommonMessages.HOTEL_FOOTER_TEXT, nameParam, addressParam);
		}
		
	}
	
	/*
	 * REPORT BACKGROUND
	 */
	public class ReportBackground implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private AonFile saleInvoiceBackgroundFile;
		private AonFile deliveryBackgroundFile;
		private AonFile salesBackgroundFile;
		private AonFile offerBackgroundFile;
		
		private IAttachment saleInvoiceBackgroundAttach;
		private IAttachment deliveryBackgroundAttach;
		private IAttachment salesBackgroundAttach;
		private IAttachment offerBackgroundAttach;
		
		public AonFile getSaleInvoiceBackgroundFile() {
			return saleInvoiceBackgroundFile;
		}
		public void setSaleInvoiceBackgroundFile(AonFile saleInvoiceBackgroundFile) {
			if ( this.saleInvoiceBackgroundFile != null ) {
				this.saleInvoiceBackgroundFile.clean();	
			}
			this.saleInvoiceBackgroundFile = saleInvoiceBackgroundFile;
		}
		public AonFile getDeliveryBackgroundFile() {
			return deliveryBackgroundFile;
		}
		public void setDeliveryBackgroundFile(AonFile deliveryBackgroundFile) {
			if ( this.deliveryBackgroundFile != null ) {
				this.deliveryBackgroundFile.clean();	
			}
			this.deliveryBackgroundFile = deliveryBackgroundFile;
		}
		public AonFile getSalesBackgroundFile() {
			return salesBackgroundFile;
		}
		public void setSalesBackgroundFile(AonFile salesBackgroundFile) {
			if ( this.salesBackgroundFile != null ) {
				this.salesBackgroundFile.clean();	
			}
			this.salesBackgroundFile = salesBackgroundFile;
		}
		public AonFile getOfferBackgroundFile() {
			return offerBackgroundFile;
		}
		public void setOfferBackgroundFile(AonFile offerBackgroundFile) {
			if ( this.offerBackgroundFile != null ) {
				this.offerBackgroundFile.clean();	
			}
			this.offerBackgroundFile = offerBackgroundFile;
		}
		
		public void saleInvoiceBackgroundFileUploaded(UploadEvent event) {
			AonFile aonFile = AttachmentUtil.fileUploaded(event);
			setSaleInvoiceBackgroundFile(aonFile);
			saleInvoiceBackgroundAttach.setData(aonFile.getData());
		}
		public void deliveryBackgroundFileUploaded(UploadEvent event) {
			AonFile aonFile = AttachmentUtil.fileUploaded(event);
			setDeliveryBackgroundFile(aonFile);
			deliveryBackgroundAttach.setData(aonFile.getData());
		}
		public void salesBackgroundFileUploaded(UploadEvent event) {
			AonFile aonFile = AttachmentUtil.fileUploaded(event);
			setSalesBackgroundFile(aonFile);
			salesBackgroundAttach.setData(aonFile.getData());
		}
		public void offerBackgroundFileUploaded(UploadEvent event) {
			AonFile aonFile = AttachmentUtil.fileUploaded(event);
			setOfferBackgroundFile(aonFile);
			offerBackgroundAttach.setData(aonFile.getData());
		}
		
		public void createSaleInvoiceBackgroundContent(OutputStream out, Object data) throws IOException {
			if (getSaleInvoiceBackgroundFile() != null && (getSaleInvoiceBackgroundFile().getSize()>0)) {
				out.write(getSaleInvoiceBackgroundFile().getData());
			}
		}
		public void createDeliveryBackgroundContent(OutputStream out, Object data) throws IOException {
			if (getDeliveryBackgroundFile() != null && (getDeliveryBackgroundFile().getSize()>0)) {
				out.write(getDeliveryBackgroundFile().getData());
			}
		}
		public void createSalesBackgroundContent(OutputStream out, Object data) throws IOException {
			if (getSalesBackgroundFile() != null && (getSalesBackgroundFile().getSize()>0)) {
				out.write(getSalesBackgroundFile().getData());
			}
		}
		public void createOfferBackgroundContent(OutputStream out, Object data) throws IOException {
			if (getOfferBackgroundFile() != null && (getOfferBackgroundFile().getSize()>0)) {
				out.write(getOfferBackgroundFile().getData());
			}
		}
		
		public String clearSaleInvoiceBackgroundUploadData() {
			cleanBackground(getSaleInvoiceBackgroundFile(), ICompanyConstants.SALE_INVOICE_REPORT_KEY);
			return null;
		}
		public String clearDeliveryBackgroundUploadData() {
			cleanBackground(getDeliveryBackgroundFile(), ICompanyConstants.DELIVERY_REPORT_KEY);
			return null;
		}
		public String clearSalesBackgroundUploadData() {
			cleanBackground(getSalesBackgroundFile(), ICompanyConstants.SALES_REPORT_KEY);
			return null;
		}
		public String clearOfferBackgroundUploadData() {
			cleanBackground(getOfferBackgroundFile(), ICompanyConstants.OFFER_REPORT_KEY);
			return null;
		}
		
		private void cleanBackground(AonFile aonFile,  String reportKey){
			if ( aonFile != null ) {
				aonFile.clean();	
			}
		}
		
		
		private void checkAonFile( AonFile aonFile ) {
			if ( aonFile.getSize() <= 0 ) {
				FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage(FILE_UPLOAD_ELEMENT) );
				throw new AbortProcessingException( message.getSummary() );									
			} else if (aonFile.getSize() > IMAGE_MAX_SIZE) {
				String message = AonUtil.getMessage( COMPANY_LOGO_MAX_SIZE_ERROR, IMAGE_MAX_SIZE);
				throw new AbortProcessingException(message);										
			}
		}	
		
		public void init() {
		
			cleanBackground(getSaleInvoiceBackgroundFile(), ICompanyConstants.SALE_INVOICE_REPORT_KEY);
			cleanBackground(getDeliveryBackgroundFile(), ICompanyConstants.DELIVERY_REPORT_KEY);
			cleanBackground(getSalesBackgroundFile(), ICompanyConstants.SALES_REPORT_KEY);
			cleanBackground(getOfferBackgroundFile(), ICompanyConstants.OFFER_REPORT_KEY);
			saleInvoiceBackgroundAttach = new RegistryAttachment();
			offerBackgroundAttach = new RegistryAttachment();
			deliveryBackgroundAttach = new RegistryAttachment();
			salesBackgroundAttach = new RegistryAttachment();
			
			String[] reportKeys = { ICompanyConstants.SALE_INVOICE_REPORT_KEY,
					ICompanyConstants.OFFER_REPORT_KEY,
					ICompanyConstants.DELIVERY_REPORT_KEY,
					ICompanyConstants.SALES_REPORT_KEY };
		
			try {
				IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE),
						RegistryAttachmentType.REPORT_BACKGROUND);
				criteria.addInExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION), reportKeys);
				List<ITransferObject> list = bean.getList(criteria);
				if(list!=null && !list.isEmpty()){
					list.forEach(to -> {
						IAttachment attach =  (IAttachment) to;
						AonFile file = selectAttach(attach);
						if(attach.getDescription().equals(ICompanyConstants.SALE_INVOICE_REPORT_KEY)) {
							saleInvoiceBackgroundAttach = attach;
							setSaleInvoiceBackgroundFile(file);
						} else if(attach.getDescription().equals(ICompanyConstants.OFFER_REPORT_KEY)) {
							offerBackgroundAttach = attach;
							setOfferBackgroundFile(file);
						} else if(attach.getDescription().equals(ICompanyConstants.DELIVERY_REPORT_KEY)) {
							deliveryBackgroundAttach = attach;
							setDeliveryBackgroundFile(file);
						} else if(attach.getDescription().equals(ICompanyConstants.SALES_REPORT_KEY)) {
							salesBackgroundAttach = attach;
							setSalesBackgroundFile(file);
						}
					});
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error on ReportBackground -> init()");
				LOGGER.error(e.getMessage());
			}
					
		}
		
		public void accept() {
			acceptAttach(saleInvoiceBackgroundFile,
					saleInvoiceBackgroundAttach,
					ICompanyConstants.SALE_INVOICE_REPORT_KEY);
			acceptAttach(offerBackgroundFile, offerBackgroundAttach,
					ICompanyConstants.OFFER_REPORT_KEY);
			acceptAttach(deliveryBackgroundFile, deliveryBackgroundAttach,
					ICompanyConstants.DELIVERY_REPORT_KEY);
			acceptAttach(salesBackgroundFile, salesBackgroundAttach,
					ICompanyConstants.SALES_REPORT_KEY);
		}
		
		private void acceptAttach(AonFile aonFile, IAttachment attach, String reportKey){
			try {
				IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);				
				if(aonFile!=null){
					if(aonFile.getData()==null){
						if(attach.getId()!=null){
							deleteDriveData(attach);
							bean.remove(attach);
						}
					} else {
						if(attach.getData()!=null){						
							if(attach.getId()!=null){
								attach = (IAttachment) bean.update(attach);
								deleteDriveData(attach);
							} else {
								attach = fillAttach(aonFile, reportKey);
								attach = (IAttachment) bean.insert(attach);
								aonFile = selectAttach(attach);
							}
						}
					}
					
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error on ReportBackground -> acceptAttach()");
				LOGGER.error(e.getMessage());
			}
		}
		
		
		private IAttachment fillAttach(AonFile aonFile, String name) {
			if ((aonFile != null) && aonFile.isDirty() ) {
				checkAonFile(aonFile);
				RegistryAttachment attach = new RegistryAttachment();
				attach.setRegistry(getCompany().getRegistry());
				attach.setAttachDate(new Date());
				attach.setConfidential(false);
				attach.setRegistryAttachmentType(RegistryAttachmentType.REPORT_BACKGROUND);
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription(name);
				attach.setMimeType(MimeType.getByExtension(aonFile.getMimeType().getExtension()));
				return attach;
			}
			return null;
		}

		private AonFile selectAttach(IAttachment attach) {
			if (attach != null && attach.getId()!=null) {
				AonFile file = new AonFile();
				file.setData(getData(attach));
				file.setKey(attach.getId());
				file.setFileName(attach.getDescription());
				file.setMimeType(com.code.aon.common.enumeration.MimeType.getByExtension(attach.getMimeType().getExtension()));
				return file;
			}
			return null;
		}
	}
	
	protected void sendEmail(String domainName, Integer domainId, String user, String logLevel, String subject, String content, String attachName,
			String attachValue, String... recipients) {
		JSONObject json = new JSONObject();
		try {
			MailAccount mail = getAdminMailAccount(domainName, domainId, user);
			if(mail==null || mail.getId()==null){
				LOGGER.error("No ADMIN mail account defined, cannot continue with email sending!");
			} else {
//				HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
//		    	boolean isDevEnabled = request.getServerPort()==8080
//		    			&& request.getRequestURL().lastIndexOf(":8080")>=0;
				boolean isDevEnabled = false;				
				
				String recipientsTo = "";
				subject = "[DESARROLLO-AON/" + (isDevEnabled?"Test-":"") + logLevel + "] " + subject;
				if (recipients != null) {
					for (String to : recipients) {
						if (!recipientsTo.isEmpty())
							recipientsTo += ",";
						recipientsTo += to;
					}
				}
				if (isDevEnabled) {
					recipientsTo = "eagirrezabal@aonsolutions.es";
				}
				
				json.put("mailAccountId", mail.getId())
						.put("recipientsTo", recipientsTo)
						.put("content", content).put("subject", subject)
						.put("login", user).put("domainName", domainName)
						.put("domainId", domainId)
						.put("bcc", "eagirrezabal@aonsolutions.es");

				if (attachValue == null || "".equals(attachValue)) {
					json.put("md5", "");
				} else {
					String encode = Base64.getEncoder().encodeToString(
							attachValue.getBytes());
					json.put("md5", encode)
							.put("attachName", attachName + ".xml")
							.put("mimetype", MimeType.MIME_XML.ordinal());
				}

				String url = "http" + (isDevEnabled ? "" : "s") + "://" + domainName
						+ (isDevEnabled ? ":8080/aon-aio" : "")
						+ "/send_email/";
//				String url = "https://" + domainName + "/send_email/";
				LOGGER.info("*** SEND EMAIL URL " + url);
				HttpClientBuilder base = HttpClientBuilder.create();
				HttpClient client = base.build();
				HttpPost post = new HttpPost(url);
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("details", json
						.toString()));
				post.setEntity(new UrlEncodedFormEntity(urlParameters));
				HttpResponse resp = client.execute(post);
				System.out.println(resp);
			}
		} catch (JSONException e) {
			LOGGER.error("Error on mailing", e.getMessage());
		} catch (IOException e) {
			LOGGER.error("Error on mailing", e.getMessage());
		}
	}
	
	public MailAccount getAdminMailAccount(String domainName, Integer domainId, String user) {
		LinkedList<MailAccount> list = AON.getMailAccountList(domainName, domainId, user, 
				f -> f.getDomainProperty().eq(0));
		return list!=null && !list.isEmpty()?list.getFirst():null;
	}
	
}
