package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CUSTOMER_ALIAS;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO.CustomerFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO.WorkplaceFiller;
import com.esferalia.aon.watson.util.AonStringUtils;

public class BookingCheckDAO {
	
	public static LinkedList<BookingCheck> getBookingWithoutFeeList(AONContext ctx, CustomerFeeParams customerFeeParams){
		Condition condition = createRitemCondition(ctx, customerFeeParams);
		
		SelectOnConditionStep<Record> bookingWithoutFeeSelect = ctx.getDslContext().select().from(RITEM)
				.join(DOMAIN).on(DOMAIN.ID.eq(RITEM.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(RITEM.REGISTRY))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(RITEM.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.leftJoin(CUSTOMER_FEE).on(CUSTOMER_FEE.CUSTOMER.eq(RITEM.REGISTRY).and(CUSTOMER_FEE.ITEM.eq(RITEM.ITEM)));
		
		Result<Record> bookingWithoutFeeRecords = bookingWithoutFeeSelect
				.where(condition)
				.and(CUSTOMER_FEE.ID.isNull())
				.and(RITEM.TYPE.eq((byte)4))
				.orderBy(RITEM.REGISTRY)
				.offset(customerFeeParams.getOffset())
				.limit(customerFeeParams.getLimit())
			.fetch();
		
		System.out.println("Booking Without Fee size : " + bookingWithoutFeeRecords.size());
		
		return bookingWithoutFeeRecords.stream().map(new BookingWithOutFeeFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<BookingCheck> getFeeWithoutBookingList(AONContext ctx, CustomerFeeParams customerFeeParams){
		Condition condition = createCustomerFeeCondition(ctx, customerFeeParams);
		
		SelectOnConditionStep<Record> feeWithoutBookingSelect = ctx.getDslContext().select().from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.leftJoin(RITEM).on(RITEM.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER).and(RITEM.ITEM.eq(CUSTOMER_FEE.ITEM)));
		
		Result<Record> feeWithoutBookingRecords = feeWithoutBookingSelect
				.where(condition)
				.and(RITEM.ID.isNull())
				.and(ITEM.BARCODE.isNull().or(ITEM.BARCODE.notLikeIgnoreCase("info%")))
				.orderBy(CUSTOMER_FEE.CUSTOMER)
				.offset(customerFeeParams.getOffset())
				.limit(customerFeeParams.getLimit())
			.fetch();
		
		System.out.println("Fee Without Booking size : " + feeWithoutBookingRecords.size());
		
		return feeWithoutBookingRecords.stream().map(new FeeWithOutBookingFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<BookingCheck> getBookingCheckList(AONContext ctx, CustomerFeeParams customerFeeParams){
		Condition condition = createCustomerFeeCondition(ctx, customerFeeParams);
		
		SelectOnConditionStep<Record> bookingCheckSelect = ctx.getDslContext().select().from(CUSTOMER_FEE)
				.join(DOMAIN).on(DOMAIN.ID.eq(CUSTOMER_FEE.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(CUSTOMER_FEE.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(RITEM).on(RITEM.REGISTRY.eq(CUSTOMER_FEE.CUSTOMER).and(RITEM.ITEM.eq(CUSTOMER_FEE.ITEM)));
		
		Result<Record> bookingCheckRecords = bookingCheckSelect
				.where(condition)
				.and(RITEM.TYPE.eq((byte)4))
				.orderBy(CUSTOMER_FEE.CUSTOMER)
				.offset(customerFeeParams.getOffset())
				.limit(customerFeeParams.getLimit())
			.fetch();
		
		System.out.println("Fee With Booking size : " + bookingCheckRecords.size());
		
		return bookingCheckRecords.stream().map(new FeeWithOutBookingFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<BookingCheck> getCustomerBookingCheckList(AONContext ctx, CustomerFeeParams customerFeeParams){
		Condition condition = createRitemCondition(ctx, customerFeeParams);
		
		SelectOnConditionStep<Record> bookingWithoutFeeSelect = ctx.getDslContext().select().from(RITEM)
				.join(DOMAIN).on(DOMAIN.ID.eq(RITEM.DOMAIN))
				.join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(RITEM.REGISTRY))
				.join(CUSTOMER_ALIAS).on(CUSTOMER.REGISTRY.eq(CUSTOMER_ALIAS.ID))
				.join(ITEM).on(ITEM.ID.eq(RITEM.ITEM))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.leftJoin(CUSTOMER_FEE).on(CUSTOMER_FEE.CUSTOMER.eq(RITEM.REGISTRY).and(CUSTOMER_FEE.ITEM.eq(RITEM.ITEM)));
		
		Result<Record> bookingWithoutFeeRecords = bookingWithoutFeeSelect
				.where(condition)
//				.and(CUSTOMER_FEE.ID.isNull())
				.and(RITEM.TYPE.eq((byte)4))
				.orderBy(RITEM.REGISTRY, CUSTOMER_FEE.LINE)
				.offset(customerFeeParams.getOffset())
				.limit(customerFeeParams.getLimit())
			.fetch();
		
		System.out.println("Customer Booking size : " + bookingWithoutFeeRecords.size());
		
		return bookingWithoutFeeRecords.stream().map(new CustomerBookingFiller()).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static BookingCheck save(AONContext ctx, BookingCheck bookingCheck) {
		ctx.checkWrite();
		
		if(bookingCheck.getItem().getProduct().isModify()) {
			ctx.getDslContext()
			.update(PRODUCT)
				.set(PRODUCT.NAME, bookingCheck.getItem().getProduct().getName())
				.where(PRODUCT.ID.eq(bookingCheck.getItem().getProduct().getId()))
				.execute();
		}
		
		return bookingCheck.getId() != null ? update(ctx, bookingCheck) : insert(ctx, bookingCheck);
	}
	
	private static BookingCheck insert(AONContext ctx, BookingCheck bookingCheck) {
		Date startDate = bookingCheck.getStartDate() != null ? new Date(bookingCheck.getStartDate().getTime()) : null;
		Date endDate = bookingCheck.getEndDate() != null ? new Date(bookingCheck.getEndDate().getTime()) : null;
		
		Integer id = ctx.getDslContext()
				.insertInto(
						RITEM, RITEM.DOMAIN, 
						RITEM.REGISTRY, 
						RITEM.ITEM, 
						RITEM.TYPE, 
						RITEM.PRICE, 
						RITEM.DISCOUNT_EXPR, 
						RITEM.WORKPLACE, 
						RITEM.STATUS, 
						RITEM.QUANTITY, 
						RITEM.START_DATE, 
						RITEM.END_DATE, 
						RITEM.CREATION_DATE, 
						RITEM.CREATION_USER)
				.values(
						bookingCheck.getDomain().getId(), 
						bookingCheck.getCustomer().getId(), 
						bookingCheck.getItem().getId(), 
						(byte) bookingCheck.getType().value(), 
						bookingCheck.getPrice(), 
						bookingCheck.getDiscountExpr(), 
						null == bookingCheck.getWorkplace() ? null : bookingCheck.getWorkplace().getId(),
						(byte) bookingCheck.getStatus().value(), 
						bookingCheck.getQuantity(), 
						startDate, 
						endDate, 
						new Timestamp(new java.util.Date().getTime()),
						ctx.getUser())
				.returning(CUSTOMER_FEE.ID).fetchOne().getValue(RITEM.ID);
		
		return bookingCheck.setId(id);
	}

	private static BookingCheck update(AONContext ctx, BookingCheck bookingCheck) {
		Date startDate = bookingCheck.getStartDate() != null ? new Date(bookingCheck.getStartDate().getTime()) : null;
		Date endDate = bookingCheck.getEndDate() != null ? new Date(bookingCheck.getEndDate().getTime()) : null;
		
		ctx.getDslContext().update(RITEM)
			.set(RITEM.REGISTRY, bookingCheck.getCustomer().getId())
			.set(RITEM.ITEM, bookingCheck.getItem().getId())
			.set(RITEM.TYPE, (byte) bookingCheck.getType().value())
			.set(RITEM.PRICE, bookingCheck.getPrice())
			.set(RITEM.DISCOUNT_EXPR, bookingCheck.getDiscountExpr())
			.set(RITEM.WORKPLACE, null == bookingCheck.getWorkplace() ? null : bookingCheck.getWorkplace().getId())
			.set(RITEM.STATUS, (byte) bookingCheck.getStatus().value())
			.set(RITEM.QUANTITY, bookingCheck.getQuantity())
			.set(RITEM.START_DATE, startDate)
			.set(RITEM.END_DATE, endDate)
			.set(RITEM.MODIFICATION_DATE, new Timestamp(new java.util.Date().getTime()))
			.set(RITEM.MODIFICATION_USER, ctx.getUser())
			.where(RITEM.ID.eq(bookingCheck.getId()))
			.execute();
		
		return bookingCheck;
	}
	
	public static void delete(CloseableAONContext ctx, LinkedList<BookingCheck> selectedBookings) {
		selectedBookings.forEach(bookingCheck -> {
			ctx.getDslContext().delete(RITEM)
				.where(RITEM.ID.eq(bookingCheck.getId()))
				.execute();
		});
	}

	private static Condition createCustomerFeeCondition(AONContext ctx, CustomerFeeParams customerFeeParams) {
		Condition condition = CUSTOMER_FEE.DOMAIN.eq(customerFeeParams.getDomain());
		
		User user = SecurityDAO.getUser(ctx);
		if(user.getDomain() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}
		
		if(null != customerFeeParams.getMonth() && null == customerFeeParams.getYear()) {
			condition = condition.and(DSL.month(CUSTOMER_FEE.BILLING_DATE).eq(customerFeeParams.getMonth() + 1));
		} else if(null == customerFeeParams.getMonth() && null != customerFeeParams.getYear()) {
			Date startBillingDate = new Date(customerFeeParams.getYear(), 0, 1);
			Date endBillingDate = new Date(customerFeeParams.getYear(), 11, 31);
			
			condition = condition.and(CUSTOMER_FEE.BILLING_DATE.between(startBillingDate, endBillingDate));	
		} else if(null != customerFeeParams.getMonth() && null != customerFeeParams.getYear()) {
			Date billingDate = new Date(customerFeeParams.getYear(), customerFeeParams.getMonth(), 1);
			
			condition = condition.and(CUSTOMER_FEE.BILLING_DATE.eq(parseSQLDate(billingDate)));
		}
			
		if(AonStringUtils.isNotBlank(customerFeeParams.getCustomer())) 
			condition = condition.and(CUSTOMER_ALIAS.NAME.eq(customerFeeParams.getCustomer()));
		
		if(null != customerFeeParams.getCustomerStatus())
			condition = condition.and(CUSTOMER.STATUS.eq(customerFeeParams.getCustomerStatus()));
		
		if(null != customerFeeParams.getProduct())
			condition = condition.and(CUSTOMER_FEE.ITEM.eq(customerFeeParams.getProduct()));
		
		if(null != customerFeeParams.getStartDate()){
			switch (customerFeeParams.getStartCompare()) {
			case (byte) 1:
				condition = condition.and(CUSTOMER_FEE.INITIAL_DATE.le(new Date(customerFeeParams.getStartDate().getTime())));
				break;
			case (byte) 2:
				condition = condition.and(CUSTOMER_FEE.INITIAL_DATE.ge(new Date(customerFeeParams.getStartDate().getTime())));
				break;
			default:
				condition = condition.and(CUSTOMER_FEE.INITIAL_DATE.eq(new Date(customerFeeParams.getStartDate().getTime())));
				break;
			}
		}
		
		if(null != customerFeeParams.getEndDate()){
			switch (customerFeeParams.getEndCompare()) {
			case (byte) 1:
				condition = condition.and(CUSTOMER_FEE.FINAL_DATE.le(new Date(customerFeeParams.getEndDate().getTime())));
				break;
			case (byte) 2:
				condition = condition.and(CUSTOMER_FEE.FINAL_DATE.ge(new Date(customerFeeParams.getEndDate().getTime())));
				break;
			default:
				condition = condition.and(CUSTOMER_FEE.FINAL_DATE.eq(new Date(customerFeeParams.getEndDate().getTime())));
				break;
			}
		}

		if(null != customerFeeParams.getDomainType()){
			condition = condition.and(DOMAIN.AONSTATUS.eq(customerFeeParams.getDomainType()));
		}
		
		if(null != customerFeeParams.getDomainStatus()){
			condition = condition.and(DOMAIN.ACTIVE.eq(customerFeeParams.getDomainStatus()));
		}
		
		return condition;
	}
	
	private static Condition createRitemCondition(AONContext ctx, CustomerFeeParams customerFeeParams) {
		Condition condition = RITEM.DOMAIN.eq(customerFeeParams.getDomain());
		
		User user = SecurityDAO.getUser(ctx);
		if(user.getDomain() == ctx.getDomainId()) {
			Integer[] userScopes = SecurityDAO.getUserScopes(ctx);
			condition = condition.and(CUSTOMER.SCOPE.in(userScopes));
		}
			
		if(AonStringUtils.isNotBlank(customerFeeParams.getCustomer())) 
			condition = condition.and(CUSTOMER_ALIAS.NAME.eq(customerFeeParams.getCustomer()));
		
		if(null != customerFeeParams.getCustomerStatus())
			condition = condition.and(CUSTOMER.STATUS.eq(customerFeeParams.getCustomerStatus()));
		
		if(null != customerFeeParams.getProduct())
			condition = condition.and(RITEM.ITEM.eq(customerFeeParams.getProduct()));
		
		if(null != customerFeeParams.getProductStatus())
			condition = condition.and(RITEM.STATUS.eq(customerFeeParams.getProductStatus()));
		
		if(null != customerFeeParams.getStartDate()){
			switch (customerFeeParams.getStartCompare()) {
			case (byte) 1:
				condition = condition.and(RITEM.START_DATE.le(new Date(customerFeeParams.getStartDate().getTime())));
				break;
			case (byte) 2:
				condition = condition.and(RITEM.START_DATE.ge(new Date(customerFeeParams.getStartDate().getTime())));
				break;
			default:
				condition = condition.and(RITEM.START_DATE.eq(new Date(customerFeeParams.getStartDate().getTime())));
				break;
			}
		}
		
		if(null != customerFeeParams.getEndDate()){
			switch (customerFeeParams.getEndCompare()) {
			case (byte) 1:
				condition = condition.and(RITEM.END_DATE.le(new Date(customerFeeParams.getEndDate().getTime())));
				break;
			case (byte) 2:
				condition = condition.and(RITEM.END_DATE.ge(new Date(customerFeeParams.getEndDate().getTime())));
				break;
			default:
				condition = condition.and(RITEM.END_DATE.eq(new Date(customerFeeParams.getEndDate().getTime())));
				break;
			}
		}
		
		if(null != customerFeeParams.getDomainType()){
			condition = condition.and(DOMAIN.AONSTATUS.eq(customerFeeParams.getDomainType()));
		}
		
		if(null != customerFeeParams.getDomainStatus()){
			condition = condition.and(DOMAIN.ACTIVE.eq(customerFeeParams.getDomainStatus()));
		}
		
		return condition;
	}
	
	protected static class FeeWithOutBookingFiller extends Filler implements Function<Record, BookingCheck> {

		@Override
		public BookingCheck apply(Record r) {
			return buildFee(r);
		}
		
		public static BookingCheck buildFee(Record r) {			
			return new BookingCheck()
					.setId(r.getValue(CUSTOMER_FEE.ID))
					.setDomain(checkField(r, DOMAIN.ID) 
						? DomainFiller.build(r) 
						: new Domain().setId(r.getValue(CUSTOMER_FEE.DOMAIN)))
					.setCustomer(checkField(r, CUSTOMER.REGISTRY)
						? CustomerFiller.buildCustomer(r, CUSTOMER_ALIAS)
						: new Customer().copy(new Registry().setId(r.getValue(CUSTOMER_FEE.CUSTOMER))))
					.setItem(checkField(r, ITEM.ID)
						? ItemFiller.buildItem(r)
						: new OldItem().setId(r.getValue(CUSTOMER_FEE.ITEM)))
					.setType(null == r.getValue(RITEM.TYPE) ? null : RegistryMode.values()[r.getValue(RITEM.TYPE)])
					.setStatus(null == r.getValue(RITEM.STATUS) ? null :RegistryItemStatus.values()[r.getValue(RITEM.STATUS)])
					.setQuantity(r.getValue(CUSTOMER_FEE.QUANTITY) + "")
					.setPrice(r.getValue(CUSTOMER_FEE.PRICE))
					.setDiscountExpr(r.getValue(CUSTOMER_FEE.DISCOUNT_EXPR))
					.setStartDate(r.getValue(CUSTOMER_FEE.INITIAL_DATE))
					.setEndDate(r.getValue(CUSTOMER_FEE.FINAL_DATE))
					.setWorkplace(checkField(r, WORKPLACE.ID)
							? WorkplaceFiller.build(r)
							: new Workplace().setId(r.getValue(CUSTOMER_FEE.WORKPLACE)))
					;
		}
	}
	
	protected static class BookingWithOutFeeFiller extends Filler implements Function<Record, BookingCheck> {

		@Override
		public BookingCheck apply(Record r) {
			return buildFee(r);
		}
		
		public static BookingCheck buildFee(Record r) {			
			return new BookingCheck()
					.setId(r.getValue(RITEM.ID))
					.setDomain(checkField(r, RITEM.ID) 
						? DomainFiller.build(r) 
						: new Domain().setId(r.getValue(RITEM.DOMAIN)))
					.setCustomer(checkField(r, CUSTOMER.REGISTRY)
						? CustomerFiller.buildCustomer(r, CUSTOMER_ALIAS)
						: new Customer().copy(new Registry().setId(r.getValue(RITEM.REGISTRY))))
					.setItem(checkField(r, ITEM.ID)
						? ItemFiller.buildItem(r)
						: new OldItem().setId(r.getValue(RITEM.ITEM)))	
					.setType(RegistryMode.values()[r.getValue(RITEM.TYPE)])
					.setStatus(RegistryItemStatus.values()[r.getValue(RITEM.STATUS)])
					.setQuantity(r.getValue(RITEM.QUANTITY))
					.setPrice(r.getValue(RITEM.PRICE))
					.setDiscountExpr(r.getValue(RITEM.DISCOUNT_EXPR))
					.setStartDate(r.getValue(RITEM.START_DATE))
					.setEndDate(r.getValue(RITEM.END_DATE))
					.setWorkplace(checkField(r, WORKPLACE.ID)
							? WorkplaceFiller.build(r)
							: new Workplace().setId(r.getValue(RITEM.WORKPLACE)))
					;
		}
	}
	
	protected static class CustomerBookingFiller extends Filler implements Function<Record, BookingCheck> {

		@Override
		public BookingCheck apply(Record r) {
			return buildFee(r);
		}
		
		public static BookingCheck buildFee(Record r) {			
			return new BookingCheck()
					.setId(r.getValue(RITEM.ID))
					.setDomain(checkField(r, RITEM.ID) 
						? DomainFiller.build(r) 
						: new Domain().setId(r.getValue(RITEM.DOMAIN)))
					.setCustomer(checkField(r, CUSTOMER.REGISTRY)
						? CustomerFiller.buildCustomer(r, CUSTOMER_ALIAS)
						: new Customer().copy(new Registry().setId(r.getValue(RITEM.REGISTRY))))
					.setItem(checkField(r, ITEM.ID)
						? ItemFiller.buildItem(r)
						: new OldItem().setId(r.getValue(RITEM.ITEM)))	
					.setType(RegistryMode.values()[r.getValue(RITEM.TYPE)])
					.setStatus(RegistryItemStatus.values()[r.getValue(RITEM.STATUS)])
					.setQuantity(r.getValue(RITEM.QUANTITY))
					.setPrice(r.getValue(RITEM.PRICE))
					.setDiscountExpr(r.getValue(RITEM.DISCOUNT_EXPR))
					.setStartDate(r.getValue(RITEM.START_DATE))
					.setEndDate(r.getValue(RITEM.END_DATE))
					.setWorkplace(checkField(r, WORKPLACE.ID)
							? WorkplaceFiller.build(r)
							: new Workplace().setId(r.getValue(RITEM.WORKPLACE)))
					.setHasFee(r.get(CUSTOMER_FEE.ID) != null)
					.setQuantityFee(null != r.get(CUSTOMER_FEE.QUANTITY) ? r.get(CUSTOMER_FEE.QUANTITY).intValue() + "" : "0")
					.setQuantityRItem(r.get(RITEM.QUANTITY))
					;
		}
	}
	
	private static java.sql.Date parseSQLDate(java.util.Date date){
		return null == date ? null : new java.sql.Date(date.getTime());
	}
	
}
