package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.FiscalModelFilter;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod130DAO extends FiscalModelDAO {

	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF
	
	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod130 mod,IModelScript<Mod130Key> script,Mod130KeyDAO keyDAO);
	}
	private static int LIMITE_GASTOS_DIF_JUST = 2000;
	
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private static enum Mod130KeyInfoDAO {
		 NONE   	 ( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO))
		,COMPUTE	 ( (ctx, mod, script,keyDAO) -> getCompute(ctx,mod, script))
		,COMPUTE_KEY ( (ctx, mod, script,keyDAO) -> getComputeKey(ctx,mod, script,keyDAO))
		,INVOICE	 ( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO, keyDAO==Mod130KeyDAO.C10)))
		,DIFF_INVOICE( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO, keyDAO==Mod130KeyDAO.C10)))
		,ACT_ACCOUNT ( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getAccountInfoInfo(ctx, mod, script,keyDAO) ))
		
		;
		private IModelInfoProvider provider;
		
		private Mod130KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod130 mod,IModelScript<Mod130Key> script,Mod130KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, script,keyDAO);
		}
	}
	
	@FunctionalInterface
	private static interface IModelAccepter {
		boolean accept(Mod130 mod);
	}

	@FunctionalInterface
	public static interface IValueIntializer {
		void initialize(AONContext ctx,Mod130 mod);
	}
	
	private static enum Mod130KeyDAO {
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// *************************************************************************
		 P0  (Mod130Key.P0  , (mod -> mod.isAEAT()),null,null,null)
		,P1  (Mod130Key.P1  , (mod -> mod.isAEAT()),null,null,null)
		,P2  (Mod130Key.P2  , (mod -> mod.isAEAT()),null,null,null)
		,C01 (Mod130Key.C01 , (mod -> mod.isAEAT())
			,(ctx,mod) -> mod.putAmount(Mod130Key.C01, getInitialC01(ctx,mod))
			,null
			,"<li>Desde contabilidad, saldo acreedor de las cuentas del grupo 7 desde el @{yearStartDate} al @{periodEndDate}</li>"
			+"<li>Resultado: @{RAW_C01}</li>"
			+"<li>Porcentaje de participaci\u00F3n: <b>@{P1}%</b></li>"
			+"<li>Resultado: <b>@{C01}</b></li>")
		,C02 (Mod130Key.C02 , (mod -> mod.isAEAT())
			,(ctx,mod) -> mod.putAmount(Mod130Key.C02, getInitialC02(ctx,mod))
			,null
			,"<li>Desde contabilidad, saldo deudor de las cuentas del grupo 6 desde el @{yearStartDate} al @{periodEndDate}</li>"
			+"<li>Resultado: @{RAW_C02}</li>"
			+"@if{ P0 == 1}"
				+"<li>R\u00E9gimen de determinaci\u00F3n de rendimientos: Estimaci\u00F3n directa simplificada</li>"
				+"<li>Se procede a la aplicaci\u00F3n del 5% de gastos de dif\u00EDcil justificaci\u00F3n</li>"
				+"@code{c02p=com.esferalia.aon.watson.util.AonMathUtils.round(RAW_C01-RAW_C02)}"
				+"<li>Rendimiento neto previo es igual @{RAW_C01} - @{RAW_C02} = @{c02p}</li>"
				+"@if{ c02p < 0 }"
					+"<li>Al ser el rendimiento neto previo menor que cero, no se aplican los gastos de dif\u00EDcil justificaci\u00F3n</li>"
				+"@else{}"
					+"@code{c02a=com.esferalia.aon.watson.util.AonMathUtils.round(c02p * 5 / 100)}"
					+"<li>5% de @{c02p} --> @{c02a}</li>"
					+"@if{ c02a > LMT_GDJ }"
						+"<li>Se supera el l\u00EDmite de @{LMT_GDJ} euros. Se aplica el l\u00EDmite.</li>"
						+"@code{c02a=LMT_GDJ}"
					+"@end{}"
					+"@code{c02b=com.esferalia.aon.watson.util.AonMathUtils.round(c02a + RAW_C02)}"
					+"<li>@{RAW_C02} m\u00E1s @{c02a} es igual a @{c02b}</li>"
				+"@end{}"
			+"@end{}"
			+"<li>Porcentaje de participaci\u00F3n: <b>@{P1}%</b></li>"
			+"<li>Resultado: <b>@{C02}</b></li>")
		,C03 (Mod130Key.C03 , (mod -> mod.isAEAT()),null
			,"C01 - C02"
			,"<li>@{C01} menos @{C02} igual <b>@{C03}</b></li>")
		,C04 (Mod130Key.C04 , (mod -> mod.isAEAT()),null
			,"C03>0?(C03 * 20 / 100):(0.0)"
			,"@if{ C03 >= 0}"
			 +"<li>Al ser la casilla [003] mayor que cero, el 20% de @{C03} es <b>@{C04}</b>.</li>"
			 +"@else{}"
			 +"<li>Al ser la casilla [003] menor que cero (@{C03}), el resultado es <b>cero.</b></li>"
			 +"@end{}")
		,C05 (Mod130Key.C05 , (mod -> mod.isAEAT())
			,(ctx,mod) -> mod.putAmount(Mod130Key.C05, getInitialC05(ctx,mod))
			,null
			,"<li>Trimestres anteriores:<ul style=\"padding-left: 20px;\">" 
			+"@code{c07Sum = 0.0;c16Sum = 0.0;}"
			+"@foreach{fm : previousModels}" 
				+"@code{X07 =  fm.getAmount('"+Mod130Key.C07.getValue()+"'); X16 =  fm.getAmount('"+Mod130Key.C16.getValue()+"')}"
				+"@code{c07Sum = c07Sum + X07;c16Sum = c16Sum + X16;}"
				+"<li>@{fm.getPeriod().getDescription()}			Casilla [007] --> @{X07}</li>"
				+"<li>				Casilla [016] --> @{X16}</li>"
			+"@end{}"
			+"</ul></li>"
			+"<li>Sumatorio de las casillas [007] --> @{c07Sum}</li>"
			+"<li>Sumatorio de las casillas [016] --> @{c16Sum}</li>"
			+"<li>Resultado: <b>@{C05}</b></li>")
		,C06 (Mod130Key.C06 , (mod -> mod.isAEAT())
			,(ctx,mod) -> {
				double percent = mod.getAmount(Mod130Key.P1);
				if (percent == 0) percent = 100;
				double c06 = IRPFDAO.getOutputInvoicesDiffIrpfBreakdown(ctx, mod)
					    .filter(  i -> ( !i.isFarmer() && (i.getIRPFRegime() == null || i.getIRPFRegime() == IRPFRegime.NORMAL || i.getIRPFRegime() == IRPFRegime.SIMPLIFIED) ) )					
						.mapToDouble(br -> br.getQuota())
						.sum();
				c06 = AonMathUtils.round(c06 * percent / 100 );
				mod.putAmount(Mod130Key.C06, c06 );
							}
			,null
			,null)
		,C07 (Mod130Key.C07 , (mod -> mod.isAEAT()),null
			,"C04 - C05 - C06"
			,"<li>@{C04} menos @{C05} menos @{C06} igual <b>@{C07}</b></li>")
		,C08 (Mod130Key.C08 , (mod -> mod.isAEAT())
			,(ctx,mod) -> mod.putAmount(Mod130Key.C08, getInitialC08(ctx,mod))
			,null
			,"<li>Desde contabilidad, saldo acreedor de las cuentas del grupo 7 desde el @{periodStartDate} al @{periodEndDate}</li>"
			+"<li>Actividades agr\u00EDcolas (la actividad del apunte contable debe ser agr\u00EDcola)</li>"			
			+"<li>Resultado: @{com.esferalia.aon.watson.util.AonMathUtils.round(RAW_C08)}</li>"
			+"<li>Porcentaje de participaci\u00F3n: <b>@{P1}%</b></li>"
			+"<li>Resultado: <b>@{C08}</b></li>")
		,C09 (Mod130Key.C09 , (mod -> mod.isAEAT()),null
			,"C08 * 2 / 100"
			,"<li>2% de @{C08} igual <b>@{C09}</b></li>")
		,C10 (Mod130Key.C10 , (mod -> mod.isAEAT())
			,(ctx,mod) -> mod.putAmount(Mod130Key.C10, 
					IRPFDAO.getOutputInvoicesDiffIrpfBreakdown(ctx, mod)
						.filter( i -> i.isFarmer())
						.mapToDouble(br -> br.getQuota())
						.sum())
			,null
			,null)
		,C11 (Mod130Key.C11 , (mod -> mod.isAEAT()),null
			,"C09 - C10"
			,"<li>@{C09} menos @{C10} igual <b>@{C11}</b></li>")
		,C12 (Mod130Key.C12 , (mod -> mod.isAEAT()),null
			,"(C07 + C11)<0?0.0:(C07 + C11)"
			,"@if{ (C07 + C11) >= 0}"
					+"<li>@{C07} m\u00E1s @{C11} igual <b>@{C12}</b></li>"
			+"@else{}"
					+"<li>Al ser [007] + [011] una cantidad negativa, se consigna cero"
			+"@end{}"
			+"<li>Resultado: <b>@{C12}</b></li>")
		,C131(Mod130Key.C131, (mod -> mod.isAEAT() && mod.getYear() > 2014)
			,(ctx,mod) -> mod.putAmount(Mod130Key.C131, getInitialC13(ctx,mod))
			,null
			,null)
		,C13 (Mod130Key.C13 , (mod -> mod.isAEAT() && mod.getYear() <=  2014)
			,null
			,null
			,null)
		,C14 (Mod130Key.C14 , (mod -> mod.isAEAT()),null
			,"C12 - C131"
			,"<li>@{C12} menos @{C131} igual <b>@{C14}</b></li>")
		,C15 (Mod130Key.C15 , (mod -> mod.isAEAT())
			,(ctx,mod) -> mod.putAmount(Mod130Key.C15, getInitialC15(ctx,mod))
			,null
			,"<li>Trimestres anteriores:<ul style=\"padding-left: 20px;\">" 
			+"<li>cantidades negtivas [019] y deducidas [015]:<ul style=\"padding-left: 20px;\">"
			+"@code{c19Sum = 0.0;c15Sum = 0.0;}"
			+"@foreach{fm : previousModels}" 
				+"@code{X19 =  fm.getAmount('"+Mod130Key.C19.getValue()+"');"
					  +"X15 =  fm.getAmount('"+Mod130Key.C15.getValue()+"');"
					  +"X19 =  X19 < 0 ? X19 : 0.0;"
					  +"c19Sum = c19Sum + X19;"
					  +"c15Sum = c15Sum + X15;"
					+ "}"
				+"<li>@{fm.getPeriod().getDescription()}			Casilla [019] --> @{X19}</li>"
				+"<li>				Casilla [015] --> @{X15}</li>"
			+"@end{}"
			+"</ul></li>"
			+"<li>Sumatorio de las casillas [019] --> @{c19Sum}</li>"
			+"<li>Sumatorio de las casillas [015] --> @{c15Sum}</li>"
			+"<li>Resultado: <b>@{C15}</b></li>")

		,C16 (Mod130Key.C16 , (mod -> mod.isAEAT()),null
			,"computeC16()"
			,"@if{ P2 <= 0}"
			 	+"<li>NO se han realizado pagos por pr\u00E9stamos destinados a la adquisici\u00F3n o rehabilitaci\u00F3n de su vivienda habitual</li>"
			 +"@else{}"
			 	+"<li>Se han realizado pagos por pr\u00E9stamos destinados a la adquisici\u00F3n o rehabilitaci\u00F3n de su vivienda habitual</li>"
			 	+"@if{ C03 > 0 && C08 > 0}"
			 		+ "<li>El contribuyente realiza simult\u00E1neamente actividades agr\u00EDcolas, ganaderas, forestales o pesqueras y actividades distintas de \u00E9stas. ( [003] > 0 y [008] > 0). El resultado es <b>ceroz</b></li>"
			 	+"@else{}"
			 		+"@code{cXX=C03>C08?C03:C08}"
			 		+"<li>Se toma el mayor valor entre las casillas [003] y [008]: @{cXX}</li>"
			 		+"@code{cYY=com.esferalia.aon.watson.util.AonMathUtils.round(cXX*2/100)}"
			 		+"<li>2% de @{cXX} = @{cYY}</li>"
			 		+"@if{ cYY > (C14 - C15)}"
			 			+"<li>El importe consignado en la casilla [016] no podr\u00E1 ser superior a la diferencia positiva entre las casillas [014] y [015].</li>"
			 			+"@code{cYY=com.esferalia.aon.watson.util.AonMathUtils.round(C14 - C15)}"
			 			+"<li>@{C14} - @{C15} = @{cYY}</li>"
			 		+"@end{}"
					+"@if{ cYY > C16_MAX_VALUE}"
						+"<li>Se aplica el l\u00EDmite m\u00E1ximo de @{C16_MAX_VALUE}</li>"		 			
					+"@end{}"
			 	+"@end{}"
			 +"@end{}"
			 +"<li>Resultado: <b>@{C16}</b></li>"	
			)
		,C17 (Mod130Key.C17 , (mod -> mod.isAEAT()),null
			,"C14 - C15 - C16"
			,"<li>@{C14} menos @{C15} menos @{C16} igual <b>@{C17}</b></li>")
		,C18 (Mod130Key.C18 , (mod -> mod.isAEAT())
			,(ctx,mod) ->  
					mod.putAmount(Mod130Key.C18,mod.isComplementary()
							?getSamePeriodModels(ctx, mod)
									.mapToDouble(fm -> fm.getResult())
									.sum()
							:0.0)
			,null
			,"<li>Declarciones en el mismo periodo/ejercicio:<ul style=\"padding-left: 20px;\">" 
			+"@code{c07Sum = 0.0;c13Sum = 0.0;}"
			+"@foreach{fm : periodModels}" 
				+"<li>Resultado:	Casilla [019] --> @{fm.getResult()}</li>"
			+"@end{}"
			+"</ul></li>"
			+"<li>Resultado: <b>@{C18}</b></li>")
		,C19 (Mod130Key.C19 , (mod -> mod.isAEAT()),null
			,"C17 - C18"
			,"<li>@{C17} menos @{C18} igual <b>@{C19}</b></li>")
		
		
		,P3  (Mod130Key.P3  , (mod -> mod.isBizkaia()),null,null,null)
		,B01 (Mod130Key.C01 , (mod -> mod.isBizkaia()),null,null,null)
		,B02 (Mod130Key.C02 , (mod -> mod.isBizkaia()),null
			,"C01*5/100"
			,"<li>5% de la casilla @{C01} igual <b>@{C02}</b></li>")
		,B03 (Mod130Key.C03 , (mod -> mod.isBizkaia()),null,null,null)
		,B04 (Mod130Key.C04 , (mod -> mod.isBizkaia()),null
			,"C02-C03"
			,"<li>@{C02} menos @{C03} igual <b>@{C04}</b></li>")
		,B05 (Mod130Key.C05 , (mod -> mod.isBizkaia()),null,null,null)
		,B06 (Mod130Key.C06 , (mod -> mod.isBizkaia()),null
			,"C05*2/100"
			,"<li>2% de la casilla @{C05} igual <b>@{C06}</b></li>")
		,B07 (Mod130Key.C07 , (mod -> mod.isBizkaia()),null,null,null)
		,B08 (Mod130Key.C08 , (mod -> mod.isBizkaia()),null
			,"C06-C07"
			,"<li>@{C06} menos @{C07} igual <b>@{C08}</b></li>")
		,B09 (Mod130Key.C09 , (mod -> mod.isBizkaia()),null,null,null)
		,B10 (Mod130Key.C10 , (mod -> mod.isBizkaia()),null
			,"C09*20/100"
			,"<li>20% de la casilla @{C09} igual <b>@{C10}</b></li>")
		,B11 (Mod130Key.C11 , (mod -> mod.isBizkaia()),null,null,null)
		,B12 (Mod130Key.C12 , (mod -> mod.isBizkaia()),null
			,"C10-C11"
			,"<li>@{C10} menos @{C11} igual <b>@{C12}</b></li>")
		,B15 (Mod130Key.C15 , (mod -> mod.isBizkaia()),null,null,null)
		,B16 (Mod130Key.C16 , (mod -> mod.isBizkaia()),null
			,"C15*0.5/100"
			,"<li>0,5% de la casilla @{C15} igual <b>@{C16}</b></li>")
		,B17 (Mod130Key.C17 , (mod -> mod.isBizkaia()),null,null,null)
		,B18 (Mod130Key.C18 , (mod -> mod.isBizkaia()),null
			,"C16-C17"
			,"<li>@{C16} menos @{C17} igual <b>@{C18}</b></li>")
		,B19 (Mod130Key.C19 , (mod -> mod.isBizkaia()),null,null,null)
		,B20 (Mod130Key.C20 , (mod -> mod.isBizkaia()),null
			,"C19*0.25/100"
			,"<li>0,25% de la casilla @{C19} igual <b>@{C20}</b></li>")
		,B28 (Mod130Key.C28 , (mod -> mod.isBizkaia()),null
			,"C04+C08+C12+C18+C20"
			,"<li>@{C04} m\u00E1s @{C08} m\u00E1s @{C12} m\u00E1s @{C18} m\u00E1s @{C20} igual <b>@{C28}</b></li>")
		,TIP (Mod130Key.CT_TIP , (mod -> mod.isAEAT() || mod.isBizkaia()), null,null,null)
		;
		
		private Mod130Key key;
		private IModelAccepter acceptModel;
		private IValueIntializer initializer;
		private String expression;
		private String template;

		private Mod130KeyDAO(Mod130Key key, IModelAccepter acceptModel
				,IValueIntializer initializer,String expression,String template) {
			this.key = key;
			this.acceptModel =  acceptModel;
			this.initializer = initializer;
			this.expression =  expression;
			this.template =  template;
		}
		
		public Mod130Key getKey() {
			return key;
		}
		public boolean acceptModel(Mod130 mod) {
			return  (acceptModel.accept(mod));
		}

		public void initialize(AONContext ctx,Mod130 mod) {
			if (initializer != null) {
				initializer.initialize(ctx, mod);
			}
		}
		public String getExpression() {
			return expression;
		}
		public String getTemplate() {
			return template;
		}
		public static Mod130KeyDAO safeValueOf(Mod130 mod, String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod130KeyDAO keyDAO : Mod130KeyDAO.values()) {	
				if (keyDAO.acceptModel(mod) && keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}
	}
	
	public static Stream<Mod130> getMod130s(AONContext ctx,int domain, FiscalModelFilter filter) {
		return getModelRecords(ctx, domain,FiscalModelType.M130,filter)
				.map( record -> map130(new Mod130(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	public static Stream<Mod130> getMod130s(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M130)
				.map( record -> map130(new Mod130(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	
	public static Mod130 getMod130(AONContext ctx,int id) {
		ctx.checkRead();
		final Mod130 mod130 = getModelRecord(ctx, id)
				.map( record -> map130(new Mod130(),record));
		if (mod130 != null) {
			getModelDetails(ctx,mod130).forEach( detail -> mod130.put( detail));	
		}
		return mod130;
		
	}
	
	public static Mod130 saveMod130(AONContext ctx, Mod130 mod130) {
		calculateMod130(ctx, mod130);
		FiscalModel fm = save(ctx, mod130);
		return getMod130(ctx, fm.getId());
	}
	
	public static Mod130 saveCommentsMod130(AONContext ctx, Mod130 mod130) {
		saveComments(ctx, mod130);
		return mod130;
	}

	private static Mod130MVELContext getMVELcontext(AONContext ctx,Mod130 mod130) {
		Mod130MVELContext mvelCtx = new Mod130MVELContext();
		for (String key : mod130.getMap().keySet()) {
			Mod130Key mod130Key = Mod130Key.getKey(key);
			if (mod130Key != null) {
				FiscalModelDetail detail = mod130.getMap().get(key);
				mvelCtx.put(mod130Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		mvelCtx.put("C16_MAX_VALUE",660.14);
		return mvelCtx;
	}
	
	public static Mod130 calculateMod130(AONContext ctx, Mod130 mod130) {
		Mod130MVELContext mvelCtx = getMVELcontext(ctx,mod130);
		for (Mod130KeyDAO key : Mod130KeyDAO.values()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) && key.acceptModel(mod130)) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod130.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod130; 
	}
	
	public static Mod130 initializeMod130(AONContext ctx,Mod130 mod130) {
		if (mod130 == null) {
			mod130 = new Mod130();
			mod130.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod130);
		if (mod130.isAEAT()) {
			mod130.putAmount(Mod130Key.P1, 100.0);
			mod130.setRegime(AppParamDAO.getDefaultIRPFRegime(ctx));
			mod130.putAmount(Mod130Key.P2, (AppParamDAO.isPermAddressChanges(ctx)?1:0) );
		}
		initializeDeponents(ctx, mod130);
		return mod130;
	}

	private static void initializeDeponents(AONContext ctx,final Mod130 mod130) {
		
		getMod130s(ctx, mod130.getDomain())
			.forEach(fm -> {
				if (mod130.getDeponents() == null || !mod130.getDeponents().containsKey(fm.getDocument())) {
					if (mod130.getDeponents() == null) mod130.setDeponents(new LinkedHashMap<String,Mod130>());
					mod130.getDeponents().put(fm.getDocument(), fm);
				}
			});
			;
		
		
	}

	public static Mod130 createMod130(AONContext ctx,Mod130 mod130) {
		for (Mod130KeyDAO key : Mod130KeyDAO.values()) {
			if (key.acceptModel(mod130)) {
				FiscalModelDetail detail = mod130.ensureDetail(key.getKey());
				detail.setExpression(key.getExpression());
			}
		}
		for (Mod130KeyDAO key : Mod130KeyDAO.values()) {
			key.initialize(ctx, mod130);
		}
		return calculateMod130(ctx, mod130);
	}

	public static String getMod130Info(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) {
		Mod130KeyInfoDAO k = Mod130KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod130KeyDAO keyDAO : Mod130KeyDAO.values()) {
			if (keyDAO.getKey() == script.getKeys()[0]) {
				return k.getInfo(ctx, mod130, script, keyDAO);
			}
		}
		return null; 
	}
	
	// -------------------------------------------------------------------- INFO
	private static String getCompute(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script) {
		return getCompute(ctx, mod130, script, getMVELcontext(ctx,mod130));
	}
	private static String getComputeKey(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script,Mod130KeyDAO keyDAO) {
		Mod130MVELContext mvelCtx = getMVELcontext(ctx,mod130);
		mvelCtx.put("RAW_C01", getRawC01(ctx, mod130));
		mvelCtx.put("RAW_C02", getRawC02(ctx, mod130));
		mvelCtx.put("RAW_C08", getRawC08(ctx, mod130));
		mvelCtx.put("LMT_GDJ", LIMITE_GASTOS_DIF_JUST);
		mvelCtx.put("yearStartDate", IRPFFormatter.FMT.format(AonDateUtils.getYearFirstDay(mod130.getYear())));
		mvelCtx.put("periodStartDate",IRPFFormatter.FMT.format(FiscalUtils.getPeriodStart(mod130)));
		mvelCtx.put("periodEndDate",IRPFFormatter.FMT.format(FiscalUtils.getPeriodEnd(mod130)));
		mvelCtx.put("previousModels", getPreviousModels(ctx, mod130).collect(Collectors.toCollection(LinkedList::new)));
		mvelCtx.put("periodModels", getSamePeriodModels(ctx, mod130).collect(Collectors.toCollection(LinkedList::new)));
		return getCompute(ctx, mod130, script,mvelCtx);
	}
	
	private static String getCompute(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script,Mod130MVELContext mvelCtx) {
		StringBuilder buf = new StringBuilder();
		buf.append("<pre style=\"font-family: Fixed, monospace;font-size: 0.9em; margin-bottom: 1em; padding: 1em;\">");
		for (Mod130Key key : script.getKeys() ) {
			Mod130KeyDAO keyDAO = Mod130KeyDAO.safeValueOf(mod130, key.getValue());
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
	
	private static String getInvoicesInfo(AONContext ctx, final Mod130 mod130
			, final IModelScript<Mod130Key> script, Mod130KeyDAO keyDAO, boolean farmer) {
		
		String title = "FACTURAS CON RETENCIONES QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod130)
				+ " DEL " + mod130.getPeriod().getDescription()
				+ " DE " + mod130.getYear();
		
		if (farmer)
		{
			// Llamada desde la casilla 10
			return IRPFFormatter.formatInvoices(title,script.getLabel()
			            ,IRPFDAO.getOutputInvoicesDiffIrpfBreakdown(ctx, mod130)
						 .filter(i -> i.isFarmer() == farmer)
						 .collect(Collectors.toCollection(LinkedList::new))
			);
		}
		else 
		{
			double percent = mod130.getAmount(Mod130Key.P1); 
			// Llamada desde la casilla 06
			return IRPFFormatter.formatInvoices(title,script.getLabel(),percent
					   ,IRPFDAO.getOutputInvoicesDiffIrpfBreakdown(ctx, mod130)
					    .filter(  i -> ( !i.isFarmer() && (i.getIRPFRegime() == null || i.getIRPFRegime() == IRPFRegime.NORMAL || i.getIRPFRegime() == IRPFRegime.SIMPLIFIED) ) )
						.collect(Collectors.toCollection(LinkedList::new))
			);
		}
		
	}
	
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod130 mod130
			, final IModelScript<Mod130Key> script, Mod130KeyDAO keyDAO, boolean farmer) {
		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
			+ FiscalModelUtils.getModelName(mod130)
			+ " DEL " + mod130.getPeriod().getDescription()
			+ " DE " + mod130.getYear();
		
		if (farmer)
		{
			// Llamada desde la Casilla 10
			return IRPFFormatter.formatDiffInvoices(title
				,script.getLabel()
				,script.getKeys()
				, getPreviousModels(ctx,mod130)
				 	.collect(Collectors.toCollection(LinkedList::new))	
				,IRPFDAO.getOutputInvoicesDiffIrpfBreakdown(ctx, mod130)
					.filter(i -> i.isFarmer() == farmer)
					.collect(Collectors.toCollection(LinkedList::new))
			);
		}
		else
		{
			// Llamada desde la Casilla 06
			return IRPFFormatter.formatDiffInvoices(title
					,script.getLabel()
					,script.getKeys()
					, getPreviousModels(ctx,mod130)
					 	.collect(Collectors.toCollection(LinkedList::new))	
					,IRPFDAO.getOutputInvoicesDiffIrpfBreakdown(ctx, mod130)
						.filter(  i -> ( !i.isFarmer() && (i.getIRPFRegime() == null || i.getIRPFRegime() == IRPFRegime.NORMAL || i.getIRPFRegime() == IRPFRegime.SIMPLIFIED) ) )
						.collect(Collectors.toCollection(LinkedList::new))
			);			
		}
	}
	// -------------------------------------------------------------------- UTIL
	public static Mod130 markAsFinished(AONContext ctx,Mod130 mod130) {
		mod130 = FiscalModelDAO.finish(ctx, mod130);
		return saveMod130(ctx, mod130);
	}
	
	public static Mod130 markAsPending(AONContext ctx,Mod130 mod130) {
		mod130.setDeclarationType((String) null);
		mod130.setStatus(FiscalStatus.PENDING);
		Finance finance = mod130.getFinance();
		mod130.setFinance(null);
		mod130 = saveMod130(ctx, mod130);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
		}
		return mod130;
	}
	public static Mod130 markAsSent(AONContext ctx,Mod130 mod130) {
		mod130.setStatus(FiscalStatus.SENT);
		mod130= saveMod130(ctx, mod130);
		return mod130;
	}
	
	public static Mod130 markAsCustomerCheck(AONContext ctx,Mod130 mod130) {
		mod130.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod130 = saveMod130(ctx, mod130);
		return mod130;
	}

	// --------------------------------------------------- KEY INTITIALIZATION
	private static Stream<AccountingBreakdown> getInitialBaseC01(AONContext ctx, final Mod130 mod) {
		return AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().ge(AonDateUtils.getYearFirstDay(mod.getYear())))
					.and(p.getEntryDateProperty().le(FiscalUtils.getPeriodEnd(mod)))
					.and(p.getAccountCodeProperty().like("7%"))
					)
			.filter( br -> (!br.hasActivity() || (!br.isFarmer() && (br.isNormalRegime() || br.isSimplifiedRegime())) ));
	}
	private static double getRawC01(AONContext ctx, final Mod130 mod) {
		double rawC01 = getInitialBaseC01(ctx, mod)
				.mapToDouble(br -> br.getCreditBalance())
				.sum();
		return AonMathUtils.round(rawC01);
	}
	private static double getInitialC01(AONContext ctx, final Mod130 mod) {
		double c01 = getRawC01(ctx, mod);  
		double percent = mod.getAmount(Mod130Key.P1);
		c01 = AonMathUtils.round(c01 * percent / 100 );
		return c01; 
	}

	private static String getAccountInfoInfo(AONContext ctx, Mod130 mod, IModelScript<Mod130Key> script,
			Mod130KeyDAO keyDAO) {
		String title = "SALDOS DE CUENTAS QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod)
				+ " DEL " + mod.getPeriod().getDescription()
				+ " DE " + mod.getYear();
		if (keyDAO == Mod130KeyDAO.C01) {
			return getC01Info(ctx, mod, script,title);
		} else if (keyDAO == Mod130KeyDAO.C02) {
			return getC02Info(ctx, mod, script,title);
		} else if (keyDAO == Mod130KeyDAO.C08) {
			return getC08Info(ctx, mod, script,title);
		}
		return null;
	}
	
	
	private static String getC01Info(AONContext ctx, final Mod130 mod130
			, final IModelScript<Mod130Key> script,String title) {
		return IRPFFormatter.formatAccountingBreakdown(title, script.getLabel() 
			,getInitialBaseC01(ctx, mod130).collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
	private static String getC02Info(AONContext ctx, final Mod130 mod130
			, final IModelScript<Mod130Key> script,String title) {
		return IRPFFormatter.formatAccountingBreakdown(title, script.getLabel() 
			,getInitialBaseC02(ctx, mod130).collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
	private static String getC08Info(AONContext ctx, final Mod130 mod130
			, final IModelScript<Mod130Key> script,String title) {
		return IRPFFormatter.formatAccountingBreakdown(title, script.getLabel() 
			,getInitialBaseC08(ctx, mod130).collect(Collectors.toCollection(LinkedList::new))
		);
	}

	private static Stream<AccountingBreakdown> getInitialBaseC02(AONContext ctx, final Mod130 mod) {
		return AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().ge(AonDateUtils.getYearFirstDay(mod.getYear())))
					.and(p.getEntryDateProperty().le(FiscalUtils.getPeriodEnd(mod)))
					.and(p.getAccountCodeProperty().like("6%"))
					)
			.filter( br -> (!br.hasActivity() || (!br.isFarmer() && (br.isNormalRegime() || br.isSimplifiedRegime()))));
	}
	private static double getRawC02(AONContext ctx, final Mod130 mod) {
		return getInitialBaseC02(ctx, mod)
				.mapToDouble(br -> br.getDebitBalance())
				.sum();
	}
	
	private static double getInitialC02(AONContext ctx, final Mod130 mod) {
		double c02 = getRawC02(ctx, mod);
		if (mod.getRegime() != null && mod.getRegime() == IRPFRegime.SIMPLIFIED) { 
			double c01 = getRawC01(ctx, mod);
			double c02_ = AonMathUtils.round( c01 - c02);
			if (c02_ > 0 ) {
				double dif = (c02_*5/100);
				if (dif > LIMITE_GASTOS_DIF_JUST ) {
					dif = LIMITE_GASTOS_DIF_JUST;
				}
				c02 = AonMathUtils.round( c02 + dif ); 
			}
		}
		double percent = mod.getAmount(Mod130Key.P1);
		c02 = AonMathUtils.round(c02 * percent / 100 );
		return c02;
	}
	
	private static double getInitialC05(AONContext ctx, final Mod130 mod) {
		LinkedList<FiscalModel> list = getPreviousModels(ctx, mod)
				.collect(Collectors.toCollection(LinkedList::new));
		double c05 = 0;
		for (FiscalModel fm : list) {
			double c007 = fm.getAmount(Mod130Key.C07);
			c007 = c007 < 0 ? 0.0 : c007;
			c05 = AonMathUtils.round( c05 + ( c007 - fm.getAmount(Mod130Key.C16)));
		}
		return c05;
	}
	private static Stream<AccountingBreakdown> getInitialBaseC08(AONContext ctx, final Mod130 mod) {
		return AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
				.and(p.getEntryDateProperty().ge(FiscalUtils.getPeriodStart(mod)))
				.and(p.getEntryDateProperty().le(FiscalUtils.getPeriodEnd(mod)))
				.and(p.getAccountCodeProperty().like("7%"))
				)
			.filter( br -> (br.isFarmer() && !br.isObjectiveRegime()));
	}
	private static double getRawC08(AONContext ctx, final Mod130 mod) {
		return getInitialBaseC08(ctx, mod)
			.mapToDouble(br -> br.getCreditBalance())
			.sum();
	}
	private static double getInitialC08(AONContext ctx, final Mod130 mod) {
		double c08 = getRawC08(ctx, mod); 
		double percent = mod.getAmount(Mod130Key.P1);
		c08 = AonMathUtils.round(c08 * percent / 100 );
		return c08; 
	}
	
	private static double getInitialC13(AONContext ctx, final Mod130 mod) {
		double c03 = 0.0;
		double c08 = 0.0;
		double rn = 0.0;
		double c13 = 0.0;
		Mod130 previous = getMod130s(ctx, mod.getDomain())
		 .filter(model -> model.getYear() == (mod.getYear() - 1))
		 .filter(model -> model.getPeriod() == Period.T4)
		 .findFirst()
		 .orElse(null);
		
		if (previous != null) {
			c03 = previous.getAmount(Mod130Key.C03);
			c08 = (previous.getAmount(Mod130Key.C08) * 25 / 100);
			rn = AonMathUtils.round(c03 + c08);
			if (rn <= 9000) {
				c13 = 100;
			} else if (rn > 9000 && rn <= 10000) {
				c13 = 75;
			} else if (rn > 10000 && rn <= 11000) {
				c13 = 50;
			} else if (rn > 11000 && rn <= 12000) {
				c13 = 25;
			}
		}
		
		return c13;
	}
	
	private static double getInitialC15(AONContext ctx, final Mod130 mod) {
		double c14 = mod.getAmount(Mod130Key.C14);
		double c15 = 0.0; 
		if (c14 > 0) {
			c15 = getPreviousModels(ctx, mod)
				.mapToDouble(fm -> AonMathUtils.round(
					AonMathUtils.absRounded(fm.getAmount(Mod130Key.C19)>0?0:(fm.getAmount(Mod130Key.C19))
					- fm.getAmount(Mod130Key.C15) )))
				.sum()
			;
			c15 = c15>c14?c14:c15;
		}
		return c15;
	}

}
