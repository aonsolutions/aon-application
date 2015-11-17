package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esferalia.aon.jooq.tables.records.InventoryDetailRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;

public class InventoryDAO {
	
	public static LinkedList<InventoryDetail> getInventoryDetailList(AONContext ctx, Integer inventoryId){
		
		return ctx.getDslContext()
				.select()
				.from(INVENTORY_DETAIL)
				.where(INVENTORY_DETAIL.INVENTORY.eq(inventoryId))
				.fetchInto(INVENTORY_DETAIL)
				.stream().map(new InventoryDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	
	private static class InventoryDetailFiller implements Function<InventoryDetailRecord, InventoryDetail> {
		
		@Override
		public InventoryDetail apply(InventoryDetailRecord r) {
			return new InventoryDetail().setId(r.getId())
					.setInventory(new Inventory().setId(r.getInventory()).setDomain(r.getDomain()))
					.setCost(r.getCost())
					.setActualQuantity(r.getActualQuantity())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setDomain(r.getDomain())
					.setItem(new Item().setId(r.getItem()).setDomain(r.getDomain()))
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
					.setRealQuantity(r.getRealQuantity());
		}

	}
}
