package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.Stock.STOCK;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
import com.esferalia.aon.gwt.template.shared.RegistryAttachTag;
import com.esferalia.aon.gwt.template.shared.marketplace.AmazonDelivery;
import com.esferalia.aon.gwt.template.shared.marketplace.CarrierCode;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.jooq.tables.records.IattachRecord;
import com.esferalia.aon.jooq.tables.records.ItemRecord;
import com.esferalia.aon.jooq.tables.records.ProductRecord;
import com.esferalia.aon.jooq.tables.records.RattachTagRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.ItemAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.ShipmentStatus;



public class DBMarketplace {

	public static List<EcommerceProduct> getProductTemplatesList(Domain domain, User user){
		List<EcommerceProduct> list = new ArrayList<EcommerceProduct>();
		AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getDomainProperty().eq(domain.getId())
				.and(filter.getTypeProperty().eq((RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value())))
				, AttachType.REGISTRY, true)
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
				.from(SALES).join(DELIVERY).on(SALES.SERIES.eq(DELIVERY.SERIES).and(SALES.NUMBER.eq(DELIVERY.NUMBER)))			
				.where(SALES.DOMAIN.eq(domain.getId()))
					.and(DELIVERY.SHIPPING_STATUS.eq(ShipmentStatus.IN_AGENCY.value()))
					//.and(SALES.STATUS.eq(SalesStatus.SERVED.value()))
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
				ad.setShipDate(record.value6() != null?record.value6():new Date()); 
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
		AON.deleteAttach(domain.getName(), domain.getId(), user.getLogin(), 
				filter -> filter.getDescriptionProperty().eq(description)
				.and(filter.getTypeProperty().eq((RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value()))
				.and(filter.getDomainProperty().eq(domain.getId())))
				, AttachType.REGISTRY);	
	}
	
	public static Seller getSeller(Domain domain, String login, Integer sellerId){
		return AON.getSeller(domain.getName(), domain.getId(), login, sellerId);
	}
	
	public static Tag insertMarketplaceTag(Domain domain, User user, Tag tag){
		return AON.insertTag(domain.getName(), domain.getId(), user.getLogin(), tag);
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
	
	public static List<OldProduct> getProductList(Domain domain, String login, Integer category){
		return getProductList(domain, login, category, null, null, null);
	}

	public static List<OldProduct> getProductList(Domain domain, String login, Integer category, Boolean active, Boolean sales, Boolean serializable){
		AONContext ctx = null;
		try {			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<ProductRecord> result = ctx.getDslContext().select()
					.from(PRODUCT)
					.where(PRODUCT.DOMAIN.eq(domain.getId()))
					.and(PRODUCT.TYPE.in( new Byte[] {ProductType.SERVICE.value(), ProductType.COMMERCIAL_PRODUCT.value()}))
					.and(serializable!=null?PRODUCT.SERIALIZABLE.equal((byte) (serializable ? 1 : 0 )):PRODUCT.SERIALIZABLE.isNotNull())
					.and(sales!=null?PRODUCT.KIND.in( new Byte[] {ProductKind.SALE.value(), ProductKind.SALE_PURCHASE.value()}):PRODUCT.KIND.isNotNull())
					.and(category!=null?PRODUCT.CATEGORY.equal(category):PRODUCT.CATEGORY.isNotNull())
					.and(active!=null?PRODUCT.STATUS.equal(active?ProductStatus.ACTIVE.value():ProductStatus.DISCONTINUED.value()):PRODUCT.STATUS.isNotNull())
					.orderBy(PRODUCT.NAME)
					.fetchInto(PRODUCT);
			List<OldProduct> list = new ArrayList<OldProduct>();
			result.stream().forEachOrdered(record ->{
				OldProduct product = new OldProduct();
				product.setId(record.getId());
				product.setCode(record.getCode());
				product.setName(record.getName());
				product.setBrandName(record.getBrand() != null? AON.getBrand(domain.getName(), domain.getId(), login, record.getBrand()).getName():"");
				list.add(product);
			});
			return list;
		} finally {
			if(ctx != null)
				ctx.close();
		}
	}
	
	public static List<OldItem> getMarketItemList(Domain domain, String login, Integer category, Boolean active, Boolean sales){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			List<OldItem> list = new ArrayList<>();
			
			List<OldProduct> serialProducts = getProductList(domain, login, category, active, sales, true);
			fillItemList(ctx, domain, list, serialProducts, true);
			
			List<OldProduct> noSerialProducts = getProductList(domain, login, category, active, sales, false);
			fillItemList(ctx, domain, list, noSerialProducts, null);
			
			Collections.sort(list, (OldItem o1, OldItem o2) -> o1.getProduct().getName().compareTo(o2.getProduct().getName()));
			
			return list;
			
		} finally {
			if(ctx != null)
				ctx.close();
		}
	}
	
	private static void fillItemList(AONContext ctx, Domain domain, List<OldItem> list, List<OldProduct> products, Boolean serial){
		List<Integer> productIds = products.stream().map(OldProduct::getId).collect(Collectors.toList());
		Result<ItemRecord> result = ctx.getDslContext().select()
				.from(ITEM)
				.where(ITEM.DOMAIN.eq(domain.getId()))
				.and(serial!=null?(serial?ITEM.SERIAL_NUMBER.isNull():ITEM.SERIAL_NUMBER.isNotNull()):ITEM.ID.isNotNull())
				.and(ITEM.PRODUCT.in(productIds))
				.fetchInto(ITEM);
		result.stream().forEachOrdered(record ->{
			OldItem item = new OldItem();
			item.setId(record.getId());
			item.setDetail(record.getDetail());
			item.setDetail2(record.getDetail2());
			item.setDetail3(record.getDetail3());
			item.setProduct(products.stream().filter(p -> (p.getId().equals(record.getProduct()))).findFirst().get());
			item.setBarcode(record.getBarcode());
			item.setDescription(record.getDescription());
			item.setPrice(record.getPrice());
			list.add(item);
		});
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
	
	public static Attach getItemTemplateAttach(Domain domain, String login, String templateName, OldItem item) {
		AONContext ctx = null;
		try {			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<IattachRecord> result = ctx.getDslContext().select()
					.from(IATTACH).leftOuterJoin(ITEM).on(IATTACH.ITEM.equal(ITEM.ID))
					.where(IATTACH.DOMAIN.eq(domain.getId()))
					.and(IATTACH.DESCRIPTION.equal(templateName))
					.and(ITEM.ID.equal(item.getId()))
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
			Result<ItemRecord> result = ctx.getDslContext()
					.select()
					.from(ITEM)
					.where(ITEM.PRODUCT.eq(productId).and(ITEM.SERIAL_NUMBER.isNull()))
					.fetchInto(ITEM);
			if( result.size() == 1 ){
				ItemRecord record = result.get(0);
				itemId = record.getId();
			} else {
				itemId = null;
			}
		} finally {
			if(ctx != null)
				ctx.close();
		}
		return itemId;
	}
	
	public static Double getItemStock(Domain domain, String login, Integer itemId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<Record1<Double>> result = ctx.getDslContext().select(STOCK.QUANTITY).from(STOCK).where(STOCK.ITEM.eq(itemId)).fetch();
			Double d = 0.0;
			for (Record1<Double> r : result) 
				d += r.getValue(STOCK.QUANTITY);
			return d;
		} finally {
			if(ctx != null)
				ctx.close();
		}
	}
	
	public static String getItemImageUrl(Domain domain, String login, Integer itemId, Integer i) {
		LinkedList<Attach> attach = AON.getAttachList(domain.getName(), domain.getId(), login,
				f -> f.getAttachModuleProperty().eq(itemId).and(f.getTypeProperty().eq(ItemAttachmentType.IMAGE.value())), AttachType.ITEM);
		if(attach != null && i<attach.size())
			return domain.getName()+"/aonItemImage/"+attach.get(i).getId()+"."+attach.get(i).getMimeType().getExtension();
		else return "";
	}
	
	public static boolean acceptProductValues(Domain domain, String login, OldItem item, String templateName, EcommerceProduct ecommerceProduct, Attach attach){
		/* TODO VARIABLE SISTEMA
		ecommerceProduct.getProductData().getEcommerce().stream().forEach(r -> {
			if(r.getValue().contains("{brand}"))
				r.setValue(r.getValue().replace("{brand}", item.getProduct().getBrandName()!= null? item.getProduct().getBrandName():""));
			if(r.getValue().contains("{code}"))
				r.setValue(r.getValue().replace("{code}", item.getProduct().getCode() != null?item.getProduct().getCode():""));
			if(r.getValue().contains("{title}"))
				r.setValue(r.getValue().replace("{title}", item.getProduct().getName()!= null?item.getProduct().getName():""));
			if(r.getValue().contains("{barcode}"))
				r.setValue(r.getValue().replace("{barcode}", item.getBarcode()!= null?item.getBarcode():""));		
			if(r.getValue().contains("{detail}"))
				r.setValue(r.getValue().replace("{detail}", item.getDetail()!= null?item.getDetail():""));
			if(r.getValue().contains("{detail2}"))
				r.setValue(r.getValue().replace("{detail2}", item.getDetail2()!= null?item.getDetail2():""));
			if(r.getValue().contains("{detail3}"))
				r.setValue(r.getValue().replace("{detail3}", item.getDetail3()!= null?item.getDetail3():""));
			if(r.getValue().contains("{sku}")){
				String code = item.getProduct().getCode()!= null?item.getProduct().getCode():"";
				String detail = item.getDetail()!= null? item.getDetail():"";
				String detail2 = item.getDetail2()!= null? item.getDetail2():"";
				String detail3 = item.getDetail3()!= null? item.getDetail3():"";
				r.setValue(r.getValue().replace("{sku}", code+detail+detail2+detail3));
			}
			if(r.getValue().contains("{description}"))
				r.setValue(r.getValue().replace("{description}", item.getDescription()!= null? item.getDescription():""));
			if(r.getValue().contains("{price}"))
				r.setValue(r.getValue().replace("{price}", String.valueOf(item.getPrice())));
			if(r.getValue().contains("{stock}"))
				r.setValue(r.getValue().replace("{stock}", getItemStock(domain, login, item.getId()).toString()));
			
			if(r.getValue().contains("{image1}"))
				r.setValue(r.getValue().replace("{image1}", getItemImageUrl(domain, login, item.getId(),0)));
			if(r.getValue().contains("{image2}"))
				r.setValue(r.getValue().replace("{image2}", getItemImageUrl(domain, login, item.getId(),1)));
			if(r.getValue().contains("{image3}"))
				r.setValue(r.getValue().replace("{image3}", getItemImageUrl(domain, login, item.getId(),2)));
			if(r.getValue().contains("{image4}"))
				r.setValue(r.getValue().replace("{image4}", getItemImageUrl(domain, login, item.getId(),3)));
			if(r.getValue().contains("{image5}"))
				r.setValue(r.getValue().replace("{image5}", getItemImageUrl(domain, login, item.getId(),4)));

		});
		*/
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
			
			attach.setDomain(domain);
			attach.setMimeType(MimeType.XML);
			attach.setDescription(templateName);
			attach.setAttachType(AttachType.ITEM);
			attach.setAttachModule(item.getId());
			attach.setType(ItemAttachmentType.ECOMMERCE_PRODUCT.value());
			attach.setConfidential(false);
			attach.setData(data);
			if(attach.getId()==null){
				AON.insertAttach(domain.getName(), domain.getId(), login, attach);
			} else {
				AON.updateAttach(domain.getName(), domain.getId(), login, attach);
			}
			return true;
		}
		return false;
	}
	
}
