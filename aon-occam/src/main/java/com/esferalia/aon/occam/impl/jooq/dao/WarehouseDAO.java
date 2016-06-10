package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Department.DEPARTMENT;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;
import static com.esferalia.aon.jooq.tables.WarehouseTransferDetail.WAREHOUSE_TRANSFER_DETAIL;
import static com.esferalia.aon.jooq.tables.WorkplaceDepartment.WORKPLACE_DEPARTMENT;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SeriesRecord;
import com.esferalia.aon.jooq.tables.records.StockRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferDetailRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.DepartmentFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.StockFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseTransferDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.WarehouseTransferFilter;
import com.esferalia.aon.occam.api.model.Properties.DepartmentProperties;
import com.esferalia.aon.occam.api.model.Properties.StockProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseTransferDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseTransferProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferDetail;

public class WarehouseDAO {
	
	private static final WarehousePropertiesDAO WAREHOUSE_PROPERTIES = new WarehousePropertiesDAO();
	private static final WarehouseTransferPropertiesDAO WAREHOUSE_TRANSFER_PROPERTIES = new WarehouseTransferPropertiesDAO();
	private static final WarehouseTransferDetailPropertiesDAO WAREHOUSE_TRANSFER_DETAIL_PROPERTIES = new WarehouseTransferDetailPropertiesDAO();

	private static final DepartmentPropertiesDAO DEPARTMENT_PROPERTIES = new DepartmentPropertiesDAO();
	private static final StockPropertiesDAO STOCK_PROPERTIES = new StockPropertiesDAO();
	
	
	protected static class WarehousePropertiesDAO implements WarehouseProperties {
		protected Condition[] getConditions(WarehouseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE.ID);} 
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<Byte>(WAREHOUSE.ACTIVE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE.DOMAIN);}
		@Override public Property<Integer> getDepartmentProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE.DEPARTMENT);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE.WORKPLACE);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(WAREHOUSE.NAME);}
	}

	protected static class WarehouseTransferPropertiesDAO implements WarehouseTransferProperties {
		protected Condition[] getConditions(WarehouseTransferFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER.DOMAIN);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<String>(WAREHOUSE_TRANSFER.COMMENTS);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(WAREHOUSE_TRANSFER.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(WAREHOUSE_TRANSFER.CREATION_USER);}
		@Override public Property<Integer> getInventoryProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER.INVENTORY);}
		@Override public Property<Timestamp> getIssueTimeProperty() {return new FilterDAO.PropertyDAO<Timestamp>(WAREHOUSE_TRANSFER.ISSUE_TIME);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(WAREHOUSE_TRANSFER.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(WAREHOUSE_TRANSFER.MODIFICATION_USER);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER.NUMBER);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<String>(WAREHOUSE_TRANSFER.SERIES);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<Byte>(WAREHOUSE_TRANSFER.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER.SOURCE_ID);}
		@Override public Property<Integer> getSourceWarehouseProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE);}
		@Override public Property<Integer> getTargetWarehouseProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER.TARGET_WAREHOUSE);}
	}
	
	protected static class WarehouseTransferDetailPropertiesDAO implements WarehouseTransferDetailProperties {
		protected Condition[] getConditions(WarehouseTransferDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER_DETAIL.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER_DETAIL.DOMAIN);}
		@Override public Property<Integer> getWarehouseTransferProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(WAREHOUSE_TRANSFER.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(WAREHOUSE_TRANSFER.CREATION_USER);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE_TRANSFER_DETAIL.ITEM);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(WAREHOUSE_TRANSFER_DETAIL.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(WAREHOUSE_TRANSFER_DETAIL.MODIFICATION_USER);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<Double>(WAREHOUSE_TRANSFER_DETAIL.QUANTITY);}
	}
	
	protected static class DepartmentPropertiesDAO implements DepartmentProperties {
		protected Condition[] getConditions(DepartmentFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(DEPARTMENT.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(DEPARTMENT.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(DEPARTMENT.NAME);}
	}
	
	public static Warehouse getWarehouse(AONContext ctx, WarehouseFilter filter){
		return ctx.getDslContext().select()
				.from(WAREHOUSE)
				.where(WAREHOUSE_PROPERTIES.getConditions(filter))
				.fetchInto(WAREHOUSE).stream().map(new FullWarehouseFiller())
				.findFirst().orElse(null);
	}
	
	public static WarehouseTransfer getWarehouseTransfer(AONContext ctx, WarehouseTransferFilter filter){
		return ctx.getDslContext().select().from(WAREHOUSE_TRANSFER).where(WAREHOUSE_TRANSFER_PROPERTIES.getConditions(filter))
				.fetchInto(WAREHOUSE_TRANSFER).stream().map(new FullWarehouseTransferFiller()).findFirst().orElse(new WarehouseTransfer());
	}
	
	public static Integer insertWarehouseTransfer(AONContext ctx, WarehouseTransfer warehouseTransfer){
		return ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER)
				.set(WAREHOUSE_TRANSFER.COMMENTS, warehouseTransfer.getComments())
				.set(WAREHOUSE_TRANSFER.DOMAIN, warehouseTransfer.getDomain())
				.set(WAREHOUSE_TRANSFER.CREATION_DATE, new Timestamp(warehouseTransfer.getCreationDate().getTime()))
				.set(WAREHOUSE_TRANSFER.CREATION_USER, warehouseTransfer.getCreationUser())
				.set(WAREHOUSE_TRANSFER.INVENTORY, warehouseTransfer.getInventory() != null ? warehouseTransfer.getInventory().getId(): null)
				.set(WAREHOUSE_TRANSFER.MODIFICATION_DATE,new Timestamp(warehouseTransfer.getModificationDate().getTime()))
				.set(WAREHOUSE_TRANSFER.MODIFICATION_USER, warehouseTransfer.getModificationUser())
				.set(WAREHOUSE_TRANSFER.ISSUE_TIME, new Timestamp(warehouseTransfer.getIssueTime().getTime()))
				.set(WAREHOUSE_TRANSFER.SERIES, warehouseTransfer.getSeries())
				.set(WAREHOUSE_TRANSFER.NUMBER, warehouseTransfer.getNumber())
				.set(WAREHOUSE_TRANSFER.SOURCE, warehouseTransfer.getSource())
				.set(WAREHOUSE_TRANSFER.SOURCE_ID, warehouseTransfer.getSourceId())
				.set(WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE, warehouseTransfer.getSourceWarehouse())
				.set(WAREHOUSE_TRANSFER.TARGET_WAREHOUSE, warehouseTransfer.getTargetWarehouse())
				.returning(WAREHOUSE_TRANSFER.ID).fetchOne().getId();
	}
	
	public static void updateWarehouseTransfer(AONContext ctx, WarehouseTransfer warehouseTransfer){
		ctx.getDslContext().update(WAREHOUSE_TRANSFER)
				.set(WAREHOUSE_TRANSFER.COMMENTS, warehouseTransfer.getComments())
				.set(WAREHOUSE_TRANSFER.DOMAIN, warehouseTransfer.getDomain())
				.set(WAREHOUSE_TRANSFER.CREATION_DATE, new Timestamp(warehouseTransfer.getCreationDate().getTime()))
				.set(WAREHOUSE_TRANSFER.CREATION_USER, warehouseTransfer.getCreationUser())
				.set(WAREHOUSE_TRANSFER.INVENTORY, warehouseTransfer.getInventory() != null ? warehouseTransfer.getInventory().getId(): null)
				.set(WAREHOUSE_TRANSFER.MODIFICATION_DATE,new Timestamp(warehouseTransfer.getModificationDate().getTime()))
				.set(WAREHOUSE_TRANSFER.MODIFICATION_USER, warehouseTransfer.getModificationUser())
				.set(WAREHOUSE_TRANSFER.ISSUE_TIME, new Timestamp(warehouseTransfer.getIssueTime().getTime()))
				.set(WAREHOUSE_TRANSFER.NUMBER, warehouseTransfer.getNumber())
				.set(WAREHOUSE_TRANSFER.SOURCE, warehouseTransfer.getSource())
				.set(WAREHOUSE_TRANSFER.SOURCE_ID, warehouseTransfer.getSourceId())
				.set(WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE, warehouseTransfer.getSourceWarehouse())
				.set(WAREHOUSE_TRANSFER.TARGET_WAREHOUSE, warehouseTransfer.getTargetWarehouse())
				.where(WAREHOUSE_TRANSFER.ID.eq(warehouseTransfer.getId()))
				.execute();
	}
	
	public static Integer insertWarehouseTransferDetail(AONContext ctx, WarehouseTransferDetail warehouseTransferDetail){
		return ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER_DETAIL)
				.set(WAREHOUSE_TRANSFER_DETAIL.DOMAIN, warehouseTransferDetail.getDomain())
				.set(WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, warehouseTransferDetail.getWarehouseTransfer().getId())
				.set(WAREHOUSE_TRANSFER_DETAIL.ITEM, warehouseTransferDetail.getItem().getId())
				.set(WAREHOUSE_TRANSFER_DETAIL.QUANTITY, warehouseTransferDetail.getQuantity())
				.set(WAREHOUSE_TRANSFER_DETAIL.CREATION_DATE, new Timestamp(warehouseTransferDetail.getCreationDate().getTime()))
				.set(WAREHOUSE_TRANSFER_DETAIL.CREATION_USER, warehouseTransferDetail.getCreationUser())
				.set(WAREHOUSE_TRANSFER_DETAIL.MODIFICATION_DATE, new Timestamp(warehouseTransferDetail.getModificationDate().getTime()))
				.set(WAREHOUSE_TRANSFER_DETAIL.MODIFICATION_USER, warehouseTransferDetail.getModificationUser())
				.returning(WAREHOUSE_TRANSFER.ID).fetchOne().getId();
	}
	
	protected static class StockPropertiesDAO implements StockProperties {
		protected Condition[] getConditions(StockFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(STOCK.ID);} 
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<Integer>(STOCK.WAREHOUSE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(STOCK.DOMAIN);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<Integer>(STOCK.ITEM);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<Double>(STOCK.QUANTITY);}
	}
	
	
	public static Stream<WarehouseTransfer> getWarehouseTransferStream(AONContext ctx, WarehouseTransferFilter filter){
		return ctx.getDslContext().select().from(WAREHOUSE_TRANSFER).where(WAREHOUSE_TRANSFER_PROPERTIES.getConditions(filter))
				.fetchInto(WAREHOUSE_TRANSFER).stream().map(new FullWarehouseTransferFiller());
	}
	
	public static Stream<WarehouseTransferDetail> getWarehouseTransferDetailStream(AONContext ctx, WarehouseTransferDetailFilter filter){
		return ctx.getDslContext().select().from(WAREHOUSE_TRANSFER_DETAIL).where(WAREHOUSE_TRANSFER_DETAIL_PROPERTIES.getConditions(filter))
				.fetchInto(WAREHOUSE_TRANSFER_DETAIL).stream().map(new FullWarehouseTransferDetailFiller());
	}
	
	public static void updateStock(AONContext ctx, WarehouseTransfer wt, WarehouseTransferDetail wtd){
		Double quantity = wtd.getQuantity();
		if(wt.getSourceWarehouse() != null){
			ctx.getDslContext().update(STOCK)
				.set(STOCK.QUANTITY, STOCK.QUANTITY.add(quantity))
				.where(STOCK.WAREHOUSE.eq(wt.getSourceWarehouse()))
				.and(STOCK.ITEM.eq(wtd.getItem().getId()))
				.execute();
		}
		if(wt.getTargetWarehouse() != null){
			ctx.getDslContext().update(STOCK)
				.set(STOCK.QUANTITY, STOCK.QUANTITY.add(-quantity))
				.where(STOCK.WAREHOUSE.eq(wt.getTargetWarehouse()))
				.and(STOCK.ITEM.eq(wtd.getItem().getId()))
				.execute();
		}
	}
	
	public static void deleteWarehouseTransfer(AONContext ctx, WarehouseTransferFilter filter){
		LinkedList<Integer> list = new LinkedList<Integer>();
		getWarehouseTransferStream(ctx, filter).forEach(wt -> {
			final WarehouseTransfer wtAux = wt;
			getWarehouseTransferDetailStream(ctx, f -> f.getWarehouseTransferProperty().eq(wt.getId()))
			.forEach(wtd -> updateStock(ctx, wtAux, wtd));
			list.add(wt.getId());
		});
		Integer[] array = new Integer[list.size()];
		for(Integer i = 0; i < list.size();i++) array[i] = list.get(i);
		deleteWarehouseTransferDetail(ctx, f -> f.getWarehouseTransferProperty().in(array));
		ctx.getDslContext().delete(WAREHOUSE_TRANSFER).where(WAREHOUSE_TRANSFER_PROPERTIES.getConditions(filter)).execute();
	}

	public static void deleteWarehouseTransferDetail(AONContext ctx, WarehouseTransferDetailFilter filter){
		ctx.getDslContext().delete(WAREHOUSE_TRANSFER_DETAIL)
		.where(WAREHOUSE_TRANSFER_DETAIL_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static LinkedList<WarehouseTransfer> getWarehouseTransferList(AONContext ctx, Integer inventoryId){
		return ctx.getDslContext().select()
				.from(WAREHOUSE_TRANSFER)
				.where(WAREHOUSE_TRANSFER.INVENTORY.eq(inventoryId))
				.fetchInto(WAREHOUSE_TRANSFER).stream().map(new FullWarehouseTransferFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<WarehouseTransferDetail> getWarehouseTransferDetailList(AONContext ctx, Integer warehouseTransferId){
		return ctx.getDslContext().select()
				.from(WAREHOUSE_TRANSFER_DETAIL)
				.where(WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER.eq(warehouseTransferId))
				.fetchInto(WAREHOUSE_TRANSFER_DETAIL).stream().map(new FullWarehouseTransferDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Department getDepartment(AONContext ctx, Integer workplaceId, DepartmentFilter filter){
		Record3<Integer, String, Integer> record = ctx.getDslContext()
			.selectDistinct(DEPARTMENT.ID, DEPARTMENT.NAME, DEPARTMENT.DOMAIN)
			.from(DEPARTMENT).join(WORKPLACE_DEPARTMENT)
			.on(WORKPLACE_DEPARTMENT.DEPARTMENT.eq(DEPARTMENT.ID))
			.where(DEPARTMENT_PROPERTIES.getConditions(filter))
			.and(WORKPLACE_DEPARTMENT.WORKPLACE.eq(workplaceId))
			.limit(1).fetchOne();
			
		if(record.getValue(DEPARTMENT.ID) != null){
			return new Department()
					.setDomain(record.getValue(DEPARTMENT.DOMAIN))
					.setId(record.getValue(DEPARTMENT.ID))
					.setName(record.getValue(DEPARTMENT.NAME))
					.setEmpty(false);
		}
		return new Department().setEmpty(true);
	}
	
	public static LinkedList<Department> getDepartmentList(AONContext ctx, Integer workplaceId, DepartmentFilter filter){
		Result<Record3<Integer, Integer, String>> record = ctx.getDslContext().selectDistinct(DEPARTMENT.ID, DEPARTMENT.DOMAIN, DEPARTMENT.NAME)
				.from(DEPARTMENT).join(WORKPLACE_DEPARTMENT).on(DEPARTMENT.ID.eq(WORKPLACE_DEPARTMENT.DEPARTMENT))
				.where(DEPARTMENT_PROPERTIES.getConditions(filter))
				.and(WORKPLACE_DEPARTMENT.WORKPLACE.eq(workplaceId))
				.fetch();
		
		LinkedList<Department> list = new LinkedList<Department>();
		record.stream().forEach(r -> {
			Department d = new Department()
					.setDomain(r.getValue(DEPARTMENT.DOMAIN))
					.setId(r.getValue(DEPARTMENT.ID))
					.setName(r.getValue(DEPARTMENT.NAME))
					.setEmpty(false);
			list.add(d);
		});
		return list;
	}
	
	public static Stream<Stock> getStockStream(AONContext ctx, StockFilter filter){
		return ctx.getDslContext().select().from(STOCK).where(STOCK_PROPERTIES.getConditions(filter))
		.fetchInto(STOCK).stream().map(new FullStockFiller());
	}
	
	
	public static Integer getWarehouseTransferNextNumber(AONContext ctx, String serie) {
		Result<Record1<Integer>> result = null;
		if(serie != null) result = ctx.getDslContext().select(DSL.max(WAREHOUSE_TRANSFER.NUMBER)).from(WAREHOUSE_TRANSFER)
			.where(WAREHOUSE_TRANSFER.SERIES.eq(serie)).fetch();
		else if(result == null) result = ctx.getDslContext().select(DSL.max(WAREHOUSE_TRANSFER.NUMBER)).from(WAREHOUSE_TRANSFER)
			.where(WAREHOUSE_TRANSFER.SERIES.isNull()).fetch();
		return result.isEmpty() ? 0 : result.get(0).value1()+1;
	}
	
	private static class FullWarehouseFiller implements Function<WarehouseRecord, Warehouse> {
		@Override
		public Warehouse apply(WarehouseRecord r) {
			return new Warehouse()
					.setActive(r.getActive())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setDepartment(r.getDepartment())
					.setName(r.getName())
					.setWorkplace(r.getWorkplace());
		}
	}
	
	private static class FullWarehouseTransferFiller implements Function<WarehouseTransferRecord, WarehouseTransfer> {
		
		@Override
		public WarehouseTransfer apply(WarehouseTransferRecord r) {
			return new WarehouseTransfer()
					.setComments(r.getComments())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setInventory(new Inventory().setId(r.getInventory()))
					.setIssueTime(r.getIssueTime())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
					.setNumber(r.getNumber())
					.setSeries(r.getSeries())
					.setSource(r.getSource())
					.setSourceId(r.getSourceId())
					.setSourceWarehouse(r.getSourceWarehouse())
					.setTargetWarehouse(r.getTargetWarehouse());
		}

	}
	
	private static class FullWarehouseTransferDetailFiller implements Function<WarehouseTransferDetailRecord, WarehouseTransferDetail> {
		
		@Override
		public WarehouseTransferDetail apply(WarehouseTransferDetailRecord r) {
			return new WarehouseTransferDetail()
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setItem(new Item().setId(r.getItem()))
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
					.setQuantity(r.getQuantity())
					.setWarehouseTransfer(new WarehouseTransfer().setId(r.getWarehouseTransfer()));
		}

	}
	
	private static class FullStockFiller implements Function<StockRecord, Stock> {
		@Override
		public Stock apply(StockRecord r) {
			return new Stock()
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setItem(r.getItem())
					.setQuantity(r.getQuantity())
					.setWarehouse(r.getWarehouse());
		}
	}
}
