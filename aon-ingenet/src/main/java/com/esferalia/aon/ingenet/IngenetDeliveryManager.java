package com.esferalia.aon.ingenet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.ingenet.util.IngenetContext;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
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
	
	
	private IngenetDeliveryManager(){
		
	}
	
	public static IngenetDeliveryManager getInstance(){
		if(instance==null){
			instance = new IngenetDeliveryManager();
		}
		return instance;
	}
	
	public Map<Integer, String> obtainUnreadDeliveries(String domainName, String user) {
		int domainId = IngenetContext.getUdapaDomainId();
		
		AONContext ctx = IngenetContext.getAONContext(domainName,
				domainId, user);
		
		
		Map<Integer, String> map = new HashMap<Integer, String>();
		LinkedList<Delivery> list = WarehouseDAO.getDeliveryList(ctx, f -> f.getStatusProperty().eq((byte)DeliveryStatus.PENDING.ordinal()));
		list.forEach(delivery -> {
			String description = "Albaran " 
					+ (delivery.getSeries()!=null?delivery.getSeries():"") + "/" + delivery.getNumber()
					+ " con fecha del " 
					+ (delivery.getIssueTime()!=null?dateFormat.format(delivery.getIssueTime()):"-");
			map.put(delivery.getId(), description);
		});
		
		return map;
	}
	
	public Delivery createAonDelivery(String domainName, String user, Integer deliveryId, Integer workplaceId, Integer currentDomainId) {
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
		return createDelivery(ctx, domainName, user, currentDomainId, deliveryId, workplaceId);
	}
	
	public void createAonDeliveryDetails(String domainName, String user, Integer currentDomainId, Integer ingenetDeliveryId, Integer aonDeliveryId, Integer warehouseId) {
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
		createDeliveryDetails(ctx, domainName, user, currentDomainId, ingenetDeliveryId, aonDeliveryId, warehouseId);
	}
	
	private Delivery createDelivery(AONContext ctx, String domainName,
			String user, Integer currentDomainId, Integer deliveryId, Integer workplaceId) {
		
		Integer[] scopes = SecurityDAO.getUserScopes(ctx, user);
		
		Delivery delivery = obtainIngenetDelivery(domainName, user, deliveryId);
		delivery.setDomain(ctx.getDomainId());
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery.setScope(scopes[0]);
		delivery.setIssueTime(new Date());
		delivery.setPayMethod(null);
		delivery.setWorkplace(workplaceId);
		delivery.setId(WarehouseDAO.insertDelivery(ctx, delivery));
		return delivery;
	}
	
	private void createDeliveryDetails(AONContext ctx, String domainName,
			String user, Integer currentDomainId, Integer ingenetDeliveryId,
			Integer aonDeliveryId, Integer warehouseId) {
		
		List<DeliveryDetail> detailList = obtainIngenetDeliveryDetailList(domainName, user, ingenetDeliveryId);
		for(int idx=0;idx<detailList.size();idx++){
			DeliveryDetail detail = detailList.get(idx);
			
			Item ingenetItem = obtainIngenetItem(domainName, user, detail.getItem().getId());
			SalesDetail aonSalesDetail = null;
			Item item = null;
			if(ingenetItem.getSerialNumber()==null && ingenetItem.getSerialDate()==null){
				item = obtainAonItem(domainName, user, currentDomainId, ingenetItem.getProduct().getCode());
			} else {
				aonSalesDetail = obtainAonSalesDetail(domainName, user, currentDomainId, detail);
				item = createNewItem(ctx, currentDomainId,
						aonSalesDetail.getItem(), ingenetItem.getSerialNumber(),
						ingenetItem.getSerialDate());
			}
			
			if(item!=null && item.getId()!=null){
				detail.setId(null);
				detail.setDomain(ctx.getDomainId());
				detail.setDelivery(new Delivery().setId(aonDeliveryId));
				detail.setWarehouse(warehouseId);
				detail.setSalesDetail(aonSalesDetail!=null?aonSalesDetail.getId():null);
				detail.setItem(item);
				detail.setDescription(item.getProduct().getName());
				detail.setDiscountExpression(aonSalesDetail!=null?aonSalesDetail.getDiscountExpression():"0");
				detail.setLine(Integer.valueOf(idx+1).shortValue());
				detail.setPrice(aonSalesDetail!=null?aonSalesDetail.getPrice():0.0);
				WarehouseDAO.insertDeliveryDetail(ctx, detail);
				
				// TODO: close manufacture_order served lines
				
				// update sales_detail, increase delivered
				if(aonSalesDetail!=null && aonSalesDetail.getId()!=null){
					aonSalesDetail.setDelivered(aonSalesDetail.getDelivered()+detail.getQuantity());
					SalesDAO.updateSalesDetail(ctx, aonSalesDetail);
				}
			}
		}
		
	}
	
	private Item createNewItem(AONContext ctx, int domainId, Integer itemId, String serialNumber, Date serialDate) {
		if(serialNumber!=null && serialDate!=null){
			Item newItem = ProductDAO.getItem(ctx, itemId);
			newItem.setId(null);
			newItem.setBarcode(null);
			newItem.setSerialNumber(serialNumber);
			newItem.setSerialDate(serialDate!=null?new java.sql.Date(serialDate.getTime()):null);
			newItem.setActive(false);
			newItem.setStatus(Integer.valueOf(ProductStatus.DISCONTINUED.ordinal()).byteValue());
			ProductDAO.insertItem(ctx, newItem);
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

	private Delivery obtainIngenetDelivery(String domainName, String user, Integer deliveryId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return WarehouseDAO.getDelivery(ctx, deliveryId);
	}
	
	private List<DeliveryDetail> obtainIngenetDeliveryDetailList(String domainName, String user, Integer deliveryId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return WarehouseDAO.getDeliveryDetailList(ctx, deliveryId);
	}
	
	private SalesDetail obtainIngenetSalesDetail(String domainName, String user, Integer salesDetailId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return SalesDAO.getSalesDetail(ctx, salesDetailId);
	}
	
	private Sales obtainIngenetSales(String domainName, String user, Integer salesId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return SalesDAO.getSales(ctx, salesId);
	}
	
	private Item obtainIngenetItem(String domainName, String user, Integer itemId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return ProductDAO.getItem( ctx, o -> o.getIdProperty().eq(itemId));
	}
	
	private Item obtainAonItem(String domainName, String user, Integer currentDomainId, String productCode) {
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
		Product product = ProductDAO.getProduct( ctx, productCode);
		return ProductDAO.getItem( ctx, o -> o.getProductProperty().eq(product.getId()));
	}
	
	private SalesDetail obtainAonSalesDetail(String domainName, String user, Integer currentDomainId, DeliveryDetail deliveryDetail) {
		
		SalesDetail salesDetail = obtainIngenetSalesDetail(domainName, user, deliveryDetail.getSalesDetail());
		Sales sales = obtainIngenetSales(domainName, user, salesDetail.getSales());
		
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
		Sales aonSales = SalesDAO.getSales(ctx, sales.getSeries(), sales.getNumber());
		if(aonSales!=null && salesDetail!=null){
			return SalesDAO.getSalesDetail(ctx, aonSales.getId(), salesDetail.getLine());
		}
		return null;
	}
	
	public void closeIngenetDelivery(String domainName, String user, Integer deliveryId) {
		int domainId = IngenetContext.getUdapaDomainId();
		AONContext ctx = IngenetContext.getAONContext(domainName,
				domainId, user);
		Delivery delivery = WarehouseDAO.getDelivery(ctx, deliveryId);
		delivery.setStatus(DeliveryStatus.INVOICED);
		delivery.setModificationUser(user);
		delivery.setModificationDate(new Date());
		WarehouseDAO.updateDelivery(ctx, delivery);
	}
	
	
}
