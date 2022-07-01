package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.accounting.AccSctiptMVELContext;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailExpressionScript;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.fiscal.mod303.entry.Mod303DefaultAccountEntryScript;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelValidation;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303MVELContext;
import com.esferalia.aon.occam.impl.jooq.dao.VATDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
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
			Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
			dec.fillSimplifiedRegime(mod303);
		}
		return mod303;
	}

	public static Stream<Mod303> getSamePeriodFiscalModels(AONContext ctx,Mod303 fm) {
		return FiscalModelDAO.getSamePeriodFiscalModels(ctx, fm, Mod303::new);
	}

	public static Stream<Mod303> getSamePeriodModels(AONContext ctx,Mod303 fm) {
		return FiscalModelDAO.getSamePeriodModels(ctx, fm, Mod303::new);
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
		initializeFiscalModel(ctx, mod303);
		initializeProrrate(ctx, mod303);
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		dec.initialize( ctx, mod303 );
		dec.ensureDetails(mod303);
		return mod303;
	}
	
	private static void initializeProrrate(AONContext ctx, Mod303 mod303) {
		if (mod303.getProrateKey() != null) {
			Pair<Double,String> prorrateInfo =  getMod303s( ctx , mod303.getDomain())
				.filter(mod -> mod.getAdministration() == mod303.getAdministration() )
				.map(mod ->  new Pair<Double,String>(mod.getProratePercent(), mod.getSpecialProrateValue() ))
				.findFirst()
				.orElse(new Pair<>(0.0, "G"));
			if (AonMathUtils.equals(prorrateInfo.getLeft() ,100.0)) {
				prorrateInfo.setLeft( 0.0);
			}
			mod303.ensureDetail(mod303.getProrateKey()).setAmount(prorrateInfo.getLeft());
			mod303.ensureDetail(mod303.getProrateTypeKey()).setDescription(prorrateInfo.getRight());
			if (mod303.getPeriod().isLastPeriod()) {
				mod303.ensureDetail(mod303.getPreviousProrateKey()).setAmount(prorrateInfo.getLeft());
				Date fromDate = AonDateUtils.getYearFirstDay(mod303.getYear());
				Date toDate = AonDateUtils.getYearLastDay(mod303.getYear());
				VATDAO.getVatBreakdown(ctx,fromDate,toDate,mod303)
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

	public static Mod303 create(AONContext ctx,Mod303 mod303) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		Set<Integer> invoices = dec.createOnTheFly(ctx,mod303);
		dec.prorrateRegularization(ctx,mod303);
		calculate(mod303);
		dec.specificInitialization(mod303);
		mod303 = save(ctx, mod303);
		AlcatrazDAO.deleteFiscalModel(ctx, mod303);
		AlcatrazDAO.saveModelInvoices(ctx, mod303, invoices);
		return mod303;
	}

	public static Mod303 calculateProrrate(Mod303 mod303) {
		if (mod303.hasProrate() || mod303.hasPreviousProrate()) {
			double c70 = mod303.ensureDetail(Mod303Key.CM_070).getAmount();
			double c71 = mod303.ensureDetail(Mod303Key.CM_071).getAmount();
			if (AonMathUtils.isNotZero(c71)) {
				double prorrate = (c70 * 100 / c71);
				prorrate = AonMathUtils.ceil(prorrate,0);
				if (AonMathUtils.isGreatherThan(prorrate,100.0)) prorrate = 100.0;
				mod303.ensureDetail(mod303.getProrateKey()).setAmount( prorrate );
			}
		}
		return mod303;
	}

	public static Mod303 markAsPending(AONContext ctx,Mod303 mod303) {
		FiscalModelValidation.statusChange(mod303, FiscalStatus.PENDING);
		mod303.setStatus(FiscalStatus.PENDING);
		mod303.setDeclarationResult(null);
		mod303.setDeclarationResultType(null);
		Finance finance = mod303.getFinance();
		mod303.setFinance(null);
		mod303 = save(ctx, mod303);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
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
		mod303 = FiscalModelDAO.finish(ctx, mod303);
		return save(ctx, mod303);
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
	
	public static Stream<Mod303> getEffectivePreviousModels(AONContext ctx, Mod303 mod) {
		return FiscalModelDAO.getEffectivePreviousModels(ctx, mod, Mod303::new);
	}
	
	public static Mod303 unrecord(AONContext ctx, Mod303 mod) {
		FiscalModelDAO.unrecord(ctx, mod.getAccountEntry());
		return get(ctx, mod.getId());
	}

	public static Mod303 doRecord(AONContext ctx, Mod303 mod) {
		Optional<AccountEntryDetailExpressionScript<Mod303>> script = Mod303DefaultAccountEntryScript.getScript(mod);
		if (script.isPresent()) {
			AccSctiptMVELContext<Mod303> mvel = getAccSctiptMVELContext( ctx, mod );
			Optional<AccountEntry> optAe = Optional.ofNullable(mvel.fillDetails(ctx, mod, script.get()));
			if (optAe.isPresent()) {
				Integer entryId = AccountEntryDAO.save(ctx, optAe.get());
				FiscalModelDAO.doRecord(ctx, mod.getId(), entryId);
				mod = get(ctx, mod.getId());
			}
		}
		return mod;
	}
	
	private static AccSctiptMVELContext<Mod303> getAccSctiptMVELContext(AONContext ctx, Mod303 mod) {
		return new AccSctiptMVELContext<Mod303>() {
			private static final long serialVersionUID = -858390524319034071L;
			@Override
			public void fillContext() {
				put(MODEL,mod);
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
