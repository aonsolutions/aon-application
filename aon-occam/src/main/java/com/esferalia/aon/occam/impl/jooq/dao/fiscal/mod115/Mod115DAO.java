package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115;

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
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115.Mod115Declaration.ComplementaryBeahaviour;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod115DAO extends FiscalModelDAO {
	
	public static Stream<Mod115> getMod115s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFiscalModels(ctx,domain,FiscalModelType.M115,filter, Mod115::new);
	}

	public static Stream<Mod115> getMod115s(AONContext ctx,int domain) {
		return getMod115s(ctx, domain, null);
	}
	
	public static Mod115 get(AONContext ctx,int id) {
		ctx.checkRead();
		Mod115 mod115 = FiscalModelDAO.get(ctx,Mod115::new,id);
		if (mod115 != null) {
			mod115.setAlcatrazBound( AlcatrazDAO.hasAlcatrazBound(ctx, mod115.getId()));
		}
		return mod115; 
	}
	
	public static Stream<Mod115> getSamePeriodFiscalModels(AONContext ctx,Mod115 fm) {
		return FiscalModelDAO.getSamePeriodFiscalModels(ctx, fm, Mod115::new);
	}

	public static Stream<Mod115> getSamePeriodEffectiveModels(AONContext ctx, Mod115 mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.eq(mod.getPeriod().value()));
		if (mod.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod.getId())); 
		}
		return getEffectiveModels(ctx, mod, cond ); 
	}

	public static Stream<Mod115> getEffectiveModels(AONContext ctx, Mod115 mod115, Condition cond ) {
		LinkedHashMap<Period, LinkedList<Mod115>> map = new LinkedHashMap<>();
		getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(mod115.getDomain()))
			.and(FS_MODEL.MODEL.eq( mod115.getModel().getValue() ))
			.and(FS_MODEL.ADMINISTRATION.eq(mod115.getAdministration().value()))
			.and( cond )
			.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.ID.asc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<Mod115>().apply(rec, Mod115::new))
			.filter( mod-> mod.getPeriod().isMonthPeriod() == mod.getPeriod().isMonthPeriod())
			.filter( mod-> mod.getPeriod().isQuarterPeriod() == mod.getPeriod().isQuarterPeriod())
			.forEach( mod -> {
				Mod115Declaration dec = Mod115Declaration.getInstance(mod);
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
			.flatMap(Collection<Mod115>::stream)
			.map(mod -> fillModelDetails(ctx,mod))
		;
	}
	private static boolean isEffectiveReplacement( Mod115Declaration dec, Mod115 mod) {
		return mod.isReplacement() || (mod.isComplementary() && dec.getComplementaryBehaviour(mod) == ComplementaryBeahaviour.REPLACEMENT); 
	}

	public static Stream<Mod115> getSamePeriodModels(AONContext ctx,Mod115 fm) {
		return FiscalModelDAO.getSamePeriodModels(ctx, fm, Mod115::new);
	}
	
	public static Mod115 save(AONContext ctx, Mod115 mod115) {
		calculate(mod115);
		return FiscalModelDAO.save(ctx, mod115);
	}
	
	public static Mod115 saveComments(AONContext ctx, Mod115 mod115) {
		FiscalModelDAO.saveComments(ctx, mod115);
		return mod115;
	}
	
	private static Map<String, Object> getMvelContext(Mod115 mod115) {
		Map<String, Object> mvelCtx = new LinkedHashMap<>();
		for (String key : mod115.getMap().keySet()) {
			Mod115Key mod115Key = Mod115Key.getKey(key,mod115.getAdministration());
			if (mod115Key != null) {
				FiscalModelDetail detail = mod115.getMap().get(key);
				mvelCtx.put(mod115Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx; 
	}
	
	public static Mod115 calculate(Mod115 mod115) {
		Map<String, Object> mvelCtx = getMvelContext(mod115);
		Mod115Declaration dec = Mod115Declaration.getInstance(mod115);
		for (IMod115KeyDAO key : dec.getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression())) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod115.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		mod115.setDeclarationResult(dec.getResult(mod115));
		return mod115; 
	}
	
	public static Mod115 initialize(AONContext ctx,Mod115 mod115) {
		if (mod115 == null) {
			mod115 = new Mod115();
			mod115.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod115);
		Mod115Declaration dec = Mod115Declaration.getInstance(mod115);
		dec.initialize( ctx, mod115 );
		dec.ensureDetails(mod115);
		return mod115;
	}
	
	public static Mod115 create(AONContext ctx,Mod115 mod115) {
		Mod115Declaration dec = Mod115Declaration.getInstance(mod115);
		dec.ensureDetails(mod115);
		Set<Alcatraz> invoices = dec.createFromInvoices(ctx,mod115);
		for (FiscalModelDetail detail : mod115.getMap().values()) {
			detail.setAccumulatedAmount( AonMathUtils.round(detail.getAccumulatedAmount()));
			detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));	
			detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
		}
		dec.uniqueInitialize(ctx,mod115);
		mod115 = save(ctx, mod115);
		
		AlcatrazDAO.deleteFiscalModel(ctx, mod115);
		AlcatrazDAO.saveModelInvoices(ctx, mod115, invoices);
		mod115.setAlcatrazBound( AonCollectionUtils.isNotEmpty(invoices) );
		return mod115;
	}
	
	public static Mod115 initializeForFinish(AONContext ctx,Mod115 mod115) {
		calculate(mod115);
		Mod115Declaration dec = Mod115Declaration.getInstance(mod115);
		dec.initializeDeclarationType(mod115);
		return FiscalModelDAO.initializeForFinish(ctx, mod115);
	}
	
	public static Mod115 markAsFinished(AONContext ctx,Mod115 mod115) {
		FiscalModelValidation.statusChange(mod115, FiscalStatus.FINISHED);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod115);
		mod115 = FiscalModelDAO.finish(ctx, mod115);
		mod115 = save(ctx, mod115);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod115.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod115;
	}
	
	public static Mod115 markAsPending(AONContext ctx,Mod115 mod115) {
		FiscalModelValidation.statusChange(mod115, FiscalStatus.PENDING);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod115);
		mod115.setStatus(FiscalStatus.PENDING);
		mod115.setDeclarationResult(null);
		mod115.setDeclarationResultType(null);
		mod115.setFinance(null);
		mod115 = save(ctx, mod115);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod115.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod115;
	}

	public static Mod115 markAsSent(AONContext ctx,Mod115 mod115) {
		FiscalModelValidation.statusChange(mod115, FiscalStatus.SENT);
		mod115.setStatus(FiscalStatus.SENT);
		mod115 = save(ctx, mod115);
		return mod115;
	}
	
	public static Mod115 markAsCustomerCheck(AONContext ctx,Mod115 mod115) {
		FiscalModelValidation.statusChange(mod115, FiscalStatus.CUSTOMER_CHECK);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod115);
		mod115 = FiscalModelDAO.finish(ctx, mod115);
		mod115.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod115 = save(ctx, mod115);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod115.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod115;
	}

	public static Mod115 markAsCustomerAccepted(AONContext ctx,Mod115 mod115) {
		FiscalModelValidation.statusChange(mod115, FiscalStatus.CUSTOMER_ACCEPTED);
		mod115.setStatus(FiscalStatus.CUSTOMER_ACCEPTED);
		mod115 = save(ctx, mod115);
		return mod115;
	}

	public static Mod115 markAsCustomerRejected(AONContext ctx,Mod115 mod115, String reason) {
		FiscalModelValidation.statusChange(mod115, FiscalStatus.CUSTOMER_REJECTED);
		mod115.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod115.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod115.setComments( comments );
		}
		mod115 = save(ctx, mod115);
		return mod115;
	}
	
	public static Mod115 aeatPresentation(AONContext ctx, Mod115 mod115, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod115, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			Mod115 changed = get(ctx, mod115.getId());
			if (changed != null) {
				changed.setNumber(response.getJustificante());
				return markAsSent(ctx, changed);
			}
		}
		return mod115;
	}
}

