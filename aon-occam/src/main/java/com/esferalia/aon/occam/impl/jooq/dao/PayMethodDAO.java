package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PayMethodTypeDetail;
import com.esferalia.aon.occam.api.model.type.PayMethodType;

public class PayMethodDAO {

	
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
	
	public static LinkedList<PayMethod>  getPayMethods(AONContext ctx) {
		return ctx.getDslContext()
				.selectFrom(PAY_METHOD)
				.where(PAY_METHOD.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(PAY_METHOD.NAME)
				.fetch()
				.stream()
				.map( new FullPayMethodFiller())
				.collect(Collectors.toCollection(LinkedList::new))
				;
	}
	
	public static LinkedList<PayMethod>  getPayMethodsById(AONContext ctx) {
		return ctx.getDslContext()
				.selectFrom(PAY_METHOD)
				.where(PAY_METHOD.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx)))
				.orderBy(PAY_METHOD.ID)
				.fetch()
				.stream()
				.map( new FullPayMethodFiller())
				.collect(Collectors.toCollection(LinkedList::new))
				;
	}

	public static PayMethod  getPayMethod(AONContext ctx, Integer id) {
		return ctx.getDslContext()
				.selectFrom(PAY_METHOD)
				.where(PAY_METHOD.DOMAIN.eq(ctx.getDomainId()))
				.and(PAY_METHOD.ID.eq(id))
				.fetch()
				.stream()
				.map( new FullPayMethodFiller())
				.findFirst().orElse(new PayMethod());
	}

	public static PayMethod  getPayMethod(AONContext ctx, String name) {
		return ctx.getDslContext()
				.selectFrom(PAY_METHOD)
				.where(PAY_METHOD.DOMAIN.eq(ctx.getDomainId()))
				.and(PAY_METHOD.NAME.eq(name))
				.fetch()
				.stream()
				.map( new FullPayMethodFiller())
				.findFirst().orElse(new PayMethod());
	}
	
	public static PayMethod insertPayMethod(AONContext ctx, PayMethod paymethod) {
		Integer id = ctx.getDslContext().insertInto(PAY_METHOD)
				.set(PAY_METHOD.DOMAIN, paymethod.getDomain())
				.set(PAY_METHOD.NAME, paymethod.getName())
				.set(PAY_METHOD.TYPE, paymethod.getType().value())
				.execute();
		return paymethod.setId(id);
	}

	// -------------------------------------------------------------
	// ---------------------------- MAP ----------------------------
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
	private static class FullPayMethodFiller  implements Function<Record,PayMethod> {
		@Override
		public PayMethod apply(Record record) {
			return new PayMethod()
				.setId(record.getValue(PAY_METHOD.ID))
				.setDomain(record.getValue(PAY_METHOD.DOMAIN))
				.setName(record.getValue(PAY_METHOD.NAME))
				.setType(PayMethodType.safeValueOf( record.getValue(PAY_METHOD.TYPE)));
		}
	}
}


