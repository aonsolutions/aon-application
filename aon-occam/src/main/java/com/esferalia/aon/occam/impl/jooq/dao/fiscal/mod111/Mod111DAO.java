package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;


import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111.Mod111Declaration.ComplementaryBeahaviour;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod111DAO extends FiscalModelDAO {
	
	public static Stream<Mod111> getMod111s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFullFiscalModels(ctx,domain,FiscalModelType.M111,filter, Mod111::new);
	}

	public static Stream<Mod111> getMod111s(AONContext ctx,int domain) {
		return getMod111s(ctx, domain, null);
	}
	
	public static Mod111 get(AONContext ctx,int id) {
		ctx.checkRead();
		Mod111 mod111 = FiscalModelDAO.get(ctx,Mod111::new,id);
		if (mod111 != null) {
			mod111.setAlcatrazBound( AlcatrazDAO.hasAlcatrazBound(ctx, mod111.getId()));
		}
		return mod111;
	}
	
	public static Stream<Mod111> getSamePeriodFiscalModels(AONContext ctx,Mod111 fm) {
		return FiscalModelDAO.getSamePeriodFiscalModels(ctx, fm, Mod111::new);
	}

	public static Stream<Mod111> getSamePeriodModels(AONContext ctx,Mod111 fm) {
		return FiscalModelDAO.getSamePeriodModels(ctx, fm, Mod111::new);
	}
	
	public static Stream<Mod111> getSamePeriodNoAdmonModels(AONContext ctx,Mod111 fm) {
		return FiscalModelDAO.getSamePeriodNoAdmonFiscalModels(ctx, fm, Mod111::new);
	}

	public static Mod111 save(AONContext ctx, Mod111 mod111) {
		calculate(mod111);
		return FiscalModelDAO.save(ctx, mod111);
	}
	
	public static Mod111 saveComments(AONContext ctx, Mod111 mod111) {
		FiscalModelDAO.saveComments(ctx, mod111);
		return mod111;
	}

	private static Map<String, Object> getMvelContext(Mod111 mod111) {
		Map<String, Object> mvelCtx = new LinkedHashMap<>();
		for (String key : mod111.getMap().keySet()) {
			Mod111Key mod111Key = Mod111Key.getKey(key);
			if (mod111Key != null) {
				FiscalModelDetail detail = mod111.getMap().get(key);
				mvelCtx.put(mod111Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx; 
	}
	
	public static Mod111 calculate(Mod111 mod111) {
		Map<String, Object> mvelCtx = getMvelContext(mod111);
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		for (IMod111KeyDAO key : dec.getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression())) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod111.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		mod111.setDeclarationResult(dec.getResult(mod111));
		return mod111; 
	}
	
	public static Mod111 initialize(AONContext ctx,Mod111 mod111) {
		if (mod111 == null) {
			mod111 = new Mod111();
			mod111.setDomain(ctx.getDomainId());
		}
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);
		initializeFiscalModel(ctx, conf, mod111);
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		dec.initialize( ctx, mod111 );
		initializeMustExcludeInvoicesOnGeneration(ctx, conf, mod111);
		dec.ensureDetails(mod111);
		return mod111;
	}
	
	private static void initializeMustExcludeInvoicesOnGeneration(AONContext ctx, AonConfiguration conf, Mod111 mod111) {
		if ( !mod111.isComplementary() && ! mod111.isReplacement() ) {
			Integer admin = conf.fiscal().getAdministration();
			boolean inc = false;
			if (admin != null ) {
				// Administración por defecto es la misma del modelo, o bien si administración por defecto es Canarias y la del modelo es AEAT
				inc = (mod111.getAdministration() == Administration.values()[admin]) || (Administration.values()[admin].isCanarias() && mod111.isAEAT());
			} else {
				inc = getSamePeriodNoAdmonModels( ctx, mod111).noneMatch( Mod111::mustIncludeInvoicesOnGeneration );
			}
			mod111.setMustIncludeInvoicesOnGeneration( inc );
		}
	}
	
	public static Mod111 simulate(AONContext ctx,Mod111 mod111) {
		return createOrSimulate(ctx,mod111, true);	
	}

	public static Mod111 create(AONContext ctx,Mod111 mod111) {
		return createOrSimulate(ctx,mod111, false);
	}

	private static Mod111 createOrSimulate(AONContext ctx,Mod111 mod111, boolean simulate) {
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		dec.ensureDetails(mod111);
		Set<Alcatraz> invoices = null;
		if ( mod111.mustIncludeInvoicesOnGeneration() ) {
			invoices = dec.createFromInvoices(ctx,mod111);
		}
		Set<Integer> salaries = null;
		if ( mod111.mustIncludeSalariesOnGeneration() ) {
			salaries = dec.createFromSalary(ctx,mod111);
		}
		boolean useChargeDate = mod111.mustUseChargeDate();
		mod111.getMap().values().stream().forEach(FiscalModelDetail::calculate);
		dec.uniqueInitialize(ctx,mod111);
		if (simulate) {
			calculate(mod111);	
		} else {
			mod111 = save(ctx, mod111);
			AlcatrazDAO.deleteFiscalModel(ctx, mod111);
			if ( mod111.mustIncludeInvoicesOnGeneration() ) {
				AlcatrazDAO.saveModelInvoices(ctx, mod111, invoices);
			}
			if ( mod111.mustIncludeSalariesOnGeneration() ) {
				AlcatrazDAO.saveModelSalaries(ctx, mod111, salaries);
			}
		}
		mod111.setAlcatrazBound( AonCollectionUtils.isNotEmpty(invoices) || AonCollectionUtils.isNotEmpty(salaries) );
		mod111.setUseChargeDate( useChargeDate );
		return mod111;
	}
	
	public static Mod111 initializeForFinish(AONContext ctx,Mod111 mod111) {
		calculate(mod111);
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		dec.initializeDeclarationType(mod111);
		return FiscalModelDAO.initializeForFinish(ctx, mod111);
	}
	
	public static Mod111 markAsFinished(AONContext ctx,Mod111 mod111) {
		FiscalModelValidation.statusChange(mod111, FiscalStatus.FINISHED);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod111);
		mod111 = FiscalModelDAO.finish(ctx, mod111);
		mod111 = save(ctx, mod111);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod111.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod111;
	}
	
	public static Mod111 markAsPending(AONContext ctx,Mod111 mod111) {
		FiscalModelValidation.statusChange(mod111, FiscalStatus.PENDING);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod111);
		mod111.setStatus(FiscalStatus.PENDING);
		mod111.setDeclarationResult(null);
		mod111.setDeclarationResultType(null);
		mod111.setFinance(null);
		mod111 = save(ctx, mod111);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod111.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod111;
	}

	public static Mod111 markAsSent(AONContext ctx,Mod111 mod111) {
		FiscalModelValidation.statusChange(mod111, FiscalStatus.SENT);
		mod111.setStatus(FiscalStatus.SENT);
		mod111 = save(ctx, mod111);
		return mod111;
	}
	
	public static Mod111 markAsCustomerCheck(AONContext ctx,Mod111 mod111) {
		FiscalModelValidation.statusChange(mod111, FiscalStatus.CUSTOMER_CHECK);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod111);
		mod111 = FiscalModelDAO.finish(ctx, mod111);
		mod111.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod111 = save(ctx, mod111);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod111.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod111;
	}

	public static Mod111 markAsCustomerAccepted(AONContext ctx,Mod111 mod111) {
		FiscalModelValidation.statusChange(mod111, FiscalStatus.CUSTOMER_ACCEPTED);
		mod111.setStatus(FiscalStatus.CUSTOMER_ACCEPTED);
		mod111 = save(ctx, mod111);
		return mod111;
	}

	public static Mod111 markAsCustomerRejected(AONContext ctx,Mod111 mod111, String reason) {
		FiscalModelValidation.statusChange(mod111, FiscalStatus.CUSTOMER_REJECTED);
		mod111.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod111.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod111.setComments( comments );
		}
		mod111 = save(ctx, mod111);
		return mod111;
	}

	public static Mod111 aeatPresentation(AONContext ctx, Mod111 mod111, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod111, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			Mod111 changed = get(ctx, mod111.getId());
			if (changed != null) {
				changed.setNumber(response.getJustificante());
				return markAsSent(ctx, changed);
			}
		}
		return mod111;
	}
	
	public static Stream<Mod111> getSamePeriodEffectiveModels(AONContext ctx, Mod111 mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.eq(mod.getPeriod().value()));
		if (mod.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod.getId())); 
		}
		return getEffectiveModels(ctx, mod, cond ); 
	}
	public static Stream<Mod111> getEffectiveModels(AONContext ctx, Mod111 mod111, Condition cond ) {
		LinkedHashMap<Period, LinkedList<Mod111>> map = new LinkedHashMap<>();
		getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(mod111.getDomain()))
			.and(FS_MODEL.MODEL.eq( mod111.getModel().getValue() ))
			.and(FS_MODEL.ADMINISTRATION.eq(mod111.getAdministration().value()))
			.and( cond )
			.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.ID.asc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<Mod111>().apply(rec, Mod111::new))
			.filter( mod-> mod.getPeriod().isMonthPeriod() == mod.getPeriod().isMonthPeriod())
			.filter( mod-> mod.getPeriod().isQuarterPeriod() == mod.getPeriod().isQuarterPeriod())
			.forEach( mod -> {
				Mod111Declaration dec = Mod111Declaration.getInstance(mod);
				map.computeIfAbsent(mod.getPeriod(), k -> new LinkedList<>());
				if ( isEffectiveReplacement(dec, mod) ) {
					map.get(mod.getPeriod()).clear();
					map.get(mod.getPeriod()).add( mod);					
				} else {
					if (map.get(mod.getPeriod())
						.stream()
						.noneMatch( m -> isEffectiveReplacement(dec, mod)) ) {
						map.get(mod.getPeriod()).add( mod );
					}
				}
			});
		return map.values()
			.stream()
			.flatMap(Collection<Mod111>::stream)
			.map(mod -> fillModelDetails(ctx,mod))
		;
	}
	private static boolean isEffectiveReplacement( Mod111Declaration dec, Mod111 mod) {
		return mod.isReplacement() || (mod.isComplementary() && dec.getComplementaryBehaviour(mod) == ComplementaryBeahaviour.REPLACEMENT); 
	}

}

