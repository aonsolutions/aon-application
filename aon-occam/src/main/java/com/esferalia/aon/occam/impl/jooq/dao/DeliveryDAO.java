package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DeliveryDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DeliveryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.PDeliveryDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.RDeliveryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO.ProductPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DeliveryDetailPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DeliveryPropertiesDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;


public class DeliveryDAO {
	
	private static final DeliveryDetailPropertiesDAO DELIVERY_DETAIL_PROPERTIES = new DeliveryDetailPropertiesDAO();
	private static final DeliveryPropertiesDAO DELIVERY_PROPERTIES = new DeliveryPropertiesDAO();
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();

	// -------------------- DELIVERY
	
	public static Stream<Delivery> getDeliveryStream(AONContext ctx, DeliveryFilter filter){
		return DELIVERY_PROPERTIES.build(ctx.getDslContext()
				.select()
				.from(DELIVERY)
				.join(REGISTRY).on(REGISTRY.ID.eq(DELIVERY.CUSTOMER))
			,filter).fetch().stream().map(new RDeliveryFiller());
	}
	
	public static Delivery insertDelivery(AONContext ctx, Delivery delivery) {
		ctx.checkWrite();
		return ctx.getDslContext()
				.insertInto(DELIVERY, DELIVERY.DOMAIN, DELIVERY.PROJECT,
						DELIVERY.SERIES, DELIVERY.NUMBER, DELIVERY.CUSTOMER,
						DELIVERY.ADDRESS, DELIVERY.ISSUE_TIME,
						DELIVERY.PAY_METHOD, DELIVERY.SECURITY_LEVEL,
						DELIVERY.STATUS, DELIVERY.COMMENTS, DELIVERY.REMARKS,
						DELIVERY.WORKPLACE, DELIVERY.SCOPE,
						DELIVERY.NUMBER_OF_PYMNTS,
						DELIVERY.DAYS_TO_FIRST_PYMNT,
						DELIVERY.DAYS_BETWEEN_PYMNTS, DELIVERY.PYMNT_DAYS,
						DELIVERY.BANK_ACCOUNT, DELIVERY.BANK_ALIAS,
						DELIVERY.BIC, 
						DELIVERY.CARRIER, DELIVERY.CARRIER_PACKING,
						DELIVERY.NUMBER_PLATE, DELIVERY.DRIVER, DELIVERY.DRIVER_DOCUMENT,
						DELIVERY.TOTAL_PACKAGES, DELIVERY.TOTAL_WEIGHT,
						DELIVERY.SHIPPING_ALTERNATIVE_ADDRESS,
						DELIVERY.SHIPPING_ALTERNATIVE_ADDRESS2,
						DELIVERY.SHIPPING_ALTERNATIVE_ZIP,
						DELIVERY.SHIPPING_ALTERNATIVE_CITY,
						DELIVERY.SHIPPING_ALTERNATIVE_PHONE,
						DELIVERY.SHIPPING_ALTERNATIVE_RECIPIENT,
						DELIVERY.SHIPPING_CONTACT, DELIVERY.SHIPPING_PERIOD,
						DELIVERY.TRACKING_NUMBER, DELIVERY.SHIPPING_STATUS,
						DELIVERY.STATUS_MODIFICATION_DATE,
						DELIVERY.CREATION_USER, DELIVERY.CREATION_DATE,
						DELIVERY.MODIFICATION_USER, DELIVERY.MODIFICATION_DATE)
				.values(delivery.getDomain(), delivery.getProject().getId(),
						delivery.getSeries(), delivery.getNumber(),
						delivery.getCustomer(), delivery.getAddress(),
						delivery.getIssueTime(), delivery.getPayMethod(),
						delivery.getSecurityLevel(), delivery.getStatus().ordinal(),
						delivery.getComments(), delivery.getRemarks(),
						delivery.getWorkplace(), delivery.getScope(),
						delivery.getNumberOfPymnts(),
						delivery.getDaysToFirstPymnt(),
						delivery.getDaysBetweenPymnt(),
						delivery.getPymntDays(), delivery.getBankAccount(),
						delivery.getBankAlias(), delivery.getBic(),
						delivery.getCarrier(), delivery.getCarrierPacking(),
						delivery.getNumberPlate(), delivery.getDriver(),
						delivery.getDriverDocument(), delivery.getTotalPackages(),
						delivery.getTotalWeight(), delivery.getShippingAlternativeAddress(),
						delivery.getShippingAlternativeAddress2(), delivery.getShippingAlternativeZip(),
						delivery.getShippingAlternativeCity(), delivery.getShippingAlternativePhone(),
						delivery.getShippingAlternativeRecipient(), delivery.getShippingContact(),
						delivery.getShippingPeriod(), delivery.getTrackingNumber(),
						delivery.getShippingStatus(), delivery.getStatusModificationDate(),
						ctx.getUser(), AonDateUtils.toTimestamp(new Date()),
						ctx.getUser(), AonDateUtils.toTimestamp(new Date()))
				.returning().fetch().stream().map(new DeliveryFiller()).findFirst().orElse(new Delivery());
	}
	
	public static Delivery updateDelivery(AONContext ctx, Delivery delivery, DeliveryFilter filter) {
		ctx.checkWrite();		
		return ctx.getDslContext().update(DELIVERY)
			.set(DELIVERY.DOMAIN, delivery.getDomain())
			.set(DELIVERY.PROJECT, delivery.getProject().getId())
			.set(DELIVERY.SERIES, delivery.getSeries())
			.set(DELIVERY.NUMBER, delivery.getNumber())
			.set(DELIVERY.CUSTOMER, delivery.getCustomer())
			.set(DELIVERY.ADDRESS, delivery.getAddress())
			.set(DELIVERY.ISSUE_TIME, new Timestamp(delivery.getIssueTime()!=null?delivery.getIssueTime().getTime():(new Date()).getTime()))
			.set(DELIVERY.PAY_METHOD, delivery.getPayMethod())
			.set(DELIVERY.SECURITY_LEVEL, delivery.getSecurityLevel())
			.set(DELIVERY.STATUS, (byte)delivery.getStatus().ordinal())
			.set(DELIVERY.COMMENTS, delivery.getComments())
			.set(DELIVERY.REMARKS, delivery.getRemarks())
			.set(DELIVERY.WORKPLACE, delivery.getWorkplace())
			.set(DELIVERY.SCOPE, delivery.getScope())
			.set(DELIVERY.NUMBER_OF_PYMNTS, delivery.getNumberOfPymnts())
			.set(DELIVERY.DAYS_TO_FIRST_PYMNT, delivery.getDaysToFirstPymnt())
			.set(DELIVERY.DAYS_BETWEEN_PYMNTS, delivery.getDaysBetweenPymnt())
			.set(DELIVERY.PYMNT_DAYS, delivery.getPymntDays())
			.set(DELIVERY.BANK_ACCOUNT, delivery.getBankAccount())
			.set(DELIVERY.BANK_ALIAS, delivery.getBankAlias())
			.set(DELIVERY.BIC, delivery.getBic())
			.set(DELIVERY.CARRIER, delivery.getCarrier())
			.set(DELIVERY.CARRIER_PACKING, delivery.getCarrierPacking())
			.set(DELIVERY.NUMBER_PLATE, delivery.getNumberPlate())
			.set(DELIVERY.DRIVER, delivery.getDriver())
			.set(DELIVERY.DRIVER_DOCUMENT, delivery.getDriverDocument())
			.set(DELIVERY.TOTAL_PACKAGES, delivery.getTotalPackages())
			.set(DELIVERY.TOTAL_WEIGHT, delivery.getTotalWeight())
			.set(DELIVERY.SHIPPING_ALTERNATIVE_ADDRESS, delivery.getShippingAlternativeAddress())
			.set(DELIVERY.SHIPPING_ALTERNATIVE_ADDRESS2, delivery.getShippingAlternativeAddress2())
			.set(DELIVERY.SHIPPING_ALTERNATIVE_ZIP, delivery.getShippingAlternativeZip())
			.set(DELIVERY.SHIPPING_ALTERNATIVE_CITY, delivery.getShippingAlternativeCity())
			.set(DELIVERY.SHIPPING_ALTERNATIVE_PHONE, delivery.getShippingAlternativePhone())
			.set(DELIVERY.SHIPPING_ALTERNATIVE_RECIPIENT, delivery.getShippingAlternativeRecipient())
			.set(DELIVERY.SHIPPING_CONTACT, delivery.getShippingContact())
			.set(DELIVERY.SHIPPING_PERIOD, delivery.getShippingPeriod())
			.set(DELIVERY.TRACKING_NUMBER, delivery.getTrackingNumber())
			.set(DELIVERY.SHIPPING_STATUS, delivery.getShippingStatus())
			.set(DELIVERY.STATUS_MODIFICATION_DATE, AonDateUtils.toTimestamp(delivery.getStatusModificationDate()))
			.set(DELIVERY.MODIFICATION_USER, ctx.getUser())
			.set(DELIVERY.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
		.where(DELIVERY_PROPERTIES.getConditions(filter)).returning().fetch()
		.stream().map(new DeliveryFiller()).findFirst().orElse(new Delivery());
	}
	
	public static void deleteDelivery(AONContext ctx, DeliveryFilter filter) {
		Integer[] ids = getDeliveryStream(ctx, filter).map(r -> r.getId()).toArray(Integer[]::new);
		deleteDeliveryDetail(ctx, f -> f.getDelivery().in(ids));
		ctx.getDslContext().delete(DELIVERY).where(DELIVERY_PROPERTIES.getConditions(filter));
	}
	
	// -------------------- DELIVERY DETAIL
	
	public static Stream<DeliveryDetail> getDeliveryDetailStream(AONContext ctx, DeliveryDetailFilter filter){
		return ctx.getDslContext().select().from(DELIVERY_DETAIL)
				.join(ITEM).on(DELIVERY_DETAIL.ITEM.eq(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new PDeliveryDetailFiller());
	}
	
	public static DeliveryDetail insertDeliveryDetail(AONContext ctx, DeliveryDetail detail) {
		ctx.checkWrite();
		return ctx.getDslContext()
				.insertInto(DELIVERY_DETAIL, DELIVERY_DETAIL.DOMAIN,
						DELIVERY_DETAIL.DELIVERY, DELIVERY_DETAIL.LINE,
						DELIVERY_DETAIL.ITEM, DELIVERY_DETAIL.DESCRIPTION,
						DELIVERY_DETAIL.WAREHOUSE, DELIVERY_DETAIL.QUANTITY,
						DELIVERY_DETAIL.PRICE, DELIVERY_DETAIL.DISCOUNT_EXPR,
						DELIVERY_DETAIL.SALES_DETAIL,
						DELIVERY_DETAIL.CREATION_USER,
						DELIVERY_DETAIL.CREATION_DATE,
						DELIVERY_DETAIL.MODIFICATION_USER,
						DELIVERY_DETAIL.MODIFICATION_DATE)
				.values(detail.getDomain(), detail.getDelivery().getId(),
						detail.getLine(), detail.getItem().getId(),
						detail.getDescription(), detail.getWarehouse(),
						detail.getQuantity(), detail.getPrice(),
						detail.getDiscountExpression(),
						detail.getSalesDetail(), 
						ctx.getUser(), AonDateUtils.toTimestamp(new Date()),
						ctx.getUser(), AonDateUtils.toTimestamp(new Date()))
				.returning().fetch().stream().map(new DeliveryDetailFiller()).findFirst().orElse(new DeliveryDetail());
	}
	
	public static Stream<DeliveryDetail> getDeliveryDetailStream(AONContext ctx, DeliveryFilter deliveryFilter, ProductFilter productFilter) {
		ctx.checkRead();
		
		Collection<Condition> whereConditions = new ArrayList<Condition>();
		whereConditions.addAll(Arrays.asList(DELIVERY_PROPERTIES.getConditions(deliveryFilter)));
		whereConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));

		return ctx.getDslContext()
			.select()
			.from(DELIVERY)
			.join(DELIVERY_DETAIL).on(DELIVERY_DETAIL.DELIVERY.equal(DELIVERY.ID))
			.join(REGISTRY).on(REGISTRY.ID.equal(DELIVERY.CUSTOMER))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(DELIVERY_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.where(whereConditions)
			.orderBy(DELIVERY.ISSUE_TIME,DELIVERY.SERIES,DELIVERY.NUMBER,DELIVERY_DETAIL.LINE)
			.fetch().stream().map(new FullDeliveryDetailSupplierItemFiller());
	}
	
	public static DeliveryDetail updateDeliveryDetail(AONContext ctx, DeliveryDetail deliveryDetail, DeliveryDetailFilter filter) {
		ctx.checkWrite();
		return ctx.getDslContext()
			.update(DELIVERY_DETAIL)
				.set(DELIVERY_DETAIL.DOMAIN, deliveryDetail.getDomain())
				.set(DELIVERY_DETAIL.DELIVERY, deliveryDetail.getDelivery().getId())
				.set(DELIVERY_DETAIL.LINE, deliveryDetail.getLine())
				.set(DELIVERY_DETAIL.ITEM, deliveryDetail.getItem().getId())
				.set(DELIVERY_DETAIL.DESCRIPTION, deliveryDetail.getDescription())
				.set(DELIVERY_DETAIL.WAREHOUSE, deliveryDetail.getWarehouse()) 
				.set(DELIVERY_DETAIL.QUANTITY, deliveryDetail.getQuantity())
				.set(DELIVERY_DETAIL.PRICE, deliveryDetail.getPrice())
				.set(DELIVERY_DETAIL.DISCOUNT_EXPR, deliveryDetail.getDiscountExpression())
				.set(DELIVERY_DETAIL.SALES_DETAIL, deliveryDetail.getSalesDetail())
				.set(DELIVERY_DETAIL.MODIFICATION_USER, ctx.getUser())
				.set(DELIVERY_DETAIL.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
			.where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter)).returning().fetch()
			.stream().map(new DeliveryDetailFiller()).findFirst().orElse(new DeliveryDetail());
	}
	
	public static void deleteDeliveryDetail(AONContext ctx, DeliveryDetailFilter filter) {
		ctx.getDslContext().delete(DELIVERY_DETAIL).where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter));
	}

	private static Result<Record> getFullDeliveries(AONContext ctx, DeliveryFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(
				 DELIVERY.ID
				,DELIVERY.DOMAIN
				,DELIVERY.STATUS
				,DELIVERY.SERIES
				,DELIVERY.NUMBER
				,DELIVERY.ISSUE_TIME
				,DELIVERY.ADDRESS
				,REGISTRY.DOCUMENT
				,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY
				,REGISTRY.NAME
				,REGISTRY.ID
				,RADDRESS.STREET_TYPE
				,RADDRESS.ADDRESS
				,RADDRESS.NUMBER
				,RADDRESS.ADDRESS2
				,RADDRESS.ADDRESS3
				,RADDRESS.ZIP
				,RADDRESS.CITY
				,GEOZONE.CODE
				,GEOZONE.NAME
				,SCOPE.DESCRIPTION
				,PROJECT.NAME
				,DELIVERY_DETAIL.LINE
				,DELIVERY_DETAIL.ITEM
				,PCATEGORY.NAME
				,PRODUCT.ID
				,PRODUCT.NAME
				,PRODUCT.CODE
				,ITEM.DETAIL
				,ITEM.DETAIL2
				,ITEM.DETAIL3
				,ITEM.DESCRIPTION
				,DELIVERY_DETAIL.DESCRIPTION
				,DELIVERY_DETAIL.QUANTITY
				,DELIVERY_DETAIL.PRICE
				,DELIVERY_DETAIL.DISCOUNT_EXPR
				,DELIVERY_DETAIL.SALES_DETAIL
				,SALES.PURCHASE_REFERENCE
				,WORKPLACE.DESCRIPTION
			)
			.from(DELIVERY)
			.join(DELIVERY_DETAIL).on(DELIVERY_DETAIL.DELIVERY.equal(DELIVERY.ID))
			.join(REGISTRY).on(REGISTRY.ID.equal(DELIVERY.CUSTOMER))
			.join(SCOPE).on(SCOPE.ID.equal(DELIVERY.SCOPE))
			.leftOuterJoin(RADDRESS).on(DELIVERY.ADDRESS.equal(RADDRESS.ID))
			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(DELIVERY.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(DELIVERY_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(DELIVERY.WORKPLACE))
			
			.leftOuterJoin(SALES_DETAIL).on(SALES_DETAIL.ID.equal(DELIVERY_DETAIL.SALES_DETAIL))
			.leftOuterJoin(SALES).on(SALES.ID.equal(SALES_DETAIL.SALES))
			
			.where(DELIVERY_PROPERTIES.getConditions(filter))
			.orderBy(DELIVERY.ISSUE_TIME,DELIVERY.SERIES,DELIVERY.NUMBER,DELIVERY_DETAIL.LINE)
			.fetch();
	}
	
	public static Stream<DeliveryDetail> getDeliveryDetails(AONContext ctx, DeliveryFilter filter) {
		return getFullDeliveries(ctx, filter)
			.stream()
			.map(new FullDeliveryDetailFiller2());
	}
	
	private static class FullDeliveryDetailFiller2  implements Function<Record,DeliveryDetail> {

		@Override
		public DeliveryDetail apply(Record record) {
			Customer customer = new Customer();
			customer.setDocument(record.getValue(REGISTRY.DOCUMENT));
			customer.setDocumentType(AonEnumUtils.enumValue(DocumentType.class,
									record.getValue(REGISTRY.DOCUMENT_TYPE)));
			customer.setDocumentCountry(Country.safeValueOf(record
									.getValue(REGISTRY.DOCUMENT_COUNTRY)));
			customer.setName(record.getValue(REGISTRY.NAME));
			customer.setId(record.getValue(REGISTRY.ID));
			return new DeliveryDetail()
				.setDelivery(new Delivery()
					.setId(record.getValue(DELIVERY.ID))
					.setAddress(record.getValue(DELIVERY.ADDRESS))
					
					.setAddressStreetType( StreetType.safeValueOf(record.getValue(RADDRESS.STREET_TYPE)))
					.setAddressName(record.getValue(RADDRESS.ADDRESS))
					.setAddressNumber(record.getValue(RADDRESS.NUMBER))
					.setAddressTown(record.getValue(RADDRESS.CITY))
					.setAddressZIP(record.getValue(RADDRESS.ZIP))
					.setAddressGeozoneCode(record.getValue(GEOZONE.CODE))
					.setAddressGeozone(record.getValue(GEOZONE.NAME))
					
					.setDomain(record.getValue(DELIVERY.DOMAIN))
					.setStatus(AonEnumUtils.enumValue(DeliveryStatus.class,
									record.getValue(DELIVERY.STATUS)))
					.setSeries(record.getValue(DELIVERY.SERIES))
					.setNumber(record.getValue(DELIVERY.NUMBER))
					.setIssueTime(record.getValue(DELIVERY.ISSUE_TIME))
					.setCustomer2(customer)
					.setScopeName(record.getValue(SCOPE.DESCRIPTION))	
					.setWorkplaceName(record.getValue(WORKPLACE.DESCRIPTION))
					.setProject(new Project()
							.setName(record.getValue(PROJECT.NAME)))
					)
				
				.setLine(record.getValue(DELIVERY_DETAIL.LINE))
				.setDescription(record.getValue( DELIVERY_DETAIL.DESCRIPTION ))
				.setQuantity(record.getValue(DELIVERY_DETAIL.QUANTITY))
				.setPrice(record.getValue(DELIVERY_DETAIL.PRICE))
				.setDiscountExpression(record.getValue(DELIVERY_DETAIL.DISCOUNT_EXPR))
				.setItem((record.getValue(DELIVERY_DETAIL.ITEM) == null)
					? null
					: new Item()
						.setId(record.getValue(DELIVERY_DETAIL.ITEM))
						.setCategory( record.getValue( PCATEGORY.NAME ) )
						.setProductId( record.getValue( PRODUCT.ID ) )
						.setName( record.getValue( PRODUCT.NAME ) )
						.setCode(record.getValue( PRODUCT.CODE ) )
						.setDetail(record.getValue( ITEM.DETAIL ))
						.setDetail2(record.getValue( ITEM.DETAIL2 ))
						.setDetail3(record.getValue( ITEM.DETAIL3 ))
						.setDescription(record.getValue( ITEM.DESCRIPTION )))
				.setSalesDetail(record.getValue(DELIVERY_DETAIL.SALES_DETAIL))
				.setPurchaseReference(record.getValue(SALES.PURCHASE_REFERENCE))
				;
		}
	}
	
	private static class FullDeliveryDetailSupplierItemFiller implements Function<Record,DeliveryDetail> {

		@Override
		public DeliveryDetail apply(Record record) {
			Customer customer = new Customer();
			customer.setDocument(record.getValue(REGISTRY.DOCUMENT));
			customer.setDocumentType(AonEnumUtils.enumValue(DocumentType.class,
									record.getValue(REGISTRY.DOCUMENT_TYPE)));
			customer.setDocumentCountry(Country.safeValueOf(record
									.getValue(REGISTRY.DOCUMENT_COUNTRY)));
			customer.setName(record.getValue(REGISTRY.NAME));
			customer.setId(record.getValue(REGISTRY.ID));
			
			Item item = (record.getValue(DELIVERY_DETAIL.ITEM) == null)
					? null
					: new Item()
						.setId(record.getValue(DELIVERY_DETAIL.ITEM))
						.setCategory( record.getValue( PCATEGORY.NAME ) )
						.setProductId( record.getValue( PRODUCT.ID ) )
						.setName( record.getValue( PRODUCT.NAME ) )
						.setCode(record.getValue( PRODUCT.CODE ) )
						.setDetail(record.getValue( ITEM.DETAIL ))
						.setDetail2(record.getValue( ITEM.DETAIL2 ))
						.setDetail3(record.getValue( ITEM.DETAIL3 ))
						.setDescription(record.getValue( ITEM.DESCRIPTION ));
			
			return new DeliveryDetail()
				.setDelivery(new Delivery()
					.setId(record.getValue(DELIVERY.ID))
					.setAddress(record.getValue(DELIVERY.ADDRESS))
					.setDomain(record.getValue(DELIVERY.DOMAIN))
					.setSeries(record.getValue(DELIVERY.SERIES))
					.setNumber(record.getValue(DELIVERY.NUMBER))
					.setStatus(AonEnumUtils.enumValue(DeliveryStatus.class,
							record.getValue(DELIVERY.STATUS)))
					.setIssueTime(record.getValue(DELIVERY.ISSUE_TIME))
					.setCustomer2(customer)
					)
				.setLine(record.getValue(DELIVERY_DETAIL.LINE))
				.setDescription(record.getValue( DELIVERY_DETAIL.DESCRIPTION ))
				.setQuantity(record.getValue(DELIVERY_DETAIL.QUANTITY))
				.setPrice(record.getValue(DELIVERY_DETAIL.PRICE))
				.setDiscountExpression(record.getValue(DELIVERY_DETAIL.DISCOUNT_EXPR))
				.setItem(item)
				;
		}
	}
	
}
