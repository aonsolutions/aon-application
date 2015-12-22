package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Department.DEPARTMENT;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;
import static com.esferalia.aon.jooq.tables.Series.SERIES;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;
import static com.esferalia.aon.jooq.tables.WarehouseTransferDetail.WAREHOUSE_TRANSFER_DETAIL;
import static com.esferalia.aon.jooq.tables.WorkplaceDepartment.WORKPLACE_DEPARTMENT;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.SeriesRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferDetailRecord;
import com.esferalia.aon.jooq.tables.records.WarehouseTransferRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.DepartmentFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.WarehouseFilter;
import com.esferalia.aon.occam.api.model.Properties.DepartmentProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.Series;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransfer;
import com.esferalia.aon.occam.api.model.warehouse.WarehouseTransferDetail;

public class WarehouseDAO {
	
	private static final WarehousePropertiesDAO WAREHOUSE_PROPERTIES = new WarehousePropertiesDAO();
	private static final DepartmentPropertiesDAO DEPARTMENT_PROPERTIES = new DepartmentPropertiesDAO();
	
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
	
	protected static class DepartmentPropertiesDAO implements DepartmentProperties {
		protected Condition[] getConditions(DepartmentFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(WAREHOUSE.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(WAREHOUSE.NAME);}
	}
	
	public static Warehouse getWarehouse(AONContext ctx, WarehouseFilter filter){
		return ctx.getDslContext().select()
				.from(WAREHOUSE)
				.where(WAREHOUSE_PROPERTIES.getConditions(filter))
				.fetchInto(WAREHOUSE).stream().map(new FullWarehouseFiller())
				.findFirst().orElse(null);
	}
	
	public static void deleteWarehouseTransfer(AONContext ctx, Integer inventoryId){
		LinkedList<WarehouseTransfer>  warehouseTransferlist = getWarehouseTransferList(ctx, inventoryId);
		warehouseTransferlist.stream().forEach(wt ->{
			final Integer source = wt.getSourceWarehouse();
			final Integer target = wt.getTargetWarehouse();
			LinkedList<WarehouseTransferDetail> warehouseTransferDetailList = 
					getWarehouseTransferDetailList(ctx, wt.getId());
			warehouseTransferDetailList.stream().forEach(wtd ->{
				Double quantity = wtd.getQuantity();
				if(source != null){
					ctx.getDslContext().update(STOCK)
						.set(STOCK.QUANTITY, STOCK.QUANTITY.add(quantity))
						.where(STOCK.WAREHOUSE.eq(source))
						.and(STOCK.ITEM.eq(wtd.getItem().getId()))
						.execute();
				}
				if(target != null){
					ctx.getDslContext().update(STOCK)
						.set(STOCK.QUANTITY, STOCK.QUANTITY.add(-quantity))
						.where(STOCK.WAREHOUSE.eq(target))
						.and(STOCK.ITEM.eq(wtd.getItem().getId()))
						.execute();
				}
			});
			ctx.getDslContext().delete(WAREHOUSE_TRANSFER_DETAIL)
				.where(WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER.eq(wt.getId()))
				.execute();
		});
		ctx.getDslContext().delete(WAREHOUSE_TRANSFER)
			.where(WAREHOUSE_TRANSFER.INVENTORY.eq(inventoryId))
			.execute();
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
		Result<Record2<Integer, String>> record = ctx.getDslContext().selectDistinct(DEPARTMENT.ID, DEPARTMENT.NAME)
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
	
	public static LinkedList<Series> getSeriesDeliveryList(AONContext ctx, Integer scopeId){
		return ctx.getDslContext().select()
			.from(SERIES).join(USER_SCOPE).on(SERIES.SCOPE.eq(USER_SCOPE.SCOPE))
			.join(USER).on(USER_SCOPE.USER_ID.eq(USER.ID))
			.where(SERIES.SCOPE.eq(scopeId))
			.and(USER.LOGIN.eq(ctx.getUser()))
			.and(SERIES.DELIVERY.eq((byte) 1))
			.fetchInto(SERIES).stream().map(new FullSeriesFiller())
			.collect(Collectors.toCollection(LinkedList::new));
			
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
	
	private static class FullSeriesFiller implements Function<SeriesRecord, Series> {
		@Override
		public Series apply(SeriesRecord r) {
			return new Series()
					.setActive(r.getActive())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setCode(r.getCode())
					.setDelivery(r.getDelivery())
					.setDescription(r.getDescription())
					.setInvoice(r.getInvoice())
					.setOffer(r.getOffer())
					.setPos(r.getPos())
					.setRectification(r.getRectification())
					.setSales(r.getSales())
					.setScope(r.getScope())
					.setSecurityLevel(r.getSecurityLevel())
					.setTas(r.getTas());
		}
	}
	
}
