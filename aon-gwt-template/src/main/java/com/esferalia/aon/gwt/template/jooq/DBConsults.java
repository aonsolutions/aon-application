package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.jooq.tables.Warehouse.WAREHOUSE;
import static com.esferalia.aon.jooq.tables.WarehouseTransfer.WAREHOUSE_TRANSFER;
import static com.esferalia.aon.jooq.tables.WarehouseTransferDetail.WAREHOUSE_TRANSFER_DETAIL;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;
import static com.esferalia.aon.jooq.tables.Series.SERIES;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.awt.List;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Series;
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
import com.esferalia.aon.gwt.template.server.StockInfo;
import com.esferalia.aon.gwt.template.server.Utils;
import com.esferalia.aon.gwt.template.server.Warehouse;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;




public class DBConsults {
	
	public static TemplateList getTemplates(String domain , Integer domainId) throws SQLException{
			Connection connection = null;
			try {
				connection = DatabaseSync.getConnection(domain);
				
				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				
				
				// DOMAIN + DOMAIN SON
				Result<Record4<Integer, String, Byte, String>> record = dslContext
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.ID.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.eq((byte)15).and(RATTACH.DOMAIN.eq(domainId).or(DOMAIN.PARENT.eq(domainId))))
						.fetch();
				
				// DOMAIN PARENT
				Result<Record4<Integer, String, Byte, String>> recordParent = dslContext
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.PARENT.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.eq((byte)15).and(DOMAIN.ID.eq(domainId)))
						.fetch();
				
				Vector<TemplateInfo> v = new Vector<TemplateInfo>();
				record.stream().forEach(r -> {
					TemplateInfo ti = new TemplateInfo();
					ti.setId(r.value1());
					ti.setName(r.value2());
					ti.setMimetype(r.value3().intValue());
					File f ;
					if(r.value4()!=null){
						ti.setDriveId(r.value4());
						//TODO GET FILE TO DRIVE SERVICE ACCOUNT!!!
						f = null;
					}
					else{
						f = new File("/tmp/"+ti.getName()+".xml"); 
						byte[] b = getXml(dslContext,ti.getId());
						try {
							org.apache.commons.io.FileUtils.writeByteArrayToFile(f,b);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					TemplateInfo aux = null;
					try {
						aux = Utils.readxml(f);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ti.setColumns(aux.getColumns());
					ti.setType(aux.getType());
					ti.setIsParent(false);
					ti.setDomainId(domainId);
					v.add(ti);
				});
				
				recordParent.stream().forEach(r -> {
					TemplateInfo ti = new TemplateInfo();
					ti.setId(r.value1());
					ti.setName(r.value2());
					ti.setMimetype(r.value3().intValue());
					File f ;
					if(r.value4()!=null){
						ti.setDriveId(r.value4());
						//TODO GET FILE TO DRIVE SERVICE ACCOUNT!!!
						f = null;
					}
					else{
						f = new File("/tmp/"+ti.getName()+".xml"); 
						byte[] b = getXml(dslContext,ti.getId());
						try {
							org.apache.commons.io.FileUtils.writeByteArrayToFile(f,b);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					TemplateInfo aux = null;
					try {
						aux = Utils.readxml(f);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ti.setColumns(aux.getColumns());
					ti.setType(aux.getType());
					ti.setIsParent(true);
					v.add(ti);
				});
				
				TemplateList tl = new TemplateList();
				tl.setList(v);
				return tl;
			}finally {
				if (connection != null)
					connection.close();	

			}
		
	}
	
	public static Integer insertTemplate(String domain, TemplateInfo ti, byte[] b,Integer domainId) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			
			Result<Record1<Integer>> reg = dslContext.select(ENTERPRISE.REGISTRY)
				.from(ENTERPRISE.join(DOMAIN).on(ENTERPRISE.DOMAIN.eq(DOMAIN.ID)))
				.where(DOMAIN.ID.eq(domainId)).fetch();
			
						
			return dslContext.insertInto(RATTACH,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID)
						.values(reg.get(0).value1(),domainId,null,(byte) MimeType.MIME_XML.ordinal(),ti.getName(),(byte) 15,null,(byte)0,null,b,null,null).returning(RATTACH.ID).fetchOne().getId();
	
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static byte[] getTemplate(String domain , Integer id) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			return getXml(dslContext, id);
			
		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static byte[] getXml(DSLContext dslContext, Integer id){
	
			return dslContext
					.select(RATTACH.DATA)
					.from(RATTACH)
					.where(RATTACH.ID.eq(id))
					.fetchOne().value1();

	}
	
	public static void removeTemplate(String domain,Integer id) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			dslContext.delete(RATTACH).where(RATTACH.ID.eq(id)).execute();
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void updateTemplate(String domain,TemplateInfo ti, Integer domainId, byte[] b) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(RATTACH).set(RATTACH.DESCRIPTION,ti.getName())
									.set(RATTACH.DATA,b)
							.where(RATTACH.ID.eq(ti.getId())).execute();
		} finally {
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
	
	public static void insertProducts(String domain, Integer domainId,Vector<ProductInfo> products) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			for(ProductInfo r : products){
				//get product 
				Record1<Integer> data = dslContext.select(PRODUCT.ID)
											.from(PRODUCT)
											.where(PRODUCT.CODE.eq(r.getProduct().getCode()).and(PRODUCT.DOMAIN.eq(domainId))).fetchOne();
				Integer productId;
				Byte inventoriable;
				if (r.getProduct().isInventoriable()) inventoriable = 1;
				else inventoriable = 0;
				
				Byte composition;
				if (r.getProduct().isComposition()) composition = 1;
				else composition = 0;
			
				Byte compositionPrice;
				if (r.getProduct().isCompositionPrice()) compositionPrice = 1;
				else compositionPrice = 0;
			
				Integer brandId;
				if(r.getProduct().getBrand()!=null) brandId = r.getProduct().getBrand().getId();
				else brandId = null;
			
				Integer categoryId;
				if(r.getProduct().getCategory()!=null) categoryId = r.getProduct().getCategory().getId();
				else categoryId = null;
				
				if(data!= null){
					productId = data.value1();
					if(r.getProduct().getTags() != null){
						dslContext.delete(PRODUCT_TAG).where(PRODUCT_TAG.PRODUCT.eq(productId));
					
						r.getProduct().getTags().stream().forEach(t->{
							dslContext.insertInto(PRODUCT_TAG, PRODUCT_TAG.DOMAIN, PRODUCT_TAG.PRODUCT, PRODUCT_TAG.TAG)
							.values(domainId, productId,t.getTag().getId());
						});
					}
		
					dslContext.update(PRODUCT).set(PRODUCT.NAME, r.getProduct().getName())
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

				}
				else{
					productId = dslContext.insertInto(PRODUCT, PRODUCT.DOMAIN,PRODUCT.NAME,PRODUCT.CODE,PRODUCT.BRAND,PRODUCT.CATEGORY,PRODUCT.INVENTORIABLE,PRODUCT.STATUS,PRODUCT.VAT,PRODUCT.RETENTION,PRODUCT.TYPE,PRODUCT.COMPOSITION,PRODUCT.COMPOSITION_PRICE,PRODUCT.SALES_ACCOUNT,PRODUCT.PURCHASE_ACCOUNT)
						.values(domainId,r.getProduct().getName(),r.getProduct().getCode(),brandId,categoryId, inventoriable,(byte) r.getProduct().getStatus().ordinal(),r.getProduct().getVat().getId(),r.getProduct().getRetention().getId(),(byte) r.getProduct().getType().ordinal(),composition,compositionPrice,null,null).returning(PRODUCT.ID).fetchOne().getId();
				
					if(r.getProduct().getTags() != null){
						r.getProduct().getTags().stream().forEach(t->{
							dslContext.insertInto(PRODUCT_TAG, PRODUCT_TAG.DOMAIN, PRODUCT_TAG.PRODUCT, PRODUCT_TAG.TAG)
								.values(domainId, productId,t.getTag().getId());
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
					
					if((barcode != null && barcode.equals(r.getItem().getBarcode())) || (details!= null && details.equals(details2))){
						dslContext.update(ITEM).set(ITEM.DESCRIPTION, r.getItem().getDescription())
							.set(ITEM.PRICE,r.getItem().getPrice())
							.set(ITEM.EXPENSES_PERCENT, r.getItem().getExpensesPercent())
							.set(ITEM.EXPENSES_FIXED, r.getItem().getExpensesFixed())
							.set(ITEM.PROFIT_PERCENT,r.getItem().getProfitPercent())
							.set(ITEM.PURCHASE_PRICE, r.getItem().getPurchasePrice())
							.set(ITEM.BARCODE, r.getItem().getBarcode())
						.where(ITEM.ID.eq(itemId)).execute();
						bool = true;
					}
				}
				if(!bool){
					itemId = dslContext.insertInto(ITEM, ITEM.DOMAIN,ITEM.PRODUCT, ITEM.DESCRIPTION, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE, ITEM.DETAIL,ITEM.DETAIL2,ITEM.DETAIL3)
							.values(domainId,productId,r.getItem().getDescription(),r.getItem().getPrice(),null,r.getItem().getExpensesPercent(),r.getItem().getExpensesFixed(),r.getItem().getProfitPercent(),r.getItem().getPurchasePrice(),(byte) 0,r.getItem().getBarcode(),r.getItem().getDetail(),r.getItem().getDetail2(),r.getItem().getDetail3()).returning(ITEM.ID).fetchOne().getId();
				}
			}
		} finally {
			if (connection != null)
				connection.close();
		}
		
		
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
	

	
	public static void insertStock(String domain, Integer domainId,Vector<StockInfo> stock) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			for(StockInfo s :stock){
				Result<Record1< Integer>> data = dslContext.select(ITEM.ID)
					.from(ITEM)
					.where(ITEM.BARCODE.eq(s.getProduct())).fetch();
				
				if(data.isEmpty()){
					data = dslContext.select(ITEM.ID)
							.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
							.where(PRODUCT.CODE.eq(s.getProduct())
									.and(ITEM.DETAIL.eq(s.getDetail()))
									.and(ITEM.DETAIL2.eq(s.getDetail2()))
									.and(ITEM.DETAIL3.eq(s.getDetail3()))).fetch();
				}
				if(!data.isEmpty()){
					Integer itemId = data.get(0).value1();
					Result<Record1<Double>> data2 = dslContext.select(STOCK.QUANTITY)
						.from(STOCK)
						.where(STOCK.ITEM.eq(itemId).and(STOCK.WAREHOUSE.eq(s.getTargetWarehouse().getId()))).fetch();
					Double quantity;
					if(data2.isEmpty())
						quantity = 0.0;
					else 
						quantity = data2.get(0).value1();
					
					Date d = new Date();
					Timestamp t = new Timestamp(d.getTime());
			
					Result<Record1<Integer>> n = dslContext.select(DSL.max(WAREHOUSE_TRANSFER.NUMBER))
						.from(WAREHOUSE_TRANSFER)
						.where(WAREHOUSE_TRANSFER.DOMAIN.eq(domainId).and(WAREHOUSE_TRANSFER.SERIES.eq(s.getSeries().getCode()))).fetch();
					Integer max;
					if(n.isEmpty() || n.get(0).value1()==null) max = 0;
					else max = n.get(0).value1(); //get max number (domain, serie)
					Integer next = max+1;
					
					Integer transferId = dslContext.insertInto(WAREHOUSE_TRANSFER,WAREHOUSE_TRANSFER.DOMAIN, WAREHOUSE_TRANSFER.SERIES,WAREHOUSE_TRANSFER.NUMBER, WAREHOUSE_TRANSFER.COMMENTS, WAREHOUSE_TRANSFER.ISSUE_TIME, WAREHOUSE_TRANSFER.SOURCE_WAREHOUSE, WAREHOUSE_TRANSFER.TARGET_WAREHOUSE)
							.values(domainId,s.getSeries().getCode(),next,s.getComments(),t,null,s.getTargetWarehouse().getId()).returning(WAREHOUSE_TRANSFER.ID).fetchOne().getId();

					dslContext.insertInto(WAREHOUSE_TRANSFER_DETAIL, WAREHOUSE_TRANSFER_DETAIL.DOMAIN, WAREHOUSE_TRANSFER_DETAIL.ITEM, WAREHOUSE_TRANSFER_DETAIL.WAREHOUSE_TRANSFER, WAREHOUSE_TRANSFER_DETAIL.QUANTITY)
							.values(domainId,itemId,transferId,s.getQuantity()-Math.abs(quantity)).execute();
					if(data2.isEmpty())
						dslContext.insertInto(STOCK, STOCK.DOMAIN, STOCK.WAREHOUSE, STOCK.ITEM, STOCK.QUANTITY)
								.values(domainId, s.getTargetWarehouse().getId(), itemId, s.getQuantity()).execute();
					else dslContext.update(STOCK)
							.set(STOCK.QUANTITY,s.getQuantity())
							.where(STOCK.ITEM.eq(itemId).and(STOCK.WAREHOUSE.eq(s.getTargetWarehouse().getId()))).execute();
					
				}
			}
			
		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	
	public static Boolean isItem(String domain, Integer domainId, String p) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1< Integer>> data = dslContext.select(ITEM.ID)
					.from(ITEM)
					.where(ITEM.BARCODE.eq(p)).fetch();
				
			if(data.isEmpty()){
				data = dslContext.select(ITEM.ID)
						.from(ITEM).join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
						.where(PRODUCT.CODE.eq(p)).fetch();
			}
			return !data.isEmpty();
			
		}finally {
			if (connection != null)
				connection.close();
			}
		
	}
	public static Vector<Warehouse> getWarehouse(String domain,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3< Integer, String,Integer>> data = dslContext.select(WAREHOUSE.ID,WAREHOUSE.NAME,WAREHOUSE.WORKPLACE)
				.from(WAREHOUSE)
				.where(WAREHOUSE.DOMAIN.eq(domainId)).fetch();
			
			Vector<Warehouse> v = new Vector<Warehouse>();
			
			for(Record3<Integer, String,Integer> r : data){
				Warehouse w = new Warehouse();
				w.setDomainId(domainId);
				w.setId(r.value1());
				w.setName(r.value2());
				w.setWorkplace(0);//
				v.add(w);
			}
			return v;

		} finally {
		if (connection != null)
			connection.close();
		}
	}
	
	public static Vector<Series> getSeries(String domain,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record3< Integer, String,String>> data = dslContext.select(SERIES.ID,SERIES.CODE,SERIES.DESCRIPTION)
				.from(SERIES)
				.where(SERIES.DOMAIN.eq(domainId)).fetch();
			
			Vector<Series> v = new Vector<Series>();
			
			for(Record3<Integer, String,String> r : data){
				Series s = new Series();
				s.setId(r.value1());
				s.setCode(r.value2());
				s.setDescription(r.value3());
				v.add(s);
			}
			return v;

		} finally {
		if (connection != null)
			connection.close();
		}
	}
	
	public static Boolean checkSeries(String domain, Integer domainId,Series s, Warehouse w) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record1< Integer>> data = dslContext.select(SERIES.ID)
				.from(WAREHOUSE)
					.join(WORKPLACE).on(WAREHOUSE.WORKPLACE.eq(WORKPLACE.ID))
					.join(SERIES).on(SERIES.SCOPE.eq(WORKPLACE.SCOPE))
				.where(SERIES.ID.eq(s.getId()).and(WAREHOUSE.ID.eq(w.getId()))).fetch();
			
			return !data.isEmpty();

		} finally {
		if (connection != null)
			connection.close();
		}
	}
	
	public static Item getItem(DSLContext dslContext, String domain, Integer id ){
		
		Result<Record5<String, String, String, String, Integer>> data = dslContext.select(ITEM.BARCODE, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3,ITEM.PRODUCT)
			.from(ITEM)
			.where(ITEM.ID.eq(id)).fetch();
		
		Item i = new Item();
		if(data.get(0).value1() != null)i.setBarcode(data.get(0).value1());
		if(data.get(0).value2() != null) i.setDetail(data.get(0).value2());
		else i.setDetail("");
		if(data.get(0).value3() != null) i.setDetail2(data.get(0).value3());
		else i.setDetail2("");
		if(data.get(0).value4() != null) i.setDetail3(data.get(0).value4());
		else i.setDetail3("");
		Product p = getProduct(dslContext, data.get(0).value5());
		i.setProduct(p);
		
		return i;
		
	}
	
	public static Product getProduct(DSLContext dslContext, Integer id ){
		
		Result<Record1<String>> data = dslContext.select(PRODUCT.CODE)
			.from(PRODUCT)
			.where(PRODUCT.ID.eq(id)).fetch();
		
		Product p = new Product();
		p.setId(id);
		p.setCode(data.get(0).value1());
		return p;
		
	}
	
	public static String[] getWarehouseComments(DSLContext dslContext, Integer id ){
		
		Result<Record2<String, String>> data = dslContext.select(WAREHOUSE_TRANSFER.COMMENTS,WAREHOUSE_TRANSFER.SERIES)
			.from(WAREHOUSE_TRANSFER)
			.where(WAREHOUSE_TRANSFER.TARGET_WAREHOUSE.eq(id)).fetch();
		String[] s = new String[2];

		if(data.get(0).value1()!=null) s[0] = data.get(0).value1();
		else s[0] = "";
		if(data.get(0).value2()!=null) s[1] = data.get(0).value2();
		else s[1] = "";
			
		return s;
		
	}
	
	public static Warehouse getWarehouse(DSLContext dslContext, Integer id ){
		
		Result<Record1<String>> data = dslContext.select(WAREHOUSE.NAME)
			.from(WAREHOUSE)
			.where(WAREHOUSE.ID.eq(id)).fetch();
		
		Warehouse w = new Warehouse();

		if(data.get(0).value1()!=null) w.setName(data.get(0).value1());
		else w.setName("");
		
			
		return w;
		
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
	
	public static Vector<StockInfo> getStocks(String domain, Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record4<Integer, Integer, Integer, Double>> data = dslContext.select(STOCK.ID, STOCK.ITEM, STOCK.WAREHOUSE, STOCK.QUANTITY)
				.from(STOCK)
				.where(STOCK.DOMAIN.eq(domainId)).fetch();
				
			Vector<StockInfo> v = new Vector<StockInfo>();
			for (Record4<Integer, Integer, Integer, Double> d : data) {
				StockInfo si = new StockInfo();
				Item i = getItem(dslContext,domain,d.value2());
				si.setDetail(i.getDetail());
				si.setDetail(i.getDetail2());
				si.setDetail(i.getDetail3());
				if(i.getBarcode()!= null) si.setProduct(i.getBarcode());
				else {
					si.setProduct(i.getProduct().getCode());
				}
				si.setQuantity(d.value4());
				String[] s = getWarehouseComments(dslContext, d.value3());
				Series ss = new Series();ss.setCode(s[1]);
				si.setSeries(ss);
				si.setComments(s[0]);
				si.setTargetWarehouse(getWarehouse(dslContext, d.value3()));
				v.add(si);
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
}
