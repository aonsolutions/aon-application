package com.esferalia.aon.ingenet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.ingenet.util.IngenetContext;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
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
			String description = "Albaran "+delivery.getSeries()+"/"+delivery.getNumber()+" con fecha del "+(delivery.getIssueTime()!=null?dateFormat.format(delivery.getIssueTime()):"-");
			map.put(delivery.getId(), description);
		});
		
		return map;
	}
	
	public void createAonDelivery(String domainName, String user, Integer deliveryId, Integer currentDomainId, Integer warehouseId) {
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
		createDelivery(ctx, domainName, user, currentDomainId, deliveryId, warehouseId);
	}
	
	public void createAonDeliveries(String domainName, String user, List<Integer> deliveryIds, Integer currentDomainId, Integer warehouseId) {
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
		deliveryIds.forEach(id -> {
			createDelivery(ctx, domainName, user, currentDomainId, id, warehouseId);
		});
	}
	
	private void createDelivery(AONContext ctx, String domainName, String user, Integer currentDomainId, Integer deliveryId, Integer warehouseId){
		
		Integer[] scopes = SecurityDAO.getUserScopes(ctx, user);
		
		Delivery delivery = obtainIngenetDelivery(domainName, user, deliveryId);
		delivery.setDomain(ctx.getDomainId());
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery.setScope(scopes[0]);
		delivery.setIssueTime(new Date());
		delivery.setPayMethod(null);
		delivery.setWorkplace(null);
		Integer newId = WarehouseDAO.insertDelivery(ctx, delivery);
		delivery.setId(newId);
		
		List<DeliveryDetail> detailList = obtainIngenetDeliveryDetailList(domainName, user, deliveryId);
		detailList.forEach(detail -> {
			Integer salesDetailId = obtainAonSalesDetail(domainName, user, currentDomainId, detail);
			Integer itemId = obtainAonSalesDetailItemId(domainName, user, currentDomainId, salesDetailId);
			if(itemId!=null){
				detail.setId(null);
				detail.setDomain(ctx.getDomainId());
				detail.setDelivery(delivery);
				detail.setWarehouse(warehouseId);
				detail.setSalesDetail(salesDetailId);
				detail.setItem(new Item().setId(itemId));
			}
		});
		
		WarehouseDAO.insertDeliveryDetails(ctx, detailList);
		
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
	
	private Integer obtainIngenetSales(String domainName, String user, Integer salesDetailId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return SalesDAO.getSalesId(ctx, salesDetailId);
	}
	
	private String obtainIngenetSalesSeries(String domainName, String user, Integer salesId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return SalesDAO.getSalesSeries(ctx, salesId);
	}
	private Integer obtainIngenetSalesNumber(String domainName, String user, Integer salesId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return SalesDAO.getSalesNumber(ctx, salesId);
	}
	private Short obtainIngenetSalesDetailLine(String domainName, String user, Integer salesDetailId) {
		AONContext ctx = IngenetContext.getAONContext(domainName,
				IngenetContext.getUdapaDomainId(), user);
		return SalesDAO.getSalesDetailLine(ctx, salesDetailId);
	}
	
	private Integer obtainAonSalesDetailItemId(String domainName, String user, Integer currentDomainId, Integer salesDetailId) {
		if(salesDetailId!=null){
			AONContext ctx = AONContext.getAONContext(domainName,
					currentDomainId, user);
			return SalesDAO.getSalesDetailItemId(ctx, salesDetailId);
		}
		return null;
	}
	
	private Integer obtainAonSalesDetail(String domainName, String user, Integer currentDomainId, DeliveryDetail deliveryDetail) {
		
		Integer salesId = obtainIngenetSales(domainName, user, deliveryDetail.getSalesDetail());
		String salesSeries = obtainIngenetSalesSeries(domainName, user, salesId);
		Integer salesNumber = obtainIngenetSalesNumber(domainName, user, salesId);
		Short salesDetailLine = obtainIngenetSalesDetailLine(domainName, user, deliveryDetail.getSalesDetail());
		
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
		Integer aonSalesId = SalesDAO.getSalesId(ctx, salesSeries, salesNumber);
		if(aonSalesId!=null && salesDetailLine!=null){
			return SalesDAO.getSalesDetailId(ctx, aonSalesId, salesDetailLine);
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
