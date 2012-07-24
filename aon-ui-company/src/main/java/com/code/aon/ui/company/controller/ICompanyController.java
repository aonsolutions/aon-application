package com.code.aon.ui.company.controller;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.form.IController;

public interface ICompanyController extends IController {

	boolean isPrintHeader();
	void setPrintHeader(boolean printHeader);

	boolean isPrintRecordData();
	void setPrintRecordData(boolean printRecordData);
	
	SaleInvoiceTemplate getSaleInvoiceTemplate();
	void setSaleInvoiceTemplate(SaleInvoiceTemplate saleInvoiceTemplate);
	
	boolean isPrintLogo();
	void setPrintLogo(boolean printLogo);
	
	ReportPrintOption getPrintName();
	void setPrintName(ReportPrintOption printName);
	
	ReportPrintOption getPrintNif();
	void setPrintNif(ReportPrintOption printNif);
	
	ReportPrintOption getPrintAddress();
	void setPrintAddress(ReportPrintOption printAddress);
	
	ReportPrintOption getPrintInternetData();
	void setPrintInternetData(ReportPrintOption printInternetData);
	
	boolean isSmartCard();
	void setSmartCard(boolean smartCard);

	boolean obtainPrintHeader() throws ManagerBeanException;
	boolean obtainPrintRecordData() throws ManagerBeanException;
	SaleInvoiceTemplate obtainSaleInvoiceTemplate() throws ManagerBeanException;
	boolean obtainPrintLogo() throws ManagerBeanException;
	ReportPrintOption obtainPrintName() throws ManagerBeanException;
	ReportPrintOption obtainPrintNif() throws ManagerBeanException;
	ReportPrintOption obtainPrintAddress() throws ManagerBeanException;
	ReportPrintOption obtainPrintInternetData() throws ManagerBeanException;
	boolean obtainSmartCard() throws ManagerBeanException;
	void searchCustomReportTemplate() throws ManagerBeanException;

	ApplicationParameter obtainApplicationParameter(String printHeaderParam)  throws ManagerBeanException;
	void updateParam(String paramName, String value) throws ManagerBeanException;

	RegistryAddress getMainAddress();
	void setPhone(RegistryMedia phone);
	void setFax(RegistryMedia fax);
	void setEmail(RegistryMedia email);
	void setWeb(RegistryMedia web);
	boolean isPhoneDirty();
	RegistryMedia getPhone();
	boolean isFaxDirty();
	RegistryMedia getFax();
	boolean isEmailDirty();
	RegistryMedia getEmail();
	boolean isWebDirty();
	RegistryMedia getWeb();
	boolean isAddressDirty();
	void setAttach(RegistryAttachment obtainRegistryAttachment);

}
