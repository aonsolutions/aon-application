package com.code.aon.ui.company.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;

public class ReportScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportScriptlet.class.getName());
	
	
	protected CompanyController getCompanyController(){
		return (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
	}
	
	public InputStream getLogoFile(){
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
	
	public InputStream getSignatureFile(){
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
	
	public InputStream getSaleInvoiceBackgroundFile(){
		byte[] data = getCompanyController().getSaleInvoiceBackgroundFile().getData();
		if(data != null && data.length>0){
			return new ByteArrayInputStream(data);
		}
		return null;
	}
	public InputStream getSalesBackgroundFile(){
		byte[] data = getCompanyController().getSalesBackgroundFile().getData();
		if(data != null && data.length>0){
			return new ByteArrayInputStream(data);
		}
		return null;
	}
	public InputStream getDeliveryBackgroundFile(){
		byte[] data = getCompanyController().getDeliveryBackgroundFile().getData();
		if(data != null && data.length>0){
			return new ByteArrayInputStream(data);
		}
		return null;
	}
	public InputStream getOfferBackgroundFile(){
		byte[] data = getCompanyController().getOfferBackgroundFile().getData();
		if(data != null && data.length>0){
			return new ByteArrayInputStream(data);
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