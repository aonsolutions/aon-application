package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod115DAO extends FiscalModelDAO {
	
	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod115 mod,IModelScript<Mod115Key> script,Mod115KeyDAO keyDAO);
	}
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private static enum Mod115KeyInfoDAO {
		 NONE   ( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO))))
		,DIFF_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO))))
		,COMPUTE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getExpression(mod, script,keyDAO))))
		;
		private IModelInfoProvider provider;
		
		private Mod115KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod115 mod,IModelScript<Mod115Key> script,Mod115KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, script,keyDAO);
		}
	}
	
	@FunctionalInterface
	private static interface IModelAccepter {
		boolean accept(Mod115 mod);
	}
	@FunctionalInterface
	public static interface IValueAccepter {
		boolean accept(Mod115 mod,IrpfBreakdown rc);
	}
	@FunctionalInterface
	public static interface IValueIntializer {
		void initialize(AONContext ctx,Mod115 mod
				,Map<Mod115Key,Set<String>> docs
				,Map<Mod115Key,Set<String>> pdocs
				,IrpfBreakdown br);
	}
	
	@FunctionalInterface
	public static interface IValueUniqueIntializer {
		void initialize(AONContext ctx,Mod115 mod);
	}
	
	private static void addDeponentDocument(AONContext ctx, Mod115 mod) {
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		if (!domain.isStandalone()) {
			Company parentCompany = CompanyDAO.getCompany(ctx, domain.getParentId());
			if (parentCompany != null) {
				mod.ensureDetail(Mod115Key.GP_X00).setDescription(parentCompany.getDocument());	
			}
		}
	}

	private static void addPerceptor(Mod115Key key
			,Mod115 mod
			,Map<Mod115Key,Set<String>> docs
			,Map<Mod115Key,Set<String>> pdocs
			,IrpfBreakdown br) {
		// Se suman todos los perceptores (acumulado).
		if (!docs.containsKey(key)) {
			docs.put(key, new HashSet<String>());
		}
		if (!docs.get(key).contains(br.getRegistryDocument())) {
			docs.get(key).add(br.getRegistryDocument());
			mod.ensureDetail(key).addAccumulatedAmount(1);
		}
		// Se suman los perceptores del periodo que se esta haciendo.
		if (FiscalUtils.isInPeriodRange(mod, br.getTaxDate())) {
			if (!pdocs.containsKey(key)) {
				pdocs.put(key, new HashSet<String>());
			}
			if (!pdocs.get(key).contains(br.getRegistryDocument())) {
				pdocs.get(key).add(br.getRegistryDocument());
				mod.ensureDetail(key).addAmount(1);
			}
		}
	}

	private static void addBase(Mod115Key key,Mod115 mod,IrpfBreakdown br) {
		Mod115KeyDAO keyDAO = Mod115KeyDAO.safeValueOf(mod, key.getValue());
		if (keyDAO.isDiffEnabled()) {
			mod.ensureDetail(key).addAccumulatedAmount(br.getBase());	
		} else {
			mod.ensureDetail(key).addAmount(br.getBase());
		}
	}
	private static void addQuota(Mod115Key key,Mod115 mod,IrpfBreakdown br) {
		Mod115KeyDAO keyDAO = Mod115KeyDAO.safeValueOf(mod, key.getValue());
		if (keyDAO.isDiffEnabled()) {
			mod.ensureDetail(key).addAccumulatedAmount(br.getQuota());	
		} else {
			mod.ensureDetail(key).addAmount(br.getQuota());
		}
	}
	

	private static enum Mod115KeyDAO {
		// *************************************************************************
		// *************************************************************** ALAVA ***
		// *************************************************************************
		 AR_907(Mod115Key.AR_907, false,(mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null,null)
		,AR_908(Mod115Key.AR_908, false,(mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null,null)
		,AR_909(Mod115Key.AR_909, false,(mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null,null)
		,AR_C01(Mod115Key.AR_C01, false
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod115Key.AR_C01,mod,docs,pdocs,br)
			,null,null)
		,AR_C02(Mod115Key.AR_C02, true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod115Key.AR_C02,mod,br)
			,null,null)
		,AR_C03(Mod115Key.AR_C03, true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod115Key.AR_C03, mod, br)
			,null,null)
		,AR_C04(Mod115Key.AR_C04, false,(mod -> mod.isAraba()),null,null,null,null)
		,AR_C05(Mod115Key.AR_C05, false,(mod -> mod.isAraba()),null,null,null,null)
		,AR_C06(Mod115Key.AR_C06, false,(mod -> mod.isAraba()),null,null,null,null)
		,AR_C07(Mod115Key.AR_C07, false,(mod -> mod.isAraba()),null,null,null,"AR_C03+AR_C06")
		,AR_C08(Mod115Key.AR_C08, false,(mod -> mod.isAraba()),null,null,null,null)
		,AR_C09(Mod115Key.AR_C09, false,(mod -> mod.isAraba()),null,null,null,null)
		,AR_C10(Mod115Key.AR_C10, false,(mod -> mod.isAraba()),null,null,null,null)
		,AR_C11(Mod115Key.AR_C11, false,(mod -> mod.isAraba()),null,null,null,"AR_C07-AR_C08+AR_C09+AR_C10")
		,AR_TIP(Mod115Key.AR_TIP, false
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null,null,null)
		// *************************************************************************
		// ************************************************************* BIZKAIA ***
		// *************************************************************************
		,BZ_C01(Mod115Key.BZ_C01, false
			, (mod -> mod.isBizkaia())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod115Key.BZ_C01,mod,docs,pdocs,br)
			,null,null)
		,BZ_C02(Mod115Key.BZ_C02, true
			, (mod -> mod.isBizkaia())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod115Key.BZ_C02,mod,br)
			,null,null)
		,BZ_C03(Mod115Key.BZ_C03, true
			, (mod -> mod.isBizkaia())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod115Key.BZ_C03, mod, br)
			,null,null)
		,BZ_C04(Mod115Key.BZ_C04, false, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C05(Mod115Key.BZ_C05, false, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C06(Mod115Key.BZ_C06, false, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C07(Mod115Key.BZ_C07, false, (mod -> mod.isBizkaia()),null,null,null,"BZ_C03+BZ_C06")
		,BZ_TIP(Mod115Key.BZ_TIP, false, (mod -> mod.isBizkaia()), null,null,null,null)
		
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// *************************************************************************
		,CT_C01(Mod115Key.CT_C01, false
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod115Key.CT_C01,mod,docs,pdocs,br)
			,null,null)
		,CT_C02(Mod115Key.CT_C02, true
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod115Key.CT_C02,mod,br)
			,null,null)
		,CT_C03(Mod115Key.CT_C03, true
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod115Key.CT_C03, mod, br)
			,null,null)
		,CT_C04(Mod115Key.CT_C04, false, (mod -> mod.isAEAT()),null,null
			, (ctx,mod) -> mod.putAmount(Mod115Key.CT_C04,mod.isComplementary()
					?getSamePeriodModels(ctx, mod).mapToDouble(fm -> fm.getResult()).sum()
					:0.0)
			,null)
		,CT_C05(Mod115Key.CT_C05, false
			, (mod -> mod.isAEAT())
			, null,null,null, "CT_C03-CT_C04" )
		,CT_TIP(Mod115Key.CT_TIP, false, (mod -> mod.isAEAT()), null,null,null,null)
		
		// *************************************************************************
		// ************************************************************ GIPUZKOA ***
		// *************************************************************************
		,GP_X00 (Mod115Key.GP_X00,false
			, (mod -> mod.isGipuzkoa())	
			, null
			, null
			, (ctx,mod) -> addDeponentDocument(ctx,mod)
			, null)
		,GP_C01(Mod115Key.GP_C01, false
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod115Key.GP_C01,mod,docs,pdocs,br)
			,null,null)
		,GP_C02(Mod115Key.GP_C02, true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod115Key.GP_C02,mod,br)
			,null,null)
		,GP_C03(Mod115Key.GP_C03, true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod115Key.GP_C03,mod,br)
			,null,null)
		,GP_C04(Mod115Key.GP_C04, false, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C05(Mod115Key.GP_C05, false, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C06(Mod115Key.GP_C06, false, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C07(Mod115Key.GP_C07, false, (mod -> mod.isGipuzkoa()),null,null,null,"GP_C03+GP_C06")
		,GP_TIP(Mod115Key.GP_TIP, false,  (mod -> mod.isGipuzkoa()), null,null,null,null)
		
		// *************************************************************************
		// ************************************************************* NAVARRA ***
		// *************************************************************************
		,NF_A1(Mod115Key.NF_C01, false
			, (mod -> mod.isNavarra())
			, (mod,br) -> (br.isRenting()) 
			, (ctx,mod,docs,pdocs,br) -> mod.ensureDetail(Mod115Key.NF_C01).addAmount(br.getQuota())
			,null,null)
		,NF_TIP(Mod115Key.NF_TIP,false
			,  (mod -> mod.isNavarra()), null,null,null,null)
		;
		
		private Mod115Key key;
		private boolean diffEnabled;
		private IModelAccepter acceptModel;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueUniqueIntializer uniqueInitializer;
		private String expression;

		private Mod115KeyDAO(Mod115Key key
				,boolean diffEnabled
				,IModelAccepter acceptModel
				,IValueAccepter acceptValue
				,IValueIntializer initializer
				,IValueUniqueIntializer uniqueInitializer
				,String expression) {
			this.key = key;
			this.diffEnabled = diffEnabled;
			this.acceptModel =  acceptModel;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.uniqueInitializer = uniqueInitializer;
			this.expression =  expression;
		}
		
		
		public Mod115Key getKey() {
			return key;
		}
		public boolean isDiffEnabled() {
			return diffEnabled;
		}
		public boolean acceptModel(Mod115 mod) {
			return  (acceptModel.accept(mod));
		}
		public boolean acceptValue(Mod115 mod,IrpfBreakdown  br) {
			return  acceptValue != null && acceptModel(mod) &&  acceptValue.accept(mod,br);
		}
		
		public void initialize(AONContext ctx,Mod115 mod,Map<Mod115Key,Set<String>> docs
				,Map<Mod115Key,Set<String>> pdocs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, pdocs, br);
			}
		}
		public void uniqueInitialize(AONContext ctx,Mod115 mod) {
			if (uniqueInitializer != null) {
				uniqueInitializer.initialize(ctx, mod);
			}
		}
		public String getExpression() {
			return expression;
		}
		public static Mod115KeyDAO safeValueOf(Mod115 mod, String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod115KeyDAO keyDAO : Mod115KeyDAO.values()) {	
				if (keyDAO.acceptModel(mod) && keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}
	}

	public static Stream<Mod115> getMod115s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return getModelRecords(ctx, domain,FiscalModelType.M115,filter)
				.map( record -> map115(new Mod115(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	public static Stream<Mod115> getMod115s(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M115)
				.map( record -> map115(new Mod115(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	
	public static Mod115 getMod115(AONContext ctx,int id) {
		ctx.checkRead();
		final Mod115 mod115 = getModelRecord(ctx, id)
				.map( record -> map115(new Mod115(),record));
		if (mod115 != null) {
			getModelDetails(ctx,mod115).forEach( detail -> mod115.put( detail));	
		}
		return mod115;
		
	}
	
	public static Mod115 saveMod115(AONContext ctx, Mod115 mod115) {
		calculateMod115(ctx, mod115);
		FiscalModel fm = save(ctx, mod115);
		return getMod115(ctx, fm.getId());
	}
	
	public static Mod115 saveCommentsMod115(AONContext ctx, Mod115 mod115) {
		saveComments(ctx, mod115);
		return mod115;
	}

	public static Mod115 calculateMod115(AONContext ctx, Mod115 mod115) {
		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>();
		for (String key : mod115.getMap().keySet()) {
			Mod115Key mod115Key = Mod115Key.getKey(key,mod115.getAdministration());
			if (mod115Key != null) {
				FiscalModelDetail detail = mod115.getMap().get(key);
				mvelCtx.put(mod115Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		for (Mod115KeyDAO key : Mod115KeyDAO.values()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) && key.acceptModel(mod115)) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod115.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod115; 
	}
	
	public static Mod115 initializeMod115(AONContext ctx,Mod115 mod115) {
		if (mod115 == null) {
			mod115 = new Mod115();
			mod115.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod115);
		
		// Cálculo por diferencia.
		String diff = AppParamDAO.fetchValue(ctx, AppParam.FS_MOD303_BY_DIFFERENCE_DISABLED);
		mod115.setDiffCalculationDisabled(AonStringUtils.equals(diff, AonStringUtils.ONE));
				
		return mod115;
	}
	
	public static Mod115 createMod115(AONContext ctx,Mod115 mod115) {
		for (Mod115KeyDAO key : Mod115KeyDAO.values()) {
			if (key.acceptModel(mod115)) {
				FiscalModelDetail detail = mod115.ensureDetail(key.getKey());
				detail.setExpression(key.getExpression());
			}
		}
		createFromInvoices(ctx,mod115);
		for (FiscalModelDetail detail : mod115.getMap().values()) {
			Mod115KeyDAO key = Mod115KeyDAO.safeValueOf(mod115, detail.getType());
			if (key != null && key.isDiffEnabled()) {
				detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));	
				detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
			}
		}
		for (Mod115KeyDAO key : Mod115KeyDAO.values()) {
			if (key.acceptModel(mod115)) {
				key.uniqueInitialize(ctx, mod115);
			};
		}
		return calculateMod115(ctx, mod115);
	}

	public static String getMod115Info(AONContext ctx, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) {
		Mod115KeyInfoDAO k = Mod115KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod115KeyDAO keyDAO : Mod115KeyDAO.values()) {
			if (keyDAO.getKey() == script.getKeys()[0]) {
				return k.getInfo(ctx, mod115, script, keyDAO);
			}
		}
		return null; 
	}
	
	// -------------------------------------------------------------------- INVOICES
	private static void createFromInvoices(final AONContext ctx, final Mod115 mod115) {
		final Map<Mod115Key,Set<String>> docs = new HashMap<Mod115Key,Set<String>>(); 
		final Map<Mod115Key,Set<String>> pdocs = new HashMap<Mod115Key,Set<String>>();
		
		if (mod115.isDiffCalculationDisabled()) {
			// Cálculo por diferencias NO
			IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod115)
			.forEach(br -> {
					for (Mod115KeyDAO key : Mod115KeyDAO.values()) {
						if (key.acceptValue(mod115,br)) {
							key.initialize(ctx, mod115, docs, pdocs, br);
						};
					}
			});
		} else {
			// Cálculo por diferencias SI
			IRPFDAO.getInputInvoicesDiffIrpfBreakdown(ctx, mod115)
				.forEach(br -> {
					for (Mod115KeyDAO key : Mod115KeyDAO.values()) {
						if (key.acceptValue(mod115,br)) {
							key.initialize(ctx, mod115, docs, pdocs, br);
						};
					}
			});
			getMod115EffectivePreviousModels(ctx, mod115)
				.forEach(mod -> {
					for (String keyString : mod.getMap().keySet()) {
						double amount = mod.getAmount(keyString);
						mod115.ensureDetail(keyString).addDeclaredAmount(amount);
					}
				});
		}
	}
	
	private static String getInvoicesInfo(AONContext ctx, final Mod115 mod115
			, final IModelScript<Mod115Key> script, Mod115KeyDAO keyDAO) {
		
		String title = "FACTURAS CON RETENCIONES QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO "
				+ FiscalModelUtils.getModelName(mod115)
				+ " DEL " + mod115.getPeriod().getDescription()
				+ " DE " + mod115.getYear();
		return IRPFFormatter.formatInvoices(title,script.getLabel()
			,IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod115)
					.filter( br ->  keyDAO.acceptValue(mod115, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod115 mod115
			, final IModelScript<Mod115Key> script, Mod115KeyDAO keyDAO) {
		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
			+ FiscalModelUtils.getModelName(mod115)
			+ " DEL " + mod115.getPeriod().getDescription()
			+ " DE " + mod115.getYear();
		return IRPFFormatter.formatDiffInvoices(title
			,script.getLabel()
			,script.getKeys()
			,getMod115EffectivePreviousModels(ctx,mod115)
			 	.collect(Collectors.toCollection(LinkedList::new))	
			,IRPFDAO.getInputInvoicesDiffIrpfBreakdown(ctx, mod115)
				.filter( br ->  keyDAO.acceptValue(mod115, br) )	
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}

	private static String getExpression(Mod115 mod115
			, IModelScript<Mod115Key> script, Mod115KeyDAO keyDAO0) {
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
				Mod115KeyDAO keyDAO = Mod115KeyDAO.valueOf(key.toString());
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

		for (String keyValue : mod115.getMap().keySet()) {
			Mod115Key mod115Key = Mod115Key.getKey(keyValue, mod115.getAdministration());
			if (mod115Key != null) {
				FiscalModelDetail detail = mod115.getMap().get(keyValue);
				mvelCtx.put(mod115Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}


		for (Mod115Key key : script.getKeys() ) {
			Mod115KeyDAO keyDAO = Mod115KeyDAO.safeValueOf(mod115, key.getValue());
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


	// -------------------------------------------------------------------- UTIL
	public static Mod115 markAsFinished(AONContext ctx,Mod115 mod115) {
		mod115 = FiscalModelDAO.finish(ctx, mod115);
		return saveMod115(ctx, mod115);
	}
	
	public static Mod115 markAsPending(AONContext ctx,Mod115 mod115) {
		mod115.setDeclarationType((String) null);
		mod115.setStatus(FiscalStatus.PENDING);
		Finance finance = mod115.getFinance();
		mod115.setFinance(null);
		mod115 = saveMod115(ctx, mod115);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
		}
		return mod115;
	}

	public static Mod115 markAsSent(AONContext ctx,Mod115 mod115) {
		mod115.setStatus(FiscalStatus.SENT);
		mod115 = saveMod115(ctx, mod115);
		return mod115;
	}

	public static Mod115 markAsCustomerCheck(AONContext ctx,Mod115 mod115) {
		mod115.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod115 = saveMod115(ctx, mod115);
		return mod115;
	}

	private static Stream<FiscalModel> getMod115EffectivePreviousModels(AONContext ctx,FiscalModel fiscalModel) {
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

