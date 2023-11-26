package com.esferalia.aon.ingenet.servlet.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esferalia.aon.ingenet.api.albaranes.ALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.PEDIDOORIGENTYPE;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;

@Deprecated
public class DeliveryCreatorSales extends AbstractDeliveryCreator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryCreatorSales.class.getName());

	
	public DeliveryCreatorSales(String domain, Integer domainId, String user) {
		super(domain, domainId, user);
	}
			
	
	@Override
	protected void manageAlbaranErrors(AONContext ctx, ALBARANTYPE albaran) {
		// nothing to to
	}
	
	@Override
	protected void afterDeliverySaved(AONContext ctx, ALBARANTYPE albaran, boolean test) {
		// nothing to to
		
//		try {
//			albaran.getLINEASALBARAN().getDATOSLINEAALBARAN().forEach(linea -> {
//				manageElaborations(ctx, albaran, linea, test);
//			});
//		} catch (Throwable th) {
//			addError(albaran, th.getLocalizedMessage());
//		}
		
	}
	
	@Override
	protected void afterDeliverySavedFail(AONContext ctx, ALBARANTYPE albaran, Throwable th) {
		// nothing to to
	}

	@Override
	protected void fillDeliveryDetailSourceSales(AONContext ctx, DeliveryDetail detail, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea) {
		SalesDetail salesDetail = obtainSalesDetail(
				ctx, linea.getDATOSPEDIDOORIGEN());
		
		if(salesDetail==null || salesDetail.getId()==null){
			String referenceNumber = linea.getDATOSPEDIDOORIGEN().getSERIE()
					+ "/" + linea.getDATOSPEDIDOORIGEN().getNUMERO();
			addError(albaran, "No hay ningun pedido con número '" + referenceNumber
					+ "' asociada a la linea " + linea.getLINEA());
		} else {
			detail.setSalesDetail(salesDetail.getId());
			detail.setPrice(salesDetail.getPrice());
			detail.setDiscountExpression(salesDetail.getDiscountExpression().getDiscountExpr());
		}
	}
	
	@Override
	public void fillSuccessMessage(StringBuffer bf, DATOSLINEAALBARANTYPE lin) {
		bf.append("<li>Pedido finalizado: <b>").append(lin.getDATOSPEDIDOORIGEN().getSERIE())
			.append("/").append(lin.getDATOSPEDIDOORIGEN().getNUMERO()).append("</b>")
			.append("</li>");
	}
	

	@Override
	protected void manageSales(AONContext ctx, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea, boolean test) {
		SalesDetail salesDetail = obtainSalesDetail(ctx, linea.getDATOSPEDIDOORIGEN());
		Integer salesDetailId = salesDetail.getId();
		try {
			SalesDetail sd = AON.getSalesDetailStream(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
					f -> f.getIdProperty().eq(salesDetailId)).findFirst().orElse(null);
			if (!test) {
				if(sd!=null && sd.getId()!=null){
					Sales sales = sd.getSales();
					Double totalSalesPending = AON.getSalesDetailStream(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(),
							f -> f.getSalesProperty().eq(sales.getId()))
							.mapToDouble(o->o.getQuantity()-o.getDelivered()).sum();
					if(totalSalesPending==0.0){
						sales.setStatus(SalesStatus.SERVED);
						SalesDAO.updateSales(ctx, sales);
						closeCommunication(ctx, sales.getId());
					}
				}
			}
		} catch (Exception e) {
			String msg = "[Pedido origen] ";
			getWarningList().add(msg + e.getMessage());
		}
	}
	
	private void closeCommunication(AONContext ctx, Integer salesId) {
		if(salesId!=null) {
			DataResponse dr = AON.getDataResponse(ctx.getDomainName(),
					ctx.getDomainId(), ctx.getUser(), f -> 
						f.getDomainProperty().eq(ctx.getDomainId())
						.and(f.getSourceProperty().eq(com.esferalia.aon.occam.api.model.type.DataResponseSource.INGENET_SALES.value()))
						.and(f.getSourceIdProperty().eq(salesId)));
			
			DataResponseDetail detail = new DataResponseDetail();
			detail.setDomain(dr.getDomain());
			detail.setDataResponse(dr.getId());
			detail.setDataVariable("STATUS");
			detail.setDataValue("PROCESSED");
			detail = AON.insertDataResponseDetail(ctx.getDomainName(),
					ctx.getDomainId(), ctx.getUser(), detail);
		}
	}


	/**
	 * 
	 * SALES
	 * 
	 */

	private SalesDetail obtainSalesDetail(AONContext ctx,
			PEDIDOORIGENTYPE datospedidoorigen) {
		String series = datospedidoorigen.getSERIE();
		Integer number = Integer.valueOf(datospedidoorigen.getNUMERO());
		Integer line = Integer.valueOf(datospedidoorigen.getLINEA());
		Sales sales = SalesDAO.getSales(ctx, f -> f.getDomainProperty()
				.eq(ctx.getDomainId())
				.and(f.getSeriesProperty().eq(series)
						.and(f.getNumberProperty().eq(number))));
		if(sales!=null) {
			return SalesDAO.getSalesDetail(ctx, sales.getId(), line.shortValue());
		}
		return null;
	}
	
		
}

