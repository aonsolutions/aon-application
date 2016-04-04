package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Hotel.HOTEL;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.Result;

import com.code.aon.AonVersion;
import com.esferalia.aon.gwt.template.server.Utils;
import com.esferalia.aon.gwt.template.shared.Hotel;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.marketplace.AmazonDelivery;
import com.esferalia.aon.jooq.tables.records.DeliveryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.ShipmentStatus;




public class DBConsults {
	
	public static TemplateList getTemplates(Domain domain, User user){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
				
				
				// DOMAIN + DOMAIN SON
				Result<Record4<Integer, String, Byte, String>> record = ctx.getDslContext()
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.ID.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.eq((byte)15).and(RATTACH.DOMAIN.eq(domain.getId()).or(DOMAIN.PARENT.eq(domain.getId()))))
						.fetch();
				
				// DOMAIN PARENT
				Result<Record4<Integer, String, Byte, String>> recordParent = ctx.getDslContext()
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.PARENT.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.eq((byte)15).and(DOMAIN.ID.eq(domain.getId())))
						.fetch();
				
				//default
				Result<Record4<Integer, String, Byte, String>> recordDefault = ctx.getDslContext()
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.DRIVE_ID)
						.from(RATTACH)
						.where(RATTACH.TYPE.eq((byte)15).and(RATTACH.DOMAIN.eq(0)))
						.fetch();
				
				Vector<TemplateInfo> v = new Vector<TemplateInfo>();
				record.stream().forEach(r -> {
					TemplateInfo ti = new TemplateInfo();
					ti.setId(r.value1());
					ti.setName(r.value2());
					ti.setMimetype(r.value3().intValue());
					byte[] b;
					if(r.value4()!=null){
						ti.setDriveId(r.value4());
						//TODO GET FILE TO DRIVE SERVICE ACCOUNT!!!
						b = null;
					}
					else{ 
						b = getXml(domain, user, ti.getId());
					}
					TemplateInfo aux = null;
					try {
						aux = Utils.readxml(new ByteArrayInputStream(b));
					} catch (Exception e) {
						e.printStackTrace();
					}
					ti.sethasWarehouse(aux.gethasWarehouse());
					ti.setColumns(aux.getColumns());
					ti.setType(aux.getType());
					ti.setIsParent(false);
					ti.setDomainId(domain.getId());
					v.add(ti);
				});
				
				recordParent.stream().forEach(r -> {
					TemplateInfo ti = new TemplateInfo();
					ti.setId(r.value1());
					ti.setName(r.value2());
					ti.setMimetype(r.value3().intValue());
					byte[] b;
					if(r.value4()!=null){
						ti.setDriveId(r.value4());
						//TODO GET FILE TO DRIVE SERVICE ACCOUNT!!!
						b = null;
					}
					else{
						b = getXml(domain, user, ti.getId());
					}
					TemplateInfo aux = null;
					try {
						aux = Utils.readxml(new ByteArrayInputStream(b));
					} catch (Exception e) {
						e.printStackTrace();
					}
					ti.setColumns(aux.getColumns());
					ti.setType(aux.getType());
					ti.setIsParent(true);
					v.add(ti);
				});
				Boolean version = false;
				if(recordDefault.isNotEmpty()){
					byte[] b = getXml(domain, user, recordDefault.get(0).value1());
					TemplateInfo aux = null;
					try {
						aux = Utils.readxmlWithVersion(new ByteArrayInputStream(b));
					} catch (Exception e) {
						e.printStackTrace();
					}
					String ver = AonVersion.VERSION;
					version = ver.compareTo(aux.getVersion()) == 1;
					
					if(version){
						DBConsults.deleteDefaultTemplates(ctx);
					}
				}
				if(recordDefault.isEmpty() || version){
					
					Result<Record1<Integer>> data = ctx.getDslContext()
							.select(DOMAIN.ID)
							.from(DOMAIN)
							.where(DOMAIN.ID.eq(0))
							.fetch();
					if (data.isNotEmpty()){
						
						TemplateInfo stockTemplate = new TemplateInfo();
						Vector<String> v2 = new Vector<String>();
						v2.add("Producto");v2.add("Nombre");v2.add("Cantidad");
						v2.add("Detalle 1");v2.add("Detalle 2");v2.add("Detalle 3");
						stockTemplate.setColumns(v2);
						stockTemplate.setDomain(domain.getName());
						stockTemplate.setDomainId(0);
						stockTemplate.setName("Est\u00e1ndar-Stock");
						stockTemplate.setType("Stock");
						stockTemplate.sethasWarehouse(false);
						stockTemplate.setIsParent(true);
						stockTemplate.setVersion(AonVersion.VERSION);
						Integer id = insertTemplate(new Domain().setId(0).setName(domain.getName()),
								stockTemplate,Utils.newXmlFileWithVersion(stockTemplate), user.getLogin());
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
						v2.add("Detalle 1");v2.add("Detalle 2");v2.add("Detalle 3");
						productTemplate.setColumns(v2);
						productTemplate.setDomain(domain.getName());
						productTemplate.setDomainId(0);
						productTemplate.setName("Est\u00e1ndar-Producto");
						productTemplate.setType("Producto");
						productTemplate.sethasWarehouse(false);
						productTemplate.setIsParent(true);
						productTemplate.setVersion(AonVersion.VERSION);
						id = insertTemplate(new Domain().setId(0).setName(domain.getName()),
								productTemplate,Utils.newXmlFileWithVersion(productTemplate), user.getLogin());
						productTemplate.setId(id);
						v.add(productTemplate);
						
						TemplateInfo feeTemplate = new TemplateInfo();
						v2 = new Vector<String>();
						v2.add("Cliente");v2.add("Producto");v2.add("Cantidad");v2.add("Precio");v2.add("Descuento");
						v2.add("Fecha Inicio");v2.add("Fecha Facturaci\u00f3n");v2.add("Centro de Trabajo");
						feeTemplate.setColumns(v2);
						feeTemplate.setDomain(domain.getName());
						feeTemplate.setDomainId(0);
						feeTemplate.setName("Est\u00e1ndar-Cuota");
						feeTemplate.setType("Cuota");
						feeTemplate.sethasWarehouse(false);
						feeTemplate.setIsParent(true);
						feeTemplate.setVersion(AonVersion.VERSION);
						id = insertTemplate(new Domain().setId(0).setName(domain.getName()),
								feeTemplate,Utils.newXmlFileWithVersion(feeTemplate), user.getLogin());
						feeTemplate.setId(id);
						v.add(feeTemplate);
						
						TemplateInfo inventoryTemplate1 = new TemplateInfo();
						v2 = new Vector<String>();
						v2.add("Producto");v2.add("Nombre");v2.add("Categor\u00eda");v2.add("Recuento");
						v2.add("Detalle 1");v2.add("Detalle 2");v2.add("Detalle 3");
						inventoryTemplate1.setColumns(v2);
						inventoryTemplate1.setDomain(domain.getName());
						inventoryTemplate1.setDomainId(0);
						inventoryTemplate1.setName("Est\u00e1ndar-Inventario-Cerrado");
						inventoryTemplate1.setType("Inventario Cerrado");
						inventoryTemplate1.sethasWarehouse(false);
						inventoryTemplate1.setIsParent(true);
						inventoryTemplate1.setVersion(AonVersion.VERSION);
						id = insertTemplate(new Domain().setId(0).setName(domain.getName()),
								inventoryTemplate1,Utils.newXmlFileWithVersion(inventoryTemplate1), user.getLogin());
						inventoryTemplate1.setId(id);
						v.add(inventoryTemplate1);
						
						TemplateInfo inventoryTemplate2 = new TemplateInfo();
						v2 = new Vector<String>();
						v2.add("Producto");v2.add("Nombre");v2.add("Categor\u00eda");v2.add("Inventario");v2.add("Coste");
						v2.add("Total");v2.add("Detalle 1");v2.add("Detalle 2");v2.add("Detalle 3");
						inventoryTemplate2.setColumns(v2);
						inventoryTemplate2.setDomain(domain.getName());
						inventoryTemplate2.setDomainId(0);
						inventoryTemplate2.setName("Est\u00e1ndar-Inventario-Valorado");
						inventoryTemplate2.setType("Inventario Valorado");
						inventoryTemplate2.sethasWarehouse(false);
						inventoryTemplate2.setIsParent(true);
						inventoryTemplate2.setVersion(AonVersion.VERSION);
						id = insertTemplate(new Domain().setId(0).setName(domain.getName()),
								inventoryTemplate2,Utils.newXmlFileWithVersion(inventoryTemplate2), user.getLogin());
						inventoryTemplate2.setId(id);
						v.add(inventoryTemplate2);
					}
				}
				else{
				recordDefault.stream().forEach(r -> {
					TemplateInfo ti = new TemplateInfo();
					ti.setId(r.value1());
					ti.setName(r.value2());
					ti.setMimetype(r.value3().intValue());
					byte[] b;
					if(r.value4()!=null){
						ti.setDriveId(r.value4());
						//TODO GET FILE TO DRIVE SERVICE ACCOUNT!!!
						b = null;
					}
					else{
						b = getXml(domain, user, ti.getId());
					}
					TemplateInfo aux = null;
					try {
						aux = Utils.readxml(new ByteArrayInputStream(b));
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

	public static Integer insertTemplate(Domain domain, TemplateInfo ti, byte[] b, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Integer registry;
			if(domain.getId().equals(0)){
				Result<Record1<Integer>> reg = ctx.getDslContext().select(REGISTRY.ID)
						.from(REGISTRY)
						.where(REGISTRY.DOMAIN.eq(domain.getId())).fetch();
				registry = reg.get(0).value1();
			}
			else{
				Result<Record1<Integer>> reg = ctx.getDslContext().select(ENTERPRISE.REGISTRY)
						.from(ENTERPRISE.join(DOMAIN).on(ENTERPRISE.DOMAIN.eq(DOMAIN.ID)))
						.where(DOMAIN.ID.eq(domain.getId())).fetch();
				registry = reg.get(0).value1();
			}
			

			return ctx.getDslContext().insertInto(RATTACH,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID)
						.values(registry,domain.getId(),null, MimeType.XML.value(),ti.getName(),(byte) 15,null,(byte)0,null,b,null,null).returning(RATTACH.ID).fetchOne().getId();
	
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static byte[] getTemplate(Domain domain, User user, Integer attachId) {
		return getXml(domain, user, attachId);
	}
	
	public static byte[] getXml(Domain domain, User user, Integer attachId){
		return AON.getAttach(domain.getName(), domain.getId(), user.getLogin(), 
				filter -> filter.getIdProperty().eq(attachId), AttachType.REGISTRY).getData();
	}
	
	public static void removeTemplate(Domain domain, User user, Integer attachId){
		AON.delete(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getIdProperty().eq(attachId), AttachType.REGISTRY);
	}
	
	public static void updateTemplate(Domain domain,TemplateInfo ti, byte[] b, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			ctx.getDslContext().update(RATTACH).set(RATTACH.DESCRIPTION,ti.getName())
									.set(RATTACH.DATA,b)
							.where(RATTACH.ID.eq(ti.getId())).execute();
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void deleteDefaultTemplates(AONContext ctx){
		ctx.getDslContext()
			.delete(RATTACH)
			.where(RATTACH.TYPE.eq((byte)15))
			.and(RATTACH.DOMAIN.eq(0))
			.execute();
	}
	
	
	public static Vector<Hotel> getHotels(Domain domain, User user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record2<Integer, String>> result = ctx.getDslContext().select(WORKPLACE.ID, WORKPLACE.DESCRIPTION )
				.from(HOTEL).join(WORKPLACE).on(HOTEL.WORKPLACE.eq(WORKPLACE.ID))
				.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(WORKPLACE.SCOPE))
				.where(WORKPLACE.DOMAIN.eq(domain.getId()))
				.and(USER_SCOPE.USER_ID.eq(user.getId()))
				.and(WORKPLACE.ACTIVE.eq((byte)1))
				.orderBy(WORKPLACE.DESCRIPTION)
				.fetch();
			
			Vector<Hotel> hs = new Vector<Hotel>();
			
			result.stream().forEach(r ->{
				Hotel h = new Hotel();
				h.setDomain(domain.getId());
				h.setId(r.value1());
				h.setWorkplaceId(r.value1());
				h.setName(r.value2());
				hs.add(h);
			});
			return hs;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static List<AmazonDelivery> getDeliveries(Domain domain, String login){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<DeliveryRecord> result = ctx.getDslContext().select()
				.from(DELIVERY)
				.where(DELIVERY.DOMAIN.eq(domain.getId()))
				.and(DELIVERY.SHIPPING_STATUS.eq(ShipmentStatus.IN_AGENCY.value()))
				.fetchInto(DELIVERY);
			
			List<AmazonDelivery> list = new ArrayList<AmazonDelivery>();
			result.stream().forEach(dr ->{
				AmazonDelivery ad = new AmazonDelivery();
				ad.setOrderId("");
				ad.setOrderItemId("");
				ad.setQuantity(dr.getTotalPackages().intValue());
				ad.setShipDate(dr.getStatusModificationDate());
				ad.setShipDateStr(dr.getStatusModificationDate());
				//ad.setCarrierCode();
				//ad.setCarrierName();
				ad.setTrackingNumber(dr.getTrackingNumber());
				ad.setShipMethod("");
				list.add(ad);
			});
			return list;
		} finally {
			if (ctx != null) ctx.close();
		}
	}
}
