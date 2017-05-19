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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.ImageUtil;
import com.code.aon.company.Company;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;
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
		loadCompany();
		getSaleInvoiceParams().init();
		getReportBackground().init();
		getSaleInvoiceFooter().init();
	}	

	public void accept(ActionEvent event) {
		getSaleInvoiceParams().accept();
		getSaleInvoiceFooter().accept();
		getReportBackground().accept();
	}

	private void loadCompany() {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		company = (Company) controller.getTo();
	}	
	
	
	private String getDomainName() {
		return AonUtil.getDomainName();
	}
	private Integer getDomainId() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return ds.getDomainId();
	}
	private String getUser() {
		return AonUtil.getAuthPrincipal().getShortName();
	}
	
	private byte[] getData(Attach attach) {
		byte[] data;
		if (attach != null && attach.getData() != null) {
			data = attach.getData();
		} else if(attach.getDriveId()!=null){
			data = DriveUtils.getByteFile(getDomainName(), getDomainId(),
					getUser(), attach.getDriveId(), attach.getId());
		} else {
			data = "".getBytes();
		}
		return data;
	}
	
	
	
	
	
	
	/*
	 * SALE INVOICE PARAMS
	 */
	public class SaleInvoiceParams {
		
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
			File customDirectory = new File( CUSTOM_REPORT_TEMPLATE_PATH );
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
		
		
		public void init() {
			setSaleInvoiceTemplate(obtainSaleInvoiceTemplate());
			searchCustomReportTemplate();

			setPrintName(getReportPrintOptionValue(APP_PRINT_NAME_PARAM));
			setPrintNif(getReportPrintOptionValue(APP_PRINT_NIF_PARAM));
			setPrintAddress(getReportPrintOptionValue(APP_PRINT_ADDRESS_PARAM));
			setPrintInternetData(getReportPrintOptionValue(APP_PRINT_INTERNET_DATA_PARAM));
			
			setPrintReferenceCode(AppParamUtil.getValueAsBoolean(APP_PRINT_REFERENCE_CODE_PARAM));
			setPrintDiscountPriceApplied(AppParamUtil.getValueAsBoolean(APP_PRINT_DISCOUNT_PRICE_APPLIED));
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
	public class SaleInvoiceFooter {
		
		private boolean printSaleInvoiceFooter;
		
		private String text;

		private Attach attach;
		
		public boolean isPrintSaleInvoiceFooter() {
			return printSaleInvoiceFooter;
		}

		public void setPrintSaleInvoiceFooter(boolean printSaleInvoiceFooter) {
			this.printSaleInvoiceFooter = printSaleInvoiceFooter;
		}
		
		public Attach getAttach() {
			return attach;
		}
		
		public void resetAttach() {
			attach = new Attach();
		}
		
		private void init() {
			setPrintSaleInvoiceFooter(AppParamUtil.getValueAsBoolean(APP_PRINT_S_INVOICE_FOOTER_PARAM));
			attach = AON.getAttach(
					getDomainName(),
					getDomainId(),
					getUser(),
					f -> f.getDomainProperty()
							.eq(getDomainId())
							.and(f.getTypeProperty().eq(
									RegistryAttachmentType.INVOICE_FOOTER_TEXT
											.value())), AttachType.REGISTRY);
			setText(new String(getData(attach)));
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
			
			if(attach.getId()==null){
				Integer id = AON.insertAttach(getDomainName(), getDomainId(), getUser(), attach);
				attach.setId(id);
			} else {
				AON.updateAttach(getDomainName(), getDomainId(), getUser(), attach);
			}
			
		}
		
		private void completeAttachInfo() {
			completeAttachInfo(getText().getBytes());
		}

		private void completeAttachInfo(byte[] data) {
			attach.setDomain(new Domain().setId(getDomainId()));
			attach.setConfidential(false);
			attach.setAttachType(AttachType.REGISTRY);
			attach.setAttachModule(getCompany().getId());
			attach.setData(data);
			attach.setType(RegistryAttachmentType.INVOICE_FOOTER_TEXT.value());
			attach.setDescription(AonUtil.getMessage(COMPANY_SALE_INVOICE_FOOTER_TEXT));
			attach.setDate(new Date());
			attach.setMimeType(MimeType.TXT);
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
	public class ReportBackground {
		
		private final int BACKGROUND_WIDTH = 535;
		
		private final int BACKGROUND_HEIGHT = 802;
		
		private AonFile saleInvoiceBackgroundFile;
		private AonFile deliveryBackgroundFile;
		private AonFile salesBackgroundFile;
		private AonFile offerBackgroundFile;
		
		private Attach saleInvoiceBackgroundAttach;
		private Attach deliveryBackgroundAttach;
		private Attach salesBackgroundAttach;
		private Attach offerBackgroundAttach;
		
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
			adjustImage(aonFile);
			setSaleInvoiceBackgroundFile(aonFile);
			saleInvoiceBackgroundAttach.setData(aonFile.getData());
		}
		public void deliveryBackgroundFileUploaded(UploadEvent event) {
			AonFile aonFile = AttachmentUtil.fileUploaded(event);
			adjustImage(aonFile);
			setDeliveryBackgroundFile(aonFile);
			deliveryBackgroundAttach.setData(aonFile.getData());
		}
		public void salesBackgroundFileUploaded(UploadEvent event) {
			AonFile aonFile = AttachmentUtil.fileUploaded(event);
			adjustImage(aonFile);
			setSalesBackgroundFile(aonFile);
			salesBackgroundAttach.setData(aonFile.getData());
		}
		public void offerBackgroundFileUploaded(UploadEvent event) {
			AonFile aonFile = AttachmentUtil.fileUploaded(event);
			adjustImage(aonFile);
			setOfferBackgroundFile(aonFile);
			offerBackgroundAttach.setData(aonFile.getData());
		}
		
		private void adjustImage(AonFile aonFile) {
			if ((aonFile != null) && aonFile.isDirty() ) {
				BufferedImage image = ImageUtil.getBufferedImage( aonFile.getData() );
				BufferedImage newImage = ImageUtil.scale(image, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					ImageIO.write(newImage, aonFile.getMimeType().getExtension(), baos);
					aonFile.setData(baos.toByteArray());
				} catch (IOException e) {
					LOGGER.error("Error when trying to adjust report background image", e);
				}
			}
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
			saleInvoiceBackgroundAttach = new Attach();
			offerBackgroundAttach = new Attach();
			deliveryBackgroundAttach = new Attach();
			salesBackgroundAttach = new Attach();
			
			String[] reportKeys = { ICompanyConstants.SALE_INVOICE_REPORT_KEY,
					ICompanyConstants.OFFER_REPORT_KEY,
					ICompanyConstants.DELIVERY_REPORT_KEY,
					ICompanyConstants.SALES_REPORT_KEY };
		
			AON.getAttachStream(
					getDomainName(),
					getDomainId(),
					getUser(),
					f -> f.getDomainProperty()
						.eq(getDomainId())
						.and(f.getTypeProperty()
							.eq(RegistryAttachmentType.REPORT_BACKGROUND
									.value())
							.and(f.getDescriptionProperty().in(reportKeys))),
					AttachType.REGISTRY, true)
					.forEach(attach -> {
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
		
		private void acceptAttach(AonFile aonFile, Attach attach, String reportKey){
			if(aonFile!=null){
				if(aonFile.getData()==null){
					if(attach.getId()!=null){
						if(attach.getDriveId()!=null){
							DriveUtils.deleteFile(getDomainName(), getDomainId(), getUser(), attach.getDriveId());
						}
						AON.deleteAttach(getDomainName(), getDomainId(), getUser(), f -> f.getIdProperty().eq(attach.getId()), AttachType.REGISTRY);
					}
				} else {
					if(attach.getData()!=null){						
						if(attach.getId()!=null){
							AON.updateAttach(getDomainName(), getDomainId(), getUser(), attach);
							if(attach.getDriveId()!=null){
								DriveUtils.deleteFile(getDomainName(), getDomainId(), getUser(), attach.getDriveId());
							}
						} else {
							insertAttach(aonFile, reportKey);
						}
					}
				}
				
			}
		}
		
		
		private void insertAttach(AonFile aonFile, String name) {
			if ((aonFile != null) && aonFile.isDirty() ) {
				checkAonFile(aonFile);
				Attach attach = new Attach();
				attach.setDomain(new Domain().setId(getDomainId()));
				attach.setConfidential(false);
				attach.setAttachType(AttachType.REGISTRY);
				attach.setDate(new Date());
				attach.setType(RegistryAttachmentType.REPORT_BACKGROUND.value());
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription(name);
				attach.setAttachModule(getCompany().getId());
				attach.setMimeType(MimeType.getByExtension(aonFile.getMimeType().getExtension()));
				AON.insertAttach(getDomainName(), getDomainId(), getUser(), attach);
				aonFile = selectAttach(attach);
			}
		}

		private AonFile selectAttach(Attach attach) {
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
	
}
