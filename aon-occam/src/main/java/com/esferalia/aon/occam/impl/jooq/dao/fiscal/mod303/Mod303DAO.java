package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.accounting.AccSctiptMVELContext;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailExpressionScript;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.fiscal.mod303.entry.Mod303DefaultAccountEntryScript;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303MVELContext;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;


public class Mod303DAO extends FiscalModelDAO {
	public static Stream<Mod303> getMod303s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFullFiscalModels(ctx,domain,FiscalModelType.M303,filter, Mod303::new);
	}
	public static Stream<Mod303> getMod303s(AONContext ctx,int domain) {
		return getMod303s(ctx, domain, null);
	}

	public static Mod303 get(AONContext ctx,int id) {
		ctx.checkRead();
		Mod303 mod303 = FiscalModelDAO.get(ctx,Mod303::new,id);
		if ( mod303 != null) {
			mod303.setInvoicesBound( AlcatrazDAO.hasAlcatrazBound(ctx, mod303.getId()));
			Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
			dec.fillSimplifiedRegime(mod303);
		}
		return mod303;
	}

	public static Mod303 saveComments(AONContext ctx, Mod303 mod303) {
		FiscalModelDAO.saveComments(ctx, mod303);
		return mod303;
	}
	
	public static Mod303 save(AONContext ctx, Mod303 mod303) {
		calculate(mod303);
		return FiscalModelDAO.save(ctx, mod303);
	}
	
	private static Mod303MVELContext getMvelContext( Mod303Declaration dec, Mod303 mod303 ) {
		dec.populateSimplifiedRegime( mod303 );
		Mod303MVELContext mvelCtx = new Mod303MVELContext(mod303);
		for (String key : mod303.getMap().keySet()) {
			Mod303Key mod303Key = Mod303Key.getKey(key);
			if (mod303Key != null) {
				FiscalModelDetail detail = mod303.getMap().get(key);
				mvelCtx.put(mod303Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx; 
	}
	public static Mod303 calculate(Mod303 mod303, Mod303Declaration dec) {
		Mod303MVELContext mvelCtx = getMvelContext( dec, mod303 );
		for (IMod303KeyDAO key : dec.getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) ) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod303.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		dec.fillSimplifiedRegime(mod303);
		mod303.setDeclarationResult(dec.getResult(mod303));
		return mod303; 
	}
	
	public static Mod303 calculate(Mod303 mod303) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		return calculate(mod303, dec);
	}
	
	public static Mod303 initialize(AONContext ctx,Mod303 mod303) {
		if (mod303 == null) {
			mod303 = new Mod303();
			mod303.setDomain(ctx.getDomainId());
		}
		mod303.getMessages().clear();
		initializeFiscalModel(ctx, mod303);
		initializeProrrate(ctx, mod303);
		try {
			Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
			dec.initialize( ctx, mod303 );
			dec.ensureDetails(mod303);
		} catch (AonCoreException e) {
			mod303.addMessage(e.getMessage());
		} 
		return mod303;
	}
	
	private static void initializeProrrate(AONContext ctx, Mod303 mod303) {
		if (mod303.getProrateKey() != null) {
			Pair<Double,String> prorrateInfo = getLastPeriodEffectiveModels(ctx, mod303)
//			Pair<Double,String> prorrateInfo = getMod303s( ctx , mod303.getDomain())
				.filter(mod -> mod.getAdministration() == mod303.getAdministration() )
				.map(mod ->  new Pair<Double,String>(mod.getProratePercent(), mod.getSpecialProrateValue() ))
				.findFirst()
				.orElse(new Pair<>(0.0, "G"));
			if (AonMathUtils.equals(prorrateInfo.getLeft() ,100.0)) {
				prorrateInfo.setLeft( 0.0);
			}
			mod303.ensureDetail(mod303.getProrateKey()).setAmount(prorrateInfo.getLeft());
			mod303.ensureDetail(mod303.getProrateTypeKey()).setDescription(prorrateInfo.getRight());
			if (mod303.getPeriod().isLastPeriod() && (mod303.hasProrate() || mod303.hasPreviousProrate())) {
				mod303.ensureDetail(mod303.getPreviousProrateKey()).setAmount(prorrateInfo.getLeft());
				AccountingReportParams params = new AccountingReportParams()
						.setFromDate(AonDateUtils.getYearFirstDay(mod303.getYear()))
						.setToDate(AonDateUtils.getYearLastDay(mod303.getYear()));
				VATDAO.getVatBreakdown(ctx,params )
					.filter( VatContext::isSales )
					.forEach( vat -> {
						if (!vat.isVatSurchargeRegime() && vat.getVatRegime() != VATRegime.EXEMPT) {
							mod303.ensureDetail(Mod303Key.CM_070).addAmount( vat.getBase());
						}
						mod303.ensureDetail(Mod303Key.CM_071).addAmount( vat.getBase());		
					});
				calculateProrrate(mod303);
			}
		}
	}
	
	public static Mod303 reset(AONContext ctx,Mod303 mod303) {
		mod303.setMap(null);
		initializeIdentificationData(ctx, mod303);
		create(ctx,mod303);
		return mod303;
	}
	
	public static Mod303 simulate(AONContext ctx,Mod303 mod303) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		dec.createOnTheFly(ctx,mod303);
		dec.prorrateRegularization(ctx,mod303);
		calculate(mod303);
		dec.specificInitialization(mod303);
		return mod303;
	}

	public static Mod303 create(AONContext ctx,Mod303 mod303) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		Set<Alcatraz> invoices = dec.createOnTheFly(ctx,mod303);
		dec.prorrateRegularization(ctx,mod303);
		calculate(mod303);
		dec.specificInitialization(mod303);
		mod303 = save(ctx, mod303);
		AlcatrazDAO.deleteFiscalModel(ctx, mod303);
		AlcatrazDAO.saveModelInvoices(ctx, mod303, invoices);
		mod303.setInvoicesBound( AlcatrazDAO.hasAlcatrazBound(ctx, mod303.getId()));
		return mod303;
	}

	public static Mod303 calculateProrrate(Mod303 mod303) {
		if (mod303.hasProrate() || mod303.hasPreviousProrate()) {
			double c70 = AonMathUtils.round( mod303.ensureDetail(Mod303Key.CM_070).getAmount());
			double c71 = AonMathUtils.round( mod303.ensureDetail(Mod303Key.CM_071).getAmount());
			if (AonMathUtils.isNotZero(c71)) {
				double prorrate = (c70 * 100 / c71);
				prorrate = AonMathUtils.ceil(prorrate,0);
				if (AonMathUtils.isGreatherThan(prorrate,100.0)) prorrate = 100.0;
				mod303.ensureDetail(mod303.getProrateKey()).setAmount( prorrate );
			}
			mod303.ensureDetail(Mod303Key.CM_070).setAmount( c70 );
			mod303.ensureDetail(Mod303Key.CM_071).setAmount( c71 );
		}
		return mod303;
	}

	public static Mod303 markAsPending(AONContext ctx,Mod303 mod303) {
		FiscalModelValidation.statusChange(mod303, FiscalStatus.PENDING);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod303);
		mod303.setStatus(FiscalStatus.PENDING);
		mod303.setDeclarationResult(null);
		mod303.setDeclarationResultType(null);
		mod303.setFinance(null);
		mod303 = save(ctx, mod303);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod303.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod303;
	}
	
 	public static Mod303 initializeForFinish(AONContext ctx,Mod303 mod303) {
		calculate(mod303);
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		dec.initializeDeclarationType(mod303);
		return FiscalModelDAO.initializeForFinish(ctx, mod303);
	}
	
	public static Mod303 markAsFinished(AONContext ctx,Mod303 mod303) {
		FiscalModelValidation.statusChange(mod303, FiscalStatus.FINISHED);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod303);
		mod303 = FiscalModelDAO.finish(ctx, mod303);
		mod303 = save(ctx, mod303);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod303.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod303;
	}

	public static Mod303 aeatPresentation(AONContext ctx, Mod303 mod303, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod303, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			Mod303 changed = get(ctx, mod303.getId());
			if (changed != null) {
				changed.setNumber(response.getJustificante());
				return markAsSent(ctx, changed);
			}
		}
		return mod303;
	}
	

	public static Mod303 markAsSent(AONContext ctx,Mod303 mod303) {
		FiscalModelValidation.statusChange(mod303, FiscalStatus.SENT);
		mod303.setStatus(FiscalStatus.SENT);
		mod303 = save(ctx, mod303);
		return mod303;
	}
	
	public static Mod303 markAsCustomerCheck(AONContext ctx,Mod303 mod303) {
		FiscalModelValidation.statusChange(mod303, FiscalStatus.CUSTOMER_CHECK);
		mod303 = FiscalModelDAO.finish(ctx, mod303);
		mod303.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod303 = save(ctx, mod303);
		return mod303;
	}

	public static Mod303 markAsCustomerAccepted(AONContext ctx,Mod303 mod303) {
		FiscalModelValidation.statusChange(mod303, FiscalStatus.CUSTOMER_ACCEPTED);
		mod303.setStatus(FiscalStatus.CUSTOMER_ACCEPTED);
		mod303 = save(ctx, mod303);
		return mod303;
	}

	public static Mod303 markAsCustomerRejected(AONContext ctx,Mod303 mod303, String reason) {
		FiscalModelValidation.statusChange(mod303, FiscalStatus.CUSTOMER_REJECTED);
		mod303.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod303.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod303.setComments( comments );
		}
		mod303 = save(ctx, mod303);
		return mod303;
	}
	
	public static Stream<Mod303> getSamePeriodEffectiveModels(AONContext ctx, FiscalModel mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.eq(mod.getPeriod().value()));
		if (mod.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod.getId())); 
		}
		return getEffectiveModels(ctx, mod, cond ); 
	}
	
	public static Stream<Mod303> getLastPeriodEffectiveModels(AONContext ctx, Mod303 mod) {
		Condition cond = null;
		if (mod.isFirstPeriod()) {
			cond = FS_MODEL.YEAR.eq(mod.getYear() - 1)
				.and(FS_MODEL.PERIOD.eq( mod.getPeriod().isQuarterPeriod()?Period.T4.value():Period.M12.value()));	
		} else {
			cond = FS_MODEL.YEAR.eq(mod.getYear()) 
				.and(FS_MODEL.PERIOD.eq( (byte) (mod.getPeriod().value() - 1) ));
		}
		if (mod.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod.getId())); 
		}
		return getEffectiveModels(ctx, mod, cond ); 
	}
	public static Stream<Mod303> getPreviousEffectiveModels(AONContext ctx, Mod303 mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.lessThan(mod.getPeriod().value()));
		return getEffectiveModels(ctx, mod, cond);
	}
	public static Stream<Mod303> getEffectiveModels(AONContext ctx, IFiscalModel mod, Condition cond ) {
		LinkedHashMap<Period, LinkedList<Mod303>> map = new LinkedHashMap<>();
		boolean fromMod390 = (mod.getModel() == FiscalModelType.M390  || mod.getModel() == FiscalModelType.M390_HF);
		getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(FS_MODEL.MODEL.eq( FiscalModelType.M303.getValue() ))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and( cond )
			.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.ID.asc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<Mod303>().apply(rec, Mod303::new))
			.filter( mod303 -> fromMod390 || mod303.getPeriod().isMonthPeriod() == mod.getPeriod().isMonthPeriod())
			.filter( mod303 -> fromMod390 || mod303.getPeriod().isQuarterPeriod() == mod.getPeriod().isQuarterPeriod())
			.forEach( mod303 -> {
				Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
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
			.flatMap(Collection<Mod303>::stream)
			.map(mod303 -> fillModelDetails(ctx,mod303))
		;
	}
	private static boolean isEffectiveReplacement( Mod303Declaration dec, Mod303 mod) {
		return mod.isReplacement() || (mod.isComplementary() && dec.getComplementaryBehaviour(mod) == ComplementaryBeahaviour.REPLACEMENT); 
	}
	
	
	public static Mod303 unrecord(AONContext ctx, Mod303 mod) {
		if (mod.isRecorded()) {
			AccountEntryDAO.delete(ctx, mod.getAccountEntry());
		}
		return get(ctx, mod.getId());
	}

	public static Mod303 doRecord(AONContext ctx, Mod303 mod) {
		if (mod.isRecorded()) {
			throw new IllegalArgumentException(MessageFormat.format(
				"El modelo {0} de {1} ya se encuentra contabilizado"
				,mod.getModelFullName(),mod.getAdministration().getDescription()));
		}
		Optional<AccountEntryDetailExpressionScript<Mod303>> script = Mod303DefaultAccountEntryScript.getScript(mod);
		if (!script.isPresent()) {
			throw new IllegalArgumentException(MessageFormat.format(
				"No se ha encontrado un script válido para el modelo {0} de {1}"
				,mod.getModelFullName(),mod.getAdministration().getDescription()));
		}
		AccSctiptMVELContext<Mod303> mvel = getAccSctiptMVELContext( ctx, mod );
		Optional<AccountEntry> optAe = Optional.ofNullable(mvel.fillDetails(ctx, mod, script.get()));
		if (optAe.isPresent() && !optAe.get().getDetails().isEmpty() ) {
			Integer entryId = AccountEntryDAO.save(ctx, optAe.get());
			FiscalModelDAO.doRecord(ctx, mod.getId(), entryId);
			mod = get(ctx, mod.getId());
		}
		return mod;
	}
	
	private static AccSctiptMVELContext<Mod303> getAccSctiptMVELContext(AONContext ctx, Mod303 mod) {
		return new AccSctiptMVELContext<Mod303>() {
			private static final long serialVersionUID = -858390524319034071L;
			@Override
			public void fillContext() {
				put(MODEL_KEY,mod);
				put(MODEL_DECLARATION_KEY,Mod303Declaration.getInstance(mod));
				for (String key : mod.getMap().keySet()) {
					Mod303Key mod303Key = Mod303Key.getKey(key);
					if (mod303Key != null) {
						FiscalModelDetail detail = mod.getMap().get(key);
						String mapKey = mod303Key.toString(); 
						if (mod303Key.getBox() != 0) {
							mapKey = "C" + mod303Key.getBox();
						}
						put(mapKey, detail==null?0.0:detail.getAmount());
					}
				}
			}
			@Override
			public AccountEntry fillAccountEntry(Mod303 mod) {
				Date entryDate = new Date();
				AccountPeriod period = AccountPeriodDAO.getActivePeriod(ctx,entryDate);
				if (period == null) {
					throw new AonCoreException( MessageFormat.format("No se ha encontrado un ejercicio activo para la fecha {0,date,dd/MM/yyyy}",entryDate) );
				}
				
				return new AccountEntry()
					.setDomain(mod.getDomain())
					.setPeriod(period.getId())
					.setEntryDate( entryDate )
					.setEntryType(AccountEntryType.TAX);
			}
		};
	}
	
}
