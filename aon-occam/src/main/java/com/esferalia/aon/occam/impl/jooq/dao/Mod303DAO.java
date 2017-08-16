package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;

import java.text.MessageFormat;
import java.util.Date;
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

	private static Map<String, Object> getMvelContext( Mod303 mod303 ) {
		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>();
		for (String key : mod303.getMap().keySet()) {
			Mod303Key mod303Key = Mod303Key.getKey(key);
			if (mod303Key != null) {
				FiscalModelDetail detail = mod303.getMap().get(key);
				mvelCtx.put(mod303Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx; 
	}
	
	public static Mod303 calculateMod303(AONContext ctx, Mod303 mod303) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		Map<String, Object> mvelCtx = getMvelContext( mod303 );
		for (IMod303KeyDAO key : dec.getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) ) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod303.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod303; 
	}
	
	public static Mod303 initializeMod303(AONContext ctx,Mod303 mod303) {
		if (mod303 == null) {
			mod303 = new Mod303();
		}
		initializeFiscalModel(ctx, mod303);
		declarationChanged(ctx, mod303);
		
		// Registro de devolucion.
		// Cálculo por diferencia.
		
		
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
					.and(FS_MODEL_DETAIL.TYPE.eq( mod303.getProrateKey().getValue()))
					.orderBy(FS_MODEL.YEAR.desc(), FS_MODEL.PERIOD.desc())
					.fetch()
					.stream()
					.findFirst()
					.orElse(null);
			double perc = 100;
			if (percent != null) {
				perc = percent.getValue(FS_MODEL_DETAIL.AMOUNT);
			}
			mod303.ensureDetail(mod303.getProrateKey()).setAmount(perc);
		}
	}

	public static Mod303 createMod303(AONContext ctx,Mod303 mod303) {
		final Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		dec.firstInitialize(ctx, mod303);
		Mod303DAO.getVatBreakdown(ctx,mod303)
			.forEach( vat -> dec.initialize(ctx, mod303, vat) );
		
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
					if (key != null ) {
						IMod303KeyDAO keyDAO = dec.getKey(key);
						if (keyDAO != null && keyDAO.hasAccepter()) {
							FiscalModelDetail target = mod303.ensureDetail(key);
							target.setDeclaredAmount(AonMathUtils.round(target.getDeclaredAmount() + source.getAmount()));
						}
					}
				}
			});
		}

		for (FiscalModelDetail detail : mod303.getMap().values()) {
			IMod303KeyDAO key = dec.safeValueOf(mod303, detail.getType());
			if (key != null) {
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
		Map<String, Object> mvelCtx = getMvelContext( mod303 );
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
			,getAccrualBreakdown(ctx, mod303)
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
			,getAccrualBreakdown(ctx, mod303)
					.filter( br ->  !br.isSales()  )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}

	private static String getDiffInvoicesInfo(AONContext ctx, final Mod303 mod303
			, final IModelScript<Mod303Key> script, IMod303KeyDAO keyDAO) {
		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
			+ mod303.getModelName() 
			+ " DEL " + mod303.getPeriod().getDescription()
			+ " DE " + mod303.getYear();
		return VATFormatter.formatDiffInvoices(title
			,script.getLabel()
			,script.getKeys()
			,getPreviousModels(ctx,mod303)
			 	.collect(Collectors.toCollection(LinkedList::new))
			,getVatBreakdown(ctx, mod303)
				.filter( br ->  keyDAO.acceptValue(mod303, br) )	
				.collect(Collectors.toCollection(LinkedList::new))
		);
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

	public static Stream<VatContext> getAccrualBreakdown(AONContext ctx, final Mod303 mod303) {
		Date fromDate = FiscalUtils.getPeriodStart(mod303);
		Date toDate = FiscalUtils.getPeriodEnd(mod303);
		return VATDAO.getAccrualBreakdown(ctx, fromDate, toDate);
	}
	
	public static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod303 mod303) {
		return getVatBreakdown(ctx,mod303,mod303.isDiffCalculationDisabled());
	}
	
	public static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod303 mod303, boolean diffDisabled) {
		Date fromDate = diffDisabled
			?FiscalUtils.getPeriodStart(mod303)		
			:AonDateUtils.getYearFirstDay(mod303.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod303);
		return VATDAO.getVatBreakdown(ctx,fromDate,toDate);
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
}
