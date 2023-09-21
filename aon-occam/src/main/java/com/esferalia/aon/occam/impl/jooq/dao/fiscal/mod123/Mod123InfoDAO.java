package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123;


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
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod123InfoDAO {
	private static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");		
	
	private Mod123InfoDAO() {
		
	}
	
	public static String getInfo(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) {
		Mod123Declaration dec = Mod123Declaration.getInstance(mod123);
		return Arrays.stream(dec.getKeys())
			.filter( keyDAO -> keyDAO.getKey() == script.getKeys()[0])
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
						return Objects.requireNonNullElse(getExpression(mod123, script),new JSONArray()).toString();
					}
					
					@Override 
					public String visitComputeKey() {
						return getComputeKey(ctx, mod123, script, keyDAO);
					}
					
					@Override 
					public String visitModelInvoiceIrpfBreakdown() {
						return Objects.requireNonNullElse(
							getModelInvoicesInfo(ctx, mod123,keyDAO)
								.map( IrpfBreakdownJSON::toJSON)
								.collect(JSONArray::new,JSONArray::put,JSONArray::put)
								,new JSONArray()).toString();
					}
				})
			)
			.findFirst()
			.orElse(null);
	}
	
	public static Stream<IrpfBreakdown> getModelInvoicesInfo(AONContext ctx, final Mod123 mod123, Mod123Key key) {
		Mod123Declaration dec = Mod123Declaration.getInstance(mod123);
		return getModelInvoicesInfo(ctx, mod123, dec.getKey(key)); 
	}
	private static Stream<IrpfBreakdown> getModelInvoicesInfo(AONContext ctx, final Mod123 mod123, IMod123KeyDAO keyDAO) {
		return IRPFDAO.getModelInputInvoicesIrpfBreakdown(ctx, mod123)
			.filter( br ->  keyDAO.acceptValue(mod123, br));
	}
	
	private static class Mod123MVELContext extends LinkedHashMap<String, Object> {

		private static final long serialVersionUID = -8110340552664306400L;
		
		protected Mod123 mod123;
		protected final LinkedList<Mod123Key> keys = new LinkedList<>();
		
		public Mod123MVELContext(final Mod123 mod123) {
			this.mod123 = mod123;
			for (String keyValue : mod123.getMap().keySet()) {
				Mod123Key mod123Key = Mod123Key.getKey(keyValue,mod123.getAdministration());
				if (mod123Key != null) {
					FiscalModelDetail detail = mod123.getMap().get(keyValue);
					put(mod123Key.toString(), detail==null?0.0:detail.getAmount());
				}
			}
		}
		
		@SuppressWarnings("unused")
		// Used on templates
		public String format(Double amount) {
			return DEC2.format(amount);
		}

	}

	private static class Mod123MVELExpressionContext extends Mod123MVELContext {
		
		private static final long serialVersionUID = 7905148919414628166L;
		
		private String expression;
		public Mod123MVELExpressionContext(String expression, final Mod123 mod123) {
			super(mod123);
			this.expression = expression;
		}
		
		@Override
		public Object get(Object keyString) {
			Mod123Key key = Mod123Key.valueOf(keyString.toString());
			keys.add(key);
			String anchor = "{" + (keys.size() - 1) + "}";
			expression = AonStringUtils.replace(expression, keyString.toString(), anchor);
			return super.get(keyString);
		}
		
		public String getExpression() {
			return expression;
		}
		public String getFormula() {
			 return MessageFormat.format(expression,keys.stream().map(Mod123Key::getBoxFormatted).toArray());
		}
		public List<Mod123Key> getKeys() {
			return keys;
		}
		public String getResult() {
			 return MessageFormat.format(expression,keys.stream()
					 .map(k-> " ["+DEC2.format(mod123.getAmount(k)) +"] " )
					 .toArray());
		}
		
	}
	
	private static String getExpression(Mod123 mod123, IModelScript<Mod123Key> script) {
		Mod123Declaration dec = Mod123Declaration.getInstance(mod123);
		JSONArray array = new JSONArray();
		for (Mod123Key key : script.getKeys() ) {
			IMod123KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				Mod123MVELExpressionContext mvelCtx = new Mod123MVELExpressionContext(keyDAO.getExpression(),mod123);
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
				for (Mod123Key k : mvelCtx.getKeys()) {
					keysArray.put(k);
					keysValues.put( DEC2.format(mod123.getAmount(k)));
				}
				info.put(IJsonNames.KEYS, keysArray);
				info.put(IJsonNames.KEY_VALUES, keysValues);
				array.put(info);
			}
			
		}
		return array.toString();
	}
	
	private static String getComputeKey(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script,IMod123KeyDAO keyDAO) {
		Mod123Declaration dec = Mod123Declaration.getInstance(mod123);
		StringBuilder buf = new StringBuilder();
		for (Mod123Key key : script.getKeys() ) {
			if (key != null) {
				if (Arrays.stream(dec.getSamePeriodExplainKeys()).anyMatch(k -> k == key)) {
					buf.append(dec.getSamePeriodExplain( ctx, mod123, key));
					return buf.toString();
				} else {
					Mod123MVELContext mvelCtx = new Mod123MVELContext(mod123); 
					mvelCtx.put("mod", mod123);
					mvelCtx.put("periodModels", FiscalModelDAO.getSamePeriodModels(ctx, mod123, Mod123::new).collect(Collectors.toCollection(LinkedList::new)));
					mvelCtx.put("lastPeriodModels", FiscalModelDAO.getLastPeriodModels(ctx, mod123, Mod123::new).collect(Collectors.toCollection(LinkedList::new)));
					mvelCtx.put("models", Mod123DAO.getMod123s(ctx, ctx.getDomainId()).collect(Collectors.toCollection(LinkedList::new)));
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

