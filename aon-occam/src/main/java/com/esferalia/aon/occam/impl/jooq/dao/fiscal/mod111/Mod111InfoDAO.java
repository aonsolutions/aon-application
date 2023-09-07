package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;


import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.IrpfBreakdownJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod111InfoDAO {
	private static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");		
	
	private Mod111InfoDAO() {
		
	}
	
	public static String getInfo(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) {
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
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
					@Override public String visitActAccount() {return visitNone(); }
					@Override public String visitTitle() {return visitNone(); }
					@Override public String visitIrpfActivity() {return visitNone(); }
					@Override public String visitCorporate() {return visitNone(); }
					@Override public String visitModelInvoiceVatBreakdown() {return visitNone(); }
					@Override public String visitProrratedModelInvoiceVatBreakdown() {return visitNone(); }
					@Override public String visitModelOutVatAccrualInvoice() {return visitNone(); }
					@Override public String visitModelInVatAccrualInvoice() {return visitNone(); }

					@Override 
					public String visitNone()    {
						return AonStringUtils.EMPTY; 
					} 
					
					@Override 
					public String visitCompute() {
						return Objects.requireNonNullElse(getExpression(mod111, script),new JSONArray()).toString();
					}
					
					@Override 
					public String visitComputeKey() {
						return getComputeKey(ctx, mod111, script, keyDAO);
					}
					
					@Override 
					public String visitModelInvoiceIrpfBreakdown() {
						return Objects.requireNonNullElse(
							getModelInvoicesInfo(ctx, mod111,keyDAO)
								.map( IrpfBreakdownJSON::toJSON)
								.collect(JSONArray::new,JSONArray::put,JSONArray::put)
								,new JSONArray()).toString();
					}
					
					@Override 
					public String visitModelSalaryIrpfBreakdown() {
						return Objects.requireNonNullElse(
							getModelSalariesInfo(ctx, mod111,keyDAO)
								.map( IrpfBreakdownJSON::toJSON)
								.collect(JSONArray::new,JSONArray::put,JSONArray::put)
								,new JSONArray()).toString();
					}
				})
			)
			.findFirst()
			.orElse(null);
	}
	
	public static Stream<IrpfBreakdown> getModelInvoicesInfo(AONContext ctx, final Mod111 mod111, Mod111Key key) {
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		return getModelInvoicesInfo(ctx, mod111, dec.getKey(key)); 
	}
	
	private static Stream<IrpfBreakdown> getModelInvoicesInfo(AONContext ctx, final Mod111 mod111, IMod111KeyDAO keyDAO) {
		return IRPFDAO.getModelInputInvoicesIrpfBreakdown(ctx, mod111)
			.filter( br ->  keyDAO.acceptValue(mod111, br));
	}
	
	private static Stream<IrpfBreakdown> getModelSalariesInfo(AONContext ctx, final Mod111 mod111, IMod111KeyDAO keyDAO) {
		return IRPFDAO.getModelSalaryIrpfBreakdown(ctx, mod111)
			.filter( br ->  keyDAO.acceptValue(mod111, br));
	}

	private static class Mod111MVELContext extends LinkedHashMap<String, Object> {

		private static final long serialVersionUID = -8110340552664306400L;
		
		protected Mod111 mod111;
		protected final LinkedList<Mod111Key> keys = new LinkedList<>();
		
		public Mod111MVELContext(final Mod111 mod111) {
			this.mod111 = mod111;
			for (String keyValue : mod111.getMap().keySet()) {
				Mod111Key mod111Key = Mod111Key.getKey(keyValue);
				if (mod111Key != null) {
					FiscalModelDetail detail = mod111.getMap().get(keyValue);
					put(mod111Key.toString(), detail==null?0.0:detail.getAmount());
				}
			}
		}
		
		@SuppressWarnings("unused")
		// Used on templates
		public String format(Double amount) {
			return DEC2.format(amount);
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

	private static class Mod111MVELExpressionContext extends Mod111MVELContext {
		
		private static final long serialVersionUID = 7905148919414628166L;
		
		private String expression;
		public Mod111MVELExpressionContext(String expression, final Mod111 mod111) {
			super(mod111);
			this.expression = expression;
		}
		
		@Override
		public Object get(Object keyString) {
			Mod111Key key = Mod111Key.valueOf(keyString.toString());
			keys.add(key);
			String anchor = "{" + (keys.size() - 1) + "}";
			expression = AonStringUtils.replace(expression, keyString.toString(), anchor);
			return super.get(keyString);
		}
		
		public String getExpression() {
			return expression;
		}
		public String getFormula() {
			 return MessageFormat.format(expression,keys.stream().map(Mod111Key::getBoxFormatted).toArray());
		}
		public List<Mod111Key> getKeys() {
			return keys;
		}
		public String getResult() {
			 return MessageFormat.format(expression,keys.stream()
					 .map(k-> " ["+DEC2.format(mod111.getAmount(k)) +"] " )
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
	
	private static String getExpression(Mod111 mod111, IModelScript<Mod111Key> script) {
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		JSONArray array = new JSONArray();
		for (Mod111Key key : script.getKeys() ) {
			IMod111KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				Mod111MVELExpressionContext mvelCtx = new Mod111MVELExpressionContext(keyDAO.getExpression(),mod111);
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
				for (Mod111Key k : mvelCtx.getKeys()) {
					keysArray.put(k);
					keysValues.put( DEC2.format(mod111.getAmount(k)));
				}
				info.put(IJsonNames.KEYS, keysArray);
				info.put(IJsonNames.KEY_VALUES, keysValues);
				array.put(info);
			}
			
		}
		return array.toString();
	}
	
	private static String getComputeKey(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script,IMod111KeyDAO keyDAO) {
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		StringBuilder buf = new StringBuilder();
		for (Mod111Key key : script.getKeys() ) {
			if (key != null) {
				if (Arrays.stream(dec.getSamePeriodExplainKeys()).anyMatch(k -> k == key)) {
					buf.append(dec.getSamePeriodExplain( ctx, mod111, key));
					return buf.toString();
				} else {
					Mod111MVELContext mvelCtx = new Mod111MVELContext(mod111); 
					mvelCtx.put("mod", mod111);
					mvelCtx.put("periodModels", FiscalModelDAO.getSamePeriodModels(ctx, mod111, Mod111::new).collect(Collectors.toCollection(LinkedList::new)));
					mvelCtx.put("lastPeriodModels", FiscalModelDAO.getLastPeriodModels(ctx, mod111, Mod111::new).collect(Collectors.toCollection(LinkedList::new)));
					mvelCtx.put("models", Mod111DAO.getMod111s(ctx, ctx.getDomainId()).collect(Collectors.toCollection(LinkedList::new)));
					String template = keyDAO.getTemplate();
					if (AonStringUtils.isNotBlank( template )) {
						Object result = TemplateRuntime.eval(template, mvelCtx);
						buf.append(result != null ? result.toString() : null);
					}
				}
			}
		}
		return buf.toString();
	}

}

