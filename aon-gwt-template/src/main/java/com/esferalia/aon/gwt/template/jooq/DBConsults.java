package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Tax.TAX;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Tax;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.JooqSettings;
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
				
				Result<Record4<Integer, String, Byte, String>> record = dslContext
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.ID.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.eq((byte)15).and(RATTACH.DOMAIN.eq(domainId).or(DOMAIN.PARENT.eq(domainId))))
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
				.where(DOMAIN.NAME.eq(domain)).fetch();
			
						
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
			
			data.stream().forEach(r -> {
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
			
			data.stream().forEach(r -> {
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
						//TODO INSERT PRODUCT TAG 
				
				//TODO INSERT PRODUCT
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
				if(r.getProduct().getBrand()!=null) categoryId = r.getProduct().getCategory().getId();
				else categoryId = null;
				
				Integer productId = dslContext.insertInto(PRODUCT, PRODUCT.DOMAIN,PRODUCT.NAME,PRODUCT.CODE,PRODUCT.BRAND,PRODUCT.CATEGORY,PRODUCT.INVENTORIABLE,PRODUCT.STATUS,PRODUCT.VAT,PRODUCT.RETENTION,PRODUCT.TYPE,PRODUCT.COMPOSITION,PRODUCT.COMPOSITION_PRICE,PRODUCT.SALES_ACCOUNT,PRODUCT.PURCHASE_ACCOUNT)
						.values(domainId,r.getProduct().getName(),r.getProduct().getCode(),brandId,categoryId, inventoriable,(byte) r.getProduct().getStatus().ordinal(),r.getProduct().getVat().getId(),r.getProduct().getRetention().getId(),(byte) r.getProduct().getType().ordinal(),composition,compositionPrice,null,null).returning(PRODUCT.ID).fetchOne().getId();
				
				//TODO INSERT ITEM
				Integer itemId = dslContext.insertInto(ITEM, ITEM.DOMAIN,ITEM.PRODUCT, ITEM.DESCRIPTION, ITEM.PRICE, ITEM.STATUS, ITEM.EXPENSES_PERCENT, ITEM.EXPENSES_FIXED, ITEM.PROFIT_PERCENT, ITEM.PURCHASE_PRICE, ITEM.INTERNET, ITEM.BARCODE)
						.values(domainId,productId,r.getItem().getDescription(),r.getItem().getPrice(),null,r.getItem().getExpensesPercent(),r.getItem().getExpensesFixed(),r.getItem().getProfitPercent(),r.getItem().getPurchasePrice(),(byte) 0,r.getItem().getBarcode()).returning(ITEM.ID).fetchOne().getId();
			}
		} finally {
			if (connection != null)
				connection.close();
		}
	}
}
