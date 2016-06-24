package com.esferalia.aon.ingenet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.ingenet.util.IngenetContext;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.WarehouseDAO;


public class IngenetDeliveryManager {

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
			map.put(delivery.getId(), delivery.getSeries()+"/"+delivery.getNumber());
		});
		
		return map;
	}
	
	public void createAonDeliveries(String domainName, String user, List<Integer> deliveryIds, Integer currentDomainId) {
		AONContext ctx = AONContext.getAONContext(domainName,
				currentDomainId, user);
		deliveryIds.forEach(id -> {
			createDelivery(ctx, domainName, user, id);
		});
	}
	
	private void createDelivery(AONContext ctx, String domainName, String user, Integer deliveryId){
		
		Delivery delivery = obtainIngenetDelivery(domainName, user, deliveryId);
		delivery.setDomain(ctx.getDomainId());
		WarehouseDAO.insertDelivery(ctx, delivery);
		
		List<DeliveryDetail> detailList = obtainIngenetDeliveryDetailList(domainName, user, deliveryId);
		ctx.getDslContext().transaction(
				configuration -> {
					detailList.forEach(detail -> {
						detail.setDomain(ctx.getDomainId());
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
	
		
	
	
}
