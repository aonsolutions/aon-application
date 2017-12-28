package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;

import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record1;
import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.IMod390KeyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFDeclaration;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HFDAO extends FiscalModelDAO {
	
	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod390HF mod,IModelScript<Mod390Key> script,IMod390KeyDAO keyDAO);
	}
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	
	public static enum Mod390KeyInfoDAO {
		 NONE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO))))
		,IN_ACCRUAL_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getAccrualInputInvoicesInfo(ctx, mod, script,keyDAO))))
		,OUT_ACCRUAL_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getAccrualOutputInvoicesInfo(ctx, mod, script,keyDAO))))
		,COMPUTE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getCompute(ctx,mod, script) )))
		,COMPUTE_KEY ( (ctx, mod, script,keyDAO) -> getComputeKey(ctx,mod, script,keyDAO))
		;
		private IModelInfoProvider provider;
		
		private Mod390KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod390HF mod,IModelScript<Mod390Key> script,IMod390KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, script,keyDAO);
		}
	}

	public static Stream<Mod390HF> getMod390HFs(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M390_HF)
				.map( record -> map390HF(new Mod390HF(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	
	
	public static Mod390HF getMod390HF(AONContext ctx,int id) {
		ctx.checkRead();
		final Mod390HF mod = getModelRecord(ctx, id)
				.map( record -> map390HF(new Mod390HF(),record));
		if (mod != null) {
			getModelDetails(ctx,mod).forEach( detail -> mod.put( detail));	
		}
		return mod;
		
	}
	
	public static FiscalModel saveOnlyMod390HF(AONContext ctx, Mod390HF mod) {
		LinkedHashMap<String, FiscalModelDetail> newMap = new LinkedHashMap<String, FiscalModelDetail>();
		for (FiscalModelDetail det :  mod.getMap().values() ) {
			if (AonStringUtils.isBlank( det.getDescription() )
			 && AonMathUtils.isZero( det.getAccumulatedAmount() ) 
			 && AonMathUtils.isZero( det.getDeclaredAmount())
			 && AonMathUtils.isZero( det.getResultAmount())
			 && AonMathUtils.isZero( det.getAdjustAmount())
			 && AonMathUtils.isZero( det.getAmount()) ) {
				// Nothing
			} else {
				newMap.put(det.getType(), det);
			}
		}
		mod.setMap(newMap);
		return save(ctx, mod);
	}
	
	public static Mod390HF saveMod390HF(AONContext ctx, Mod390HF mod) {
		calculateMod390HF(ctx, mod);
		FiscalModel fm = saveOnlyMod390HF(ctx, mod);
		return getMod390HF(ctx, fm.getId());
	}
	
	public static Mod390HF saveCommentsMod390HF(AONContext ctx, Mod390HF mod) {
		saveComments(ctx, mod);
		return mod;
	}

	private static Mod390HFMVELContext getMvelContext( Mod390HFDeclaration dec, Mod390HF mod ) {
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
	public static Mod390HF calculateMod390HF(AONContext ctx, Mod390HF mod, Mod390HFDeclaration dec) {
		Mod390HFMVELContext mvelCtx = getMvelContext( dec, mod );
		for (IMod390KeyDAO key : dec.getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) ) {
//				Object ret =  mvelCtx.evaluateExpression(key.toString(), key.getExpression());
				 Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod; 
	}
	
	public static Mod390HF calculateMod390HF(AONContext ctx, Mod390HF mod) {
		Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
		return calculateMod390HF(ctx, mod, dec);
	}
	
	public static Mod390HF initializeMod390HF(AONContext ctx,Mod390HF mod) {
		if (mod == null) {
			mod = new Mod390HF();
			mod.setDomain(ctx.getDomainId());
			mod.setPeriod(Period.YEAR);
		}
		initializeFiscalModel(ctx, mod);
		declarationChanged(ctx, mod);
		
		return mod;
	}
	
	public static Mod390HF declarationChanged(AONContext ctx, Mod390HF mod) {
		initializeProrrate(ctx, mod);
		return mod;
	}

	
	private static void initializeProrrate(AONContext ctx, Mod390HF mod) {
		if (mod.getProrateKey() != null) {
			ctx.checkRead();
			Record1<Double> percent = ctx.getDslContext()
					.select(FS_MODEL_DETAIL.AMOUNT)
					.from(FS_MODEL)
					.innerJoin(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
					.where(FS_MODEL.DOMAIN.eq(ctx.getDomainId()))
					.and(FS_MODEL.MODEL.eq(FiscalModelType.M303.getValue()))
					.and(FS_MODEL.ADMINISTRATION.eq(mod.getAdministration().getValue()))
					.and(FS_MODEL.YEAR.eq(mod.getYear()))
					.and(FS_MODEL_DETAIL.TYPE.eq( Mod303Key.CM_003.getValue()))
					.orderBy(FS_MODEL.PERIOD.desc())
					.fetch()
					.stream()
					.findFirst()
					.orElse(null);
			double perc = 100;
			if (percent != null) {
				perc = percent.getValue(FS_MODEL_DETAIL.AMOUNT);
				if (AonMathUtils.isZero(perc)) perc = 100;
			}
			mod.ensureDetail(mod.getProrateKey()).setAmount(perc);
		}
	}
	
	public static Mod390HF createMod390HF(AONContext ctx,final Mod390HF mod) {
		final Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);

		// Primera inicialización.		
		for (IMod390KeyDAO key : dec.getKeys()) {
			FiscalModelDetail detail = mod.ensureDetail(key.getKey());
			detail.setExpression(key.getExpression());
			key.firstInitialize(ctx, mod);
		}

		// INTIALIZATION VIA BREAKDOWN		
		Mod390HFDAO.getVatBreakdown(ctx,mod)
			.forEach( vat -> dec.initialize(ctx, mod, vat) );
		
		if (mod.getProratePercent() != 0 && mod.getProratePercent() != 100) {
			for (Mod390Key key : dec.getProrateKeys()) {
				FiscalModelDetail det = mod.ensureDetail(key);
				det.setAccumulatedAmount(AonMathUtils.round(det.getAccumulatedAmount() * mod.getProratePercent() / 100));
			}
		}

		calculateMod390HF(ctx, mod);		
		dec.specificInitialization(ctx, mod);
		return mod; 
	}

	public static String getMod390HFInfo(AONContext ctx, Mod390HF mod, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey) {
		Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
		Mod390KeyInfoDAO k = Mod390KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod390Key key :script.getKeys()) {
			IMod390KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				return k.getInfo(ctx, mod, script, keyDAO);
			}
		}
		return null;
	}

	private static String defaultTemplate(IMod390KeyDAO keyDAO,Mod390HFDeclaration dec) {
		final String EXP_KEY = "_EXP_";
		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = -4910560506222174407L;
			@Override
			public Object get(Object key) {
				if (EXP_KEY.equals(key)) {
					return getExpression();
				}
				IMod390KeyDAO keyDAO = dec.valueOf(key.toString());
				String box = " @{" + keyDAO.toString()+"} ";
				put(EXP_KEY, AonStringUtils.replace(getExpression(), key.toString(), box));
				return null;
			}
			public String getExpression () {
				return (String) super.get(EXP_KEY); 
			}
		};
		mvelCtx.put(EXP_KEY, keyDAO.getExpression());
		for (IMod390KeyDAO keyValue : dec.getKeys()) {
			Mod390Key modKey = keyValue.getKey();
			mvelCtx.put(modKey.toString(), 0.0);
		}
		MVEL.eval(keyDAO.getExpression(), mvelCtx, mvelCtx);
		return "<li><b>Resultado:</b> " + ((String) mvelCtx.get(EXP_KEY)) + "= <b>@{"+keyDAO.toString()+"}" + "</b></li>";
	}

	private static String getComputeKey(AONContext ctx, Mod390HF mod, IModelScript<Mod390Key> script,IMod390KeyDAO keyDAO) {
		Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
		Mod390HFMVELContext mvelCtx = getMvelContext( dec, mod );
		mvelCtx.put("periodModels", getSamePeriodModels(ctx, mod).collect(Collectors.toCollection(LinkedList::new)));
		mvelCtx.put("lastPeriodModels", getLastPeriodModels(ctx, mod).collect(Collectors.toCollection(LinkedList::new)));
		return getCompute(ctx, mod, script,mvelCtx);
	}
	
	private static String getCompute(AONContext ctx, Mod390HF mod, IModelScript<Mod390Key> script) {
		return getCompute(ctx, mod, script, getMvelContext(Mod390HFDeclaration.getInstance(mod), mod));
	}
	private static String getCompute(AONContext ctx, Mod390HF mod, IModelScript<Mod390Key> script,Mod390HFMVELContext mvelCtx) {
		Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
		StringBuilder buf = new StringBuilder();
		buf.append("<pre style=\"font-family: Fixed, monospace;font-size: 0.9em; margin-bottom: 1em; padding: 1em;text-align: left;\">");
		for (Mod390Key key : script.getKeys() ) {
			if (key != null) {
				IMod390KeyDAO keyDAO = dec.safeValueOf(mod, key.getValue());
				if (keyDAO != null) {
					String box = " [" + keyDAO.getKey().getBoxCode()+ "] ";
					buf.append(AonStringUtils.CR_LF);
					buf.append("<b>DETALLE DEL C\u00C1LCULO DE LA CASILLA: " + box + " - " + script.getLabel() + "</b>");
					buf.append(AonStringUtils.CR_LF);
					buf.append(AonStringUtils.CR_LF);
					buf.append("<ul style=\"padding-left: 20px;\">");
					if (AonStringUtils.isNotBlank( keyDAO.getExpression())) {
						buf.append("<li><b>F\u00F3rmula:</b> " + keyDAO.getExpression() + "</li>" );
					}
					String template = keyDAO.getTemplate();
					if (AonStringUtils.isBlank( template )) {
						template = defaultTemplate(keyDAO, dec);	
					}
					if (AonStringUtils.isNotBlank( template )) {
						Object result = TemplateRuntime.eval(template, mvelCtx);
						buf.append(result != null ? result.toString() : null);
					} else {
						
					}
					buf.append("</ul>");
				}
			}
		}
		buf.append("</pre>");
		return buf.toString();
	}

	// -------------------------------------------------------------------- INVOICES
	
	private static String getInvoicesInfo(AONContext ctx, final Mod390HF mod
			, final IModelScript<Mod390Key> script, IMod390KeyDAO keyDAO) {
		String title = "FACTURAS QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod) 
				+ " DEL " + mod.getPeriod().getDescription()
				+ " DE " + mod.getYear();
		return VATFormatter.formatInvoices(title,script.getLabel()
			,getVatBreakdown(ctx, mod, true)
					.filter( br ->  keyDAO.acceptValue(mod, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
	private static String getAccrualOutputInvoicesInfo(AONContext ctx, final Mod390HF mod
			, final IModelScript<Mod390Key> script, IMod390KeyDAO keyDAO) {
		String title = "FACTURAS CRITERIO CAJA QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod) 
				+ " DEL " + mod.getPeriod().getDescription()
				+ " DE " + mod.getYear();
		return VATFormatter.formatInvoices(title,script.getLabel()
			,getAccrualBreakdown(ctx, mod,true)
					.filter( br ->  br.isSales()  )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static String getAccrualInputInvoicesInfo(AONContext ctx, final Mod390HF mod
			, final IModelScript<Mod390Key> script, IMod390KeyDAO keyDAO) {
		String title = "FACTURAS CRITERIO CAJA QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod) 
				+ " DEL " + mod.getPeriod().getDescription()
				+ " DE " + mod.getYear();
		return VATFormatter.formatInvoices(title,script.getLabel()
			,getAccrualBreakdown(ctx, mod,true)
					.filter( br ->  !br.isSales()  )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
	// -------------------------------------------------------------------- UTIL
	public static Mod390HF markAsFinished(AONContext ctx,Mod390HF mod) {
		mod = FiscalModelDAO.finish(ctx, mod);
		return saveMod390HF(ctx, mod);
	}
	
	public static Mod390HF markAsPending(AONContext ctx,Mod390HF mod) {
		mod.setDeclarationType( (String) null);
		mod.setStatus(FiscalStatus.PENDING);
		Finance finance = mod.getFinance();
		mod.setFinance(null);
		mod = saveMod390HF(ctx, mod);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
		}
		return mod;
	}
	
	public static Mod390HF markAsSent(AONContext ctx,Mod390HF mod) {
		mod.setStatus(FiscalStatus.SENT);
		mod = saveMod390HF(ctx, mod);
		return mod;
	}
	
	public static Stream<VatContext> getAccrualBreakdown(final AONContext ctx, final Mod390HF mod, boolean diffDisabled) {
		Date fromDate = diffDisabled
			?FiscalUtils.getPeriodStart(mod)		
			:AonDateUtils.getYearFirstDay(mod.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod);
		return VATDAO.getAccrualBreakdown(ctx, fromDate, toDate,mod);
	}

	public static Stream<VatContext> getAccrualBreakdown(AONContext ctx, final Mod390HF mod) {
		return getAccrualBreakdown(ctx, mod, false);
	}
	
	public static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod390HF mod) {
		return getVatBreakdown(ctx,mod, false);
	}
	
	public static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod390HF mod, boolean diffDisabled) {
		Date fromDate = diffDisabled
			?FiscalUtils.getPeriodStart(mod)		
			:AonDateUtils.getYearFirstDay(mod.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod);
		return VATDAO.getVatBreakdown(ctx,fromDate,toDate,mod);
	}
	
	public static double getPercent1( FiscalModel model ) {
		return 4.0;
	}
	public static double getPercent2( FiscalModel model ) {
		return 10.0;
	}
	public static double getPercent3( FiscalModel model ) {
		return 21.0;
	}
	public static double getSurchargePercent1( FiscalModel model ) {
		return 0.5;
	}
	public static double getSurchargePercent2( FiscalModel model ) {
		return 1.4;
	}
	public static double getSurchargePercent3( FiscalModel model ) {
		return 5.2;
	}

	
	public static void print(Mod390HF mod) {
		for (String key : mod.getMap().keySet() ) {
			FiscalModelDetail det = mod.getMap().get(key);
			System.out.println(key + "\t" +  det.getAmount() );
		}
	}
	
	public static double getVatAccrualPaymentOutputBase(AONContext ctx, Mod390HF mod) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod);
		return VATDAO.getVatAccrualPaymentOutputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentOutputQuota(AONContext ctx, Mod390HF mod) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod);
		return VATDAO.getVatAccrualPaymentOutputQuota(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputBase(AONContext ctx, Mod390HF mod) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod);
		return VATDAO.getVatAccrualPaymentInputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputQuota(AONContext ctx, Mod390HF mod) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod);
		return VATDAO.getVatAccrualPaymentInputQuota(ctx,fromDate,toDate);
	}
	
	
}
