package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod111DAO extends FiscalModelDAO {
	
	public static Stream<Mod111> getMod111s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return FiscalModelDAO.getFiscalModels(ctx,domain,FiscalModelType.M111,filter, Mod111::new);
	}

	public static Stream<Mod111> getMod111s(AONContext ctx,int domain) {
		return getMod111s(ctx, domain, null);
	}
	
	public static Mod111 get(AONContext ctx,int id) {
		ctx.checkRead();
		return FiscalModelDAO.get(ctx,Mod111::new,id);
	}
	
	public static Stream<Mod111> getSamePeriodFiscalModels(AONContext ctx,Mod111 fm) {
		return FiscalModelDAO.getSamePeriodFiscalModels(ctx, fm, Mod111::new);
	}

	public static Stream<Mod111> getSamePeriodModels(AONContext ctx,Mod111 fm) {
		return FiscalModelDAO.getSamePeriodModels(ctx, fm, Mod111::new);
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
		initializeFiscalModel(ctx, mod111);
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		dec.initialize( ctx, mod111 );
		dec.ensureDetails(mod111);
		return mod111;
	}
	
	public static Mod111 reset(AONContext ctx,Mod111 mod111) {
		mod111.setMap(null);
		initializeIdentificationData(ctx, mod111);
		create(ctx,mod111);
		return mod111;
	}
	
	public static Mod111 create(AONContext ctx,Mod111 mod111) {
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		dec.ensureDetails(mod111);
		Set<Integer> invoices = dec.createFromInvoices(ctx,mod111);
		Set<Integer> salaries = dec.createFromSalary(ctx,mod111);
		for (FiscalModelDetail detail : mod111.getMap().values()) {
			detail.setAccumulatedAmount( AonMathUtils.round(detail.getAccumulatedAmount()));
			detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));	
			detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
		}
		dec.uniqueInitialize(ctx,mod111);
		mod111 = save(ctx, mod111);
		
		AlcatrazDAO.deleteFiscalModel(ctx, mod111);
		AlcatrazDAO.saveModelInvoices(ctx, mod111, invoices);
		AlcatrazDAO.saveModelSalaries(ctx, mod111, salaries);
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
}

