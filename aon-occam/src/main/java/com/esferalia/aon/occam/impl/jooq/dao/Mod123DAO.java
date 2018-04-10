package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod123DAO extends FiscalModelDAO {
	
	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod123 mod,IModelScript<Mod123Key> script,Mod123KeyDAO keyDAO);
	}
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private static enum Mod123KeyInfoDAO {
		 NONE   ( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO))))
		,DIFF_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO))))
		,COMPUTE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getExpression(mod, script,keyDAO))))
		;
		private IModelInfoProvider provider;
		
		private Mod123KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod123 mod,IModelScript<Mod123Key> script,Mod123KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, script,keyDAO);
		}
	}
	
	@FunctionalInterface
	private static interface IModelAccepter {
		boolean accept(Mod123 mod);
	}
	@FunctionalInterface
	public static interface IValueAccepter {
		boolean accept(Mod123 mod,IrpfBreakdown rc);
	}
	@FunctionalInterface
	public static interface IValueIntializer {
		void initialize(AONContext ctx,Mod123 mod,Set<String> docs,IrpfBreakdown br);
	}
	
	private static void addPerceptor(Mod123Key key,Mod123 mod,Set<String> docs,IrpfBreakdown br) {
		if (!docs.contains(br.getRegistryDocument())) {
			docs.add(br.getRegistryDocument());
			mod.ensureDetail(key).addAmount(1);
		}
	}
	
	private static void addBase(Mod123Key key,Mod123 mod,IrpfBreakdown br) {
		Mod123KeyDAO keyDAO = Mod123KeyDAO.safeValueOf(mod, key.getValue());
		if (keyDAO.isDiffEnabled()) {
			mod.ensureDetail(key).addAccumulatedAmount(br.getBase());	
		} else {
			mod.ensureDetail(key).addAmount(br.getBase());
		}
	}
	
	private static void addQuota(Mod123Key key,Mod123 mod,IrpfBreakdown br) {
		Mod123KeyDAO keyDAO = Mod123KeyDAO.safeValueOf(mod, key.getValue());
		if (keyDAO.isDiffEnabled()) {
			mod.ensureDetail(key).addAccumulatedAmount(br.getQuota());	
		} else {
			mod.ensureDetail(key).addAmount(br.getQuota());
		}
	}

	private static enum Mod123KeyDAO {
		// *************************************************************************
		// *************************************************************** ALAVA ***
		// *************************************************************************
		 AR_907(Mod123Key.AR_907, false, (mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null)
		,AR_908(Mod123Key.AR_908, false, (mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null)
		,AR_909(Mod123Key.AR_909, false, (mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null)
		,AR_C01(Mod123Key.AR_C01, false
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addPerceptor(Mod123Key.AR_C01,mod,docs,br)
			,null)
		,AR_C02(Mod123Key.AR_C02, true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addBase(Mod123Key.AR_C02,mod,br)
			,null)
		,AR_C03(Mod123Key.AR_C03, true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addQuota(Mod123Key.AR_C03,mod,br)
			,null)
		,AR_C04(Mod123Key.AR_C04, false, (mod -> mod.isAraba()),null,null,null)
		,AR_C05(Mod123Key.AR_C05, false, (mod -> mod.isAraba()),null,null,null)
		,AR_C06(Mod123Key.AR_C06, false, (mod -> mod.isAraba()),null,null,"AR_C03+AR_C05")
		,AR_C07(Mod123Key.AR_C07, false, (mod -> mod.isAraba()),null,null,null)
		,AR_C08(Mod123Key.AR_C08, false, (mod -> mod.isAraba()),null,null,null)
		,AR_C09(Mod123Key.AR_C09, false, (mod -> mod.isAraba()),null,null,null)
		,AR_C10(Mod123Key.AR_C10, false, (mod -> mod.isAraba()),null,null,"AR_C06-AR_C07+AR_C08+AR_C09")
		,AR_TIP(Mod123Key.AR_TIP, false
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null,null)
		// *************************************************************************
		// ************************************************************* BIZKAIA ***
		// *************************************************************************
		,BZ_C01(Mod123Key.BZ_C01, false
			, (mod -> mod.isBizkaia())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addPerceptor(Mod123Key.BZ_C01,mod,docs,br)
			,null)
		,BZ_C02(Mod123Key.BZ_C02, true
			, (mod -> mod.isBizkaia())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addBase(Mod123Key.BZ_C02,mod,br)
			,null)
		,BZ_C03(Mod123Key.BZ_C03, true
			, (mod -> mod.isBizkaia())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addQuota(Mod123Key.BZ_C03,mod,br)
			,null)
		,BZ_C04(Mod123Key.BZ_C04, false, (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C05(Mod123Key.BZ_C05, false, (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C06(Mod123Key.BZ_C06, false, (mod -> mod.isBizkaia()),null,null,"BZ_C03+BZ_C05")
		,BZ_TIP (Mod123Key.BZ_TIP, false , (mod -> mod.isBizkaia()), null,null,null)
		
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// *************************************************************************
		,CT_C01(Mod123Key.CT_C01, false
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addPerceptor(Mod123Key.CT_C01,mod,docs,br)
			,null)
		,CT_C02(Mod123Key.CT_C02, true
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addBase(Mod123Key.CT_C02,mod,br)
			,null)
		,CT_C03(Mod123Key.CT_C03, true
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addQuota(Mod123Key.CT_C03,mod,br)
			,null)
		,CT_C04(Mod123Key.CT_C04, false, (mod -> mod.isAEAT()),null,null,null)
		,CT_C05(Mod123Key.CT_C05, false, (mod -> mod.isAEAT()),null,null,null)
		,CT_C06(Mod123Key.CT_C06, false, (mod -> mod.isAEAT()),null,null, "CT_C03+CT_C05" )
		,CT_C07(Mod123Key.CT_C07, false, (mod -> mod.isAEAT()),null,null,null)
		,CT_C08(Mod123Key.CT_C08, false, (mod -> mod.isAEAT()),null,null, "CT_C06-CT_C07" )
		,CT_TIP (Mod123Key.CT_TIP, false , (mod -> mod.isAEAT()), null,null,null)
		
		// *************************************************************************
		// ************************************************************ GIPUZKOA ***
		// *************************************************************************
		,GP_C01(Mod123Key.GP_C01, false
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addPerceptor(Mod123Key.GP_C01,mod,docs,br)
			,null)
		,GP_C02(Mod123Key.GP_C02, true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addBase(Mod123Key.GP_C02,mod,br)
			,null)
		,GP_C03(Mod123Key.GP_C03, true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isMovableCapital()
			, (ctx,mod,docs,br) -> addQuota(Mod123Key.GP_C03,mod,br)
			,null)
		,GP_C04(Mod123Key.GP_C04, false, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C05(Mod123Key.GP_C05, false, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C06(Mod123Key.GP_C06, false, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C07(Mod123Key.GP_C07, false, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C08(Mod123Key.GP_C08, false, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C09(Mod123Key.GP_C09, false, (mod -> mod.isGipuzkoa()),null,null,"GP_C03+GP_C06+GP_C08")
		,GP_TIP (Mod123Key.GP_TIP, false , (mod -> mod.isGipuzkoa()), null,null,null)
		
		// *************************************************************************
		// ************************************************************* NAVARRA ***
		// *************************************************************************
		,NF_A1(Mod123Key.NF_C01, true
			, (mod -> mod.isNavarra())
			, (mod,br) -> (br.isMovableCapital()) 
			, (ctx,mod,docs,br) -> addQuota(Mod123Key.NF_C01,mod,br)
			,null)
		,NF_TIP (Mod123Key.NF_TIP, false , (mod -> mod.isNavarra()), null,null,null)
		;
		
		private Mod123Key key;
		private boolean diffEnabled;
		private IModelAccepter acceptModel;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private String expression;

		private Mod123KeyDAO(Mod123Key key, boolean diffEnabled, IModelAccepter acceptModel, IValueAccepter acceptValue
				,IValueIntializer initializer,String expression) {
			this.key = key;
			this.acceptModel =  acceptModel;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.expression =  expression;
			this.diffEnabled = diffEnabled;
		}
		
		
		public Mod123Key getKey() {
			return key;
		}
		public boolean isDiffEnabled() {
			return diffEnabled;
		}
		public boolean acceptModel(Mod123 mod) {
			return  (acceptModel.accept(mod));
		}
		public boolean acceptValue(Mod123 mod,IrpfBreakdown  br) {
			return  acceptValue != null && acceptModel(mod) &&  acceptValue.accept(mod,br);
		}
		public void initialize(AONContext ctx,Mod123 mod,Set<String> docs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, br);
			}
		}
		public String getExpression() {
			return expression;
		}
		public static Mod123KeyDAO safeValueOf(Mod123 mod, String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod123KeyDAO keyDAO : Mod123KeyDAO.values()) {	
				if (keyDAO.acceptModel(mod) && keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}
	}

	public static Stream<Mod123> getMod123s(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M123)
				.map( record -> map123(new Mod123(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	
	public static Mod123 getMod123(AONContext ctx,int id) {
		ctx.checkRead();
		final Mod123 mod123 = getModelRecord(ctx, id)
				.map( record -> map123(new Mod123(),record));
		if (mod123 != null) {
			getModelDetails(ctx,mod123).forEach( detail -> mod123.put( detail));	
		}
		return mod123;
		
	}
	
	public static Mod123 saveMod123(AONContext ctx, Mod123 mod123) {
		calculateMod123(ctx, mod123);
		FiscalModel fm = save(ctx, mod123);
		return getMod123(ctx, fm.getId());
	}
	
	public static Mod123 saveCommentsMod123(AONContext ctx, Mod123 mod123) {
		saveComments(ctx, mod123);
		return mod123;
	}

	public static Mod123 calculateMod123(AONContext ctx, Mod123 mod123) {
		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>();
		for (String key : mod123.getMap().keySet()) {
			Mod123Key mod123Key = Mod123Key.getKey(key,mod123.getAdministration());
			if (mod123Key != null) {
				FiscalModelDetail detail = mod123.getMap().get(key);
				mvelCtx.put(mod123Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		for (Mod123KeyDAO key : Mod123KeyDAO.values()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) && key.acceptModel(mod123)) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod123.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod123; 
	}
	
	public static Mod123 initializeMod123(AONContext ctx,Mod123 mod123) {
		if (mod123 == null) {
			mod123 = new Mod123();
			mod123.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod123);
		
		// Cálculo por diferencia.
		String diff = AppParamDAO.fetchValue(ctx, AppParam.FS_MOD303_BY_DIFFERENCE_DISABLED);
		mod123.setDiffCalculationDisabled(AonStringUtils.equals(diff, AonStringUtils.ONE));
				
		return mod123;
	}
	
	public static Mod123 createMod123(AONContext ctx,Mod123 mod123) {
		for (Mod123KeyDAO key : Mod123KeyDAO.values()) {
			if (key.acceptModel(mod123)) {
				FiscalModelDetail detail = mod123.ensureDetail(key.getKey());
				detail.setExpression(key.getExpression());
			}
		}
		
		createFromInvoices(ctx,mod123);
		
		for (FiscalModelDetail detail : mod123.getMap().values()) {
			Mod123KeyDAO key = Mod123KeyDAO.safeValueOf(mod123, detail.getType());
			if (key != null && key.isDiffEnabled()) {
				detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));	
				detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
			}
		}
		return calculateMod123(ctx, mod123);
	}

	public static String getMod123Info(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) {
		Mod123KeyInfoDAO k = Mod123KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod123KeyDAO keyDAO : Mod123KeyDAO.values()) {
			if (keyDAO.getKey() == script.getKeys()[0]) {
				return k.getInfo(ctx, mod123, script, keyDAO);
			}
		}
		return null; 
	}
	
	// -------------------------------------------------------------------- INVOICES
	private static void createFromInvoices(final AONContext ctx, final Mod123 mod123) {
		final Set<String> docs = new HashSet<String>();
		if (mod123.isDiffCalculationDisabled()) {
			// Cálculo por diferencias NO 
			// Es como se estaba calculando hasta ahora, solo se leía el periodo que se declara
			IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod123)
			.forEach(br -> {
					for (Mod123KeyDAO key : Mod123KeyDAO.values()) {
						if (key.acceptValue(mod123,br)) {
							key.initialize(ctx, mod123, docs, br);
						};
					}
			});			
		}
		else {
			// Cálculo por diferencias SI
			IRPFDAO.getInputInvoicesDiffIrpfBreakdown(ctx, mod123)
					.forEach(br -> {
							for (Mod123KeyDAO key : Mod123KeyDAO.values()) {
								if (key.acceptValue(mod123,br)) {
									key.initialize(ctx, mod123, docs, br);
								};
							}
					});
			// Calcular lo declarado hasta ahora
			getMod123EffectivePreviousModels(ctx, mod123)
			.forEach(mod -> {
				for (String keyString : mod.getMap().keySet()) {
					double amount = mod.getAmount(keyString);
					mod123.ensureDetail(keyString).addDeclaredAmount(amount);
				}
			});
			
		}
	}
	
	private static String getInvoicesInfo(AONContext ctx, final Mod123 mod123
			, final IModelScript<Mod123Key> script, Mod123KeyDAO keyDAO) {
		
		String title = "FACTURAS CON RETENCIONES QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod123)
				+ " DEL " + mod123.getPeriod().getDescription()
				+ " DE " + mod123.getYear();
		return IRPFFormatter.formatInvoices(title,script.getLabel()
			,IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod123)
					.filter( br ->  keyDAO.acceptValue(mod123, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod123 mod123
			, final IModelScript<Mod123Key> script, Mod123KeyDAO keyDAO) {
		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
			+ FiscalModelUtils.getModelName(mod123)
			+ " DEL " + mod123.getPeriod().getDescription()
			+ " DE " + mod123.getYear();
		return IRPFFormatter.formatDiffInvoices(title
			,script.getLabel()
			,script.getKeys()
			//, getPreviousModels(ctx,mod123)
			,getMod123EffectivePreviousModels(ctx, mod123)
			 	.collect(Collectors.toCollection(LinkedList::new))	
			,IRPFDAO.getInputInvoicesDiffIrpfBreakdown(ctx, mod123)
				.filter( br ->  keyDAO.acceptValue(mod123, br) )	
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}

	private static String getExpression(Mod123 mod123
			, IModelScript<Mod123Key> script, Mod123KeyDAO keyDAO0) {
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
				Mod123KeyDAO keyDAO = Mod123KeyDAO.valueOf(key.toString());
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

		for (String keyValue : mod123.getMap().keySet()) {
			Mod123Key mod123Key = Mod123Key.getKey(keyValue, mod123.getAdministration());
			if (mod123Key != null) {
				FiscalModelDetail detail = mod123.getMap().get(keyValue);
				mvelCtx.put(mod123Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}


		for (Mod123Key key : script.getKeys() ) {
			Mod123KeyDAO keyDAO = Mod123KeyDAO.safeValueOf(mod123, key.getValue());
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
		return buf.toString();
	}

	public static Mod123 markAsFinished(AONContext ctx,Mod123 mod123) {
		mod123 = FiscalModelDAO.finish(ctx, mod123);
		return saveMod123(ctx, mod123);
	}
	
	public static Mod123 markAsPending(AONContext ctx,Mod123 mod123) {
		mod123.setDeclarationType((String) null);
		mod123.setStatus(FiscalStatus.PENDING);
		Finance finance = mod123.getFinance();
		mod123.setFinance(null);
		mod123 = saveMod123(ctx, mod123);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
		}
		return mod123;
	}

	public static Mod123 markAsSent(AONContext ctx,Mod123 mod123) {
		mod123.setStatus(FiscalStatus.SENT);
		mod123= saveMod123(ctx, mod123);
		return mod123;
	}

	private static Stream<FiscalModel> getMod123EffectivePreviousModels(AONContext ctx,FiscalModel fiscalModel) {
		if (fiscalModel.isBizkaia()) {
			LinkedList<FiscalModel> previousModels = getPreviousModels(ctx, fiscalModel) .collect(Collectors.toCollection(LinkedList::new));
			if (fiscalModel.isComplementary()) { 
				getSamePeriodModels(ctx, fiscalModel)
					.forEach(fm ->  previousModels.add(fm));
			}
			return previousModels.stream(); 
		} 
		return getEffectivePreviousModels(ctx,fiscalModel);
	}
}

