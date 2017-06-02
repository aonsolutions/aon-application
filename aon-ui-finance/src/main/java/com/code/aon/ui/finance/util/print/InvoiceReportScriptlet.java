package com.code.aon.ui.finance.util.print;

import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;

import net.sf.jasperreports.engine.JRScriptletException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.registry.RecordData;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.util.ReportScriptlet;
import com.code.aon.ui.util.AonUtil;

public class InvoiceReportScriptlet extends ReportScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceReportScriptlet.class.getName());
	
	/**
	 * REPORT TEMPLATE PARAMETERS
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
						builder.append("Tomo ");
						builder.append(recordData.getVolume());
					}
					if(StringUtils.isNotBlank(recordData.getSection())){
						builder.append(builder.length()>0?", ":"");
						builder.append("Sección ");
						builder.append(recordData.getSection());
					}
					if(StringUtils.isNotBlank(recordData.getPage())){
						builder.append(builder.length()>0?", ":"");
						builder.append("Folio ");
						builder.append(recordData.getPage());
					}
					if(StringUtils.isNotBlank(recordData.getSheet())){
						builder.append(builder.length()>0?", ":"");
						builder.append("Hoja ");
						builder.append(recordData.getSheet());
					}
					if(recordData.getRecordDate()!=null){
						builder.append("con fecha ");
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
			builder.append(AonUtil.getMessage(ICommonMessages.COMPANY_DOCUMENT)).append(": ");
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
	
}