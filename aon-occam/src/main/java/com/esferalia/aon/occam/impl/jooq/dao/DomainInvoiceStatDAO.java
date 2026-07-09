package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Rawdoc.RAWDOC;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainInvoiceStat;
import com.esferalia.aon.occam.api.model.DomainInvoiceStatParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DomainInvoiceStatDAO {
	
	private DomainInvoiceStatDAO() {
	}

	public static Stream<DomainInvoiceStat> stream(AONContext ctx, DomainInvoiceStatParams params){
		Domain domain = DomainDAO.getDomain(ctx, params.getDomain());
		if ( domain == null ) {
			return Stream.empty();
		}
		return ctx.getDslContext()
			.select(DOMAIN.ID, DOMAIN.PARENT, DOMAIN.NAME, DOMAIN.DESCRIPTION, DOMAIN.ACTIVE, DOMAIN.EXPIRATIONDATE
				, REGISTRY.ID, REGISTRY.DOCUMENT, REGISTRY.NAME)
			.from(DOMAIN)
			.innerJoin(COMPANY).on(COMPANY.DOMAIN.eq(DOMAIN.ID))
			.innerJoin(REGISTRY).on(REGISTRY.ID.eq(COMPANY.REGISTRY))
			.where(getCondition(ctx,domain,params))
			.orderBy(REGISTRY.NAME)
			.limit(params.getLimit())
			.offset(params.getOffset())
			.fetch()
			.stream()
			.map(new DomainInvoiceStatFiller())
			.map( stat -> fillData( ctx, params, stat ) )
			.filter(stat -> filter(stat, params))
		;
	}

	private static boolean filter(DomainInvoiceStat stat, DomainInvoiceStatParams params) {
		int f = params.getScoredFilter().orElse(0).intValue();
		//	0 - "Todas"
		if (f == 0) return true;
		
		//	1 - "Pendiente de declarar"
		if (f == 1) return (
			stat.getUndeclaredIssued() +
			stat.getUndeclaredReceived() + 
			stat.getUndeclaredSimplified()) > 0;
		
		//	2 - "Algo pendiente"
		if (f == 2) return (
			stat.getProformas() +
			stat.getUnrecordedIssued()+
			stat.getUnrecordedReceived() +
			stat.getUnrecordedSimplified() +
			stat.getDraft() + 
			stat.getInProcess()) > 0;

		
		//	3 - "Facturas pendientes"
		if (f == 3) return (
			stat.getProformas() +
			stat.getUnrecordedIssued()+
			stat.getUnrecordedReceived() +
			stat.getUnrecordedSimplified()) > 0;
		
		//	4 - "Documentos pendientes"
		if (f == 4) return (
			stat.getDraft() + 
			stat.getInProcess()) > 0;

		return true;
	}

	private static DomainInvoiceStat fillData(AONContext ctx, DomainInvoiceStatParams params, DomainInvoiceStat stat) {
		if (params.getInvoices().orElse(true)) {
			fillInvoicesData( ctx, params, stat );
			fillProformasData( ctx, params, stat );
		}
		if (params.getRawdoc().orElse(true)) {
			fillRawdocsData( ctx, stat );
		}
		if (params.getAlcatraz().orElse(true)) {
			fillAlcatrazData( ctx, params, stat );
		}
		return stat;
	}
	
	private static void fillInvoicesData(AONContext ctx, DomainInvoiceStatParams params, DomainInvoiceStat stat) {
		Field<Integer> invCount = DSL.count( INVOICE.ID );
		ctx.getDslContext().select( invCount, INVOICE.TYPE, INVOICE.TRANSACTION, INVOICE.WITHHOLDING, INVOICE.STATUS)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(stat.getId()))
			.and( params.getFromDate()
				.map(AonDateUtils::toSql)
				.map(INVOICE.ISSUE_DATE::ge)
				.orElse(DSL.noCondition()))
			.and( params.getToDate()
				.map(AonDateUtils::toSql)
				.map(INVOICE.ISSUE_DATE::le)
				.orElse(DSL.noCondition()))
			.groupBy(INVOICE.TYPE, INVOICE.TRANSACTION, INVOICE.WITHHOLDING, INVOICE.STATUS)
			.fetch()
			.forEach( r -> {
				Integer c = r.get(invCount);
				InvoiceTransactionType transaction = InvoiceTransactionType.safeValueOf( r.get(INVOICE.TRANSACTION) );
				if (transaction == InvoiceTransactionType.NATIONAL) stat.addNational(c);
				else if (transaction == InvoiceTransactionType.INTRACOMMUNITY) stat.addIntracommunity(c);
				else if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) stat.addExtracommunity(c);
				else if (transaction == InvoiceTransactionType.CAN_CEU_MEL) stat.addCanCeuMel(c);
				else if (transaction == InvoiceTransactionType.OTHER_ISP) stat.addOtherISP(c);
				
				InvoiceType type = InvoiceType.safeValueOf( r.get(INVOICE.TYPE) );
				InvoiceStatus status = InvoiceStatus.safeValueOf( r.get(INVOICE.STATUS) );
				if (status == InvoiceStatus.PENDING ) {
					if (type.isSales()) stat.addUnrecordedIssued(c);
					else if (type.isUndeductible()) stat.addUnrecordedSimplified(c);
					else stat.addUnrecordedReceived(c);
				}
				
				boolean withholding = AonEnumUtils.getBoolean( r.get(INVOICE.WITHHOLDING));
				if (withholding) stat.addWithholding(c);
			});
	}

	private static void fillProformasData(AONContext ctx, DomainInvoiceStatParams params, DomainInvoiceStat stat) {
		Field<Integer> invCount = DSL.count( INVOICE.ID );
		ctx.getDslContext()
			.select(invCount)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(stat.getId()))
			.and(INVOICE.NUMBER.lt(0))
			.and( params.getFromDate()
				.map(AonDateUtils::toSql)
				.map(INVOICE.ISSUE_DATE::ge)
				.orElse(DSL.noCondition()))
			.and( params.getToDate()
				.map(AonDateUtils::toSql)
				.map(INVOICE.ISSUE_DATE::le)
				.orElse(DSL.noCondition()))
			.fetch()
			.stream()
			.findFirst()
			.ifPresent( r -> stat.addProformas( r.get(invCount) ) );
	}

	private static void fillRawdocsData(AONContext ctx, DomainInvoiceStat stat) {
		Field<Integer> rawCount = DSL.count( RAWDOC.ID );
		ctx.getDslContext().select( rawCount, RAWDOC.STATUS )
		 	.from(RAWDOC)
			.where(RAWDOC.DOMAIN.eq(stat.getId()))
			.groupBy(RAWDOC.STATUS)
			.fetch()
			.forEach( r -> {
				Integer c = r.get(rawCount);
				RawdocStatus status = RawdocStatus.safeValueOf( r.get(RAWDOC.STATUS) );
				if (status == RawdocStatus.INBOX) stat.addDraft(c);
				else if (status == RawdocStatus.REJECTED) stat.addReview(c);
				else if (status == RawdocStatus.TRASH) stat.addTrash(c);
				else stat.addInProcess(c);
			});
	}

	private static void fillAlcatrazData(AONContext ctx, DomainInvoiceStatParams params, DomainInvoiceStat stat) {
		Field<Integer> invCount = DSL.count( INVOICE.ID );
		
		ctx.getDslContext()
			.select(invCount, INVOICE.TYPE)
			.from(INVOICE)
			.where(INVOICE.DOMAIN.eq(stat.getId()))
			.and( params.getFromDate()
				.map(AonDateUtils::toSql)
				.map(INVOICE.ISSUE_DATE::ge)
				.orElse(DSL.noCondition()))
			.and( params.getToDate()
				.map(AonDateUtils::toSql)
				.map(INVOICE.ISSUE_DATE::le)
				.orElse(DSL.noCondition()))
			.andNotExists(
				params.getFiscalModelType()
					.map(t -> 
						ctx.getDslContext().selectOne()
							.from(ALCATRAZ)
							.innerJoin(FS_MODEL).on(FS_MODEL.ID.eq(ALCATRAZ.FS_MODEL))
							.where(ALCATRAZ.INVOICE.eq(INVOICE.ID))
							.and(FS_MODEL.MODEL.eq(t.getValue()))
					)
					.orElse(
						ctx.getDslContext().selectOne()
							.from(ALCATRAZ)
							.where(ALCATRAZ.INVOICE.eq(INVOICE.ID))
							.and(ALCATRAZ.FS_MODEL.isNotNull())
					)
			)
		   .groupBy(INVOICE.TYPE, INVOICE.TRANSACTION, INVOICE.WITHHOLDING, INVOICE.STATUS)
		   .fetch()
		   .forEach( r -> {
			   Integer c = r.get(invCount);
			   InvoiceType type = InvoiceType.safeValueOf( r.get(INVOICE.TYPE) );
			   if (type.isSales()) stat.addUndeclaredIssued(c);
			   else if (type.isUndeductible()) stat.addUndeclaredSimplified(c);
			   else stat.addUndeclaredReceived(c);
		   })
		;
	}
	
	private static Condition getCondition(AONContext ctx, Domain domain, DomainInvoiceStatParams params) {
		ConditionRecord cr = new ConditionRecord(ctx,domain, params);
		return DOMAIN_CONDITION
			.andThen(c -> SCOPE_CONDITION.apply(c, cr))
			.andThen(c -> ACTIVE_CONDITION.apply(c, cr))
			.andThen(c -> QUERY_CONDITION.apply(c, cr))
		.apply(DSL.noCondition(), cr);
	}
	
	private static record ConditionRecord (AONContext ctx, Domain domain, DomainInvoiceStatParams params) {}
	
	private static final BiFunction<Condition, ConditionRecord, Condition> DOMAIN_CONDITION = (c, cr) -> 
		c.and( (cr.domain.isParent())
			? DOMAIN.PARENT.eq(cr.domain.getId())
			: DOMAIN.ID.eq(cr.params.getDomain()));
	
	private static final BiFunction<Condition, ConditionRecord, Condition> ACTIVE_CONDITION = (c, cr) -> 
		cr.params.getActive()
			.filter(a -> a != null)
			.filter(a -> AonNumberUtils.notEquals(a, 0))
			.map(a -> {
				if (AonNumberUtils.equals(a, 1)) {
					return c.and(DOMAIN.ACTIVE.eq((byte)1))
						.and( DOMAIN.EXPIRATIONDATE.isNull().or(DOMAIN.EXPIRATIONDATE.ge( AonDateUtils.toSql( AonDateUtils.today()))));
				}
				else if (AonNumberUtils.equals(a, 2)) {
					return c.and(DOMAIN.ACTIVE.eq((byte)0));
				}
				else if (AonNumberUtils.equals(a, 3)) {
					return c.and( DOMAIN.ACTIVE.eq((byte)1))
						.and(DOMAIN.EXPIRATIONDATE.lt( AonDateUtils.toSql( AonDateUtils.today())));
				} else {
					return c;
				}
			})
			.orElse(c);
		
	private static String getUserName(ConditionRecord cr) {
		return cr.params
			.getImpersonatedUser()
			.flatMap(userId -> 
				cr.ctx.getDslContext()
					.select(USER.LOGIN)
					.from(USER)
					.where(USER.ID.eq(userId))
					.fetch()
					.stream()
					.map(r -> r.get(USER.LOGIN))
					.findFirst()
			)
			.orElse(cr.ctx.getUser());
	}
	
	private static final BiFunction<Condition, ConditionRecord, Condition> SCOPE_CONDITION = (c, cr) -> {
		if (!cr.domain.isParent()) return c;
		int scopes = cr.params.getScope().orElse(0);
		String userName = getUserName(cr);
		if ( scopes == 0) {				// TODOS
			c = c.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(cr.ctx, userName, DOMAIN.SCOPE)));
		} else if ( scopes == 1) {		// MIS SCOPES
			c = c.and(SecurityDAO.getUserScopesCondition(cr.ctx, userName, DOMAIN.SCOPE));
		} else if ( scopes == 2) {		// SIN SCOPES
			c = c.and(DOMAIN.SCOPE.isNull());
		}
		return c;
	};
	
	private static final BiFunction<Condition, ConditionRecord, Condition> QUERY_CONDITION = (c, cr) -> 
		cr.params.getQuery()
	    	.filter(AonStringUtils::isNotBlank)
	        .map(AonStringUtils::SQLlike)
	        .map(q -> c.and(REGISTRY.DOCUMENT.like(q).or(REGISTRY.NAME.like(q))))
	        .orElse(c);

	private static class DomainInvoiceStatFiller extends Filler implements Function<Record,DomainInvoiceStat> {
		
		@Override
		public DomainInvoiceStat apply(Record r) {
			return build(r);
		}
		
		public static DomainInvoiceStat build( Record r ) {
			Date expirationDate = getValue(r, DOMAIN.EXPIRATIONDATE);
			boolean expired = expirationDate != null && expirationDate.before( AonDateUtils.today() );
			return new DomainInvoiceStat()
				.setId(getValue(r, DOMAIN.ID))
				.setParentId(getValue(r, DOMAIN.PARENT))
				.setName(getValue(r, DOMAIN.NAME))
				.setDescription(getValue(r, DOMAIN.DESCRIPTION))
				.setCompanyId(getValue(r, REGISTRY.ID))
				.setCompanyDocument(getValue(r, REGISTRY.DOCUMENT))
				.setCompanyName(getValue(r, REGISTRY.NAME))
				.setActive(getBoolean(r, DOMAIN.ACTIVE))
				.setExpired( expired )
			;
		}
	}
	
}
