package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO.ProductFiller;


public class ItemDAO {
	
	private static final ItemPropertiesDAO ITEM_PROPERTIES = new ItemPropertiesDAO();

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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.DOMAIN);}
		@Override public Property<Integer> getProductProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.PRODUCT);}
		@Override public Property<String> getDetailProperty() {return new FilterDAO.PropertyDAO<String>(ITEM.DETAIL);}
		@Override public Property<String> getDetail2Property() {return new FilterDAO.PropertyDAO<String>(ITEM.DETAIL2);}
		@Override public Property<String> getDetail3Property() {return new FilterDAO.PropertyDAO<String>(ITEM.DETAIL3);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(ITEM.DESCRIPTION);}
		@Override public Property<String> getSerialNumberProperty() {return new FilterDAO.PropertyDAO<String>(ITEM.SERIAL_NUMBER);}
		@Override public Property<Date> getSerialDateProperty() {return new FilterDAO.PropertyDAO<Date>(ITEM.SERIAL_DATE);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<Double>(ITEM.PRICE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(ITEM.STATUS);}
		@Override public Property<Double> getExpensesPercentProperty() {return new FilterDAO.PropertyDAO<Double>(ITEM.EXPENSES_PERCENT);}
		@Override public Property<Double> getExpensesFixedProperty() {return new FilterDAO.PropertyDAO<Double>(ITEM.EXPENSES_FIXED);}
		@Override public Property<Double> getProfitPercentProperty() {return new FilterDAO.PropertyDAO<Double>(ITEM.PROFIT_PERCENT);}
		@Override public Property<Double> getPurchasePriceProperty() {return new FilterDAO.PropertyDAO<Double>(ITEM.PURCHASE_PRICE);}
		@Override public Property<Byte> getInternetProperty() {return new FilterDAO.PropertyDAO<Byte>(ITEM.INTERNET);}
		@Override public Property<String> getBarcodeProperty() {return new FilterDAO.PropertyDAO<String>(ITEM.BARCODE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(ITEM.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ITEM.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(ITEM.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(ITEM.MODIFICATION_DATE);}
		
		@Override public Property<Integer> getPackFormatTagProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.PACK_FORMAT_TAG);}
		@Override public Property<Integer> getPackUnitsProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.PACK_UNITS);}
		@Override public Property<Integer> getPackUnitsTagProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.PACK_UNITS_TAG);}
		@Override public Property<Double> getPackMeasurementProperty() {return new FilterDAO.PropertyDAO<Double>(ITEM.PACK_MEASUREMENT);}
		@Override public Property<Integer> getPackMeasurementTagProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.PACK_MEASUREMENT_TAG);}
		@Override public Property<Integer> getStockUnitTagProperty() {return new FilterDAO.PropertyDAO<Integer>(ITEM.STOCK_UNIT_TAG);}
		
		@Override public Property<String> getProductCodeProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.CODE);}
		@Override public Property<String> getProductNameProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.NAME);}

	}

	public static Item get(AONContext ctx, ItemFilter filter){
		return ctx.getDslContext()
			.select()
			.from(ITEM)
			.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.where(ITEM_PROPERTIES.getConditions(filter))
			.limit(1)
			.fetch().stream().map(new ItemFiller()).findFirst().orElse(new Item());
	}
	
	public static Stream<Item> getStream(AONContext ctx, ItemFilter filter){
		return ctx.getDslContext()
			.select()
			.from(ITEM)
			.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
			.leftOuterJoin(TAX).on(PRODUCT.VAT.eq(TAX.ID))
			.where(ITEM_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new ItemFiller());
	}
	
	public static Item save(AONContext ctx, Item item) {
		if(item.getProduct().getId() == null) {
			ProductDAO.save(ctx, item.getProduct());
		}

		ctx.checkWrite();
		return item.getId() != null 
			? update(ctx, item)
			: insert(ctx, item);		
	}

	public static Item insert(AONContext ctx, Item item) {
		Timestamp now = new Timestamp(new java.util.Date().getTime());
		ctx.checkWrite();

		//ProductValidation.validateItem(ctx, item);

		Integer id = ctx.getDslContext().insertInto(ITEM)
		.set(ITEM.DOMAIN, item.getDomain().getId())
		.set(ITEM.PRODUCT, item.getProduct().getId())
		.set(ITEM.DETAIL, item.getDetail())
		.set(ITEM.DETAIL2, item.getDetail2())
		.set(ITEM.DETAIL3, item.getDetail3())
		.set(ITEM.DESCRIPTION, item.getDescription())
		.set(ITEM.SERIAL_NUMBER, item.getSerialNumber())
		.set(ITEM.SERIAL_DATE, new Date(item.getSerialDate().getTime()))
		.set(ITEM.PRICE, item.getPrice())
		.set(ITEM.STATUS, item.getStatus().value())
		.set(ITEM.EXPENSES_PERCENT, item.getExpensesPercent())
		.set(ITEM.EXPENSES_FIXED, item.getExpensesFixed())
		.set(ITEM.PROFIT_PERCENT, item.getProfitPercent())
		.set(ITEM.PURCHASE_PRICE, item.getPurchasePrice())
		.set(ITEM.INTERNET, item.isInternet() ? (byte) 1: 0)
		.set(ITEM.BARCODE, item.getBarcode())

		.set(ITEM.PACK_FORMAT_TAG, item.getPackFormatTag().getId())
		.set(ITEM.PACK_UNITS, item.getPackUnits())
		.set(ITEM.PACK_UNITS_TAG, item.getPackUnitsTag().getId())
		.set(ITEM.PACK_MEASUREMENT, item.getPackMeasurement())
		.set(ITEM.PACK_MEASUREMENT_TAG, item.getPackMeasurementTag().getId())
		.set(ITEM.STOCK_UNIT_TAG, item.getStockUnitTag().getId())
		
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

		//ProductValidation.validateItem(ctx, item);

		ctx.getDslContext().update(ITEM)
		.set(ITEM.DOMAIN, item.getDomain().getId())
		.set(ITEM.PRODUCT, item.getProduct().getId())
		.set(ITEM.DETAIL, item.getDetail())
		.set(ITEM.DETAIL2, item.getDetail2())
		.set(ITEM.DETAIL3, item.getDetail3())
		.set(ITEM.DESCRIPTION, item.getDescription())
		.set(ITEM.SERIAL_NUMBER, item.getSerialNumber())
		.set(ITEM.SERIAL_DATE, new Date(item.getSerialDate().getTime()))
		.set(ITEM.PRICE, item.getPrice())
		.set(ITEM.STATUS, item.getStatus().value())
		.set(ITEM.EXPENSES_PERCENT, item.getExpensesPercent())
		.set(ITEM.EXPENSES_FIXED, item.getExpensesFixed())
		.set(ITEM.PROFIT_PERCENT, item.getProfitPercent())
		.set(ITEM.PURCHASE_PRICE, item.getPurchasePrice())
		.set(ITEM.INTERNET, item.isInternet() ? (byte) 1: 0)
		.set(ITEM.BARCODE, item.getBarcode())

		.set(ITEM.PACK_FORMAT_TAG, item.getPackFormatTag().getId())
		.set(ITEM.PACK_UNITS, item.getPackUnits())
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

	private static class ItemFiller implements Function<Record, Item> {
		
		@Override
		public Item apply(Record r) {
			return build(r);
		}
		
		public Item build(Record r) {
			return new Item()
				.setId(r.getValue(ITEM.ID))
				.setDomain(new Domain().setId(r.getValue(ITEM.DOMAIN)))
				.setProduct(ProductFiller.buildProduct(r))
				.setBarcode(r.getValue(ITEM.BARCODE))
				.setDescription(r.getValue(ITEM.DESCRIPTION))
				.setDetail(r.getValue(ITEM.DETAIL))
				.setDetail2(r.getValue(ITEM.DETAIL2))
				.setDetail3(r.getValue(ITEM.DETAIL3))
				.setExpensesFixed(r.getValue(ITEM.EXPENSES_FIXED))
				.setExpensesPercent(r.getValue(ITEM.EXPENSES_PERCENT))
				.setInternet(r.getValue(ITEM.INTERNET) == 1)
				.setPackFormatTag(new Tag().setId(r.getValue(ITEM.PACK_FORMAT_TAG)))
				.setPackMeasurement(r.getValue(ITEM.PACK_MEASUREMENT))
				.setPackMeasurementTag(new Tag().setId(r.getValue(ITEM.PACK_MEASUREMENT_TAG)))
				.setPackUnits(r.getValue(ITEM.PACK_UNITS))
				.setPackUnitsTag(new Tag().setId(r.getValue(ITEM.PACK_UNITS_TAG)))
				.setPrice(r.getValue(ITEM.PRICE))
				.setCreationDate(r.getValue(PRODUCT.CREATION_DATE))
				.setCreationUser(r.getValue(PRODUCT.CREATION_USER))
				.setModificationDate(r.getValue(PRODUCT.MODIFICATION_DATE))
				.setModificationUser(r.getValue(PRODUCT.MODIFICATION_USER));
		}
	}
}
