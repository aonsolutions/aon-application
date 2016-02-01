package com.code.aon.ui.company.controller;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.enumeration.FinancePaymentTemplate;
import com.code.aon.company.enumeration.ItemTagTemplate;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.config.Scope;
import com.code.aon.config.Tag;
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

	boolean isPrintDiscountPriceApplied();
	void setPrintDiscountPriceApplied(boolean PrintDiscountPriceApplied);
	
	FinancePaymentTemplate getFinancePaymentTemplate();
	void setFinancePaymentTemplate(FinancePaymentTemplate FinancePaymentTemplate);
	
	ItemTagTemplate getItemTagTemplate();
	void setItemTagTemplate(ItemTagTemplate itemTagTemplate);
	
	String getItemTagDefaultText();
	void setItemTagDefaultText(String itemTagDefaultText);
	
	String getItemTagBarcodePattern();
	void setItemTagBarcodePattern(String iitemTagBarcodePattern);
	
	Tag getManufacturingOrderTemplateTag();
	void setManufacturingOrderTemplateTag(Tag manufacturingOrderTemplateTag);
	
	boolean isPrintLogo();
	void setPrintLogo(boolean printLogo);

	boolean isPrintProductCode();
	void setPrintProductCode(boolean printProductCode);

	boolean isPrintReferenceCode();
	void setPrintReferenceCode(boolean printReferenceCode);
	
	boolean isPrintProject();
	void setPrintProject(boolean printReferenceCode);
	
	ReportPrintOption getPrintName();
	void setPrintName(ReportPrintOption printName);
	
	ReportPrintOption getPrintNif();
	void setPrintNif(ReportPrintOption printNif);
	
	ReportPrintOption getPrintAddress();
	void setPrintAddress(ReportPrintOption printAddress);
	
	ReportPrintOption getPrintInternetData();
	void setPrintInternetData(ReportPrintOption printInternetData);
	
	boolean isPrintSaleInvoiceFooter();
	void setPrintSaleInvoiceFooter(boolean printSaleInvoiceFooter);
	
	boolean isSmartCard();
	void setSmartCard(boolean smartCard);

	Scope getScope();
	void setScope(Scope scope);	
	
	boolean isActive();
	void setActive(boolean active);
	
	boolean isHelpdeskEnabled();
	void setHelpdeskEnabled(boolean helpdeskEnabled);
	
	boolean obtainPrintHeader() throws ManagerBeanException;
	boolean obtainPrintRecordData() throws ManagerBeanException;
	SaleInvoiceTemplate obtainSaleInvoiceTemplate() throws ManagerBeanException;
	boolean obtainPrintDiscountPriceApplied() throws ManagerBeanException;
	FinancePaymentTemplate obtainFinancePaymentTemplate() throws ManagerBeanException;
	ItemTagTemplate obtainItemTagTemplate() throws ManagerBeanException;
	String obtainItemTagDefaultText() throws ManagerBeanException;
	String obtainItemTagBarcodePattern() throws ManagerBeanException;
	Tag obtainManufacturingOrderTemplateTag() throws ManagerBeanException;
	boolean obtainPrintLogo() throws ManagerBeanException;
	boolean obtainPrintReferenceCode() throws ManagerBeanException;
	boolean obtainPrintProject() throws ManagerBeanException;
	boolean obtainPrintProductCode() throws ManagerBeanException;
	ReportPrintOption obtainPrintName() throws ManagerBeanException;
	ReportPrintOption obtainPrintNif() throws ManagerBeanException;
	ReportPrintOption obtainPrintAddress() throws ManagerBeanException;
	ReportPrintOption obtainPrintInternetData() throws ManagerBeanException;
	boolean obtainPrintSaleInvoiceFooter() throws ManagerBeanException;
	boolean obtainSmartCard() throws ManagerBeanException;
	void searchCustomReportTemplate() throws ManagerBeanException;
	boolean obtainHelpdeskEnabled() throws ManagerBeanException;

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
	void setLogoAttach(RegistryAttachment obtainRegistryAttachment);
	void setSignatureAttach(RegistryAttachment obtainRegistryAttachment);

}
