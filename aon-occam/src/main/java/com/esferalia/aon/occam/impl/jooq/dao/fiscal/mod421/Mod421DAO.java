package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.util.Collection;
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
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.Mod421MVELContext;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod421DAO extends FiscalModelDAO {
	
	public static Stream<Mod421> getMod421s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFullFiscalModels(ctx,domain,FiscalModelType.M421,filter, Mod421::new);
	}
	
	public static Stream<Mod421> getMod421s(AONContext ctx,int domain) {
		return getMod421s(ctx, domain, null);
	}

	public static Mod421 get(AONContext ctx,int id) {
		ctx.checkRead();
		Mod421 mod421 = FiscalModelDAO.get(ctx,Mod421::new,id);
		if ( mod421 != null) {
			mod421.setInvoicesBound(AlcatrazDAO.hasAlcatrazBound(ctx, mod421.getId()));
			Mod421Declaration dec = Mod421Declaration.getInstance(mod421);
			dec.fillSimplifiedRegime(mod421);
		}
		return mod421;
	}

	public static Mod421 saveComments(AONContext ctx, Mod421 mod421) {
		FiscalModelDAO.saveComments(ctx, mod421);
		return mod421;
	}
	
	public static Mod421 save(AONContext ctx, Mod421 mod421) {
		calculate(mod421);
		return FiscalModelDAO.save(ctx, mod421);
	}
	
	private static Mod421MVELContext getMvelContext( Mod421Declaration dec, Mod421 mod421 ) {
		dec.populateSimplifiedRegime( mod421 );
		Mod421MVELContext mvelCtx = new Mod421MVELContext(mod421);
		for (String key : mod421.getMap().keySet()) {
			Mod421Key mod421Key = Mod421Key.getKey(key);
			if (mod421Key != null) {
				FiscalModelDetail detail = mod421.getMap().get(key);
				mvelCtx.put(mod421Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx; 
	}
	
	public static Mod421 calculate(Mod421 mod421, Mod421Declaration dec) {
		Mod421MVELContext mvelCtx = getMvelContext( dec, mod421 );
		for (IMod421KeyDAO key : dec.getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) ) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod421.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		dec.fillSimplifiedRegime(mod421);
		mod421.setDeclarationResult(dec.getResult(mod421));
		return mod421; 
	}
	
	public static Mod421 calculate(Mod421 mod421) {
		Mod421Declaration dec = Mod421Declaration.getInstance(mod421);
		return calculate(mod421, dec);
	}
	
	public static Mod421 initialize(AONContext ctx,Mod421 mod421) {
		if (mod421 == null) {
			mod421 = new Mod421();
			mod421.setDomain(ctx.getDomainId());
		}
		mod421.getMessages().clear();
		mod421.setAdministration(Administration.CANARIAS); // Solo Canarias
		initializeFiscalModel(ctx, mod421);
		try {
			Mod421Declaration dec = Mod421Declaration.getInstance(mod421);
			dec.initialize( ctx, mod421 );
			dec.ensureDetails(mod421);
		} catch (AonCoreException e) {
			mod421.addMessage(e.getMessage());
		}
		Mod421 m421 = mod421;
		AppParamDAO.getApplicationParameterStream( ctx, f -> 
			f.getDomainProperty().eq(m421.getDomain() )
			.and( f.getNameProperty().eq( AppParam.FS_FORCE_DIFF_CALC.name() ))
			)
			.filter( ap -> AonNumberUtils.equals(m421.getYear(),AonNumberUtils.toInteger(ap.getValue())))
			.findFirst()
			.ifPresent( ap -> {
				m421.addMessage("Se han modificado facturas declaradas en el ejercicio. Se recomienda realizar cálculo por diferencia.");
			}
		);
		
		return mod421;
	}
	
	public static Mod421 reset(AONContext ctx,Mod421 mod421) {
		mod421.setMap(null);
		initializeIdentificationData(ctx, mod421);
		create(ctx,mod421);
		return mod421;
	}
	
	public static Mod421 simulate(AONContext ctx,Mod421 mod421) {
		Mod421Declaration dec = Mod421Declaration.getInstance(mod421);
		dec.createOnTheFly(ctx,mod421);
		calculate(mod421);
		dec.specificInitialization(mod421);
		return mod421;
	}

	public static Mod421 create(AONContext ctx,Mod421 mod421) {
		Mod421Declaration dec = Mod421Declaration.getInstance(mod421);
		Set<Alcatraz> invoices = dec.createOnTheFly(ctx,mod421);
		calculate(mod421);
		dec.specificInitialization(mod421);
		mod421 = save(ctx, mod421);
		AlcatrazDAO.deleteFiscalModel(ctx, mod421);
		AlcatrazDAO.saveModelInvoices(ctx, mod421, invoices);
		mod421.setInvoicesBound( AlcatrazDAO.hasAlcatrazBound(ctx, mod421.getId()));
		return mod421;
	}

	public static Mod421 markAsPending(AONContext ctx,Mod421 mod421) {
		FiscalModelValidation.statusChange(mod421, FiscalStatus.PENDING);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod421);
		mod421.setStatus(FiscalStatus.PENDING);
		mod421.setDeclarationResult(null);
		mod421.setDeclarationResultType(null);
		mod421.setFinance(null);
		mod421 = save(ctx, mod421);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod421.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod421;
	}
	
 	public static Mod421 initializeForFinish(AONContext ctx,Mod421 mod421) {
		calculate(mod421);
		Mod421Declaration dec = Mod421Declaration.getInstance(mod421);
		dec.initializeDeclarationType(mod421);
		return FiscalModelDAO.initializeForFinish(ctx, mod421);
	}
	
	public static Mod421 markAsFinished(AONContext ctx,Mod421 mod421) {
		FiscalModelValidation.statusChange(mod421, FiscalStatus.FINISHED);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod421);
		mod421 = FiscalModelDAO.finish(ctx, mod421);
		mod421 = save(ctx, mod421);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod421.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod421;
	}

	public static Mod421 markAsSent(AONContext ctx,Mod421 mod421) {
		FiscalModelValidation.statusChange(mod421, FiscalStatus.SENT);
		mod421.setStatus(FiscalStatus.SENT);
		mod421 = save(ctx, mod421);
		return mod421;
	}
	
	public static Mod421 markAsCustomerCheck(AONContext ctx,Mod421 mod421) {
		FiscalModelValidation.statusChange(mod421, FiscalStatus.CUSTOMER_CHECK);
		mod421 = FiscalModelDAO.finish(ctx, mod421);
		mod421.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod421 = save(ctx, mod421);
		return mod421;
	}

	public static Mod421 markAsCustomerAccepted(AONContext ctx,Mod421 mod421) {
		FiscalModelValidation.statusChange(mod421, FiscalStatus.CUSTOMER_ACCEPTED);
		mod421.setStatus(FiscalStatus.CUSTOMER_ACCEPTED);
		mod421 = save(ctx, mod421);
		return mod421;
	}

	public static Mod421 markAsCustomerRejected(AONContext ctx,Mod421 mod421, String reason) {
		FiscalModelValidation.statusChange(mod421, FiscalStatus.CUSTOMER_REJECTED);
		mod421.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod421.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod421.setComments( comments );
		}
		mod421 = save(ctx, mod421);
		return mod421;
	}
	
	public static Stream<Mod421> getSamePeriodEffectiveModels(AONContext ctx, FiscalModel mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.eq(mod.getPeriod().value()));
		if (mod.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod.getId())); 
		}
		return getEffectiveModels(ctx, mod, cond ); 
	}
	
	public static Stream<Mod421> getLastPeriodEffectiveModels(AONContext ctx, Mod421 mod) {
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
	
	public static Stream<Mod421> getPreviousEffectiveModels(AONContext ctx, Mod421 mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.lessThan(mod.getPeriod().value()));
		return getEffectiveModels(ctx, mod, cond);
	}
	
	public static Stream<Mod421> getEffectiveModels(AONContext ctx, IFiscalModel mod, Condition cond ) {
		LinkedHashMap<Period, LinkedList<Mod421>> map = new LinkedHashMap<>();
		boolean fromMod390 = (mod.getModel() == FiscalModelType.M390  || mod.getModel() == FiscalModelType.M390_HF);
		getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(mod.getDomain()))
			.and(FS_MODEL.MODEL.eq( FiscalModelType.M421.getValue() ))
			.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().value()))
			.and( cond )
			.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.ID.asc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<Mod421>().apply(rec, Mod421::new))
			.filter( mod421 -> fromMod390 || mod421.getPeriod().isMonthPeriod() == mod.getPeriod().isMonthPeriod())
			.filter( mod421 -> fromMod390 || mod421.getPeriod().isQuarterPeriod() == mod.getPeriod().isQuarterPeriod())
			.forEach( mod421 -> {
				Mod421Declaration dec = Mod421Declaration.getInstance(mod421);
				map.computeIfAbsent(mod421.getPeriod(), k -> new LinkedList<>());
				if (fromMod390) {
					map.get(mod421.getPeriod()).add( mod421 ); // Si es desde la generación del 390, se añaden todos, incluso las complementarias
				}
				else if ( isEffectiveReplacement(dec, mod421) ) {
					map.get(mod421.getPeriod()).clear();
					map.get(mod421.getPeriod()).add( mod421 );					
				} else {
					if (map.get(mod421.getPeriod())
						.stream()
						.noneMatch( m -> isEffectiveReplacement(dec, mod421)) ) {
						map.get(mod421.getPeriod()).add( mod421 );
					}
				}
			});
		return map.values()
			.stream()
			.flatMap(Collection<Mod421>::stream)
			.map(mod421 -> fillModelDetails(ctx,mod421))
		;
	}
	
	private static boolean isEffectiveReplacement( Mod421Declaration dec, Mod421 mod) {
		return mod.isReplacement() || (mod.isComplementary() && dec.getComplementaryBehaviour(mod) == ComplementaryBeahaviour.REPLACEMENT); 
	}	
	
	public static Mod421 unrecord(AONContext ctx, Mod421 mod) {
		if (mod.isRecorded()) {
			AccountEntryDAO.delete(ctx, mod.getAccountEntry());
		}
		return get(ctx, mod.getId());
	}

	public static Mod421 doRecord(AONContext ctx, Mod421 mod) {
//		if (mod.isRecorded()) {
//			throw new IllegalArgumentException(MessageFormat.format(
//				"El modelo {0} de {1} ya se encuentra contabilizado"
//				,mod.getModelFullName(),mod.getAdministration().getDescription()));
//		}
//		Optional<AccountEntryDetailExpressionScript<Mod421>> script = Mod421DefaultAccountEntryScript.getScript(mod);
//		if (!script.isPresent()) {
//			throw new IllegalArgumentException(MessageFormat.format(
//				"No se ha encontrado un script válido para el modelo {0} de {1}"
//				,mod.getModelFullName(),mod.getAdministration().getDescription()));
//		}
//		AccSctiptMVELContext<Mod421> mvel = getAccSctiptMVELContext( ctx, mod );
//		Optional<AccountEntry> optAe = Optional.ofNullable(mvel.fillDetails(ctx, mod, script.get()));
//		if (optAe.isPresent() && !optAe.get().getDetails().isEmpty() ) {
//			Integer entryId = AccountEntryDAO.save(ctx, optAe.get());
//			FiscalModelDAO.doRecord(ctx, mod.getId(), entryId);
//			mod = get(ctx, mod.getId());
//		}
		return mod;
	}
	
//	private static AccSctiptMVELContext<Mod421> getAccSctiptMVELContext(AONContext ctx, Mod421 mod) {
//		return new AccSctiptMVELContext<Mod421>() {
//			private static final long serialVersionUID = -858390524319034071L;
//			@Override
//			public void fillContext() {
//				put(MODEL_KEY,mod);
//				put(MODEL_DECLARATION_KEY,Mod421Declaration.getInstance(mod));
//				for (String key : mod.getMap().keySet()) {
//					Mod421Key mod421Key = Mod421Key.getKey(key);
//					if (mod421Key != null) {
//						FiscalModelDetail detail = mod.getMap().get(key);
//						String mapKey = mod421Key.toString(); 
//						if (mod421Key.getBox() != 0) {
//							mapKey = "C" + mod421Key.getBox();
//						}
//						put(mapKey, detail==null?0.0:detail.getAmount());
//					}
//				}
//			}
//			@Override
//			public AccountEntry fillAccountEntry(Mod421 mod) {
//				Date entryDate = new Date();
//				AccountPeriod period = AccountPeriodDAO.getActivePeriod(ctx,entryDate);
//				if (period == null) {
//					throw new AonCoreException( MessageFormat.format("No se ha encontrado un ejercicio activo para la fecha {0,date,dd/MM/yyyy}",entryDate) );
//				}
//				
//				return new AccountEntry()
//					.setDomain(mod.getDomain())
//					.setPeriod(period.getId())
//					.setEntryDate( entryDate )
//					.setEntryType(AccountEntryType.TAX);
//			}
//		};
//	}
	
}
