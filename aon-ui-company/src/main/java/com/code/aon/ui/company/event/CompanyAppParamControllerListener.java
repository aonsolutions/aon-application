package com.code.aon.ui.company.event;

import static com.code.aon.common.enumeration.AppParam.AON_DSI_LOADER_ENABLED;
import static com.code.aon.common.enumeration.AppParam.AON_HELPDESK_ENABLED;
import static com.code.aon.common.enumeration.AppParam.APP_FPAYMENT_TEMPLATE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_ITEM_TAG_TEMPLATE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_MANUFACT_TEMPLATE_TAG_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_HEADER_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_LOGO_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PRODUCT_CODE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PRODUCT_VAT_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_RECORD_DATA_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PROJECT_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_SMART_CARD_PARAM;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.enumeration.FinancePaymentTemplate;
import com.code.aon.company.enumeration.ItemTagTemplate;
import com.code.aon.config.Tag;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.company.controller.ICompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CompanyAppParamControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
		try {
			companyController.setPrintHeader(companyController.obtainPrintHeader());
			companyController.setPrintRecordData(companyController.obtainPrintRecordData());
			companyController.setPrintLogo(companyController.obtainPrintLogo());
			companyController.setPrintProject(companyController.obtainPrintProject());
			companyController.setPrintProductCode(companyController.obtainPrintProductCode());
			companyController.setPrintProductVatPercent(companyController.obtainPrintProductVatPercent());
			companyController.setSmartCard(companyController.obtainSmartCard());
			companyController.setFinancePaymentTemplate(companyController.obtainFinancePaymentTemplate());
			companyController.setItemTagTemplate(companyController.obtainItemTagTemplate());
			companyController.setItemTagDefaultText(companyController.obtainItemTagDefaultText());
			companyController.setItemTagBarcodePattern(companyController.obtainItemTagBarcodePattern());
			companyController.setManufacturingOrderTemplateTag(companyController.obtainManufacturingOrderTemplateTag());
			companyController.setHelpdeskEnabled(companyController.obtainHelpdeskEnabled());
			companyController.setDsiLoaderEnabled(companyController.obtainDsiLoaderEnabled());
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
		
		// Sale Invoice params
		AppParamUtil.insertParameter(APP_PRINT_HEADER_PARAM, companyController.isPrintHeader());
		AppParamUtil.insertParameter(APP_PRINT_RECORD_DATA_PARAM, companyController.isPrintRecordData());
		AppParamUtil.insertParameter(APP_PRINT_LOGO_PARAM, companyController.isPrintLogo());
		AppParamUtil.insertParameter(APP_PRINT_PROJECT_PARAM, companyController.isPrintProject());
		AppParamUtil.insertParameter(APP_PRINT_PRODUCT_CODE_PARAM, companyController.isPrintProductCode());
		AppParamUtil.insertParameter(APP_PRINT_PRODUCT_VAT_PARAM, companyController.isPrintProductVatPercent());
		AppParamUtil.insertParameter(APP_SMART_CARD_PARAM, companyController.isSmartCard());	
		
		// Finance payment
		updateParam(APP_FPAYMENT_TEMPLATE_PARAM, companyController.getFinancePaymentTemplate());
		
		// Item Tag
		updateParam(APP_ITEM_TAG_TEMPLATE_PARAM, companyController.getItemTagTemplate());
		updateParam(AppParam.APP_ITEM_TAG_TEXT_PARAM, companyController.getItemTagDefaultText());
		if(StringUtils.isNotBlank(companyController.getItemTagBarcodePattern())){
			if(checkItemTagBarcodePattern(companyController.getItemTagBarcodePattern())){
				updateParam(AppParam.APP_ITEM_TAG_BARCODE_PARAM, companyController.getItemTagBarcodePattern());
			} else {
				AonUtil.addErrorMessage("Invalid barcode pattern!");
			}
		}
		
		// Manufacturing Order
		updateParam(APP_MANUFACT_TEMPLATE_TAG_PARAM, companyController.getManufacturingOrderTemplateTag());

		// Helpdesk Enabled
		AppParamUtil.insertParameter(AON_HELPDESK_ENABLED, companyController.isHelpdeskEnabled());
		
		// DSI Loader Enabled
		AppParamUtil.insertParameter(AON_DSI_LOADER_ENABLED, companyController.isDsiLoaderEnabled());
		
	}
	
	private boolean checkItemTagBarcodePattern(String itemTagBarcodePattern) {
		final String[] VALID_VAR_NAMES = {"codigo_barras", "lote", "fecha_caducidad"}; 
		final Pattern TAG_REGEX = Pattern.compile("\\$\\{(.+?)\\}");
		if(StringUtils.isNotBlank(itemTagBarcodePattern)){
			Matcher matcher = TAG_REGEX.matcher(itemTagBarcodePattern);
			boolean validPattern = true;
			while (matcher.find() && validPattern) {
				if( !ArrayUtils.contains(VALID_VAR_NAMES, matcher.group(1)) ){
					validPattern = false;
				}
			}
			return validPattern;
		}
		return false;
	}


	private void updateParam(AppParam appParam, FinancePaymentTemplate value) throws ManagerBeanException {
		AppParamUtil.insertParameter(appParam, (value != null) ? value.getValue() : null);
	}
	
	private void updateParam(AppParam appParam, ItemTagTemplate value) throws ManagerBeanException {
		AppParamUtil.insertParameter(appParam, (value != null) ? value.getValue() : null);
	}
	
	private void updateParam(AppParam appParam, Tag value) throws ManagerBeanException {
		AppParamUtil.insertParameter(appParam, (value != null) ? value.getId() : null);
	}
	
	private void updateParam(AppParam appParam, String value) throws ManagerBeanException {
		AppParamUtil.insertParameter(appParam, value);
	}
	
}