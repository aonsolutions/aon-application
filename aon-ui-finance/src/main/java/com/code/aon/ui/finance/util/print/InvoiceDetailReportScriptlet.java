package com.code.aon.ui.finance.util.print;

import static com.code.aon.common.enumeration.AppParam.APP_PRINT_REFERENCE_CODE_PARAM;
import net.sf.jasperreports.engine.JRScriptletException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.sales.Sales;
import com.code.aon.ui.company.util.ReportProductLinesScriptlet;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;

public class InvoiceDetailReportScriptlet extends ReportProductLinesScriptlet {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceDetailReportScriptlet.class.getName());
	
	private static final String FIELD_ID = "id";
	
	@Override
	public void afterDetailEval() throws JRScriptletException {
		try {
			String description = getFieldDescription();
			StringBuilder newDescription = new StringBuilder (buildLineDescription());
			fillLineReferenceCode(newDescription);
			if(newDescription.length()>0){
				newDescription.append("\n  ");
				newDescription.append(description.replace("\n", "\n  "));
			} else {
				newDescription.delete(0, newDescription.length());
				newDescription.append(description);
			}
			setVariableDescription(newDescription.toString());
		} catch (Exception e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg,e);
			throw new JRScriptletException(msg, e);
		}
	}
	
	private void fillLineReferenceCode(StringBuilder newDescription) throws JRScriptletException{
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			InvoiceDetail invoiceDetail = (InvoiceDetail) invoiceDetailBean.get((Integer)super.getFieldValue(FIELD_ID));
			
			boolean isPrintReferenceCode = AppParamUtil.getValueAsBoolean(APP_PRINT_REFERENCE_CODE_PARAM);
			
			String referenceCode = obtainReferenceCode(invoiceDetail);
			if(isPrintReferenceCode && StringUtils.isNotBlank(referenceCode)){
				newDescription.append(newDescription.length()>0?" - ":"");
				newDescription.append(referenceCode);
			}
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg);
			throw new JRScriptletException(msg, e);
		}
		
	}
	

	private String obtainReferenceCode(InvoiceDetail invoiceDetail) throws JRScriptletException{
		StringBuilder builder = new StringBuilder();
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
				builder.append("N. pedido: ");
				builder.append(sales.getReferenceCode());
				if(StringUtils.isNotBlank(sales.getPurchaseReference())){
					builder.append(" / Su referencia: ");
					builder.append(sales.getPurchaseReference());
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg,e);
			throw new JRScriptletException(msg, e);
		}
		return builder.toString();
	}
	
	
}