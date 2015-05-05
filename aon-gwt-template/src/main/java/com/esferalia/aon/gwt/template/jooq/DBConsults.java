package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.io.File;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Result;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.template.server.Utils;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.occam.api.AONContext;




public class DBConsults {
	
	public static TemplateList getTemplates(String domain , Integer domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
				
				
				// DOMAIN + DOMAIN SON
				Result<Record4<Integer, String, Byte, String>> record = ctx.getDslContext()
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.ID.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.eq((byte)15).and(RATTACH.DOMAIN.eq(domainId).or(DOMAIN.PARENT.eq(domainId))))
						.fetch();
				
				// DOMAIN PARENT
				Result<Record4<Integer, String, Byte, String>> recordParent = ctx.getDslContext()
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.PARENT.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.eq((byte)15).and(DOMAIN.ID.eq(domainId)))
						.fetch();
				
				//default
				Result<Record4<Integer, String, Byte, String>> recordDefault = ctx.getDslContext()
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH)
						.where(RATTACH.TYPE.eq((byte)15).and(RATTACH.DOMAIN.eq(0)))
						.fetch();
				
				Vector<TemplateInfo> v = new Vector<TemplateInfo>();
				AONContext sctx = ctx;
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
						byte[] b = getXml(sctx.getDslContext(),ti.getId());
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
					ti.sethasWarehouse(aux.gethasWarehouse());
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
						byte[] b = getXml(sctx.getDslContext(),ti.getId());
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
				if(recordDefault.isEmpty()){
					TemplateInfo stockTemplate = new TemplateInfo();
					Vector<String> v2 = new Vector<String>();
					v2.add("Producto");v2.add("Nombre");v2.add("Cantidad");
					v2.add("Detalle 1");v2.add("Detalle 2");v2.add("Detalle 3");
					stockTemplate.setColumns(v2);
					stockTemplate.setDomain(domain);
					stockTemplate.setDomainId(0);
					stockTemplate.setName("Est\u00e1ndar");
					stockTemplate.setType("Stock");
					stockTemplate.sethasWarehouse(false);
					stockTemplate.setIsParent(true);
					Integer id = insertTemplate(domain, stockTemplate,Utils.newXmlFile(stockTemplate), 0);
					stockTemplate.setId(id);
					v.add(stockTemplate);
					
					/*TemplateInfo stockPurchaseTemplate = new TemplateInfo();
					v2 = new Vector<String>();
					v2.add("Centro de Trabajo");v2.add("Departamento");
					v2.add("Producto");v2.add("Nombre");v2.add("Cantidad");
					v2.add("Detalle 1");v2.add("Detalle 2");v2.add("Detalle 3");
					stockPurchaseTemplate.setColumns(v2);
					stockPurchaseTemplate.setDomain(domain);
					stockPurchaseTemplate.setDomainId(0);
					stockPurchaseTemplate.setName("Solicitud de Compra");
					stockPurchaseTemplate.setType("Catalogo");
					stockPurchaseTemplate.sethasWarehouse(false);
					stockPurchaseTemplate.setIsParent(true);
					id = insertTemplate(domain, stockPurchaseTemplate,Utils.newXmlFile(stockPurchaseTemplate), 0);
					stockPurchaseTemplate.setId(id);
					v.add(stockPurchaseTemplate);
					*/
					TemplateInfo productTemplate = new TemplateInfo();
					v2 = new Vector<String>();
					v2.add("Nombre");v2.add("Código");v2.add("Precio Coste");v2.add("Precio Venta Base");
					productTemplate.setColumns(v2);
					productTemplate.setDomain(domain);
					productTemplate.setDomainId(0);
					productTemplate.setName("Est\u00e1ndar");
					productTemplate.setType("Producto");
					productTemplate.sethasWarehouse(false);
					productTemplate.setIsParent(true);
					id = insertTemplate(domain, productTemplate,Utils.newXmlFile(productTemplate), 0);
					productTemplate.setId(id);
					v.add(productTemplate);
					
					TemplateInfo feeTemplate = new TemplateInfo();
					v2 = new Vector<String>();
					v2.add("Cliente");v2.add("Producto");v2.add("Cantidad");v2.add("Precio");v2.add("Descuento");
					v2.add("Fecha Inicio");v2.add("Fecha Facturaci\u00f3n");v2.add("Centro de Trabajo");
					feeTemplate.setColumns(v2);
					feeTemplate.setDomain(domain);
					feeTemplate.setDomainId(0);
					feeTemplate.setName("Est\u00e1ndar");
					feeTemplate.setType("Cuota");
					feeTemplate.sethasWarehouse(false);
					feeTemplate.setIsParent(true);
					id = insertTemplate(domain, feeTemplate,Utils.newXmlFile(feeTemplate), 0);
					feeTemplate.setId(id);
					v.add(feeTemplate);
				}
				else{
				recordDefault.stream().forEach(r -> {
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
						byte[] b = getXml(sctx.getDslContext(),ti.getId());
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
					ti.setDomainId(0);
					v.add(ti);
				});
				}
				
				TemplateList tl = new TemplateList();
				tl.setList(v);
				return tl;
			}finally {
				if (ctx != null) ctx.close();
			}
		
	}

	public static Integer insertTemplate(String domain, TemplateInfo ti, byte[] b,Integer domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Integer registry;
			if(domainId.equals(0)){
				Result<Record1<Integer>> reg = ctx.getDslContext().select(REGISTRY.ID)
						.from(REGISTRY)
						.where(REGISTRY.DOMAIN.eq(domainId)).fetch();
				registry = reg.get(0).value1();
			}
			else{
				Result<Record1<Integer>> reg = ctx.getDslContext().select(ENTERPRISE.REGISTRY)
						.from(ENTERPRISE.join(DOMAIN).on(ENTERPRISE.DOMAIN.eq(DOMAIN.ID)))
						.where(DOMAIN.ID.eq(domainId)).fetch();
				registry = reg.get(0).value1();
			}
			

			return ctx.getDslContext().insertInto(RATTACH,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID)
						.values(registry,domainId,null,(byte) MimeType.MIME_XML.ordinal(),ti.getName(),(byte) 15,null,(byte)0,null,b,null,null).returning(RATTACH.ID).fetchOne().getId();
	
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static byte[] getTemplate(String domain , Integer domainId, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			return getXml(ctx.getDslContext(), id);
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static byte[] getXml(DSLContext dslContext, Integer id){
	
			return dslContext
					.select(RATTACH.DATA)
					.from(RATTACH)
					.where(RATTACH.ID.eq(id))
					.fetchOne().value1();

	}
	
	public static void removeTemplate(String domain,Integer domainId, Integer id){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			ctx.getDslContext().delete(RATTACH).where(RATTACH.ID.eq(id)).execute();
			
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void updateTemplate(String domain,TemplateInfo ti, Integer domainId, byte[] b){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			ctx.getDslContext().update(RATTACH).set(RATTACH.DESCRIPTION,ti.getName())
									.set(RATTACH.DATA,b)
							.where(RATTACH.ID.eq(ti.getId())).execute();
		} finally {
			if (ctx != null) ctx.close();
		}
	}

	
	public static String getUsername(String domain,Integer domainId, Integer id){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Record1<String> data = ctx.getDslContext().select(USER.LOGIN)
				.from(USER)
				.where(USER.ID.eq(id))
				.and(USER.DOMAIN.eq(domainId))
				.fetchOne();
			
			return data.value1();
			
			
		} finally {
			if (ctx != null) ctx.close();
		}
	}
}
