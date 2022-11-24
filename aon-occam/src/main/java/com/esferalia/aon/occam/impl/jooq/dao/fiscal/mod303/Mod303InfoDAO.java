package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
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
import com.esferalia.aon.occam.api.json.VatContextJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303InfoDAO extends FiscalModelDAO {
	private static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");		
	
	private Mod303InfoDAO() {
	}
	
	public static String getInfo(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
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
					@Override public String visitDiffInAccrualInvoice() {return visitNone(); }
					@Override public String visitDiffOutAccrualInvoice() {return visitNone(); }
					@Override public String visitSalary() {return visitNone(); }
					@Override public String visitDiffSalary() {return visitNone(); }
					@Override public String visitActAccount() {return visitNone(); }
					@Override public String visitTitle() {return visitNone(); }
					@Override public String visitIrpfActivity() {return visitNone(); }
					@Override public String visitCorporate() {return visitNone(); }
					@Override public String visitModelSalaryIrpfBreakdown() {return visitNone(); }
					@Override public String visitModelInvoiceIrpfBreakdown() {return visitNone(); }
					
					@Override 
					public String visitNone()    {
						return AonStringUtils.EMPTY; 
					} 
					
					@Override 
					public String visitCompute() {
						return Objects.requireNonNullElse(getExpression(mod303, script),new JSONArray()).toString();
					}
					
					@Override 
					public String visitComputeKey() {
						return new JSONObject( getComputeKey(ctx, mod303, script, keyDAO) ).toString();
					}
					
					@Override 
					public String visitDiffInvoice() {
						return getDiffInvoicesInfo(ctx, mod303, script, keyDAO).toString();
					}
					
					@Override 
					public String visitProrratedModelInvoiceVatBreakdown() {
						return Objects.requireNonNullElse(
								getModelInvoicesInfo(ctx, mod303,keyDAO)
									.map( vt -> checkProrrated(mod303, vt))
									.map( VatContextJSON::toJSON)
									.collect(JSONArray::new,JSONArray::put,JSONArray::put)
									,new JSONArray()).toString();
					}
					@Override 
					public String visitModelOutVatAccrualInvoice() {
						return Objects.requireNonNullElse(
								getModelOutVatAccrualInvoicesInfo(ctx, mod303,keyDAO)
									.map( VatContextJSON::toJSON)
									.collect(JSONArray::new,JSONArray::put,JSONArray::put)
									,new JSONArray()).toString();
					}
					
					@Override 
					public String visitModelInVatAccrualInvoice() {
						return Objects.requireNonNullElse(
								getModelInVatAccrualInvoicesInfo(ctx, mod303,keyDAO)
									.map( vt -> checkProrrated(mod303, vt))
									.map( VatContextJSON::toJSON)
									.collect(JSONArray::new,JSONArray::put,JSONArray::put)
									,new JSONArray()).toString();
					}

					
					
					@Override 
					public String visitModelInvoiceVatBreakdown() {
						return Objects.requireNonNullElse(
							getModelInvoicesInfo(ctx, mod303,keyDAO)
								.map( VatContextJSON::toJSON)
								.collect(JSONArray::new,JSONArray::put,JSONArray::put)
								,new JSONArray()).toString();
					}
					
					private VatContext checkProrrated(Mod303 mod303, VatContext vt) {
						if (Mod303Declaration.mustApplyProrrate(mod303, vt) &&
								Arrays.stream(script.getKeys())
									.filter(Objects::nonNull)
									.anyMatch(dec::isProrrated)) {
							vt.setProrrated(true);
							vt.setSpecialProrrate(mod303.isSpecialProrate());
							vt.setProrratePercent( mod303.getProratePercent());	
							vt.setProrrateQuota(AonMathUtils.round(vt.getDeductibleQuota() * mod303.getProratePercent() / 100));
						}
						return vt;
					}
				})
			)
			.findFirst()
			.orElse(null);
	}
	
	private static Stream<VatContext> getModelInvoicesInfo(AONContext ctx, final Mod303 mod303, IMod303KeyDAO keyDAO) {
		return VATDAO.getModelVatBreakdown(ctx, mod303)
			.filter( br ->  keyDAO.acceptValue(mod303, br));
	}
	
	private static Stream<VatContext> getModelOutVatAccrualInvoicesInfo(AONContext ctx, final Mod303 mod303, IMod303KeyDAO keyDAO) {
		return VATDAO.getModelAccrualInvoices(ctx, mod303)
			.filter( VatContext::isSales )	
			.filter( br ->  keyDAO.acceptValue(mod303, br));
	}
		
	private static Stream<VatContext> getModelInVatAccrualInvoicesInfo(AONContext ctx, final Mod303 mod303, IMod303KeyDAO keyDAO) {
		return VATDAO.getModelAccrualInvoices(ctx, mod303)
				.filter( VatContext::isNotSales )	
				.filter( br ->  keyDAO.acceptValue(mod303, br));
	}
	
	
	private static class Mod303MVELContext extends com.esferalia.aon.occam.impl.jooq.dao.Mod303MVELContext {
		public Mod303MVELContext(final Mod303 mod303) {
			super(mod303);
			for (String keyValue : mod303.getMap().keySet()) {
				Mod303Key mod303Key = Mod303Key.getKey(keyValue);
				if (mod303Key != null) {
					FiscalModelDetail detail = mod303.getMap().get(keyValue);
					put(mod303Key.toString(), detail==null?0.0:detail.getAmount());
				}
			}
		}
	}

	private static class Mod303MVELExpressionContext extends Mod303MVELContext {
		
		private final LinkedList<Mod303Key> keys = new LinkedList<>();
		private String expression;
		public Mod303MVELExpressionContext(String expression, final Mod303 mod303) {
			super(mod303);
			this.expression = expression;
		}
		
		@Override
		public Object get(Object keyString) {
			Mod303Key key = Mod303Key.valueOf(keyString.toString());
			keys.add(key);
			String anchor = "{" + (keys.size() - 1) + "}";
			expression = AonStringUtils.replace(expression, keyString.toString(), anchor);
			return super.get(keyString);
		}
		
		public String getExpression() {
			return expression;
		}
		public String getFormula() {
			 return MessageFormat.format(expression,keys.stream().map(Mod303Key::getBoxFormatted).toArray());
		}
		public List<Mod303Key> getKeys() {
			return keys;
		}

		public String format(Double amount) {
			return DEC2.format(amount);
		}

		public String getResult() {
			 return MessageFormat.format(expression,
					 keys.stream()
					 .map(k -> " ["+ format(getMod303().getAmount(k)) +"] " )
					 .toArray());
		}
		
	}
	
	private static String getExpression(Mod303 mod303, IModelScript<Mod303Key> script) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		JSONArray array = new JSONArray();
		for (Mod303Key key : script.getKeys() ) {
			IMod303KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				Mod303MVELExpressionContext mvelCtx = new Mod303MVELExpressionContext(keyDAO.getExpression(),mod303);
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
				for (Mod303Key k : mvelCtx.getKeys()) {
					keysArray.put(k);
					keysValues.put( DEC2.format(mod303.getAmount(k)));
				}
				info.put(IJsonNames.KEYS, keysArray);
				info.put(IJsonNames.KEY_VALUES, keysValues);
				array.put(info);
			}
			
		}
		return array.toString();
	}
	
	private static String getComputeKey(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script,IMod303KeyDAO keyDAO) {
		Mod303MVELContext mvelCtx = new Mod303MVELContext(mod303); 
		mvelCtx.put("mod", mod303);
		mvelCtx.put("periodModels", FiscalModelDAO.getSamePeriodModels(ctx, mod303, Mod303::new).collect(Collectors.toCollection(LinkedList::new)));
		mvelCtx.put("lastPeriodModels", FiscalModelDAO.getLastPeriodModels(ctx, mod303, Mod303::new).collect(Collectors.toCollection(LinkedList::new)));
		mvelCtx.put("models", Mod303DAO.getMod303s(ctx, ctx.getDomainId()).collect(Collectors.toCollection(LinkedList::new)));
		StringBuilder buf = new StringBuilder();
		for (Mod303Key key : script.getKeys() ) {
			if (key != null) {
				String template = keyDAO.getTemplate();
				if (AonStringUtils.isNotBlank( template )) {
					Object result = TemplateRuntime.eval(template, mvelCtx);
					buf.append(result != null ? result.toString() : null);
				}
			}
		}
		return buf.toString();
	}

	private static JSONObject getDiffInvoicesInfo(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script,IMod303KeyDAO keyDAO) {
		JSONObject json = new JSONObject();
		JSONArray messages = new JSONArray();
		for (Mod303Key key : script.getKeys() ) {
			if (key != null && key.isDiffEnabled()) {
				messages.put("\u2022 Resultado de la casilla " + key.getBoxFormatted());
				messages.put(" - (A) Total acumulado " + DEC2.format(mod303.getAccumulatedAmount(key)));
				double keyTotal = FiscalModelDAO.getPreviousModels(ctx, mod303, Mod303::new)
					.map( fm ->  putMessage(fm,messages," - >>>>> Resultado del modelo " + fm.getModelFullName() + " " + DEC2.format(fm.getAmount(key))))
					.mapToDouble(fm -> fm.getAmount(key))
					.sum();
				messages.put("- (B) Total declarado " + DEC2.format(keyTotal));
				messages.put("\u2022 Total a declarar (A-B) ->	" + DEC2.format(AonMathUtils.round(mod303.getAccumulatedAmount(key) - keyTotal)));
				messages.put(" ------------------------------ ");
			}
		}
		return json.put( IJsonNames.MESSAGES, messages) ;
	}

	private static Mod303 putMessage(Mod303 mod303,JSONArray messages, String message) {
		messages.put(message);
		return mod303;
	}
}

