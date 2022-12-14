package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.VATDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.ComplementaryBeahaviour;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class Mod390HFDAO extends FiscalModelDAO {
	
	public static Stream<Mod390HF> getMod390HFs(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFullFiscalModels(ctx,domain,FiscalModelType.M390,filter, Mod390HF::new);
	}
	public static Stream<Mod390HF> getMod390HFs(AONContext ctx,int domain) {
		return getMod390HFs(ctx, domain, null);
	}
	public static Mod390HF get(AONContext ctx,int id) {
		ctx.checkRead();
		return FiscalModelDAO.get(ctx,Mod390HF::new,id);
	}
	
	public static Stream<Mod390HF> getSamePeriodFiscalModels(AONContext ctx,Mod390HF fm) {
		return FiscalModelDAO.getSamePeriodFiscalModels(ctx, fm, Mod390HF::new);
	}

	public static Stream<Mod390HF> getSamePeriodModels(AONContext ctx,Mod390HF fm) {
		return FiscalModelDAO.getSamePeriodModels(ctx, fm, Mod390HF::new);
	}

	public static Mod390HF saveComments(AONContext ctx, Mod390HF mod) {
		FiscalModelDAO.saveComments(ctx, mod);
		return mod;
	}

	public static Mod390HF save(AONContext ctx, Mod390HF mod) {
		calculate(mod);
		return FiscalModelDAO.save(ctx, mod);
	}

	public static Mod390HF calculate( Mod390HF mod ) {
		Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
		return calculate(mod, dec);
	}
	
	public static Mod390HF calculate(Mod390HF mod, Mod390HFDeclaration dec) {
		Mod390HFMVELContext mvelCtx = getMvelContext( mod );
		for (IMod390KeyDAO key : dec.getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) ) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		mod.setDeclarationResult(dec.getResult(mod));
		return mod; 
	}
	
	private static Mod390HFMVELContext getMvelContext( Mod390HF mod ) {
		Mod390HFMVELContext mvelCtx = new Mod390HFMVELContext(mod);
		for (String key : mod.getMap().keySet()) {
			Mod390Key modKey = Mod390Key.getKey(key);
			if (modKey != null) {
				FiscalModelDetail detail = mod.getMap().get(key);
				mvelCtx.put(modKey.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx; 
	}
	
	public static Mod390HF initialize(AONContext ctx,Mod390HF mod) {
		if (mod == null) {
			mod = new Mod390HF();
			mod.setDomain(ctx.getDomainId());
			mod.setPeriod(Period.YEAR);
		}
		initializeFiscalModel(ctx, mod);
		if (mod.getAdministration() == null || mod.isAEAT()) {
			mod.setAdministration(Administration.BIZKAIA);
		}
		initializeProrrate(ctx, mod);
		Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
		dec.initialize( ctx, mod );
		dec.ensureDetails( mod );
		return mod;
	}

	private static void initializeProrrate(AONContext ctx, Mod390HF mod) {
		if (mod.getProrateKey() != null) {
			Pair<Double,String> prorrateInfo =  Mod303DAO.getMod303s( ctx , mod.getDomain())
				.filter(mod303 -> mod303.getAdministration() == mod.getAdministration() )
				.filter(mod303 -> mod303.getYear() == mod.getYear() )
				.map(mod303 ->  new Pair<Double,String>(mod303.getProratePercent(), mod303.getSpecialProrateValue() ))
				.findFirst()
				.orElse(new Pair<>(0.0, "G"));
			if (AonMathUtils.equals(prorrateInfo.getLeft() ,100.0)) {
				prorrateInfo.setLeft( 0.0);
			}
			mod.ensureDetail(mod.getProrateKey()).setAmount(prorrateInfo.getLeft());
			mod.ensureDetail(mod.getProrateTypeKey()).setDescription(prorrateInfo.getRight());
			mod.ensureDetail(mod.getPreviousProrateKey()).setAmount(prorrateInfo.getLeft());
			if (mod.hasProrate() || mod.hasPreviousProrate()) {
				Date fromDate = AonDateUtils.getYearFirstDay(mod.getYear());
				Date toDate = AonDateUtils.getYearLastDay(mod.getYear());
				VATDAO.getVatBreakdown(ctx,fromDate,toDate,mod)
				.filter( VatContext::isSales )
				.forEach( vat -> {
					if (!vat.isVatSurchargeRegime() && vat.getVatRegime() != VATRegime.EXEMPT) {
						mod.ensureDetail(Mod390Key.CM_070).addAmount( vat.getBase());
					}
					mod.ensureDetail(Mod390Key.CM_071).addAmount( vat.getBase());		
				});
				calculateProrrate(mod);
			}
		}
	}
	
	public static Mod390HF calculateProrrate(Mod390HF mod) {
		if (mod.hasProrate() || mod.hasPreviousProrate()) {
			double c70 = mod.ensureDetail(Mod390Key.CM_070).getAmount();
			double c71 = mod.ensureDetail(Mod390Key.CM_071).getAmount();
			if (AonMathUtils.isNotZero(c71)) {
				double prorrate = (c70 * 100 / c71);
				prorrate = AonMathUtils.ceil(prorrate,0);
				if (AonMathUtils.isGreatherThan(prorrate,100.0)) prorrate = 100.0;
				mod.ensureDetail(mod.getProrateKey()).setAmount( prorrate );
			}
		}
		return mod;
	}
	

	public static Mod390HF reset(AONContext ctx,Mod390HF mod390HF) {
		mod390HF.setMap(null);
		initializeIdentificationData(ctx, mod390HF);
		create(ctx,mod390HF);
		return mod390HF;
	}

	public static Mod390HF create(AONContext ctx,Mod390HF mod) {
		final Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
		Set<Integer> invoices = dec.createOnTheFly(ctx,mod);
		dec.prorrateRegularization(ctx,mod);
		dec.specificInitialization(ctx, mod);
		round( mod );
		mod = save(ctx, mod);
		AlcatrazDAO.deleteFiscalModel(ctx, mod);
		AlcatrazDAO.saveModelInvoices(ctx, mod, invoices);
		return mod;
	}

	private static void round(Mod390HF mod) {
		for (FiscalModelDetail detail : mod.getMap().values()) {
			detail.setAccumulatedAmount( AonMathUtils.round(detail.getAccumulatedAmount()));
			detail.setResultAmount( AonMathUtils.round(detail.getResultAmount()));
			detail.setAmount( AonMathUtils.round(detail.getAmount()));
		}
	}
	public static Mod390HF markAsPending(AONContext ctx,Mod390HF mod) {
		FiscalModelValidation.statusChange(mod, FiscalStatus.PENDING);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod);
		mod.setStatus(FiscalStatus.PENDING);
		mod.setDeclarationResult(null);
		mod.setDeclarationResultType(null);
		mod.setFinance(null);
		mod = save(ctx, mod);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod;
	}

 	public static Mod390HF initializeForFinish(AONContext ctx,Mod390HF mod) {
		calculate(mod);
		Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
		dec.initializeDeclarationType(mod);
		return FiscalModelDAO.initializeForFinish(ctx, mod);
	}

 	public static Mod390HF markAsFinished(AONContext ctx,Mod390HF mod) {
		FiscalModelValidation.statusChange(mod, FiscalStatus.FINISHED);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod);
		mod = FiscalModelDAO.finish(ctx, mod);
		mod = save(ctx, mod);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod;
	}

	public static Mod390HF markAsSent(AONContext ctx,Mod390HF mod) {
		FiscalModelValidation.statusChange(mod, FiscalStatus.SENT);
		mod.setStatus(FiscalStatus.SENT);
		mod= save(ctx, mod);
		return mod;
	}

	public static Mod390HF markAsCustomerCheck(AONContext ctx,Mod390HF mod) {
		FiscalModelValidation.statusChange(mod, FiscalStatus.CUSTOMER_CHECK);
		mod = FiscalModelDAO.finish(ctx, mod);
		mod.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod = save(ctx, mod);
		return mod;
	}

	public static Mod390HF markAsCustomerAccepted(AONContext ctx,Mod390HF mod) {
		FiscalModelValidation.statusChange(mod, FiscalStatus.CUSTOMER_ACCEPTED);
		mod.setStatus(FiscalStatus.CUSTOMER_ACCEPTED);
		mod= save(ctx, mod);
		return mod;
	}
	
	public static Mod390HF markAsCustomerRejected(AONContext ctx,Mod390HF mod, String reason) {
		FiscalModelValidation.statusChange(mod, FiscalStatus.CUSTOMER_REJECTED);
		mod.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod.setComments( comments );
		}
		mod = save(ctx, mod);
		return mod;
	}

	public static Stream<Mod303> getM303EffectiveYearModels(AONContext ctx,Mod390HF mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
				.and(FS_MODEL.PERIOD.lessThan(mod.getPeriod().value()));
		return Mod303DAO.getEffectiveModels(ctx, mod, cond);
	}

	public static Stream<Mod303> getM303YearModels(AONContext ctx,Mod390HF mod) {
		return Mod303DAO.getMod303s(ctx, mod.getDomain(), 
				p -> p.getAdministrationProperty().eq(mod.getAdministration().value())
					.and( p.getYearProperty().eq(mod.getYear())));
	}
	
	public static Stream<FiscalModel> getM303YearPaybackModels(AONContext ctx,Mod390HF mod) {
		return Stream.concat(getM303YearModels(ctx,mod),getSamePeriodFiscalModels(ctx, mod))
			.filter(  fm -> 
	   	   		fm.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK
	   	   	 || fm.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK_CCT)				; 
	}
	public static Stream<FiscalModel> getM303YearDepositModels(AONContext ctx,Mod390HF mod) {
		return Stream.concat(getM303YearModels(ctx,mod),getSamePeriodFiscalModels(ctx, mod))
			.filter(  fm -> 
			   fm.getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT
			|| fm.getDeclarationResultType() == FiscalModelDeclarationType.BANK
			|| fm.getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT_CCT);
	}
	
	public static Stream<Mod390HF> getSamePeriodEffectiveModels(AONContext ctx, FiscalModel mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.eq(mod.getPeriod().value()));
		if (mod.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod.getId())); 
		}
		return getEffectiveModels(ctx, mod, cond ); 
	}
	
	public static Stream<Mod390HF> getLastPeriodEffectiveModels(AONContext ctx, Mod303 mod) {
		Mod390HF mod390 = new Mod390HF();
		mod390.setId(mod.getId());
		mod390.setDomain(mod.getDomain());
		mod390.setAdministration(mod.getAdministration());
		mod390.setPeriod(Period.YEAR);
		mod390.setYear(mod.getYear()-1);
		return getLastPeriodEffectiveModels(ctx, mod390); 
	}
	
	public static Stream<Mod390HF> getLastPeriodEffectiveModels(AONContext ctx, Mod390HF mod390) {
		Condition cond = FS_MODEL.YEAR.eq(mod390.getYear() - 1)
				.and(FS_MODEL.PERIOD.eq(mod390.getPeriod().value()));
		if (mod390.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod390.getId())); 
		}
		return getEffectiveModels(ctx, mod390, cond ); 
	}
	private static boolean isEffectiveReplacement( Mod390HFDeclaration dec, Mod390HF mod) {
		return mod.isReplacement() || (mod.isComplementary() && dec.getComplementaryBehaviour(mod) == ComplementaryBeahaviour.REPLACEMENT); 
	}
	
	public static Stream<Mod390HF> getEffectiveModels(AONContext ctx, FiscalModel mod, Condition cond ) {
		LinkedHashMap<Period, LinkedList<Mod390HF>> map = new LinkedHashMap<>();
		getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(FS_MODEL.MODEL.eq( FiscalModelType.M390_HF.getValue() ))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and( cond )
			.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.ID.asc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<Mod390HF>().apply(rec, Mod390HF::new))
			.forEach( mod303 -> {
				Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod303);
				map.computeIfAbsent(mod303.getPeriod(), k -> new LinkedList<>());
				if ( isEffectiveReplacement(dec, mod303) ) {
					map.get(mod303.getPeriod()).clear();
					map.get(mod303.getPeriod()).add( mod303 );					
				} else {
					if (map.get(mod303.getPeriod())
						.stream()
						.noneMatch( m -> isEffectiveReplacement(dec, mod303)) ) {
						map.get(mod303.getPeriod()).add( mod303 );
					}
				}
			});
		return map.values()
			.stream()
			.flatMap(Collection<Mod390HF>::stream)
			.map(mod303 -> fillModelDetails(ctx,mod303))
		;
	}
}
