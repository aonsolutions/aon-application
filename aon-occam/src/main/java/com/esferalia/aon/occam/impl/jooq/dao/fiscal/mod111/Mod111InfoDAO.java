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

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.IrpfBreakdownJSON;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.impl.jooq.dao.IRPFFormatter;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod111InfoDAO {
	public static final DecimalFormat DEC2 = new DecimalFormat("#,###.##");		
	private static final String INFO_MSG = "<pre class='aon_margin_bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private enum Mod111KeyInfoDAO {
		 NONE {
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return MessageFormat.format(INFO_MSG, NONE_INFO);
			}
		}
		,INVOICE {
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return Objects.requireNonNullElse(
						getInvoicesInfo(ctx, mod,keyDAO)
							.map( IrpfBreakdownJSON::toJSON)
							.collect(JSONArray::new,JSONArray::put,JSONArray::put)
							,new JSONArray()).toString(); 
			}
		} 
		,MODEL_INVOICE_IRPF_BREAKDOWN {
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return Objects.requireNonNullElse(
					getModelInvoicesInfo(ctx, mod,keyDAO)
						.map( IrpfBreakdownJSON::toJSON)
						.collect(JSONArray::new,JSONArray::put,JSONArray::put)
						,new JSONArray()).toString(); 
			}
		}
		,SALARY {
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return Objects.requireNonNullElse(
					getSalaryInfo(ctx, mod,keyDAO)
						.map( IrpfBreakdownJSON::toJSON)
						.collect(JSONArray::new,JSONArray::put,JSONArray::put)
						,new JSONArray()).toString(); 
			}
		}
		,SALARY_IN_KIND {
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return Objects.requireNonNullElse(
					getSalaryInfo(ctx, mod,keyDAO)
						.map( IrpfBreakdownJSON::toJSON)
						.collect(JSONArray::new,JSONArray::put,JSONArray::put)
						,new JSONArray()).toString(); 
			}
		}
		,COMPUTE {
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return Objects.requireNonNullElse(getExpression(mod, script),new JSONArray()).toString(); 
			}
		}
		// **********************************************
		// **********************************************
		// **********************************************
		,DIFF_INVOICE{
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO)); 
			}
		}
		,DIFF_SALARY{
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return MessageFormat.format(INFO_MSG, getDiffSalaryInfo(ctx, mod, script,keyDAO));
			}
		}
		;
		
		abstract String obtain(AONContext ctx, Mod111 mod,IModelScript<Mod111Key> script,IMod111KeyDAO keyDAO);
	}
	
	public static String getInfo(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) {
		Mod111KeyInfoDAO k = Mod111KeyInfoDAO.valueOf(infoKey.toString());
		Mod111Declaration dec = Mod111Declaration.getInstance(mod111);
		return Arrays.stream(dec.getKeys())
			.filter( keyDAO -> keyDAO.getKey() == script.getKeys()[0])
			.map(keyDAO -> k.obtain(ctx, mod111, script, keyDAO))
			.findFirst()
			.orElse(null);
	}
	
	private static Stream<IrpfBreakdown> getModelInvoicesInfo(AONContext ctx, final Mod111 mod111, IMod111KeyDAO keyDAO) {
		return IRPFDAO.getModelInputInvoicesIrpfBreakdown(ctx, mod111)
			.filter( br ->  keyDAO.acceptValue(mod111, br));
	}
	
	private static Stream<IrpfBreakdown> getInvoicesInfo(AONContext ctx, final Mod111 mod111, IMod111KeyDAO keyDAO) {
		return IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod111)
			.filter( br ->  keyDAO.acceptValue(mod111, br));
	}

	private static Stream<IrpfBreakdown> getSalaryInfo(AONContext ctx, final Mod111 mod111, IMod111KeyDAO keyDAO) {
		return IRPFDAO.getSalaryIrpfBreakdown(ctx, mod111)
			.filter( br ->  keyDAO.acceptValue(mod111, br) );
	}

	private static class Mod111MVELExpressionContext extends LinkedHashMap<String, Object> {
		
		private static final long serialVersionUID = 7905148919414628166L;
		
		private String expression;
		private Mod111 mod111;
		private final LinkedList<Mod111Key> keys = new LinkedList<>();

		public Mod111MVELExpressionContext(String expression, final Mod111 mod111) {
			super();
			this.expression = expression;
			this.mod111 = mod111;
			for (String keyValue : mod111.getMap().keySet()) {
				Mod111Key mod111Key = Mod111Key.getKey(keyValue);
				if (mod111Key != null) {
					FiscalModelDetail detail = mod111.getMap().get(keyValue);
					put(mod111Key.toString(), detail==null?0.0:detail.getAmount());
				}
			}
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
		public boolean equals(Object arg0) {
			return super.equals(arg0);
		}
	}
	
	private static String getExpression(Mod111 mod111, IModelScript<Mod111Key> script) {
		StringBuilder buf = new StringBuilder();
		int headerLength = 100;
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.center("DETALLE DEL C\u00C1LCULO", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.repeat("-", headerLength)));
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
					.put(IJsonNames.RESULT, mvelCtx.getResult())
					.put(IJsonNames.VALUE, ret)
					;
				JSONArray keysArray = new JSONArray();		
				for (Mod111Key k : mvelCtx.getKeys()) {
					keysArray.put(k);
				}
				info.put(IJsonNames.KEYS, keysArray);
				array.put(info);
			}
			
		}
		return array.toString();
	}
	
	// *****************************************************
	// *****************************************************
	// *****************************************************
	private static String getDiffSalaryInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
		return IRPFFormatter.formatDiffInvoices(
				"DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
				,script.getLabel()
				,script.getKeys()
				,FiscalModelDAO.getEffectivePreviousModels(ctx,mod111,Mod111::new)
				 .collect(Collectors.toCollection(LinkedList::new))	
				,com.esferalia.aon.occam.impl.jooq.dao.IRPFDAO.getSalaryDiffIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
				);
	}
	
	
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
			+ FiscalModelUtils.getModelName(mod111) 
			+ " DEL " + mod111.getPeriod().getDescription()
			+ " DE " + mod111.getYear();
		return IRPFFormatter.formatDiffInvoices(title
			,script.getLabel()
			,script.getKeys()
			,FiscalModelDAO.getEffectivePreviousModels(ctx, mod111, Mod111::new)
			 	.collect(Collectors.toCollection(LinkedList::new))	
			,com.esferalia.aon.occam.impl.jooq.dao.IRPFDAO.getInputInvoicesDiffIrpfBreakdown(ctx, mod111)
				.filter( br ->  keyDAO.acceptValue(mod111, br) )	
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}

}

