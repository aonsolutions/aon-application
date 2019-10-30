package com.code.aon.ui.finance.util.print;

import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.RecordData;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.util.ReportScriptlet;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.tedi.AonParser;
import net.sf.jasperreports.engine.JRScriptletException;

public class InvoiceReportScriptlet extends ReportScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceReportScriptlet.class.getName());
	
	/**
	 * REPORT CUSTOM PARAMETERS
	 */
	public static  final String PARAM_PRINT_NAME = "printName";
	public static  final String PARAM_PRINT_NIF = "printNif";
	public static  final String PARAM_PRINT_ADDRESS = "printAddress";
	public static  final String PARAM_PRINT_INTERNET_DATA = "printInternetData";
	public static  final String PARAM_PRINT_DISCOUNT_PRICE_APPLIED = "printDiscountPriceApplied";
	public static  final String PARAM_INVOICE_FOOTER_TEXT = "invoiceFooterText";
	
	/**
	 * REPORT TEMPLATE FIELDS
	 */
	public static final String FIELD_ID = "id";
	public static final String FIELD_ADDRESS = "address";
	
	/**
	 * REPORT TEMPLATE VARIABLES
	 */
	public static final String PAGE_NUMBER = "PAGE_NUMBER";
	
	/**
	 * CALCULATED VALUES
	 */
	private int invoicePageNumber = -1;
	private int currentInvoice = -1;
	private int calculatedPageNumber = 0;
	private Map<Integer, Integer> pagesPerInvoice = null;
	private Map<Integer, Integer> invoicePage = null;
	
	
	@Override
	protected void loadParams() throws ManagerBeanException {
		super.loadParams();
		setParameter(PARAM_PRINT_NAME, getPrintParamsController().getSaleInvoiceParams().getPrintName());
		setParameter(PARAM_PRINT_NIF, getPrintParamsController().getSaleInvoiceParams().getPrintNif());
		setParameter(PARAM_PRINT_ADDRESS, getPrintParamsController().getSaleInvoiceParams().getPrintAddress());
		setParameter(PARAM_PRINT_INTERNET_DATA, getPrintParamsController().getSaleInvoiceParams().getPrintInternetData());
		setParameter(PARAM_PRINT_DISCOUNT_PRICE_APPLIED, getPrintParamsController().getSaleInvoiceParams().isPrintDiscountPriceApplied());
		setParameter(PARAM_INVOICE_FOOTER_TEXT, getPrintParamsController().getSaleInvoiceFooter().getText());
	}
	@Override
	public void beforeReportInit() throws JRScriptletException {
		super.beforeReportInit();
		invoicePageNumber = -1;
		currentInvoice = -1;
		calculatedPageNumber = 0;
		pagesPerInvoice = new HashMap<>();
		invoicePage = new HashMap<>();
	}
	@Override
	public void beforePageInit() throws JRScriptletException {
		super.beforePageInit();
		if(currentInvoice<0 || currentInvoice!=(int)getFieldValue(FIELD_ID)) {
			currentInvoice = (int)getFieldValue(FIELD_ID);
			invoicePageNumber = 0;
		}
		
		invoicePageNumber++;
		pagesPerInvoice.put(currentInvoice, invoicePageNumber);
		
		String _pageNum = String.valueOf(getVariableValue(PAGE_NUMBER));
		Integer pageNum = NumberUtils.isNumber(_pageNum) ? Integer.valueOf(_pageNum) : 0;
		invoicePage.put(pageNum, currentInvoice);
	}
	
	public int getInvoicePageNumber() {
		return invoicePageNumber;
	}
	public Integer consumeCurrentPageInvoiceTotal() {
		return pagesPerInvoice.get(invoicePage.get(calculatedPageNumber++));
	}
	
	public InputStream getBackgroundFile() {
		return this.getSaleInvoiceBackgroundFile();
	}
	
	public ReportPrintOption getPrintName() {
		return getPrintParamsController().getSaleInvoiceParams().getPrintName();
	}
	
	public ReportPrintOption getPrintNif() {
		return getPrintParamsController().getSaleInvoiceParams().getPrintNif();
	}
	
	public ReportPrintOption getPrintAddress() {
		return getPrintParamsController().getSaleInvoiceParams().getPrintAddress();
	}
	
	public ReportPrintOption getPrintInternetData() {
		return getPrintParamsController().getSaleInvoiceParams().getPrintInternetData();
	}
	
	public String getInvoiceFooterText() {
		return getPrintParamsController().getSaleInvoiceFooter().getText();
	}
	
	public boolean isPrintDiscountPriceApplied() {
		return getPrintParamsController().getSaleInvoiceParams().isPrintDiscountPriceApplied();
	}
	
	public String getLeftSideText() {
		return getLeftSideText(true);
	}
	
	public String getFooterSideText() {
		return getFooterSideText(true);
	}
	
	public String getLeftSideText(boolean printDirStaff) {
		return getCompanyRegistrationText(ReportPrintOption.LEFT_SIDE, printDirStaff);
	}
	
	public String getFooterSideText(boolean printDirStaff) {
		return getCompanyRegistrationText(ReportPrintOption.FOOTER, printDirStaff);
	}
	
	private String getCompanyRegistrationText(ReportPrintOption printOption, boolean printDirStaff) {
		StringBuilder builder = new StringBuilder("");
		if(getPrintName()!=null && getPrintName()==printOption){
			builder.append(getCompany().getName());
		}
		if(printDirStaff){
			try {
				RecordData recordData = super.getCompanyRecordData();
				if( isPrintRecordData() && recordData!=null){			
					if(StringUtils.isNotBlank(recordData.getRegistration())){
						builder.append(builder.length()>0?", ":"");
						builder.append(recordData.getRegistration());
					}
					if(StringUtils.isNotBlank(recordData.getVolume())){
						builder.append(builder.length()>0?", ":"");
						builder.append(getMessage("registry_rd_volume") + " ");
						builder.append(recordData.getVolume());
					}
					if(StringUtils.isNotBlank(recordData.getSection())){
						builder.append(builder.length()>0?", ":"");
						builder.append(getMessage("registry_rd_section") + " ");
						builder.append(recordData.getSection());
					}
					if(StringUtils.isNotBlank(recordData.getPage())){
						builder.append(builder.length()>0?", ":"");
						builder.append(getMessage("registry_rd_page") + " ");
						builder.append(recordData.getPage());
					}
					if(StringUtils.isNotBlank(recordData.getSheet())){
						builder.append(builder.length()>0?", ":"");
						builder.append(getMessage("registry_rd_sheet") + " ");
						builder.append(recordData.getSheet());
					}
					if(recordData.getRecordDate()!=null){
						builder.append(", " + getMessage("registry_rd_record_date") + " ");
						builder.append(new SimpleDateFormat("dd/MM/yyyy").format(recordData.getRecordDate()));
					}
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("ERROR - No se ha podido obtener los datos registrales de la empresa [Imprimiendo factura de venta]");
				return "";
			}
		}
		if(getPrintNif()!=null && getPrintNif()==printOption){
			builder.append(builder.length()>0?", ":"");
			builder.append(getMessage(ICommonMessages.COMPANY_DOCUMENT)).append(": ");
			builder.append(getCompany().getDocumentCountry()).append("-");
			builder.append(getCompany().getDocument());
		}
		return builder.toString();
	}
	
	public String getCustomerAccountNumber() throws JRScriptletException{
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Invoice invoice = (Invoice) invoiceBean.get((Integer)super.getFieldValue(FIELD_ID));
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Customer customer = (Customer) customerBean.get(invoice.getRegistry().getId());
			if(customer!=null){
				return customer.getAccount().getCode();
			}
		} catch (ManagerBeanException e) {
			String msg = "ERROR: imposible obtener los datos de la factura al generar su informe";
			LOGGER.error(msg,e);
		}
		return null;
	}
	
	public String getInvoiceProvince() throws JRScriptletException{
		IAddress raddress = (IAddress) super.getFieldValue(FIELD_ADDRESS);
		StringBuilder builder = new StringBuilder("");
		if(raddress!=null && raddress.getGeozone()!=null && raddress.getGeozone().getId()!=null){
			builder.append(raddress.getGeozone()!=null?raddress.getGeozone().getName():"");
			try {
				if(!raddress.getGeozone().getGeoZoneCountry().getId().equals(getAddress().getGeozone().getGeoZoneCountry().getId())){
					builder.append(raddress.getGeozone().getGeoZoneCountry()!=null?" - "+raddress.getGeozone().getGeoZoneCountry().getName():"");		
				}
			} catch (Exception e) {
				String msg = "ERROR: no se ha podido obtener la provincia";
				LOGGER.error(msg,e);
			}
		}
		return builder.toString();
	}
	
	public String getTedi() throws JRScriptletException {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		com.esferalia.aon.occam.api.model.Domain domain = 
				AON.getDomain(domainName, domainId, login);
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Invoice invoice = (Invoice) invoiceBean.get((Integer)super.getFieldValue(FIELD_ID));			
			AonParser ap = new AonParser();
			TediInvoice ti = ap.aon2Tedi(domain, login, invoice.getId());
			return TediInvoiceJSON.toJSON(ti).toString();
		} catch (ManagerBeanException e) {
			String msg = "ERROR: no se ha podido convertir la factura";
			LOGGER.error(msg,e);
		}
		return null;
	}
	
}