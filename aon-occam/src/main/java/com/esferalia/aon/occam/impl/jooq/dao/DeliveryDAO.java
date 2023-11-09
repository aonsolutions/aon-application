package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
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
import static com.esferalia.aon.occam.impl.jooq.dao.CarrierDAO.CARRIER_ALIAS;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.management.ShipmentPeriod;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.ShipmentStatus;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDetailDAO.DeliveryDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDetailDAO.DeliveryDetailPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PayMethodDAO.PayMethodFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ItemPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ProductPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO.ProjectFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO.RegistryAddressFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.occam.impl.jooq.validation.DeliveryValidation;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class DeliveryDAO {
	
	private static final DeliveryDetailPropertiesDAO DELIVERY_DETAIL_PROPERTIES = new DeliveryDetailPropertiesDAO();
	private static final DeliveryPropertiesDAO DELIVERY_PROPERTIES = new DeliveryPropertiesDAO();
	private static final ProductPropertiesDAO PRODUCT_PROPERTIES = new ProductPropertiesDAO();
	private static final ItemPropertiesDAO ITEM_PROPERTIES = new ItemPropertiesDAO();

	private DeliveryDAO() {
		
	}
	
	protected static class DeliveryPropertiesDAO implements DeliveryProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,DeliveryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DeliveryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.PROJECT);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.NUMBER);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CUSTOMER);}
		@Override public Property<Integer> getAddressProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.ADDRESS);}
		@Override public Property<Timestamp> getIssueTimeProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.ISSUE_TIME);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.PAY_METHOD);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.SECURITY_LEVEL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.REMARKS);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.WORKPLACE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.SCOPE);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.PYMNT_DAYS);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.BIC);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.MODIFICATION_USER);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CARRIER);}
		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CARRIER_PACKING);}
		
		@Override public Property<String> getRegistryNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getRegistryDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}

		@Override public Property<Byte> getConfidentialProperty() {return null;}
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
	
	// ----- SELECT

	private static SelectConditionStep<Record> select(AONContext ctx, DeliveryFilter filter) {
		 return ctx.getDslContext().select()
			.from(DELIVERY)
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(DELIVERY.CUSTOMER))
			.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
			.where(DELIVERY_PROPERTIES.getConditions(filter));
	}
	
	private static SelectConditionStep<Record> selectFull(AONContext ctx, DeliveryFilter filter) {
		 return ctx.getDslContext().select()
			.from(DELIVERY)
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(DELIVERY.CUSTOMER))
			.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
			.join(DELIVERY_DETAIL).on(DELIVERY_DETAIL.DELIVERY.eq(DELIVERY.ID))
			.leftOuterJoin(PROJECT).on(PROJECT.ID.equal(DELIVERY.PROJECT))
			.leftOuterJoin(ITEM).on(ITEM.ID.equal(DELIVERY_DETAIL.ITEM))
			.leftOuterJoin(PRODUCT).on(PRODUCT.ID.equal(ITEM.PRODUCT))
			.leftOuterJoin(PCATEGORY).on(PRODUCT.CATEGORY.equal(PCATEGORY.ID))
			.leftOuterJoin(SCOPE).on(SCOPE.ID.equal(DELIVERY.SCOPE))
			.leftOuterJoin(WORKPLACE).on(WORKPLACE.ID.equal(DELIVERY.WORKPLACE))
			.leftOuterJoin(CARRIER).on(CARRIER.REGISTRY.eq(DELIVERY.CARRIER))
			.leftOuterJoin(CARRIER_ALIAS).on(CARRIER.REGISTRY.eq(CARRIER_ALIAS.ID))
			.where(DELIVERY_PROPERTIES.getConditions(filter));
	}

	// ----- GET
	
	public static Delivery get(AONContext ctx, Integer deliveryId){
		return get(ctx, f -> f.getIdProperty().eq(deliveryId));
	}
	
	public static Delivery get(AONContext ctx, DeliveryFilter filter, Options... options){
		if(options.length > 0 && options[0].isFull())
			return getFull(ctx, filter);
		return select(ctx, filter).limit(1).fetch().stream().map(new DeliveryFiller())
			.findFirst().orElse(new Delivery());
	}
	
	public static Delivery getFull(AONContext ctx, DeliveryFilter filter){
		Delivery delivery = getFullStream(ctx, filter).findFirst().orElse(new Delivery()); 
		if(delivery.getId() != null) {
			delivery.setPackaging(DeliveryPackagingDAO.getList(ctx, f -> f.getDeliveryProperty().eq(delivery.getId())));
		}
		return delivery; 
	}
	
	// ----- GET STREAM
	
	public static Stream<Delivery> getStream(AONContext ctx, DeliveryFilter filter, Options... options){
		if(options.length > 0) 
			return getStream(ctx, filter, options[0]);
		return select(ctx, filter)
			.orderBy(DELIVERY.ID.desc())
			.fetch().stream().map(new DeliveryFiller());
	}
	
	private static Stream<Delivery> getStream(AONContext ctx, DeliveryFilter filter, Options options){
		if(options.isFull() && options.isPagination())
			return getFullStream(ctx, filter, options.getPage(), options.getPerPage());
		else if(options.isFull())
			return getFullStream(ctx, filter);
		else if(options.isPagination())
			return getStream(ctx, filter, options.getPage(), options.getPerPage());
		else return getStream(ctx, filter);
	}
	
	public static Stream<Delivery> getStream(AONContext ctx, DeliveryFilter filter, Integer page, Integer perPage){
		return select(ctx, filter)
			.orderBy(DELIVERY.ID.desc())
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new DeliveryFiller());
	}
	
	public static Stream<Delivery> getFullStream(AONContext ctx, DeliveryFilter filter){
		List<Tag> tagList = TagDAO.getList(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getTypeProperty().eq(TagType.PACKING.value())));
		
		Map<Delivery, List<DeliveryDetail>> map = selectFull(ctx, filter)
			.groupBy(DELIVERY.ID, DELIVERY_DETAIL.ID)
			.orderBy(DELIVERY.ID.desc())
			.fetchGroups(
				new DeliveryFiller()::apply,
				new DeliveryDetailFiller()::apply
			);
		map.forEach((object, details) -> details.forEach(detail -> {
			detail.setItem(completeItemPackingTag(detail.getItem(), tagList));
			object.addDetail(detail);
		}));
		return map.keySet().stream(); 
	}
	
	private static Item completeItemPackingTag(Item item, List<Tag> tags) {
		item.setPackFormatTag(getItemPackingTag(tags, item.getPackFormatTag()));
		item.setPackMeasurementTag(getItemPackingTag(tags, item.getPackMeasurementTag()));
		item.setPackUnitsTag(getItemPackingTag(tags, item.getPackUnitsTag()));
		item.setStockUnitTag(getItemPackingTag(tags, item.getStockUnitTag()));
		return item;
	}
	
	private static Tag getItemPackingTag(List<Tag> tags, Tag tag) {
		return tags.stream().filter(f -> f.getId().equals(tag.getId())).findFirst().orElse(tag);
	}
		
	public static Stream<Delivery> getFullStream(AONContext ctx, DeliveryFilter filter, Integer page, Integer perPage){
		Map<Delivery, List<DeliveryDetail>> map = selectFull(ctx, filter)
			.groupBy(DELIVERY.ID, DELIVERY_DETAIL.ID)
			.orderBy(DELIVERY.ID.desc())
			.fetchGroups(
				new DeliveryFiller()::apply,
				new DeliveryDetailFiller()::apply
			);
		map.forEach((object, details) -> details.forEach(object::addDetail));
		return map.keySet().stream(); 
	}
	
	// ----- GET LIST
	
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
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(DELIVERY.CUSTOMER))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
			,filter).fetch().stream().map(new DeliveryFiller());
	}
	
	public static Delivery save(AONContext ctx, Delivery delivery) {
		ctx.checkWrite();
		
		DeliveryValidation.autocomplete(ctx, delivery);
		DeliveryValidation.validate(ctx, delivery);
		
		delivery = delivery.hasId() 
			? update(ctx, delivery)
			: insertDelivery(ctx, delivery);
		
		delivery.setDetails(DeliveryDetailDAO.save(ctx, delivery, delivery.getDetails()));
		return delivery;
	}
	
	/**
	 * @deprecated  Replaced by save(AONContext ctx, Delivery delivery)
	 */
	public static Delivery insertDelivery(AONContext ctx, Delivery delivery) {
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
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
				.returning().fetch().stream().map(new DeliveryFiller()).findFirst().orElse(new Delivery()).getId();
		return delivery.setId(id);
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
			.set(DELIVERY.SHIPPING_PERIOD, delivery.getShippingPeriodValue())
			.set(DELIVERY.TRACKING_NUMBER, delivery.getTrackingNumber())
			.set(DELIVERY.SHIPPING_STATUS, delivery.getShippingStatusValue())
			.set(DELIVERY.STATUS_MODIFICATION_DATE, AonDateUtils.toTimestamp(delivery.getStatusModificationDate()))
			.set(DELIVERY.MODIFICATION_USER, ctx.getUser())
			.set(DELIVERY.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
		.where(DELIVERY_PROPERTIES.getConditions(filter))
		.execute();
		return delivery;
	}
	
	public static void delete(AONContext ctx, Integer id) {
		deleteDeliveryDetail(ctx, f -> f.getDelivery().eq(id));
		deleteDelivery(ctx, f -> f.getIdProperty().eq(id));
	}
	
	public static void delete(AONContext ctx, DeliveryFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext()
			.delete(DELIVERY).where(DELIVERY_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	public static void deleteDelivery(AONContext ctx, DeliveryFilter filter) {
		Integer[] ids = getStream(ctx, filter)
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
		List<Tag> tagList = TagDAO.getList(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getTypeProperty().eq(TagType.PACKING.value())));
	
		return ctx.getDslContext().select().from(DELIVERY_DETAIL)
				.join(ITEM).on(DELIVERY_DETAIL.ITEM.eq(ITEM.ID))
				.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
				.join(DELIVERY).on(DELIVERY.ID.eq(DELIVERY_DETAIL.DELIVERY))
			.where(DELIVERY_DETAIL_PROPERTIES.getConditions(filter))
			.fetch().stream().map(new DeliveryDetailFiller())
			.map(detail -> detail.setItem(completeItemPackingTag(detail.getItem(), tagList)));
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
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(DELIVERY.CUSTOMER))
			.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
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
			.select()
			.from(DELIVERY)
			.join(DELIVERY_DETAIL).on(DELIVERY_DETAIL.DELIVERY.equal(DELIVERY.ID))
			.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(DELIVERY.CUSTOMER))
			.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
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
					.setProject(checkField(r, PROJECT.ID)
					        ? ProjectFiller.build(r)
					        : new Project().setId(getValue(r, DELIVERY.PROJECT)))
					.setSeries(getValue(r, DELIVERY.SERIES))
					.setNumber(getInteger(r, DELIVERY.NUMBER))
					.setCustomer(checkField(r, CUSTOMER.REGISTRY) || checkField(r, CUSTOMER_ALIAS.ID)
						? CustomerFiller.buildCustomer(r, CUSTOMER_ALIAS)
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
}
