package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;

import java.util.LinkedList;

import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.InventoryDetailRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;

public class InventoryDAO {
	
	public static LinkedList<InventoryDetail> getInventoryDetailList(AONContext ctx, Integer inventoryId){
		
		Result<InventoryDetailRecord> data = ctx.getDslContext()
				.select()
				.from(INVENTORY_DETAIL)
				.where(INVENTORY_DETAIL.INVENTORY.eq(inventoryId))
				.fetchInto(INVENTORY_DETAIL);
		
		LinkedList<InventoryDetail> list = new LinkedList<InventoryDetail>();
		
		data.stream().forEach(r -> {
			InventoryDetail inventoryDetail = new InventoryDetail();
			inventoryDetail.setId(r.getId());
			Inventory inventory = new Inventory();
			inventory.setId(r.getInventory());inventory.setDomain(r.getDomain());
			inventoryDetail.setInventory(inventory);
			inventoryDetail.setCost(r.getCost());
			inventoryDetail.setActualQuantity(r.getActualQuantity());
			inventoryDetail.setCreationDate(r.getCreationDate());
			inventoryDetail.setCreationUser(r.getCreationUser());
			inventoryDetail.setDomain(r.getDomain());
			Item item = new Item();
			item.setId(r.getItem());item.setDomain(r.getDomain());
			inventoryDetail.setItem(item);
			inventoryDetail.setModificationDate(r.getModificationDate());
			inventoryDetail.setModificationUser(r.getModificationUser());
			inventoryDetail.setRealQuantity(r.getRealQuantity());
			list.add(inventoryDetail);
		});
		return list;
	}
}
