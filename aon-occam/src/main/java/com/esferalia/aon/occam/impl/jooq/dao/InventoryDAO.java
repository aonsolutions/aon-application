package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.Tables.PRODUCT;
import static com.esferalia.aon.jooq.tables.Inventory.INVENTORY;
import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.InventoryDetailRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.InventoryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.InventoryFilter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InventoryDetailPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InventoryPropertiesDAO;

public class InventoryDAO {

	public static final InventoryPropertiesDAO INVENTORY_PROPERTIES = new InventoryPropertiesDAO();
	public static final InventoryDetailPropertiesDAO INVENTORY_DETAIL_PROPERTIES = new InventoryDetailPropertiesDAO();
	
	private static SelectConditionStep<Record> select(AONContext ctx, InventoryFilter filter) {
		return ctx.getDslContext().select()
			.from(INVENTORY)
			.where(INVENTORY_PROPERTIES.getConditions(filter));
	}

	public static Inventory get(AONContext ctx, Integer domain, Integer id, Options...options) {
		Inventory inventory = select(ctx, f -> f.getDomainProperty().eq(domain).and(f.getIdProperty().eq(id)))
				.limit(1).fetch().stream().map(new InventoryFiller()).findFirst().orElse(null); 
		if(options.length > 0 && options[0].isFull()) {
			getInventoryDetailStream(ctx, f -> f.getDomainProperty().eq(domain).and(f.getInventoryProperty().eq(id)))
			.forEach(detail -> inventory.addDetail(detail));
		}
			
		return inventory;
	}
		
	public static Stream<Inventory> getStream(AONContext ctx, InventoryFilter filter, Options...options) {
		return select(ctx, filter)
			.fetch().stream().map(new InventoryFiller());
	}
	
	public static List<Inventory> getList(AONContext ctx, InventoryFilter filter, Options...options) {
		return getStream(ctx, filter, options)
				.collect(Collectors.toList());
	}
	
	public static Stream<InventoryDetail> getInventoryDetailStream(AONContext ctx, InventoryDetailFilter filter){
		return ctx.getDslContext()
			.select()
			.from(INVENTORY_DETAIL)
			.join(ITEM).on(INVENTORY_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.where(INVENTORY_DETAIL_PROPERTIES.getConditions(filter))
			.fetch()
			.stream().map(new com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.InventoryDetailFiller());
	}

	public static InventoryDetail getInventoryDetail(AONContext ctx, Integer domain, Integer id){
		return getInventoryDetailStream(ctx, f -> f.getDomainProperty().eq(domain).and(f.getIdProperty().eq(id)))
				.findFirst().orElse(null);	
	}
	
	public static LinkedList<InventoryDetail> getInventoryDetailList(AONContext ctx, Integer inventoryId){		
		return ctx.getDslContext()
				.select()
				.from(INVENTORY_DETAIL)
				.where(INVENTORY_DETAIL.INVENTORY.eq(inventoryId))
				.fetchInto(INVENTORY_DETAIL)
				.stream().map(new InventoryDetailFiller(ctx))
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
				.stream().map(new InventoryFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static InventoryDetail saveInventoryDetail(AONContext ctx, InventoryDetail inventoryDetail) {
		return inventoryDetail.getId() != null
			? updateInventoryDetail(ctx, inventoryDetail)
			: insertInventoryDetail(ctx, inventoryDetail);	
	}
	
	private static InventoryDetail insertInventoryDetail(AONContext ctx, InventoryDetail inventoryDetail){
		Integer id = ctx.getDslContext().insertInto(INVENTORY_DETAIL)
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
		.returning(INVENTORY_DETAIL.ID).fetchOne().getId();
		return inventoryDetail.setId(id);
	}
	
	public static InventoryDetail updateInventoryDetail(AONContext ctx, InventoryDetail inventoryDetail){
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
		return inventoryDetail;
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
	
	private static class InventoryFiller extends Filler implements Function<Record, Inventory> {
		
		@Override
		public Inventory apply(Record r) {
			return new Inventory()
				.setId(getValue(r, INVENTORY.ID))
				.setDomain(getValue(r, INVENTORY.DOMAIN))
				.setInventoryDate(getValue(r, INVENTORY.INVENTORY_DATE))
				.setStatus(getValue(r, INVENTORY.STATUS).intValue()) // TODO CHANGE TO ENUM
				.setWarehouse(getValue(r, INVENTORY.WAREHOUSE))
				.setDescription(getValue(r, INVENTORY.DESCRIPTION))
				.setCreationDate(getValue(r, INVENTORY.CREATION_DATE))
				.setCreationUser(getValue(r, INVENTORY.CREATION_USER))
				.setModificationDate(getValue(r, INVENTORY.MODIFICATION_DATE))
				.setModificationUser(getValue(r, INVENTORY.MODIFICATION_USER));
		}

	}
}
