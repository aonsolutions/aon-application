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
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
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
		void initialize(AONContext ctx,Mod115 mod,Set<String> docs,IrpfBreakdown br);
	}
	
	private static void addPerceptor(Mod115Key key,Mod115 mod,Set<String> docs,IrpfBreakdown br) {
		if (!docs.contains(br.getDocument())) {
			docs.add(br.getDocument());
			mod.ensureDetail(key).addAmount(1);
		}
	}

	private static enum Mod115KeyDAO {
		// *************************************************************************
		// *************************************************************** ALAVA ***
		// *************************************************************************
		 AR_907(Mod115Key.AR_907, (mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null)
		,AR_908(Mod115Key.AR_908, (mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null)
		,AR_909(Mod115Key.AR_909, (mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null)
		,AR_C01(Mod115Key.AR_C01
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> addPerceptor(Mod115Key.AR_C01,mod,docs,br)
			,null)
		,AR_C02(Mod115Key.AR_C02
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod115Key.AR_C02).addAmount(br.getBase())
			,null)
		,AR_C03(Mod115Key.AR_C03
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod115Key.AR_C03).addAmount(br.getQuota())
			,null)
		,AR_C04(Mod115Key.AR_C04, (mod -> mod.isAraba()),null,null,null)
		,AR_C05(Mod115Key.AR_C05, (mod -> mod.isAraba()),null,null,null)
		,AR_C06(Mod115Key.AR_C06, (mod -> mod.isAraba()),null,null,null)
		,AR_C07(Mod115Key.AR_C07, (mod -> mod.isAraba()),null,null,"AR_C03+AR_C06")
		,AR_C08(Mod115Key.AR_C08, (mod -> mod.isAraba()),null,null,null)
		,AR_C09(Mod115Key.AR_C09, (mod -> mod.isAraba()),null,null,null)
		,AR_C10(Mod115Key.AR_C10, (mod -> mod.isAraba()),null,null,null)
		,AR_C11(Mod115Key.AR_C11, (mod -> mod.isAraba()),null,null,"AR_C07-AR_C08+AR_C09+AR_C10")
		,AR_TIP(Mod115Key.AR_TIP
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null,null)
		// *************************************************************************
		// ************************************************************* BIZKAIA ***
		// *************************************************************************
		,BZ_C01(Mod115Key.BZ_C01
			, (mod -> mod.isBizkaia())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> addPerceptor(Mod115Key.BZ_C01,mod,docs,br)
			,null)
		,BZ_C02(Mod115Key.BZ_C02
			, (mod -> mod.isBizkaia())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod115Key.BZ_C02).addAmount(br.getBase())
			,null)
		,BZ_C03(Mod115Key.BZ_C03
			, (mod -> mod.isBizkaia())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod115Key.BZ_C03).addAmount(br.getQuota())
			,null)
		,BZ_C04(Mod115Key.BZ_C04, (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C05(Mod115Key.BZ_C04, (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C06(Mod115Key.BZ_C04, (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C07(Mod115Key.BZ_C07, (mod -> mod.isBizkaia()),null,null,"BZ_C03+BZ_C06")
		,BZ_TIP (Mod115Key.BZ_TIP , (mod -> mod.isBizkaia()), null,null,null)
		
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// *************************************************************************
		,CT_C01(Mod115Key.CT_C01
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> addPerceptor(Mod115Key.CT_C01,mod,docs,br)
			,null)
		,CT_C02(Mod115Key.CT_C02
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod115Key.CT_C02).addAmount(br.getBase())
			,null)
		,CT_C03(Mod115Key.CT_C03
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod115Key.CT_C03).addAmount(br.getQuota())
			,null)
		,CT_C04(Mod115Key.CT_C04, (mod -> mod.isAEAT()),null,null,null)
		,CT_C05(Mod115Key.CT_C05
			, (mod -> mod.isAEAT())
			, null,null, "CT_C03-CT_C04" )
		,CT_TIP (Mod115Key.CT_TIP , (mod -> mod.isAEAT()), null,null,null)
		
		// *************************************************************************
		// ************************************************************ GIPUZKOA ***
		// *************************************************************************
		,GP_C01(Mod115Key.GP_C01
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> addPerceptor(Mod115Key.GP_C01,mod,docs,br)
			,null)
		,GP_C02(Mod115Key.GP_C02
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod115Key.GP_C02).addAmount(br.getBase())
			,null)
		,GP_C03(Mod115Key.GP_C03
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isRenting()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod115Key.GP_C03).addAmount(br.getQuota())
			,null)
		,GP_C04(Mod115Key.GP_C04, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C05(Mod115Key.GP_C04, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C06(Mod115Key.GP_C04, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C07(Mod115Key.GP_C07, (mod -> mod.isGipuzkoa()),null,null,"GP_C03+GP_C07")
		,GP_TIP (Mod115Key.GP_TIP , (mod -> mod.isGipuzkoa()), null,null,null)
		
		// *************************************************************************
		// ************************************************************* NAVARRA ***
		// *************************************************************************
		,NF_A1(Mod115Key.NF_C01
			, (mod -> mod.isNavarra())
			, (mod,br) -> (br.isRenting()) 
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod115Key.NF_C01).addAmount(br.getQuota())
			,null)
		,NF_TIP (Mod115Key.NF_TIP , (mod -> mod.isNavarra()), null,null,null)
		;
		
		private Mod115Key key;
		private IModelAccepter acceptModel;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private String expression;

		private Mod115KeyDAO(Mod115Key key, IModelAccepter acceptModel, IValueAccepter acceptValue
				,IValueIntializer initializer,String expression) {
			this.key = key;
			this.acceptModel =  acceptModel;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.expression =  expression;
		}
		
		
		public Mod115Key getKey() {
			return key;
		}
		public boolean acceptModel(Mod115 mod) {
			return  (acceptModel.accept(mod));
		}
		public boolean acceptValue(Mod115 mod,IrpfBreakdown  br) {
			return  acceptValue != null && acceptModel(mod) &&  acceptValue.accept(mod,br);
		}
		public void initialize(AONContext ctx,Mod115 mod,Set<String> docs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, br);
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
		}
		initializeFiscalModel(ctx, mod115);
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
		final Set<String> docs = new HashSet<String>();
		IRPFDAO.getInvoiceIrpfBreakdown(ctx, mod115)
				.forEach(br -> {
						for (Mod115KeyDAO key : Mod115KeyDAO.values()) {
							if (key.acceptValue(mod115,br)) {
								key.initialize(ctx, mod115, docs, br);
							};
						}
				});
	}
	
	private static String getInvoicesInfo(AONContext ctx, final Mod115 mod115
			, final IModelScript<Mod115Key> script, Mod115KeyDAO keyDAO) {
		
		String title = "FACTURAS CON RETENCIONES QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ mod115.getModelName() 
				+ " DEL " + mod115.getPeriod().getDescription()
				+ " DE " + mod115.getYear();
		return IRPFFormatter.formatInvoices(title,script.getLabel()
			,IRPFDAO.getInvoiceIrpfBreakdown(ctx, mod115)
					.filter( br ->  keyDAO.acceptValue(mod115, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod115 mod115
			, final IModelScript<Mod115Key> script, Mod115KeyDAO keyDAO) {
		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
			+ mod115.getModelName() 
			+ " DEL " + mod115.getPeriod().getDescription()
			+ " DE " + mod115.getYear();
		return IRPFFormatter.formatDiffInvoices(title
			,script.getLabel()
			,script.getKeys()
			, getPreviousModels(ctx,mod115)
			 	.collect(Collectors.toCollection(LinkedList::new))	
			,IRPFDAO.getInvoiceDiffIrpfBreakdown(ctx, mod115)
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
	public static Mod115 finish(AONContext ctx,Mod115 mod115) {
		mod115 = FiscalModelDAO.finish(ctx, mod115);
		return saveMod115(ctx, mod115);
	}
	
	public static Mod115 reopen(AONContext ctx,Mod115 mod115) {
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


}

