package com.esferalia.aon.occam.impl.jooq.dao.accounting.amortization;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.Amortization.AMORTIZATION;
import static com.esferalia.aon.jooq.tables.AmortizationDetail.AMORTIZATION_DETAIL;
import static com.esferalia.aon.jooq.tables.AmortizationInvoice.AMORTIZATION_INVOICE;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jooq.Condition;
import org.jooq.InsertSetMoreStep;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.AmortizationDetailRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.accounting.AmortizationInvoice;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus;
import com.esferalia.aon.occam.api.model.type.AmortizationPeriod;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.InvestAssetDAO.InvestAssetFiller;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.mutable.MutableObject;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AmortizationDAO {
	
	private static final String AMORTIZATION_DETAIL_ID_LABEL = "Identificador del detalle de amortizaci\u00F3n";
	private static final String AMORTIZATION_DETAIL_LABEL = "Detalle de amortizaci\u00F3n";

	private static record AmortizationContext( AONContext ctx,Amortization a) {}
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
			.where(AMORTIZATION.DOMAIN.eq(domain))
		;
	}
	
	private static SelectConditionStep<Record> selectFull(AONContext ctx, Integer domain) {
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
		return selectFull(ctx, domain)
			.and(AMORTIZATION.ID.eq(id))
			.orderBy(AMORTIZATION.DESCRIPTION, AMORTIZATION_DETAIL.FROM_DATE)
	        .collect(toAmortizationMap())
	        .values()
	        .stream()
	        .map( am -> calculateDetails(am) )
			.findFirst()
		;
	}
	
	public static Stream<Amortization> stream(AONContext ctx, AmortizationParams params) {
	    return select(ctx, params.getDomain())
    		.and( getCondition(params) )
    		.orderBy(AMORTIZATION.DESCRIPTION.desc())
    		.limit(params.getOffset() , params.getLimit())
	        .fetch()
	        .stream()
	        .map( r -> AmortizationFiller.build(r))
        ;
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
	
	private static Amortization calculateDetails( Amortization am) {
		MutableInt year = new MutableInt(-1);
		MutableObject<AmortizationDetail> detail = new MutableObject<>(null);
		MutableDouble accumulated = new MutableDouble(0.0);
		MutableDouble pending = new MutableDouble(am.getAmount());
		MutableDouble fiscalAccumulated = new MutableDouble(0.0);
		MutableDouble fiscalPending = new MutableDouble(am.getAmount());
		
		am.detailStream()
			.forEach( ad -> {
				int detailYear= AonDateUtils.getYear( ad.getFromDate() );
				if ( AonNumberUtils.notEquals( year.getValue() , detailYear )) {
					year.setValue( detailYear );
					AmortizationDetail det = new AmortizationDetail()
						.setDomain( ad.getDomain())
						.setAmortization( ad.getAmortization() )
						.setFromDate(ad.getFromDate());
					detail.setValue( det );
				}
				AmortizationDetail det = detail.getValue();
				
				accumulated.setValue( AonMathUtils.round(accumulated.getValue() + ad.getAllocation()));
				pending.setValue( AonMathUtils.round(pending.getValue() - ad.getAllocation()));
				fiscalAccumulated.setValue( AonMathUtils.round(fiscalAccumulated.getValue() + ad.getFiscalAllocation()));
				fiscalPending.setValue( AonMathUtils.round(fiscalPending.getValue() - ad.getFiscalAllocation()));
				
				det.setToDate(ad.getToDate());
				det.setCoefficient( AonMathUtils.round(det.getCoefficient() + ad.getCoefficient()));
				det.setAllocation( AonMathUtils.round(det.getAllocation() + ad.getAllocation()));
				det.setAccumulated( accumulated.getValue());
				det.setPending(pending.getValue());
				det.setFiscalAllocation( AonMathUtils.round(det.getFiscalAllocation() + ad.getFiscalAllocation()));
				det.setFiscalAccumulated( fiscalAccumulated.getValue());
				det.setFiscalPending(fiscalPending.getValue());
		});
		return am;
	}

	// ------------------------------------------------------------ [WRITE] ---
	public static Amortization save(AONContext ctx, Amortization a) {
		if (a.isDirty()) {
			AmortizationAutoComplete.complete(ctx, a);
			AmortizationValidation.validate(ctx, a);
			Amortization amo = (a.getId() == null)
				?insert(ctx,a)
				:update(ctx,a)
			;
			return get(ctx, amo.getDomain(), amo.getId())
				.orElseThrow(() -> new AonCoreException(AonError.INVALID_INSERT.getMessage()));
		}
		return a;
	}
	
	public static Amortization calculate(AONContext ctx, Amortization a) {
		AmortizationAutoComplete.complete(ctx, a);
		AmortizationValidation.validate(ctx, a);
		AmortizationManager am = new AmortizationManager();
		am.generateDetailsAndFill(ctx, a);
		Amortization amo = update(ctx,a);
		return get(ctx, amo.getDomain(), amo.getId())
			.orElseThrow(() -> new AonCoreException(AonError.INVALID_UPDATE.getMessage()));
	}

	private static Amortization insert(AONContext ctx, Amortization a) {
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
			.getValue(AMORTIZATION.ID)
		;
		a.setId(id);
		AmortizationManager am = new AmortizationManager();
		am.generateDetailsAndFill(ctx, a);
		saveDetails(ctx, a);
		return a;
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
	
	private static void saveDetails(AONContext ctx, Amortization a) {
		List<Query> stmts = new LinkedList<>();
		
		// Se eliminan los detalles que fueron marcados como eliminados
		a.detailStream()
			.filter( d -> d.getId() != null )
			.filter( d -> d.isDeleted() )
			.forEach(d -> stmts.add(getDeleteDetailStmt(ctx, d)));
		
		// Se insertan los detalles nuevos pendientes y no marcados como eliminados
		a.detailStream()
			.filter( d -> d.isNotDeleted() )
			.filter( d -> d.isPending() )
			.forEach(d -> stmts.add(getInsertDetailStmt(ctx, d)));
			
		if (AonCollectionUtils.isNotEmpty(stmts)) {
			ctx.getDslContext().batch(stmts).execute();
		}
	}
	
	public static void delete(AONContext ctx, Amortization a) {
		AmortizationValidation.validateDeletion(ctx, a);
		ctx.getDslContext()
			.deleteFrom(AMORTIZATION_DETAIL)
			.where(AMORTIZATION_DETAIL.AMORTIZATION.eq(a.getId()))
			.execute();
		ctx.getDslContext()
			.deleteFrom(AMORTIZATION)
			.where(AMORTIZATION.ID.eq(a.getId()))
			.execute();
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
	public static Amortization saveFiscalAllocation(AONContext ctx, AmortizationDetail detail) {
		Date yearFirst = detail.getFromDate();
		Date yearLast = detail.getToDate();
		Amortization am = get( ctx, detail.getDomain(), detail.getAmortization())
			.orElseThrow(() -> new AonCoreException("Amortizaci\u00F3n no encontrada"));
		
		
		// Se averigua cuantas cuotas están dentro del period anual para 
		// repartir la amortización fiscal equitativamente en todas ellas.
		long count = am.detailStream()
			.filter( d -> (DateUtils.isSameDay(yearFirst, d.getFromDate()) || yearFirst.before(d.getFromDate())) &&
					      (DateUtils.isSameDay(yearLast, d.getToDate()) || yearLast.after(d.getToDate())) )
			.count();
		
		// Se calcula la dotación fiscal para cada periodo, la última se calcula por diferencia.				
		Double fiscalAllocation = AonMathUtils.round(detail.getFiscalAllocation() / count);
		Double diff = AonMathUtils.round((fiscalAllocation * count) - detail.getFiscalAllocation());
		Double lastFiscalAllocation = (diff == 0.0)?fiscalAllocation:AonMathUtils.round(fiscalAllocation - diff);
						
		// Se modifica las cuotas con las nueva dotación fiscal.
		MutableInt i = new MutableInt(0);
		am.detailStream()
		.filter( d ->  (DateUtils.isSameDay(yearFirst, d.getFromDate()) || yearFirst.before(d.getFromDate()) ) 
					&& (DateUtils.isSameDay(yearLast, d.getToDate())    || yearLast.after(d.getToDate())) )
		.forEach( d -> {
			i.increment();
			d.setFiscalAllocation(i.getValue()<count?fiscalAllocation:lastFiscalAllocation);
			ctx.getDslContext()
				.update(AMORTIZATION_DETAIL)
				.set(AMORTIZATION_DETAIL.FISCAL_ALLOCATION, d.getFiscalAllocation())
				.where(AMORTIZATION_DETAIL.ID.eq(d.getId()))
				.execute();
		});
		return get(ctx, detail.getDomain(), detail.getAmortization())
			.orElseThrow(() -> new AonCoreException("Error al obtener amortizaci\u00F3n"));
	}
	
	private static Query getDeleteDetailStmt(AONContext ctx, AmortizationDetail detail) {
		return ctx.getDslContext().deleteFrom(AMORTIZATION_DETAIL)
			.where(AMORTIZATION_DETAIL.ID.eq(detail.getId()));
	}
	
	private static InsertSetMoreStep<AmortizationDetailRecord> getInsertDetailStmt(AONContext ctx, AmortizationDetail d) {
		return ctx.getDslContext()
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
		;
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
				.setDomain(getValue(r, AMORTIZATION_DETAIL.DOMAIN))
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
		
		private static final Consumer<AmortizationContext> COMPLETE_PERCENTAGE = ac -> {
			if (ac.a.getAmortizationType() == null) return;
			if (AonMathUtils.isZero(ac.a.getPercentage())) ac.a.setPercentage(ac.a.getAmortizationType().getPercentage());
		};
		
		private static final Consumer<AmortizationContext> COMPLETE_FIXED_ASSET_ACCOUNT = ac -> {
			if (ac.a.getAmortizationType() == null) return;
			if (ac.a.getFixedAssetAccount() == null || ac.a.getFixedAssetAccount().getId() != null) {
				String code = AccountDAO.getNextAccountCode( ac.ctx, ac.a.getAmortizationType().getFixedAssetAccount() );
				Account account = new Account()
					.setDomain(ac.a.getDomain())
					.setCode(code)
					.setDescription(ac.a.getDescription())
					.setActive(true)
				;
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
					.setDescription(ac.a.getDescription())
					.setActive(true)
				;
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
					.setDescription(ac.a.getDescription())
					.setActive(true)
				;
				ac.a.setAllocationAccount( AccountDAO.save(ac.ctx, account ));
			}
		};
		private static final Consumer<AmortizationContext> COMPLETE_SECURITY_LEVEL = ac -> {
			if (ac.a.getSecurityLevel() == null) ac.a.setSecurityLevel( SecurityLevel.OFFICIAL);
		};
		
		static void complete(AONContext ctx,Amortization a) throws AonCoreException {
			COMPLETE_PERCENTAGE
				.andThen(COMPLETE_FIXED_ASSET_ACCOUNT)
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
		private static final Consumer<AmortizationContext> EMPTY_DESCRIPTION = ivc -> {
			if (AonStringUtils.isBlank(ivc.a.getDescription())) 
				throw new AonCoreException(AonError.EMPTY_DESCRIPTION.getMessage());
		};
		private static final Consumer<AmortizationContext> EMPTY_INITIAL_DATE = ivc -> {
			if (ivc.a.getInitialDate() == null) 
				throw new AonCoreException(AonError.EMPTY_DATE.getMessage());
		};
		private static final Consumer<AmortizationContext> EMPTY_AMOUNT = ivc -> {
			if (ivc.a.getAmount() == null || AonMathUtils.isLessThanOrEqual(ivc.a.getAmount(), 0)) 
				throw new AonCoreException(AonError.EMPTY_AMOUNT.getMessage());
		};
		private static final Consumer<AmortizationContext> OVERFLOW_DESCRIPTION = ivc -> {
			if (AonStringUtils.length(ivc.a.getDescription()) > AMORTIZATION.DESCRIPTION.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( "DESCRIPTION", AMORTIZATION.DESCRIPTION.getDataType().length() ));
		};
		private static final Consumer<AmortizationContext> EMPTY_AMORTIZATION_TYPE = ivc -> {
			if (ivc.a.getId() == null && ivc.a.getAmortizationType() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format( "Tipo de amortizaci\u00F3n" ));
		};
		private static final Consumer<AmortizationContext> INVALID_PERCENTAGE = ivc -> {
			if (AonMathUtils.isLessThanOrEqual(ivc.a.getPercentage(), 0) || AonMathUtils.isGreaterThan(ivc.a.getPercentage(), 100))	 
				throw new AonCoreException(AonError.AMORTIZATION_EMPTY_PERCENTAGE.getMessage());
		};
		private static final Consumer<AmortizationContext> EMPTY_PERIOD = ivc -> {
			if (ivc.a.getFeePeriod() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format( "Periodicidad" ));
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
				.andThen(EMPTY_DESCRIPTION)
				.andThen(EMPTY_INITIAL_DATE)
				.andThen(EMPTY_AMOUNT)
				.andThen(OVERFLOW_DESCRIPTION)
				.andThen(EMPTY_AMORTIZATION_TYPE)
				.andThen(INVALID_PERCENTAGE)
				.andThen(EMPTY_PERIOD)
				.andThen(EMPTY_FIXED_ASSET_ACCOUNT)
				.andThen(EMPTY_ACCUMULATED_ACCOUNT)
				.andThen(EMPTY_ALLOCATION_ACCOUNT)
			.accept(new AmortizationContext(ctx,a));
		}

		private static final Consumer<AmortizationContext> EMPTY_ID = ivc -> {
			if (ivc.a.getDomain() == 0) 
				throw new AonCoreException(AonError.EMPTY_ID.getMessage());
		};
		private static final Consumer<AmortizationContext> SCORED_DETAILS = ivc -> 
			ivc.ctx.getDslContext()
			.select(AMORTIZATION_DETAIL.ID)
				.from(AMORTIZATION_DETAIL)
				.where(AMORTIZATION_DETAIL.AMORTIZATION.eq(ivc.a.getId()))
				.and(AMORTIZATION_DETAIL.STATUS.eq(AmortizationDetailStatus.SCORED.value()))
				.fetch()
				.stream()
				.findAny()
				.ifPresent( r -> {
					throw new AonCoreException(AonError.AMORTIZATION_DELETE_SCORED_DETAILS.getMessage());
				})
			;
			
		private static final Consumer<AmortizationContext> LINKED_INVOICES = ivc -> 
			ivc.ctx.getDslContext()
			.select(AMORTIZATION_INVOICE.ID)
				.from(AMORTIZATION_INVOICE)
				.where(AMORTIZATION_INVOICE.AMORTIZATION.eq(ivc.a.getId()))
				.fetch()
				.stream()
				.findAny()
				.ifPresent( r -> {
					throw new AonCoreException(AonError.AMORTIZATION_DELETE_LINKED_INVOICES.getMessage());
				})
			;
		
		public static void validateDeletion(AONContext ctx, Amortization a) {
			EMPTY_ID
				.andThen(SCORED_DETAILS)
				.andThen(LINKED_INVOICES)
			.accept(new AmortizationContext(ctx,a));
		}

		private static final Consumer<AmortizationContext> EMPTY_DEADLINE = ivc -> {
			if (ivc.a.getDeadline() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("Fecha de baja"));
		};
		
		private static final Consumer<AmortizationContext> WRONG_DEADLINE = ivc -> {
			if (DateUtils.isSameDay(ivc.a.getInitialDate(), ivc.a.getDeadline()) || ivc.a.getInitialDate().after(ivc.a.getDeadline())) 
				throw new AonCoreException(AonError.AMORTIZATION_WRONG_DEADLINE.getMessage());
		};
		
		private static final Consumer<AmortizationContext> WRONG_SALE_AMOUNT = ivc -> {
			if (AonMathUtils.isLessThanZero( ivc.a.getSaleAmount()) ) 
				throw new AonCoreException(AonError.AMORTIZATION_WRONG_SALE_AMOUNT.getMessage());
		};

		private static final Consumer<AmortizationContext> NOT_PENDING_ALLOCATIONS = ivc -> {
			Date cancelDate = DateUtils.addDays(ivc.a.getDeadline(), -1);
			ivc.ctx.getDslContext().select(AMORTIZATION_DETAIL.ID)
				.from(AMORTIZATION_DETAIL)
				.where(AMORTIZATION_DETAIL.AMORTIZATION.eq(ivc.a.getId()))
				.and(AMORTIZATION_DETAIL.TO_DATE.gt( AonDateUtils.toSql(cancelDate)))
				.and(AMORTIZATION_DETAIL.STATUS.notEqual(AmortizationDetailStatus.PENDING.value()))
				.fetch()
				.stream()
				.findAny()
				.ifPresent( r -> {
					throw new AonCoreException(AonError.AMORTIZATION_NOT_PENDING_ALLOCATIONS.getMessage());
				})
			;
		};
		
		public static void validateSale(AONContext ctx, Amortization a) {
			EMPTY_ID
			.andThen(EMPTY_DEADLINE)
			.andThen(WRONG_DEADLINE)
			.andThen(WRONG_SALE_AMOUNT)
			.andThen(NOT_PENDING_ALLOCATIONS)
			.accept(new AmortizationContext(ctx,a));
		}
}

	// ----------------------------------------------------------- [BLOCK] ---
	public static AmortizationDetail blockDetail(AONContext ctx, AmortizationDetail detail) {
		if (detail == null) {
			throw new AonCoreException(AonError.EMPTY_DATA.format(AMORTIZATION_DETAIL_LABEL));
		}
		if (detail.getId() == null) {
			throw new AonCoreException(AonError.EMPTY_DATA.format(AMORTIZATION_DETAIL_ID_LABEL));
		}
		detail.setStatus(AmortizationDetailStatus.BLOCKED);
		ctx.getDslContext()
			.update(AMORTIZATION_DETAIL)
			.set(AMORTIZATION_DETAIL.STATUS, detail.getStatus().value())
			.where(AMORTIZATION_DETAIL.ID.eq(detail.getId()))
			.execute();
		return detail;
	}
	public static AmortizationDetail unblockDetail(AONContext ctx, AmortizationDetail detail) {
		if (detail == null) {
			throw new AonCoreException(AonError.EMPTY_DATA.format(AMORTIZATION_DETAIL_LABEL));
		}
		if (detail.getId() == null) {
			throw new AonCoreException(AonError.EMPTY_DATA.format(AMORTIZATION_DETAIL_ID_LABEL));
		}
		detail.setStatus(AmortizationDetailStatus.PENDING);
		ctx.getDslContext()
			.update(AMORTIZATION_DETAIL)
			.set(AMORTIZATION_DETAIL.STATUS, detail.getStatus().value())
			.where(AMORTIZATION_DETAIL.ID.eq(detail.getId()))
			.execute();
		return detail;
	}
	// ------------------------------------------------------ [ACCOUNTING] ---
	public static AmortizationDetail recordAllocation(AONContext ctx, Amortization am, AmortizationDetail detail) {
		if (detail == null) {
			throw new AonCoreException(AonError.EMPTY_DATA.format(AMORTIZATION_DETAIL_LABEL));
		}
		if (detail.getId() == null) {
			throw new AonCoreException(AonError.EMPTY_DATA.format(AMORTIZATION_DETAIL_ID_LABEL));
		}
		Date accountEntryDate = detail.getToDate();
		AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, accountEntryDate);
		if (period == null) {
			throw new AonCoreException(AonError.ACCOUNT_ENTRY_EMPTY_PERIOD.getMessage());
		}
		Integer activity = ( am.getInvestAsset() != null && am.getInvestAsset().getActivity() != null)
			? am.getInvestAsset().getActivity().getId()
			: null;
		
		AccountEntry entry = new AccountEntry()
			.setDomain(am.getDomain())
			.setPeriod(period.getId())
			.setEntryDate(accountEntryDate)
			.setSecurityLevel( am.getSecurityLevel() )
			.setEntryType(AccountEntryType.AMORTIZATION)
			.setActivity(activity)
		; 
		
		Account accumulated = am.getAccumulatedAccount();
		Account allocation  = am.getAllocationAccount();
		String concept = StringUtils.abbreviate("Amort. - " + am.getFixedAssetAccount().getDescription(), 32);
		
		entry.addDetail( new AccountEntryDetail()
			.setLine(0)
			.setAccount(allocation)
			.setConcept(concept)
			.setDebit(detail.getAllocation())
			.setBalancingAccount(accumulated)
		);

		entry.addDetail(new AccountEntryDetail()
			.setLine(1)
			.setAccount(accumulated)
			.setConcept(concept)
			.setCredit(detail.getAllocation())
			.setBalancingAccount(allocation)
		);
		
		Integer entryId = AccountEntryDAO.save(ctx, entry);
		detail.setAccountEntry(entryId)
			.setStatus(AmortizationDetailStatus.SCORED);
		ctx.getDslContext()
			.update(AMORTIZATION_DETAIL)
			.set(AMORTIZATION_DETAIL.ACCOUNT_ENTRY, detail.getAccountEntry() )
			.set(AMORTIZATION_DETAIL.STATUS, detail.getStatus().value())
			.where(AMORTIZATION_DETAIL.ID.eq(detail.getId()))
			.execute();
		return detail;

	}

	public static AmortizationDetail unrecordAllocation(AONContext ctx, AmortizationDetail detail) {
		if (detail == null) {
			throw new AonCoreException(AonError.EMPTY_DATA.format(AMORTIZATION_DETAIL_LABEL));
		}
		if (detail.getId() == null) {
			throw new AonCoreException(AonError.EMPTY_DATA.format(AMORTIZATION_DETAIL_ID_LABEL));
		}
		if (detail.getAccountEntry() != null) {
			AccountEntry ae = AccountEntryDAO.getAccountEntry(ctx, detail.getAccountEntry());
			if (detail.getAccountEntry() == null) {
				throw new AonCoreException(AonError.ACCOUNT_ENTRY_NOT_FOUND.getMessage());
			}
			AccountEntryDAO.delete( ctx, ae.getId() );
		}
		return ctx.getDslContext().select()
			.from(AMORTIZATION_DETAIL)
			.where(AMORTIZATION_DETAIL.ID.eq(detail.getId()))
			.fetch()
			.stream()
			.findFirst()
			.map( r -> AmortizationDetailFiller.build(r))
			.orElseThrow( () -> new AonCoreException(AonError.AMORTIZATION_DETAIL_NOT_FOUND.getMessage()) );
	}

	public static void unrecord(AONContext ctx, Integer entryId) {
		ctx.getDslContext()
			.update(AMORTIZATION_DETAIL)
			.setNull(AMORTIZATION_DETAIL.ACCOUNT_ENTRY)
			.set(AMORTIZATION_DETAIL.STATUS, AmortizationDetailStatus.PENDING.value())
			.where(AMORTIZATION_DETAIL.ACCOUNT_ENTRY.eq(entryId))
			.execute();
	}

	// ------------------------------------------------------ [SALE] ---
	public static Amortization sale(AONContext ctx, Amortization a) {
		AmortizationValidation.validateSale(ctx, a);
		Date cancelDate = DateUtils.addDays(a.getDeadline(), -1);
		Amortization am = get(ctx, a.getDomain(), a.getId())
			.orElseThrow(() -> new AonCoreException(AonError.AMORTIZATION_NOT_FOUND.getMessage()));
		
		am.setDeadline(a.getDeadline());
		am.setSaleAmount(a.getSaleAmount());
		am.detailStream()
			.filter( d -> d.getStatus() == AmortizationDetailStatus.PENDING )
			.filter( d -> d.getToDate().after(cancelDate) )
			.forEach( d -> {
				System.out.println( "Procesando detalle " + d.getId() + " desde " + d.getFromDate() + " hasta " + d.getToDate() );
				Date from = d.getFromDate();
				Date to =  d.getToDate();
				if (from.equals(cancelDate) || from.before(cancelDate)) {
						int days = (int) AonDateUtils.getDaysBetweenDates(from, to);
						int newDays = (int) AonDateUtils.getDaysBetweenDates(from, cancelDate );
						double newAllocation = AonMathUtils.round( d.getAllocation() * newDays / days );
						d.setAllocation(newAllocation);
						d.setToDate(cancelDate);
				} else {		
					d.setDeleted( true );
				}
			})
		;
		update(ctx, am);
		saveDetails(ctx, am);
		return get(ctx, am.getDomain(), am.getId())
			.orElseThrow(() -> new AonCoreException("Error al grabar amortizaci\u00F3n"));
	}

	public static LinkedList<AmortizationInvoice> getInvoices(AONContext ctx, Integer domain, Integer amortizationId) {
		if (domain == null) throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		if (amortizationId == null) throw new AonCoreException(AonError.EMPTY_ID.getMessage());
		System.out.println( "amortizationId  = " + amortizationId );
		Amortization am = get(ctx, domain, amortizationId)
			.orElseThrow(() -> new AonCoreException(AonError.AMORTIZATION_NOT_FOUND.getMessage()));
		return ctx.getDslContext()
	        .select()
	        .from(AMORTIZATION_INVOICE)
			.where(AMORTIZATION_INVOICE.AMORTIZATION.eq(amortizationId))
			.fetch()
			.stream()
			.map( r -> new AmortizationInvoice()
				.setId( r.getValue(AMORTIZATION_INVOICE.ID) )
				.setDomain( domain )
				.setAmortization( am )
				.setInvoice( InvoiceDAO.getFullInvoice(ctx, r.getValue(AMORTIZATION_INVOICE.INVOICE)) ))
			.map( ami -> fillAccountEntryId(ctx, ami) )
			.collect(Collectors.toCollection(LinkedList::new))
		;
		
	}

	private static AmortizationInvoice fillAccountEntryId(AONContext ctx, AmortizationInvoice ami) {
		if (ami != null && ami.getInvoice() != null) {
			ctx.getDslContext()
				.select(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY)
				.from(ACCOUNT_ENTRY_INVOICE)
				.where(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(ami.getInvoice().getId()))
				.fetch()
				.stream()
				.map( r -> r.getValue(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY) )
				.findFirst()
				.ifPresent( id -> ami.setAccountEntryId(id))
			;
		}
		return ami;
	}
}
