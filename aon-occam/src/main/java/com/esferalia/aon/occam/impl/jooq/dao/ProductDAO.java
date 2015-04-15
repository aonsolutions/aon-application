package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Stream;

import org.jooq.InsertValuesStep20;
import org.jooq.InsertValuesStep21;
import org.jooq.InsertValuesStep22;
import org.jooq.InsertValuesStep3;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record3;

import com.esferalia.aon.jooq.tables.records.ItemRecord;
import com.esferalia.aon.jooq.tables.records.ProductRecord;
import com.esferalia.aon.jooq.tables.records.ProductTagRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.impl.jooq.validation.ProductValidation;


public class ProductDAO {
	
	public static List<String> getProductTags(AONContext ctx) {
		ctx.checkRead();
		final List<String> list = new LinkedList<String>();
		ctx.getDslContext()
			.select(TAG.NAME)
			.from(TAG)
			.where(TAG.DOMAIN.equal(ctx.getDomainId()))
			.fetch()
			.stream()
			.forEach( record -> list.add(record.getValue(TAG.NAME) ));
		return list; 
	}

	public static Map<Integer,String[]> getProductTagMap(AONContext ctx) {
		ctx.checkRead();
		final Map<Integer,String[]> map = new HashMap<Integer, String[]>();
		ctx.getDslContext()
			.select(PRODUCT.ID,TAG.NAME)
			.from(PRODUCT)
			.join(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.equal(PRODUCT.ID))
			.join(TAG).on(TAG.ID.equal(PRODUCT_TAG.TAG))
			.where(PRODUCT.DOMAIN.equal(ctx.getDomainId()))
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
				.insertInto(PRODUCT, PRODUCT.DOMAIN, PRODUCT.NAME, PRODUCT.CODE, PRODUCT.BRAND, PRODUCT.CATEGORY, PRODUCT.INVENTORIABLE, PRODUCT.SERIALIZABLE, PRODUCT.LOTABLE, PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION, PRODUCT.TYPE, PRODUCT.MANUFACTURED, PRODUCT.COMPOSITION, PRODUCT.COMPOSITION_PRICE, PRODUCT.SALES_ACCOUNT, PRODUCT.PURCHASE_ACCOUNT, PRODUCT.CREATION_USER, PRODUCT.CREATION_DATE, PRODUCT.MODIFICATION_USER, PRODUCT.MODIFICATION_DATE)
				.values(p.getDomain(), p.getName(), p.getCode(), p.getBrand(), p.getCategory(), p.getInventoriable(), p.getSerializable(), p.getLotable(), p.getStatus(), p.getVat(), p.getRetention(), p.getType(), p.getManufactured(),p.getComposition(), p.getCompositionPrice(), p.getSalesAccount(), p.getPurchaseAccount(), p.getCreationUser(), creationDate, p.getModificationUser(), modificationDate)
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
				.insertInto(PRODUCT,PRODUCT.ID, PRODUCT.DOMAIN, PRODUCT.NAME, PRODUCT.CODE, PRODUCT.BRAND, PRODUCT.CATEGORY, PRODUCT.INVENTORIABLE, PRODUCT.SERIALIZABLE, PRODUCT.LOTABLE, PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION, PRODUCT.TYPE, PRODUCT.MANUFACTURED, PRODUCT.COMPOSITION, PRODUCT.COMPOSITION_PRICE, PRODUCT.SALES_ACCOUNT, PRODUCT.PURCHASE_ACCOUNT, PRODUCT.CREATION_USER, PRODUCT.CREATION_DATE, PRODUCT.MODIFICATION_USER, PRODUCT.MODIFICATION_DATE)
					.values(p.getId(), p.getDomain(), p.getName(), p.getCode(), p.getBrand(), p.getCategory(), p.getInventoriable(), p.getSerializable(), p.getLotable(), p.getStatus(), p.getVat(), p.getRetention(), p.getType(), p.getManufactured(),p.getComposition(), p.getCompositionPrice(), p.getSalesAccount(), p.getPurchaseAccount(), p.getCreationUser(), creationDate, p.getModificationUser(), modificationDate)
					.execute();
		});
	}
	
	public static void insert(AONContext ctx, Stream<Product> ps) {
		ctx.checkWrite();
		AONContext sctx = ctx;
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStep21<ProductRecord, Integer, String, String, Integer, Integer, Byte, Byte, Byte, Byte, Integer, Integer, Byte, Byte, Byte, Byte, Integer, Integer, String, Timestamp, String, Timestamp> insertQuery = ctx.getDslContext().insertInto(PRODUCT, PRODUCT.DOMAIN, PRODUCT.NAME, PRODUCT.CODE, PRODUCT.BRAND, PRODUCT.CATEGORY, PRODUCT.INVENTORIABLE, PRODUCT.SERIALIZABLE, PRODUCT.LOTABLE, PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION, PRODUCT.TYPE, PRODUCT.MANUFACTURED, PRODUCT.COMPOSITION, PRODUCT.COMPOSITION_PRICE, PRODUCT.SALES_ACCOUNT, PRODUCT.PURCHASE_ACCOUNT, PRODUCT.CREATION_USER, PRODUCT.CREATION_DATE, PRODUCT.MODIFICATION_USER, PRODUCT.MODIFICATION_DATE);
			AONContext sctx2 = sctx;
			ps.forEach(p ->{
				ProductValidation.validate(sctx2, p);
				Timestamp creationDate = null, modificationDate = null;
				if(p.getCreationDate() != null)
					creationDate = new java.sql.Timestamp(p.getCreationDate().getTime());
				if(p.getModificationDate() != null)
					modificationDate = new java.sql.Timestamp(p.getModificationDate().getTime());
				
				insertQuery.values(p.getDomain(), p.getName(), p.getCode(), p.getBrand(), p.getCategory(), p.getInventoriable(), p.getSerializable(), p.getLotable(), p.getStatus(), p.getVat(), p.getRetention(), p.getType(), p.getManufactured(),p.getComposition(), p.getCompositionPrice(), p.getSalesAccount(), p.getPurchaseAccount(), p.getCreationUser(), creationDate, p.getModificationUser(), modificationDate);
			});
			insertQuery.execute();
		});
	}
	public static void insertWithId(AONContext ctx, Stream<Product> ps) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStep22<ProductRecord, Integer, Integer, String, String, Integer, Integer, Byte, Byte, Byte, Byte, Integer, Integer, Byte, Byte, Byte, Byte, Integer, Integer, String, Timestamp, String, Timestamp> insertQuery = ctx.getDslContext().insertInto(PRODUCT,PRODUCT.ID, PRODUCT.DOMAIN, PRODUCT.NAME, PRODUCT.CODE, PRODUCT.BRAND, PRODUCT.CATEGORY, PRODUCT.INVENTORIABLE, PRODUCT.SERIALIZABLE, PRODUCT.LOTABLE, PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION, PRODUCT.TYPE, PRODUCT.MANUFACTURED, PRODUCT.COMPOSITION, PRODUCT.COMPOSITION_PRICE, PRODUCT.SALES_ACCOUNT, PRODUCT.PURCHASE_ACCOUNT, PRODUCT.CREATION_USER, PRODUCT.CREATION_DATE, PRODUCT.MODIFICATION_USER, PRODUCT.MODIFICATION_DATE);
			ps.forEach(p ->{
				ProductValidation.validate(ctx, p);
				Timestamp creationDate = null, modificationDate = null;
				if(p.getCreationDate() != null)
					creationDate = new java.sql.Timestamp(p.getCreationDate().getTime());
				if(p.getModificationDate() != null)
					modificationDate = new java.sql.Timestamp(p.getModificationDate().getTime());
				
				insertQuery.values(p.getId(), p.getDomain(), p.getName(), p.getCode(), p.getBrand(), p.getCategory(), p.getInventoriable(), p.getSerializable(), p.getLotable(), p.getStatus(), p.getVat(), p.getRetention(), p.getType(), p.getManufactured(),p.getComposition(), p.getCompositionPrice(), p.getSalesAccount(), p.getPurchaseAccount(), p.getCreationUser(), creationDate, p.getModificationUser(), modificationDate);
			});
			insertQuery.execute();
		});
	}
	
	public static void update(AONContext ctx, Product p) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ProductValidation.validate(ctx, p);
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
					.set(PRODUCT.CREATION_DATE, new java.sql.Timestamp(p.getCreationDate().getTime()))
					.set(PRODUCT.MODIFICATION_USER, p.getModificationUser())
					.set(PRODUCT.MODIFICATION_DATE, new java.sql.Timestamp(p.getModificationDate().getTime()))
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
			if(record.value1() != null) pt.setDomain(record.value1());
			if(record.value2() != null) pt.setProduct(record.value2());
			if(record.value3() != null) pt.setTag(record.value3());
			
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
				.values(pt.getDomain(), pt.getProduct(), pt.getTag())
				.execute();
		});		
	}
	
	public static void insertProductTag(AONContext ctx, Stream<ProductTag> pts) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStep3<ProductTagRecord, Integer, Integer, Integer> insertQuery = ctx.getDslContext().insertInto(PRODUCT_TAG, PRODUCT_TAG.DOMAIN, PRODUCT_TAG.PRODUCT, PRODUCT_TAG.TAG);
			pts.forEach(pt ->{
				ProductValidation.validateProductTag(ctx, pt);
				insertQuery.values(pt.getDomain(), pt.getProduct(), pt.getTag());
			});
			insertQuery.execute();
		});		
	}

	public static void updateProductTag(AONContext ctx, ProductTag pt) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ProductValidation.validateProductTag(ctx, pt);
			ctx.getDslContext()
				.update(PRODUCT_TAG)
					.set(PRODUCT_TAG.DOMAIN, pt.getDomain())
					.set(PRODUCT_TAG.PRODUCT, pt.getProduct())
					.set(PRODUCT_TAG.TAG, pt.getTag())
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

	public static Item getItem(AONContext ctx, Integer id){
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
				.insertInto(ITEM, ITEM.DOMAIN, ITEM.PRODUCT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, ITEM.DESCRIPTION, ITEM.SERIAL_NUMBER, ITEM.SERIAL_DATE, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.CREATION_USER, ITEM.CREATION_DATE, ITEM.MODIFICATION_USER, ITEM.MODIFICATION_DATE)
				.values(i.getDomain(), i.getProductId(), i.getDetail(), i.getDetail2(), i.getDetail3(), i.getDescription(), i.getSerialNumber()
						, null, i.getPrice(), i.getStatus(), i.getExpensesPercent(),i.getExpensesFixed(), i.getProfitPercent(), i.getPurchasePrice(), null, i.getBarcode(), null, null, null, null)
				.execute();
		});		
	}

	public static void insertItem(AONContext ctx, Stream<Item> is) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStep20<ItemRecord, Integer, Integer, String, String, String, String, String, Date, Double, Byte, Double, Double, Double, Double, Byte, String, String, Timestamp, String, Timestamp> insertQuery = ctx.getDslContext().insertInto(ITEM, ITEM.DOMAIN, ITEM.PRODUCT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, ITEM.DESCRIPTION, ITEM.SERIAL_NUMBER, ITEM.SERIAL_DATE, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.CREATION_USER, ITEM.CREATION_DATE, ITEM.MODIFICATION_USER, ITEM.MODIFICATION_DATE);
			is.forEach(i ->{
				ProductValidation.validateItem(ctx, i);
				insertQuery.values(i.getDomain(), i.getProductId(), i.getDetail(), i.getDetail2(), i.getDetail3(), i.getDescription(), i.getSerialNumber(), null, i.getPrice(), i.getStatus(), i.getExpensesPercent(),i.getExpensesFixed(), i.getProfitPercent(), i.getPurchasePrice(), null, i.getBarcode(), null, null, null, null);
			});
			insertQuery.execute();
		});		
	}

	public static void updateItem(AONContext ctx, Item i) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ProductValidation.validateItem(ctx, i);
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
				.values(i.getId(), i.getDomain(), i.getProductId(), i.getDetail(), i.getDetail2(), i.getDetail3(), i.getDescription(), null, null, i.getPrice(), i.getStatus(), i.getExpensesPercent(),i.getExpensesFixed(), i.getProfitPercent(), i.getPurchasePrice(), null, i.getBarcode(), null, null, null, null)
				.execute();
		});		
	}

	public static void insertItemWithId(AONContext ctx, Stream<Item> is) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStep21<ItemRecord,Integer, Integer, Integer, String, String, String, String, String, Date, Double, Byte, Double, Double, Double, Double, Byte, String, String, Timestamp, String, Timestamp> insertQuery = ctx.getDslContext().insertInto(ITEM,ITEM.ID, ITEM.DOMAIN, ITEM.PRODUCT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, ITEM.DESCRIPTION, ITEM.SERIAL_NUMBER, ITEM.SERIAL_DATE, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.CREATION_USER, ITEM.CREATION_DATE, ITEM.MODIFICATION_USER, ITEM.MODIFICATION_DATE);
			is.forEach(i ->{
				ProductValidation.validateItem(ctx, i);
				insertQuery.values(i.getId(), i.getDomain(), i.getProductId(), i.getDetail(), i.getDetail2(), i.getDetail3(), i.getDescription(), null, null, i.getPrice(), i.getStatus(), i.getExpensesPercent(),i.getExpensesFixed(), i.getProfitPercent(), i.getPurchasePrice(), null, i.getBarcode(), null, null, null, null);
			});
			insertQuery.execute();
		});		
	}

	
	
	
}
