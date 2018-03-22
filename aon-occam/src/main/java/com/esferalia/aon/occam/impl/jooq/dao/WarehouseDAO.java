package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.CarrierPacking.CARRIER_PACKING;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Department.DEPARTMENT;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;
import static com.esferalia.aon.jooq.tables.WarehouseTransferDetail.WAREHOUSE_TRANSFER_DETAIL;
import static com.esferalia.aon.jooq.tables.WorkplaceDepartment.WORKPLACE_DEPARTMENT;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.InsertValuesStep8;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.StockRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferDetailRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.CarrierPackingFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.DepartmentFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
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
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferDetail;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.CarrierPackingFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DeliveryDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DeliveryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.FullWarehouseFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO.ItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO.ProductPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.CarrierPackingPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DeliveryDetailPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DeliveryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.validation.ProductValidation;
import com.esferalia.aon.watson.server.AonDateUtils;


public class WarehouseDAO {
	
	private static final WarehousePropertiesDAO WAREHOUSE_PROPERTIES = new WarehousePropertiesDAO();
	private static final WarehouseTransferPropertiesDAO WAREHOUSE_TRANSFER_PROPERTIES = new WarehouseTransferPropertiesDAO();
	private static final WarehouseTransferDetailPropertiesDAO WAREHOUSE_TRANSFER_DETAIL_PROPERTIES = new WarehouseTransferDetailPropertiesDAO();
	private static final DeliveryPropertiesDAO DELIVERY_PROPERTIES = new DeliveryPropertiesDAO();
	private static final DeliveryDetailPropertiesDAO DELIVERY_DETAIL_PROPERTIES = new DeliveryDetailPropertiesDAO();
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
	private static final ItemPropertiesDAO ITEM_PROPERTIES = new ItemPropertiesDAO();

	private static final DepartmentPropertiesDAO DEPARTMENT_PROPERTIES = new DepartmentPropertiesDAO();
	private static final StockPropertiesDAO STOCK_PROPERTIES = new StockPropertiesDAO();
	private static final CarrierPackingPropertiesDAO CARRIER_PACKING_PROPERTIES = new CarrierPackingPropertiesDAO();
	
	
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
	
	public static Stream<Warehouse> getWarehouseStream(AONContext ctx, WarehouseFilter filter){
		return ctx.getDslContext().select()
				.from(WAREHOUSE)
				.where(WAREHOUSE_PROPERTIES.getConditions(filter))
				.fetchInto(WAREHOUSE).stream().map(new FullWarehouseFiller());
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
				.set(WAREHOUSE_TRANSFER.INVENTORY, warehouseTransfer.getInventory() != null ? warehouseTransfer.getInventory().getId(): null)				
				.set(WAREHOUSE_TRANSFER.ISSUE_TIME, new Timestamp(warehouseTransfer.getIssueTime().getTime()))
				.set(WAREHOUSE_TRANSFER.SERIES, warehouseTransfer.getSeries())
				.set(WAREHOUSE_TRANSFER.NUMBER, warehouseTransfer.getNumber())
				.set(WAREHOUSE_TRANSFER.SOURCE, warehouseTransfer.getSource())
				.set(WAREHOUSE_TRANSFER.SOURCE_ID, warehouseTransfer.getSourceId())
				.set(WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE, warehouseTransfer.getSourceWarehouse())
				.set(WAREHOUSE_TRANSFER.TARGET_WAREHOUSE, warehouseTransfer.getTargetWarehouse())
				.set(WAREHOUSE_TRANSFER.CREATION_DATE, new Timestamp(new Date().getTime()))
				.set(WAREHOUSE_TRANSFER.CREATION_USER, ctx.getUser())
				.set(WAREHOUSE_TRANSFER.MODIFICATION_DATE,  new Timestamp(new Date().getTime()))
				.set(WAREHOUSE_TRANSFER.MODIFICATION_USER, ctx.getUser())
				.returning(WAREHOUSE_TRANSFER.ID).fetchOne().getId();
	}
	
	public static void updateWarehouseTransfer(AONContext ctx, WarehouseTransfer warehouseTransfer){
		ctx.getDslContext().update(WAREHOUSE_TRANSFER)
				.set(WAREHOUSE_TRANSFER.COMMENTS, warehouseTransfer.getComments())
				.set(WAREHOUSE_TRANSFER.DOMAIN, warehouseTransfer.getDomain())
				.set(WAREHOUSE_TRANSFER.INVENTORY, warehouseTransfer.getInventory() != null ? warehouseTransfer.getInventory().getId(): null)
				.set(WAREHOUSE_TRANSFER.ISSUE_TIME, new Timestamp(warehouseTransfer.getIssueTime().getTime()))
				.set(WAREHOUSE_TRANSFER.NUMBER, warehouseTransfer.getNumber())
				.set(WAREHOUSE_TRANSFER.SOURCE, warehouseTransfer.getSource())
				.set(WAREHOUSE_TRANSFER.SOURCE_ID, warehouseTransfer.getSourceId())
				.set(WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE, warehouseTransfer.getSourceWarehouse())
				.set(WAREHOUSE_TRANSFER.TARGET_WAREHOUSE, warehouseTransfer.getTargetWarehouse())
				.set(WAREHOUSE_TRANSFER.CREATION_DATE, new Timestamp(new Date().getTime()))
				.set(WAREHOUSE_TRANSFER.CREATION_USER, ctx.getUser())
				.set(WAREHOUSE_TRANSFER.MODIFICATION_DATE,new Timestamp(new Date().getTime()))
				.set(WAREHOUSE_TRANSFER.MODIFICATION_USER, ctx.getUser())
				.where(WAREHOUSE_TRANSFER.ID.eq(warehouseTransfer.getId()))
				.execute();
	}
	
	public static Integer insertWarehouseTransferDetail(AONContext ctx, WarehouseTransferDetail warehouseTransferDetail){
		return ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER_DETAIL)
				.set(WAREHOUSE_TRANSFER_DETAIL.DOMAIN, warehouseTransferDetail.getDomain())
				.set(WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, warehouseTransferDetail.getWarehouseTransfer().getId())
				.set(WAREHOUSE_TRANSFER_DETAIL.ITEM, warehouseTransferDetail.getItem().getId())
				.set(WAREHOUSE_TRANSFER_DETAIL.QUANTITY, warehouseTransferDetail.getQuantity())
				.set(WAREHOUSE_TRANSFER_DETAIL.CREATION_DATE, new Timestamp(new Date().getTime()))
				.set(WAREHOUSE_TRANSFER_DETAIL.CREATION_USER, ctx.getUser())
				.set(WAREHOUSE_TRANSFER_DETAIL.MODIFICATION_DATE, new Timestamp(new Date().getTime()))
				.set(WAREHOUSE_TRANSFER_DETAIL.MODIFICATION_USER, ctx.getUser())
				.returning(WAREHOUSE_TRANSFER.ID).fetchOne().getId();
	}

	public static Integer insertWarehouseTransferDetail(AONContext ctx, Stream<WarehouseTransferDetail> warehouseTransferDetail){
		InsertValuesStep8<WarehouseTransferDetailRecord, Integer, Integer, Integer, Double, Timestamp, String, Timestamp, String> jooq =
				ctx.getDslContext().insertInto(WAREHOUSE_TRANSFER_DETAIL, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER,
				WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.QUANTITY, WAREHOUSE_TRANSFER_DETAIL.CREATION_DATE, WAREHOUSE_TRANSFER_DETAIL.CREATION_USER,
				WAREHOUSE_TRANSFER_DETAIL.MODIFICATION_DATE, WAREHOUSE_TRANSFER_DETAIL.MODIFICATION_USER);
		warehouseTransferDetail.forEach(wtd -> {
			jooq.values(wtd.getDomain(), wtd.getWarehouseTransfer().getId(), wtd.getItem().getId(), wtd.getQuantity(),
					new Timestamp(new Date().getTime()), ctx.getUser(),new Timestamp(new Date().getTime()), ctx.getUser());
		});
		return jooq.execute();
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

		if(quantity > 0) {
			ctx.getDslContext().update(com.esferalia.aon.jooq.tables.Item.ITEM)
				.set(com.esferalia.aon.jooq.tables.Item.ITEM.STATUS, ProductStatus.ACTIVE.value())
				.where(com.esferalia.aon.jooq.tables.Item.ITEM.ID.eq(wtd.getItem().getId()));
		}
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
			getWarehouseTransferDetailStream(ctx, f -> f.getWarehouseTransferProperty().eq(wt.getId()))
			.forEach(wtd -> updateStock(ctx, wt, wtd));
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
	
	public static Stream<WarehouseTransferDetail> getWarehouseTransferDetailStream(AONContext ctx, WarehouseTransferFilter filter,
			ProductFilter pFilter, ItemFilter iFilter) {
		
		Collection<Condition> whereConditions = new ArrayList<Condition>();
		whereConditions.addAll(Arrays.asList(WAREHOUSE_TRANSFER_PROPERTIES.getConditions(filter)));
		whereConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(pFilter)));
		whereConditions.addAll(Arrays.asList(ITEM_PROPERTIES.getConditions(iFilter)));

		return ctx.getDslContext()
			.select()
			.from(WAREHOUSE_TRANSFER)
			.join(WAREHOUSE_TRANSFER_DETAIL).on(WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER.equal(WAREHOUSE_TRANSFER.ID))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(WAREHOUSE_TRANSFER_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.where(whereConditions)
			.orderBy(WAREHOUSE_TRANSFER.ISSUE_TIME,WAREHOUSE_TRANSFER.SERIES, WAREHOUSE_TRANSFER.NUMBER)
			.fetch().stream().map(new WarehouseTransferDetailFiller());
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
	
	public static Optional<Stock> insertStock(AONContext ctx, Stock stock){
		ctx.checkWrite();
		ProductValidation.validateStocking(ctx, stock.getItem());
		return ctx.getDslContext().insertInto(STOCK, STOCK.DOMAIN, STOCK.ITEM, STOCK.QUANTITY, STOCK.WAREHOUSE)
				.values(stock.getDomain(), stock.getItem(), stock.getQuantity(), stock.getWarehouse())
				.returning().fetch().stream().map(new FullStockFiller()).findFirst();
	}
	
	public static Optional<Stock> updateStock(AONContext ctx, Stock stock){
		ctx.checkWrite();
		return ctx.getDslContext().update(STOCK)
			.set(STOCK.QUANTITY, stock.getQuantity())
			.set(STOCK.DOMAIN, stock.getDomain())
			.set(STOCK.ITEM, stock.getItem())
			.set(STOCK.WAREHOUSE, stock.getWarehouse())
			.where(STOCK.ID.eq(stock.getId()))
			.returning().fetch().stream().map(new FullStockFiller()).findFirst();
	}
	
	public static Optional<Stock> deleteStock(AONContext ctx, Integer stockId){
		Optional<Stock> stock = getStockStream(ctx, f -> f.getIdProperty().eq(stockId)).findFirst();
		ctx.getDslContext().delete(STOCK).where(STOCK.ID.eq(stockId)).execute();
		return stock;
	}
	
	public static Integer getWarehouseTransferNextNumber(AONContext ctx, String serie) {
		Result<Record1<Integer>> result = null;
		if(serie != null) result = ctx.getDslContext().select(DSL.max(WAREHOUSE_TRANSFER.NUMBER)).from(WAREHOUSE_TRANSFER)
			.where(WAREHOUSE_TRANSFER.SERIES.eq(serie)).fetch();
		else if(result == null) result = ctx.getDslContext().select(DSL.max(WAREHOUSE_TRANSFER.NUMBER)).from(WAREHOUSE_TRANSFER)
			.where(WAREHOUSE_TRANSFER.SERIES.isNull()).fetch();
		return result.isEmpty() ? 0 : result.get(0).value1()+1;
	}
	
	
	
	public static Delivery getDelivery(AONContext ctx, Integer deliveryId) {
		return ctx.getDslContext().select().from(DELIVERY)
				.where(DELIVERY.ID.eq(deliveryId)).limit(1).fetchInto(DELIVERY)
				.stream().map(new DeliveryFiller()).findFirst()
				.orElse(new Delivery());
	}

	public static Delivery getDelivery(AONContext ctx, DeliveryFilter filter) {
		return ctx.getDslContext().select().from(DELIVERY)
				.where(DELIVERY_PROPERTIES.getConditions(filter)).limit(1)
				.fetchInto(DELIVERY).stream().map(new DeliveryFiller())
				.findFirst().orElse(new Delivery());
	}

	public static LinkedList<Delivery> getDeliveryList(AONContext ctx,
			DeliveryFilter filter) {
		return ctx.getDslContext().select().from(DELIVERY)
				.where(DELIVERY_PROPERTIES.getConditions(filter))
				.fetchInto(DELIVERY).stream().map(new DeliveryFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}

	public static DeliveryDetail getDeliveryDetail(AONContext ctx,
			DeliveryDetailFilter filter) {
		return ctx.getDslContext().select().from(DELIVERY_DETAIL)
				.where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter))
				.limit(1).fetchInto(DELIVERY_DETAIL).stream()
				.map(new DeliveryDetailFiller()).findFirst()
				.orElse(new DeliveryDetail());
	}
	
	public static LinkedList<DeliveryDetail> getDeliveryDetailList(
			AONContext ctx, Integer deliveryId) {
		return ctx.getDslContext().select().from(DELIVERY_DETAIL)
				.where(DELIVERY_DETAIL.DELIVERY.eq(deliveryId))
				.orderBy(DELIVERY_DETAIL.SALES_DETAIL.desc(), DELIVERY_DETAIL.ITEM.asc())
				.fetchInto(DELIVERY_DETAIL).stream()
				.map(new DeliveryDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<DeliveryDetail> getDeliveryDetailList(
			AONContext ctx, DeliveryDetailFilter filter) {
		return ctx.getDslContext().select().from(DELIVERY_DETAIL)
				.where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter))
				.orderBy(DELIVERY_DETAIL.SALES_DETAIL.desc(), DELIVERY_DETAIL.ITEM.asc())
				.fetchInto(DELIVERY_DETAIL).stream()
				.map(new DeliveryDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static int insertDelivery(AONContext ctx, Delivery delivery) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		return ctx
				.getDslContext()
				.insertInto(DELIVERY, DELIVERY.DOMAIN, DELIVERY.PROJECT,
						DELIVERY.SERIES, DELIVERY.NUMBER, DELIVERY.CUSTOMER,
						DELIVERY.ADDRESS, DELIVERY.ISSUE_TIME,
						DELIVERY.PAY_METHOD, DELIVERY.SECURITY_LEVEL,
						DELIVERY.STATUS, DELIVERY.COMMENTS, DELIVERY.REMARKS,
						DELIVERY.WORKPLACE, DELIVERY.SCOPE,
						DELIVERY.NUMBER_OF_PYMNTS,
						DELIVERY.DAYS_TO_FIRST_PYMNT,
						DELIVERY.DAYS_BETWEEN_PYMNTS, DELIVERY.PYMNT_DAYS,
						DELIVERY.BANK_ACCOUNT, DELIVERY.BANK_ALIAS,
						DELIVERY.BIC, DELIVERY.CREATION_USER,
						DELIVERY.CREATION_DATE, DELIVERY.MODIFICATION_USER,
						DELIVERY.MODIFICATION_DATE)
				.values(delivery.getDomain(), delivery.getProject().getId(),
						delivery.getSeries(), delivery.getNumber(),
						delivery.getCustomer(), delivery.getAddress(),
						delivery.getIssueTime(), delivery.getPayMethod(),
						delivery.getSecurityLevel(), delivery.getStatus().ordinal(),
						delivery.getComments(), delivery.getRemarks(),
						delivery.getWorkplace(), delivery.getScope(),
						delivery.getNumberOfPymnts(),
						delivery.getDaysToFirstPymnt(),
						delivery.getDaysBetweenPymnt(),
						delivery.getPymntDays(), delivery.getBankAccount(),
						delivery.getBankAlias(), delivery.getBic(),
						ctx.getUser(), creationDate, ctx.getUser(),
						modificationDate).returning(DELIVERY.ID).fetchOne()
				.getId();
	}
	
	public static void insertDeliveryDetail(AONContext ctx,
			DeliveryDetail detail) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		ctx.getDslContext()
				.insertInto(DELIVERY_DETAIL, DELIVERY_DETAIL.DOMAIN,
						DELIVERY_DETAIL.DELIVERY, DELIVERY_DETAIL.LINE,
						DELIVERY_DETAIL.ITEM, DELIVERY_DETAIL.DESCRIPTION,
						DELIVERY_DETAIL.WAREHOUSE, DELIVERY_DETAIL.QUANTITY,
						DELIVERY_DETAIL.PRICE, DELIVERY_DETAIL.DISCOUNT_EXPR,
						DELIVERY_DETAIL.SALES_DETAIL,
						DELIVERY_DETAIL.CREATION_USER,
						DELIVERY_DETAIL.CREATION_DATE,
						DELIVERY_DETAIL.MODIFICATION_USER,
						DELIVERY_DETAIL.MODIFICATION_DATE)
				.values(detail.getDomain(), detail.getDelivery().getId(),
						detail.getLine(), detail.getItem().getId(),
						detail.getDescription(), detail.getWarehouse(),
						detail.getQuantity(), detail.getPrice(),
						detail.getDiscountExpression(),
						detail.getSalesDetail(), ctx.getUser(), creationDate,
						ctx.getUser(), modificationDate).execute();
	}
	
	public static void insertDeliveryDetails(AONContext ctx,
			List<DeliveryDetail> list) {
		ctx.checkWrite();
		list.forEach(detail -> {
			Timestamp creationDate = null, modificationDate = null;
			creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
			modificationDate = new java.sql.Timestamp(
					new java.util.Date().getTime());
			ctx.getDslContext()
			.insertInto(DELIVERY_DETAIL, DELIVERY_DETAIL.DOMAIN,
					DELIVERY_DETAIL.DELIVERY, DELIVERY_DETAIL.LINE,
					DELIVERY_DETAIL.ITEM, DELIVERY_DETAIL.DESCRIPTION,
					DELIVERY_DETAIL.WAREHOUSE, DELIVERY_DETAIL.QUANTITY,
					DELIVERY_DETAIL.PRICE, DELIVERY_DETAIL.DISCOUNT_EXPR,
					DELIVERY_DETAIL.SALES_DETAIL,
					DELIVERY_DETAIL.CREATION_USER,
					DELIVERY_DETAIL.CREATION_DATE,
					DELIVERY_DETAIL.MODIFICATION_USER,
					DELIVERY_DETAIL.MODIFICATION_DATE)
					.values(detail.getDomain(), detail.getDelivery().getId(),
							detail.getLine(), detail.getItem().getId(),
							detail.getDescription(), detail.getWarehouse(),
							detail.getQuantity(), detail.getPrice(),
							detail.getDiscountExpression(),
							detail.getSalesDetail(), ctx.getUser(), creationDate,
							ctx.getUser(), modificationDate).execute();
		});
	}
	
	public static void updateDelivery(AONContext ctx, Delivery delivery) {
		ctx.checkWrite();
		Timestamp creationDate = null, modificationDate = null;
		creationDate = new java.sql.Timestamp(new java.util.Date().getTime());
		modificationDate = new java.sql.Timestamp(
				new java.util.Date().getTime());
		
		ctx.getDslContext().update(DELIVERY)
		.set(DELIVERY.DOMAIN, delivery.getDomain())
		.set(DELIVERY.PROJECT, delivery.getProject().getId())
		.set(DELIVERY.SERIES, delivery.getSeries())
		.set(DELIVERY.NUMBER, delivery.getNumber())
		.set(DELIVERY.CUSTOMER, delivery.getCustomer())
		.set(DELIVERY.ADDRESS, delivery.getAddress())
		.set(DELIVERY.ISSUE_TIME, new Timestamp(delivery.getIssueTime()!=null?delivery.getIssueTime().getTime():(new Date()).getTime()))
		.set(DELIVERY.PAY_METHOD, delivery.getPayMethod())
		.set(DELIVERY.SECURITY_LEVEL, delivery.getSecurityLevel())
		.set(DELIVERY.STATUS, (byte)delivery.getStatus().ordinal())
		.set(DELIVERY.COMMENTS, delivery.getComments())
		.set(DELIVERY.REMARKS, delivery.getRemarks())
		.set(DELIVERY.WORKPLACE, delivery.getWorkplace())
		.set(DELIVERY.SCOPE, delivery.getScope())
		.set(DELIVERY.NUMBER_OF_PYMNTS, delivery.getNumberOfPymnts())
		.set(DELIVERY.DAYS_TO_FIRST_PYMNT, delivery.getDaysToFirstPymnt())
		.set(DELIVERY.DAYS_BETWEEN_PYMNTS, delivery.getDaysBetweenPymnt())
		.set(DELIVERY.PYMNT_DAYS, delivery.getPymntDays())
		.set(DELIVERY.BANK_ACCOUNT, delivery.getBankAccount())
		.set(DELIVERY.BANK_ALIAS, delivery.getBankAlias())
		.set(DELIVERY.BIC, delivery.getBic())
		.set(DELIVERY.CARRIER, delivery.getCarrier())
		.set(DELIVERY.CARRIER_PACKING, delivery.getCarrierPacking())
		.set(DELIVERY.CREATION_USER, ctx.getUser())
		.set(DELIVERY.CREATION_DATE, creationDate)
		.set(DELIVERY.MODIFICATION_USER, ctx.getUser())
		.set(DELIVERY.MODIFICATION_DATE, modificationDate)
		.where(DELIVERY.ID.eq(delivery.getId()))
		.execute();
	}
	
	// ----------------- CARRIER 
	public static int insertCarrier(AONContext ctx, Carrier carrier) {
		ctx.checkWrite();
		Registry registry = ctx
				.getDslContext()
				.insertInto(REGISTRY, REGISTRY.ALIAS, REGISTRY.DOCUMENT,
						REGISTRY.DOMAIN, REGISTRY.NAME, REGISTRY.NATIONALITY,
						REGISTRY.TYPE)
				.values(carrier.getAlias(), carrier.getDocument(),
						carrier.getDomain(), carrier.getName(),
						carrier.getDocumentCountry().getIso2(),
						carrier.getType()).returning().fetch().stream()
				.map(new RegistryFiller()).findFirst().orElse(new Registry());

		ctx.getDslContext()
				.insertInto(CARRIER, CARRIER.DOMAIN, CARRIER.SCOPE,
						CARRIER.REGISTRY, CARRIER.STATUS)
				.values(carrier.getDomain(), carrier.getScope(),
						registry.getId(), carrier.getStatus().value())
				.execute();
		return registry.getId();
	}
	
	// ----------------- CARRIER PACKING

	public static Stream<String> getCarrierPackingSeries(AONContext ctx){
		return ctx.getDslContext().selectDistinct(CARRIER_PACKING.SERIES).from(CARRIER_PACKING)
				.where(CARRIER_PACKING.DOMAIN.eq(ctx.getDomainId()))
				.fetch().stream().map(r -> r.getValue(CARRIER_PACKING.SERIES));
	}	
	
	public static Stream<CarrierPacking> getCarrierPackingStream(AONContext ctx, CarrierPackingFilter filter){
		return CARRIER_PACKING_PROPERTIES.build(ctx.getDslContext().select().from(CARRIER_PACKING)
				.join(REGISTRY).on(REGISTRY.ID.eq(CARRIER_PACKING.CARRIER)), filter)
				.fetch().stream().map(new CarrierPackingFiller());
	}
	
	public static Integer insertCarrierPacking(AONContext ctx, CarrierPacking carrierPacking){
		if(carrierPacking.getNumber()==null || carrierPacking.getNumber()==0){
			Integer num = getCarrierPackingStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getSeriesProperty().eq(carrierPacking.getSeries())))
					.mapToInt(r -> r.getNumber()).max().orElse(0);
			carrierPacking.setNumber(num +1);
		}
		return ctx.getDslContext().insertInto(CARRIER_PACKING, CARRIER_PACKING.CARRIER,
				CARRIER_PACKING.CARRIER_REFERENCE, CARRIER_PACKING.COMMENTS, CARRIER_PACKING.CREATION_DATE,
				CARRIER_PACKING.CREATION_USER, CARRIER_PACKING.DELIVERY_DATE,
				CARRIER_PACKING.DOMAIN, CARRIER_PACKING.DRIVER_DOCUMENT, 
				CARRIER_PACKING.DRIVER_NAME, CARRIER_PACKING.ISSUE_DATE,
				CARRIER_PACKING.MODIFICATION_DATE, CARRIER_PACKING.MODIFICATION_USER,
				CARRIER_PACKING.NUMBER, CARRIER_PACKING.NUMBER_PLATE,
				CARRIER_PACKING.SERIES,	CARRIER_PACKING.STATUS, CARRIER_PACKING.TYPE,
				CARRIER_PACKING.GROSS, CARRIER_PACKING.TARE, CARRIER_PACKING.ADDITIONAL_TARE, CARRIER_PACKING.NET, 
				CARRIER_PACKING.RECEPTION_START_DATE, CARRIER_PACKING.RECEPTION_END_DATE)
				.values(carrierPacking.getCarrier() != null ? carrierPacking.getCarrier() : 0,
						carrierPacking.getCarrierReference(), carrierPacking.getComments(), carrierPacking.getCreationDate() != null ? new Timestamp(new Date().getTime()) : null,
						ctx.getUser(), carrierPacking.getDeliveryDate() != null ? new Timestamp(carrierPacking.getDeliveryDate().getTime()) : null,
						carrierPacking.getDomain() != null ? carrierPacking.getDomain() : ctx.getDomainId(), carrierPacking.getDriverDocument(),
						carrierPacking.getDriverName(), carrierPacking.getIssueDate() != null ? new Timestamp(carrierPacking.getIssueDate().getTime()) : null,
						carrierPacking.getModificationDate() != null ? new Timestamp(carrierPacking.getModificationDate().getTime()) : null, carrierPacking.getModificationUser(),
						carrierPacking.getNumber() != null ? carrierPacking.getNumber() : 1, carrierPacking.getNumberPlate(), 
						carrierPacking.getSeries(), carrierPacking.getStatus() != null ? carrierPacking.getStatus().value() : 0, carrierPacking.getStatus() != null ? carrierPacking.getType().value() : 0,
						carrierPacking.getGross(), carrierPacking.getTare(), carrierPacking.getAdditionalTare(), carrierPacking.getNet(),
						AonDateUtils.toTimestamp(carrierPacking.getReceptionStartDate()), AonDateUtils.toTimestamp(carrierPacking.getReceptionEndDate()))
				.returning(CARRIER_PACKING.ID).fetchOne().getId();
	}
	
	public static CarrierPacking updateCarrierPacking(AONContext ctx, CarrierPacking carrierPacking, CarrierPackingFilter filter){
		return ctx.getDslContext().update(CARRIER_PACKING)
				.set(CARRIER_PACKING.CARRIER, carrierPacking.getCarrier() != null ? carrierPacking.getCarrier() : 0)
				.set(CARRIER_PACKING.CARRIER_REFERENCE, carrierPacking.getCarrierReference())
				.set(CARRIER_PACKING.COMMENTS, carrierPacking.getComments())
				.set(CARRIER_PACKING.CREATION_DATE, carrierPacking.getCreationDate() != null ? new Timestamp(carrierPacking.getCreationDate().getTime()) : null)
				.set(CARRIER_PACKING.CREATION_USER, carrierPacking.getCreationUser())
				.set(CARRIER_PACKING.DELIVERY_DATE, carrierPacking.getDeliveryDate() != null ? new Timestamp(carrierPacking.getDeliveryDate().getTime()) : null)
				.set(CARRIER_PACKING.DOMAIN, carrierPacking.getDomain())
				.set(CARRIER_PACKING.DRIVER_DOCUMENT, carrierPacking.getDriverDocument())
				.set(CARRIER_PACKING.DRIVER_NAME, carrierPacking.getDriverName())
				.set(CARRIER_PACKING.ISSUE_DATE, carrierPacking.getIssueDate() != null ? new Timestamp(carrierPacking.getIssueDate().getTime()) : null)
				.set(CARRIER_PACKING.NUMBER, carrierPacking.getNumber())
				.set(CARRIER_PACKING.NUMBER_PLATE, carrierPacking.getNumberPlate())
				.set(CARRIER_PACKING.SERIES, carrierPacking.getSeries())
				.set(CARRIER_PACKING.STATUS, carrierPacking.getStatus().value())
				.set(CARRIER_PACKING.TYPE, carrierPacking.getType().value())
				.set(CARRIER_PACKING.GROSS, carrierPacking.getGross())
				.set(CARRIER_PACKING.TARE, carrierPacking.getTare())
				.set(CARRIER_PACKING.ADDITIONAL_TARE, carrierPacking.getAdditionalTare())
				.set(CARRIER_PACKING.NET, carrierPacking.getNet())
				.set(CARRIER_PACKING.RECEPTION_START_DATE, AonDateUtils.toTimestamp(carrierPacking.getReceptionStartDate()))
				.set(CARRIER_PACKING.RECEPTION_END_DATE, AonDateUtils.toTimestamp(carrierPacking.getReceptionEndDate()))
				.set(CARRIER_PACKING.MODIFICATION_DATE, new Timestamp(new Date().getTime()))
				.set(CARRIER_PACKING.MODIFICATION_USER, ctx.getUser())
			.where(CARRIER_PACKING_PROPERTIES.getConditions(filter))
			.returning().fetch().stream().map(new CarrierPackingFiller()).findFirst().orElse(new CarrierPacking());
	}
	
	public static void deleteCarrierPacking(AONContext ctx, CarrierPackingFilter filter){
		Integer nullInt = null;
		
		LinkedList<Integer> list = getCarrierPackingStream(ctx, filter).map(f -> f.getId()).collect(Collectors.toCollection(LinkedList::new));

		ctx.getDslContext().update(PURCHASE)
			.set(PURCHASE.CARRIER_PACKING, nullInt)
			.where(PURCHASE.CARRIER_PACKING.in(list))
			.execute();
		
		ctx.getDslContext().update(PURCHASE_DETAIL)
			.set(PURCHASE_DETAIL.CARRIER_PACKING, nullInt)
			.where(PURCHASE_DETAIL.CARRIER_PACKING.in(list))
			.execute();
		
		ctx.getDslContext().update(DELIVERY)
			.set(DELIVERY.CARRIER_PACKING, nullInt)
			.where(DELIVERY.CARRIER_PACKING.in(list))
			.execute();
			
		ctx.getDslContext().delete(CARRIER_PACKING)
			.where(CARRIER_PACKING_PROPERTIES.getConditions(filter))
			.execute();
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
	
	private static class WarehouseTransferDetailFiller implements Function<Record, WarehouseTransferDetail> {
		
		@Override
		public WarehouseTransferDetail apply(Record r) {
			return new WarehouseTransferDetail()
					.setId(r.getValue(WAREHOUSE_TRANSFER_DETAIL.ID))
					.setDomain(r.getValue(WAREHOUSE_TRANSFER_DETAIL.DOMAIN))
					.setItem(new Item().setId(r.getValue(WAREHOUSE_TRANSFER_DETAIL.ITEM)))
					.setQuantity(r.getValue(WAREHOUSE_TRANSFER_DETAIL.QUANTITY))
					.setWarehouseTransfer(new WarehouseTransfer()
							.setId(r.getValue(WAREHOUSE_TRANSFER.ID))
							.setIssueTime(r.getValue(WAREHOUSE_TRANSFER.ISSUE_TIME))
							.setSeries(r.getValue(WAREHOUSE_TRANSFER.SERIES))
							.setNumber(r.getValue(WAREHOUSE_TRANSFER.NUMBER)));
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
