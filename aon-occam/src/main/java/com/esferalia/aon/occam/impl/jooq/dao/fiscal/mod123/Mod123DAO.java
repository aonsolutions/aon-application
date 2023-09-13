package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123.Mod123Declaration.ComplementaryBeahaviour;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod123DAO extends FiscalModelDAO {
	
	
	public static Stream<Mod123> getMod123s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFiscalModels(ctx,domain,FiscalModelType.M123,filter, Mod123::new);
	}

	public static Stream<Mod123> getMod123s(AONContext ctx,int domain) {
		return getMod123s(ctx, domain, null);
	}
	
	public static Mod123 get(AONContext ctx,int id) {
		ctx.checkRead();
		Mod123 mod123 = FiscalModelDAO.get(ctx,Mod123::new,id);
		if (mod123 != null) {
			mod123.setAlcatrazBound( AlcatrazDAO.hasAlcatrazBound(ctx, mod123.getId()));
		}
		return mod123; 
	}
	
	public static Stream<Mod123> getSamePeriodFiscalModels(AONContext ctx,Mod123 fm) {
		return FiscalModelDAO.getSamePeriodFiscalModels(ctx, fm, Mod123::new);
	}

	public static Stream<Mod123> getSamePeriodModels(AONContext ctx,Mod123 fm) {
		return FiscalModelDAO.getSamePeriodModels(ctx, fm, Mod123::new);
	}
	
	public static Mod123 save(AONContext ctx, Mod123 mod123) {
		calculate(mod123);
		return FiscalModelDAO.save(ctx, mod123);
	}
	
	public static Mod123 saveComments(AONContext ctx, Mod123 mod123) {
		FiscalModelDAO.saveComments(ctx, mod123);
		return mod123;
	}
	
	private static Map<String, Object> getMvelContext(Mod123 mod123) {
		Map<String, Object> mvelCtx = new LinkedHashMap<>();
		for (String key : mod123.getMap().keySet()) {
			Mod123Key mod123Key = Mod123Key.getKey(key,mod123.getAdministration());
			if (mod123Key != null) {
				FiscalModelDetail detail = mod123.getMap().get(key);
				mvelCtx.put(mod123Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx; 
	}
	
	public static Mod123 calculate(Mod123 mod123) {
		Map<String, Object> mvelCtx = getMvelContext(mod123);
		Mod123Declaration dec = Mod123Declaration.getInstance(mod123);
		for (IMod123KeyDAO key : dec.getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression())) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod123.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		mod123.setDeclarationResult(dec.getResult(mod123));
		return mod123; 
	}
	
	public static Mod123 initialize(AONContext ctx,Mod123 mod123) {
		if (mod123 == null) {
			mod123 = new Mod123();
			mod123.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod123);
		Mod123Declaration dec = Mod123Declaration.getInstance(mod123);
		dec.initialize( ctx, mod123 );
		dec.ensureDetails(mod123);
		return mod123;
	}
	
	public static Mod123 create(AONContext ctx,Mod123 mod123) {
		Mod123Declaration dec = Mod123Declaration.getInstance(mod123);
		dec.ensureDetails(mod123);
		Set<Alcatraz> invoices = dec.createFromInvoices(ctx,mod123);
		for (FiscalModelDetail detail : mod123.getMap().values()) {
			detail.setAccumulatedAmount( AonMathUtils.round(detail.getAccumulatedAmount()));
			detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));	
			detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
		}
		dec.uniqueInitialize(ctx,mod123);
		mod123 = save(ctx, mod123);
		
		AlcatrazDAO.deleteFiscalModel(ctx, mod123);
		AlcatrazDAO.saveModelInvoices(ctx, mod123, invoices);
		mod123.setAlcatrazBound( AonCollectionUtils.isNotEmpty(invoices) );
		return mod123;
	}
	
	public static Mod123 initializeForFinish(AONContext ctx,Mod123 mod123) {
		calculate(mod123);
		Mod123Declaration dec = Mod123Declaration.getInstance(mod123);
		dec.initializeDeclarationType(mod123);
		return FiscalModelDAO.initializeForFinish(ctx, mod123);
	}
	
	public static Mod123 markAsFinished(AONContext ctx,Mod123 mod123) {
		FiscalModelValidation.statusChange(mod123, FiscalStatus.FINISHED);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod123);
		mod123 = FiscalModelDAO.finish(ctx, mod123);
		mod123 = save(ctx, mod123);
		if (oldFinanceId != null) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod123;
	}
	
	public static Mod123 markAsPending(AONContext ctx,Mod123 mod123) {
		FiscalModelValidation.statusChange(mod123, FiscalStatus.PENDING);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod123);
		mod123.setStatus(FiscalStatus.PENDING);
		mod123.setDeclarationResult(null);
		mod123.setDeclarationResultType(null);
		mod123.setFinance(null);
		mod123 = save(ctx, mod123);
		if (oldFinanceId != null) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod123;
	}

	public static Mod123 markAsSent(AONContext ctx,Mod123 mod123) {
		FiscalModelValidation.statusChange(mod123, FiscalStatus.SENT);
		mod123.setStatus(FiscalStatus.SENT);
		mod123 = save(ctx, mod123);
		return mod123;
	}
	
	public static Mod123 markAsCustomerCheck(AONContext ctx,Mod123 mod123) {
		FiscalModelValidation.statusChange(mod123, FiscalStatus.CUSTOMER_CHECK);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod123);
		mod123 = FiscalModelDAO.finish(ctx, mod123);
		mod123.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod123 = save(ctx, mod123);
		if (oldFinanceId != null) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod123;
	}

	public static Mod123 markAsCustomerAccepted(AONContext ctx,Mod123 mod123) {
		FiscalModelValidation.statusChange(mod123, FiscalStatus.CUSTOMER_ACCEPTED);
		mod123.setStatus(FiscalStatus.CUSTOMER_ACCEPTED);
		mod123 = save(ctx, mod123);
		return mod123;
	}

	public static Mod123 markAsCustomerRejected(AONContext ctx,Mod123 mod123, String reason) {
		FiscalModelValidation.statusChange(mod123, FiscalStatus.CUSTOMER_REJECTED);
		mod123.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod123.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod123.setComments( comments );
		}
		mod123 = save(ctx, mod123);
		return mod123;
	}
	
	public static Mod123 aeatPresentation(AONContext ctx, Mod123 mod123, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod123, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			Mod123 changed = get(ctx, mod123.getId());
			if (changed != null) {
				changed.setNumber(response.getJustificante());
				return markAsSent(ctx, changed);
			}
		}
		return mod123;
	}

	public static Stream<Mod123> getSamePeriodEffectiveModels(AONContext ctx, Mod123 mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.eq(mod.getPeriod().value()));
		if (mod.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod.getId())); 
		}
		return getEffectiveModels(ctx, mod, cond ); 
	}
	public static Stream<Mod123> getEffectiveModels(AONContext ctx, Mod123 mod123, Condition cond ) {
		LinkedHashMap<Period, LinkedList<Mod123>> map = new LinkedHashMap<>();
		getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(mod123.getDomain()))
			.and(FS_MODEL.MODEL.eq( mod123.getModel().getValue() ))
			.and(FS_MODEL.ADMINISTRATION.eq(mod123.getAdministration().value()))
			.and( cond )
			.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.ID.asc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<Mod123>().apply(rec, Mod123::new))
			.filter( mod-> mod.getPeriod().isMonthPeriod() == mod.getPeriod().isMonthPeriod())
			.filter( mod-> mod.getPeriod().isQuarterPeriod() == mod.getPeriod().isQuarterPeriod())
			.forEach( mod -> {
				Mod123Declaration dec = Mod123Declaration.getInstance(mod);
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
			.flatMap(Collection<Mod123>::stream)
			.map(mod -> fillModelDetails(ctx,mod))
		;
	}
	private static boolean isEffectiveReplacement( Mod123Declaration dec, Mod123 mod) {
		return mod.isReplacement() || (mod.isComplementary() && dec.getComplementaryBehaviour(mod) == ComplementaryBeahaviour.REPLACEMENT); 
	}
}

