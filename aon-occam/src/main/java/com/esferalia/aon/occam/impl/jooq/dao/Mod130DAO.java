package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.stream.Stream;

import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
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

	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private static enum Mod130KeyInfoDAO {
		 NONE   	( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,COMPUTE	( ((ctx, mod, script,keyDAO) -> getExpression(mod, script)))
		,COMPUTE_KEY( ((ctx, mod, script,keyDAO) -> getExpression(mod, script)))
		
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
			,(ctx,mod) -> initializeC01(ctx,mod)
			,null
			,"<li>Desde contabilidad, saldo acreedor de las cuentas del grupo 7 desde el @{yearStartDate} al @{periodEndDate}</li>"
			+"<li>Resultado: <b>@{C01}</b></li>")
		,C02 (Mod130Key.C02 , (mod -> mod.isAEAT())
			,(ctx,mod) -> initializeC02(ctx,mod)
			,null
			,"<li>Desde contabilidad, saldo deudor de las cuentas del grupo 6 desde el @{yearStartDate} al @{periodEndDate}</li>"
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
		,C05 (Mod130Key.C05 , (mod -> mod.isAEAT()),null,null,null)
		,C06 (Mod130Key.C06 , (mod -> mod.isAEAT()),null,null,null)
		,C07 (Mod130Key.C07 , (mod -> mod.isAEAT()),null
			,"C04 - C05 - C06"
			,"<li>@{C04} menos @{C05} menos @{C06} igual <b>@{C07}</b></li>")
		,C08 (Mod130Key.C08 , (mod -> mod.isAEAT()),null,null,null)
		,C09 (Mod130Key.C09 , (mod -> mod.isAEAT()),null
			,"C08 * 2 / 100"
			,"<li>2% de @{C08} igual <b>@{C09}</b></li>")
		,C10 (Mod130Key.C10 , (mod -> mod.isAEAT()),null,null,null)
		,C11 (Mod130Key.C11 , (mod -> mod.isAEAT()),null
			,"C09 - C10"
			,"<li>@{C09} menos @{C10} igual <b>@{C11}</b></li>")
		,C12 (Mod130Key.C12 , (mod -> mod.isAEAT()),null
			,"C07 - C11"
			,"<li>@{C07} menos @{C11} igual <b>@{C12}</b></li>")
		,C13 (Mod130Key.C13 , (mod -> mod.isAEAT() && mod.getYear() >  2014),null,null,null)
		,C131(Mod130Key.C131, (mod -> mod.isAEAT() && mod.getYear() <= 2014),null,null,null)
		,C14 (Mod130Key.C14 , (mod -> mod.isAEAT()),null
			,"C12 - C13"
			,"<li>@{C12} menos @{C13} igual <b>@{C14}</b></li>")
		,C15 (Mod130Key.C15 , (mod -> mod.isAEAT()),null,null,null)
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
			 		+"@code{cYY=cXX*2/100}"
			 		+"<li>2% de @{cXX} = @{cYY}</li>"
			 		+"@if{ cYY > (C14 - C15)}"
			 			+"<li>El importe consignado en la casilla [016] no podr\u00E1 ser superior a la diferencia positiva entre las casillas [014] y [015].</li>"
			 			+"@code{cYY=C14 - C15}"
			 			+"<li>@{C14} - @{C15} = @{cYY}</li>"
			 		+"@end{}"
					+"@code{cMax="+Mod130MVELContext.C16_MAX_VALUE+"}"
					+"@if{ cYY > cMax}"
						+"<li>Se aplica el l\u00EDmite m\u00E1ximo de @{cMax}</li>"		 			
					+"@end{}"
			 	+"@end{}"
			 +"@end{}"
			 +"<li>Resultado: <b>@{C16}</b></li>"	
			)
		,C17 (Mod130Key.C17 , (mod -> mod.isAEAT()),null
			,"C14 - C15 - C16"
			,"<li>@{C14} menos @{C15} menos @{C16} igual <b>@{C17}</b></li>")
		,C18 (Mod130Key.C18 , (mod -> mod.isAEAT()),null,null,null)
		,C19 (Mod130Key.C19 , (mod -> mod.isAEAT()),null
			,"C17 - C18"
			,"<li>@{C17} menos @{C18} igual <b>@{C19}</b></li>")
		,TIP (Mod130Key.CT_TIP , (mod -> mod.isAEAT()), null,null,null)
		
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

	private static Mod130MVELContext getMVELcontext(Mod130 mod130) {
		Mod130MVELContext mvelCtx = new Mod130MVELContext();
		for (String key : mod130.getMap().keySet()) {
			Mod130Key mod130Key = Mod130Key.getKey(key);
			if (mod130Key != null) {
				FiscalModelDetail detail = mod130.getMap().get(key);
				mvelCtx.put(mod130Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx;
	}
	
	public static Mod130 calculateMod130(AONContext ctx, Mod130 mod130) {
		Mod130MVELContext mvelCtx = getMVELcontext(mod130);
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
		}
		if (mod130.getDeponents() == null || mod130.getDeponents().size() == 0) {
			initializeFiscalModel(ctx, mod130);
			mod130.putAmount(Mod130Key.P1, 100.0);
			mod130.setRegime(AppParamDAO.getDefaultIRPFRegime(ctx));
			mod130.putAmount(Mod130Key.P2, (AppParamDAO.isPermAddressChanges(ctx)?1:0) );
		}
		return mod130;
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
	private static String getExpression(Mod130 mod130, IModelScript<Mod130Key> script) {
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
					Mod130MVELContext mvelCtx = getMVELcontext(mod130);
					mvelCtx.put("yearStartDate", IRPFFormatter.FMT.format(AonDateUtils.getYearFirstDay(mod130.getYear())));
					mvelCtx.put("periodEndDate",IRPFFormatter.FMT.format(FiscalUtils.getPeriodEnd(mod130)));
					Object result = TemplateRuntime.eval(template, mvelCtx);
					buf.append(result != null ? result.toString() : null);
				}
				buf.append("</ul>");
			}
		}
		buf.append("</pre>");
		return buf.toString();
	}

	// -------------------------------------------------------------------- UTIL
	public static Mod130 finish(AONContext ctx,Mod130 mod130) {
		mod130 = FiscalModelDAO.finish(ctx, mod130);
		return saveMod130(ctx, mod130);
	}
	
	public static Mod130 reopen(AONContext ctx,Mod130 mod130) {
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

	// --------------------------------------------------- KEY INTITIALIZATION
	private static void initializeC01(AONContext ctx, final Mod130 mod) {
		double c01 = AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().ge(AonDateUtils.getYearFirstDay(mod.getYear())))
					.and(p.getEntryDateProperty().le(FiscalUtils.getPeriodEnd(mod)))
					.and(p.getAccountCodeProperty().like("7%"))
					)
			.filter( br -> (!br.isFarmer() && !br.isObjectiveRegime()))
			.mapToDouble(br -> br.getCreditBalance())
			.sum();
		double percent = mod.getAmount(Mod130Key.P1);
		mod.putAmount(Mod130Key.C01, AonMathUtils.round(c01 * percent / 100 ));
	}
	private static void initializeC02(AONContext ctx, final Mod130 mod) {
		double c02 = AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().ge(AonDateUtils.getYearFirstDay(mod.getYear())))
					.and(p.getEntryDateProperty().le(FiscalUtils.getPeriodEnd(mod)))
					.and(p.getAccountCodeProperty().like("6%"))
					)
			.filter( br -> (!br.isFarmer() && !br.isObjectiveRegime()))
			.mapToDouble(br -> br.getDebitBalance())
			.sum();
		// Cálculo según el método de E.D. simplificada.
		if (mod.getRegime() != null && mod.getRegime() == IRPFRegime.SIMPLIFIED) { 
			double c01 = mod.getAmount(Mod130Key.C01);
			double c02_ = AonMathUtils.round( c01 - c02);
			if (c02_ > 0 ) {
				c02 = AonMathUtils.round( c02 + (c02_ - (c02_*5/100)) ); 
			}
		}
		double percent = mod.getAmount(Mod130Key.P1);
		mod.putAmount(Mod130Key.C02, AonMathUtils.round(c02 * percent / 100 ));
	}
	
}
