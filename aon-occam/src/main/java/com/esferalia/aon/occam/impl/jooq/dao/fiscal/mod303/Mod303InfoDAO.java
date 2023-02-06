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
						return getComputeKey(ctx, mod303, script, keyDAO);
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
						if (Arrays.stream(script.getKeys()).filter(Objects::nonNull).anyMatch(dec::isProrrated)) {
							if (mod303.hasProrate() && mod303.isSpecialProrate()) {
								vt.setProrrateQuota(AonMathUtils.round(vt.getDeductibleQuota()));	
							}
						if (Mod303Declaration.mustApplyProrrate(mod303, vt)) {
							vt.setProrrated(true);
							vt.setSpecialProrrate(mod303.isSpecialProrate());
							vt.setProrratePercent( mod303.getProratePercent());	
							vt.setProrrateQuota(AonMathUtils.round(vt.getDeductibleQuota() * mod303.getProratePercent() / 100));
						} 
						}
						return vt;
					}
				})
			)
			.findFirst()
			.orElse(null);
	}
	
	public static Stream<VatContext> getModelInvoicesInfo(AONContext ctx, final Mod303 mod303, Mod303Key key) {
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		return getModelInvoicesInfo(ctx, mod303, dec.getKey(key)); 
	}
	
	private static Stream<VatContext> getModelInvoicesInfo(AONContext ctx, final Mod303 mod303, IMod303KeyDAO keyDAO) {
		if (mod303.isManualDeclaration() || !mod303.hasInvoicesBound()) {
			return VATDAO.getVatBreakdown(ctx, mod303)
					.filter( br ->  keyDAO.acceptValue(mod303, br));
		}
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
		Mod303Declaration dec = Mod303Declaration.getInstance(mod303);
		StringBuilder buf = new StringBuilder();
		for (Mod303Key key : script.getKeys() ) {
			if (key != null) {
				if (dec.getRegularizationKey() != null && dec.getRegularizationKey() == key) {
					buf.append(  dec.getRegularizationExplain( ctx, mod303, key));
					return buf.toString();
				} else if (Arrays.stream(dec.getCompensationExplainKeys()).anyMatch(k -> k == key)) {
					buf.append(  dec.getCompensationExplain( ctx, mod303, key));
					return buf.toString();
				} else if (Arrays.stream(dec.getSamePeriodExplainKeys()).anyMatch(k -> k == key)) {
					buf.append(  dec.getSamePeriodExplain( ctx, mod303, key));
					return buf.toString();
				} else {
					Mod303MVELContext mvelCtx = new Mod303MVELContext(mod303); 
					mvelCtx.put("mod", mod303);
					mvelCtx.put("periodModels", Mod303DAO.getSamePeriodEffectiveModels(ctx, mod303).collect(Collectors.toCollection(LinkedList::new)));
					mvelCtx.put("lastPeriodModels", Mod303DAO.getLastPeriodEffectiveModels(ctx, mod303).collect(Collectors.toCollection(LinkedList::new)));
					mvelCtx.put("models", Mod303DAO.getMod303s(ctx, ctx.getDomainId()).collect(Collectors.toCollection(LinkedList::new)));
					String template = keyDAO.getTemplate();
					if (AonStringUtils.isNotBlank( template )) {
						Object result = TemplateRuntime.eval(template, mvelCtx);
						buf.append(result != null ? result.toString() : null);
					}
				}
			}
		}
		return new JSONObject(buf.toString()).toString();
	}

	private static String getDiffInvoicesInfo(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script,IMod303KeyDAO keyDAO) {
		StringBuilder buf = new StringBuilder();
		buf.append("<div style=\"" +
				  "padding-right: 15px; padding-left: 15px; margin-right: auto; "
				+ "margin-left: auto; width:100%; display: flex;flex-wrap: wrap; "
				+ "justify-content: center; box-sizing: border-box"
				+ "\">");
		for (Mod303Key key : script.getKeys() ) {
			if (key != null && key.isDiffEnabled()) {
				String styledTag = "<{0} style = \"{1}\">"; 
				String marginTop = "margin-top: 20px;";
				String border = "border: solid gray 0.5px;";
				String fontLarger = "font-size: 1.2em;";
				String textCenter = "text-align: center;";
				String textRight= "text-align: right;";
				String paddingLeft = "padding-left: 15px;";
				String width400 = "width: 400px;";
				String width150 = "width: 150px;";
				String bold = "font-weight: bold;";
				String blockCenter = "margin-left: auto;margin-right: auto;";
				
				buf.append("<div style=\"" 
					+ "border-radius: 4px; background: #fff; box-shadow: 0 6px 10px rgba(0,0,0,.08), 0 0 6px rgba(0,0,0,.05);"
					+ "transition: .3s transform cubic-bezier(.155,1.105,.295,1.12),.3s box-shadow,.3s -webkit-transform cubic-bezier(.155,1.105,.295,1.12);"
					+ "padding: 4px 5px 5px 10px; margin: 20px 10px 10px 10px; cursor: pointer;"
					+ "flex: 0 1 40%; min-height: 120px; min-width: 350px;"
					+ "\">")
					.append( MessageFormat.format(styledTag, "table cellspacing=\"0\"",  blockCenter+marginTop+border ) )
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+bold+fontLarger+border) )
							.append("Casilla " + key.getBoxFormatted())
						.append("</td>")
					.append("</tr>")
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td",  bold+border+width400) )
							.append("(A) Total acumulado desde inicio ejercicio")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border) )
							.append(DEC2.format(mod303.getAccumulatedAmount(key)))
						.append("</td>")
					.append("</tr>");
					
					double keyTotal = Mod303DAO.getPreviousEffectiveModels(ctx, mod303)
							.map( fm ->  {
								buf.append("<tr>")
									.append( MessageFormat.format(styledTag, "td", paddingLeft+border) )
										.append("Resultado del modelo " + fm.getModelFullName())
									.append("</td>")
									.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
										.append(DEC2.format(fm.getAmount(key)))
									.append("</td>")
								.append("</tr>");
								return fm;
							})
							.mapToDouble(fm -> fm.getAmount(key))
							.sum();
					
					buf.append("<tr>")
						.append( MessageFormat.format(styledTag, "td",  bold+border) )
							.append("(B) Total declarado ")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+border) )
							.append(DEC2.format(keyTotal))
						.append("</td>")
					.append("</tr>");

					buf.append("<tr>")
						.append( MessageFormat.format(styledTag, "td",  bold+fontLarger+border) )
							.append("(A - B) Total a declarar")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+fontLarger+border) )
							.append(DEC2.format(AonMathUtils.round(mod303.getAccumulatedAmount(key) - keyTotal)))
						.append("</td>")
					.append("</tr>");
				
				buf.append("</table>");
				buf.append("</div>");
			}
		}
		buf.append("</div>");
		return buf.toString();
	}
}

