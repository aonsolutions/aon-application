package com.esferalia.aon.ingenet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.TransactionalRunnable;

import com.esferalia.aon.ingenet.util.IngenetContext;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;


public class IngenetDeliveryManager {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	private static IngenetDeliveryManager instance;
	
	private Delivery aonDelivery = null;
	
	
	private IngenetDeliveryManager(){
	}
	
	public static IngenetDeliveryManager getInstance(){
		if(instance==null){
			instance = new IngenetDeliveryManager();
		}
		return instance;
	}
	
	public Map<Integer, String> obtainUnreadDeliveries(String domainName,
			String user) {
		int domainId = IngenetContext.getUdapaDomainId();

		AONContext ctx = IngenetContext.getAONContext(domainName, domainId,
				user);

		Map<Integer, String> map = new HashMap<Integer, String>();
		LinkedList<Delivery> list = WarehouseDAO.getDeliveryList(ctx, f -> f
				.getStatusProperty()
				.eq((byte) DeliveryStatus.PENDING.ordinal()));
		list.forEach(delivery -> {
			String description = "Albaran "
					+ (delivery.getSeries() != null ? delivery.getSeries() : "")
					+ "/"
					+ delivery.getNumber()
					+ " con fecha del "
					+ (delivery.getIssueTime() != null ? dateFormat
							.format(delivery.getIssueTime()) : "-");
			map.put(delivery.getId(), description);
		});

		return map;
	}
	
	public Delivery createAonDelivery(String domainName, String user,
			Integer currentDomainId, Integer ingenetDeliveryId,
			Integer workplaceId, Integer warehouseId) throws SourceSalesNotFoundException {
		
		AONContext ctx = AONContext.getAONContext(domainName, currentDomainId,
				user);

		List<DeliveryDetail> detailList = obtainIngenetDeliveryDetailList(
				domainName, user, ingenetDeliveryId);
		
		Integer ingenetSalesDetailId = detailList.stream()
				.filter(d -> d.getSalesDetail() != null).findFirst()
				.orElse(new DeliveryDetail()).getSalesDetail();
		SalesDetail ingenetSalesDetail = obtainIngenetSalesDetail(domainName,
				user, ingenetSalesDetailId);
		Sales ingenetSales = obtainIngenetSales(domainName, user,
				ingenetSalesDetail.getSales());
		Sales aonSales = SalesDAO.getSales(ctx, ingenetSales.getSeries(),
				ingenetSales.getNumber());
		
		if (aonSales == null | aonSales.getId() == null) {
			throw new SourceSalesNotFoundException(
					"Imposible realizar el traspaso. No existe el pedido "
							+ ingenetSales.getSeries() + "/"
							+ ingenetSales.getNumber());
		}
		
		try {
			ctx.transaction(new TransactionalRunnable() {
				@Override
				public void run(Configuration arg0) throws Exception {
					aonDelivery = createDelivery(ctx, ingenetDeliveryId,
							workplaceId, aonSales);
					
					createAonDeliveryDetails(ctx, ingenetDeliveryId,
							aonDelivery.getId(), warehouseId, detailList,
							aonSales);
					
					closeIngenetDelivery(domainName, user, ingenetDeliveryId);
				}
			});
		} catch (RuntimeException e) {
			try {
				throw e.getCause();
			} catch (Throwable throwable) {
				throw e;
			}
		}
		
		return aonDelivery;
	}
	
	private void createAonDeliveryDetails(AONContext ctx,
			Integer ingenetDeliveryId, Integer aonDeliveryId,
			Integer warehouseId, List<DeliveryDetail> detailList, Sales aonSales) {
		createDeliveryDetails(ctx, ingenetDeliveryId, aonDeliveryId,
				warehouseId, detailList, aonSales);
	}
	
	private Delivery createDelivery(AONContext ctx, Integer deliveryId,
			Integer workplaceId, Sales aonSales) {

		Integer[] scopes = SecurityDAO.getUserScopes(ctx, ctx.getUser());

		Delivery delivery = obtainIngenetDelivery(ctx.getDomainName(),
				ctx.getUser(), deliveryId);
		delivery.setDomain(ctx.getDomainId());
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery.setScope(scopes[0]);
		delivery.setIssueTime(new Date());
		delivery.setPayMethod(null);
		delivery.setWorkplace(workplaceId);
		delivery.setAddress(aonSales.getShippingAddress());
		delivery.setComments("Fecha de carga: "
				+ dateFormat.format(aonSales.getIssueDate()));
		delivery.setId(WarehouseDAO.insertDelivery(ctx, delivery));
		return delivery;
	}
	
	private void createDeliveryDetails(AONContext ctx,
			Integer ingenetDeliveryId, Integer aonDeliveryId,
			Integer warehouseId, List<DeliveryDetail> detailList, Sales aonSales) {

		for (int idx = 0; idx < detailList.size(); idx++) {
			DeliveryDetail detail = detailList.get(idx);

			Item ingenetItem = obtainIngenetItem(ctx.getDomainName(),
					ctx.getUser(), detail.getItem().getId());
			SalesDetail aonSalesDetail = null;
			Item item = null;
			if (isPackageItem(ingenetItem)) {
				item = obtainAonItem(ctx.getDomainName(), ctx.getUser(),
						ctx.getDomainId(), ingenetItem.getProduct().getCode());
			} else {
				aonSalesDetail = obtainAonSalesDetail(ctx.getDomainName(),
						ctx.getUser(), ctx.getDomainId(), detail, aonSales);
				item = createNewItem(ctx, ctx.getDomainId(),
						aonSalesDetail.getItem(),
						ingenetItem.getSerialNumber(),
						ingenetItem.getSerialDate());
			}

			if (item != null && item.getId() != null) {
				detail.setId(null);
				detail.setDomain(ctx.getDomainId());
				detail.setDelivery(new Delivery().setId(aonDeliveryId));
				detail.setWarehouse(warehouseId);
				detail.setSalesDetail(aonSalesDetail != null ? aonSalesDetail
						.getId() : null);
				detail.setItem(item);
				detail.setDescription(obtainDeliveryDetailDescription(item,
						detail.getQuantity()));
				detail.setDiscountExpression(aonSalesDetail != null ? aonSalesDetail
						.getDiscountExpression() : "0");
				detail.setLine(Integer.valueOf(idx + 1).shortValue());
				detail.setPrice(aonSalesDetail != null ? aonSalesDetail
						.getPrice() : 0.0);
				WarehouseDAO.insertDeliveryDetail(ctx, detail);

				// TODO: close manufacture_order served lines

				// update sales_detail, increase delivered
				if (aonSalesDetail != null && aonSalesDetail.getId() != null) {
					aonSalesDetail.setDelivered(aonSalesDetail.getDelivered()
							+ detail.getQuantity());
					SalesDAO.updateSalesDetail(ctx, aonSalesDetail);
				}
			}
		}

	}
	
	private boolean isPackageItem(Item item) {
		return item != null && item.getSerialNumber() == null
				&& item.getSerialDate() == null;
	}

	private String obtainDeliveryDetailDescription(Item item, double quantity) {
		if (item.getSerialNumber() != null && item.getSerialDate() != null) {
			Tag itemPackMeasurementTag = item.getPackMeasurementTag();
			Tag itemPackingTag = item.getPackUnitsTag();
			Tag itemPackFormatTag = item.getPackFormatTag();
			Double itemPackMeasurement = item.getPackMeasurement();
			Double itemPackUnits = item.getPackUnits();
			return String
					.format("%1$s \n\t- LOTE: %2$s \n\t- %3$.2f %4$s de %5$.2f %6$s \n\t- %7$.2f %8$s de %9$.2f %10$s",
							item.getProduct().getName(),
							item.getSerialNumber(),
							(quantity / itemPackMeasurement),
							itemPackingTag.getName(), itemPackMeasurement,
							itemPackMeasurementTag.getName(), (quantity
									/ itemPackMeasurement / itemPackUnits),
							itemPackFormatTag.getName(), itemPackUnits,
							itemPackingTag.getName());
		}
		return item.getProduct().getName();
	}

	private Item createNewItem(AONContext ctx, int domainId, Integer itemId,
			String serialNumber, Date serialDate) {
		if (serialNumber != null && serialDate != null) {
			Item newItem = ProductDAO.getItem(ctx, itemId);
			newItem.setId(null);
			newItem.setBarcode(null);
			newItem.setSerialNumber(serialNumber);
			newItem.setSerialDate(serialDate != null ? new java.sql.Date(
					serialDate.getTime()) : null);
			newItem.setActive(false);
			newItem.setStatus(Integer.valueOf(
					ProductStatus.DISCONTINUED.ordinal()).byteValue());
			ProductDAO.insert(ctx, newItem);
			return ProductDAO.getItem(
					ctx,
					o -> o.getDomainProperty()
							.eq(domainId)
							.and(o.getSerialNumberProperty().eq(
									newItem.getSerialNumber()))
							.and(o.getSerialDateProperty().eq(
									newItem.getSerialDate())));
		}
		return null;
	}

	private Delivery obtainIngenetDelivery(String domainName, String user,
			Integer deliveryId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return WarehouseDAO.getDelivery(ctx, deliveryId);
	}

	private List<DeliveryDetail> obtainIngenetDeliveryDetailList(
			String domainName, String user, Integer deliveryId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return WarehouseDAO.getDeliveryDetailList(ctx, deliveryId);
	}

	private SalesDetail obtainIngenetSalesDetail(String domainName,
			String user, Integer salesDetailId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return SalesDAO.getSalesDetail(ctx, salesDetailId);
	}

	private Sales obtainIngenetSales(String domainName, String user,
			Integer salesId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return SalesDAO.getSales(ctx, salesId);
	}

	private Item obtainIngenetItem(String domainName, String user,
			Integer itemId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return ProductDAO.getItem(ctx, o -> o.getIdProperty().eq(itemId));
	}

	private Item obtainAonItem(String domainName, String user,
			Integer currentDomainId, String productCode) {
		AONContext ctx = AONContext.getAONContext(domainName, currentDomainId,
				user);
		Product product = ProductDAO.getProduct(ctx, productCode);
		return ProductDAO.getItem(
				ctx,
				o -> o.getProductProperty().eq(product.getId())
						.and(o.getDomainProperty().eq(currentDomainId)));
	}

	private SalesDetail obtainAonSalesDetail(String domainName, String user,
			Integer currentDomainId, DeliveryDetail deliveryDetail,
			Sales aonSales) {
		SalesDetail salesDetail = obtainIngenetSalesDetail(domainName, user,
				deliveryDetail.getSalesDetail());
		AONContext ctx = AONContext.getAONContext(domainName, currentDomainId,
				user);
		if (aonSales != null && salesDetail != null) {
			return SalesDAO.getSalesDetail(ctx, aonSales.getId(),
					salesDetail.getLine());
		}
		return null;
	}

	public void closeIngenetDelivery(String domainName, String user,
			Integer deliveryId) {
		int domainId = IngenetContext.getUdapaDomainId();
		AONContext ctx = IngenetContext.getAONContext(domainName, domainId,
				user);
		Delivery delivery = WarehouseDAO.getDelivery(ctx, deliveryId);
		delivery.setStatus(DeliveryStatus.INVOICED);
		delivery.setModificationUser(user);
		delivery.setModificationDate(new Date());
		WarehouseDAO.updateDelivery(ctx, delivery);
	}
	
	
}
