package com.code.aon.ui.finance.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceParamsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PosInvoiceParamsController.class.getName());
	
	private final String PRINT_LOGO 			= "POS_INVOICE_PRINT_LOGO";
	private final String PRINT_TRADENAME 		= "POS_INVOICE_PRINT_TRADENAME";
	private final String PRINT_DIR_STAFF 		= "POS_INVOICE_PRINT_DIR_STAFF";
	private final String PRINT_SELLER_NAME 		= "POS_INVOICE_PRINT_SELLER_NAME";
	private final String PRINT_DOMAIN 			= "POS_INVOICE_PRINT_DOMAIN";
	private final String FOOTER_TEXT 			= "POS_INVOICE_FOOTER_TEXT";
	private final String BUNDLE_NAME = "financeBundle";
	private final String FOOTER_TEXT_CONTENT_MSG_KEY_PREFIX = "finance_posInvoice_footerText_default";
	
	private Map<String, ApplicationParameter> params;

	public boolean isPrintLogo() {
		return new Boolean(params.get(PRINT_LOGO).getValue()).booleanValue();
	}

	public void setPrintLogo(boolean printLogo) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(PRINT_LOGO);
		param.setValue(String.valueOf(printLogo));
		params.put(PRINT_LOGO, param );
	}

	public boolean isPrintTradename() {
		return new Boolean(params.get(PRINT_TRADENAME).getValue()).booleanValue();
	}

	public void setPrintTradename(boolean printTradename) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(PRINT_TRADENAME);
		param.setValue(String.valueOf(printTradename));
		params.put(PRINT_TRADENAME, param );
	}

	public ReportPrintOption getPrintDirStaff() {
		return obtainReportPrintOption(params.get(PRINT_DIR_STAFF).getValue());
	}

	public void setPrintDirStaff(ReportPrintOption printDirStaff) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(PRINT_DIR_STAFF);
		param.setValue(String.valueOf(printDirStaff.ordinal()));
		params.put(PRINT_DIR_STAFF, param );
	}

	public ReportPrintOption getPrintSellerName() {
		return obtainReportPrintOption(params.get(PRINT_SELLER_NAME).getValue());
	}

	public void setPrintSellerName(ReportPrintOption printSellerName) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(PRINT_SELLER_NAME);
		param.setValue(String.valueOf(printSellerName.ordinal()));
		params.put(PRINT_SELLER_NAME, param );
	}

	public boolean isPrintDomain() {
		return new Boolean(params.get(PRINT_DOMAIN).getValue()).booleanValue();
	}

	public void setPrintDomain(boolean printDomain) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(PRINT_DOMAIN);
		param.setValue(String.valueOf(printDomain));
		params.put(PRINT_DOMAIN, param );
	}

	public String getFooterText() {
		return (String) params.get(FOOTER_TEXT).getValue();
	}

	public void setFooterText(String footerText) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(FOOTER_TEXT);
		param.setValue(String.valueOf(footerText));
		params.put(FOOTER_TEXT, param );
	}
	
	public void onInit(ActionEvent event){
		params = new HashMap<String, ApplicationParameter>();
		try {
			params.put(PRINT_LOGO, obtainApplicationParameter(PRINT_LOGO) );
			params.put(PRINT_TRADENAME, obtainApplicationParameter(PRINT_TRADENAME) );
			params.put(PRINT_DIR_STAFF, obtainApplicationParameter(PRINT_DIR_STAFF) );
			params.put(PRINT_SELLER_NAME, obtainApplicationParameter(PRINT_SELLER_NAME) );
			params.put(PRINT_DOMAIN, obtainApplicationParameter(PRINT_DOMAIN) );
			ApplicationParameter footerText = obtainApplicationParameter(FOOTER_TEXT);
			if(footerText.getId()==null){
				footerText.setValue(AonUtil.getMessage(BUNDLE_NAME, FOOTER_TEXT_CONTENT_MSG_KEY_PREFIX));
			}
			params.put(FOOTER_TEXT, footerText );
		} catch (ManagerBeanException e) {
			String msg = "No se han podido iniciar correctamente lo parámetros.";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
		}
	}
	
	public void onAccept(ActionEvent event) throws ManagerBeanException{
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		for(ApplicationParameter param: params.values()){
			appParamBean.insertOrUpdate(param);
		}
	}
	
	public ApplicationParameter obtainApplicationParameter(String paramName) throws ManagerBeanException{
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), paramName);
		Iterator<ITransferObject> iter = appParamBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			return (ApplicationParameter)iter.next();
		} else {
			ApplicationParameter param = new ApplicationParameter();
			param.setName(paramName);
			param.setValue("");
			return param;
		}
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
