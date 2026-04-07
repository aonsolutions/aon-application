package com.esferalia.aon.occam.impl.jooq.dao.accounting.amortization;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Amortization.AMORTIZATION;
import static com.esferalia.aon.jooq.tables.AmortizationDetail.AMORTIZATION_DETAIL;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.InvestAssetDAO.InvestAssetFiller;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

public class AmortizationDAO {
	
	private static record AmortizationContext( AONContext ctx,Amortization a) {};
	private static final com.esferalia.aon.jooq.tables.Account FIXED_ASSET_ACCOUNT = ACCOUNT.as("FIXED_ASSET_ACCOUNT");
	private static final com.esferalia.aon.jooq.tables.Account ACCUMULATED_ACCOUNT = ACCOUNT.as("ACCUMULATED_ACCOUNT");
	private static final com.esferalia.aon.jooq.tables.Account ALLOCATION_ACCOUNT = ACCOUNT.as("ALLOCATION_ACCOUNT");
	
	private AmortizationDAO() {}
	
	// ------------------------------------------------------------------------
	// ----------------------------------------------------- [Amortization] ---
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------- [READ] ---
	private static SelectConditionStep<Record> select(AONContext ctx, Integer domain) {
		return ctx.getDslContext()
	        .select()
	        .from(AMORTIZATION)
	        .leftOuterJoin(INVEST_ASSET).on(AMORTIZATION.INVEST_ASSET.eq(INVEST_ASSET.ID))
	        .leftOuterJoin(FIXED_ASSET_ACCOUNT).on(AMORTIZATION.FIXED_ASSET_ACCOUNT.eq(FIXED_ASSET_ACCOUNT.ID))
	        .leftOuterJoin(ACCUMULATED_ACCOUNT).on(AMORTIZATION.ACCUMULATED_ACCOUNT.eq(ACCUMULATED_ACCOUNT.ID))
	        .leftOuterJoin(ALLOCATION_ACCOUNT).on(AMORTIZATION.ALLOCATION_ACCOUNT.eq(ALLOCATION_ACCOUNT.ID))
	        .leftOuterJoin(AMORTIZATION_DETAIL).on(AMORTIZATION_DETAIL.AMORTIZATION.eq(AMORTIZATION.ID))
			.where(AMORTIZATION.DOMAIN.eq(domain))
		;
	}
	
	public static Optional<Amortization> get(AONContext ctx, Integer domain, Integer id) {
		return select(ctx, domain)
			.and(AMORTIZATION.ID.eq(id))
	        .collect(toAmortizationMap())
	        .values()
	        .stream()
			.findFirst()
		;
	}
	
	public static Stream<Amortization> stream(AONContext ctx, Integer domain) {
	    return select(ctx, domain)
	        .fetch()
	        .stream()
	        .collect(toAmortizationMap())
	        .values()
	        .stream();
	}
	
	public static Stream<Amortization> stream(AONContext ctx, AmortizationParams params) {
	    return select(ctx, params.getDomain())
    		.and( getCondition(params) )
	        .fetch()
	        .stream()
	        .collect(toAmortizationMap())
	        .values()
	        .stream();
	}

	
	private static Condition getCondition(AmortizationParams params) {
		return DSL.noCondition();
	}

	private static Collector<Record, Map<Integer, Amortization>, Map<Integer, Amortization>> toAmortizationMap() {

	    return Collector.of(
	        LinkedHashMap::new,
	        (map, r) -> {
	            Amortization a = map.computeIfAbsent(
	                r.getValue(AMORTIZATION.ID),
	                id -> AmortizationFiller.build(r)
	            );
	            if (r.getValue(AMORTIZATION_DETAIL.ID) != null) {
	                a.addDetail(AmortizationDetailFiller.build(r));
	            }
	        },
	        (m1, m2) -> { m1.putAll(m2); return m1; }
	    );
	}
	
	// ------------------------------------------------------------ [WRITE] ---
	public static Amortization save(AONContext ctx, Amortization a) {
		return (a.getId() == null)
			?insert(ctx,a)
			:update(ctx,a)
		;
	}
	
	private static Amortization insert(AONContext ctx, Amortization a) {
		AmortizationAutoComplete.complete(ctx, a);
		AmortizationValidation.validate(ctx, a);
		Integer id = ctx.getDslContext()
			.insertInto(AMORTIZATION)
			.set(AMORTIZATION.DOMAIN, a.getDomain())
			.set(AMORTIZATION.DESCRIPTION, a.getDescription())
			.set(AMORTIZATION.INITIAL_DATE, AonDateUtils.toSql(a.getInitialDate()))
			.set(AMORTIZATION.DEADLINE, AonDateUtils.toSql(a.getDeadline()))
			.set(AMORTIZATION.AMOUNT, a.getAmount())
			.set(AMORTIZATION.FEE_PERIOD, a.getFeePeriod() != null ? (byte)a.getFeePeriod().ordinal() : null)
			.set(AMORTIZATION.SALE_AMOUNT, a.getSaleAmount())
			.set(AMORTIZATION.COMMENTS, a.getComments())
			.set(AMORTIZATION.INVEST_ASSET, a.getInvestAsset() != null ? a.getInvestAsset().getId() : null)
			.set(AMORTIZATION.FIXED_ASSET_ACCOUNT, a.getFixedAssetAccount() != null ? a.getFixedAssetAccount().getId() : null)
			.set(AMORTIZATION.ACCUMULATED_ACCOUNT, a.getAccumulatedAccount() != null ? a.getAccumulatedAccount().getId() : null)
			.set(AMORTIZATION.ALLOCATION_ACCOUNT, a.getAllocationAccount() != null ? a.getAllocationAccount().getId() : null)
			.set(AMORTIZATION.PERCENTAGE, a.getPercentage())
			.set(AMORTIZATION.SECURITY_LEVEL, a.getSecurityLevel() != null ? (byte)a.getSecurityLevel().ordinal() : null)
			.returning(AMORTIZATION.ID)
			.fetchOne()
			.getValue(AMORTIZATION.ID);
		a.setId(id);
		AmortizationManager am = new AmortizationManager();
		am.generateDetails(ctx, a);
		return get(ctx, a.getDomain(), id)
			.orElseThrow(() -> new AonCoreException("Error al grabar amortizaci\u00F3n"));
				
	}
	
	private static Amortization update(AONContext ctx, Amortization a) {
		ctx.getDslContext()
			.update(AMORTIZATION)
			.set(AMORTIZATION.DESCRIPTION, a.getDescription())
			.set(AMORTIZATION.INITIAL_DATE, AonDateUtils.toSql(a.getInitialDate()))
			.set(AMORTIZATION.DEADLINE, AonDateUtils.toSql(a.getDeadline()))
			.set(AMORTIZATION.AMOUNT, a.getAmount())
			.set(AMORTIZATION.FEE_PERIOD, a.getFeePeriod() != null ? (byte)a.getFeePeriod().ordinal() : null)
			.set(AMORTIZATION.SALE_AMOUNT, a.getSaleAmount())
			.set(AMORTIZATION.COMMENTS, a.getComments())
			.set(AMORTIZATION.INVEST_ASSET, a.getInvestAsset() != null ? a.getInvestAsset().getId() : null)
			.set(AMORTIZATION.FIXED_ASSET_ACCOUNT, a.getFixedAssetAccount() != null ? a.getFixedAssetAccount().getId() : null)
			.set(AMORTIZATION.ACCUMULATED_ACCOUNT, a.getAccumulatedAccount() != null ? a.getAccumulatedAccount().getId() : null)
			.set(AMORTIZATION.ALLOCATION_ACCOUNT, a.getAllocationAccount() != null ? a.getAllocationAccount().getId() : null)
			.set(AMORTIZATION.PERCENTAGE, a.getPercentage())
			.set(AMORTIZATION.SECURITY_LEVEL, a.getSecurityLevel() != null ? (byte)a.getSecurityLevel().ordinal() : null)
			.where(AMORTIZATION.ID.eq(a.getId()))
			.execute();
		return a;
	}
	
	// ----------------------------------------------------------- [FILLER] ---
	static class AmortizationFiller extends Filler implements Function<Record,Amortization> {
		
		@Override
		public Amortization apply(Record r) {
			return build(r);
		}
		
		public static Amortization build( Record r ) {
			return new Amortization()
					.setId(getValue(r, AMORTIZATION.ID))
					.setDomain(getValue(r, AMORTIZATION.DOMAIN))
					.setDescription(getValue(r, AMORTIZATION.DESCRIPTION))
					.setInitialDate(getValue(r, AMORTIZATION.INITIAL_DATE))
					.setDeadline(getValue(r, AMORTIZATION.DEADLINE))
					.setAmount(getValue(r, AMORTIZATION.AMOUNT))
					.setFeePeriod(AmortizationPeriod.safeValueOf( getValue(r, AMORTIZATION.FEE_PERIOD) ).orElse(null))
					.setSaleAmount(getValue(r, AMORTIZATION.SALE_AMOUNT))
					.setComments(getValue(r, AMORTIZATION.COMMENTS))
					.setInvestAsset( InvestAssetFiller.build( r) )
					.setFixedAssetAccount(FullAccountFiller.build( r, FIXED_ASSET_ACCOUNT))
					.setAccumulatedAccount(FullAccountFiller.build( r, ACCUMULATED_ACCOUNT))
					.setAllocationAccount(FullAccountFiller.build( r, ALLOCATION_ACCOUNT))
					.setPercentage(getValue(r, AMORTIZATION.PERCENTAGE))
					.setSecurityLevel( SecurityLevel.safeValueOf(getValue(r, AMORTIZATION.SECURITY_LEVEL)))
				;
		}
	}
	// ------------------------------------------------------------------------
	// ----------------------------------------------- [AmortizationDetail] ---
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------- [READ] ---
	
	// ------------------------------------------------------------ [WRITE] ---
	public static AmortizationDetail save(AONContext ctx, AmortizationDetail d) {
		return (d.getId() == null)
			?insert(ctx,d)
			:update(ctx,d)
		;
	}

	private static AmortizationDetail insert(AONContext ctx, AmortizationDetail d) {
		Integer id = ctx.getDslContext()
			.insertInto(AMORTIZATION_DETAIL)
			.set(AMORTIZATION_DETAIL.DOMAIN, d.getDomain())
			.set(AMORTIZATION_DETAIL.AMORTIZATION, d.getAmortization())
			.set(AMORTIZATION_DETAIL.FROM_DATE, AonDateUtils.toSql( d.getFromDate()))
			.set(AMORTIZATION_DETAIL.TO_DATE, AonDateUtils.toSql( d.getToDate()))
			.set(AMORTIZATION_DETAIL.COEFFICIENT, d.getCoefficient())
			.set(AMORTIZATION_DETAIL.ALLOCATION, d.getAllocation())
			.set(AMORTIZATION_DETAIL.STATUS, d.getStatus().value() )
			.set(AMORTIZATION_DETAIL.ACCOUNT_ENTRY, d.getAccountEntry())
			.set(AMORTIZATION_DETAIL.FISCAL_ALLOCATION, d.getFiscalAllocation())
			.returning(AMORTIZATION_DETAIL.ID)
			.fetchOne()
			.getValue(AMORTIZATION_DETAIL.ID);
		return d.setId(id);
	}

	private static AmortizationDetail update(AONContext ctx, AmortizationDetail d) {
		ctx.getDslContext()
			.update(AMORTIZATION_DETAIL)
			.set(AMORTIZATION_DETAIL.FROM_DATE, AonDateUtils.toSql( d.getFromDate()))
			.set(AMORTIZATION_DETAIL.TO_DATE, AonDateUtils.toSql( d.getToDate()))
			.set(AMORTIZATION_DETAIL.COEFFICIENT, d.getCoefficient())
			.set(AMORTIZATION_DETAIL.ALLOCATION, d.getAllocation())
			.set(AMORTIZATION_DETAIL.STATUS, d.getStatus().value() )
			.set(AMORTIZATION_DETAIL.ACCOUNT_ENTRY, d.getAccountEntry())
			.set(AMORTIZATION_DETAIL.FISCAL_ALLOCATION, d.getFiscalAllocation())
			.where(AMORTIZATION_DETAIL.ID.eq(d.getId()))
			.execute();
		return d;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.getDslContext()
			.deleteFrom(AMORTIZATION_DETAIL)
			.where(AMORTIZATION_DETAIL.ID.eq(id))
			.execute();
	}

	// ----------------------------------------------------------- [FILLER] ---
	static class AmortizationDetailFiller extends Filler implements Function<Record,AmortizationDetail> {
		
		@Override
		public AmortizationDetail apply(Record r) {
			return build(r);
		}
		
		public static AmortizationDetail build( Record r ) {
			return new AmortizationDetail()
				.setId(getValue(r, AMORTIZATION_DETAIL.ID))
				.setDomain(getValue(r, AMORTIZATION_DETAIL.ID))
				.setAmortization(getValue(r, AMORTIZATION_DETAIL.AMORTIZATION))
				.setFromDate(getValue(r, AMORTIZATION_DETAIL.FROM_DATE))
				.setToDate(getValue(r, AMORTIZATION_DETAIL.TO_DATE))
				.setCoefficient(getValue(r, AMORTIZATION_DETAIL.COEFFICIENT))
				.setAllocation(getValue(r, AMORTIZATION_DETAIL.ALLOCATION))
				.setStatus(AmortizationDetailStatus.safeValueOf(  getValue(r, AMORTIZATION_DETAIL.STATUS)).orElse(null))
				.setAccountEntry(getValue(r, AMORTIZATION_DETAIL.ACCOUNT_ENTRY))
				.setFiscalAllocation(getValue(r, AMORTIZATION_DETAIL.FISCAL_ALLOCATION))
			;
		}
	}

	// -------------------------------------------------------- [COMPLETE] ---
	private static class AmortizationAutoComplete {
		private static final Consumer<AmortizationContext> COMPLETE_FIXED_ASSET_ACCOUNT = ac -> {
			if (ac.a.getAmortizationType() == null) return;
			if (ac.a.getFixedAssetAccount() == null || ac.a.getFixedAssetAccount().getId() != null) {
				String code = AccountDAO.getNextAccountCode( ac.ctx, ac.a.getAmortizationType().getFixedAssetAccount() );
				Account account = new Account()
					.setDomain(ac.a.getDomain())
					.setCode(code)
					.setDescription(ac.a.getDescription());
				ac.a.setFixedAssetAccount( AccountDAO.save(ac.ctx, account ));
			}
		};
		
		private static final Consumer<AmortizationContext> COMPLETE_ACCUMULATED_ACCOUNT = ac -> {
			if (ac.a.getAmortizationType() == null) return;
			if (ac.a.getAccumulatedAccount() == null || ac.a.getAccumulatedAccount().getId() != null) {
				String code = AccountDAO.getNextAccountCode( ac.ctx, ac.a.getAmortizationType().getAccumulatedAccount() );
				Account account = new Account()
					.setDomain(ac.a.getDomain())
					.setCode(code)
					.setDescription(ac.a.getDescription());
				ac.a.setAccumulatedAccount( AccountDAO.save(ac.ctx, account ));
			}
		};
		
		private static final Consumer<AmortizationContext> COMPLETE_ALLOCATION_ACCOUNT = ac -> {
			if (ac.a.getAmortizationType() == null) return;
			if (ac.a.getAllocationAccount() == null || ac.a.getAllocationAccount().getId() != null) {
				String code = AccountDAO.getNextAccountCode( ac.ctx, ac.a.getAmortizationType().getAllocationAccount() );
				Account account = new Account()
					.setDomain(ac.a.getDomain())
					.setCode(code)
					.setDescription(ac.a.getDescription());
				ac.a.setAllocationAccount( AccountDAO.save(ac.ctx, account ));
			}
		};
		private static final Consumer<AmortizationContext> COMPLETE_SECURITY_LEVEL = ac -> {
			if (ac.a.getSecurityLevel() == null) ac.a.setSecurityLevel( SecurityLevel.OFFICIAL);
		};
		
		static void complete(AONContext ctx,Amortization a) throws AonCoreException {
			COMPLETE_FIXED_ASSET_ACCOUNT
				.andThen(COMPLETE_ACCUMULATED_ACCOUNT)
				.andThen(COMPLETE_ALLOCATION_ACCOUNT)
				.andThen(COMPLETE_SECURITY_LEVEL)
			.accept(new AmortizationContext(ctx,a));
		}
	}
	
	// ------------------------------------------------------ [VALIDATION] ---
	private static class AmortizationValidation {
		
		private static final Consumer<AmortizationContext> EMPTY_DOMAIN = ivc -> {
			if (ivc.a.getDomain() == 0) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		private static final Consumer<AmortizationContext> EMPTY_FIXED_ASSET_ACCOUNT = ivc -> {
			if (ivc.a.getFixedAssetAccount() == null || ivc.a.getFixedAssetAccount().getId() == null) 
				throw new AonCoreException(AonError.AMORTIZATION_EMPTY_FIXED_ASSET_ACCOUNT.getMessage());
		};
		private static final Consumer<AmortizationContext> EMPTY_ACCUMULATED_ACCOUNT = ivc -> {
			if (ivc.a.getAccumulatedAccount() == null || ivc.a.getAccumulatedAccount().getId() == null) 
				throw new AonCoreException(AonError.AMORTIZATION_EMPTY_ACCUMULATED_ACCOUNT.getMessage());
		};
		private static final Consumer<AmortizationContext> EMPTY_ALLOCATION_ACCOUNT = ivc -> {
			if (ivc.a.getAllocationAccount() == null || ivc.a.getAllocationAccount().getId() == null) 
				throw new AonCoreException(AonError.AMORTIZATION_EMPTY_ALLOCATION_ACCOUNT.getMessage());
		};
		
		static void validate(AONContext ctx,Amortization a) throws AonCoreException {
			EMPTY_DOMAIN
				.andThen(EMPTY_FIXED_ASSET_ACCOUNT)
				.andThen(EMPTY_ACCUMULATED_ACCOUNT)
				.andThen(EMPTY_ALLOCATION_ACCOUNT)
			.accept(new AmortizationContext(ctx,a));
		}
		
	}

	

}
