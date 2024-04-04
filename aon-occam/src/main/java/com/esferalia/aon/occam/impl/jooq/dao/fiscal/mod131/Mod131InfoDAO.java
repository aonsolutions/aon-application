package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod131;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.AccountingBreakdownJSON;
import com.esferalia.aon.occam.api.json.IrpfBreakdownJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod131InfoDAO extends FiscalModelDAO {
	
	private static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");
	
	public static String getInfo(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) {
		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
		return  Stream.of(script)
				.map(IModelScript::getKeys)
				.filter( Objects::nonNull )
				.flatMap(Arrays::stream)
				.filter( Objects::nonNull )
				.map( dec::getKey )
				.map(keyDAO -> 	infoKey.visit( new IFiscalModelKeyInfoVisitor<String>() {
					@Override public String visitInvoice() {return visitNone(); }
					@Override public String visitInAccrualInvoice() {return visitNone(); }
					@Override public String visitOutAccrualInvoice() {return visitNone(); }
					@Override public String visitDiffInvoice() {return visitNone(); }
					@Override public String visitDiffInAccrualInvoice() {return visitNone(); }
					@Override public String visitDiffOutAccrualInvoice() {return visitNone(); }
					@Override public String visitSalary() {return visitNone(); }
					@Override public String visitDiffSalary() {return visitNone(); }
					@Override public String visitTitle() {return visitNone(); }
					@Override public String visitIrpfActivity() {return visitNone(); }
					@Override public String visitCorporate() {return visitNone(); }
					@Override public String visitModelInvoiceVatBreakdown() {return visitNone(); }
					@Override public String visitProrratedModelInvoiceVatBreakdown() {return visitNone(); }
					@Override public String visitModelOutVatAccrualInvoice() {return visitNone(); }
					@Override public String visitModelInVatAccrualInvoice() {return visitNone(); }
					@Override public String visitModelSalaryIrpfBreakdown() {return visitNone();}
					
					@Override 
					public String visitNone()    {
						return AonStringUtils.EMPTY; 
					} 
					
					@Override 
					public String visitCompute() {
						return Objects.requireNonNullElse(getExpression(ctx, mod131, script),new JSONArray()).toString();
					}
					
					@Override 
					public String visitComputeKey() {
						return getComputeKey(ctx, mod131, script, keyDAO);
					}
					
					@Override 
					public String visitModelInvoiceIrpfBreakdown() {
						return Objects.requireNonNullElse(
							getInvoicesInfo(ctx, mod131, script,keyDAO)
								.map( IrpfBreakdownJSON::toJSON)
								.collect(JSONArray::new,JSONArray::put,JSONArray::put)
								,new JSONArray()).toString();
					}

					@Override 
					public String visitActAccount() {
						return Objects.requireNonNullElse(
								getAccountInfoInfo(ctx, mod131, script,keyDAO)
									.map( AccountingBreakdownJSON::toJSON)
									.collect(JSONArray::new,JSONArray::put,JSONArray::put)
									,new JSONArray()).toString();
					}
					
				})
			)
			.findFirst()
			.orElse(null);
	}
	
	private static String getComputeKey(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script,IMod131KeyDAO keyDAO) {
		return Stream.of( script )
			.map(IModelScript::getKeys)
			.filter( Objects::nonNull )
			.flatMap(Arrays::stream)
			.filter( Objects::nonNull )
			.map(key -> keyDAO.info( ctx, mod131) )
			.findFirst()
			.orElse(null);
	}

	private static Stream<IrpfBreakdown> getInvoicesInfo(AONContext ctx, final Mod131 mod131 , final IModelScript<Mod131Key> script, IMod131KeyDAO keyDAO) {
		return IRPFDAO.getOutputInvoicesIrpfBreakdown(ctx, mod131)
			.filter(br -> keyDAO.acceptIrpfBreakdown(mod131, br ));
	}

	private static Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod131 mod, IModelScript<Mod131Key> script,IMod131KeyDAO keyDAO) {
		Mod131Declaration dec = Mod131Declaration.getInstance(mod);
		return dec.getAccountInfoInfo(ctx, mod, script, keyDAO);
	}
	
	private static String getExpression(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script) {
		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
		JSONArray array = new JSONArray();
		for (Mod131Key key : script.getKeys() ) {
			IMod131KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				Mod131MVELExpressionContext mvelCtx = new Mod131MVELExpressionContext(ctx ,mod131 , keyDAO.getExpression());
				Object ret = MVEL.eval(keyDAO.getExpression(), mvelCtx, mvelCtx);
				JSONObject info = new JSONObject()
					.put(IJsonNames.EXPRESSION, keyDAO.getExpression())
					.put(IJsonNames.PATTERN, mvelCtx.getExpression())
					.put(IJsonNames.FORMULA, mvelCtx.getFormula())
					.put(IJsonNames.KEY, key.toString())
					.put(IJsonNames.RESULT, mvelCtx.getResult())
					.put(IJsonNames.VALUE, DEC2.format(ret))
					;
				JSONArray keysArray = new JSONArray();		
				JSONArray keysValues = new JSONArray();
				for (Mod131Key k : mvelCtx.getKeys()) {
					keysArray.put(k);
					keysValues.put( DEC2.format(mod131.getAmount(k)));
				}
				info.put(IJsonNames.KEYS, keysArray);
				info.put(IJsonNames.KEY_VALUES, keysValues);
				array.put(info);
			}
			
		}
		return array.toString();
	}
	
	private static class Mod131MVELExpressionContext extends Mod131MVELContext {
		
		private String expression;
		public Mod131MVELExpressionContext(final AONContext ctx, final Mod131 mod131, String expression) {
			super(ctx, mod131);
			this.expression = expression;
		}
		
		@Override
		public Object get(Object keyString) {
			Mod131Key key = Mod131Key.valueOf(keyString.toString());
			keys.add(key);
			String anchor = "{" + (keys.size() - 1) + "}";
			expression = AonStringUtils.replace(expression, keyString.toString(), anchor);
			return super.get(keyString);
		}
		
		public String getExpression() {
			return expression;
		}
		public String getFormula() {
			 return MessageFormat.format(expression,keys.stream().map(Mod131Key::getBoxFormatted).toArray());
		}
		public List<Mod131Key> getKeys() {
			return keys;
		}
		public String getResult() {
			 return MessageFormat.format(expression,keys.stream()
					 .map(k-> " ["+DEC2.format(mod131.getAmount(k)) +"] " )
					 .toArray());
		}
		@Override
		public boolean equals(Object o) {
			return super.equals(o);
		}
		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}
	
	// **************************************************
	// **************************************************
//	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
//	private static final String NONE_INFO = "No hay datos";

//	private static enum Mod131KeyInfoDAO {
//		 NONE   	 ( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO))
//		,COMPUTE	 ( (ctx, mod, script,keyDAO) -> getCompute(ctx,mod, script))
//		,COMPUTE_KEY ( (ctx, mod, script,keyDAO) -> getComputeKey(ctx,mod, script,keyDAO))
//		,INVOICE	 ( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO)))
//		,DIFF_INVOICE( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO)))
//		;
//		private IModelInfoProvider provider;
//		
//		private Mod131KeyInfoDAO (IModelInfoProvider provider) {
//			this.provider = provider;
//		}
//		public String getInfo(AONContext ctx, Mod131 mod,IModelScript<Mod131Key> script, IMod131KeyDAO keyDAO) {
//			return provider.obtain(ctx, mod, script,keyDAO);
//		}
//	}
	
	// -------------------------------------------------------------------- INFO
//	private static String getCompute(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script) {
//		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
//		Mod131MVELContext mvelCtx = dec.getMVELcontext(ctx,mod131);
//		return getCompute(ctx, mod131, script, mvelCtx);
//	}
//	private static String getComputeKey(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, IMod131KeyDAO keyDAO) {
//		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
//		Mod131MVELContext mvelCtx = dec.getMVELcontext(ctx,mod131);
//		mvelCtx.put("yearStartDate", IRPFFormatter.FMT.format(AonDateUtils.getYearFirstDay(mod131.getYear())));
//		mvelCtx.put("periodStartDate",IRPFFormatter.FMT.format(FiscalUtils.getPeriodStart(mod131)));
//		mvelCtx.put("periodEndDate",IRPFFormatter.FMT.format(FiscalUtils.getPeriodEnd(mod131)));
//		mvelCtx.put("previousModels", com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO.getPreviousModels(ctx, mod131).collect(Collectors.toCollection(LinkedList::new)));
//		mvelCtx.put("periodModels", com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO.getSamePeriodModels(ctx, mod131).collect(Collectors.toCollection(LinkedList::new)));
//		return getCompute(ctx, mod131, script,mvelCtx);
//	}
	
//	private static String getCompute(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script,Mod131MVELContext mvelCtx) {
//		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
//		StringBuilder buf = new StringBuilder();
//		buf.append("<pre style=\"font-family: Fixed, monospace;font-size: 0.9em; margin-bottom: 1em; padding: 1em;\">");
//		for (Mod131Key key : script.getKeys() ) {
//			IMod131KeyDAO keyDAO = dec.getKey(key);
//			if (keyDAO != null) {
//				String box = keyDAO.getKey().getBoxFormatted();
//				buf.append(AonStringUtils.CR_LF);
//				buf.append("<b>DETALLE DEL C\u00C1LCULO DE LA CASILLA: " + box + " - " + script.getLabel() + "</b>");
//				buf.append(AonStringUtils.CR_LF);
//				buf.append(AonStringUtils.CR_LF);
//				buf.append("<ul style=\"padding-left: 20px;\">");
////				if (AonStringUtils.isNotBlank( keyDAO.getExpression()) && keyDAO != Mod131KeyDAO.C12 ) {
////					String exp = keyDAO.getExpression();
////					if (keyDAO == Mod131KeyDAO.C10) exp =  "C07 - C08 - C09";
////					buf.append("<li><b>F\u00F3rmula:</b> " + exp + "</li>" );
////				}
//				String template = keyDAO.info(ctx, mod131);
//				if (AonStringUtils.isNotBlank( template )) {
//					Object result = TemplateRuntime.eval(template, mvelCtx);
//					buf.append(result != null ? result.toString() : null);
//				}
//				buf.append("</ul>");
//			}
//		}
//		buf.append("</pre>");
//		return buf.toString();
//	}
	
//	private static String getInvoicesInfo(AONContext ctx, final Mod131 mod131
//			, final IModelScript<Mod131Key> script, IMod131KeyDAO keyDAO) {
//		
//		String title = "FACTURAS CON RETENCIONES QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
//				+ FiscalModelUtils.getModelName(mod131) 
//				+ " DEL " + mod131.getPeriod().getDescription()
//				+ " DE " + mod131.getYear();
//		return IRPFFormatter.formatInvoices(title,script.getLabel()
//			,IRPFDAO.getOutputInvoicesDiffIrpfBreakdown(ctx, mod131)
//					.filter(br -> !br.isExempt())
//					.collect(Collectors.toCollection(LinkedList::new))
//		);
//	}
	
//	private static String getDiffInvoicesInfo(AONContext ctx, final Mod131 mod131
//			, final IModelScript<Mod131Key> script, IMod131KeyDAO keyDAO) {
//		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
//			+ FiscalModelUtils.getModelName(mod131) 
//			+ " DEL " + mod131.getPeriod().getDescription()
//			+ " DE " + mod131.getYear();
//		return IRPFFormatter.formatDiffInvoices(title
//			,script.getLabel()
//			,script.getKeys()
//			, com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO.getPreviousModels(ctx,mod131)
//			 	.collect(Collectors.toCollection(LinkedList::new))	
//			,IRPFDAO.getOutputInvoicesDiffIrpfBreakdown(ctx, mod131)
//				.filter(br -> !br.isExempt())
//				.collect(Collectors.toCollection(LinkedList::new))
//		);
//	}
	
//	public static String getInfo(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) {
//		Mod131Declaration dec = Mod131Declaration.getInstance(mod131);
//		Mod131KeyInfoDAO k = Mod131KeyInfoDAO.valueOf(infoKey.toString());
//		for (IMod131KeyDAO keyDAO : dec.getKeys() ) {
//			if (keyDAO.getKey().getValue().equals( script.getKeys()[0].getValue())) {
//				return k.getInfo(ctx, mod131, script, keyDAO);
//			}
//		}
//		return null; 
//	}
}
