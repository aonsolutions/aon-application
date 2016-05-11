package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Sales.SALES;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Record9;
import org.jooq.Result;

import com.esferalia.aon.gwt.template.server.Utils;
import com.esferalia.aon.gwt.template.server.marketplace.XMLUtils;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.Product;
import com.esferalia.aon.gwt.template.shared.RegistryAttachTag;
import com.esferalia.aon.gwt.template.shared.marketplace.AmazonDelivery;
import com.esferalia.aon.gwt.template.shared.marketplace.CarrierCode;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.jooq.tables.records.IattachRecord;
import com.esferalia.aon.jooq.tables.records.ProductRecord;
import com.esferalia.aon.jooq.tables.records.RattachTagRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.AttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.type.ShipmentStatus;



public class DBMarketplace {

	public static List<EcommerceProduct> getProductTemplatesList(Domain domain, User user){
		List<EcommerceProduct> list = new ArrayList<EcommerceProduct>();
		AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getDomainProperty().eq(domain.getId())
				.and(filter.getTypeProperty().eq((RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())))
				, AttachType.REGISTRY)
		.forEach(attach ->{
			try {
				EcommerceProduct ep = XMLUtils.readXml(attach.getData());
				list.add(ep);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		return list;
	}
	
	private static class OrderFiller implements Function<Record7<Integer, String, Integer, String, java.sql.Date, Object, Object>, Order> {
		@Override
		public Order apply(Record7<Integer, String, Integer, String, java.sql.Date, Object, Object> r) {
			return new Order()
					.setId(r.getValue(SALES.ID))
					.setSerie(r.getValue(SALES.SERIES))
					.setNumber(r.getValue(SALES.NUMBER))
					.setOrderId(r.getValue(SALES.PURCHASE_REFERENCE))
					.setCustomerName(r.value6() != null ? r.value6().toString() : "") 
					.setDate(r.getValue(SALES.ISSUE_DATE))
					.setDateStr(Utils.getDateStr(r.getValue(SALES.ISSUE_DATE)))
					.setSellerName(r.value7() != null ? r.value7().toString() : "");
		}
	}

	public static LinkedList<Order> getOrderList(Domain domain, String login){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Field<Object> customerName = ctx.getDslContext().select(REGISTRY.NAME)
					.from(REGISTRY)
					.where(REGISTRY.ID.eq(SALES.CUSTOMER)).asField();
			
			Field<Object> sellerName = ctx.getDslContext().select(REGISTRY.NAME)
					.from(REGISTRY)
					.where(REGISTRY.ID.eq(SALES.SELLER)).asField();
			
			return ctx.getDslContext().select(SALES.ID, SALES.SERIES, SALES.NUMBER, SALES.PURCHASE_REFERENCE, SALES.ISSUE_DATE, customerName, sellerName)
				.from(SALES)			
				.where(SALES.DOMAIN.eq(domain.getId()))
					.and(SALES.STATUS.eq(SalesStatus.SERVED.value()))
					.and(SALES.PURCHASE_REFERENCE.isNotNull())			
				.fetch()
				.stream().map(new OrderFiller())
				.collect(Collectors.toCollection(LinkedList::new));
			
		}finally{
			if(ctx != null)
				ctx.close();
		}
	}
	
	public static List<Order> getOrderDeliveryList(Domain domain, String login){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<Record9<Integer, String, Integer, String, Double, Timestamp, String, Integer, Integer>> result = 
								ctx.getDslContext().select(SALES.ID, SALES.SERIES, SALES.NUMBER, SALES.PURCHASE_REFERENCE,
												DELIVERY.TOTAL_PACKAGES, DELIVERY.STATUS_MODIFICATION_DATE, DELIVERY.TRACKING_NUMBER,
												DELIVERY.CARRIER, DELIVERY.ID)
								.from(SALES).join(DELIVERY).on(SALES.SERIES.eq(DELIVERY.SERIES).and(SALES.NUMBER.eq(DELIVERY.NUMBER)))
								.where(SALES.DOMAIN.eq(domain.getId()))
									.and(SALES.PURCHASE_REFERENCE.isNotNull())
									.and(DELIVERY.SHIPPING_STATUS.eq(ShipmentStatus.IN_AGENCY.value()))
								.fetch();

			List<Order> orderList = new ArrayList<Order>();
			result.stream().forEach(record ->{
				Order order = new Order();
				AmazonDelivery ad = new AmazonDelivery();
				order.setId(record.value1() != null?record.value1():0);
				order.setSerie(record.value2() != null?record.value2():"");
				order.setNumber(record.value3() != null?record.value3():0);
				order.setOrderId(record.value4() != null?record.value4():"");
				ad.setOrderId(record.value4() != null?record.value4():"");
				ad.setOrderItemId("");
				//ad.setQuantity(record.value5() != null?record.value5().intValue():0);
				ad.setShipDate(record.value6() != null?record.value6():new Date()); 
				String dateStr = Utils.getDateStr(record.value6() != null?record.value6():new Date());
				ad.setShipDateStr(dateStr);
				AONContext sctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
				if(record.value8() != null){
					Result<RegistryRecord> registryRecord = sctx.getDslContext().select().from(REGISTRY).where(REGISTRY.ID.eq(record.value8())).fetchInto(REGISTRY);
					CarrierCode cc = CarrierCode.getValue(registryRecord.get(0).getName());
					ad.setCarrierCode(cc);
					if(cc.equals(CarrierCode.OTRO)) ad.setCarrierName(registryRecord.get(0).getName());
				}
				ad.setTrackingNumber(record.value7() != null?record.value7():"");
				ad.setShipMethod("Estándar");
				order.setAmazonDelivery(ad);
				orderList.add(order);
				updateConfirmDelivery(sctx, record.getValue(DELIVERY.ID));
			});
			return orderList;
			
		}finally{
			if(ctx != null)
				ctx.close();
		}
	}
	
	public static void updateConfirmDelivery(AONContext ctx, Integer id){
		ctx.getDslContext().update(DELIVERY)
			.set(DELIVERY.SHIPPING_STATUS, ShipmentStatus.SHIPPING.value())
			.set(DELIVERY.STATUS_MODIFICATION_DATE, new Timestamp(new Date().getTime()))
			.where(DELIVERY.ID.eq(id)).execute();
	}
	
	
	public static String getCustomer(Domain domain, String login, Integer customerId){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Record1<String> record = ctx.getDslContext().select(REGISTRY.NAME)
			.from(REGISTRY)
			.where(REGISTRY.ID.eq(customerId))
			.limit(1).fetchOne();
			
			return (record != null && record.value1() != null) ? record.value1() : "";
		}finally{
			if(ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteTemplate(Domain domain, User user, String description){
		AON.delete(domain.getName(), domain.getId(), user.getLogin(), 
				filter -> filter.getDescriptionProperty().eq(description)
				.and(filter.getTypeProperty().eq((RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value()))
				.and(filter.getDomainProperty().eq(domain.getId())))
				, AttachType.REGISTRY);	
	}
	
	public static Seller getSeller(Domain domain, String login, Integer sellerId){
		return AON.getSeller(domain.getName(), domain.getId(), login, sellerId);
	}
	
	public static Tag insertMarketplaceTag(Domain domain, User user, Tag tag){
		return AON.addNewTag(domain.getId(), domain.getName(), user.getLogin(), tag);
	}
	
	public static void deleteMarketplaceTag(Domain domain, User user, Tag tag){
		AON.deleteTag(domain.getName(), domain.getId(), user.getLogin(), tag);
	}
	
	public static void updateMarketplaceTag(Domain domain, User user, Tag tag){
		AON.updateTag(domain.getName(), domain.getId(), user.getLogin(), tag);
	}
	
	public static LinkedList<Tag> getMarketplaceTagList(Domain domain, User user){
		return AON.getMatketplaceTagList(domain.getName(), domain.getId(), user.getLogin());
	}
	
	public static List<Product> getProductList(Domain domain, String login, Integer category){
		return getProductList(domain, login, category, null);
	}

	public static List<Product> getProductList(Domain domain, String login, Integer category, Boolean active){
		AONContext ctx = null;
		try {			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<ProductRecord> result = ctx.getDslContext().select()
					.from(PRODUCT)
					.where(PRODUCT.DOMAIN.eq(domain.getId()))
					.and(category!=null?PRODUCT.CATEGORY.equal(category):PRODUCT.CATEGORY.isNotNull())
					.and(active!=null?PRODUCT.STATUS.equal(active?ProductStatus.ACTIVE.value():ProductStatus.DISCONTINUED.value()):PRODUCT.STATUS.isNotNull())
					.fetchInto(PRODUCT);
			List<Product> list = new ArrayList<Product>();
			result.stream().forEach(record ->{
				Product product = new Product();
				product.setId(record.getId());
				product.setCode(record.getCode());
				product.setName(record.getName());
				list.add(product);
			});
			return list;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
	}

	public static List<RegistryAttachTag> getAttachTemplateTagList(Domain domain, String login, List<Integer> pTagList) {
		AONContext ctx = null;
		try {			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<RattachTagRecord> result = ctx.getDslContext().select()
					.from(RATTACH_TAG)
					.where(RATTACH_TAG.DOMAIN.eq(domain.getId()))
					.and(pTagList!=null?RATTACH_TAG.TAG.in(pTagList):RATTACH_TAG.TAG.isNotNull())
					.fetchInto(RATTACH_TAG);
			List<RegistryAttachTag> list = new ArrayList<RegistryAttachTag>();
			result.stream().forEach(record ->{
				RegistryAttachTag rat = new RegistryAttachTag();
				rat.setId(record.getId());
				rat.setTag(record.getTag());
				rat.setRattach(record.getRattach());
				list.add(rat);
			});
			return list;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
	}
	
	public static Attach getItemTemplateAttach(Domain domain, String login, String templateName, Product product) {
		AONContext ctx = null;
		try {			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<IattachRecord> result = ctx.getDslContext().select()
					.from(IATTACH).leftOuterJoin(ITEM).on(IATTACH.ITEM.equal(ITEM.ID))
					.where(IATTACH.DOMAIN.eq(domain.getId()))
					.and(IATTACH.DESCRIPTION.equal(templateName))
					.and(ITEM.ID.equal(getBaseItemId(domain, login, product.getId())))
					.fetchInto(IATTACH);
			Attach attach = null;
			if(result.isNotEmpty()){
				attach = new Attach();
				IattachRecord record = result.get(0);
				attach.setId(record.getId());
				attach.setData(record.getData());
			}
			return attach;
		} finally {
			if(ctx != null)
				ctx.close();
		}
	}
	
	public static Integer getBaseItemId(Domain domain, String login, Integer productId){
		Integer itemId = null;
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Record1<Integer> record = ctx.getDslContext()
					.select(ITEM.ID)
					.from(ITEM)
					.where(ITEM.PRODUCT.eq(productId).and(ITEM.SERIAL_NUMBER.isNull()))
					.fetchOne();
			itemId = record.value1();
		} finally {
			if(ctx != null)
				ctx.close();
		}
		return itemId;
	}
	
	public static boolean acceptProductValues(Domain domain, String login, String templateName, EcommerceProduct ecommerceProduct, Attach attach){
		byte[] data = null;
		try {
			data = XMLUtils.writeXml(ecommerceProduct);
		} catch (Throwable th) {
			data = null;
			th.printStackTrace();
		}
	
		if(data!=null){
			if(attach==null){
				attach = new Attach();
			}
			
			Integer productId = Integer.parseInt(ecommerceProduct.getProduct().getId());
			Integer itemId = getBaseItemId(domain, login, productId);
			
			if(itemId==null){
				throw new IllegalArgumentException("No se ha podido recuperar el producto base");
			}
			attach.setDomain(domain);
			attach.setMimeType(MimeType.XML);
			attach.setDescription(templateName);
			attach.setAttachType(AttachType.ITEM);
			attach.setAttachModule(itemId);
			attach.setType(AttachmentType.ECOMMERCE_PRODUCT.value());
			attach.setConfidential(false);
			attach.setData(data);
			if(attach.getId()==null){
				AON.insert(domain.getName(), domain.getId(), login, attach);
			} else {
				AON.update(domain.getName(), domain.getId(), login, attach);
			}
			return true;
		}
		return false;
	}
	
}
