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
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
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
			InvoiceDetail detail = obtainInvoiceDetail();
			fillLineReferenceCode(newDescription, detail);
			if(newDescription.length()>0){
				newDescription.append("\n  ");
				newDescription.append(description.replace("\n", "\n  "));
			} else {
				newDescription.delete(0, newDescription.length());
				newDescription.append(description);
			}
			fillProductPackage(newDescription, detail);
			setVariableDescription(newDescription.toString());
		} catch (Exception e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg,e);
			throw new JRScriptletException(msg, e);
		}
	}


	private void fillLineReferenceCode(StringBuilder newDescription, InvoiceDetail invoiceDetail) throws JRScriptletException{
		boolean isPrintReferenceCode = AppParamUtil.getValueAsBoolean(APP_PRINT_REFERENCE_CODE_PARAM);
		String referenceCode = obtainReferenceCode(invoiceDetail);
		if(isPrintReferenceCode && StringUtils.isNotBlank(referenceCode)){
			newDescription.append(newDescription.length()>0?" - ":"");
			newDescription.append(referenceCode);
		}
	}

	private void fillProductPackage(StringBuilder newDescription, InvoiceDetail invoiceDetail) throws JRScriptletException, ManagerBeanException{
		boolean isPrintProductPackage = AppParamUtil.getValueAsBoolean(AppParam.APP_PRINT_PRODUCT_PACKAGE_PARAM);
		Item item = invoiceDetail.getItem();
		if( isPrintProductPackage && item!=null && item.getProduct().isPackaged() ){
			if( item.getPackMeasurementTag()==null
					|| item.getPackUnitsTag()==null
					|| item.getPackFormatTag()==null ){
				item = item.getProduct().getBaseItem();
			}
			
			StringBuilder builder = new StringBuilder("  ");
			if( item.getPackMeasurementTag()!=null ){
				builder.append( String.format("%.2f", invoiceDetail.getQuantity()) )
					.append( " " )
					.append( item.getPackMeasurementTag().getName() )
					.append( ": " );
			}
			if( item.getPackUnitsTag()!=null ){
				builder.append( String.format("%.2f",invoiceDetail.getQuantity()
						/ item.getPackMeasurement()) )
					.append( " " )
					.append( item.getPackUnitsTag().getName() );
			}
			if( item.getPackUnitsTag()!=null
					&& item.getPackFormatTag()!=null ){
				builder.append( ", " );
			}
			if( item.getPackFormatTag()!=null ){
				builder.append( String.format("%.2f",(invoiceDetail.getQuantity()
						/ item.getPackMeasurement())
						/ item.getPackUnits()) )
					.append( " " )
					.append( item.getPackFormatTag().getName() );
			}
			newDescription.append( newDescription.length()>0?"\n":"" );
			newDescription.append( builder.toString() );
		}		
	}

	public Sales obtainSales(Integer detailId) throws JRScriptletException{
		InvoiceDetail invoiceDetail = obtainInvoiceDetail(detailId);
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
			
			return sales;
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg,e);
			throw new JRScriptletException(msg, e);
		}
	}
	
	private String obtainReferenceCode(InvoiceDetail invoiceDetail) throws JRScriptletException{
		StringBuilder builder = new StringBuilder();
		Sales sales = obtainSales(invoiceDetail.getId());
		if(sales!=null){
			builder.append("N. pedido: ");
			builder.append(sales.getReferenceCode());
			if(StringUtils.isNotBlank(sales.getPurchaseReference())){
				builder.append(" / Su referencia: ");
				builder.append(sales.getPurchaseReference());
			}
		}
		return builder.toString();
	}

	private InvoiceDetail obtainInvoiceDetail(Integer id) throws JRScriptletException {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			InvoiceDetail invoiceDetail = (InvoiceDetail) invoiceDetailBean.get(id);
			return invoiceDetail;
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error, vuelva a intentarlo pasados unos segundos";
			LOGGER.error(msg);
			throw new JRScriptletException(msg, e);
		}
	}
	
	private InvoiceDetail obtainInvoiceDetail() throws JRScriptletException {
		return obtainInvoiceDetail((Integer)super.getFieldValue(FIELD_ID));
	}
	
}