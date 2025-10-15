package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractDoc.CONTRACT_DOC;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Hotel.HOTEL;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.MkCampaign.MK_CAMPAIGN;
import static com.esferalia.aon.jooq.tables.MkTemplate.MK_TEMPLATE;
import static com.esferalia.aon.jooq.tables.News.NEWS;
import static com.esferalia.aon.jooq.tables.Newsletter.NEWSLETTER;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Proposal.PROPOSAL;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Rdoc.RDOC;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Series.SERIES;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Survey.SURVEY;
import static com.esferalia.aon.jooq.tables.Target.TARGET;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.Optional;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class ScopeDAO {
	
	public static final Table<?>[] SCOPE_TABLES = new Table[] {	
		CARRIER,	CATEGORY,	CONTRACT_ATTACH,		CONTRACT_DOC,	CREDITOR,
		CUSTOMER,	DELIVERY,	ENTERPRISE,				FINANCE,		HOTEL,
		INCOME,		INVOICE,	MK_CAMPAIGN,			MK_TEMPLATE,	NEWS,
		NEWSLETTER,	OFFER,		PAYROLL_BATCH_ATTACH,	PROPOSAL,		PURCHASE,
		RATTACH,	RDOC,		SALES,					SELLER,			SEPE_BATCH_ATTACH,
		SERIES,		SUPPLIER,	SURVEY,					TARGET,			USER_SCOPE,
		WORKPLACE
	};


	private ScopeDAO() {
		throw new IllegalStateException("Utility class");
	}
	
	// *********************************************************************
	// ******************************************** [READ] *****************
	// *********************************************************************
	
	public static Optional<Scope> get(AONContext ctx, Integer domainId, Integer scopeId) {
		return ctx.getDslContext().select()
			.from(SCOPE)
			.where(SCOPE.DOMAIN.eq(domainId))
			.and(SCOPE.ID.eq(scopeId))
			.fetch()
			.stream()
			.map(new ScopeFiller())
			.findFirst();
	}
	
	public static boolean exists(AONContext ctx, Integer scopeId) {
		return ctx.getDslContext().select()
			.from(SCOPE)
			.where(SCOPE.ID.eq(scopeId))
			.fetch()
			.stream()
			.findFirst()
			.isPresent();
	}

	private static class ScopeFiller extends Filler implements Function<Record, Scope> {
		
		@Override
		public Scope apply(Record r) {
			return build(r, SCOPE);
		}
		
		public static Scope build(Record r, final com.esferalia.aon.jooq.tables.Scope scopeTable) {
			if (isNull(r, scopeTable.ID)) return null;
			return new Scope()
				.setId(getValue(r, scopeTable.ID))
				.setDomain(getValue(r, scopeTable.DOMAIN))
				.setDescription(getValue(r, scopeTable.DESCRIPTION));
		}
	}
	
	// *********************************************************************
	// ******************************************** [WRITE] ****************
	// *********************************************************************
	public static void reassign( AONContext ctx, Integer domainId, Integer oldScopeId, Integer newScopeId ) {
		if (domainId == null || oldScopeId == null || newScopeId == null) {
			throw new AonCoreException( "El dominio y los \u00E1mbitos no pueden ser nulos." );
		}
		if ( !exists(ctx, oldScopeId)) {
			throw new AonCoreException( "El \u00E1mbito origen no existe." );
		}
		if ( get(ctx, domainId, newScopeId).isEmpty() ) {
			throw new AonCoreException( "El \u00E1mbito destino no existe o no pertenece al dominio en curso." );
		}
		
		AonCollectionUtils.stream( SCOPE_TABLES )
			.filter( t -> !t.equals(USER_SCOPE) )
			.forEach( t -> 
				ctx.getDslContext().update(t)
					.set(getScopeField(t), newScopeId)
					.where(getScopeField(t).eq(oldScopeId))
					.and( getDomainField(t).eq(domainId))
					.execute() );
		
		// Si en "user_scope" ya existe newScopeId, solo se borra oldScopeId, 
		// si no, se modifica oldScopeId por newScopeId.
		ctx.getDslContext().select()
			.from(USER_SCOPE)
			.where( USER_SCOPE.DOMAIN.eq(domainId) )
			.and( USER_SCOPE.SCOPE.eq(newScopeId) )
			.fetch()
			.stream()
			.findFirst()
			.ifPresentOrElse( 
				r -> ctx.getDslContext()
					.deleteFrom(USER_SCOPE)
					.where( USER_SCOPE.DOMAIN.eq(domainId) )
					.and( USER_SCOPE.SCOPE.eq(oldScopeId) )
					.execute()
				,() -> ctx.getDslContext()
					.update(USER_SCOPE)
					.set( getScopeField(USER_SCOPE), newScopeId )
					.where( USER_SCOPE.DOMAIN.eq(domainId) )
					.and( USER_SCOPE.SCOPE.eq(oldScopeId) )
					.execute()
		);
	}
	
	public static void reassignAndDelete( AONContext ctx, Integer domainId, Integer oldScopeId, Integer newScopeId ) {
		reassign(ctx, domainId, oldScopeId, newScopeId);
		delete(ctx, domainId, oldScopeId);
	}
	
	public static void delete(AONContext ctx, Integer domainId, Integer scopeId){
		if ( get( ctx, domainId, scopeId ).isEmpty()) {
			throw new AonCoreException( "El \u00E1mbito no existe o no pertenece al dominio indicado." );
		}
		if ( canBeDeleted(ctx, domainId, scopeId) ) {
			ctx.getDslContext()
				.delete(SCOPE)
				.where( SCOPE.DOMAIN.eq(domainId) )
				.and( SCOPE.ID.eq(scopeId) )
				.execute()
			;
		} else {
			throw new AonCoreException( "El \u00E1mbito no puede ser eliminado. Tiene referencias en otros registros." );
		}
	}
	
	public static boolean canBeDeleted( AONContext ctx, Integer domainId, Integer scopeId ) {
		return AonCollectionUtils.stream( SCOPE_TABLES )
			.flatMap( t -> 
				ctx.getDslContext().select(DSL.count())
					.from(t)
					.where(getScopeField(t).eq(scopeId))
					.fetch()
					.stream())
			.map( r -> r.get(DSL.count()) )
			.noneMatch( count -> count != null && count > 0 );
	}

	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getDomainField(Table<T> table) {
		return (TableField<T, Integer>) table.field("domain");
	}
	@SuppressWarnings("unchecked")
	private static <T extends Record> Field<Integer> getScopeField(Table<T> table) {
		return (TableField<T, Integer>) table.field("scope");
	}
}
