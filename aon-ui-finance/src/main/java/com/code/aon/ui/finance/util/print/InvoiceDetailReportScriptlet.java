package com.code.aon.ui.finance.util.print;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.sales.Sales;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;

public class InvoiceDetailReportScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDetailReportScriptlet.class.getName());
	
	private final String VARIABLE_LINE_DESCRIPTION = "lineDescription";
	private final String FIELD_ID = "id";
	private final String PRINT_REFERENCE_CODE = "printReferenceCode";
	private final String PRINT_PRODUCT_CODE = "printProductCode";

	
	@Override
	public void afterDetailEval() throws JRScriptletException {
		try {
			fillDetailLineDescriptionCode();
		} catch (Exception e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg,e);
		}
	}

	private boolean getParameterAsBoolean( String param ) throws JRScriptletException {
		Boolean value = (Boolean) getParameterValue(param, true);
		return ( value != null) ? value.booleanValue() : false;		
	}
	
	private boolean isPrintProductCode() throws JRScriptletException {
		return getParameterAsBoolean(PRINT_PRODUCT_CODE);
	}
	
	private boolean isPrintReferenceCode() throws JRScriptletException {
		return getParameterAsBoolean(PRINT_REFERENCE_CODE);
	}
		
	private void fillDetailLineDescriptionCode() throws JRScriptletException{
		StringBuilder builder = new StringBuilder();
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			InvoiceDetail invoiceDetail = (InvoiceDetail) invoiceDetailBean.get((Integer)super.getFieldValue(FIELD_ID));
			
			if(isPrintProductCode() && invoiceDetail.getItem().getProduct()!=null){
				builder.append(AonUtil.getMessage(ICommonMessages.ID)).append(": ");
				builder.append(invoiceDetail.getItem().getProduct().getCode());
			}
			
			String referenceCode = obtainReferenceCode(invoiceDetail);
			if(isPrintReferenceCode() && StringUtils.isNotBlank(referenceCode)){
				builder.append(builder.length()>0?" - ":"");
				builder.append(referenceCode);
			}
			
			if((isPrintProductCode() && invoiceDetail.getItem().getProduct()!=null)
				|| (isPrintReferenceCode() && StringUtils.isNotBlank(referenceCode))){
				builder.append("\n  ");
				builder.append(invoiceDetail.getDescription().replace("\n", "\n  "));
			} else {
				builder.append(invoiceDetail.getDescription());
			}
			
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg);
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
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg,e);
		}
		return reference.toString();
	}
	
}