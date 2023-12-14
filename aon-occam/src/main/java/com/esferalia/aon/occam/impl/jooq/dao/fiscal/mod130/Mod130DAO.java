package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Stream;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130.Mod130Declaration.ComplementaryBeahaviour;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod130DAO extends FiscalModelDAO {

	public static Stream<Mod130> getMod130s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFullFiscalModels(ctx,domain,FiscalModelType.M130,filter, Mod130::new);
	}
	
	public static Stream<Mod130> getMod130s(AONContext ctx,int domain) {
		return getMod130s(ctx, domain, null);
	}
	
	public static Mod130 get(AONContext ctx,int id) {
		ctx.checkRead();
		Mod130 mod130 = FiscalModelDAO.get(ctx,Mod130::new,id);
		if (mod130 != null) {
			mod130.setAlcatrazBound( AlcatrazDAO.hasAlcatrazBound(ctx, mod130.getId()));
		}
		return mod130;
	}
	
	public static Mod130 save(AONContext ctx, Mod130 mod130) {
		calculate(ctx, mod130);
		FiscalModel fm = FiscalModelDAO.save(ctx, mod130);
		return get(ctx, fm.getId());
	}
	
	public static Mod130 saveComments(AONContext ctx, Mod130 mod130) {
		FiscalModelDAO.saveComments(ctx, mod130);
		return mod130;
	}
	
	public static Mod130 calculate(AONContext ctx, final Mod130 mod130) {
		Mod130Declaration dec = Mod130Declaration.getInstance(mod130);
		return dec.calculate( ctx,mod130 );
	}
	
	public static Mod130 initialize(AONContext ctx,Mod130 mod130) {
		if (mod130 == null) {
			mod130 = new Mod130();
			mod130.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod130);
		Mod130Declaration dec = Mod130Declaration.getInstance(mod130);
		dec.initialize( ctx, mod130 );
		dec.ensureDetails(mod130);
		getMod130s(ctx, mod130.getDomain()).forEach(mod130::ensureDeponent);
		return mod130;
	}
	
	public static Mod130 initializeForFinish(AONContext ctx,Mod130 mod130) {
		calculate(ctx,mod130);
		Mod130Declaration dec = Mod130Declaration.getInstance(mod130);
		dec.initializeDeclarationType(mod130);
		return FiscalModelDAO.initializeForFinish(ctx, mod130);
	}
	
	public static Mod130 create(AONContext ctx,Mod130 mod130) {
		Mod130Declaration dec = Mod130Declaration.getInstance(mod130);
		dec.ensureDetails(mod130);
		Set<Alcatraz> invoices = dec.createFromInvoices(ctx,mod130);
		dec.uniqueInitialize(ctx,mod130);
		calculate(ctx, mod130);
		mod130 = save(ctx, mod130);
		AlcatrazDAO.deleteFiscalModel(ctx, mod130);
		AlcatrazDAO.saveModelInvoices(ctx, mod130, invoices);
		mod130.setAlcatrazBound( AonCollectionUtils.isNotEmpty(invoices));
		return mod130;
	}
	
	public static Mod130 markAsFinished(AONContext ctx,Mod130 mod130) {
		FiscalModelValidation.statusChange(mod130, FiscalStatus.FINISHED);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod130);
		mod130 = FiscalModelDAO.finish(ctx, mod130);
		mod130 = save(ctx, mod130);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod130.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod130;
	}
	
	public static Mod130 markAsPending(AONContext ctx,Mod130 mod130) {
		FiscalModelValidation.statusChange(mod130, FiscalStatus.PENDING);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod130);
		mod130.setStatus(FiscalStatus.PENDING);
		mod130.setDeclarationResult(null);
		mod130.setDeclarationResultType(null);
		mod130.setFinance(null);
		mod130 = save(ctx, mod130);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod130.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod130;
	}
	
	public static Mod130 markAsSent(AONContext ctx,Mod130 mod130) {
		FiscalModelValidation.statusChange(mod130, FiscalStatus.SENT);
		mod130.setStatus(FiscalStatus.SENT);
		mod130 = save(ctx, mod130);
		return mod130;
	}
	
	public static Mod130 markAsCustomerCheck(AONContext ctx,Mod130 mod130) {
		FiscalModelValidation.statusChange(mod130, FiscalStatus.CUSTOMER_CHECK);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod130);
		mod130 = FiscalModelDAO.finish(ctx, mod130);
		mod130.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod130 = save(ctx, mod130);
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, mod130.getFinanceId())) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod130;
	}

	public static Mod130 markAsCustomerAccepted(AONContext ctx,Mod130 mod130) {
		FiscalModelValidation.statusChange(mod130, FiscalStatus.CUSTOMER_ACCEPTED);
		mod130.setStatus(FiscalStatus.CUSTOMER_ACCEPTED);
		mod130 = save(ctx, mod130);
		return mod130;
	}

	public static Mod130 markAsCustomerRejected(AONContext ctx,Mod130 mod130, String reason) {
		FiscalModelValidation.statusChange(mod130, FiscalStatus.CUSTOMER_REJECTED);
		mod130.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod130.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod130.setComments( comments );
		}
		mod130 = save(ctx, mod130);
		return mod130;
	}
	
	public static Mod130 aeatPresentation(AONContext ctx, Mod130 mod130, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod130, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			Mod130 changed = get(ctx, mod130.getId());
			if (changed != null) {
				changed.setNumber(response.getJustificante());
				return markAsSent(ctx, changed);
			}
		}
		return mod130;
	}
	
	public static Stream<Mod130> getSamePeriodFiscalModels(AONContext ctx,Mod130 fm) {
		return FiscalModelDAO.getSamePeriodFiscalModels(ctx, fm, Mod130::new)
			.filter( m -> AonStringUtils.equals(m.getDocument(), fm.getDocument()));
	}
	public static Stream<Mod130> getSamePeriodModels(AONContext ctx,Mod130 fm) {
		return FiscalModelDAO.getSamePeriodModels(ctx, fm, Mod130::new)
			.filter( m -> AonStringUtils.equals(m.getDocument(), fm.getDocument()));
	}
	public static Stream<Mod130> getPreviousModels(AONContext ctx, Mod130 mod) {
		return FiscalModelDAO.getPreviousModels(ctx, mod, Mod130::new)
			.filter( m -> AonStringUtils.equals(m.getDocument(), mod.getDocument()));
	}
	
	public static Stream<Mod130> getPreviousEffectiveModels(AONContext ctx, Mod130 mod) {
		Condition cond = FS_MODEL.YEAR.eq(mod.getYear())
			.and(FS_MODEL.PERIOD.lt(mod.getPeriod().value()));
		if (mod.getId() != null) {
			cond = cond.and(FS_MODEL.ID.ne(mod.getId())); 
		}
		return getEffectiveModels(ctx, mod, cond ); 
	}
	
	public static Stream<Mod130> getEffectiveModels(AONContext ctx, Mod130 mod130, Condition cond ) {
		LinkedHashMap<Period, LinkedList<Mod130>> map = new LinkedHashMap<>();
		getSelect(ctx)
			.where(FS_MODEL.DOMAIN.eq(mod130.getDomain()))
			.and(FS_MODEL.MODEL.eq( mod130.getModel().getValue() ))
			.and(FS_MODEL.ADMINISTRATION.eq(mod130.getAdministration().value()))
			.and( cond )
			.orderBy(FS_MODEL.PERIOD.desc(),FS_MODEL.ID.asc())
			.fetch()
			.stream()
			.map(rec -> new FiscalModelFiller<Mod130>().apply(rec, Mod130::new))
			.filter( mod-> mod.getPeriod().isMonthPeriod() == mod.getPeriod().isMonthPeriod())
			.filter( mod-> mod.getPeriod().isQuarterPeriod() == mod.getPeriod().isQuarterPeriod())
			.forEach( mod -> {
				Mod130Declaration dec = Mod130Declaration.getInstance(mod);
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
			.flatMap(Collection<Mod130>::stream)
			.map(mod -> fillModelDetails(ctx,mod))
		;
	}
	private static boolean isEffectiveReplacement( Mod130Declaration dec, Mod130 mod) {
		return mod.isReplacement() || (mod.isComplementary() && dec.getComplementaryBehaviour(mod) == ComplementaryBeahaviour.REPLACEMENT); 
	}
}
