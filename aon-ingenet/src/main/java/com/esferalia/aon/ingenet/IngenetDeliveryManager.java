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
	
	public void createAonDeliveries(String domainName, String user, List<Integer> deliveryIds, Integer currentDomainId) {
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
		deliveryIds.forEach(id -> {
			createDelivery(ctx, domainName, user, currentDomainId, id);
		});
	}
	
	private void createDelivery(AONContext ctx, String domainName, String user, Integer currentDomainId, Integer deliveryId){
		
		Integer[] scopes = SecurityDAO.getUserScopes(ctx, user);
		
		Delivery delivery = obtainIngenetDelivery(domainName, user, deliveryId);
		delivery.setDomain(ctx.getDomainId());
		delivery.setStatus(DeliveryStatus.PENDING);
		delivery.setScope(scopes[0]);
		delivery.setIssueTime(new Date());
		Integer newId = WarehouseDAO.insertDelivery(ctx, delivery);
		delivery.setId(newId);
		
		List<DeliveryDetail> detailList = obtainIngenetDeliveryDetailList(domainName, user, deliveryId);
		ctx.getDslContext().transaction(
				configuration -> {
					detailList.forEach(detail -> {
						detail.setDomain(ctx.getDomainId());
						detail.setDelivery(delivery);
						SalesDetail salesDetail = obtainAonSalesDetail(domainName, user, currentDomainId, detail);
						// TODO
//						Sales sales = obtainAonSales(domainName, user, currentDomainId, detail);
//						detail.setWarehouse(sales.);
						detail.setSalesDetail(salesDetail.getId());
						WarehouseDAO.insertDeliveryDetail(ctx, detail);
					});
				}
		);
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
	
	// TODO
	private SalesDetail obtainAonSalesDetail(String domainName, String user, Integer currentDomainId, DeliveryDetail deliveryDetail) {
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
//		return SalesDAO.getSalesDetail(ctx, deliveryDetail.getLine(), );
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
