package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod131;

import java.util.Set;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod131DAO extends FiscalModelDAO {
	
	public static Mod131 create(AONContext ctx,Mod131 mod131) {
		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
		dec.ensureDetails( mod131 );
		dec.copyActivities( ctx, mod131 );
		Set<Alcatraz> invoices = dec.createFromInvoices(ctx,mod131);
		calculate(ctx, mod131);
		for (Mod131Activity activity : mod131.getActivities()) {
			// Solo se recalculan los datos de la actividad, si hay epígrafe
			if (activity.getEpigraph() != null)
				calculateActivity(ctx, mod131, activity);
		}
		for (IMod131KeyDAO key : dec.getKeys()) {
			key.initialize(ctx, mod131);
		}
		calculate(ctx, mod131);
		mod131 = save(ctx, mod131);
		AlcatrazDAO.deleteFiscalModel(ctx, mod131);
		AlcatrazDAO.saveModelInvoices(ctx, mod131, invoices);
		mod131.setAlcatrazBound( AonCollectionUtils.isNotEmpty(invoices));
		return mod131;
	}
	
	
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************
	// ***********************************************************************************************

	public static Stream<Mod131> getMod131s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFullFiscalModels(ctx,domain,FiscalModelType.M131,filter, Mod131::new)
			.map(fm -> fillActivities(fm));
	}
	public static Stream<Mod131> getMod131s(AONContext ctx,int domain) {
		return getMod131s(ctx, domain, null);
	}
	
	public static Mod131 get(AONContext ctx,int id) {
		ctx.checkRead();
		Mod131 mod131 = FiscalModelDAO.get(ctx,Mod131::new,id);
		if (mod131 != null) {
			mod131.setAlcatrazBound( AlcatrazDAO.hasAlcatrazBound(ctx, mod131.getId()));
			fillActivities(mod131);
		}
		return mod131;
	}
	
	private static Mod131 fillActivities(Mod131 mod131) {
		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
		return dec.fillActivitiesFromMap(mod131);
	}
	
	public static Mod131 save(AONContext ctx, Mod131 mod131) {
		ctx.checkWrite();
		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
		dec.calculate(ctx, mod131);
		dec.fillMapFromActivities(mod131);
		FiscalModel fm = FiscalModelDAO.save(ctx, mod131);
		return get(ctx, fm.getId());
	}

	public static Mod131 saveComments(AONContext ctx, Mod131 mod131) {
		FiscalModelDAO.saveComments(ctx, mod131);
		return mod131;
	}
	
	public static Mod131 calculate(AONContext ctx, Mod131 mod131) {
		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
		return dec.calculate(ctx,mod131);
	}
	
	public static Mod131Activity calculateActivity(AONContext ctx, Mod131 mod131, Mod131Activity activity) {
		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
		return dec.calculateActivity(ctx,activity);
	}
	
	public static Mod131 initialize(AONContext ctx,Mod131 mod131) {
		if (mod131 == null) {
			mod131 = new Mod131();
			mod131.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod131);
		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
		dec.initialize( ctx, mod131 );
		dec.ensureDetails(mod131);
		getMod131s(ctx, mod131.getDomain()).forEach(mod131::ensureDeponent);
		return mod131;
	}
	
	public static Stream<Mod131> getSamePeriodFiscalModels(AONContext ctx,Mod131 fm) {
		return FiscalModelDAO.getSamePeriodFiscalModels(ctx, fm, Mod131::new)
			.filter( m -> AonStringUtils.equals(m.getDocument(), fm.getDocument()));
	}
	public static Stream<Mod131> getPreviousModels(AONContext ctx, Mod131 mod) {
		return FiscalModelDAO.getPreviousModels(ctx, mod, Mod131::new)
			.filter( m -> AonStringUtils.equals(m.getDocument(), mod.getDocument()));
	}
	
	public static Mod131 initializeForFinish(AONContext ctx,Mod131 mod131) {
		calculate(ctx,mod131);
		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
		dec.initializeDeclarationType(mod131);
		return FiscalModelDAO.initializeForFinish(ctx, mod131);
	}
	
	// -------------------------------------------------------------------- AEAT
	public static Mod131 aeatPresentation(AONContext ctx, Mod131 mod131, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {
			DataResponseDAO.insertAEATResponse(ctx, mod131, aeatResponse);
			AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
			Mod131 changed = get(ctx, mod131.getId());
			if (changed != null) {
				changed.setNumber(response.getJustificante());
				return markAsSent(ctx, changed);
			}
		}
		return mod131;
	}
	// -------------------------------------------------------------------- STATUS
	public static Mod131 markAsPending(AONContext ctx,Mod131 mod131) {
		FiscalModelValidation.statusChange(mod131, FiscalStatus.PENDING);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod131);
		mod131.setStatus(FiscalStatus.PENDING);
		mod131.setDeclarationResult(null);
		mod131.setDeclarationResultType(null);
		mod131.setFinance(null);
		mod131 = save(ctx, mod131);
		Integer newFinanceId = mod131 == null ? null : mod131.getFinanceId();
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, newFinanceId)) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod131;
	}
	
	public static Mod131 markAsFinished(AONContext ctx,Mod131 mod131) {
		FiscalModelValidation.statusChange(mod131, FiscalStatus.FINISHED);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod131);
		mod131 = FiscalModelDAO.finish(ctx, mod131);
		mod131 = save(ctx, mod131);
		Integer newFinanceId = mod131 == null ? null : mod131.getFinanceId();
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, newFinanceId)) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod131;
	}
	
	public static Mod131 markAsSent(AONContext ctx,Mod131 mod131) {
		FiscalModelValidation.statusChange(mod131, FiscalStatus.SENT);
		mod131.setStatus(FiscalStatus.SENT);
		mod131 = save(ctx, mod131);
		return mod131;
	}
	
	public static Mod131 markAsCustomerCheck(AONContext ctx,Mod131 mod131) {
		FiscalModelValidation.statusChange(mod131, FiscalStatus.CUSTOMER_CHECK);
		Integer oldFinanceId = FiscalModelDAO.getFinance(ctx, mod131);
		mod131 = FiscalModelDAO.finish(ctx, mod131);
		mod131.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod131 = save(ctx, mod131);
		Integer newFinanceId = mod131 == null ? null : mod131.getFinanceId();
		if (oldFinanceId != null && AonNumberUtils.notEquals(oldFinanceId, newFinanceId)) {
			FinanceDAO.delete(ctx, oldFinanceId);
		}
		return mod131;
	}

	public static Mod131 markAsCustomerAccepted(AONContext ctx,Mod131 mod131) {
		FiscalModelValidation.statusChange(mod131, FiscalStatus.CUSTOMER_ACCEPTED);
		mod131.setStatus(FiscalStatus.CUSTOMER_ACCEPTED);
		mod131 = save(ctx, mod131);
		return mod131;
	}

	public static Mod131 markAsCustomerRejected(AONContext ctx,Mod131 mod131, String reason) {
		FiscalModelValidation.statusChange(mod131, FiscalStatus.CUSTOMER_REJECTED);
		mod131.setStatus(FiscalStatus.CUSTOMER_REJECTED);
		if (AonStringUtils.isNotBlank(reason)) {
			String comments = mod131.getComments();
			if (AonStringUtils.isNotBlank(comments)) {
				comments = AonStringUtils.join(comments, "\n", reason);
			} else {
				comments = reason; 
			}
			mod131.setComments( comments );
		}
		mod131 = save(ctx, mod131);
		return mod131;
	}
}
