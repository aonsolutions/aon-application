package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.PosShiftCount.POS_SHIFT_COUNT;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Rsupplier.RSUPPLIER;
import static com.esferalia.aon.jooq.tables.Sales.SALES;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.PayMethodRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Filter.PayMethodFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.PayMethodParams;
import com.esferalia.aon.occam.api.model.Properties.PayMethodProperties;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PayMethodTypeDetail;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PayMethodDAO {
	private PayMethodDAO() {
		
	}
	private static final PayMethodPropertiesDAO PAYMETHOD_PROPERTIES = new PayMethodPropertiesDAO();
	private static class PayMethodPropertiesDAO implements PayMethodProperties {
		private Condition[] getConditions(PayMethodFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PAY_METHOD.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PAY_METHOD.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(PAY_METHOD.NAME);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(PAY_METHOD.TYPE);}
	}
	
	public static class PayMethodFiller  implements Function<Record,PayMethod> {
		@Override
		public PayMethod apply(Record record) {
			return build(record);
		}
		
		public static PayMethod build(Record r) {
			return new PayMethod()
					.setId(r.getValue(PAY_METHOD.ID))
					.setDomain(r.getValue(PAY_METHOD.DOMAIN))
					.setName(r.getValue(PAY_METHOD.NAME))
					.setType(PayMethodType.safeValueOf(r.getValue(PAY_METHOD.TYPE)));
		}
	}
	
	public static class PayMethodValidation {
		
		public static BiConsumer<PayMethod,AONContext> EMPTY_DOMAIN = (pm,ctx) -> {
			if (pm.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		private static BiConsumer<PayMethod,AONContext> EMPTY_NAME  = (pm,ctx) -> {
			if (pm.getName() == null) 
				throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
		};
		
		public static BiConsumer<PayMethod,AONContext> OVERFLOW_NAME = (pm,ctx) -> {
			if (AonStringUtils.length(pm.getName()) > PAY_METHOD.NAME.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( "Nombre", PAY_METHOD.NAME.getDataType().length() ));
		};
		
		public static BiConsumer<Integer, AONContext> EXIST_DELIVERY = (pmId, ctx) -> {
			Integer deliveryCount = ctx.getDslContext().selectCount().from(DELIVERY).where(DELIVERY.PAY_METHOD.eq(pmId)).fetchOne().value1();
			if(null != deliveryCount && deliveryCount > 0)
				throw new AonCoreException("Existen " + deliveryCount + " albaranes de venta que tienen esta forma de pago.");
		};
		
		public static BiConsumer<Integer, AONContext> EXIST_FINANCE = (pmId, ctx) -> {
			Integer financeCount = ctx.getDslContext().selectCount().from(FINANCE).where(FINANCE.PAY_METHOD.eq(pmId)).fetchOne().value1();
			if(null != financeCount && financeCount > 0)
				throw new AonCoreException("Existen " + financeCount + " vencimientos que tienen esta forma de pago.");
		};
		
		public static BiConsumer<Integer, AONContext> EXIST_INCOME = (pmId, ctx) -> {
			Integer incomeCount = ctx.getDslContext().selectCount().from(INCOME).where(INCOME.PAY_METHOD.eq(pmId)).fetchOne().value1();
			if(null != incomeCount && incomeCount > 0)
				throw new AonCoreException("Existen " + incomeCount + " albaranes de compra que tienen esta forma de pago.");
		};
		
		public static BiConsumer<Integer, AONContext> EXIST_OFFER = (pmId, ctx) -> {
			Integer offerCount = ctx.getDslContext().selectCount().from(OFFER).where(OFFER.PAY_METHOD.eq(pmId)).fetchOne().value1();
			if(null != offerCount && offerCount > 0)
				throw new AonCoreException("Existen " + offerCount + " presupuestos que tienen esta forma de pago.");
		};
		
		public static BiConsumer<Integer, AONContext> EXIST_POSSHIFTCOUNT = (pmId, ctx) -> {
			Integer posShiftCount = ctx.getDslContext().selectCount().from(POS_SHIFT_COUNT).where(POS_SHIFT_COUNT.PAY_METHOD.eq(pmId)).fetchOne().value1();
			if(null != posShiftCount && posShiftCount > 0)
				throw new AonCoreException("Existen " + posShiftCount + " arqueos del TPV que tienen esta forma de pago.");
		};
		
		public static BiConsumer<Integer, AONContext> EXIST_PURCHASE = (pmId, ctx) -> {
			Integer purchaseCount = ctx.getDslContext().selectCount().from(PURCHASE).where(PURCHASE.PAY_METHOD.eq(pmId)).fetchOne().value1();
			if(null != purchaseCount && purchaseCount > 0)
				throw new AonCoreException("Existen " + purchaseCount + " pedidos de compra que tienen esta forma de pago.");
		};
		
		public static BiConsumer<Integer, AONContext> EXIST_RPAYMETHOD = (pmId, ctx) -> {
			Integer rpaymethodCount = ctx.getDslContext().selectCount().from(RPAYMETHOD).where(RPAYMETHOD.PAY_METHOD.eq(pmId)).fetchOne().value1();
			if(null != rpaymethodCount && rpaymethodCount > 0)
				throw new AonCoreException("Existen " + rpaymethodCount + " datos de pago de personas fisicas o juridicas que tienen esta forma de pago.");
		};
		
		public static BiConsumer<Integer, AONContext> EXIST_RSUPPLIER = (pmId, ctx) -> {
			Integer rsupplierCount = ctx.getDslContext().selectCount().from(RSUPPLIER).where(RSUPPLIER.PAY_METHOD.eq(pmId)).fetchOne().value1();
			if(null != rsupplierCount && rsupplierCount > 0)
				throw new AonCoreException("Existen " + rsupplierCount + " datos de pago de proveedores que tienen esta forma de pago.");
		};
		
		public static BiConsumer<Integer, AONContext> EXIST_SALES = (pmId, ctx) -> {
			Integer salesCount = ctx.getDslContext().selectCount().from(SALES).where(SALES.PAY_METHOD.eq(pmId)).fetchOne().value1();
			if(null != salesCount && salesCount > 0)
				throw new AonCoreException("Existen " + salesCount + " pedidos de venta que tienen esta forma de pago.");
		};
		
		public static void validate(AONContext ctx, PayMethod pm) throws AonCoreException{
			EMPTY_DOMAIN
			.andThen(EMPTY_NAME)
			.andThen(OVERFLOW_NAME)
			.accept(pm, ctx);
		}

		public static void validateDeletion(AONContext ctx, Integer pmId) {
			EXIST_DELIVERY
			.andThen(EXIST_FINANCE)
			.andThen(EXIST_INCOME)
			.andThen(EXIST_OFFER)
			.andThen(EXIST_POSSHIFTCOUNT)
			.andThen(EXIST_PURCHASE)
			.andThen(EXIST_RPAYMETHOD)
			.andThen(EXIST_RSUPPLIER)
			.andThen(EXIST_SALES)
			.accept(pmId, ctx);
		}
	}
	
	public static class PayMethodAutoComplete {
		
		public static BiConsumer<AONContext,PayMethod> COMPLETE_TYPE = (ctx,payMethod) -> {
			if (payMethod.getType() == null) {
				ctx.log().debug("\t saving pay method: autocomplete type: {0}",PayMethodType.CASH_BASIS);
				payMethod.setType(PayMethodType.CASH_BASIS);
			}
		};

		public static void autoComplete(AONContext ctx, PayMethod payMethod) throws AonCoreException {
			COMPLETE_TYPE.accept(ctx, payMethod);

		}

	}
	
	public static LinkedList<PayMethod> getList(AONContext ctx, PayMethodParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		SelectConditionStep<Record> select = ctx.getDslContext().select()
			.from(PAY_METHOD)
			.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(PAY_METHOD.NAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "type"))
				select.orderBy(PAY_METHOD.TYPE);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(PAY_METHOD.NAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "type"))
				select.orderBy(PAY_METHOD.TYPE.desc());
		}
				
		LinkedList<PayMethod> paymethods = select
				.limit(params.getOffset(), params.getLimit())
				.fetch()
				.stream()
				.map(new PayMethodFiller())
				.collect(Collectors.toCollection(LinkedList::new));
			
		System.out.println("paymethods : " + paymethods.size());
		
		return paymethods;
	}
	
	private static Condition paramsToCondition(AONContext ctx, PayMethodParams params) {
		Condition condition = PAY_METHOD.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));
		
		if(AonStringUtils.isNotBlank(params.getDescription()))
			condition = condition.and(PAY_METHOD.NAME.like("%" + params.getDescription() + "%"));
		
		if(null != params.getType())
			condition = condition.and(PAY_METHOD.TYPE.eq(params.getType()));
		
		return condition;
	}
	
	private static SelectConditionStep<PayMethodRecord> getSelect(AONContext ctx, PayMethodFilter filter) {
		ctx.checkRead();
		return ctx.getDslContext()
				.selectFrom(PAY_METHOD)
				.where(PAYMETHOD_PROPERTIES.getConditions(filter))
				.and(PAY_METHOD.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)));
	}
	
	public static LinkedList<PayMethod>  getOrderByNames(AONContext ctx) {
		ctx.checkRead();
		return getSelect(ctx, null)
			.orderBy(PAY_METHOD.NAME)
			.fetch()
			.stream()
			.map( new PayMethodFiller())
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<PayMethod>  getOrderByIds(AONContext ctx) {
		ctx.checkRead();
		return getSelect(ctx, null)
			.orderBy(PAY_METHOD.ID)
			.fetch()
			.stream()
			.map( new PayMethodFiller())
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static PayMethod  get(AONContext ctx, Integer id) {
		return get(ctx, p -> p.getIdProperty().eq(id));
	}

	public static PayMethod  get(AONContext ctx, String name) {
		return get(ctx, p -> p.getNameProperty().eq(name));
	}
	
	public static PayMethod get(AONContext ctx, PayMethodFilter filter) {
		ctx.checkRead();
		return getSelect(ctx, filter)
			.fetch()
			.stream()
			.map( new PayMethodFiller())
			.findFirst()
			.orElse(null);
	}
	
	public static PayMethod save(AONContext ctx, PayMethod payMethod) {
		ctx.checkWrite();
		PayMethodAutoComplete.autoComplete(ctx, payMethod);
		PayMethodValidation.validate(ctx, payMethod);
		if (payMethod.getId() == null) {
			payMethod = insert(ctx, payMethod);
		} else {
			payMethod = update(ctx, payMethod);			
		}
		return payMethod;
	}

	private static PayMethod insert(AONContext ctx, PayMethod payMethod) {
		Integer id = ctx.getDslContext().insertInto(PAY_METHOD)
			.set(PAY_METHOD.DOMAIN, payMethod.getDomain())
			.set(PAY_METHOD.NAME, payMethod.getName())
			.set(PAY_METHOD.TYPE, payMethod.getType().value())
			.returning(PAY_METHOD.ID)
			.fetchOne()
			.getValue(PAY_METHOD.ID);
		payMethod.setId(id);
		ctx.log().debug("INSERT PAY METHOD id: {0}",payMethod.getId());
		return payMethod; 
	}
	
	private static PayMethod update(AONContext ctx, PayMethod payMethod) {
		int count = ctx.getDslContext().update(PAY_METHOD)
			.set(PAY_METHOD.NAME, payMethod.getName())
			.set(PAY_METHOD.TYPE, payMethod.getType().value())
			.where(PAY_METHOD.ID.eq(payMethod.getId()))
			.execute();
		ctx.log().debug("UPDATE PAY METHOD id: {0} ({1} rows)",payMethod.getId(),count);
		return payMethod; 
	}
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		PayMethodValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(PAY_METHOD)
			.where(PAY_METHOD.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE PAY METHOD id: {0} ({1} rows)",id,count);
	}
	
	public static void merge(AONContext ctx, List<PayMethod> selectedPaymethods, PayMethod groupedPaymthod) {
		ctx.checkWrite();
		
		ctx.getDslContext().update(DELIVERY)
			.set(DELIVERY.PAY_METHOD, groupedPaymthod.getId())
			.where(DELIVERY.PAY_METHOD.in(selectedPaymethods.stream().map(PayMethod::getId).toList()))
			.and(DELIVERY.DOMAIN.eq(groupedPaymthod.getDomain()))
			.execute();

		ctx.getDslContext().update(FINANCE)
			.set(FINANCE.PAY_METHOD, groupedPaymthod.getId())
			.where(FINANCE.PAY_METHOD.in(selectedPaymethods.stream().map(PayMethod::getId).toList()))
			.and(FINANCE.DOMAIN.eq(groupedPaymthod.getDomain()))
			.execute();
		
		ctx.getDslContext().update(INCOME)
			.set(INCOME.PAY_METHOD, groupedPaymthod.getId())
			.where(INCOME.PAY_METHOD.in(selectedPaymethods.stream().map(PayMethod::getId).toList()))
			.and(INCOME.DOMAIN.eq(groupedPaymthod.getDomain()))
			.execute();
		
		ctx.getDslContext().update(OFFER)
			.set(OFFER.PAY_METHOD, groupedPaymthod.getId())
			.where(OFFER.PAY_METHOD.in(selectedPaymethods.stream().map(PayMethod::getId).toList()))
			.and(OFFER.DOMAIN.eq(groupedPaymthod.getDomain()))
			.execute();
		
		ctx.getDslContext().update(POS_SHIFT_COUNT)
			.set(POS_SHIFT_COUNT.PAY_METHOD, groupedPaymthod.getId())
			.where(POS_SHIFT_COUNT.PAY_METHOD.in(selectedPaymethods.stream().map(PayMethod::getId).toList()))
			.and(POS_SHIFT_COUNT.DOMAIN.eq(groupedPaymthod.getDomain()))
			.execute();
		
		ctx.getDslContext().update(PURCHASE)
			.set(PURCHASE.PAY_METHOD, groupedPaymthod.getId())
			.where(PURCHASE.PAY_METHOD.in(selectedPaymethods.stream().map(PayMethod::getId).toList()))
			.and(PURCHASE.DOMAIN.eq(groupedPaymthod.getDomain()))
			.execute();
		
		ctx.getDslContext().update(RPAYMETHOD)
			.set(RPAYMETHOD.PAY_METHOD, groupedPaymthod.getId())
			.where(RPAYMETHOD.PAY_METHOD.in(selectedPaymethods.stream().map(PayMethod::getId).toList()))
			.and(RPAYMETHOD.DOMAIN.eq(groupedPaymthod.getDomain()))
			.execute();
		
		ctx.getDslContext().update(RSUPPLIER)
			.set(RSUPPLIER.PAY_METHOD, groupedPaymthod.getId())
			.where(RSUPPLIER.PAY_METHOD.in(selectedPaymethods.stream().map(PayMethod::getId).toList()))
			.and(RSUPPLIER.DOMAIN.eq(groupedPaymthod.getDomain()))
			.execute();
		
		ctx.getDslContext().update(SALES)
			.set(SALES.PAY_METHOD, groupedPaymthod.getId())
			.where(SALES.PAY_METHOD.in(selectedPaymethods.stream().map(PayMethod::getId).toList()))
			.and(SALES.DOMAIN.eq(groupedPaymthod.getDomain()))
			.execute();

		
		ctx.getDslContext().delete(PAY_METHOD)
			.where(PAY_METHOD.ID.in(selectedPaymethods.stream().filter(selectedPaymethod -> !selectedPaymethod.getId().equals(groupedPaymthod.getId())).map(PayMethod::getId).toList()))
			.execute();
		
		ctx.log().debug("DELETE PAY METHODS : {0}" , selectedPaymethods.size());
	}
	

	// -------------------------------------------------------------
	// ---------------------- PayMethodType ------------------------
	// -------------------------------------------------------------
	
	private static class FullPayMethodTypeDetailFiller  implements Function<Record,PayMethodTypeDetail> {
		@Override
		public PayMethodTypeDetail apply(Record record) {
			return new PayMethodTypeDetail()
				.setId(record.getValue(PM_TYPE_DETAIL.ID))
				.setDomain(record.getValue(PM_TYPE_DETAIL.DOMAIN))
				.setDescription(record.getValue(PM_TYPE_DETAIL.DESCRIPTION))
				.setType(PayMethodType.safeValueOf( record.getValue(PM_TYPE_DETAIL.TYPE)))
				.setAccount(new Account()
					.setId(record.getValue(ACCOUNT.ID))
					.setCode(record.getValue(ACCOUNT.CODE))
					.setDescription(record.getValue(ACCOUNT.DESCRIPTION)));
		}
	}
	public static LinkedList<PayMethodTypeDetail>  getPayMethodTypeDetails(AONContext ctx) {
		return ctx.getDslContext()
				.select()
				.from(PM_TYPE_DETAIL)
				.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(PM_TYPE_DETAIL.ACCOUNT))
				.where(PM_TYPE_DETAIL.DOMAIN.eq(ctx.getDomainId()))
				.orderBy(PM_TYPE_DETAIL.DESCRIPTION)
				.fetch()
				.stream()
				.map( new FullPayMethodTypeDetailFiller())
				.collect(Collectors.toCollection(LinkedList::new))
				;
	}
	
}


