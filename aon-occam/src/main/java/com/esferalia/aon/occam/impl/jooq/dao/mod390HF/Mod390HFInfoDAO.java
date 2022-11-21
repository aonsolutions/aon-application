package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HFInfoDAO extends FiscalModelDAO {
	private static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");		
	
	private Mod390HFInfoDAO() {
	}
	
	public static String getInfo(AONContext ctx, Mod390HF mod, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey) {
		Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
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
						return Objects.requireNonNullElse(getExpression(mod, script),new JSONArray()).toString();
					}
					
					@Override 
					public String visitComputeKey() {
						return new JSONObject( getComputeKey(ctx, mod, script, keyDAO) ).toString();
					}
					
					@Override 
					public String visitDiffInvoice() {
						return getDiffInvoicesInfo(ctx, mod, script, keyDAO).toString();
					}
					
					@Override 
					public String visitProrratedModelInvoiceVatBreakdown() {
						return Objects.requireNonNullElse(
								getModelInvoicesInfo(ctx, mod,keyDAO)
									.map( vt -> checkProrrated(mod, vt))
									.map( VatContextJSON::toJSON)
									.collect(JSONArray::new,JSONArray::put,JSONArray::put)
									,new JSONArray()).toString();
					}
					@Override 
					public String visitModelOutVatAccrualInvoice() {
						return Objects.requireNonNullElse(
								getModelOutVatAccrualInvoicesInfo(ctx, mod,keyDAO)
									.map( VatContextJSON::toJSON)
									.collect(JSONArray::new,JSONArray::put,JSONArray::put)
									,new JSONArray()).toString();
					}
					
					@Override 
					public String visitModelInVatAccrualInvoice() {
						return Objects.requireNonNullElse(
								getModelInVatAccrualInvoicesInfo(ctx, mod,keyDAO)
									.map( vt -> checkProrrated(mod, vt))
									.map( VatContextJSON::toJSON)
									.collect(JSONArray::new,JSONArray::put,JSONArray::put)
									,new JSONArray()).toString();
					}

					
					
					@Override 
					public String visitModelInvoiceVatBreakdown() {
						return Objects.requireNonNullElse(
							getModelInvoicesInfo(ctx, mod,keyDAO)
								.map( VatContextJSON::toJSON)
								.collect(JSONArray::new,JSONArray::put,JSONArray::put)
								,new JSONArray()).toString();
					}
					
					private VatContext checkProrrated(Mod390HF mod, VatContext vt) {
						if (Mod390HFDeclaration.mustApplyProrrate(mod, vt) &&
								Arrays.stream(script.getKeys())
									.filter(Objects::nonNull)
									.anyMatch(dec::isProrrated)) {
							vt.setProrrated(true);
							vt.setSpecialProrrate(mod.isSpecialProrate());
							vt.setProrratePercent( mod.getProratePercent());	
							vt.setProrrateQuota(AonMathUtils.round(vt.getDeductibleQuota() * mod.getProratePercent() / 100));
						}
						return vt;
					}
				})
			)
			.findFirst()
			.orElse(null);
	}
	
	private static Stream<VatContext> getModelInvoicesInfo(AONContext ctx, final Mod390HF mod, IMod390KeyDAO keyDAO) {
		return VATDAO.getModelVatBreakdown(ctx, mod)
			.filter( br ->  keyDAO.acceptValue(mod, br));
	}
	
	private static Stream<VatContext> getModelOutVatAccrualInvoicesInfo(AONContext ctx, final Mod390HF mod, IMod390KeyDAO keyDAO) {
		return VATDAO.getModelAccrualInvoices(ctx, mod)
			.filter( VatContext::isSales )	
			.filter( br ->  keyDAO.acceptValue(mod, br));
	}
		
	private static Stream<VatContext> getModelInVatAccrualInvoicesInfo(AONContext ctx, final Mod390HF mod, IMod390KeyDAO keyDAO) {
		return VATDAO.getModelAccrualInvoices(ctx, mod)
				.filter( VatContext::isNotSales )	
				.filter( br ->  keyDAO.acceptValue(mod, br));
	}
	
	
	private static class Mod390HFMVELContext extends com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFMVELContext {
		public Mod390HFMVELContext(final Mod390HF mod) {
			super(mod);
			for (String keyValue : mod.getMap().keySet()) {
				Mod390Key modKey = Mod390Key.getKey(keyValue);
				if (modKey != null) {
					FiscalModelDetail detail = mod.getMap().get(keyValue);
					put(modKey.toString(), detail==null?0.0:detail.getAmount());
				}
			}
		}
	}

	private static class Mod390HFMVELExpressionContext extends Mod390HFMVELContext {
		
		private final LinkedList<Mod390Key> keys = new LinkedList<>();
		private String expression;
		public Mod390HFMVELExpressionContext(String expression, final Mod390HF mod) {
			super(mod);
			this.expression = expression;
		}
		
		@Override
		public Object get(Object keyString) {
			Mod390Key key = Mod390Key.valueOf(keyString.toString());
			keys.add(key);
			String anchor = "{" + (keys.size() - 1) + "}";
			expression = AonStringUtils.replace(expression, keyString.toString(), anchor);
			return super.get(keyString);
		}
		
		public String getExpression() {
			return expression;
		}
		public String getFormula() {
			 return MessageFormat.format(expression,keys.stream().map(Mod390Key::getBoxFormatted).toArray());
		}
		public List<Mod390Key> getKeys() {
			return keys;
		}

		public String format(Double amount) {
			return DEC2.format(amount);
		}

		public String getResult() {
			 return MessageFormat.format(expression,
					 keys.stream()
					 .map(k -> " ["+ format(getMod390HF().getAmount(k)) +"] " )
					 .toArray());
		}
		
	}
	
	private static String getExpression(Mod390HF mod, IModelScript<Mod390Key> script) {
		Mod390HFDeclaration dec = Mod390HFDeclaration.getInstance(mod);
		JSONArray array = new JSONArray();
		for (Mod390Key key : script.getKeys() ) {
			IMod390KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				Mod390HFMVELExpressionContext mvelCtx = new Mod390HFMVELExpressionContext(keyDAO.getExpression(),mod);
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
				for (Mod390Key k : mvelCtx.getKeys()) {
					keysArray.put(k);
					keysValues.put( DEC2.format(mod.getAmount(k)));
				}
				info.put(IJsonNames.KEYS, keysArray);
				info.put(IJsonNames.KEY_VALUES, keysValues);
				array.put(info);
			}
			
		}
		return array.toString();
	}
	
	private static String getComputeKey(AONContext ctx, Mod390HF mod, IModelScript<Mod390Key> script,IMod390KeyDAO keyDAO) {
		Mod390HFMVELContext mvelCtx = new Mod390HFMVELContext(mod); 
		mvelCtx.put("mod", mod);
		mvelCtx.put("periodModels", FiscalModelDAO.getSamePeriodModels(ctx, mod, Mod390HF::new).collect(Collectors.toCollection(LinkedList::new)));
		mvelCtx.put("lastPeriodModels", FiscalModelDAO.getLastPeriodModels(ctx, mod, Mod390HF::new).collect(Collectors.toCollection(LinkedList::new)));
		mvelCtx.put("models", Mod390HFDAO.getMod390HFs(ctx, ctx.getDomainId()).collect(Collectors.toCollection(LinkedList::new)));
		StringBuilder buf = new StringBuilder();
		for (Mod390Key key : script.getKeys() ) {
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

	private static JSONObject getDiffInvoicesInfo(AONContext ctx, Mod390HF mod, IModelScript<Mod390Key> script,IMod390KeyDAO keyDAO) {
		JSONObject json = new JSONObject();
		JSONArray messages = new JSONArray();
		for (Mod390Key key : script.getKeys() ) {
			if (key != null) {
				messages.put("\u2022 Resultado de la casilla " + key.getBoxFormatted());
				messages.put(" - (A) Total acumulado " + DEC2.format(mod.getAccumulatedAmount(key)));
				double keyTotal = FiscalModelDAO.getPreviousModels(ctx, mod, Mod390HF::new)
					.map( fm ->  putMessage(fm,messages," - >>>>> Resultado del modelo " + fm.getModelFullName() + " " + DEC2.format(fm.getAmount(key))))
					.mapToDouble(fm -> fm.getAmount(key))
					.sum();
				messages.put("- (B) Total declarado " + DEC2.format(keyTotal));
				messages.put("\u2022 Total a declarar (A-B) ->	" + DEC2.format(AonMathUtils.round(mod.getAccumulatedAmount(key) - keyTotal)));
				messages.put(" ------------------------------ ");
			}
		}
		return json.put( IJsonNames.MESSAGES, messages) ;
	}

	private static Mod390HF putMessage(Mod390HF mod,JSONArray messages, String message) {
		messages.put(message);
		return mod;
	}
}

