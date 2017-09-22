package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
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
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.mod303.IMod303KeyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod303.Mod303Declaration;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod303DAO extends FiscalModelDAO {
	
	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod303 mod,IModelScript<Mod303Key> script,IMod303KeyDAO keyDAO);
	}
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	
	public static enum Mod303KeyInfoDAO {
		 NONE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO))))
		,IN_ACCRUAL_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getAccrualInputInvoicesInfo(ctx, mod, script,keyDAO))))
		,OUT_ACCRUAL_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getAccrualOutputInvoicesInfo(ctx, mod, script,keyDAO))))
		,DIFF_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO))))
		,DIFF_IN_ACCRUAL_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffInAccrualInvoicesInfo(ctx, mod, script,keyDAO))))
		,DIFF_OUT_ACCRUAL_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffOutAccrualInvoicesInfo(ctx, mod, script,keyDAO))))
		,COMPUTE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getExpression(mod, script,keyDAO))))
		,COMPUTE_KEY ( (ctx, mod, script,keyDAO) -> getComputeKey(ctx,mod, script,keyDAO))
		;
		private IModelInfoProvider provider;
		
		private Mod303KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod303 mod,IModelScript<Mod303Key> script,IMod303KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, script,keyDAO);
		}
	}

	public static Stream<Mod303> getMod303s(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M303)
				.map( record -> map303(new Mod303(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	
	
	public static Mod303 getMod303(AONContext ctx,int id) {
		ctx.checkRead();
		final Mod303 mod303 = getModelRecord(ctx, id)
				.map( record -> map303(new Mod303(),record));
		if (mod303 != null) {
			getModelDetails(ctx,mod303).forEach( detail -> mod303.put( detail));	
		}
		if (mod303.isOldMod303()) {
			transformToNewMap(mod303);
		}
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		dec.fillSimplifiedRegime(mod303);
		return mod303;
		
	}
	
	public static Mod303 saveMod303(AONContext ctx, Mod303 mod303) {
		calculateMod303(ctx, mod303);
		FiscalModel fm = save(ctx, mod303);
		return getMod303(ctx, fm.getId());
	}
	
	public static Mod303 saveCommentsMod303(AONContext ctx, Mod303 mod303) {
		saveComments(ctx, mod303);
		return mod303;
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
	public static Mod303 calculateMod303(AONContext ctx, Mod303 mod303, Mod303Declaration dec) {
		Mod303MVELContext mvelCtx = getMvelContext( dec, mod303 );
		for (IMod303KeyDAO key : dec.getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) ) {
				Object ret =  mvelCtx.evaluateExpression(key.toString(), key.getExpression());
				// Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod303.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		dec.fillSimplifiedRegime(mod303);
		return mod303; 
	}
	
	public static Mod303 calculateMod303(AONContext ctx, Mod303 mod303) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		return calculateMod303(ctx, mod303, dec);
	}
	
	public static Mod303 initializeMod303(AONContext ctx,Mod303 mod303) {
		if (mod303 == null) {
			mod303 = new Mod303();
			mod303.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod303);
		declarationChanged(ctx, mod303);
		
		// Cálculo por diferencia.
		String diff = AppParamDAO.fetchValue(ctx, AppParam.FS_MOD303_BY_DIFFERENCE_DISABLED);
		mod303.setDiffCalculationDisabled(AonStringUtils.equals(diff, AonStringUtils.ONE));
		
		return mod303;
	}
	
	public static Mod303 declarationChanged(AONContext ctx, Mod303 mod303) {
		initializeProrrate(ctx, mod303);
		return mod303;
	}

	
	private static void initializeProrrate(AONContext ctx, Mod303 mod303) {
		if (mod303.getProrateKey() != null) {
			ctx.checkRead();
			Record1<Double> percent = ctx.getDslContext()
					.select(FS_MODEL_DETAIL.AMOUNT)
					.from(FS_MODEL)
					.innerJoin(FS_MODEL_DETAIL).on(FS_MODEL.ID.equal(FS_MODEL_DETAIL.FS_MODEL))
					.where(FS_MODEL.DOMAIN.eq(ctx.getDomainId()))
					.and(FS_MODEL.MODEL.eq(FiscalModelType.M303.getValue()))
					.and(FS_MODEL.ADMINISTRATION.eq(mod303.getAdministration().getValue()))
					.and(FS_MODEL.YEAR.eq(mod303.getYear()))
					.and(FS_MODEL_DETAIL.TYPE.eq( mod303.getProrateKey().getValue()))
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
			mod303.ensureDetail(mod303.getProrateKey()).setAmount(perc);
		}
	}

	public static Mod303 createMod303(AONContext ctx,final Mod303 mod303) {
		final Mod303Declaration dec = Mod303Declaration.getInstance(mod303);

		// Primera inicialización.		
		for (IMod303KeyDAO key : dec.getKeys()) {
			FiscalModelDetail detail = mod303.ensureDetail(key.getKey());
			detail.setExpression(key.getExpression());
			key.firstInitialize(ctx, mod303);
		}

		// INTIALIZATION VIA BREAKDOWN		
		Mod303DAO.getVatBreakdown(ctx,mod303)
			.forEach( vat -> dec.initialize(ctx, mod303, vat) );
		
		// Inicialización del régimen simplificado.
		if (dec.hasSimplifiedRegime()) {
			dec.initializeSimplifiedRegime(ctx, mod303);
		}
		
		if (!mod303.isDiffCalculationDisabled()) {
			getModelRecords(ctx, ctx.getDomainId(),FiscalModelType.M303)
			.map( rec -> map303(new Mod303(),rec) )
			.filter(mod -> mod.getAdministration() == mod303.getAdministration())
			.filter(mod -> mod.getYear() == mod303.getYear())
			.filter(mod -> mod.getPeriod().ordinal() < mod303.getPeriod().ordinal())
			.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
			.forEach(mod -> {
				for (FiscalModelDetail source : mod.getMap().values() ) {
					Mod303Key key = Mod303Key.getKey(source.getType());
					if (key != null && key.isDiffEnabled()) {
						IMod303KeyDAO keyDAO = dec.getKey(key);
						if (keyDAO != null) {
							FiscalModelDetail target = mod303.ensureDetail(key);
							target.setDeclaredAmount(AonMathUtils.round(target.getDeclaredAmount() + source.getAmount()));
						}
					}
				}
			});
		}

		if (mod303.getProratePercent() != 0 && mod303.getProratePercent() != 100) {
			for (Mod303Key key : dec.getProrateKeys()) {
				FiscalModelDetail det = mod303.ensureDetail(key);
				det.setAccumulatedAmount(AonMathUtils.round(det.getAccumulatedAmount() * mod303.getProratePercent() / 100));
			}
		}

		for (FiscalModelDetail detail : mod303.getMap().values()) {
			IMod303KeyDAO key = dec.safeValueOf(mod303, detail.getType());
			if (key != null && key.getKey().isDiffEnabled()) {
				detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));
				detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
			}
		}
		return calculateMod303(ctx, mod303); 
	}

	public static String getMod303Info(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		Mod303KeyInfoDAO k = Mod303KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod303Key key :script.getKeys()) {
			IMod303KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				return k.getInfo(ctx, mod303, script, keyDAO);
			}
		}
		return null;
	}

	private static String getExpression(Mod303 mod303
			, IModelScript<Mod303Key> script, IMod303KeyDAO keyDAO0) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		StringBuilder buf = new StringBuilder();
		int headerLength = 100;
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.center("DETALLE DEL C\u00C1LCULO", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.repeat("-", headerLength)));

		final StringBuilder expr = new StringBuilder();
		final StringBuilder resu = new StringBuilder();

		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = -4910560506222174407L;
			@Override
			public Object get(Object key) {
				IMod303KeyDAO keyDAO = dec.valueOf(key.toString());
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				String exprCopy = expr.toString();
				expr.delete(0, expr.length());
				expr.append(AonStringUtils.replace(exprCopy
						, key.toString()
						, box));
				Object value = super.get(key);
				exprCopy = resu.toString();
				resu.delete(0, resu.length());
				resu.append(AonStringUtils.replace(exprCopy
						, key.toString()
						," " + value.toString() + " "
						));
				return value;
			}
		};

		for (String keyValue : mod303.getMap().keySet()) {
			Mod303Key mod303Key = Mod303Key.getKey(keyValue);
			if (mod303Key != null) {
				FiscalModelDetail detail = mod303.getMap().get(keyValue);
				mvelCtx.put(mod303Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}


		for (Mod303Key key : script.getKeys() ) {
			if (key != null) {
				IMod303KeyDAO keyDAO = dec.safeValueOf(mod303, key.getValue());
				if (keyDAO != null) {
					resu.delete(0, resu.length());
					resu.append(keyDAO.getExpression());
					expr.delete(0, expr.length());
					expr.append(keyDAO.getExpression());
					
					String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
					buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
					buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.center("Casilla: " + box + " - " + script.getLabel(), headerLength)));
					Object ret = MVEL.eval(keyDAO.getExpression(), mvelCtx, mvelCtx);
					resu.append(" = ");
					resu.append(AonMathUtils.round((Double) ret));
					expr.append(" = ");
					expr.append(box);
					buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,"<b>F\u00F3rmula:</b> " + expr.toString()));		
					buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BLUE_BORDER_BOTTOM,"<b>Resultado:</b> " + resu.toString()));
				}
			}
		}
		return buf.toString();
	}
	
	private static String getComputeKey(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script,IMod303KeyDAO keyDAO) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		Mod303MVELContext mvelCtx = getMvelContext( dec, mod303 );
		mvelCtx.put("periodModels", getSamePeriodModels(ctx, mod303).collect(Collectors.toCollection(LinkedList::new)));
		mvelCtx.put("lastPeriodModels", getLastPeriodModels(ctx, mod303).collect(Collectors.toCollection(LinkedList::new)));
		return getCompute(ctx, mod303, script,mvelCtx);
	}
	
	private static String getCompute(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script,Map<String, Object> mvelCtx) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		StringBuilder buf = new StringBuilder();
		buf.append("<pre style=\"font-family: Fixed, monospace;font-size: 0.9em; margin-bottom: 1em; padding: 1em;text-align: left;\">");
		for (Mod303Key key : script.getKeys() ) {
			IMod303KeyDAO keyDAO = dec.safeValueOf(mod303, key.getValue());
			if (keyDAO != null) {
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				buf.append(AonStringUtils.CR_LF);
				buf.append("<b>DETALLE DEL C\u00C1LCULO DE LA CASILLA: " + box + " - " + script.getLabel() + "</b>");
				buf.append(AonStringUtils.CR_LF);
				buf.append(AonStringUtils.CR_LF);
				buf.append("<ul style=\"padding-left: 20px;\">");
				if (AonStringUtils.isNotBlank( keyDAO.getExpression())) {
					buf.append("<li><b>F\u00F3rmula:</b> " + keyDAO.getExpression() + "</li>" );
				}
				String template = keyDAO.getTemplate();
				if (AonStringUtils.isNotBlank( template )) {
					Object result = TemplateRuntime.eval(template, mvelCtx);
					buf.append(result != null ? result.toString() : null);
				}
				buf.append("</ul>");
			}
		}
		buf.append("</pre>");
		return buf.toString();
	}

	// -------------------------------------------------------------------- INVOICES
	
	private static String getInvoicesInfo(AONContext ctx, final Mod303 mod303
			, final IModelScript<Mod303Key> script, IMod303KeyDAO keyDAO) {
		String title = "FACTURAS QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ mod303.getModelName() 
				+ " DEL " + mod303.getPeriod().getDescription()
				+ " DE " + mod303.getYear();
		return VATFormatter.formatInvoices(title,script.getLabel()
			,getVatBreakdown(ctx, mod303, true)
					.filter( br ->  keyDAO.acceptValue(mod303, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
	private static String getAccrualOutputInvoicesInfo(AONContext ctx, final Mod303 mod303
			, final IModelScript<Mod303Key> script, IMod303KeyDAO keyDAO) {
		String title = "FACTURAS CRITERIO CAJA QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ mod303.getModelName() 
				+ " DEL " + mod303.getPeriod().getDescription()
				+ " DE " + mod303.getYear();
		return VATFormatter.formatInvoices(title,script.getLabel()
			,getAccrualBreakdown(ctx, mod303,true)
					.filter( br ->  br.isSales()  )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static String getAccrualInputInvoicesInfo(AONContext ctx, final Mod303 mod303
			, final IModelScript<Mod303Key> script, IMod303KeyDAO keyDAO) {
		String title = "FACTURAS CRITERIO CAJA QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ mod303.getModelName() 
				+ " DEL " + mod303.getPeriod().getDescription()
				+ " DE " + mod303.getYear();
		return VATFormatter.formatInvoices(title,script.getLabel()
			,getAccrualBreakdown(ctx, mod303,true)
					.filter( br ->  !br.isSales()  )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static String getDiffInAccrualInvoicesInfo(AONContext ctx, final Mod303 mod303
			, final IModelScript<Mod303Key> script, IMod303KeyDAO keyDAO) {
		return VATFormatter.formatDiffInvoices(getDiffTitle(mod303)
				,script.getLabel()
				,script.getKeys()
				,script.getKeyTypes()
				,getPreviousModels(ctx,mod303)
				 	.collect(Collectors.toCollection(LinkedList::new))
				,getAccrualBreakdown(ctx, mod303)
					.filter( br ->  !br.isSales()  )	
					.collect(Collectors.toCollection(LinkedList::new))
					);
	}
	private static String getDiffOutAccrualInvoicesInfo(AONContext ctx, final Mod303 mod303
			, final IModelScript<Mod303Key> script, IMod303KeyDAO keyDAO) {
		return VATFormatter.formatDiffInvoices(getDiffTitle(mod303)
				,script.getLabel()
				,script.getKeys()
				,script.getKeyTypes()
				,getPreviousModels(ctx,mod303)
				 	.collect(Collectors.toCollection(LinkedList::new))
				,getAccrualBreakdown(ctx, mod303)
					.filter( br ->  br.isSales()  )	
					.collect( Collectors.toCollection(LinkedList::new) )
					);
	}
	
		
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod303 mod303
			, final IModelScript<Mod303Key> script, IMod303KeyDAO keyDAO) {
		return VATFormatter.formatDiffInvoices(getDiffTitle(mod303)
			,script.getLabel()
			,script.getKeys()
			,script.getKeyTypes()
			,getPreviousModels(ctx,mod303)
			 	.collect(Collectors.toCollection(LinkedList::new))
			,getVatBreakdown(ctx, mod303)
				.filter( br ->  keyDAO.acceptValue(mod303, br) )	
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}


	private static String getDiffTitle(final Mod303 mod303) {
		return "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
				+ mod303.getModelName() 
				+ " DEL " + mod303.getPeriod().getDescription()
				+ " DE " + mod303.getYear();
	}


	// -------------------------------------------------------------------- UTIL
	public static Mod303 finish(AONContext ctx,Mod303 mod303) {
		mod303 = FiscalModelDAO.finish(ctx, mod303);
		return saveMod303(ctx, mod303);
	}
	
	public static Mod303 reopen(AONContext ctx,Mod303 mod303) {
		mod303.setDeclarationType( (String) null);
		mod303.setStatus(FiscalStatus.PENDING);
		Finance finance = mod303.getFinance();
		mod303.setFinance(null);
		mod303 = saveMod303(ctx, mod303);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
		}
		return mod303;
	}
	public static Stream<VatContext> getAccrualBreakdown(final AONContext ctx, final Mod303 mod303, boolean diffDisabled) {
		Date fromDate = diffDisabled
			?FiscalUtils.getPeriodStart(mod303)		
			:AonDateUtils.getYearFirstDay(mod303.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod303);
		return VATDAO.getAccrualBreakdown(ctx, fromDate, toDate,mod303);
	}

	public static Stream<VatContext> getAccrualBreakdown(AONContext ctx, final Mod303 mod303) {
		return getAccrualBreakdown(ctx, mod303, mod303.isDiffCalculationDisabled());
	}
	
	public static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod303 mod303) {
		return getVatBreakdown(ctx,mod303, mod303.isDiffCalculationDisabled());
	}
	
	public static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod303 mod303, boolean diffDisabled) {
		Date fromDate = diffDisabled
			?FiscalUtils.getPeriodStart(mod303)		
			:AonDateUtils.getYearFirstDay(mod303.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod303);
		return VATDAO.getVatBreakdown(ctx,fromDate,toDate,mod303);
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

	
	public static void print(Mod303 mod) {
		for (String key : mod.getMap().keySet() ) {
			FiscalModelDetail det = mod.getMap().get(key);
			System.out.println(key + "\t" +  det.getAmount() );
		}
	}
	
	public static double getVatAccrualPaymentOutputBase(AONContext ctx, Mod303 mod303) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod303.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod303);
		return VATDAO.getVatAccrualPaymentOutputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentOutputQuota(AONContext ctx, Mod303 mod303) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod303.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod303);
		return VATDAO.getVatAccrualPaymentOutputQuota(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputBase(AONContext ctx, Mod303 mod303) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod303.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod303);
		return VATDAO.getVatAccrualPaymentInputBase(ctx,fromDate,toDate);
	}

	public static double getVatAccrualPaymentInputQuota(AONContext ctx, Mod303 mod303) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod303.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod303);
		return VATDAO.getVatAccrualPaymentInputQuota(ctx,fromDate,toDate);
	}
	
	@FunctionalInterface
	private static interface IMod303KeyConverter {
		void convert(Mod303 mod, FiscalModelDetail det, Mod303 map);
	}
	
	private static enum OldMod303Key {
		 CAG1		("303-AG1"		,(mod,det,map) -> {
			map.putDescription(Mod303Key.CT_SA11, AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-")));
			map.putDescription(Mod303Key.CT_SA1D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAG1_V1	("303-AG1V1"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA12, det.getAmount() ))
		,CAG1_V2	("303-AG1V2" 	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA13, det.getAmount() ))	
		,CAG1_V3	("303-AG1V3"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA14, det.getAmount() ))
		,CAG1_V4	("303-AG1V4"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA15, det.getAmount() ))
		,CAG1_V5	("303-AG1V5"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA16, det.getAmount() ))
		,CAG1_V6	("303-AG1V6"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA17, det.getAmount() ))
		,CAG1_V7	("303-AG1V7"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA18, det.getAmount() ))

		,CAG2		("303-AG2"		,(mod,det,map) -> {
			map.putDescription(Mod303Key.CT_SA21, AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-")));
			map.putDescription(Mod303Key.CT_SA2D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAG2_V1	("303-AG2V1"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA22, det.getAmount() ))
		,CAG2_V2	("303-AG2V2" 	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA23, det.getAmount() ))	
		,CAG2_V3	("303-AG2V3"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA24, det.getAmount() ))
		,CAG2_V4	("303-AG2V4"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA25, det.getAmount() ))
		,CAG2_V5	("303-AG2V5"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA26, det.getAmount() ))
		,CAG2_V6	("303-AG2V6"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA27, det.getAmount() ))
		,CAG2_V7	("303-AG2V7"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA28, det.getAmount() ))
		
		,CAG3		("303-AG3"		,(mod,det,map) -> {
			map.putDescription(Mod303Key.CT_SA31, AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-")));
			map.putDescription(Mod303Key.CT_SA3D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAG3_V1	("303-AG3V1"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA32, det.getAmount() ))
		,CAG3_V2	("303-AG3V2" 	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA33, det.getAmount() ))	
		,CAG3_V3	("303-AG3V3"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA34, det.getAmount() ))
		,CAG3_V4	("303-AG3V4"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA35, det.getAmount() ))
		,CAG3_V5	("303-AG3V5"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA36, det.getAmount() ))
		,CAG3_V6	("303-AG3V6"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA37, det.getAmount() ))
		,CAG3_V7	("303-AG3V7"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA38, det.getAmount() ))

		,CAG4		("303-AG4"		,(mod,det,map) -> {
			map.putDescription(Mod303Key.CT_SA41, AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-")));
			map.putDescription(Mod303Key.CT_SA4D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAG4_V1	("303-AG4V1"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA42, det.getAmount() ))
		,CAG4_V2	("303-AG4V2" 	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA43, det.getAmount() ))	
		,CAG4_V3	("303-AG4V3"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA44, det.getAmount() ))
		,CAG4_V4	("303-AG4V4"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA45, det.getAmount() ))
		,CAG4_V5	("303-AG4V5"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA46, det.getAmount() ))
		,CAG4_V6	("303-AG4V6"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA47, det.getAmount() ))
		,CAG4_V7	("303-AG4V7"	, (mod,det,map) -> map.putAmount(Mod303Key.CT_SA48, det.getAmount() ))

		// ACTIVIDAD 1		
		,CAC1     ("303-AC1"		,(mod,det,map) 	-> {
			String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-"));
			map.putDescription(Mod303Key.CT_S101, epigraph);
			map.putDescription(Mod303Key.CT_S10D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAC1_M1U ("303-AC1M1U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S11I, det.getAmount())) 
		,CAC1_M1I ("303-AC1M1I", (mod,det,map)	-> fillModule(mod,det,0, "303-AC1", Mod303Key.CT_S11U, Mod303Key.CT_S11F, Mod303Key.CT_S11D, Mod303Key.CT_S11R,map))
		,CAC1_M2U ("303-AC1M2U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S12I, det.getAmount() ))
		,CAC1_M2I ("303-AC1M2I", (mod,det,map)	-> fillModule(mod,det,1, "303-AC1", Mod303Key.CT_S12U, Mod303Key.CT_S12F, Mod303Key.CT_S12D, Mod303Key.CT_S12R,map))
		,CAC1_M3U ("303-AC1M3U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S13I, det.getAmount() ))
		,CAC1_M3I ("303-AC1M3I", (mod,det,map)	-> fillModule(mod,det,2, "303-AC1", Mod303Key.CT_S13U, Mod303Key.CT_S13F, Mod303Key.CT_S13D, Mod303Key.CT_S13R,map))
		,CAC1_M4U ("303-AC1M4U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S14I, det.getAmount() ))
		,CAC1_M4I ("303-AC1M4I", (mod,det,map)	-> fillModule(mod,det,3, "303-AC1", Mod303Key.CT_S14U, Mod303Key.CT_S14F, Mod303Key.CT_S14D, Mod303Key.CT_S14R,map))
		,CAC1_M5U ("303-AC1M5U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S15I, det.getAmount() ))
		,CAC1_M5I ("303-AC1M5I", (mod,det,map)	-> fillModule(mod,det,4, "303-AC1", Mod303Key.CT_S15U, Mod303Key.CT_S15F, Mod303Key.CT_S15D, Mod303Key.CT_S15R,map))
		,CAC1_M6U ("303-AC1M6U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S16I, det.getAmount() ))
		,CAC1_M6I ("303-AC1M6I", (mod,det,map)	-> fillModule(mod,det,5, "303-AC1", Mod303Key.CT_S16U, Mod303Key.CT_S16F, Mod303Key.CT_S16D, Mod303Key.CT_S16R,map))
		,CAC1_M7U ("303-AC1M7U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S17I, det.getAmount() ))
		,CAC1_M7I ("303-AC1M7I", (mod,det,map)	-> fillModule(mod,det,6, "303-AC1", Mod303Key.CT_S17U, Mod303Key.CT_S17F, Mod303Key.CT_S17D, Mod303Key.CT_S17R,map))
		,CAC1_C   ("303-AC1C" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S117, det.getAmount() ))
		,CAC1_D   ("303-AC1D" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S118, det.getAmount() ))
		,CAC1_Z   ("303-AC1Z" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S119, det.getAmount() ))
		,CAC1_ZA  ("303-AC1ZA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S1X1, det.getAmount() ))
		,CAC1_ZD  ("303-AC1ZD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S1X2, det.getAmount() ))
		,CAC1_E   ("303-AC1E" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S120, det.getAmount() ))
		,CAC1_F   ("303-AC1F" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S121, det.getAmount() ))
		,CAC1_G   ("303-AC1G" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S122, det.getAmount() ))
		,CAC1_H   ("303-AC1H" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S123, det.getAmount() ))
		,CAC1_HA  ("303-AC1HA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S1X1, det.getAmount() ))
		,CAC1_HD  ("303-AC1HD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S1X2, det.getAmount() ))
		,CAC1_I   ("303-AC1I" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S124, det.getAmount() ))
		,CAC1_J   ("303-AC1J" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S125, det.getAmount() ))
		,CAC1_K   ("303-AC1K" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S126, det.getAmount() ))
		,CAC1_L   ("303-AC1L" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S127, det.getAmount() ))
		,CAC1_M   ("303-AC1M" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S128, det.getAmount() ))
		
		// ACTIVIDAD 2		
		,CAC2    ("303-AC2"		,(mod,det,map) 	-> {
			String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-"));
			map.putDescription(Mod303Key.CT_S201, epigraph);
			map.putDescription(Mod303Key.CT_S20D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAC2_M1U ("303-AC2M1U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S21I, det.getAmount() ))
		,CAC2_M1I ("303-AC2M1I", (mod,det,map)	-> fillModule(mod,det,0, "303-AC2", Mod303Key.CT_S21U, Mod303Key.CT_S21F, Mod303Key.CT_S21D, Mod303Key.CT_S21R,map))
		,CAC2_M2U ("303-AC2M2U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S22I, det.getAmount() ))
		,CAC2_M2I ("303-AC2M2I", (mod,det,map)	-> fillModule(mod,det,1, "303-AC2", Mod303Key.CT_S22U, Mod303Key.CT_S22F, Mod303Key.CT_S22D, Mod303Key.CT_S22R,map))
		,CAC2_M3U ("303-AC2M3U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S23I, det.getAmount() ))
		,CAC2_M3I ("303-AC2M3I", (mod,det,map)	-> fillModule(mod,det,2, "303-AC2", Mod303Key.CT_S23U, Mod303Key.CT_S23F, Mod303Key.CT_S23D, Mod303Key.CT_S23R,map))
		,CAC2_M4U ("303-AC2M4U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S24I, det.getAmount() ))
		,CAC2_M4I ("303-AC2M4I", (mod,det,map)	-> fillModule(mod,det,3, "303-AC2", Mod303Key.CT_S24U, Mod303Key.CT_S24F, Mod303Key.CT_S24D, Mod303Key.CT_S24R,map))
		,CAC2_M5U ("303-AC2M5U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S25I, det.getAmount() ))
		,CAC2_M5I ("303-AC2M5I", (mod,det,map)	-> fillModule(mod,det,4, "303-AC2", Mod303Key.CT_S25U, Mod303Key.CT_S25F, Mod303Key.CT_S25D, Mod303Key.CT_S25R,map))
		,CAC2_M6U ("303-AC2M6U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S26I, det.getAmount() ))
		,CAC2_M6I ("303-AC2M6I", (mod,det,map)	-> fillModule(mod,det,5, "303-AC2", Mod303Key.CT_S26U, Mod303Key.CT_S26F, Mod303Key.CT_S26D, Mod303Key.CT_S26R,map))
		,CAC2_M7U ("303-AC2M7U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S27I, det.getAmount() ))
		,CAC2_M7I ("303-AC2M7I", (mod,det,map)	-> fillModule(mod,det,6, "303-AC2", Mod303Key.CT_S27U, Mod303Key.CT_S27F, Mod303Key.CT_S27D, Mod303Key.CT_S27R,map))
		,CAC2_C   ("303-AC2C" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S217, det.getAmount()))
		,CAC2_D   ("303-AC2D" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S218, det.getAmount()))
		,CAC2_Z   ("303-AC2Z" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S219, det.getAmount()))
		,CAC2_ZA  ("303-AC2ZA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S2X1, det.getAmount()))
		,CAC2_ZD  ("303-AC2ZD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S2X2, det.getAmount()))
		,CAC2_E   ("303-AC2E" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S220, det.getAmount()))
		,CAC2_F   ("303-AC2F" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S221, det.getAmount()))
		,CAC2_G   ("303-AC2G" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S222, det.getAmount()))
		,CAC2_H   ("303-AC2H" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S223, det.getAmount()))
		,CAC2_HA  ("303-AC2HA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S2X1, det.getAmount()))
		,CAC2_HD  ("303-AC2HD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S2X2, det.getAmount()))
		,CAC2_I   ("303-AC2I" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S224, det.getAmount()))
		,CAC2_J   ("303-AC2J" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S225, det.getAmount()))
		,CAC2_K   ("303-AC2K" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S226, det.getAmount()))
		,CAC2_L   ("303-AC2L" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S227, det.getAmount()))
		,CAC2_M   ("303-AC2M" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S228, det.getAmount()))

		// ACTIVIDAD 3		
		,CAC3    ("303-AC3"		,(mod,det,map) 	-> {
			String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-"));
			map.putDescription(Mod303Key.CT_S301, epigraph);
			map.putDescription(Mod303Key.CT_S30D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAC3_M1U ("303-AC3M1U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S31I, det.getAmount() ))
		,CAC3_M1I ("303-AC3M1I", (mod,det,map)	-> fillModule(mod,det,0, "303-AC3", Mod303Key.CT_S31U, Mod303Key.CT_S31F, Mod303Key.CT_S31D, Mod303Key.CT_S31R,map))
		,CAC3_M2U ("303-AC3M2U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S32I, det.getAmount() ))
		,CAC3_M2I ("303-AC3M2I", (mod,det,map)	-> fillModule(mod,det,1, "303-AC3", Mod303Key.CT_S32U, Mod303Key.CT_S32F, Mod303Key.CT_S32D, Mod303Key.CT_S32R,map))
		,CAC3_M3U ("303-AC3M3U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S33I, det.getAmount() ))
		,CAC3_M3I ("303-AC3M3I", (mod,det,map)	-> fillModule(mod,det,2, "303-AC3", Mod303Key.CT_S33U, Mod303Key.CT_S33F, Mod303Key.CT_S33D, Mod303Key.CT_S33R,map))
		,CAC3_M4U ("303-AC3M4U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S34I, det.getAmount() ))
		,CAC3_M4I ("303-AC3M4I", (mod,det,map)	-> fillModule(mod,det,3, "303-AC3", Mod303Key.CT_S34U, Mod303Key.CT_S34F, Mod303Key.CT_S34D, Mod303Key.CT_S34R,map))
		,CAC3_M5U ("303-AC3M5U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S35I, det.getAmount() ))
		,CAC3_M5I ("303-AC3M5I", (mod,det,map)	-> fillModule(mod,det,4, "303-AC3", Mod303Key.CT_S35U, Mod303Key.CT_S35F, Mod303Key.CT_S35D, Mod303Key.CT_S35R,map))
		,CAC3_M6U ("303-AC3M6U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S36I, det.getAmount() ))
		,CAC3_M6I ("303-AC3M6I", (mod,det,map)	-> fillModule(mod,det,5, "303-AC3", Mod303Key.CT_S36U, Mod303Key.CT_S36F, Mod303Key.CT_S36D, Mod303Key.CT_S36R,map))
		,CAC3_M7U ("303-AC3M7U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S37I, det.getAmount() ))
		,CAC3_M7I ("303-AC3M7I", (mod,det,map)	-> fillModule(mod,det,6, "303-AC3", Mod303Key.CT_S37U, Mod303Key.CT_S37F, Mod303Key.CT_S37D, Mod303Key.CT_S37R,map))
		,CAC3_C   ("303-AC3C" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S317, det.getAmount()))
		,CAC3_D   ("303-AC3D" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S318, det.getAmount()))
		,CAC3_Z   ("303-AC3Z" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S319, det.getAmount()))
		,CAC3_ZA  ("303-AC3ZA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S3X1, det.getAmount()))
		,CAC3_ZD  ("303-AC3ZD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S3X2, det.getAmount()))
		,CAC3_E   ("303-AC3E" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S320, det.getAmount()))
		,CAC3_F   ("303-AC3F" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S321, det.getAmount()))
		,CAC3_G   ("303-AC3G" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S322, det.getAmount()))
		,CAC3_H   ("303-AC3H" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S323, det.getAmount()))
		,CAC3_HA  ("303-AC3HA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S3X1, det.getAmount()))
		,CAC3_HD  ("303-AC3HD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S3X2, det.getAmount()))
		,CAC3_I   ("303-AC3I" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S324, det.getAmount()))
		,CAC3_J   ("303-AC3J" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S325, det.getAmount()))
		,CAC3_K   ("303-AC3K" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S326, det.getAmount()))
		,CAC3_L   ("303-AC3L" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S327, det.getAmount()))
		,CAC3_M   ("303-AC3M" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S328, det.getAmount()))

		// ACTIVIDAD 4		
		,CAC4    ("303-AC3"		,(mod,det,map) 	-> {
			String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( det.getDescription() , "-"));
			map.putDescription(Mod303Key.CT_S401, epigraph);
			map.putDescription(Mod303Key.CT_S40D, AonStringUtils.trim(AonStringUtils.substringAfter( det.getDescription() , "-")));
		})
		,CAC4_M1U ("303-AC4M1U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S41I, det.getAmount() ))
		,CAC4_M1I ("303-AC4M1I", (mod,det,map)	-> fillModule(mod,det,0, "303-AC4", Mod303Key.CT_S41U, Mod303Key.CT_S41F, Mod303Key.CT_S41D, Mod303Key.CT_S41R,map))
		,CAC4_M2U ("303-AC4M2U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S42I, det.getAmount() ))
		,CAC4_M2I ("303-AC4M2I", (mod,det,map)	-> fillModule(mod,det,1, "303-AC4", Mod303Key.CT_S42U, Mod303Key.CT_S42F, Mod303Key.CT_S42D, Mod303Key.CT_S42R,map))
		,CAC4_M3U ("303-AC4M3U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S43I, det.getAmount() ))
		,CAC4_M3I ("303-AC4M3I", (mod,det,map)	-> fillModule(mod,det,2, "303-AC4", Mod303Key.CT_S43U, Mod303Key.CT_S43F, Mod303Key.CT_S43D, Mod303Key.CT_S43R,map))
		,CAC4_M4U ("303-AC4M4U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S44I, det.getAmount() ))
		,CAC4_M4I ("303-AC4M4I", (mod,det,map)	-> fillModule(mod,det,3, "303-AC4", Mod303Key.CT_S44U, Mod303Key.CT_S44F, Mod303Key.CT_S44D, Mod303Key.CT_S44R,map))
		,CAC4_M5U ("303-AC4M5U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S45I, det.getAmount() ))
		,CAC4_M5I ("303-AC4M5I", (mod,det,map)	-> fillModule(mod,det,4, "303-AC4", Mod303Key.CT_S45U, Mod303Key.CT_S45F, Mod303Key.CT_S45D, Mod303Key.CT_S45R,map))
		,CAC4_M6U ("303-AC4M6U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S46I, det.getAmount() ))
		,CAC4_M6I ("303-AC4M6I", (mod,det,map)	-> fillModule(mod,det,5, "303-AC4", Mod303Key.CT_S46U, Mod303Key.CT_S46F, Mod303Key.CT_S46D, Mod303Key.CT_S46R,map))
		,CAC4_M7U ("303-AC4M7U", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S47I, det.getAmount() ))
		,CAC4_M7I ("303-AC4M7I", (mod,det,map)	-> fillModule(mod,det,6, "303-AC4", Mod303Key.CT_S47U, Mod303Key.CT_S47F, Mod303Key.CT_S47D, Mod303Key.CT_S47R,map))
		,CAC4_C   ("303-AC4C" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S417, det.getAmount()))
		,CAC4_D   ("303-AC4D" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S418, det.getAmount()))
		,CAC4_Z   ("303-AC4Z" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S419, det.getAmount()))
		,CAC4_ZA  ("303-AC4ZA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S4X1, det.getAmount()))
		,CAC4_ZD  ("303-AC4ZD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S4X2, det.getAmount()))
		,CAC4_E   ("303-AC4E" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S420, det.getAmount()))
		,CAC4_F   ("303-AC4F" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S421, det.getAmount()))
		,CAC4_G   ("303-AC4G" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S422, det.getAmount()))
		,CAC4_H   ("303-AC4H" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S423, det.getAmount()))
		,CAC4_HA  ("303-AC4HA", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S4X1, det.getAmount()))
		,CAC4_HD  ("303-AC4HD", (mod,det,map)	-> map.putAmount(Mod303Key.CT_S4X2, det.getAmount()))
		,CAC4_I   ("303-AC4I" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S424, det.getAmount()))
		,CAC4_J   ("303-AC4J" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S425, det.getAmount()))
		,CAC4_K   ("303-AC4K" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S426, det.getAmount()))
		,CAC4_L   ("303-AC4L" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S427, det.getAmount()))
		,CAC4_M   ("303-AC4M" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S428, det.getAmount()))

		,C47      ("303-47" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S47, det.getAmount()))
		,C48      ("303-48" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S48, det.getAmount()))
		,C49      ("303-49" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S49, det.getAmount()))
		,C50      ("303-50" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S50, det.getAmount()))
		,C51      ("303-51" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S51, det.getAmount()))
		,C52      ("303-52" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S52, det.getAmount()))
		,C53      ("303-53" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S53, det.getAmount()))
		,C54      ("303-54" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S54, det.getAmount()))
		,C55      ("303-55" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S55, det.getAmount()))
		,C56      ("303-56" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S56, det.getAmount()))
		,C57      ("303-57" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S57, det.getAmount()))
		,C58      ("303-58" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_S58, det.getAmount()))

 

		,C59      ("303-59" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C59, det.getAmount()))
		,C60      ("303-60" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C60, det.getAmount()))
		,C61      ("303-61" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C61, det.getAmount()))
		,C62      ("303-62" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C62, det.getAmount()))
		,C63      ("303-63" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C63, det.getAmount()))
		,C74      ("303-74" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C74, det.getAmount()))
		,C75      ("303-75" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C75, det.getAmount()))
		,C64      ("303-64" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C64, det.getAmount()))
		,C65      ("303-65" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C65, det.getAmount()))
		,C66      ("303-66" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C66, det.getAmount()))
		,C67      ("303-67" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C67, det.getAmount()))
		,C68      ("303-68" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C68, det.getAmount()))
		,C69      ("303-69" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C69, det.getAmount()))
		,C70      ("303-70" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C70, det.getAmount()))
		,C71      ("303-71" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C71, det.getAmount()))

		,IAC_01	 ("303-IAC01", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U1C, det.getDescription()))
		,IAE_01	 ("303-IAE01", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U1E, det.getDescription()))
		,IAD_01	 ("303-IAD01", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U1D, det.getDescription()))
		
		,IAC_02	 ("303-IAC02", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U2C, det.getDescription()))
		,IAE_02	 ("303-IAE02", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U2E, det.getDescription()))
		,IAD_02	 ("303-IAD02", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U2D, det.getDescription()))
		
		,IAC_03	 ("303-IAC03", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U3C, det.getDescription()))
		,IAE_03	 ("303-IAE03", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U3E, det.getDescription()))
		,IAD_03	 ("303-IAD03", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U3D, det.getDescription()))
		
		,IAC_04	 ("303-IAC04", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U4C, det.getDescription()))
		,IAE_04	 ("303-IAE04", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U4E, det.getDescription()))
		,IAD_04	 ("303-IAD04", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U4D, det.getDescription()))
		
		,IAC_05	 ("303-IAC05", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U5C, det.getDescription()))
		,IAE_05	 ("303-IAE05", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U5E, det.getDescription()))
		,IAD_05	 ("303-IAD05", (mod,det,map)	-> map.putDescription(Mod303Key.CT_U5D, det.getDescription()))
		
		
		,C80      ("303-80" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C80, det.getAmount()))
		,C81      ("303-81" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C81, det.getAmount()))
		,C82      ("303-82" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C82, det.getAmount()))
		,C83      ("303-83" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C83, det.getAmount()))
		,C84      ("303-84" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C84, det.getAmount()))
		,C85      ("303-85" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C85, det.getAmount()))
		,C86      ("303-86" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C86, det.getAmount()))
		,C87      ("303-87" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C87, det.getAmount()))
		,C88      ("303-88" , (mod,det,map)	-> map.putAmount(Mod303Key.CT_C88, det.getAmount()))
		,D		  ("303-D"  , (mod,det,map)	-> map.putAmount(Mod303Key.CT_U13, det.getAmount()))
		,PBK  	  ("303-PBK", (mod,det,map)	-> map.putDescription(Mod303Key.CM_004, det.getAmount()<0
				?FiscalModelDeclarationType.PAYBACK.getValue()
				:""))
/*		

???	,CT_U14("303-CTU14",false,null,"Exonerado de presentar el modelo 390 y con volumen de operaciones cero.")

*/			
			;
		
		private String value;
		private IMod303KeyConverter converter;
		
		private OldMod303Key(String value) {
			this(value,null);
		}
		private OldMod303Key(String value,IMod303KeyConverter converter) {
			this.value = value;
			this.converter = converter;
		}
		
		private String getValue() {
			return value;
		}
		private void convert(Mod303 mod, FiscalModelDetail det, Mod303 map) {
			if (this.converter != null) converter.convert(mod, det, map);
		}
	    private static OldMod303Key getKeyWithValue( String value ) {
	    	for (OldMod303Key key : OldMod303Key.values() ) {
	    		if (AonStringUtils.equals(value, key.getValue())) {
	    			return key;
	    		}
	    	}
	    	return null;
	    }
	}
	private static void transformToNewMap(Mod303 mod303) {
		Mod303 map = new Mod303();
		Iterator<FiscalModelDetail> iter = mod303.getMap().values().iterator(); 
		while (iter.hasNext()) {
			FiscalModelDetail det = iter.next();
			OldMod303Key oldKey = OldMod303Key.getKeyWithValue(det.getType());
			if (oldKey != null) {
				oldKey.convert(mod303, det, map);
			}
		}
		mod303.setMap(map.getMap());
		mod303.putAmount(Mod303Key.CT_A02, 0); // Solo regimen simplificado.
	}
	
	private static void fillModule(Mod303 mod,FiscalModelDetail det,int idx, String epiKey,Mod303Key unitKey, Mod303Key factorKey,Mod303Key descKey, Mod303Key resultKey,Mod303 map) {
		String epigraph = AonStringUtils.trim(AonStringUtils.substringBefore( mod.getDescription(epiKey) , "-"));
		Epigraph epi = Modules2016.Epigraph.getEpigraph(epigraph);
		if (epi != null) {
			Module module = epi.getVATModules()[idx];
			map.putDescription	(unitKey, module.getUnit() );
			map.putAmount		(factorKey, module.getAmount() );
			map.putDescription	(descKey, module.getKey().getDescription() );
		}
		map.putAmount(resultKey, det.getAmount() );
	}
}
