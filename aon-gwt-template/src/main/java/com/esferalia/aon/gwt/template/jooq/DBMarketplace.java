package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Sales.SALES;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Result;

import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.carrier.enumeration.ShipmentStatus;
import com.esferalia.aon.gwt.template.server.Utils;
import com.esferalia.aon.gwt.template.server.marketplace.XMLUtils;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.marketplace.AmazonDelivery;
import com.esferalia.aon.gwt.template.shared.marketplace.CarrierCode;
import com.esferalia.aon.gwt.template.shared.marketplace.Order;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.SalesRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.User;



public class DBMarketplace {

	public static List<EcommerceProduct> getProductTemplatesList(Domain domain, User user){
		LinkedList<Attach> attachList = AON.getAttachList(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getDomainProperty().eq(domain.getId())
				.and(filter.getTypeProperty().eq(((byte) RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.ordinal())))
				, AttachType.REGISTRY);
		List<EcommerceProduct> list = new ArrayList<EcommerceProduct>();
		attachList.stream().forEach(attach->{
			try {
				EcommerceProduct ep = XMLUtils.readXml(attach.getData());
				list.add(ep);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return list;
	}
	
	public static List<Order> getOrderList(Domain domain, String login){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<SalesRecord> result = ctx.getDslContext().select()
										.from(SALES)
										.where(SALES.DOMAIN.eq(domain.getId()))
											.and(SALES.PURCHASE_REFERENCE.isNotNull())
										.fetchInto(SALES);
			
			List<Order> orderList = new ArrayList<Order>();
			result.stream().forEach(record ->{
				Order order = new Order();
				order.setId(record.getId());
				order.setSerie(record.getSeries());
				order.setNumber(record.getNumber());
				order.setOrderId(record.getPurchaseReference());
				String CustomerName = getCustomer(domain, login, record.getCustomer());
				order.setCustomerName(CustomerName);
				order.setDate(record.getIssueDate());
				String dateStr = Utils.getDateStr(record.getIssueDate());
				order.setDateStr(dateStr);
				orderList.add(order);
			});
			return orderList;
			
		}finally{
			if(ctx != null)
				ctx.close();
		}
	}
	
	public static List<Order> getOrderDeliveryList(Domain domain, String login){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Result<Record8<Integer, String, Integer, String, Double, Timestamp, String, Integer>> result = 
								ctx.getDslContext().select(SALES.ID, SALES.SERIES, SALES.NUMBER, SALES.PURCHASE_REFERENCE,
												DELIVERY.TOTAL_PACKAGES, DELIVERY.STATUS_MODIFICATION_DATE, DELIVERY.TRACKING_NUMBER, DELIVERY.CARRIER)
								.from(SALES).join(DELIVERY).on(SALES.SERIES.eq(DELIVERY.SERIES).and(SALES.NUMBER.eq(DELIVERY.NUMBER)))
								.where(SALES.DOMAIN.eq(domain.getId()))
									.and(SALES.PURCHASE_REFERENCE.isNotNull())
									.and(DELIVERY.SHIPPING_STATUS.eq((byte)ShipmentStatus.IN_AGENCY.ordinal()))
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
				if(record.value8() != null){
					AONContext sctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
					Result<RegistryRecord> registryRecord = sctx.getDslContext().select().from(REGISTRY).where(REGISTRY.ID.eq(record.value8())).fetchInto(REGISTRY);
					CarrierCode cc = CarrierCode.getValue(registryRecord.get(0).getName());
					ad.setCarrierCode(cc);
					if(cc.equals(CarrierCode.OTRO)) ad.setCarrierName(registryRecord.get(0).getName());
				}
				ad.setTrackingNumber(record.value7() != null?record.value7():"");
				ad.setShipMethod("Estándar");
				order.setAmazonDelivery(ad);
				orderList.add(order);
			});
			return orderList;
			
		}finally{
			if(ctx != null)
				ctx.close();
		}
	}
	
	
	public static String getCustomer(Domain domain, String login, Integer customerId){
		AONContext ctx = null;
		try{
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Record1<String> record = ctx.getDslContext().select(REGISTRY.NAME)
			.from(REGISTRY)
			.where(REGISTRY.ID.eq(customerId))
			.limit(1).fetchOne();
			
			return record.value1() != null ? record.value1() : "";
		}finally{
			if(ctx != null)
				ctx.close();
		}
	}
	
	public static void deleteTemplate(Domain domain, User user, String description){
		AON.delete(domain.getName(), domain.getId(), user.getLogin(), 
				filter -> filter.getDescriptionProperty().eq(description)
				.and(filter.getTypeProperty().eq(((byte) RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.ordinal()))
				.and(filter.getDomainProperty().eq(domain.getId())))
				, AttachType.REGISTRY);	
	}
	
	public static LinkedList<Tag> getMarketplaceTagList(Domain domain, User user){
		return AON.getMatketplaceTagList(domain.getName(), domain.getId(), user.getLogin());
	}
}
