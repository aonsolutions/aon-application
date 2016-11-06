package com.code.aon.ui.finance.util.print;

import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PRODUCT_CODE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_PRODUCT_TAX_TYPE_PARAM;
import static com.code.aon.common.enumeration.AppParam.APP_PRINT_REFERENCE_CODE_PARAM;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.sales.Sales;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;

public class InvoiceDetailReportScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDetailReportScriptlet.class.getName());
	
	private static final String PARAMETER_REPORT_LOCALE = "REPORT_LOCALE";
	private static final String PARAMETER_REPORT_RESOURCE_BUNDLE = "REPORT_RESOURCE_BUNDLE";
	private static final String VARIABLE_LINE_DESCRIPTION = "lineDescription";
	private static final String FIELD_ID = "id";
	
	@Override
	public void afterDetailEval() throws JRScriptletException {
		try {
			fillDetailLineDescriptionCode();
		} catch (Exception e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg,e);
			throw new JRScriptletException(msg, e);
		}
	}
	
	private void fillDetailLineDescriptionCode() throws JRScriptletException{
		StringBuilder builder = new StringBuilder();
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			InvoiceDetail invoiceDetail = (InvoiceDetail) invoiceDetailBean.get((Integer)super.getFieldValue(FIELD_ID));
			
			boolean isPrintProductCode = AppParamUtil.getValueAsBoolean(APP_PRINT_PRODUCT_CODE_PARAM);
			boolean isPrintProductTaxType = AppParamUtil.getValueAsBoolean(APP_PRINT_PRODUCT_TAX_TYPE_PARAM);
			boolean isPrintReferenceCode = AppParamUtil.getValueAsBoolean(APP_PRINT_REFERENCE_CODE_PARAM);
			
			if(isPrintProductCode && invoiceDetail.getItem().getProduct()!=null){
				builder.append(getMessage(ICommonMessages.ID)).append(": ");
				builder.append(invoiceDetail.getItem().getProduct().getCode());
			}

			if(isPrintProductTaxType && invoiceDetail.getItem().getProduct()!=null){
				builder.append(builder.length()>0?" - ":"").append("con ");
				builder.append(invoiceDetail.getItem().getProduct().getVat().getType().getName(getReportLocale()));
				builder.append(" al ");
				builder.append(CommonUtil.round(invoiceDetail.getItem().getProduct().getVat().getPercentage()));
				builder.append(" %");
			}
			
			String referenceCode = obtainReferenceCode(invoiceDetail);
			if(isPrintReferenceCode && StringUtils.isNotBlank(referenceCode)){
				builder.append(builder.length()>0?" - ":"");
				builder.append(referenceCode);
			}
			
			if(builder.length()>0){
				builder.append("\n  ");
				builder.append(invoiceDetail.getDescription().replace("\n", "\n  "));
			} else {
				builder.append(invoiceDetail.getDescription());
			}
			
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg);
			throw new JRScriptletException(msg, e);
		}
		
		super.setVariableValue(VARIABLE_LINE_DESCRIPTION, builder.toString());
	}

	private String obtainReferenceCode(InvoiceDetail invoiceDetail) throws JRScriptletException{
		StringBuilder reference = new StringBuilder();
		try {
			Sales sales = null;
			if(invoiceDetail.getSourceTo() instanceof Delivery){
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());
				if(deliveryDetail.getSalesDetail()!=null){
					sales = deliveryDetail.getSalesDetail().getSales();
				}
			} else if(invoiceDetail.getSourceTo() instanceof Sales){
				sales = (Sales) invoiceDetail.getSourceTo();
			}
			
			if(sales!=null){
				reference.append("N. pedido: ");
				reference.append(sales.getReferenceCode());
				if(StringUtils.isNotBlank(sales.getPurchaseReference())){
					reference.append(" / Su referencia: ");
					reference.append(sales.getPurchaseReference());
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg,e);
			throw new JRScriptletException(msg, e);
		}
		return reference.toString();
	}
	
	public String getMessage(String messageKey, Object ... arguments ) throws JRScriptletException {
		ResourceBundle bundle = getReportResourceBundle();
    	String value = bundle.getString(messageKey);
    	if ( arguments.length > 0 ) {
    		MessageFormat mf = new MessageFormat( value, getReportLocale() );
    		value = mf.format( arguments );
    	}
    	return value;
    }
	
	private ResourceBundle getReportResourceBundle() throws JRScriptletException{
		return (ResourceBundle) super.getParameterValue(PARAMETER_REPORT_RESOURCE_BUNDLE);
	}
	
	private Locale getReportLocale() throws JRScriptletException{
		return (Locale) super.getParameterValue(PARAMETER_REPORT_LOCALE);
	}
	
}