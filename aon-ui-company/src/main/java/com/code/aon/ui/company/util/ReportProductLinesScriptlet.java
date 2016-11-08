package com.code.aon.ui.company.util;

import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PRODUCT_CODE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PRODUCT_VAT_PARAM;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.common.ICommonMessages;

public class ReportProductLinesScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportProductLinesScriptlet.class.getName());
	
	private static final String PARAMETER_REPORT_LOCALE = "REPORT_LOCALE";
	private static final String PARAMETER_REPORT_RESOURCE_BUNDLE = "REPORT_RESOURCE_BUNDLE";
	private static final String VARIABLE_LINE_DESCRIPTION = "lineDescription";
	private static final String FIELD_ITEM = "item";
	private static final String FIELD_DESCRIPTION = "description";
	
	
	@Override
	public void afterDetailEval() throws JRScriptletException {
		try {
			String description = getFieldDescription();
			String newDescription = buildLineDescription();
			if(newDescription.length()>0){
				newDescription = newDescription.concat("\n  ");
				newDescription = newDescription.concat(description.replace("\n", "\n  "));
			} else {
				newDescription = description;
			}
			setVariableDescription(newDescription);
		} catch (Exception e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg,e);
			throw new JRScriptletException(msg, e);
		}
	}
	
	protected String buildLineDescription() throws JRScriptletException {
		Item item = (Item) super.getFieldValue(FIELD_ITEM);
		boolean isPrintProductCode = AppParamUtil.getValueAsBoolean(APP_PRINT_PRODUCT_CODE_PARAM);
		boolean isPrintProductVatPercent = AppParamUtil.getValueAsBoolean(APP_PRINT_PRODUCT_VAT_PARAM);

		StringBuilder builder = new StringBuilder();
		if(isPrintProductCode && item.getProduct()!=null){
			builder.append(getMessage(ICommonMessages.ID)).append(": ");
			builder.append(item.getProduct().getCode());
		}
		if(isPrintProductVatPercent && item.getProduct()!=null 
				&& item.getProduct().getVat()!=null && item.getProduct().getVat().getType()!=null){
			builder.append(builder.length()>0?" - con ":"");
			builder.append(item.getProduct().getVat().getType().getName(getReportLocale()));
			builder.append(" al ");
			builder.append(CommonUtil.round(item.getProduct().getVat().getPercentage()));
			builder.append(" %");
		}
		return builder.toString();
	}

	private String getMessage(String messageKey, Object ... arguments ) throws JRScriptletException {
		ResourceBundle bundle = getReportResourceBundle();
    	String value = bundle.getString(messageKey);
    	if ( arguments.length > 0 ) {
    		MessageFormat mf = new MessageFormat( value, getReportLocale() );
    		value = mf.format( arguments );
    	}
    	return value;
    }
	
	private ResourceBundle getReportResourceBundle() throws JRScriptletException {
		return (ResourceBundle) super.getParameterValue(PARAMETER_REPORT_RESOURCE_BUNDLE);
	}
	
	private Locale getReportLocale() throws JRScriptletException {
		return (Locale) super.getParameterValue(PARAMETER_REPORT_LOCALE);
	}
	
	protected String getFieldDescription() throws JRScriptletException {
		return (String) super.getFieldValue(FIELD_DESCRIPTION);
	}
	
	protected void setVariableDescription(String newDescription) throws JRScriptletException {
		super.setVariableValue(VARIABLE_LINE_DESCRIPTION, newDescription);
	}
	
}