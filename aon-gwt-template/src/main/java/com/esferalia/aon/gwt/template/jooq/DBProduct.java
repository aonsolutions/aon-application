package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertValuesStep14;
import org.jooq.InsertValuesStep15;
import org.jooq.InsertValuesStep3;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.config.Tag;
import com.code.aon.config.Tax;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.code.aon.product.Brand;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductTag;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.esferalia.aon.gwt.template.server.ProductInfo;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.jooq.tables.records.ItemRecord;
import com.esferalia.aon.jooq.tables.records.ProductRecord;
import com.esferalia.aon.jooq.tables.records.ProductTagRecord;

public class DBProduct {

	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";

	static TemplateInfo ti;
	public static Error insertProducts(String domain, Integer domainId,Vector<ProductInfo> products, TemplateInfo templateInfo) throws SQLException {
		ti = templateInfo;
		long startAll = System.currentTimeMillis();
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			DeleteConditionStep<ProductTagRecord> productTagDeleteQuery;
			DeleteConditionStep<ProductRecord> productDeleteQuery;
			DeleteConditionStep<ItemRecord> itemDeleteQuery;
			Vector<Integer> productTagDeleteProductIds = new Vector<Integer>();
			Vector<Integer> productDeleteProductIds = new Vector<Integer>();
			Vector<String> productInsertCode = new Vector<String>();
			Vector<Integer> itemDeleteItemIds = new Vector<Integer>();
			
			InsertValuesStep3<ProductTagRecord, Integer, Integer, Integer> productTagInsertQuery = dslContext.insertInto(PRODUCT_TAG, PRODUCT_TAG.DOMAIN, PRODUCT_TAG.PRODUCT, PRODUCT_TAG.TAG);
			InsertValuesStep14<ItemRecord, Integer, Integer, String, Double, Byte, Double, Double, Double, Double, Byte, String, String, String, String> itemInsertQuery = dslContext.insertInto(ITEM, ITEM.DOMAIN,ITEM.PRODUCT, ITEM.DESCRIPTION, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.DETAIL,ITEM.DETAIL2,ITEM.DETAIL3);
			InsertValuesStep15<ProductRecord, Integer, Integer, String, String, Integer, Integer, Byte, Byte, Integer, Integer, Byte, Byte, Byte, Integer, Integer> productInsertQuery = dslContext.insertInto(PRODUCT,PRODUCT.ID, PRODUCT.DOMAIN,PRODUCT.NAME,PRODUCT.CODE,PRODUCT.BRAND,PRODUCT.CATEGORY,PRODUCT.INVENTORIABLE,PRODUCT.STATUS,PRODUCT.VAT,PRODUCT.RETENTION,PRODUCT.TYPE,PRODUCT.COMPOSITION,PRODUCT.COMPOSITION_PRICE,PRODUCT.SALES_ACCOUNT,PRODUCT.PURCHASE_ACCOUNT);
			InsertValuesStep14<ProductRecord, Integer, String, String, Integer, Integer, Byte, Byte, Integer, Integer, Byte, Byte, Byte, Integer, Integer> productInsertQuery2 = dslContext.insertInto(PRODUCT, PRODUCT.DOMAIN,PRODUCT.NAME,PRODUCT.CODE,PRODUCT.BRAND,PRODUCT.CATEGORY,PRODUCT.INVENTORIABLE,PRODUCT.STATUS,PRODUCT.VAT,PRODUCT.RETENTION,PRODUCT.TYPE,PRODUCT.COMPOSITION,PRODUCT.COMPOSITION_PRICE,PRODUCT.SALES_ACCOUNT,PRODUCT.PURCHASE_ACCOUNT);
			InsertValuesStep15<ItemRecord,Integer, Integer, Integer, String, Double, Byte, Double, Double, Double, Double, Byte, String, String, String, String> itemInsertQuery2 = dslContext.insertInto(ITEM, ITEM.ID,ITEM.DOMAIN,ITEM.PRODUCT, ITEM.DESCRIPTION, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.DETAIL,ITEM.DETAIL2,ITEM.DETAIL3);
			
			//for(ProductInfo r : products){
			products.stream().forEach(r->{
				//get product 
		
				Record10<Integer, Integer, Integer, Byte, Byte, Integer, Integer, Byte, Byte, Byte> data = dslContext.select(PRODUCT.ID, PRODUCT.BRAND, PRODUCT.CATEGORY,PRODUCT.INVENTORIABLE,PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION,PRODUCT.TYPE, PRODUCT.COMPOSITION, PRODUCT.COMPOSITION_PRICE)
											.from(PRODUCT)
											.where(PRODUCT.CODE.eq(r.getProduct().getCode()).and(PRODUCT.DOMAIN.eq(domainId))).fetchOne();
			
				Product product = getProduct(r.getProduct(), data, ti, domainId);

				
				Integer productId;
				Byte inventoriable;
				if (product.isInventoriable()) inventoriable = 1;
				else inventoriable = 0;
				
				Byte composition;
				if (product.isComposition()) composition = 1;
				else composition = 0;
			
				Byte compositionPrice;
				if (product.isCompositionPrice()) compositionPrice = 1;
				else compositionPrice = 0;
				
				Integer brandId;
				if(product.getBrand() != null) brandId = product.getBrand().getId();
				else brandId = null;
				
				Integer catId;
				if(product.getCategory() != null) catId= product.getCategory().getId();
				else catId = null;
				
				r.setIsProduct(data!=null);
				if(data!= null){
					productId = data.value1();
					
					if(r.getProduct().getTags() != null){
						productTagDeleteProductIds.add(productId);
							
						r.getProduct().getTags().stream().forEach(t->{
							productTagInsertQuery.values(domainId, productId,t.getTag().getId());
						});
					}
					

					if(!esta(productId,productDeleteProductIds)){
						productDeleteProductIds.add(productId);
						
						productInsertQuery.values(product.getId(),product.getDomain(),product.getName(),product.getCode(),brandId,catId, inventoriable,(byte) product.getStatus().ordinal(),product.getVat().getId(),product.getRetention().getId(),(byte) product.getType().ordinal(),composition,compositionPrice,null,null);
					}
					
					/*dslContext.update(PRODUCT).set(PRODUCT.NAME, r.getProduct().getName())
							.set(PRODUCT.BRAND,brandId)
							.set(PRODUCT.CATEGORY, categoryId)
							.set(PRODUCT.INVENTORIABLE, inventoriable)
							.set(PRODUCT.STATUS, (byte) r.getProduct().getStatus().ordinal())
							.set(PRODUCT.VAT, r.getProduct().getVat().getId())
							.set(PRODUCT.RETENTION, r.getProduct().getRetention().getId())
							.set(PRODUCT.TYPE, (byte) r.getProduct().getType().ordinal())
							.set(PRODUCT.COMPOSITION, composition)
							.set(PRODUCT.COMPOSITION_PRICE, compositionPrice)
						.where(PRODUCT.ID.eq(productId)).execute();
					 */
				}
				else{
					/*productId = dslContext.insertInto(PRODUCT, PRODUCT.DOMAIN,PRODUCT.NAME,PRODUCT.CODE,PRODUCT.BRAND,PRODUCT.CATEGORY,PRODUCT.INVENTORIABLE,PRODUCT.STATUS,PRODUCT.VAT,PRODUCT.RETENTION,PRODUCT.TYPE,PRODUCT.COMPOSITION,PRODUCT.COMPOSITION_PRICE,PRODUCT.SALES_ACCOUNT,PRODUCT.PURCHASE_ACCOUNT)
						.values(domainId,r.getProduct().getName(),r.getProduct().getCode(),brandId,categoryId, inventoriable,(byte) r.getProduct().getStatus().ordinal(),r.getProduct().getVat().getId(),r.getProduct().getRetention().getId(),(byte) r.getProduct().getType().ordinal(),composition,compositionPrice,null,null).returning(PRODUCT.ID).fetchOne().getId();
					*/
					if(!esta(r.getProduct().getCode(), productInsertCode)){
						productInsertCode.add(r.getProduct().getCode());
						productInsertQuery2.values(domainId,product.getName(),product.getCode(),brandId,catId, inventoriable,(byte) product.getStatus().ordinal(),product.getVat().getId(),product.getRetention().getId(),(byte) product.getType().ordinal(),composition,compositionPrice,null,null);
					}
				}
			});
			if(error.getError()){
				if(productTagDeleteProductIds.size() > 0){
					productTagDeleteQuery = dslContext.delete(PRODUCT_TAG).where(PRODUCT_TAG.PRODUCT.in(productTagDeleteProductIds));
					productTagDeleteQuery.execute();
				}	
				Statement sOpen = connection.createStatement();
				sOpen.execute(SET_FOREIGN_KEY_CHECKS_0);
				System.out.println("Claves referenciales desactivadas");
				if(productDeleteProductIds.size() > 0){
					productDeleteQuery = dslContext.delete(PRODUCT).where(PRODUCT.ID.in(productDeleteProductIds));
					productDeleteQuery.execute();
					productInsertQuery.execute();
				}
				productInsertQuery2.execute();
			}
			products.stream().forEach(r->{
				Integer productId = dslContext.select(PRODUCT.ID).from(PRODUCT).where(PRODUCT.DOMAIN.eq(domainId)).and(PRODUCT.CODE.eq(r.getProduct().getCode())).fetchOne().value1();
				
				if(!r.getIsProduct()){
					if(r.getProduct().getTags() != null){
						r.getProduct().getTags().stream().forEach(t->{
							productTagInsertQuery.values(domainId, productId,t.getTag().getId());
						});
					}
				}
				Result<Record5<Integer, String, String, String, String>> data2 = dslContext.select(ITEM.ID,ITEM.BARCODE,ITEM.DETAIL,ITEM.DETAIL2,ITEM.DETAIL3)
						.from(ITEM)
						.where(ITEM.PRODUCT.eq(productId)).fetch();
				Boolean bool = false;
				String barcode = null;
				Integer itemId = null;
				String details = null;
				String details2 = r.getItem().getDetail()+r.getItem().getDetail2()+r.getItem().getDetail3();
				for(Record5<Integer, String, String, String, String> i : data2){
					itemId =  i.value1();
					if(i.value2()!= null) barcode = i.value2();
					details = "";
					if(i.value3()!= null) details =  details + i.value3();
					if(i.value4()!= null) details =  details + i.value4();
					if(i.value5()!= null) details =  details + i.value5();
					
					if(!esta(itemId,itemDeleteItemIds)){
						itemDeleteItemIds.add(itemId);
						itemInsertQuery2.values(itemId,domainId,productId,r.getItem().getDescription(),r.getItem().getPrice(),null,r.getItem().getExpensesPercent(),r.getItem().getExpensesFixed(),r.getItem().getProfitPercent(),r.getItem().getPurchasePrice(),(byte) 0,r.getItem().getBarcode(),r.getItem().getDetail(),r.getItem().getDetail2(),r.getItem().getDetail3());
					}
					else{
						error.setError(false);
						verror.add("*Fila " + (r.getRow()+1)+": El producto está repetido.");
						error.setTextError(verror);
					}
					
					if((barcode != null && barcode.equals(r.getItem().getBarcode())) || (details!= null && details.equals(details2))){
						
						/*dslContext.update(ITEM).set(ITEM.DESCRIPTION, r.getItem().getDescription())
							.set(ITEM.PRICE,r.getItem().getPrice())
							.set(ITEM.EXPENSES_PERCENT, r.getItem().getExpensesPercent())
							.set(ITEM.EXPENSES_FIXED, r.getItem().getExpensesFixed())
							.set(ITEM.PROFIT_PERCENT,r.getItem().getProfitPercent())
							.set(ITEM.PURCHASE_PRICE, r.getItem().getPurchasePrice())
							.set(ITEM.BARCODE, r.getItem().getBarcode())
						.where(ITEM.ID.eq(itemId)).execute();*/
						bool = true;
					}
				}
				if(!bool){
					itemInsertQuery.values(domainId,productId,r.getItem().getDescription(),r.getItem().getPrice(),null,r.getItem().getExpensesPercent(),r.getItem().getExpensesFixed(),r.getItem().getProfitPercent(),r.getItem().getPurchasePrice(),(byte) 0,r.getItem().getBarcode(),r.getItem().getDetail(),r.getItem().getDetail2(),r.getItem().getDetail3());//.returning(ITEM.ID).fetchOne().getId();
				}
			});
			
			if(error.getError()){
				if(itemDeleteItemIds.size() > 0){
					itemDeleteQuery = dslContext.delete(ITEM).where(ITEM.ID.in(itemDeleteItemIds));
					itemDeleteQuery.execute();
					itemInsertQuery2.execute();
				}
				Statement sClose = connection.createStatement();
				sClose.execute(SET_FOREIGN_KEY_CHECKS_1);
				System.out.println("Claves referenciales activadas");
				
				productTagInsertQuery.execute();
				itemInsertQuery.execute();
			}
			long time = System.currentTimeMillis() - startAll;
			System.out.println("time: " + (time/1000d));
			return error;
		} finally {
			if (connection != null)
				connection.close();
		}
		
		
	}
	
	private static Product getProduct(Product p, Record10<Integer, Integer, Integer, Byte, Byte, Integer, Integer,Byte, Byte, Byte> data, TemplateInfo ti, Integer domainId){
		Product product = new Product();
		product.setId(data.value1());
		product.setDomain(domainId);
		product.setName(p.getName());
		product.setCode(p.getCode());
		
		if(ti.getColumns().contains("Marca")) product.setBrand(p.getBrand());
		else if(data.value2()!= null){
			Brand brand = new Brand();if(data.value2() != null) brand.setId(data.value2());
			product.setBrand(brand);
		}		
		else product.setBrand(null);
			
		if(ti.getColumns().contains("Categor\u00eda")) product.setCategory(p.getCategory());
		else if(data.value3() != null){
			ProductCategory cat = new ProductCategory();cat.setId(data.value3());
			product.setCategory(cat);
		}
		else product.setCategory(null);
		
		if(ti.getColumns().contains("Inventoriable")) product.setInventoriable(p.isInventoriable());
		else product.setInventoriable(data.value4()==1);
	
		if(ti.getColumns().contains("Estado")) product.setStatus(p.getStatus());
		else product.setStatus(ProductStatus.values()[data.value5()]);
		
		if(ti.getColumns().contains("IVA")) product.setVat(p.getVat());
		else{
			Tax t = new Tax(); t.setId(data.value6());
			product.setVat(t);
		}
		
		if(ti.getColumns().contains("IRPF")) product.setRetention(p.getRetention());
		else{
			Tax tax = new Tax(); tax.setId(data.value7());
			product.setRetention(tax); 
		}
		
		if(ti.getColumns().contains("Tipo")) product.setType(p.getType());
		else product.setType(ProductType.values()[data.value8()]);

		if(ti.getColumns().contains("Producto Compuesto")) product.setComposition(p.isComposition());
		else product.setComposition(data.value9() ==1);
		
		if(ti.getColumns().contains("Precio Composici\u00f3n")) product.setCompositionPrice(p.isCompositionPrice());
		else product.setCompositionPrice(data.value10() ==1);		 
		 
		return product;
		
	}
	
	private static Boolean esta(String code, Vector<String> vector) {
		for (String string : vector) {
			if(string.equals(code)) return true;
		}
		return false;
	}
	
	private static Boolean esta(Integer id, Vector<Integer> vector) {
		for (Integer integer : vector) {
			if(integer.equals(id)) return true;
		}
		return false;
	}
	
	public static ProductCategory getCategory(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record5<Integer, String, String, String, String>> data = dslContext.select(PCATEGORY.ID,PCATEGORY.NAME,PCATEGORY.DETAIL,PCATEGORY.DETAIL2,PCATEGORY.DETAIL3)
				.from(PCATEGORY)
				.where(PCATEGORY.ID.eq(id)).fetch();
			
			ProductCategory c = new ProductCategory();
			c.setId(data.get(0).value1());
			c.setName(data.get(0).value2());
			if(data.get(0).value3() != null) c.setDetail(data.get(0).value3());
			if(data.get(0).value4() != null) c.setDetail2(data.get(0).value4());
			if(data.get(0).value5() != null) c.setDetail3(data.get(0).value5());
			return c;
		} finally {
			if (connection != null)
				connection.close();
		}	
	}
	
	public static  Vector<ProductCategory> getCategories(String domain, Integer domainId) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record5<Integer, String, String, String, String>> data = dslContext.select(PCATEGORY.ID,PCATEGORY.NAME,PCATEGORY.DETAIL,PCATEGORY.DETAIL2,PCATEGORY.DETAIL3)
				.from(PCATEGORY)
				.where(PCATEGORY.DOMAIN.eq(domainId)).fetch();
			
			Result<Record5<Integer, String, String, String, String>> dataSon = dslContext.select(PCATEGORY.ID,PCATEGORY.NAME,PCATEGORY.DETAIL,PCATEGORY.DETAIL2,PCATEGORY.DETAIL3)
					.from(PCATEGORY).join(DOMAIN).on(PCATEGORY.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId)).fetch();
			
			Result<Record5<Integer, String, String, String, String>> dataParent = dslContext.select(PCATEGORY.ID,PCATEGORY.NAME,PCATEGORY.DETAIL,PCATEGORY.DETAIL2,PCATEGORY.DETAIL3)
					.from(PCATEGORY).join(DOMAIN).on(PCATEGORY.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId)).fetch();
			
			Vector<ProductCategory> v = new Vector<ProductCategory>();
			
			for(Record5<Integer, String, String, String, String> r : data){
				ProductCategory pc = new ProductCategory();
				pc.setId(r.value1());
				pc.setName(r.value2());
				if(r.value3() != null) pc.setDetail(r.value3());
				if(r.value4() != null) pc.setDetail2(r.value4());
				if(r.value5() != null) pc.setDetail3(r.value5());
				v.add(pc);
			}
			for(Record5<Integer, String, String, String, String> r : dataSon){
				ProductCategory pc = new ProductCategory();
				pc.setId(r.value1());
				pc.setName(r.value2());
				if(r.value3() != null) pc.setDetail(r.value3());
				if(r.value4() != null) pc.setDetail2(r.value4());
				if(r.value5() != null) pc.setDetail3(r.value5());
				v.add(pc);
			}
			for(Record5<Integer, String, String, String, String> r : dataParent){
				ProductCategory pc = new ProductCategory();
				pc.setId(r.value1());
				pc.setName(r.value2());
				if(r.value3() != null) pc.setDetail(r.value3());
				if(r.value4() != null) pc.setDetail2(r.value4());
				if(r.value5() != null) pc.setDetail3(r.value5());
				v.add(pc);
			}
			return v;
			
		} finally {
		if (connection != null)
			connection.close();
		}
	}
	
	public static Vector<ProductInfo> getProducts(String domain,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record19<String, String, Integer, Integer, Byte, Integer, Integer, Byte, Byte, Byte, Byte, Double, Double, String, String, String, String, String, Integer>>
				data =	dslContext.select(PRODUCT.CODE,PRODUCT.NAME,PRODUCT.CATEGORY
						,PRODUCT.BRAND,PRODUCT.TYPE,PRODUCT.VAT, PRODUCT.RETENTION,PRODUCT.INVENTORIABLE,PRODUCT.COMPOSITION
						,PRODUCT.COMPOSITION_PRICE,PRODUCT.STATUS,ITEM.PURCHASE_PRICE,ITEM.PRICE,ITEM.BARCODE,ITEM.DESCRIPTION
						,ITEM.DETAIL,ITEM.DETAIL2,ITEM.DETAIL3,PRODUCT.ID)
						.from(PRODUCT).join(ITEM).on(PRODUCT.ID.eq(ITEM.PRODUCT))
						.where(PRODUCT.DOMAIN.eq(domainId)).fetch();
			
			Vector<ProductInfo> v = new Vector<ProductInfo>();
			
			for(Record19<String, String, Integer, Integer, Byte, Integer, Integer, Byte, Byte, Byte, Byte, Double, Double, String, String, String, String, String, Integer> r : data){
				ProductInfo pi = new ProductInfo();
				Product p = new Product();
				Item i = new Item();
				p.setCode(r.value1());
				p.setName(r.value2());
				if(r.value3()!=null) p.setCategory(getCategory(domain, r.value3()));
				else {
					ProductCategory pc = new ProductCategory();
					pc.setName("");
					p.setCategory(pc);
				}
				if(r.value4()!=null) p.setBrand(getBrand(domain, r.value4()));
				else{
					Brand b = new Brand();
					b.setName("");
					p.setBrand(b);
				}
				if(r.value5()!=null) p.setType(ProductType.values()[r.value5()]);
				if(r.value6()!=null) p.setVat(getTax(domain, r.value6()));
				else{
					Tax t = new Tax();
					t.setName("");
					p.setVat(t);
				}
				if(r.value7()!=null) p.setRetention(getTax(domain, r.value7()));
				else{
					Tax t = new Tax();
					t.setName("");
					p.setRetention(t);
				}
				if(r.value8()!=null) p.setInventoriable(r.value8()==1);
				if(r.value9()!=null) p.setComposition(r.value9()==1);
				if(r.value10()!=null) p.setCompositionPrice(r.value10()==1);
				if(r.value11()!=null) p.setStatus(ProductStatus.values()[r.value11()]);
				if(r.value12()!=null) i.setPurchasePrice(r.value12());
				if(r.value13()!=null) i.setPrice(r.value13());
				if(r.value14()!=null) i.setBarcode(r.value14());
				else i.setBarcode("");
				if(r.value15()!=null) i.setDescription(r.value15());
				else i.setDescription("");
				if(r.value16()!=null) i.setDetail(r.value16());
				else i.setDetail("");
				if(r.value17()!=null) i.setDetail2(r.value17());
				else i.setDetail2("");
				if(r.value18()!=null) i.setDetail3(r.value18());
				else i.setDetail3("");
				p.setTags(getTags(dslContext, r.value19()));
				pi.setProduct(p);
				pi.setItem(i);
				v.add(pi);
			}	
			return v;

		} finally {
		if (connection != null)
			connection.close();
		}
	}
	
	public static Set<ProductTag> getTags(DSLContext dslContext, Integer id ){
		
		Result<Record1<String>> data = dslContext.select(TAG.NAME)
			.from(TAG).join(PRODUCT_TAG).on(TAG.ID.eq(PRODUCT_TAG.TAG))
			.where(PRODUCT_TAG.PRODUCT.eq(id)).fetch();
		
		Set<ProductTag> s = new HashSet<ProductTag>();
		
		for(Record1<String> r : data ){
			ProductTag pt = new ProductTag();
			Tag t = new Tag();
			t.setName(r.value1());
			pt.setTag(t);
			s.add(pt);
		}		
			
		return s;
		
	}
	
	public static  Vector<ProductTag> getTags(String domain, Integer domainId) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record2< Integer, String>> data = dslContext.select(TAG.ID,TAG.NAME)
				.from(TAG)
				.where(TAG.DOMAIN.eq(domainId)).fetch();
		
			Result<Record2< Integer, String>> dataSon = dslContext.select(TAG.ID,TAG.NAME)
					.from(TAG).join(DOMAIN).on(TAG.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId)).fetch();
			
			Result<Record2< Integer, String>> dataParent = dslContext.select(TAG.ID,TAG.NAME)
					.from(TAG).join(DOMAIN).on(TAG.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId)).fetch();
			
			Vector<ProductTag> v = new Vector<ProductTag>();
			
			for(Record2<Integer, String> r : data){
				ProductTag tag = new ProductTag();
				Tag tag1 = new Tag();
				tag1.setId(r.value1());
				tag1.setName(r.value2());
				tag.setTag(tag1);
				v.add(tag);
			}
			for(Record2<Integer, String> r : dataSon){
				ProductTag tag = new ProductTag();
				Tag tag1 = new Tag();
				tag1.setId(r.value1());
				tag1.setName(r.value2());
				tag.setTag(tag1);
				v.add(tag);
			}
			for(Record2<Integer, String> r : dataParent){
				ProductTag tag = new ProductTag();
				Tag tag1 = new Tag();
				tag1.setId(r.value1());
				tag1.setName(r.value2());
				tag.setTag(tag1);
				v.add(tag);
			}
			
			return v;
			
		} finally {
		if (connection != null)
			connection.close();
		}
	}
	
	public static  Vector<Brand> getBrands(String domain, Integer domainId) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record2<Integer, String>> data = dslContext.select(BRAND.ID,BRAND.NAME)
				.from(BRAND)
				.where(BRAND.DOMAIN.eq(domainId)).fetch();
			
			Result<Record2<Integer, String>> dataSon = dslContext.select(BRAND.ID,BRAND.NAME)
					.from(BRAND).join(DOMAIN).on(BRAND.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId)).fetch();
			
			Result<Record2<Integer, String>> dataParent = dslContext.select(BRAND.ID,BRAND.NAME)
					.from(BRAND).join(DOMAIN).on(BRAND.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId)).fetch();
			
			Vector<Brand> v = new Vector<Brand>();
			
			for(Record2<Integer, String> r : data){
				Brand brand = new Brand();
				brand.setId(r.value1());
				brand.setName(r.value2());
				
				v.add(brand);
			}

			for(Record2<Integer, String> r : dataSon){
				Brand brand = new Brand();
				brand.setId(r.value1());
				brand.setName(r.value2());
				
				v.add(brand);
			}

			for(Record2<Integer, String> r : dataParent){
				Brand brand = new Brand();
				brand.setId(r.value1());
				brand.setName(r.value2());
				
				v.add(brand);
			}
			return v;
			
		} finally {
		if (connection != null)
			connection.close();
		}
	}
	
	public static Brand getBrand(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record2<Integer, String>> data = dslContext.select(BRAND.ID,BRAND.NAME)
				.from(BRAND)
				.where(BRAND.ID.eq(id)).fetch();
			
			Brand brand = new Brand();
			brand.setId(data.get(0).value1());
			brand.setName(data.get(0).value2());
			
			return brand;
		} finally {
			if (connection != null)
				connection.close();
		}	
	}
	
	public static Tax getTax(String domain, Integer id) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3<Integer,String,Double>>  data = dslContext.select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
				.from(TAX)
				.where(TAX.ID.eq(id)).fetch();
			
			Tax t = new Tax();
			t.setId(id);
			if(data.get(0).value2()!=null)t.setName(data.get(0).value2());
			else t.setName("");
			if(data.get(0).value3()!=null)t.setPercentage(data.get(0).value3());
			return t;
		} finally {
			if (connection != null)
				connection.close();
		}	
	}
	
	public static Vector<Tax> getRetentions(String domain, Integer domainId) throws SQLException {
		Vector<Tax> v = new Vector<Tax>();
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3<Integer,String,Double>> data = dslContext.select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX)
					.where(TAX.DOMAIN.eq(domainId).and(TAX.TAX_TYPE.eq((byte)2))).fetch();
			
			Result<Record3<Integer,String,Double>> dataSon = dslContext.select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX).join(DOMAIN).on(TAX.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId).and(TAX.TAX_TYPE.eq((byte)2))).fetch();
			
			Result<Record3<Integer,String,Double>> dataParent = dslContext.select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX).join(DOMAIN).on(TAX.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId).and(TAX.TAX_TYPE.eq((byte)2))).fetch();
			
			data.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			
			dataSon.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			
			dataParent.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			return v;			
		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Vector<Tax> getIVA(String domain, Integer domainId) throws SQLException {
		Vector<Tax> v = new Vector<Tax>();
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3<Integer,String,Double>> data = dslContext.select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX)
					.where(TAX.DOMAIN.eq(domainId).and(TAX.TAX_TYPE.eq((byte)1))).fetch();
			
			Result<Record3<Integer,String,Double>> dataSon = dslContext.select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX).join(DOMAIN).on(TAX.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId).and(TAX.TAX_TYPE.eq((byte)1))).fetch();
			
			Result<Record3<Integer,String,Double>> dataParent = dslContext.select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX).join(DOMAIN).on(TAX.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId).and(TAX.TAX_TYPE.eq((byte)1))).fetch();
			
			data.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			
			dataSon.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			
			dataParent.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			return v;			
		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	
}
