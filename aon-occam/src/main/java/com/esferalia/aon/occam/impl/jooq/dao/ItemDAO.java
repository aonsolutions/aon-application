package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.ItemComposition.ITEM_COMPOSITION;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.occam.impl.jooq.dao.ItemCompositionDAO.COMPOSITION_ALIAS;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.jooq.tables.records.RitemRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.impl.jooq.dao.ItemCompositionDAO.ItemCompositionFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO.ProductFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.RItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.validation.ItemAutoComplete;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class ItemDAO {
	
	private ItemDAO() {
	    throw new IllegalStateException("Utility class");
	}
	
	private static final ItemPropertiesDAO ITEM_PROPERTIES = new ItemPropertiesDAO();
	private static final RItemPropertiesDAO RITEM_PROPERTIES = new RItemPropertiesDAO();

	protected static class ItemPropertiesDAO implements ItemProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, ItemFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ItemFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ITEM.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ITEM.DOMAIN);}
		@Override public Property<Integer> getProductProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PRODUCT);}
		@Override public Property<String> getDetailProperty() {return new FilterDAO.PropertyDAO<>(ITEM.DETAIL);}
		@Override public Property<String> getDetail2Property() {return new FilterDAO.PropertyDAO<>(ITEM.DETAIL2);}
		@Override public Property<String> getDetail3Property() {return new FilterDAO.PropertyDAO<>(ITEM.DETAIL3);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(ITEM.DESCRIPTION);}
		@Override public Property<String> getSerialNumberProperty() {return new FilterDAO.PropertyDAO<>(ITEM.SERIAL_NUMBER);}
		@Override public Property<Date> getSerialDateProperty() {return new FilterDAO.PropertyDAO<>(ITEM.SERIAL_DATE);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PRICE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(ITEM.STATUS);}
		@Override public Property<Double> getExpensesPercentProperty() {return new FilterDAO.PropertyDAO<>(ITEM.EXPENSES_PERCENT);}
		@Override public Property<Double> getExpensesFixedProperty() {return new FilterDAO.PropertyDAO<>(ITEM.EXPENSES_FIXED);}
		@Override public Property<Double> getProfitPercentProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PROFIT_PERCENT);}
		@Override public Property<Double> getPurchasePriceProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PURCHASE_PRICE);}
		@Override public Property<Byte> getInternetProperty() {return new FilterDAO.PropertyDAO<>(ITEM.INTERNET);}
		@Override public Property<String> getBarcodeProperty() {return new FilterDAO.PropertyDAO<>(ITEM.BARCODE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(ITEM.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(ITEM.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(ITEM.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(ITEM.MODIFICATION_DATE);}
		
		@Override public Property<Integer> getPackFormatTagProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PACK_FORMAT_TAG);}
		@Override public Property<Integer> getPackUnitsProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PACK_UNITS);}
		@Override public Property<Integer> getPackUnitsTagProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PACK_UNITS_TAG);}
		@Override public Property<Double> getPackMeasurementProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PACK_MEASUREMENT);}
		@Override public Property<Integer> getPackMeasurementTagProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PACK_MEASUREMENT_TAG);}
		@Override public Property<Integer> getStockUnitTagProperty() {return new FilterDAO.PropertyDAO<>(ITEM.STOCK_UNIT_TAG);}
		
		@Override public Property<String> getProductCodeProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.CODE);}
		@Override public Property<String> getProductNameProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.NAME);}
		
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RITEM.REGISTRY);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(RITEM.TYPE);}


	}

	
	// ----- SELECT

	private static SelectConditionStep<Record> select(AONContext ctx, ItemFilter filter) {
		 return ctx.getDslContext().select()
			.from(ITEM)
			.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.leftOuterJoin(TAX).on(PRODUCT.VAT.eq(TAX.ID))
			.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
			.where(ITEM_PROPERTIES.getConditions(filter));
	}
	
	private static SelectConditionStep<Record> selectFull(AONContext ctx, ItemFilter filter) {
		return ctx.getDslContext().select()
			.from(ITEM)
			.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.leftOuterJoin(TAX).on(PRODUCT.VAT.eq(TAX.ID))
			.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
			.leftOuterJoin(ITEM_COMPOSITION).on(ITEM.ID.equal(ITEM_COMPOSITION.ITEM))
			.leftOuterJoin(COMPOSITION_ALIAS).on(ITEM_COMPOSITION.COMPOSITION_ITEM.eq(COMPOSITION_ALIAS.ID))
			.where(ITEM_PROPERTIES.getConditions(filter));
	}
	
	public static Item get(AONContext ctx, Integer id){
        return get(ctx, f -> f.getIdProperty().eq(id));
    }
	
	public static Item get(AONContext ctx, ItemFilter filter, Options... options){
		if(options.length > 0 && options[0].isFull())
			return getFull(ctx, filter);
		return select(ctx, filter).limit(1).fetch().stream().map(new ItemFiller())
				.findFirst().orElse(new Item());
	}
	
	public static Item getFull(AONContext ctx, ItemFilter filter){
		return getFullStream(ctx, filter).findFirst().orElse(new Item());
	}
	
	public static Stream<Item> getFullStream(AONContext ctx, ItemFilter filter){
		Map<Item, List<ItemComposition>> map =  selectFull(ctx, filter)
				.groupBy(ITEM.ID, ITEM_COMPOSITION.ID)
				.fetchGroups(new ItemFiller()::apply, new ItemCompositionFiller()::apply);
		map.forEach((object, composition) -> composition.forEach(c -> object.addItemComposition(c)));
		return map.keySet().stream(); 
	}
	
	public static Stream<Item> getStream(AONContext ctx, ItemFilter filter){
		return getStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	public static Stream<Item> getStream(AONContext ctx, ItemFilter filter, Integer page, Integer perPage){
		return getStream(ctx, filter, Optional.ofNullable(page), Optional.ofNullable(perPage));
	}
	
	public static Stream<Item> getRItemStream(AONContext ctx, ItemFilter filter){
		ctx.checkRead();
		return ctx.getDslContext()
		.select()
		.from(ITEM)
		.join(RITEM).on(RITEM.ITEM.eq(ITEM.ID))
		.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
		.leftOuterJoin(PCATEGORY).on(PCATEGORY.ID.eq(PRODUCT.CATEGORY))
		.leftOuterJoin(TAX).on(PRODUCT.VAT.eq(TAX.ID))
		.where(ITEM_PROPERTIES.getConditions(filter))
	    .fetch().stream().map(new ItemFiller());

	}
	
	public static Stream<RitemRecord> getRItemRecordStream(AONContext ctx, RegistryItemFilter filter){
		ctx.checkRead();
		return ctx.getDslContext().select().from(RITEM).where(RITEM_PROPERTIES.getConditions(filter))
				.fetchStreamInto(RITEM);
	}
	
	
	private static Stream<Item> getStream(AONContext ctx, ItemFilter filter, Optional<Integer> page, Optional<Integer> perPage){
		ctx.checkRead();
		SelectConditionStep<Record> query = ctx.getDslContext()
			.select()
			.from(ITEM)
			.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.leftOuterJoin(TAX).on(PRODUCT.VAT.eq(TAX.ID))
			.where(ITEM_PROPERTIES.getConditions(filter));

		if(page.isPresent() && perPage.isPresent()) {
			Integer per = perPage.get();
			Integer p = page.get();
			query.limit(per).offset(per * (p -1));
		}
			
		return query.fetch().stream().map(new ItemFiller());
	}
	
	
	public static LinkedList<Item> getList(AONContext ctx, ItemFilter filter) {
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<Item> getFullList(AONContext ctx, ItemFilter filter) {
		LinkedList<Item> list = getList(ctx, filter);
		for (int i = 0; i < list.size(); i++) {
			Integer id = list.get(i).getId();
			list.get(i).setItemComposition(ItemCompositionDAO.getList(ctx, f -> f.getItemProperty().eq(id)));
		}
		return list;
	}
	
	public static Item save(AONContext ctx, Item item) {
		if(item.getProduct().getId() == null) {
			ProductDAO.save(ctx, item.getProduct());
		}
		
		ctx.checkWrite();
		ItemAutoComplete.autoComplete(ctx, item);
		return item.getId() != null 
			? update(ctx, item)
			: insert(ctx, item);		
	}
	
	public static RegistryItem[] saveRItem(AONContext ctx, RegistryItem ...ritems) {
		ctx.checkWrite();
		if ( ritems == null )
			return new RegistryItem[0];
		if ( ritems.length == 0 )
			return new RegistryItem[0];
		
		List<RegistryItem> list = Arrays.asList(ritems);

		return setRItem(ctx, list.stream().filter(r-> !r.isRemoved()).toArray(RegistryItem[]::new) );
	}
	
	

	public static Item insert(AONContext ctx, Item item) {
		Timestamp now = new Timestamp(new java.util.Date().getTime());
		ctx.checkWrite();

		Integer id = ctx.getDslContext().insertInto(ITEM)
		.set(ITEM.DOMAIN, item.getDomain().getId())
		.set(ITEM.PRODUCT, item.getProduct().getId())
		.set(ITEM.DETAIL, item.getDetail())
		.set(ITEM.DETAIL2, item.getDetail2())
		.set(ITEM.DETAIL3, item.getDetail3())
		.set(ITEM.DESCRIPTION, item.getDescription())
		.set(ITEM.SERIAL_NUMBER, item.getSerialNumber())
		.set(ITEM.SERIAL_DATE, item.getSerialDate() != null 
				? new Date(item.getSerialDate().getTime()) : null)
		.set(ITEM.PRICE, item.getPrice())
		.set(ITEM.STATUS, item.getStatus() != null ? item.getStatus().value() : ProductStatus.ACTIVE.value())
		.set(ITEM.EXPENSES_PERCENT, item.getExpensesPercent())
		.set(ITEM.EXPENSES_FIXED, item.getExpensesFixed())
		.set(ITEM.PROFIT_PERCENT, item.getProfitPercent())
		.set(ITEM.PURCHASE_PRICE, item.getPurchasePrice())
		.set(ITEM.INTERNET, item.isInternet() ? (byte) 1: 0)
		.set(ITEM.BARCODE, item.getBarcode())

		.set(ITEM.PACK_FORMAT_TAG, item.getPackFormatTag() != null ? item.getPackFormatTag().getId() : null)
		.set(ITEM.PACK_UNITS, item.getPackUnits()!= null ? item.getPackUnits() : 0)
		.set(ITEM.PACK_UNITS_TAG, item.getPackUnitsTag() != null ? item.getPackUnitsTag().getId() : null)
		.set(ITEM.PACK_MEASUREMENT, item.getPackMeasurement())
		.set(ITEM.PACK_MEASUREMENT_TAG, item.getPackMeasurementTag() != null ? item.getPackMeasurementTag().getId() : null)
		.set(ITEM.STOCK_UNIT_TAG, item.getStockUnitTag() != null ? item.getStockUnitTag().getId() : null)
		
		.set(ITEM.CREATION_USER, ctx.getUser())
		.set(ITEM.CREATION_DATE, now)
		.set(ITEM.MODIFICATION_USER, ctx.getUser())
		.set(ITEM.MODIFICATION_DATE, now)
		.returning(ITEM.ID).fetchOne().getValue(ITEM.ID);
		
		return item.setId(id);
	}
	
	public static Item update(AONContext ctx, Item item) {
		Timestamp now = new Timestamp(new java.util.Date().getTime());
		ctx.checkWrite();

		ctx.getDslContext().update(ITEM)
		.set(ITEM.DOMAIN, item.getDomain().getId())
		.set(ITEM.PRODUCT, item.getProduct().getId())
		.set(ITEM.DETAIL, item.getDetail())
		.set(ITEM.DETAIL2, item.getDetail2())
		.set(ITEM.DETAIL3, item.getDetail3())
		.set(ITEM.DESCRIPTION, item.getDescription())
		.set(ITEM.SERIAL_NUMBER, item.getSerialNumber())
		.set(ITEM.SERIAL_DATE, item.getSerialDate() != null ? new Date(item.getSerialDate().getTime()) : null)
		.set(ITEM.PRICE, item.getPrice())
		.set(ITEM.STATUS, item.getStatus() != null ? item.getStatus().value() : null)
		.set(ITEM.EXPENSES_PERCENT, item.getExpensesPercent())
		.set(ITEM.EXPENSES_FIXED, item.getExpensesFixed())
		.set(ITEM.PROFIT_PERCENT, item.getProfitPercent())
		.set(ITEM.PURCHASE_PRICE, item.getPurchasePrice())
		.set(ITEM.INTERNET, item.isInternet() ? (byte) 1: 0)
		.set(ITEM.BARCODE, item.getBarcode())

		.set(ITEM.PACK_FORMAT_TAG, item.getPackFormatTag().getId())
		.set(ITEM.PACK_UNITS, item.getPackUnits()!= null ? item.getPackUnits() : 0)
		.set(ITEM.PACK_UNITS_TAG, item.getPackUnitsTag().getId())
		.set(ITEM.PACK_MEASUREMENT, item.getPackMeasurement())
		.set(ITEM.PACK_MEASUREMENT_TAG, item.getPackMeasurementTag().getId())
		.set(ITEM.STOCK_UNIT_TAG, item.getStockUnitTag().getId())
		
		.set(ITEM.MODIFICATION_USER, ctx.getUser())
		.set(ITEM.MODIFICATION_DATE, now)
		.where(ITEM.ID.eq(item.getId()))
		.execute();
		return item;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(ITEM)
			.where(ITEM.ID.equal(id))
			.execute();	
	}

	public static void deleteRItem(AONContext ctx, RegistryItemFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(RITEM)
			.where(RITEM_PROPERTIES.getConditions(filter))
			.execute();	
	}

	public static void updateRItemStatus(AONContext ctx, RegistryItemStatus status, RegistryItemFilter filter) {
		if (status != null) {			
			ctx.getDslContext()
			.update(RITEM)
			.set(RITEM.STATUS, status.value())
			.where(RITEM_PROPERTIES.getConditions(filter))
			.execute();
		}
	}
	
	public static void updateRItemQuantity(AONContext ctx, String quantity, RegistryItemFilter filter) {
		if (AonStringUtils.isNotBlank(quantity)) {			
			ctx.getDslContext()
			.update(RITEM)
			.set(RITEM.QUANTITY, quantity)
			.where(RITEM_PROPERTIES.getConditions(filter))
			.execute();
		}
	}

	private static RegistryItem[] setRItem(AONContext ctx, RegistryItem ...ritems) {
		ctx.checkWrite();

		InsertSetMoreStep<RitemRecord> insertRItem = null;
		
		for (RegistryItem ritem : ritems) {

			Optional<RitemRecord> opt = getRItemRecordStream(ctx, 
					f-> f.getDomainProperty().eq(ritem.getDomain())
					.and(f.getRegistryProperty().eq(ritem.getRegistry()))
					.and(f.getItemProperty().eq(ritem.getItem() != null ? ritem.getItem().getId() : null)
					.and(f.getTypeProperty().eq(ritem.getType() != null ? ritem.getType().value() : null)))
					.and(f.getEdiSalesCodeProperty().eq(ritem.getEdiSalesCode()))
			).findFirst();
			
			if(opt.isPresent()) { 		//------------------UPDATE ----------
				RitemRecord ritemRecord = opt.get();
				ritem.setId(ritemRecord.getId());
				
				ritemRecord.setQuantity(ritem.getQuantity());
				ritemRecord.update();
				
//				if(ritem.getPrice()!=null) {
//					ritemRecord.set(RITEM.PRICE, ritem.getPrice());
//				}
//				
//				ritemRecord.update();
			} else {
				InsertSetStep<RitemRecord> insert = null != insertRItem ? insertRItem.newRecord() : ctx.getDslContext().insertInto(RITEM);
						
				InsertSetMoreStep<RitemRecord> recordSets = insert
				.set(RITEM.DOMAIN, ritem.getDomain())
				.set(RITEM.REGISTRY, ritem.getRegistry())
				.set(RITEM.ITEM, ritem.getItem().getId())
				.set(RITEM.TYPE, ritem.getType().value())
				.set(RITEM.EDI_SALES_CODE, ritem.getEdiSalesCode())
				.set(RITEM.CUSTOMER_FEE, ritem.getCustomerFee())
				.set(RITEM.STATUS, ritem.getStatus().value())
				.set(RITEM.PRIORITY, ritem.getPriority().value())
				.set(RITEM.QUANTITY, ritem.getQuantity())
				.set(RITEM.START_DATE, AonDateUtils.toSql(ritem.getStartDate()))
				.set(RITEM.END_DATE, AonDateUtils.toSql(ritem.getEndDate()))
				.set(RITEM.CREATION_DATE, AonDateUtils.toTimestamp(ritem.getCreationDate()))
				.set(RITEM.CREATION_USER, ritem.getCreationUser())
				.set(RITEM.MODIFICATION_DATE, AonDateUtils.toTimestamp(ritem.getModificationDate()))
				.set(RITEM.MODIFICATION_USER, ritem.getModificationUser())
				;
				
				if(ritem.getPrice()!=null) {
					recordSets.set(RITEM.PRICE, ritem.getPrice());
				}
				
				if(ritem.getCode()!=null) {
					recordSets.set(RITEM.CODE, ritem.getCode());
				}
				
				if(ritem.getWorkplace()!=null) {
					recordSets.set(RITEM.WORKPLACE, ritem.getWorkplace());
				}
				
				if(ritem.getCustomerFee() != null) {
					recordSets.set(RITEM.CUSTOMER_FEE, ritem.getCustomerFee());
				}
				
				if(ritem.getSeller() != null) {
					recordSets.set(RITEM.SELLER, ritem.getSeller().getId());
				}
				
				insertRItem = recordSets;
			}
		}
		
		if(null!=insertRItem) {
			return insertRItem.returning().fetchStreamInto(RITEM).map(r -> new RegistryItem()
					.setId(r.getId())
					.setDomain(r.getDomain())
					.setItem(ItemFiller.build(r))
					.setType(r.getType() != null ? RegistryMode.values()[r.getType()] : null)
					.setStatus(r.getStatus() != null ? RegistryItemStatus.values()[r.getStatus()] : null)
					.setPriority(r.getPriority() != null ? Priority.values()[r.getPriority()] : null)
					.setPrice(r.getPrice())
					.setCode(r.getCode())
					.setEdiSalesCode(r.getEdiSalesCode())
					.setWorkplace(r.getWorkplace())
					.setRegistry(r.getRegistry())
					.setDiscountExpr(r.getDiscountExpr())
					.setQuantity(r.getQuantity())
					.setStartDate(r.getStartDate())
					.setEndDate(r.getEndDate())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
			).toArray(RegistryItem[]::new);
			
		}
		
		return ritems;
	}
	

	public static class ItemFiller extends Filler implements Function<Record, Item> {
		
		@Override
		public Item apply(Record r) {
			return build(r);
		}
		
		public static Item build(Record r) {
			return build(r, ITEM, PRODUCT);
		}
		
		public static Item build(Record r, com.esferalia.aon.jooq.tables.Item alias, com.esferalia.aon.jooq.tables.Product palias) {
			return new Item()
				.setId(getValue(r, alias.ID))
				.setDomain(new Domain().setId(getValue(r, alias.DOMAIN)))
				.setProduct(checkField(r, palias.ID)
						? ProductFiller.build(r, palias)
						: new Product().setId(getValue(r, alias.PRODUCT)))
				.setDetail(getValue(r, alias.DETAIL))
				.setDetail2(getValue(r, alias.DETAIL2))
				.setDetail3(getValue(r, alias.DETAIL3))
				.setDescription(getValue(r, alias.DESCRIPTION))
				.setSerialNumber(getValue(r, alias.SERIAL_NUMBER))
				.setSerialDate(getValue(r, alias.SERIAL_DATE))
				.setExpireDate(getValue(r, alias.EXPIRE_DATE))
				.setPrice(getDouble(r, alias.PRICE))
				.setStatus(ProductStatus.safeValueOf(getValue(r, alias.STATUS)))
				.setExpensesPercent(getDouble(r, alias.EXPENSES_PERCENT))
				.setExpensesFixed(getDouble(r, alias.EXPENSES_FIXED))
				.setProfitPercent(getDouble(r, alias.PROFIT_PERCENT))
				.setPurchasePrice(getDouble(r, alias.PURCHASE_PRICE))				
				.setInternet(getBoolean(r, alias.INTERNET))
				.setBarcode(getValue(r, alias.BARCODE))
				.setPackFormatTag(new Tag().setId(getValue(r, alias.PACK_FORMAT_TAG)))
				.setPackUnits(getInteger(r, alias.PACK_UNITS))
				.setPackUnitsTag(new Tag().setId(getValue(r, alias.PACK_UNITS_TAG)))
				.setPackMeasurement(getDouble(r, alias.PACK_MEASUREMENT))
				.setPackMeasurementTag(new Tag().setId(getValue(r, alias.PACK_MEASUREMENT_TAG)))
				.setStockUnitTag(new Tag().setId(getValue(r, alias.STOCK_UNIT_TAG)))
				.setCreationUser(getValue(r, alias.CREATION_USER))
				.setCreationDate(getValue(r, alias.CREATION_DATE))
				.setModificationUser(getValue(r, alias.MODIFICATION_USER))
				.setModificationDate(getValue(r, alias.MODIFICATION_DATE));
		}
	}
}
