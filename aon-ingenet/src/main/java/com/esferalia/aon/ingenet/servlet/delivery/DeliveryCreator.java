package com.esferalia.aon.ingenet.servlet.delivery;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.esferalia.aon.ingenet.api.albaranes.ALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.DATOSLINEAALBARANTYPE;
import com.esferalia.aon.ingenet.api.albaranes.ELABORACIONORIGENTYPE;
import com.esferalia.aon.ingenet.util.ProductUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.ElaborationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;

@Deprecated
public class DeliveryCreator extends AbstractDeliveryCreator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryCreator.class.getName());

	
	public DeliveryCreator(String domain, Integer domainId, String user) {
		super(domain, domainId, user);
	}
		



	@Override
	protected void manageAlbaranErrors(AONContext ctx, ALBARANTYPE albaran) {
		albaran.getLINEASALBARAN().getDATOSLINEAALBARAN().forEach(linea -> {
			String cause = "";
			if(albaran.getERRORES()!=null
					&& albaran.getERRORES().getERRORES()!=null
					&& !albaran.getERRORES().getERRORES().isEmpty()){
				for(String error: albaran.getERRORES().getERRORES()){
					cause += error!=null?error+". ":"";
				}
			}
			failElaborations(ctx, albaran, linea, cause);
		});
	}


	@Override
	protected void afterDeliverySaved(AONContext ctx, ALBARANTYPE albaran, boolean test) {
		try {
			albaran.getLINEASALBARAN().getDATOSLINEAALBARAN().forEach(linea -> {
				manageElaborations(ctx, albaran, linea, test);
			});
		} catch (Throwable th) {
			addError(albaran, th.getLocalizedMessage());
		}
	}
	
	@Override
	protected void afterDeliverySavedFail(AONContext ctx, ALBARANTYPE albaran, Throwable th) {
		albaran.getLINEASALBARAN().getDATOSLINEAALBARAN().forEach(linea -> {
			failElaborations(ctx, albaran, linea, th.getLocalizedMessage());
		});
	}


	@Override
	protected void fillDeliveryDetailSourceSales(AONContext ctx, DeliveryDetail detail, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea) {
		Elaboration elaboration = obtainElaboration(
				ctx, linea.getDATOSELABORACIONORIGEN());
		if(elaboration==null || elaboration.getId()==null){
			String number = linea.getDATOSELABORACIONORIGEN().getSERIE()
					+ "/" + linea.getDATOSELABORACIONORIGEN().getNUMERO();
			addError(albaran, "No hay ninguna elaboración con número '" + number
					+ "' asociada a la linea " + linea.getLINEA());
		} else {
			SalesDetail salesDetail = SalesDAO.getSalesDetail(ctx, elaboration.getSourceId());
			if(salesDetail!=null && salesDetail.getId()!=null){
				detail.setSalesDetail(salesDetail.getId());
				detail.setPrice(salesDetail.getPrice());
				detail.setDiscountExpression(salesDetail.getDiscountExpression());
			} else {
				String number = linea.getDATOSELABORACIONORIGEN().getSERIE()
						+ "/" + linea.getDATOSELABORACIONORIGEN().getNUMERO();
				addError(albaran, "No hay ningún pedido asociado a la elaboración '" +
						number + "' de a la linea " + linea.getLINEA());
			}
		}
	}
	
	@Override
	public void fillSuccessMessage(StringBuffer bf, DATOSLINEAALBARANTYPE lin) {
		bf.append("<li>Elaboración finalizada: <b>").append(lin.getDATOSELABORACIONORIGEN().getSERIE())
			.append("/").append(lin.getDATOSELABORACIONORIGEN().getNUMERO()).append("</b>")
			.append("</li>");
	}
	
	
	/**
	 * 
	 * ELABORATIONS
	 * 
	 */
	private void manageElaborations(AONContext ctx, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea,
			boolean test) {

		Elaboration elaboration = obtainElaboration(ctx,
				linea.getDATOSELABORACIONORIGEN());
		ElaborationDetail elaborationDetail = new ElaborationDetail();
		List<ElaborationDetailComposition> compositionList = new LinkedList<>();

		if (elaboration != null && elaboration.getId() != null) {

			try {
				elaborationDetail.setDomain(ctx.getDomainId());
				elaborationDetail.setElaboration(elaboration);
				elaborationDetail.setDate(new Date());
				elaborationDetail.setItem(ProductUtils.obtainItem(ctx, linea.getPRODUCTO()));
				elaborationDetail.setQuantity(Double.valueOf(linea.getCANTIDAD()));
				elaborationDetail.setWarehouse(null);
				elaborationDetail.setAddInfo("");
			} catch (AonException e) {
				String msg = "[Producto " + linea.getPRODUCTO().getCODIGO() + "] ";
				addError(albaran, msg + e.getMessage());
			}

			linea.getCOMPOSICIONPRODUCTOELABORADO()
					.getDATOSCOMPOSICIONPRODUCTO()
					.forEach(
							lineaComposicion -> {
								try {
									Item compositionItem = ProductUtils.obtainItem(ctx, lineaComposicion.getPRODUCTO());
									ElaborationDetailComposition elaborationDetailComposition = new ElaborationDetailComposition();
									elaborationDetailComposition.setDomain(ctx
											.getDomainId());
									elaborationDetailComposition
									.setElaborationDetail(elaborationDetail);
									elaborationDetailComposition
									.setItem(compositionItem);
									elaborationDetailComposition.setQuantity(Double
											.valueOf(lineaComposicion.getCANTIDAD()));
									elaborationDetailComposition.setWarehouse(null);
									elaborationDetailComposition.setAddInfo(null);
									elaborationDetailComposition.setCreationUser(ctx.getUser());
									elaborationDetailComposition.setCreationDate(new Date());
									compositionList.add(elaborationDetailComposition);
								} catch (Exception e) {
									String msg = "[Compuesto " + lineaComposicion.getPRODUCTO().getCODIGO() + "] ";
									addError(albaran, msg + e.getMessage());
								}
							});
			
			if (!test) {
				int elaborationDetailId = ElaborationDAO
						.insertElaborationDetail(ctx, elaborationDetail);
				elaborationDetail.setId(elaborationDetailId);

				compositionList.forEach(c -> {
					c.setElaborationDetail(elaborationDetail);
					ElaborationDAO.insertElaborationDetailComposition(ctx, c);
				});

				elaboration.setStatus(ElaborationStatus.CLOSED);
				elaboration.setModificationUser(ctx.getUser());
				elaboration.setModificationDate(new Date());
				ElaborationDAO.updateElaboration(ctx, elaboration);
			}
		}
	}
	
	private void failElaborations(AONContext ctx, ALBARANTYPE albaran,
			DATOSLINEAALBARANTYPE linea, String cause) {
		Elaboration elaboration = obtainElaboration(ctx,
				linea.getDATOSELABORACIONORIGEN());
		if (elaboration != null && elaboration.getId() != null) {
			elaboration.setStatus(ElaborationStatus.FAIL);
			elaboration.setRemarks(StringUtils.mid(cause, 0, 128));
			elaboration.setModificationUser(ctx.getUser());
			elaboration.setModificationDate(new Date());
			ElaborationDAO.updateElaboration(ctx, elaboration);
		}
	}
	
	private Elaboration obtainElaboration(AONContext ctx,
			ELABORACIONORIGENTYPE datoselaboracionorigen) {
		String series = datoselaboracionorigen.getSERIE();
		Integer number = Integer.valueOf(datoselaboracionorigen.getNUMERO());
		List<Elaboration> list = ElaborationDAO.getElaborationList(
				ctx,
				f -> f.getDomainProperty()
						.eq(ctx.getDomainId())
						.and(f.getSeriesProperty().eq(series)
								.and(f.getNumberProperty().eq(number))));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	
	
	/**
	 * 
	 * SALES
	 * 
	 */
	
	@Override
	protected void manageSales(AONContext ctx, ALBARANTYPE albaran, DATOSLINEAALBARANTYPE linea, boolean test) {
		Elaboration elaboration = obtainElaboration(ctx,
				linea.getDATOSELABORACIONORIGEN());
		if (elaboration != null && elaboration.getId() != null
				&& ElaborationSource.SALES.equals(elaboration.getSource()) // SALES
				&& elaboration.getSourceId() != null) {
			Integer salesDetailId = elaboration.getSourceId();
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
						}
					}
				}
			} catch (Exception e) {
				String msg = "[Pedido origen] ";
				getWarningList().add(msg + e.getMessage());
			}
		}
		
	}
	
	
		
}

