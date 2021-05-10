package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;

import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.Properties.PayMethodProperties;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PayMethodTypeDetail;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PayMethodDAO {

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
	private static class PayMethodFiller  implements Function<Record,PayMethod> {
		@Override
		public PayMethod apply(Record record) {
			return new PayMethod()
				.setId(record.getValue(PAY_METHOD.ID))
				.setDomain(record.getValue(PAY_METHOD.DOMAIN))
				.setName(record.getValue(PAY_METHOD.NAME))
				.setType(PayMethodType.safeValueOf( record.getValue(PAY_METHOD.TYPE)));
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
		
		public static void validate(AONContext ctx, PayMethod pm) throws AonCoreException{
			EMPTY_DOMAIN
			.andThen(EMPTY_NAME)
			.andThen(OVERFLOW_NAME)
			.accept(pm, ctx);
		}

		public static void validateDeletion(AONContext ctx, Integer id) {
			// TODO Auto-generated method stub
		}
	}
	
	public static class PayMethodAutoComplete {
		
		public static BiConsumer<AONContext,PayMethod> COMPLETE_TYPE = (ctx,payMethod) -> {
			if (payMethod.getType() == null) {
				ctx.log().info("\t saving pay method: autocomplete type: " + PayMethodType.CASH_BASIS);
				payMethod.setType(PayMethodType.CASH_BASIS);
			}
		};

		public static void autoComplete(AONContext ctx, PayMethod payMethod) throws AonCoreException {
			COMPLETE_TYPE.accept(ctx, payMethod);

		}

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
		ctx.checkRead();
		return getSelect(ctx, p -> p.getIdProperty().eq(id))
			.fetch()
			.stream()
			.map( new PayMethodFiller())
			.findFirst()
			.orElse(null);
	}

	public static PayMethod  get(AONContext ctx, String name) {
		ctx.checkRead();
		return getSelect(ctx, p -> p.getNameProperty().eq(name))
			.fetch()
			.stream()
			.map( new PayMethodFiller())
			.findFirst()
			.orElse(null);
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
		ctx.log().info("INSERT PAY METHOD id: " + payMethod.getId());
		return payMethod; 
	}
	
	private static PayMethod update(AONContext ctx, PayMethod payMethod) {
		int count = ctx.getDslContext().update(PAY_METHOD)
			.set(PAY_METHOD.NAME, payMethod.getName())
			.set(PAY_METHOD.TYPE, payMethod.getType().value())
			.where(PAY_METHOD.ID.eq(payMethod.getId()))
			.execute();
		ctx.log().info("UPDATE PAY METHOD id: " + payMethod.getId() + ". (" + count + " rows)");
		return payMethod; 
	}
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		PayMethodValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(PAY_METHOD)
			.where(PAY_METHOD.ID.eq(id))
			.execute();
		ctx.log().info("DELETE PAY METHOD id:" + id + " ("+count+" rows)");
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


