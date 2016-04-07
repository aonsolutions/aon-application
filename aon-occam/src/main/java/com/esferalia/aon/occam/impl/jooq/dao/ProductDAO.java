package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.InsertValuesStep21;
import org.jooq.InsertValuesStep3;
import org.jooq.InsertValuesStepN;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record3;

import com.esferalia.aon.jooq.tables.records.BrandRecord;
import com.esferalia.aon.jooq.tables.records.ItemRecord;
import com.esferalia.aon.jooq.tables.records.PcategoryRecord;
import com.esferalia.aon.jooq.tables.records.ProductRecord;
import com.esferalia.aon.jooq.tables.records.ProductTagRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.BrandFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.BrandProperties;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductCategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.impl.jooq.validation.ProductValidation;


public class ProductDAO {
	
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
	private static final ItemPropertiesDAO ITEM_PROPERTIES = new ItemPropertiesDAO();
	private static final BrandPropertiesDAO BRAND_PROPERTIES = new BrandPropertiesDAO();
	private static final ProductCategoryPropertiesDAO PRODUCT_CATEGORY_PROPERTIES = new ProductCategoryPropertiesDAO();

	protected static class ProductPropertiesDAO implements ProductProperties {
		protected Condition[] getConditions(ProductFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.NAME);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.CODE);}
		@Override public Property<Byte> getKindProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.KIND);}
		@Override public Property<Integer> getBrandProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.BRAND);}
		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.CATEGORY);}
		@Override public Property<Byte> getInventoriableProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.INVENTORIABLE);}
		@Override public Property<Byte> getSerializableProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.SERIALIZABLE);}
		@Override public Property<Byte> getLotableProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.LOTABLE);}
		@Override public Property<Byte> getStatusProperty() { return new FilterDAO.PropertyDAO<Byte>(PRODUCT.STATUS);}
		@Override public Property<Integer> getVatProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.VAT);}
		@Override public Property<Integer> getRetentionProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.RETENTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.TYPE);}
		@Override public Property<Byte> getManufacturedProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.MANUFACTURED);}
		@Override public Property<Byte> getCompositionProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.COMPOSITION);}
		@Override public Property<Byte> getCompositionPriceProperty() {return new FilterDAO.PropertyDAO<Byte>(PRODUCT.COMPOSITION_PRICE);}
		@Override public Property<Integer> getSalesAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.SALES_ACCOUNT);}
		@Override public Property<Integer> getPurchaseAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(PRODUCT.PURCHASE_ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PRODUCT.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(PRODUCT.NAME);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(PRODUCT.MODIFICATION_DATE);}
	}
	
	protected static class ItemPropertiesDAO implements ItemProperties {
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
		
	}
	
	protected static class BrandPropertiesDAO implements BrandProperties {
		protected Condition[] getConditions(BrandFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(BRAND.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(BRAND.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(BRAND.NAME);}
	}
	
	protected static class ProductCategoryPropertiesDAO implements ProductCategoryProperties {
		protected Condition[] getConditions(ProductCategoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PCATEGORY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PCATEGORY.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(PCATEGORY.NAME);}
		@Override public Property<String> getDetailProperty() {return new FilterDAO.PropertyDAO<String>(PCATEGORY.DETAIL);}
		@Override public Property<String> getDetail2Property() {return new FilterDAO.PropertyDAO<String>(PCATEGORY.DETAIL2);}
		@Override public Property<String> getDetail3Property() {return new FilterDAO.PropertyDAO<String>(PCATEGORY.DETAIL3);}
	}
	
	public static LinkedList<ProductCategory> getProductCategories(AONContext ctx) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(PCATEGORY.ID,PCATEGORY.DOMAIN,PCATEGORY.NAME)
			.from(PCATEGORY)
			.where(DAOUtilities.getHeritableDomainCondition(ctx, PCATEGORY.DOMAIN,ctx.getDomainId()))
			.orderBy(PCATEGORY.NAME)
			.fetch()
			.stream()
			.map( record -> new ProductCategory()
					.setId( record.getValue(PCATEGORY.ID))
					.setDomain( record.getValue(PCATEGORY.DOMAIN))
					.setName( record.getValue(PCATEGORY.NAME))
				)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static LinkedList<String> getProductTags(AONContext ctx) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(TAG.NAME)
			.from(TAG)
			.where(TAG.DOMAIN.equal(ctx.getDomainId()))
			.and(TAG.TYPE.eq( (byte) 1 ))
			.fetch()
			.stream()
			.map( record -> record.getValue(TAG.NAME) )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static LinkedHashMap<Integer,String[]> getProductTagMap(AONContext ctx) {
		ctx.checkRead();
		final LinkedHashMap<Integer,String[]> map = new LinkedHashMap<Integer, String[]>();
		ctx.getDslContext()
			.select(PRODUCT.ID,TAG.NAME)
			.from(PRODUCT)
			.join(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.equal(PRODUCT.ID))
			.join(TAG).on(TAG.ID.equal(PRODUCT_TAG.TAG))
			.where(PRODUCT.DOMAIN.equal(ctx.getDomainId()))
			.and(TAG.TYPE.eq( (byte) 1 ))
			.fetch()
			.stream()
			.forEach( record -> {
				Integer key = record.getValue(PRODUCT.ID); 
				String tag = record.getValue(TAG.NAME);
				String[] tags = map.get(key);
				if (tags == null) {
					tags = new String[1];
					tags[0] = tag;
					map.put(key, tags);
				} else {
					int length = tags.length;
					tags = Arrays.copyOf(tags, length + 1);
					tags[length] = tag;
				}
			});
		return map; 
	}
	
	// ------------------------------------- PRODUCT
	
	public static Product getProduct(AONContext ctx, Integer id){
		return ctx.getDslContext().select().from(PRODUCT).where(PRODUCT.ID.eq(id)).limit(1).fetchInto(PRODUCT)
			.stream().map(new FullProductFiller()).findFirst().orElse(new Product());
	}
	
	public static Product getProduct(AONContext ctx, ProductFilter filter){
		return ctx.getDslContext().select().from(PRODUCT).where(PRODUCT_PROPERTIES.getConditions(filter)).limit(1)
				.fetchInto(PRODUCT).stream().map(new FullProductFiller()).findFirst().orElse(new Product());
	}
	
	public static Stream<Product> getProductStream(AONContext ctx, ProductFilter filter){
		return ctx.getDslContext().select().from(PRODUCT).where(PRODUCT_PROPERTIES.getConditions(filter))
				.fetchInto(PRODUCT).stream().map(new FullProductFiller());
	}
	
	public static LinkedList<Product> getProductList(AONContext ctx, ProductFilter filter){
		return getProductStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Deprecated
	public static Product getProduct2(AONContext ctx, Integer id){
		ctx.checkRead();
		Record21<Integer, String, String, Integer, Integer, Byte, Byte, Byte, Byte, Integer, Integer, Byte, Byte, Byte, Byte, Integer, Integer, String, Timestamp, String, Timestamp> record = ctx.getDslContext()
				.select(PRODUCT.DOMAIN, PRODUCT.NAME, PRODUCT.CODE, PRODUCT.BRAND,
						PRODUCT.CATEGORY, PRODUCT.INVENTORIABLE, PRODUCT.SERIALIZABLE,
						PRODUCT.LOTABLE, PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION,
						PRODUCT.TYPE, PRODUCT.MANUFACTURED, PRODUCT.COMPOSITION,
						PRODUCT.COMPOSITION_PRICE, PRODUCT.SALES_ACCOUNT,
						PRODUCT.PURCHASE_ACCOUNT, PRODUCT.CREATION_USER,
						PRODUCT.CREATION_DATE, PRODUCT.MODIFICATION_USER,PRODUCT.MODIFICATION_DATE)
			.from(PRODUCT)
			.where(PRODUCT.ID.eq(id))
			.fetchOne();
		if(record != null){
			Product p = new Product();
			p.setId(id);
			if(record.value1() != null) p.setDomain(record.value1());
			if(record.value2() != null) p.setName(record.value2());
			if(record.value3() != null) p.setCode(record.value3());
			if(record.value4() != null) p.setBrand(record.value4());
			if(record.value5() != null) p.setCategory(record.value5());
			if(record.value6() != null) p.setInventoriable(record.value6());
			if(record.value7() != null) p.setSerializable(record.value7());
			if(record.value8() != null) p.setLotable(record.value8());
			if(record.value9() != null) p.setStatus(record.value9());
			if(record.value10() != null) p.setVat(record.value10());
			if(record.value11() != null) p.setRetention(record.value11());
			if(record.value12() != null) p.setType(record.value12());
			if(record.value13() != null) p.setManufactured(record.value13());
			if(record.value14() != null) p.setComposition(record.value14());
			if(record.value15() != null) p.setCompositionPrice(record.value15());
			if(record.value16() != null) p.setSalesAccount(record.value16());
			if(record.value17() != null) p.setPurchaseAccount(record.value17());
			if(record.value18() != null) p.setCreationUser(record.value18());
			if(record.value19() != null) p.setCreationDate(record.value19());
			if(record.value20() != null) p.setModificationUser(record.value20());
			if(record.value21() != null) p.setModificationDate(record.value21());
			
			return p;
		}
		
		return null;
	}
	
	public static void insert(AONContext ctx, Product p) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ProductValidation.validate(ctx, p);
			Timestamp creationDate = null, modificationDate = null;
			if(p.getCreationDate() != null)
				creationDate = new java.sql.Timestamp(p.getCreationDate().getTime());
			if(p.getModificationDate() != null)
				modificationDate = new java.sql.Timestamp(p.getModificationDate().getTime());
			
			ctx.getDslContext()
				.insertInto(PRODUCT, PRODUCT.DOMAIN, PRODUCT.NAME, PRODUCT.CODE, PRODUCT.BRAND, PRODUCT.CATEGORY, PRODUCT.INVENTORIABLE,
						PRODUCT.SERIALIZABLE, PRODUCT.LOTABLE, PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION, PRODUCT.TYPE,
						PRODUCT.MANUFACTURED, PRODUCT.COMPOSITION, PRODUCT.COMPOSITION_PRICE, PRODUCT.SALES_ACCOUNT,
						PRODUCT.PURCHASE_ACCOUNT, PRODUCT.CREATION_USER, PRODUCT.CREATION_DATE, PRODUCT.MODIFICATION_USER,
						PRODUCT.MODIFICATION_DATE, PRODUCT.KIND, PRODUCT.PACKAGED)
				.values(p.getDomain(), p.getName(), p.getCode(), p.getBrand(), p.getCategory(), p.getInventoriable(), p.getSerializable(),
						p.getLotable(), p.getStatus(), p.getVat(), p.getRetention(), p.getType(), p.getManufactured(),p.getComposition(),
						p.getCompositionPrice(), p.getSalesAccount(), p.getPurchaseAccount(), p.getCreationUser(), creationDate,
						p.getModificationUser(), modificationDate, p.getKind() != null ? p.getKind() : 0, p.getPackagedValue())
				.execute();
		});
	}
	
	public static void insertWithId(AONContext ctx, Product p) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ProductValidation.validate(ctx, p);
			Timestamp creationDate = null, modificationDate = null;
			if(p.getCreationDate() != null)
				creationDate = new java.sql.Timestamp(p.getCreationDate().getTime());
			if(p.getModificationDate() != null)
				modificationDate = new java.sql.Timestamp(p.getModificationDate().getTime());
			
			ctx.getDslContext()
				.insertInto(PRODUCT,PRODUCT.ID, PRODUCT.DOMAIN, PRODUCT.NAME, PRODUCT.CODE, PRODUCT.BRAND, PRODUCT.CATEGORY,
						PRODUCT.INVENTORIABLE, PRODUCT.SERIALIZABLE, PRODUCT.LOTABLE, PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION,
						PRODUCT.TYPE, PRODUCT.MANUFACTURED, PRODUCT.COMPOSITION, PRODUCT.COMPOSITION_PRICE, PRODUCT.SALES_ACCOUNT,
						PRODUCT.PURCHASE_ACCOUNT, PRODUCT.CREATION_USER, PRODUCT.CREATION_DATE, PRODUCT.MODIFICATION_USER,
						PRODUCT.MODIFICATION_DATE, PRODUCT.KIND, PRODUCT.PACKAGED)
					.values(p.getId(), p.getDomain(), p.getName(), p.getCode(), p.getBrand(), p.getCategory(), p.getInventoriable(),
							p.getSerializable(), p.getLotable(), p.getStatus(), p.getVat(), p.getRetention(), p.getType(),
							p.getManufactured(),p.getComposition(), p.getCompositionPrice(), p.getSalesAccount(), p.getPurchaseAccount(),
							p.getCreationUser(), creationDate, p.getModificationUser(), modificationDate,
							p.getKind() != null ? p.getKind() : 0, p.getPackagedValue())
					.execute();
		});
	}
	public static LinkedList<Product> insert(AONContext ctx, Stream<Product> ps) {
		ctx.checkWrite();
		AONContext sctx = ctx;
		InsertValuesStepN<ProductRecord> insertQuery = ctx.getDslContext().insertInto(PRODUCT, PRODUCT.DOMAIN, PRODUCT.NAME,
				PRODUCT.CODE, PRODUCT.BRAND, PRODUCT.CATEGORY, PRODUCT.INVENTORIABLE, PRODUCT.SERIALIZABLE, PRODUCT.LOTABLE,
				PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION, PRODUCT.TYPE, PRODUCT.MANUFACTURED, PRODUCT.COMPOSITION,
				PRODUCT.COMPOSITION_PRICE, PRODUCT.SALES_ACCOUNT, PRODUCT.PURCHASE_ACCOUNT, PRODUCT.CREATION_USER, PRODUCT.CREATION_DATE,
				PRODUCT.MODIFICATION_USER, PRODUCT.MODIFICATION_DATE, PRODUCT.KIND, PRODUCT.PACKAGED);
		AONContext sctx2 = sctx;
		ps.forEach(p ->{
			ProductValidation.validate(sctx2, p);
			Timestamp creationDate = null, modificationDate = null;
			if(p.getCreationDate() != null)
				creationDate = new java.sql.Timestamp(p.getCreationDate().getTime());
			if(p.getModificationDate() != null)
				modificationDate = new java.sql.Timestamp(p.getModificationDate().getTime());
				
			insertQuery.values(p.getDomain(), p.getName(), p.getCode(), p.getBrand(), p.getCategory(), p.getInventoriable(), p.getSerializable(),
					p.getLotable(), p.getStatus(), p.getVat(), p.getRetention(), p.getType(), p.getManufactured(),p.getComposition(),
					p.getCompositionPrice(), p.getSalesAccount(), p.getPurchaseAccount(), p.getCreationUser(), creationDate, p.getModificationUser(),
					modificationDate, p.getKind() != null ? p.getKind() : 0, p.getPackagedValue());
		});
		return insertQuery.returning().fetch().stream().map(new ImportProductFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	public static void insertWithId(AONContext ctx, Stream<Product> ps) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStepN<ProductRecord> productQuery = ctx.getDslContext().insertInto(PRODUCT,PRODUCT.ID, PRODUCT.DOMAIN,
					PRODUCT.NAME, PRODUCT.CODE, PRODUCT.BRAND, PRODUCT.CATEGORY, PRODUCT.INVENTORIABLE, PRODUCT.SERIALIZABLE,
					PRODUCT.LOTABLE, PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION, PRODUCT.TYPE, PRODUCT.MANUFACTURED,
					PRODUCT.COMPOSITION, PRODUCT.COMPOSITION_PRICE, PRODUCT.SALES_ACCOUNT, PRODUCT.PURCHASE_ACCOUNT,
					PRODUCT.CREATION_USER, PRODUCT.CREATION_DATE, PRODUCT.MODIFICATION_USER, PRODUCT.MODIFICATION_DATE,
					PRODUCT.PACKAGED, PRODUCT.KIND);
			ps.forEach(p ->{
				ProductValidation.validate(ctx, p);
				Timestamp creationDate = null, modificationDate = null;
				if(p.getCreationDate() != null)
					creationDate = new java.sql.Timestamp(p.getCreationDate().getTime());
				if(p.getModificationDate() != null)
					modificationDate = new java.sql.Timestamp(p.getModificationDate().getTime());
				
				productQuery.values(p.getId(), p.getDomain(), p.getName(), p.getCode(), p.getBrand(), p.getCategory(),
						p.getInventoriable(), p.getSerializable(), p.getLotable(), p.getStatus(), p.getVat(), p.getRetention(),
						p.getType(), p.getManufactured(),p.getComposition(), p.getCompositionPrice(), p.getSalesAccount(),
						p.getPurchaseAccount(), p.getCreationUser(), creationDate, p.getModificationUser(), modificationDate,
						p.getPackagedValue(), p.getKind());
			});
			productQuery.execute();
		});
	}
	
	public static void update(AONContext ctx, Product p) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.update(PRODUCT)
					.set(PRODUCT.DOMAIN, p.getDomain())
					.set(PRODUCT.NAME, p.getName())
					.set(PRODUCT.CODE, p.getCode())
					.set(PRODUCT.BRAND, p.getBrand())
					.set(PRODUCT.CATEGORY, p.getCategory())
					.set(PRODUCT.INVENTORIABLE, p.getInventoriable())
					.set(PRODUCT.SERIALIZABLE, p.getSerializable())
					.set(PRODUCT.LOTABLE, p.getLotable())
					.set(PRODUCT.STATUS, p.getStatus())
					.set(PRODUCT.VAT, p.getVat())
					.set(PRODUCT.RETENTION, p.getRetention())
					.set(PRODUCT.TYPE, p.getType())
					.set(PRODUCT.MANUFACTURED, p.getManufactured())
					.set(PRODUCT.COMPOSITION, p.getComposition())
					.set(PRODUCT.COMPOSITION_PRICE, p.getCompositionPrice())
					.set(PRODUCT.SALES_ACCOUNT, p.getSalesAccount())
					.set(PRODUCT.PURCHASE_ACCOUNT, p.getPurchaseAccount())
					.set(PRODUCT.CREATION_USER, p.getCreationUser())
					.set(PRODUCT.CREATION_DATE,p.getCreationDate() != null ?
							new java.sql.Timestamp(p.getCreationDate().getTime()) : null)
					.set(PRODUCT.MODIFICATION_USER, p.getModificationUser())
					.set(PRODUCT.MODIFICATION_DATE, new java.sql.Timestamp(p.getModificationDate().getTime()))
					.set(PRODUCT.KIND, p.getKind() != null ? p.getKind() : 0)
					.set(PRODUCT.PACKAGED, p.getPackagedValue())
					.where(PRODUCT.ID.equal(p.getId()))
					.execute();
		});
	}
	
	public static void delete(AONContext ctx, Product p) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.delete(PRODUCT)
				.where(PRODUCT.ID.equal(p.getId())).execute();
		});
	}
	
	public static void delete(AONContext ctx, Stream<Product> ps) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			Vector<Integer> ids = new Vector<Integer>();
			ps.forEach(p ->{
				ids.add(p.getId());
			});
			ctx.getDslContext()
				.delete(PRODUCT)
				.where(PRODUCT.ID.in(ids)).execute();
		});
	}

	// ------------------------------------- PRODUCT_TAG

	public static ProductTag getProductTag(AONContext ctx, Integer id){
		ctx.checkRead();
		Record3<Integer, Integer, Integer> record = ctx.getDslContext()
			.select(PRODUCT_TAG.DOMAIN, PRODUCT_TAG.PRODUCT, PRODUCT_TAG.TAG)
			.from(PRODUCT)
			.where(PRODUCT.ID.eq(id))
			.fetchOne();
		
		if(record != null){
			ProductTag pt = new ProductTag();
			pt.setId(id);
			if(record.value1() != null) pt.setDomain(record.getValue(PRODUCT_TAG.DOMAIN));
			if(record.value2() != null) pt.setProduct(record.getValue(PRODUCT_TAG.PRODUCT));
			if(record.value3() != null) pt.setTag(new Tag().setId(record.getValue(PRODUCT_TAG.TAG)));

			return pt;
		}
		return null;
	}
		
	public static void insertProductTag(AONContext ctx, ProductTag pt) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ProductValidation.validateProductTag(ctx, pt);
			ctx.getDslContext()
				.insertInto(PRODUCT_TAG, PRODUCT_TAG.DOMAIN, PRODUCT_TAG.PRODUCT, PRODUCT_TAG.TAG)
				.values(pt.getDomain(), pt.getProduct(), pt.getTag().getId())
				.execute();
		});		
	}
	
	public static void insertProductTag(AONContext ctx, Stream<ProductTag> pts) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStep3<ProductTagRecord, Integer, Integer, Integer> insertQuery = ctx.getDslContext().insertInto(PRODUCT_TAG, PRODUCT_TAG.DOMAIN, PRODUCT_TAG.PRODUCT, PRODUCT_TAG.TAG);
			pts.forEach(pt ->{
				ProductValidation.validateProductTag(ctx, pt);
				insertQuery.values(pt.getDomain(), pt.getProduct(), pt.getTag().getId());
			});
			insertQuery.execute();
		});		
	}

	public static void updateProductTag(AONContext ctx, ProductTag pt) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.update(PRODUCT_TAG)
					.set(PRODUCT_TAG.DOMAIN, pt.getDomain())
					.set(PRODUCT_TAG.PRODUCT, pt.getProduct())
					.set(PRODUCT_TAG.TAG, pt.getTag().getId())
					.where(PRODUCT_TAG.ID.equal(pt.getId()))
					.execute();
		});	
	}
	public static void deleteProductTag(AONContext ctx, ProductTag pt) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.delete(PRODUCT_TAG)
				.where(PRODUCT_TAG.ID.equal(pt.getId())).execute();
		});		
	}

	public static void deleteProductTag(AONContext ctx, Stream<ProductTag> pts) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			Vector<Integer> ids = new Vector<Integer>();
			pts.forEach(p ->{
				ids.add(p.getId());
			});
			ctx.getDslContext()
				.delete(PRODUCT_TAG)
				.where(PRODUCT_TAG.ID.in(ids)).execute();
		});		
	}
	
	// ------------------------------------- ITEM
	public static Item getItem(AONContext ctx, Integer itemId){
		return ctx.getDslContext().select().from(ITEM).where(ITEM.ID.eq(itemId)).limit(1).fetchInto(ITEM)
				.stream().map(new FullItemFiller(ctx)).findFirst().orElse(new Item());
	}
	
	public static Item getItem(AONContext ctx, ItemFilter filter){
		return ctx.getDslContext().select().from(ITEM).where(ITEM_PROPERTIES.getConditions(filter)).limit(1).fetchInto(ITEM)
				.stream().map(new FullItemFiller(ctx)).findFirst().orElse(new Item());
	}
	
	public static LinkedList<Item> getItemList(AONContext ctx, ItemFilter filter){
		return ctx.getDslContext().select().from(ITEM).where(ITEM_PROPERTIES.getConditions(filter)).fetchInto(ITEM)
				.stream().map(new FullItemFiller(ctx)).collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Deprecated
	public static Item getItemOld(AONContext ctx, Integer id){
		ctx.checkRead();
		
		Record20<Integer, Integer, String, String, String, String, String, Date, Double, Byte, Double, Double, Double, Double, Byte, String, String, Timestamp, String, Timestamp> record = ctx.getDslContext()
			.select(ITEM.DOMAIN, ITEM.PRODUCT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3,
					ITEM.DESCRIPTION, ITEM.SERIAL_NUMBER, ITEM.SERIAL_DATE, ITEM.PRICE,
					ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT,
					ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.CREATION_USER,
					ITEM.CREATION_DATE, ITEM.MODIFICATION_USER, ITEM.MODIFICATION_DATE)
			.from(ITEM)
			.where(ITEM.ID.eq(id))
			.fetchOne();
		
		if(record != null){
			Item i = new Item();
			i.setId(id);
			if(record.value1() != null) i.setDomain(record.value1());
			if(record.value2() != null) i.setProductId(record.value2());
			if(record.value3() != null) i.setDetail(record.value3());
			if(record.value4() != null) i.setDetail2(record.value4());
			if(record.value5() != null) i.setDetail3(record.value5());
			if(record.value6() != null) i.setDescription(record.value6());
			if(record.value7() != null) i.setSerialNumber(record.value7());
			// if(record.value8() != null) i.setSerialDate(record.value8());
			if(record.value9() != null) i.setPrice(record.value9());
			if(record.value10() != null) i.setStatus(record.value10());
			if(record.value11() != null) i.setExpensesPercent(record.value11());
			if(record.value12() != null) i.setExpensesFixed(record.value12());
			if(record.value13() != null) i.setProfitPercent(record.value13());
			if(record.value14() != null) i.setPurchasePrice(record.value14());
			if(record.value15() != null) i.setInternet(record.value15() == 1);
			if(record.value16() != null) i.setBarcode(record.value16());
			//if(record.value17() != null) i.setCreationUser(record.value17());
			//if(record.value18() != null) i.setCreationDate(record.value18());
			//if(record.value19() != null) i.setModificationUser(record.value19());
			//if(record.value20() != null) i.setModificationDate(record.value20());
			
			return i;
		}
		return null;
	}
	
	public static void insertItem(AONContext ctx, Item i) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ProductValidation.validateItem(ctx, i);
			ctx.getDslContext()
				.insertInto(ITEM, ITEM.DOMAIN, ITEM.PRODUCT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, ITEM.DESCRIPTION, ITEM.SERIAL_NUMBER, ITEM.SERIAL_DATE, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.CREATION_USER, ITEM.CREATION_DATE, ITEM.MODIFICATION_USER, ITEM.MODIFICATION_DATE,
						ITEM.PACK_FORMAT_TAG, ITEM.PACK_UNITS, ITEM.PACK_UNITS_TAG, ITEM.PACK_MEASUREMENT, ITEM.PACK_MEASUREMENT_TAG)
				.values(i.getDomain(), i.getProductId(), i.getDetail(), i.getDetail2(), i.getDetail3(), i.getDescription(), i.getSerialNumber()
						, null, i.getPrice(), i.getStatus(), i.getExpensesPercent(),i.getExpensesFixed(), i.getProfitPercent(), i.getPurchasePrice(), (byte)0, i.getBarcode(), i.getCreationUser(), i.getCreationDate(), i.getModificationUser(), i.getModificationDate(),
						i.getPackFormatTag().getId(), i.getPackUnits(), i.getPackUnitsTag().getId(), i.getPackMeasurement(), i.getPackMeasurementTag().getId())
				.execute();
		});		
	}

	public static void insertItem(AONContext ctx, Stream<Item> is) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStepN<ItemRecord> insertQuery = ctx.getDslContext().insertInto(ITEM, ITEM.DOMAIN, ITEM.PRODUCT, ITEM.DETAIL,
					ITEM.DETAIL2, ITEM.DETAIL3, ITEM.DESCRIPTION, ITEM.SERIAL_NUMBER, ITEM.SERIAL_DATE, ITEM.PRICE, ITEM.STATUS,
					ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE,
					ITEM.CREATION_USER, ITEM.CREATION_DATE, ITEM.MODIFICATION_USER, ITEM.MODIFICATION_DATE, ITEM.PACK_FORMAT_TAG,
					ITEM.PACK_UNITS, ITEM.PACK_UNITS_TAG, ITEM.PACK_MEASUREMENT, ITEM.PACK_MEASUREMENT_TAG);
			is.forEach(i ->{
				ProductValidation.validateItem(ctx, i);
				insertQuery.values(i.getDomain(), i.getProductId(), i.getDetail(), i.getDetail2(), i.getDetail3(), i.getDescription(),
						i.getSerialNumber(), null, i.getPrice(), i.getStatus(), i.getExpensesPercent(),i.getExpensesFixed(), i.getProfitPercent(),
						i.getPurchasePrice(), (byte)0, i.getBarcode(), i.getCreationUser(), i.getCreationDate(), i.getModificationUser(),
						i.getModificationDate(), i.getPackFormatTag().getId(), i.getPackUnits(), i.getPackUnitsTag().getId(),
						i.getPackMeasurement(), i.getPackMeasurementTag().getId());
			});
			insertQuery.execute();
		});		
	}

	public static void updateItem(AONContext ctx, Item i) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.update(ITEM)
					.set(ITEM.DOMAIN, i.getDomain())
					.set(ITEM.PRODUCT, i.getProductId())
					.set(ITEM.DETAIL, i.getDetail())
					.set(ITEM.DETAIL2, i.getDetail2())
					.set(ITEM.DETAIL3, i.getDetail3())
					.set(ITEM.DESCRIPTION, i.getDescription())
					.set(ITEM.SERIAL_NUMBER, i.getSerialNumber())
					//.set(ITEM.SERIAL_DATE, null)
					.set(ITEM.PRICE, i.getPrice())
					.set(ITEM.STATUS, i.getStatus())
					.set(ITEM.EXPENSES_PERCENT, i.getExpensesPercent())
					.set(ITEM.EXPENSES_FIXED, i.getExpensesFixed())
					.set(ITEM.PROFIT_PERCENT, i.getProfitPercent())
					.set(ITEM.PURCHASE_PRICE, i.getPurchasePrice())
					//.set(ITEM.INTERNET, null)
					.set(ITEM.BARCODE, i.getBarcode())
					//.set(ITEM.CREATION_USER, null)
					//.set(ITEM.CREATION_DATE, null)
					//.set(ITEM.MODIFICATION_USER, null)
					//.set(ITEM.MODIFICATION_DATE, null)
					.where(ITEM.ID.equal(i.getId()))
					.execute();
		});	
	}

	public static void deleteItem(AONContext ctx, Item i) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.delete(ITEM)
				.where(ITEM.ID.equal(i.getId())).execute();
		});	
	}

	public static void deleteItem(AONContext ctx, Stream<Item> is) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			Vector<Integer> ids = new Vector<Integer>();
			is.forEach(i ->{
				ids.add(i.getId());
			});
			ctx.getDslContext()
				.delete(ITEM)
				.where(ITEM.ID.in(ids)).execute();
		});		
	}

	public static void insertItemWithId(AONContext ctx, Item i) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ProductValidation.validateItem(ctx, i);
			ctx.getDslContext()
				.insertInto(ITEM,ITEM.ID, ITEM.DOMAIN, ITEM.PRODUCT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, ITEM.DESCRIPTION, ITEM.SERIAL_NUMBER, ITEM.SERIAL_DATE, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.CREATION_USER, ITEM.CREATION_DATE, ITEM.MODIFICATION_USER, ITEM.MODIFICATION_DATE)
				.values(i.getId(), i.getDomain(), i.getProductId(), i.getDetail(), i.getDetail2(), i.getDetail3(), i.getDescription(), null, null, i.getPrice(), i.getStatus(), i.getExpensesPercent(),i.getExpensesFixed(), i.getProfitPercent(), i.getPurchasePrice(), (byte)0, i.getBarcode(), i.getCreationUser(), i.getCreationDate(), i.getModificationUser(), i.getModificationDate())
				.execute();
		});		
	}

	public static void insertItemWithId(AONContext ctx, Stream<Item> is) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStep21<ItemRecord,Integer, Integer, Integer, String, String, String, String, String, Date, Double, Byte, Double, Double, Double, Double, Byte, String, String, Timestamp, String, Timestamp> insertQuery = ctx.getDslContext().insertInto(ITEM,ITEM.ID, ITEM.DOMAIN, ITEM.PRODUCT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, ITEM.DESCRIPTION, ITEM.SERIAL_NUMBER, ITEM.SERIAL_DATE, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.CREATION_USER, ITEM.CREATION_DATE, ITEM.MODIFICATION_USER, ITEM.MODIFICATION_DATE);
			is.forEach(i ->{
				ProductValidation.validateItem(ctx, i);
				insertQuery.values(i.getId(), i.getDomain(), i.getProductId(), i.getDetail(), i.getDetail2(), i.getDetail3(), i.getDescription(), null, null, i.getPrice(), i.getStatus(), i.getExpensesPercent(),i.getExpensesFixed(), i.getProfitPercent(), i.getPurchasePrice(), (byte)0, i.getBarcode(), i.getCreationUser(), i.getCreationDate(), i.getModificationUser(), i.getModificationDate());
			});
			insertQuery.execute();
		});		
	}

	private static class ImportProductFiller implements Function<ProductRecord, Product> {
		@Override
		public Product apply(ProductRecord r) {
			return new Product().setId(r.getId())
					.setCode(r.getCode())
					.setDomain(r.getDomain());		
		}
	}
	
	// ------------------------------------- BRAND
	
	public static Brand getBrand(AONContext ctx, Integer brandId){
		return ctx.getDslContext().select().from(BRAND).where(BRAND.ID.eq(brandId))
				.fetchInto(BRAND).stream().map(new FullBrandFiller()).findFirst().orElse(new Brand());
	}
	
	public static Brand getBrand(AONContext ctx, BrandFilter filter){
		return ctx.getDslContext().select().from(BRAND).where(BRAND_PROPERTIES.getConditions(filter))
				.fetchInto(BRAND).stream().map(new FullBrandFiller()).findFirst().orElse(new Brand());
	}
	
	public static Brand insertBrand(AONContext ctx, Brand brand){
		return ctx.getDslContext().insertInto(BRAND, BRAND.DOMAIN, BRAND.NAME)
		.values(brand.getDomain(), brand.getName()).returning().fetch().stream()
		.map(new FullBrandFiller()).findFirst().orElse(new Brand());
	}

	private static class FullBrandFiller implements Function<BrandRecord, Brand> {
		@Override
		public Brand apply(BrandRecord r) {
			return new Brand().setId(r.getId())
					.setName(r.getName())
					.setDomain(r.getDomain());		
		}
	}
	
	// ------------------------------------- PRODUCT CATEGORY
	
	public static ProductCategory getProductCategory(AONContext ctx, Integer productCategoryId){
		return ctx.getDslContext().select().from(PCATEGORY).where(PCATEGORY.ID.eq(productCategoryId))
				.fetchInto(PCATEGORY).stream().map(new FullProductCategoryFiller()).findFirst().orElse(new ProductCategory());
	}
	
	public static ProductCategory getProductCategory(AONContext ctx, ProductCategoryFilter filter){
		return ctx.getDslContext().select().from(PCATEGORY).where(PRODUCT_CATEGORY_PROPERTIES.getConditions(filter))
				.fetchInto(PCATEGORY).stream().map(new FullProductCategoryFiller()).findFirst().orElse(new ProductCategory());
	}
		
	public static ProductCategory insertProductCategory(AONContext ctx, ProductCategory productCategory){
		return ctx.getDslContext().insertInto(PCATEGORY, PCATEGORY.DOMAIN, PCATEGORY.NAME, PCATEGORY.DETAIL, PCATEGORY.DETAIL2, PCATEGORY.DETAIL3)
		.values(productCategory.getDomain(), productCategory.getName(), productCategory.getDetail(), productCategory.getDetail2(), productCategory.getDetail3())
		.returning().fetch().stream().map(new FullProductCategoryFiller()).findFirst().orElse(new ProductCategory());
	}

	private static class FullProductCategoryFiller implements Function<PcategoryRecord, ProductCategory> {
		@Override
		public ProductCategory apply(PcategoryRecord r) {
			return new ProductCategory().setId(r.getId())
					.setName(r.getName())
					.setDomain(r.getDomain())
					.setDetail(r.getDetail())
					.setDetail2(r.getDetail2())
					.setDetail3(r.getDetail3());		
		}
	}
	
	private static class FullProductFiller implements Function<ProductRecord, Product> {
		@Override
		public Product apply(ProductRecord r) {
			return new Product().setId(r.getId())
					.setName(r.getName())
					.setDomain(r.getDomain())
					.setBrand(r.getBrand())
					.setCategory(r.getCategory())
					.setCode(r.getCode())
					.setComposition(r.getComposition() == 1)
					.setComposition(r.getComposition())
					.setCompositionPrice(r.getCompositionPrice() == 1)
					.setCompositionPrice(r.getCompositionPrice())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setInventoriable(r.getInventoriable() == 1)
					.setInventoriable(r.getInventoriable())
					.setKind(r.getKind())
					.setLotable(r.getLotable() == 1)
					.setLotable(r.getLotable())
					.setManufactured(r.getManufactured())
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
					.setPackaged(r.getPackaged() == 1)
					.setPurchaseAccount(r.getPurchaseAccount())
					.setRetention(r.getRetention())
					.setSalesAccount(r.getSalesAccount())
					.setSerializable(r.getSerializable() == 1)
					.setSerializable(r.getSerializable())
					.setStatus(r.getStatus())
					.setType(r.getType())
					.setVat(r.getVat());					
		}
	}
	
	private static class FullItemFiller implements Function<ItemRecord, Item> {
		AONContext ctx;
		public FullItemFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public Item apply(ItemRecord r) {
			return new Item().setId(r.getId())
					.setBarcode(r.getBarcode())
					.setCreationDate(r.getCreationDate())
					.setCreationUser(r.getCreationUser())
					.setDescription(r.getDescription())
					.setDetail(r.getDetail())
					.setDetail2(r.getDetail2())
					.setDetail3(r.getDetail3())
					.setDomain(r.getDomain())
					.setExpensesFixed(r.getExpensesFixed())
					.setExpensesPercent(r.getExpensesPercent())
					.setInternet(r.getInternet() == 1)
					.setModificationDate(r.getModificationDate())
					.setModificationUser(r.getModificationUser())
					.setPackFormatTag(TagDAO.getTag(ctx, r.getPackFormatTag()))
					.setPackMeasurement(r.getPackMeasurement())
					.setPackMeasurementTag(TagDAO.getTag(ctx, r.getPackMeasurementTag()))
					.setPackUnits(r.getPackUnits().doubleValue())
					.setPackUnitsTag(TagDAO.getTag(ctx, r.getPackUnitsTag()))
					.setPrice(r.getPrice())
					.setProduct(getProduct(ctx, r.getProduct()))
					.setProductId(r.getProduct())
					.setProfitPercent(r.getProfitPercent())
					.setPurchasePrice(r.getPurchasePrice())
					.setSerialNumber(r.getSerialNumber())
					.setStatus(r.getStatus());
		}
	}
}
