package com.code.aon.ui.finance.controller;

import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_FOOTER_TEXT;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_DIR_STAFF;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_DOMAIN;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_LOGO;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_SELLER_NAME;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_TRADENAME;
import static com.code.aon.ui.finance.controller.IFinanceConstants.BUNDLE_NAME;
import static com.code.aon.ui.finance.controller.IFinanceConstants.FOOTER_TEXT_CONTENT_MSG_KEY_PREFIX;

import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.util.AonUtil;

public class PosInvoiceParamsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PosInvoiceParamsController.class.getName());
	
	private Map<AppParam, ApplicationParameter> params;

	public boolean isPrintPDF() {
		return StringUtils.equals(AppParamUtil.getValue(AppParam.POS_INVOICE_PRINT_OUTPUT), "1");
	}
	
	public boolean isPrintLogo() {
		return new Boolean(params.get(POS_INVOICE_PRINT_LOGO).getValue()).booleanValue();
	}

	public void setPrintLogo(boolean printLogo) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_LOGO);
		param.setValue(String.valueOf(printLogo));
		params.put(POS_INVOICE_PRINT_LOGO, param );
	}

	public boolean isPrintTradename() {
		return new Boolean(params.get(POS_INVOICE_PRINT_TRADENAME).getValue()).booleanValue();
	}

	public void setPrintTradename(boolean printTradename) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_TRADENAME);
		param.setValue(String.valueOf(printTradename));
		params.put(POS_INVOICE_PRINT_TRADENAME, param );
	}

	public ReportPrintOption getPrintDirStaff() {
		return obtainReportPrintOption(params.get(POS_INVOICE_PRINT_DIR_STAFF).getValue());
	}

	public void setPrintDirStaff(ReportPrintOption printDirStaff) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_DIR_STAFF);
		param.setValue(String.valueOf(printDirStaff.ordinal()));
		params.put(POS_INVOICE_PRINT_DIR_STAFF, param );
	}

	public ReportPrintOption getPrintSellerName() {
		return obtainReportPrintOption(params.get(POS_INVOICE_PRINT_SELLER_NAME).getValue());
	}

	public void setPrintSellerName(ReportPrintOption printSellerName) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_SELLER_NAME);
		param.setValue(String.valueOf(printSellerName.ordinal()));
		params.put(POS_INVOICE_PRINT_SELLER_NAME, param );
	}

	public boolean isPrintDomain() {
		return new Boolean(params.get(POS_INVOICE_PRINT_DOMAIN).getValue()).booleanValue();
	}

	public void setPrintDomain(boolean printDomain) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_DOMAIN);
		param.setValue(String.valueOf(printDomain));
		params.put(POS_INVOICE_PRINT_DOMAIN, param );
	}

	public String getFooterText() {
		return (String) params.get(POS_INVOICE_FOOTER_TEXT).getValue();
	}

	public void setFooterText(String footerText) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_FOOTER_TEXT);
		param.setValue(String.valueOf(footerText));
		params.put(POS_INVOICE_FOOTER_TEXT, param );
	}
	
	public void load() {
		params = new HashMap<AppParam, ApplicationParameter>();
		try {
			params.put(POS_INVOICE_PRINT_LOGO, obtainApplicationParameter(POS_INVOICE_PRINT_LOGO) );
			params.put(POS_INVOICE_PRINT_TRADENAME, obtainApplicationParameter(POS_INVOICE_PRINT_TRADENAME) );
			params.put(POS_INVOICE_PRINT_DIR_STAFF, obtainApplicationParameter(POS_INVOICE_PRINT_DIR_STAFF) );
			params.put(POS_INVOICE_PRINT_SELLER_NAME, obtainApplicationParameter(POS_INVOICE_PRINT_SELLER_NAME) );
			params.put(POS_INVOICE_PRINT_DOMAIN, obtainApplicationParameter(POS_INVOICE_PRINT_DOMAIN) );
			ApplicationParameter footerText = obtainApplicationParameter(POS_INVOICE_FOOTER_TEXT);
			if ( footerText.getId() == null ) {
				footerText.setValue(AonUtil.getMessage(BUNDLE_NAME, FOOTER_TEXT_CONTENT_MSG_KEY_PREFIX));
			}
			params.put(POS_INVOICE_FOOTER_TEXT, footerText );
		} catch (ManagerBeanException e) {
			String msg = "No se han podido iniciar correctamente lo parámetros.";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
		}		
	}
	
	public void onInit(ActionEvent event){
		load();
	}
	
	public void onAccept(ActionEvent event) throws ManagerBeanException{
		for(ApplicationParameter param: params.values()) {
			AppParamUtil.insertParameter(param);
		}
	}
	
	public ApplicationParameter obtainApplicationParameter(AppParam appParam) throws ManagerBeanException{
		ApplicationParameter param = AppParamUtil.getParameter(appParam);
		if ( param == null ) {
			param = new ApplicationParameter();
			param.setName(appParam.getValue());
			param.setValue("");
		}
		return param;
	}
	
	private ReportPrintOption obtainReportPrintOption(String value) {
		for(ReportPrintOption o: ReportPrintOption.values()){
			if(StringUtils.isNotBlank(value) && o.ordinal() == Integer.parseInt(value)){
				return o;
			}
		}
		return null;
	}
	
}
