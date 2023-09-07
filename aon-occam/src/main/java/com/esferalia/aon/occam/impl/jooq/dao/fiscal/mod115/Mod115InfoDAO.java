package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115;


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
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod115InfoDAO {
	private static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");		
	
	private Mod115InfoDAO() {

	}
	
	public static String getInfo(AONContext ctx, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) {
		Mod115Declaration dec = Mod115Declaration.getInstance(mod115);
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
					@Override public String visitModelSalaryIrpfBreakdown() {return visitNone(); }
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
						return Objects.requireNonNullElse(getExpression(mod115, script),new JSONArray()).toString();
					}
					
					@Override 
					public String visitComputeKey() {
						return getComputeKey(ctx, mod115, script, keyDAO);
					}
					
					@Override 
					public String visitModelInvoiceIrpfBreakdown() {
						return Objects.requireNonNullElse(
							getModelInvoicesInfo(ctx, mod115,keyDAO)
								.map( IrpfBreakdownJSON::toJSON)
								.collect(JSONArray::new,JSONArray::put,JSONArray::put)
								,new JSONArray()).toString();
					}
					
				})
			)
			.findFirst()
			.orElse(null);
	}
	
	public static Stream<IrpfBreakdown> getModelInvoicesInfo(AONContext ctx, final Mod115 mod115, Mod115Key key) {
		Mod115Declaration dec = Mod115Declaration.getInstance(mod115);
		return getModelInvoicesInfo(ctx, mod115, dec.getKey(key)); 
	}

	private static Stream<IrpfBreakdown> getModelInvoicesInfo(AONContext ctx, final Mod115 mod115, IMod115KeyDAO keyDAO) {
		return IRPFDAO.getModelInputInvoicesIrpfBreakdown(ctx, mod115)
			.filter( br ->  keyDAO.acceptValue(mod115, br));
	}
	
	private static class Mod115MVELContext extends LinkedHashMap<String, Object> {

		private static final long serialVersionUID = -8110340552664306400L;
		
		protected Mod115 mod115;
		protected final LinkedList<Mod115Key> keys = new LinkedList<>();
		
		public Mod115MVELContext(final Mod115 mod115) {
			this.mod115 = mod115;
			for (String keyValue : mod115.getMap().keySet()) {
				Mod115Key mod115Key = Mod115Key.getKey(keyValue,mod115.getAdministration());
				if (mod115Key != null) {
					FiscalModelDetail detail = mod115.getMap().get(keyValue);
					put(mod115Key.toString(), detail==null?0.0:detail.getAmount());
				}
			}
		}
		
		@SuppressWarnings("unused")
		// Used on templates
		public String format(Double amount) {
			return DEC2.format(amount);
		}

	}

	private static class Mod115MVELExpressionContext extends Mod115MVELContext {
		
		private static final long serialVersionUID = 7905148919414628166L;
		
		private String expression;
		public Mod115MVELExpressionContext(String expression, final Mod115 mod115) {
			super(mod115);
			this.expression = expression;
		}
		
		@Override
		public Object get(Object keyString) {
			Mod115Key key = Mod115Key.valueOf(keyString.toString());
			keys.add(key);
			String anchor = "{" + (keys.size() - 1) + "}";
			expression = AonStringUtils.replace(expression, keyString.toString(), anchor);
			return super.get(keyString);
		}
		
		public String getExpression() {
			return expression;
		}
		public String getFormula() {
			 return MessageFormat.format(expression,keys.stream().map(Mod115Key::getBoxFormatted).toArray());
		}
		public List<Mod115Key> getKeys() {
			return keys;
		}
		public String getResult() {
			 return MessageFormat.format(expression,keys.stream()
					 .map(k-> " ["+DEC2.format(mod115.getAmount(k)) +"] " )
					 .toArray());
		}
		
	}
	
	private static String getExpression(Mod115 mod115, IModelScript<Mod115Key> script) {
		Mod115Declaration dec = Mod115Declaration.getInstance(mod115);
		JSONArray array = new JSONArray();
		for (Mod115Key key : script.getKeys() ) {
			IMod115KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				Mod115MVELExpressionContext mvelCtx = new Mod115MVELExpressionContext(keyDAO.getExpression(),mod115);
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
				for (Mod115Key k : mvelCtx.getKeys()) {
					keysArray.put(k);
					keysValues.put( DEC2.format(mod115.getAmount(k)));
				}
				info.put(IJsonNames.KEYS, keysArray);
				info.put(IJsonNames.KEY_VALUES, keysValues);
				array.put(info);
			}
			
		}
		return array.toString();
	}
	
	private static String getComputeKey(AONContext ctx, Mod115 mod115, IModelScript<Mod115Key> script,IMod115KeyDAO keyDAO) {
		Mod115Declaration dec = Mod115Declaration.getInstance(mod115);
		StringBuilder buf = new StringBuilder();
		for (Mod115Key key : script.getKeys() ) {
			if (key != null) {
				if (Arrays.stream(dec.getSamePeriodExplainKeys()).anyMatch(k -> k == key)) {
					buf.append(  dec.getSamePeriodExplain( ctx, mod115, key));
					return buf.toString();
				} else {
					Mod115MVELContext mvelCtx = new Mod115MVELContext(mod115); 
					mvelCtx.put("mod", mod115);
					mvelCtx.put("periodModels", FiscalModelDAO.getSamePeriodModels(ctx, mod115, Mod115::new).collect(Collectors.toCollection(LinkedList::new)));
					mvelCtx.put("lastPeriodModels", FiscalModelDAO.getLastPeriodModels(ctx, mod115, Mod115::new).collect(Collectors.toCollection(LinkedList::new)));
					mvelCtx.put("models", Mod115DAO.getMod115s(ctx, ctx.getDomainId()).collect(Collectors.toCollection(LinkedList::new)));
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

