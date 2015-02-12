package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Tag;
import com.code.aon.config.Tax;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.code.aon.product.Brand;
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductTag;
import com.esferalia.aon.gwt.template.server.ProductInfo;
import com.esferalia.aon.gwt.template.server.Utils;
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
}
