package com.code.aon.ui.company.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.controller.PrintParametersController;
import com.code.aon.ui.util.AonUtil;

public class ReportScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportScriptlet.class.getName());
	
	
	protected CompanyController getCompanyController(){
		return (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
	}

	protected PrintParametersController getPrintParamsController() {
		return (PrintParametersController) AonUtil
				.getRegisteredBean(ICompanyConstants.PRINT_PARAMETERS_CONTROLLER_NAME);
	}
	
	@Override
	public void beforeReportInit() throws JRScriptletException {
		super.beforeReportInit();
//		getPrintParamsController().onInit(null);
	}
	
	@Override
	public void afterReportInit() throws JRScriptletException {
		super.afterReportInit();
//		getPrintParamsController().onInit(null);
	}
	
	public Company getCompany(){
		return (Company) getCompanyController().getTo();
	}
	
	public RegistryAddress getAddress() throws ManagerBeanException {
		return getCompanyController().obtainAddress();
	}
	
	public RegistryMedia getPhone() throws ManagerBeanException {
		return getCompanyController().obtainPhone();
	}
	
	public RegistryMedia getFax() throws ManagerBeanException {
		return getCompanyController().obtainFax();
	}
	
	public InputStream getLogoFile() {
		try {
			return getCompanyController().getAttachAsInputStream();
		} catch (ManagerBeanException e) {
			String msg = "ERROR: imposible obtener los datos de la factura al generar su informe";
			LOGGER.error(msg,e);
		} catch (IOException e) {
			String msg = "ERROR: imposible obtener los datos de la factura al generar su informe";
			LOGGER.error(msg,e);
		}
		return null;
	}
	
	public InputStream getSignatureFile() {
		try {
			return getCompanyController().getSignatureAttachAsInputStream();
		} catch (ManagerBeanException e) {
			String msg = "ERROR: imposible obtener los datos de la factura al generar su informe";
			LOGGER.error(msg,e);
		} catch (IOException e) {
			String msg = "ERROR: imposible obtener los datos de la factura al generar su informe";
			LOGGER.error(msg,e);
		}
		return null;
	}
	
	public InputStream getSaleInvoiceBackgroundFile() {
		if(getPrintParamsController().getReportBackground().getSaleInvoiceBackgroundFile()!=null){
			byte[] data = getPrintParamsController().getReportBackground().getSaleInvoiceBackgroundFile().getData();
			if(data != null && data.length>0){
				return new ByteArrayInputStream(data);
			}
		}
		return null;
	}
	public InputStream getSalesBackgroundFile() {
		if(getPrintParamsController().getReportBackground().getSalesBackgroundFile()!=null){
			byte[] data = getPrintParamsController().getReportBackground().getSalesBackgroundFile().getData();
			if(data != null && data.length>0){
				return new ByteArrayInputStream(data);
			}
		}
		return null;
	}
	public InputStream getDeliveryBackgroundFile() {
		if(getPrintParamsController().getReportBackground().getDeliveryBackgroundFile()!=null){
			byte[] data = getPrintParamsController().getReportBackground().getDeliveryBackgroundFile().getData();
			if(data != null && data.length>0){
				return new ByteArrayInputStream(data);
			}
		}
		return null;
	}
	public InputStream getOfferBackgroundFile() {
		if(getPrintParamsController().getReportBackground().getOfferBackgroundFile()!=null){
			byte[] data = getPrintParamsController().getReportBackground().getOfferBackgroundFile().getData();
			if(data != null && data.length>0){
				return new ByteArrayInputStream(data);
			}
		}
		return null;
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
	
	public boolean isPrintProject() {
		return getCompanyController().isPrintProject();
	}
	
}