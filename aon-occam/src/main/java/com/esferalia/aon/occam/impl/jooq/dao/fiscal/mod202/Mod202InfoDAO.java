package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.AccountingBreakdownJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod202InfoDAO {
	
	private static final DecimalFormat DEC2 = new DecimalFormat("#,##0.00");		

	private Mod202InfoDAO() {
		
	}

	public static String getInfo(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey) {
		Mod202Declaration dec = Mod202Declaration.getInstance(mod202);
		return  Stream.of(script)
			.map(IModelScript::getKeys)
			.filter( Objects::nonNull )
			.flatMap(Arrays::stream)
			.filter( Objects::nonNull )
			.map( dec::getKey )
			.peek( keyDAO -> System.out.println( keyDAO ) )
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
						return Objects.requireNonNullElse(getExpression(mod202, script),new JSONArray()).toString();
					}
					
					@Override 
					public String visitComputeKey() {
						return keyDAO.info( ctx, mod202);
					}
					
					@Override 
					public String visitModelInvoiceIrpfBreakdown() {
						return AonStringUtils.EMPTY;
					}
					
					@Override 
					public String visitActAccount() {
						return Objects.requireNonNullElse(
							getAccountInfoInfo(ctx, mod202, script,keyDAO)
								.map( AccountingBreakdownJSON::toJSON)
								.collect(JSONArray::new,JSONArray::put,JSONArray::put)
								,new JSONArray()).toString();
					}
					
				})
			)
			.collect(Collectors.joining());
	}
	
	private static Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod202 mod, IModelScript<Mod202Key> script,IMod202KeyDAO keyDAO) {
		Mod202Declaration dec = Mod202Declaration.getInstance(mod);
		return dec.getAccountInfoInfo(ctx, mod, script, keyDAO);
	}
	
	private static String getExpression(Mod202 mod202, IModelScript<Mod202Key> script) {
		Mod202Declaration dec = Mod202Declaration.getInstance(mod202);
		JSONArray array = new JSONArray();
		for (Mod202Key key : script.getKeys() ) {
			IMod202KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				Mod202MVELExpressionContext mvelCtx = new Mod202MVELExpressionContext(keyDAO.getExpression(),mod202);
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
				for (Mod202Key k : mvelCtx.getKeys()) {
					keysArray.put(k);
					keysValues.put( DEC2.format(mod202.getAmount(k)));
				}
				info.put(IJsonNames.KEYS, keysArray);
				info.put(IJsonNames.KEY_VALUES, keysValues);
				array.put(info);
			}
			
		}
		return array.toString();
	}
	
	private static class Mod202MVELExpressionContext extends Mod202MVELContext {
		
		private String expression;
		public Mod202MVELExpressionContext(String expression, final Mod202 mod202) {
			super(mod202);
			this.expression = expression;
		}
		
		@Override
		public Object get(Object keyString) {
			Mod202Key key = Mod202Key.valueOf(keyString.toString());
			keys.add(key);
			String anchor = "{" + (keys.size() - 1) + "}";
			expression = AonStringUtils.replace(expression, keyString.toString(), anchor);
			return super.get(keyString);
		}
		
		@SuppressWarnings("unused")
		// Used on templates
		public String format(Double amount) {
			return DEC2.format(amount);
		}
		
		public String getExpression() {
			return expression;
		}
		public String getFormula() {
			 return MessageFormat.format(expression,keys.stream().map(Mod202Key::getBoxFormatted).toArray());
		}
		public List<Mod202Key> getKeys() {
			return keys;
		}
		public String getResult() {
			 return MessageFormat.format(expression,keys.stream()
					 .map(k-> " ["+DEC2.format(mod202.getAmount(k)) +"] " )
					 .toArray());
		}
	}
	
//	private static String getComputeKey(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script,IMod202KeyDAO keyDAO) {
//		return Stream.of( script )
//			.map(IModelScript::getKeys)
//			.filter( Objects::nonNull )
//			.flatMap(Arrays::stream)
//			.filter( Objects::nonNull )
//			.map(key -> keyDAO.info( ctx, mod202) )
//			.collect(Collectors.joining());
//	}
	
}
