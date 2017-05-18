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
	
	private static final String FIELD_ID = "id";
	
	private static final String INVOICE_FOOTER_TEXT = "invoiceFooterText";
	
	public InputStream getBackgroundFile(){
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
		try {
			if(getPrintParamsController().getSaleInvoiceFooter().isPrintSaleInvoiceFooter()){
				return (String) getParameterValue(INVOICE_FOOTER_TEXT);
			}
		} catch (JRScriptletException e) {
			LOGGER.error( e.getMessage(), e);
		}
		return null;
	}
	
	public boolean isPrintDiscountPriceApplied() {
		return getPrintParamsController().getSaleInvoiceParams().isPrintDiscountPriceApplied();
	}
	
	public String getLeftSideText(){
		return getLeftSideText(true);
	}
	
	public String getLeftSideText(boolean printDirStaff){
		return getCompanyRegistrationText(ReportPrintOption.LEFT_SIDE, printDirStaff);
	}
	
	public String getFooterSideText(boolean printDirStaff){
		return getCompanyRegistrationText(ReportPrintOption.FOOTER, printDirStaff);
	}
	
	private String getCompanyRegistrationText(ReportPrintOption printOption, boolean printDirStaff){
		StringBuilder builder = new StringBuilder("");
		if(getPrintName()!=null && getPrintName()==printOption){
			builder.append(getCompanyController().obtainCompany().getName());
		}
		if(printDirStaff){
			try {
				RecordData recordData = getCompanyController().getCompanyRecordData();
				if( isPrintRecordData() && recordData!=null){			
					if(StringUtils.isNotBlank(recordData.getRegistration())){
						builder.append(builder.length()>0?", ":"");
						builder.append(recordData.getRegistration());
					}
					if(StringUtils.isNotBlank(recordData.getVolume())){
						builder.append(builder.length()>0?", ":"");
						builder.append("Tomo ").append(recordData.getVolume());
					}
					if(StringUtils.isNotBlank(recordData.getSection())){
						builder.append(builder.length()>0?", ":"");
						builder.append("Sección ").append(recordData.getSection());
					}
					if(StringUtils.isNotBlank(recordData.getPage())){
						builder.append(builder.length()>0?", ":"");
						builder.append("Folio ").append(recordData.getPage());
					}
					if(StringUtils.isNotBlank(recordData.getSheet())){
						builder.append(builder.length()>0?", ":"");
						builder.append("Hoja ").append(recordData.getSheet());
					}
					if(recordData.getRecordDate()!=null){
						builder.append("con fecha ").append(new SimpleDateFormat("dd/MM/yyyy").format(recordData.getRecordDate()));
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
			builder.append(getCompanyController().obtainCompany().getDocumentCountry()).append("-");
			builder.append(getCompanyController().obtainCompany().getDocument());
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