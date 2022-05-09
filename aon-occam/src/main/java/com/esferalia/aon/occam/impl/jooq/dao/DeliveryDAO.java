package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.management.ShipmentPeriod;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.ShipmentStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO.PayMethodFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ProductPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DeliveryDetailPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DeliveryPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO.RegistryAddressFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class DeliveryDAO {
	
	private static final DeliveryDetailPropertiesDAO DELIVERY_DETAIL_PROPERTIES = new DeliveryDetailPropertiesDAO();
	private static final DeliveryPropertiesDAO DELIVERY_PROPERTIES = new DeliveryPropertiesDAO();
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
	private static final ItemPropertiesDAO ITEM_PROPERTIES = new ItemPropertiesDAO();

	private DeliveryDAO() {
	
	}
	
	// -------------------- DELIVERY

	public static int getNextNumber(AONContext ctx, String series ) {
		Integer next = ctx.getDslContext()
			.select( DSL.max(DELIVERY.NUMBER))
			.from(DELIVERY)
			.where(DELIVERY.DOMAIN.eq(ctx.getDomainId()))
			.and(AonStringUtils.isBlank(series)
				? DELIVERY.SERIES.isNull().or(DSL.trim(DELIVERY.SERIES).eq(""))
				: DELIVERY.SERIES.eq(series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(DELIVERY.NUMBER)) != null) 
					? rec.getValue(DSL.max(DELIVERY.NUMBER)) 
					: 0)
			.findFirst()
			.orElse(0);
		if(next < 0) next = 0;
		return ++next;
	}

	private static SelectConditionStep<Record> select(AONContext ctx, DeliveryFilter filter) {
		 return ctx.getDslContext().select()
			.from(DELIVERY)
			.join(REGISTRY).on(REGISTRY.ID.eq(DELIVERY.CUSTOMER))
			.where(DELIVERY_PROPERTIES.getConditions(filter));
	}

	public static Delivery get(AONContext ctx, Integer deliveryId){
		return get(ctx, f -> f.getIdProperty().eq(deliveryId));
	}
	
	public static Delivery get(AONContext ctx, DeliveryFilter filter){
		return select(ctx, filter).limit(1).fetch().stream().map(new DeliveryFiller())
			.findFirst().orElse(new Delivery());
	}
	
	public static Stream<Delivery> getStream(AONContext ctx, DeliveryFilter filter){
		return select(ctx, filter).fetch().stream().map(new DeliveryFiller());
	}
	
	public static Stream<Delivery> getStream(AONContext ctx, DeliveryFilter filter, Integer page, Integer perPage){
		return select(ctx, filter)
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new DeliveryFiller());
	}
	
	public static List<Delivery> getList(AONContext ctx, DeliveryFilter filter){
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<Delivery> getList(AONContext ctx, DeliveryFilter filter, Integer page, Integer perPage){
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	/**
	 * @deprecated  Replaced by getStream(AONContext ctx, DeliveryFilter filter)
	 */
	public static Stream<Delivery> getDeliveryStream(AONContext ctx, DeliveryFilter filter){
		return DELIVERY_PROPERTIES.build(ctx.getDslContext()
				.select()
				.from(DELIVERY)
				.join(REGISTRY).on(REGISTRY.ID.eq(DELIVERY.CUSTOMER))
			,filter).fetch().stream().map(new DeliveryFiller());
	}
	
	public static Delivery save(AONContext ctx, Delivery delivery) {
		ctx.checkWrite();
		
		return delivery.hasId() 
			? update(ctx, delivery)
			: insertDelivery(ctx, delivery);
	}
	
	/**
	 * @deprecated  Replaced by save(AONContext ctx, Delivery delivery)
	 */
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
						delivery.getCustomer().getId(), delivery.getAddress().getId(),
						delivery.getDate(), delivery.getPayMethod().getId(),
						delivery.getSecurityLevel().value(), delivery.getStatus().ordinal(),
						delivery.getComments(), delivery.getRemarks(),
						delivery.getWorkplace().getId(), delivery.getScope().getId(),
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
						delivery.getShippingPeriodValue(), delivery.getTrackingNumber(),
						delivery.getShippingStatusValue(), delivery.getStatusModificationDate(),
						ctx.getUser(), AonDateUtils.toTimestamp(new Date()),
						ctx.getUser(), AonDateUtils.toTimestamp(new Date()))
				.returning().fetch().stream().map(new DeliveryFiller()).findFirst().orElse(new Delivery());
	}
	
	public static Delivery update(AONContext ctx, Delivery delivery) {
		return updateDelivery(ctx, delivery, f -> f.getIdProperty().eq(delivery.getId()));
	}
	/**
	 * @deprecated  Replaced by save(AONContext ctx, Delivery delivery)
	 */
	public static Delivery updateDelivery(AONContext ctx, Delivery delivery, DeliveryFilter filter) {
		ctx.checkWrite();		
		ctx.getDslContext().update(DELIVERY)
			.set(DELIVERY.DOMAIN, delivery.getDomain())
			.set(DELIVERY.PROJECT, delivery.getProject().getId())
			.set(DELIVERY.SERIES, delivery.getSeries())
			.set(DELIVERY.NUMBER, delivery.getNumber())
			.set(DELIVERY.CUSTOMER, delivery.getCustomer().getId())
			.set(DELIVERY.ADDRESS, delivery.getAddress().getId())
			.set(DELIVERY.ISSUE_TIME, AonDateUtils.toTimestamp(delivery.getDate()))
			.set(DELIVERY.PAY_METHOD, delivery.getPayMethod().getId())
			.set(DELIVERY.SECURITY_LEVEL, delivery.getSecurityLevel().value())
			.set(DELIVERY.STATUS, (byte)delivery.getStatus().ordinal())
			.set(DELIVERY.COMMENTS, delivery.getComments())
			.set(DELIVERY.REMARKS, delivery.getRemarks())
			.set(DELIVERY.WORKPLACE, delivery.getWorkplace().getId())
			.set(DELIVERY.SCOPE, delivery.getScope().getId())
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
			.set(DELIVERY.SHIPPING_PERIOD, delivery.getShippingPeriod().value())
			.set(DELIVERY.TRACKING_NUMBER, delivery.getTrackingNumber())
			.set(DELIVERY.SHIPPING_STATUS, delivery.getShippingStatus().value())
			.set(DELIVERY.STATUS_MODIFICATION_DATE, AonDateUtils.toTimestamp(delivery.getStatusModificationDate()))
			.set(DELIVERY.MODIFICATION_USER, ctx.getUser())
			.set(DELIVERY.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
		.where(DELIVERY_PROPERTIES.getConditions(filter))
		.execute();
		return delivery;
	}
	
	public static void delete(AONContext ctx, Integer id) {
		deleteDelivery(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static void deleteDelivery(AONContext ctx, DeliveryFilter filter) {
		Integer[] ids = getDeliveryStream(ctx, filter)
				.map(Delivery::getId)
				.toArray(Integer[]::new);
		deleteDeliveryDetail(ctx, f -> f.getDelivery().in(ids));
		ctx.getDslContext()
			.delete(DELIVERY).where(DELIVERY_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	// -------------------- DELIVERY DETAIL

	public static DeliveryDetail getDeliveryDetail(AONContext ctx, DeliveryDetailFilter filter){
		return ctx.getDslContext().select().from(DELIVERY_DETAIL)
				.join(ITEM).on(DELIVERY_DETAIL.ITEM.eq(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new DeliveryDetailFiller())
			.findFirst().orElse(new DeliveryDetail());
	}
	
	public static Stream<DeliveryDetail> getDeliveryDetailStream(AONContext ctx, DeliveryDetailFilter filter){
		return ctx.getDslContext().select().from(DELIVERY_DETAIL)
				.join(ITEM).on(DELIVERY_DETAIL.ITEM.eq(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.join(DELIVERY).on(DELIVERY.ID.eq(DELIVERY_DETAIL.DELIVERY))
			.where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new DeliveryDetailFiller());
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
	
	public static Stream<DeliveryDetail> getDeliveryDetailStream(AONContext ctx, DeliveryFilter deliveryFilter, DeliveryDetailFilter detailFilter,
			ProductFilter productFilter, ItemFilter itemFilter) {
		ctx.checkRead();
		
		Collection<Condition> whereConditions = new ArrayList<>();
		whereConditions.addAll(Arrays.asList(DELIVERY_PROPERTIES.getConditions(deliveryFilter)));
		whereConditions.addAll(Arrays.asList(DELIVERY_DETAIL_PROPERTIES.getConditions(detailFilter)));
		whereConditions.addAll(Arrays.asList(PRODUCT_PROPERTIES.getConditions(productFilter)));
		whereConditions.addAll(Arrays.asList(ITEM_PROPERTIES.getConditions(itemFilter)));

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
			.fetch().stream().map(new DeliveryDetailFiller());
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
				,RADDRESS.ID
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
				,PCATEGORY.ID
				,PCATEGORY.NAME
				,PRODUCT.ID
				,PRODUCT.NAME
				,PRODUCT.CODE
				,ITEM.ID
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
			.map(new DeliveryDetailFiller());
	}
	
	public static class DeliveryFiller extends Filler implements Function<Record, Delivery> {

		@Override
		public Delivery apply(Record r) {
			return build(r);
		}
		
		public static Delivery build(Record r) {
			return new Delivery()
					.setId(getValue(r, DELIVERY.ID))
					.setDomain(getValue(r, DELIVERY.DOMAIN))
					.setProject(new Project()
							.setId(getValue(r, DELIVERY.PROJECT)))
					.setSeries(getValue(r, DELIVERY.SERIES))
					.setNumber(getInteger(r, DELIVERY.NUMBER))
					.setCustomer(checkField(r, CUSTOMER.REGISTRY) || checkField(r, REGISTRY.ID)
						? CustomerFiller.build(r)
						: new Customer().setId(getValue(r, DELIVERY.CUSTOMER)))
					.setAddress(checkField(r, RADDRESS.ID)
						? RegistryAddressFiller.build(r, GEOZONE, GEOZONE)
						: new RegistryAddress().setId(getValue(r, DELIVERY.ADDRESS)))
					
					.setDate(getValue(r, DELIVERY.ISSUE_TIME))
					.setPayMethod(checkField(r, PAY_METHOD.ID)
							? PayMethodFiller.build(r)
							: new PayMethod().setId(getValue(r, DELIVERY.PAY_METHOD)))
					.setSecurityLevel(SecurityLevel.safeValueOf(getByte(r, DELIVERY.SECURITY_LEVEL)))
					.setStatus(DeliveryStatus.safeValueOf(getValue(r, DELIVERY.STATUS)))
					.setComments(getValue(r, DELIVERY.COMMENTS))
					.setRemarks(getValue(r, DELIVERY.REMARKS))
					.setWorkplace(checkField(r, WORKPLACE.ID)
							? WorkplaceFiller.build(r)
							: new Workplace().setId(getValue(r, DELIVERY.WORKPLACE)))
					.setScope(checkField(r, SCOPE.ID)
							? ScopeFiller.buildScope(r)
							: new Scope().setId(getValue(r, DELIVERY.SCOPE)))
					.setNumberOfPymnts(getShort(r, DELIVERY.NUMBER_OF_PYMNTS))
					.setDaysToFirstPymnt(getShort(r, DELIVERY.DAYS_TO_FIRST_PYMNT))
					.setDaysBetweenPymnt(getShort(r, DELIVERY.DAYS_BETWEEN_PYMNTS))
					.setPymntDays(getValue(r, DELIVERY.PYMNT_DAYS))
					.setBankAccount(getValue(r, DELIVERY.BANK_ACCOUNT))
					.setBankAlias(getValue(r, DELIVERY.BANK_ALIAS))
					.setBic(getValue(r, DELIVERY.BIC))
					.setCarrier(getValue(r, DELIVERY.CARRIER))
					.setCarrierPacking(getValue(r, DELIVERY.CARRIER_PACKING))
					.setNumberPlate(getValue(r, DELIVERY.NUMBER_PLATE))
					.setDriver(getValue(r, DELIVERY.DRIVER))
					.setDriverDocument(getValue(r, DELIVERY.DRIVER_DOCUMENT))
					.setTotalPackages(getValue(r, DELIVERY.TOTAL_PACKAGES))
					.setTotalWeight(getValue(r, DELIVERY.TOTAL_WEIGHT))
					.setShippingAlternativeAddress(getValue(r, DELIVERY.SHIPPING_ALTERNATIVE_ADDRESS))
					.setShippingAlternativeAddress2(getValue(r, DELIVERY.SHIPPING_ALTERNATIVE_ADDRESS2))
					.setShippingAlternativeZip(getValue(r, DELIVERY.SHIPPING_ALTERNATIVE_ZIP))
					.setShippingAlternativeCity(getValue(r, DELIVERY.SHIPPING_ALTERNATIVE_CITY))
					.setShippingAlternativePhone(getValue(r, DELIVERY.SHIPPING_ALTERNATIVE_PHONE))
					.setShippingAlternativeRecipient(getValue(r, DELIVERY.SHIPPING_ALTERNATIVE_RECIPIENT))
					.setShippingContact(getValue(r, DELIVERY.SHIPPING_CONTACT))
					.setShippingPeriod(ShipmentPeriod.safeValueOf(getValue(r, DELIVERY.SHIPPING_PERIOD)))
					.setTrackingNumber(getValue(r, DELIVERY.TRACKING_NUMBER))
					.setShippingStatus(ShipmentStatus.safeValueOf(getValue(r, DELIVERY.SHIPPING_STATUS)))
					.setStatusModificationDate(getValue(r, DELIVERY.STATUS_MODIFICATION_DATE))
					.setCreationDate(getValue(r, DELIVERY.CREATION_DATE))
					.setCreationUser(getValue(r, DELIVERY.CREATION_USER))
					.setModificationDate(getValue(r, DELIVERY.MODIFICATION_DATE))
					.setModificationUser(getValue(r, DELIVERY.MODIFICATION_USER));
		}
	}

	public static class DeliveryDetailFiller extends Filler implements Function<Record, DeliveryDetail> {

		@Override
		public DeliveryDetail apply(Record r) {
			return build(r);
		}
		
		public static DeliveryDetail build(Record r) {
			return new DeliveryDetail()
				.setId(getValue(r, DELIVERY_DETAIL.ID))
				.setDomain(getInteger(r, DELIVERY_DETAIL.DOMAIN))
				.setDelivery(checkField(r, DELIVERY.ID)
					? DeliveryFiller.build(r)
					: new Delivery().setId(getValue(r, DELIVERY_DETAIL.DELIVERY)))
				.setLine(getShort(r, DELIVERY_DETAIL.LINE))
				.setItem(checkField(r, ITEM.ID)
					? ItemFiller.build(r)
					: new Item().setId(getValue(r, DELIVERY_DETAIL.ITEM)))
				.setDescription(getValue(r, DELIVERY_DETAIL.DESCRIPTION))
				.setWarehouse(getValue(r, DELIVERY_DETAIL.WAREHOUSE))
				.setQuantity(getDouble(r, DELIVERY_DETAIL.QUANTITY))
				.setPrice(getDouble(r, DELIVERY_DETAIL.PRICE))
				.setDiscountExpression(getValue(r, DELIVERY_DETAIL.DISCOUNT_EXPR))
				.setSalesDetail(getValue(r, DELIVERY_DETAIL.SALES_DETAIL))
				.setPurchaseReference(getValue(r, SALES.PURCHASE_REFERENCE))
				.setCreationDate(getValue(r, DELIVERY_DETAIL.CREATION_DATE))
				.setCreationUser(getValue(r, DELIVERY_DETAIL.CREATION_USER))
				.setModificationDate(getValue(r, DELIVERY_DETAIL.MODIFICATION_DATE))
				.setModificationUser(getValue(r, DELIVERY_DETAIL.MODIFICATION_USER));
		}
	}
	
}
