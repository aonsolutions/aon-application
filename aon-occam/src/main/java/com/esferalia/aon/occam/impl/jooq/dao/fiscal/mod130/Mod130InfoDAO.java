package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod130InfoDAO extends FiscalModelDAO {
	private static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");		

	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod130 mod,IModelScript<Mod130Key> script,IMod130KeyDAO keyDAO);
	}

	public static String getInfo(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) {
		Mod130Declaration dec = Mod130Declaration.getInstance(mod130);
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
						return Objects.requireNonNullElse(getExpression(mod130, script),new JSONArray()).toString();
					}
					
					@Override 
					public String visitComputeKey() {
						return getComputeKey(ctx, mod130, script, keyDAO);
					}
					
					@Override 
					public String visitModelInvoiceIrpfBreakdown() {
						return Objects.requireNonNullElse(
							getInvoicesInfo(ctx, mod130, script,keyDAO)
								.map( br -> {
									double participationPercent = mod130.getAmount(Mod130Key.P1);
									double participationQuota = AonMathUtils.round( br.getDeductibleQuota() * participationPercent / 100 );
									br.setParticipationPercent(participationPercent)
									  .setParticipationQuota(participationQuota);
									return br;
								})
								.map( IrpfBreakdownJSON::toJSON)
								.collect(JSONArray::new,JSONArray::put,JSONArray::put)
								,new JSONArray()).toString();
					}

					@Override 
					public String visitActAccount() {
						return Objects.requireNonNullElse(
								getAccountInfoInfo(ctx, mod130, script,keyDAO)
									.map( AccountingBreakdownJSON::toJSON)
									.collect(JSONArray::new,JSONArray::put,JSONArray::put)
									,new JSONArray()).toString();
					}
					
				})
			)
			.findFirst()
			.orElse(null);
	}

	private static class Mod130MVELExpressionContext extends Mod130MVELContext {
		
		private String expression;
		public Mod130MVELExpressionContext(String expression, final Mod130 mod130) {
			super(mod130);
			this.expression = expression;
		}
		
		@Override
		public Object get(Object keyString) {
			Mod130Key key = Mod130Key.valueOf(keyString.toString());
			keys.add(key);
			String anchor = "{" + (keys.size() - 1) + "}";
			expression = AonStringUtils.replace(expression, keyString.toString(), anchor);
			return super.get(keyString);
		}
		
		public String getExpression() {
			return expression;
		}
		public String getFormula() {
			 return MessageFormat.format(expression,keys.stream().map(Mod130Key::getBoxFormatted).toArray());
		}
		public List<Mod130Key> getKeys() {
			return keys;
		}
		public String getResult() {
			 return MessageFormat.format(expression,keys.stream()
					 .map(k-> " ["+DEC2.format(mod130.getAmount(k)) +"] " )
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
	
	
	private static String getExpression(Mod130 mod130, IModelScript<Mod130Key> script) {
		Mod130Declaration dec = Mod130Declaration.getInstance(mod130);
		JSONArray array = new JSONArray();
		for (Mod130Key key : script.getKeys() ) {
			IMod130KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				Mod130MVELExpressionContext mvelCtx = new Mod130MVELExpressionContext(keyDAO.getExpression(),mod130);
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
				for (Mod130Key k : mvelCtx.getKeys()) {
					keysArray.put(k);
					keysValues.put( DEC2.format(mod130.getAmount(k)));
				}
				info.put(IJsonNames.KEYS, keysArray);
				info.put(IJsonNames.KEY_VALUES, keysValues);
				array.put(info);
			}
			
		}
		return array.toString();
	}

	private static String getComputeKey(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script,IMod130KeyDAO keyDAO) {
		return Stream.of( script )
			.map(IModelScript::getKeys)
			.filter( Objects::nonNull )
			.flatMap(Arrays::stream)
			.filter( Objects::nonNull )
			.map(key -> keyDAO.info( ctx, mod130) )
			.findFirst()
			.orElse(null);
	}

	private static Stream<IrpfBreakdown> getInvoicesInfo(AONContext ctx, final Mod130 mod130 , final IModelScript<Mod130Key> script, IMod130KeyDAO keyDAO) {
		return IRPFDAO.getOutputInvoicesIrpfBreakdown(ctx, mod130)
			.filter(br -> keyDAO.acceptValue(mod130, br ));
	}
	
	private static Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod130 mod, IModelScript<Mod130Key> script,IMod130KeyDAO keyDAO) {
		Mod130Declaration dec = Mod130Declaration.getInstance(mod);
		return dec.getAccountInfoInfo(ctx, mod, script, keyDAO);
	}
	
}
