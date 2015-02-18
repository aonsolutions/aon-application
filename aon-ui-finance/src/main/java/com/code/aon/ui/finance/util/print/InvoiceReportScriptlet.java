package com.code.aon.ui.finance.util.print;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.registry.RecordData;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.CompanySaleInvoiceFooterController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;

public class InvoiceReportScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceReportScriptlet.class.getName());
	
	private CompanyController getCompanyController(){
		return (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
	}

	private CompanySaleInvoiceFooterController getCompanySaleInvoiceFooterController(){
		return (CompanySaleInvoiceFooterController) AonUtil.getRegisteredBean("companySaleInvoiceFooter");
	}
	
	public boolean isPrintHeader() {
		return getCompanyController().isPrintHeader();
	}

	public boolean isPrintLogo() {
		return getCompanyController().isPrintLogo();
	}
	
	public boolean isPrintRecordData() {
		return getCompanyController().isPrintRecordData();
	}
	
	public ReportPrintOption getPrintName() {
		return getCompanyController().getPrintName();
	}
	
	public ReportPrintOption getPrintNif() {
		return getCompanyController().getPrintNif();
	}
	
	public ReportPrintOption getPrintAddress() {
		return getCompanyController().getPrintAddress();
	}
	
	public ReportPrintOption getPrintInternetData() {
		return getCompanyController().getPrintInternetData();
	}
	
	public String getInvoiceFooterText() {
		return getCompanySaleInvoiceFooterController().getText();
	}
	
	public boolean isPrintDiscountPriceApplied() {
		return getCompanyController().isPrintDiscountPriceApplied();
	}
	
	public String getLeftSideText(){
		StringBuilder builder = new StringBuilder("");
		if(getPrintName()!=null && getPrintName()==ReportPrintOption.LEFT_SIDE){
			builder.append(getCompanyController().obtainCompany().getName());
		}
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
		}
		if(getPrintNif()!=null && getPrintNif()==ReportPrintOption.LEFT_SIDE){
			builder.append(builder.length()>0?", ":"");
			builder.append(AonUtil.getMessage(ICommonMessages.COMPANY_DOCUMENT)).append(": ");
			builder.append(getCompanyController().obtainCompany().getDocumentCountry()).append("-");
			builder.append(getCompanyController().obtainCompany().getDocument());
		}
		return builder.toString();
	}
	

}