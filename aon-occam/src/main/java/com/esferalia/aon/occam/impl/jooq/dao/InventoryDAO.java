package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Inventory.INVENTORY;
import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esferalia.aon.jooq.tables.records.InventoryDetailRecord;
import com.esferalia.aon.jooq.tables.records.InventoryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;

public class InventoryDAO {
	
	public static LinkedList<InventoryDetail> getInventoryDetailList(AONContext ctx, Integer inventoryId){
		
		return ctx.getDslContext()
				.select()
				.from(INVENTORY_DETAIL)
				.where(INVENTORY_DETAIL.INVENTORY.eq(inventoryId))
				.fetchInto(INVENTORY_DETAIL)
				.stream().map(new InventoryDetailFiller(ctx))
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	
	public static LinkedList<Inventory> getInventoryList(AONContext ctx, Date startDate, Date endDate){
		return ctx.getDslContext()
			.select()
			.from(INVENTORY)
			.where(INVENTORY.INVENTORY_DATE.between(startDate, endDate))
			.and(INVENTORY.DOMAIN.eq(ctx.getDomainId()))
			.fetchInto(INVENTORY)
			.stream().map(new FullInventoryFiller())
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Inventory> getTwoLastInventory(AONContext ctx, Integer warehouseId){
		return ctx.getDslContext()
				.select()
				.from(INVENTORY)
				.where(INVENTORY.WAREHOUSE.eq(warehouseId))
				.orderBy(INVENTORY.INVENTORY_DATE.desc())
				.limit(2)
				.fetchInto(INVENTORY)
				.stream().map(new FullInventoryFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static void updateInventoryDetail(AONContext ctx, InventoryDetail inventoryDetail){
		ctx.getDslContext().update(INVENTORY_DETAIL)
		.set(INVENTORY_DETAIL.ACTUAL_QUANTITY, inventoryDetail.getActualQuantity())
		.set(INVENTORY_DETAIL.COST, inventoryDetail.getCost())
		.set(INVENTORY_DETAIL.CREATION_DATE, inventoryDetail.getCreationDate() != null ? new Timestamp(inventoryDetail.getCreationDate().getTime()) : null)
		.set(INVENTORY_DETAIL.CREATION_USER, inventoryDetail.getCreationUser())
		.set(INVENTORY_DETAIL.DOMAIN, inventoryDetail.getDomain())
		.set(INVENTORY_DETAIL.INVENTORY, inventoryDetail.getInventory().getId())
		.set(INVENTORY_DETAIL.ITEM, inventoryDetail.getItem().getId())
		.set(INVENTORY_DETAIL.MODIFICATION_DATE, inventoryDetail.getModificationDate() != null ? new Timestamp(inventoryDetail.getModificationDate().getTime()) : null)
		.set(INVENTORY_DETAIL.MODIFICATION_USER, inventoryDetail.getModificationUser())
		.set(INVENTORY_DETAIL.REAL_QUANTITY, inventoryDetail.getRealQuantity())
		.where(INVENTORY_DETAIL.ID.eq(inventoryDetail.getId()))
		.execute();
	}
	
	public static void updateZeroInventoryDetail(AONContext ctx){
		ctx.getDslContext().update(INVENTORY_DETAIL)
		.set(INVENTORY_DETAIL.COST, 0.0)
		.where(INVENTORY_DETAIL.REAL_QUANTITY.eq(0.0))
		.execute();
	}
	
	public static void updateInventory(AONContext ctx, Inventory inventory){
		ctx.getDslContext().update(INVENTORY)
		.set(INVENTORY.CREATION_DATE, inventory.getCreationDate() != null ?
				new Timestamp(inventory.getCreationDate().getTime()) : null)
		.set(INVENTORY.CREATION_USER, inventory.getCreationUser())
		.set(INVENTORY.DESCRIPTION, inventory.getDescription())
		.set(INVENTORY.DOMAIN, inventory.getDomain())
		.set(INVENTORY.INVENTORY_DATE, inventory.getInventoryDate() != null ?
				new Date(inventory.getInventoryDate().getTime()) : null)
		.set(INVENTORY.MODIFICATION_DATE, inventory.getModificationDate() != null ? 
				new Timestamp(inventory.getModificationDate().getTime()) : null)
		.set(INVENTORY.MODIFICATION_USER, inventory.getModificationUser())
		.set(INVENTORY.STATUS, inventory.getStatus().byteValue())
		.set(INVENTORY.WAREHOUSE, inventory.getWarehouse())
		.where(INVENTORY.ID.eq(inventory.getId()))
		.execute();
	}
	
	public static void deleteInventory(AONContext ctx, Integer inventoryId){
		deleteInventoryDetail(ctx, inventoryId);
		ctx.getDslContext().delete(INVENTORY).where(INVENTORY.ID.eq(inventoryId)).execute();
	}
	
	public static void deleteInventoryDetail(AONContext ctx, Integer inventoryId){
		ctx.getDslContext().delete(INVENTORY_DETAIL).where(INVENTORY_DETAIL.INVENTORY.eq(inventoryId)).execute();
	}
	
	private static class InventoryDetailFiller implements Function<InventoryDetailRecord, InventoryDetail> {
		AONContext ctx;
		public InventoryDetailFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public InventoryDetail apply(InventoryDetailRecord r) {
			return new InventoryDetail().setId(r.getId())
					.setInventory(new Inventory().setId(r.getInventory()).setDomain(r.getDomain()))
					.setCost(r.getCost())
					.setActualQuantity(r.getActualQuantity())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setDomain(r.getDomain())
					.setItem(AON.getItem(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), r.getItem()))
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
					.setRealQuantity(r.getRealQuantity());
		}

	}
	
	private static class FullInventoryFiller implements Function<InventoryRecord, Inventory> {
		
		@Override
		public Inventory apply(InventoryRecord r) {
			return new Inventory().setId(r.getId())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setDescription(r.getDescription())
					.setDomain(r.getDomain())
					.setInventoryDate(r.getInventoryDate())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
					.setStatus(r.getStatus().intValue())
					.setWarehouse(r.getWarehouse());
		}

	}
}
