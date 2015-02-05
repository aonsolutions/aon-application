package com.code.aon.ui.company.event;

import static com.code.aon.common.enumeration.AppParam.APP_FPAYMENT_TEMPLATE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_ADDRESS_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_HEADER_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_INTERNET_DATA_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_LOGO_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_NAME_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_NIF_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_REFERENCE_CODE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_RECORD_DATA_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PRODUCT_CODE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_S_INVOICE_FOOTER_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_SMART_CARD_PARAM;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.enumeration.FinancePaymentTemplate;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompanyAppParamControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
		try {
			companyController.setPrintHeader(companyController.obtainPrintHeader());
			companyController.setPrintRecordData(companyController.obtainPrintRecordData());
			companyController.setSaleInvoiceTemplate(companyController.obtainSaleInvoiceTemplate());
			companyController.setPrintDiscountPriceApplied(companyController.obtainPrintDiscountPriceApplied());
			companyController.setPrintLogo(companyController.obtainPrintLogo());
			companyController.setPrintReferenceCode(companyController.obtainPrintReferenceCode());
			companyController.setPrintProductCode(companyController.obtainPrintProductCode());
			companyController.setPrintName(companyController.obtainPrintName());
			companyController.setPrintNif(companyController.obtainPrintNif());
			companyController.setPrintAddress(companyController.obtainPrintAddress());
			companyController.setPrintInternetData(companyController.obtainPrintInternetData());
			companyController.setPrintSaleInvoiceFooter(companyController.obtainPrintSaleInvoiceFooter());
			companyController.setSmartCard(companyController.obtainSmartCard());
			companyController.setFinancePaymentTemplate(companyController.obtainFinancePaymentTemplate());
			companyController.searchCustomReportTemplate();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
		try {
			updateParams(companyController);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
		try {
			updateParams(companyController);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	private void updateParams( ICompanyController companyController ) throws ManagerBeanException {
		// Sale Invoice template
		CompanyController company = (CompanyController) companyController;
		if(company.isCustomReportTemplate()
				&& StringUtils.isNotBlank(company.getCustomSaleInvoiceTemplateParam().getValue())){
			AppParamUtil.insertParameter(AppParam.REPORT_saleInvoice, company.getCustomSaleInvoiceTemplateParam().getValue());
			AppParamUtil.insertParameter(AppParam.REPORT_invoicePrint, company.getCustomSaleInvoiceTemplateParam().getValue());
			companyController.setSaleInvoiceTemplate(SaleInvoiceTemplate.DEFAULT);
		} else {
			AppParamUtil.removeParameter(AppParam.REPORT_saleInvoice);
			AppParamUtil.removeParameter(AppParam.REPORT_invoicePrint);
			company.setCustomReportTemplate(false);
		}
		updateParam(APP_SALE_INVOICE_TEMPLATE_PARAM, companyController.getSaleInvoiceTemplate());
		
		// Sale Invoice params
		AppParamUtil.insertParameter(APP_PRINT_HEADER_PARAM, companyController.isPrintHeader());
		AppParamUtil.insertParameter(APP_PRINT_RECORD_DATA_PARAM, companyController.isPrintRecordData());
		AppParamUtil.insertParameter(AppParam.APP_PRINT_DISCOUNT_PRICE_APPLIED, companyController.isPrintDiscountPriceApplied());
		AppParamUtil.insertParameter(APP_PRINT_LOGO_PARAM, companyController.isPrintLogo());
		AppParamUtil.insertParameter(APP_PRINT_REFERENCE_CODE_PARAM, companyController.isPrintReferenceCode());
		AppParamUtil.insertParameter(APP_PRINT_PRODUCT_CODE_PARAM, companyController.isPrintProductCode());
		updateParam(APP_PRINT_NAME_PARAM, companyController.getPrintName());
		updateParam(APP_PRINT_NIF_PARAM, companyController.getPrintNif());
		updateParam(APP_PRINT_ADDRESS_PARAM, companyController.getPrintAddress());
		updateParam(APP_PRINT_INTERNET_DATA_PARAM, companyController.getPrintInternetData());
		AppParamUtil.insertParameter(APP_PRINT_S_INVOICE_FOOTER_PARAM, companyController.isPrintSaleInvoiceFooter());
		AppParamUtil.insertParameter(APP_SMART_CARD_PARAM, companyController.isSmartCard());	
		
		// Finance payment
		updateParam(APP_FPAYMENT_TEMPLATE_PARAM, companyController.getFinancePaymentTemplate());
	}
	
	private void updateParam(AppParam appParam, ReportPrintOption value) throws ManagerBeanException {
		AppParamUtil.insertParameter(appParam, (value != null) ? String.valueOf(value.ordinal()) : null);
	}
	
	private void updateParam(AppParam appParam, SaleInvoiceTemplate value) throws ManagerBeanException {
		AppParamUtil.insertParameter(appParam, (value != null) ? value.getValue() : null);
	}

	private void updateParam(AppParam appParam, FinancePaymentTemplate value) throws ManagerBeanException {
		AppParamUtil.insertParameter(appParam, (value != null) ? value.getValue() : null);
	}
	
}