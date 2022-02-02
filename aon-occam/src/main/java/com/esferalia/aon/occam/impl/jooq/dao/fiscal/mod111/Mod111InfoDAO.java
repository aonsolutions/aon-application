package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;


import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.impl.jooq.dao.IRPFDAO;
import com.esferalia.aon.occam.impl.jooq.dao.IRPFFormatter;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod111InfoDAO {
	
	/*
public static <T extends FiscalModel> Stream<T> getFiscalModels(AONContext ctx,int domain, FiscalModelType model, Supplier<T> modelSupplier) { 
	 */
	
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
				return MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO)); 
			}
		} 
		,DIFF_INVOICE{
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO)); 
			}
		}
		,SALARY {
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return MessageFormat.format(INFO_MSG, getSalaryInfo(ctx, mod, script,keyDAO));
			}
		}
		,SALARY_IN_KIND {
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return MessageFormat.format(INFO_MSG, getSalaryInKindInfo(ctx, mod, script,keyDAO));
			}
		}
		,DIFF_SALARY{
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return MessageFormat.format(INFO_MSG, getDiffSalaryInfo(ctx, mod, script,keyDAO));
			}
		}
		,COMPUTE {
			@Override
			String obtain(AONContext ctx, Mod111 mod, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
				return MessageFormat.format(INFO_MSG, getExpression(mod, script,keyDAO));
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

	private static String getSalaryInKindInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
		return IRPFFormatter.formatSalaries(
				"INFORME RETENCIONES EN ESPECIE EN N\u00D3MINAS"
						+ " DEL " + mod111.getPeriod().getDescription()
						+ " DE " + mod111.getYear()
				,script.getLabel()
				,IRPFDAO.getSalaryIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
				);
	}
	private static String getSalaryInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
		return IRPFFormatter.formatSalaries(
				"INFORME RETENCIONES DINERARIAS EN N\u00D3MINAS"
						+ " DEL " + mod111.getPeriod().getDescription()
						+ " DE " + mod111.getYear()
				,script.getLabel()
				,IRPFDAO.getSalaryIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
				);
	}
	
	private static String getExpression(Mod111 mod111, IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO0) {
		StringBuilder buf = new StringBuilder();
		int headerLength = 100;
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.center("DETALLE DEL C\u00C1LCULO", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.repeat("-", headerLength)));

		final StringBuilder expr = new StringBuilder();
		final StringBuilder resu = new StringBuilder();

		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = -4910560506222174407L;
			@Override
			public Object get(Object keyString) {
				Mod111Key key = Mod111Key.valueOf(keyString.toString());
				String exprCopy = expr.toString();
				expr.delete(0, expr.length());
				expr.append(AonStringUtils.replace(exprCopy
						, keyString.toString()
						, key.getBoxFormatted()));
				Object value = super.get(keyString);
				exprCopy = resu.toString();
				resu.delete(0, resu.length());
				resu.append(AonStringUtils.replace(exprCopy
						, keyString.toString()
						," " + value.toString() + " "
						));
				return value;
			}
		};

		for (String keyValue : mod111.getMap().keySet()) {
			Mod111Key mod111Key = Mod111Key.getKey(keyValue);
			if (mod111Key != null) {
				FiscalModelDetail detail = mod111.getMap().get(keyValue);
				mvelCtx.put(mod111Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}

		Mod111Declaration dec = Mod111Declaration.getInstance(mod111); 
		for (Mod111Key key : script.getKeys() ) {
			IMod111KeyDAO keyDAO = dec.getKey(key);
			if (keyDAO != null) {
				resu.delete(0, resu.length());
				resu.append(keyDAO.getExpression());
				expr.delete(0, expr.length());
				expr.append(keyDAO.getExpression());
				
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.center("Casilla: " + box + " - " + script.getLabel(), headerLength)));
				Object ret = MVEL.eval(keyDAO.getExpression(), mvelCtx, mvelCtx);
				resu.append(" = ");
				resu.append(AonMathUtils.round((Double) ret));
				expr.append(" = ");
				expr.append(box);
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,"<b>F\u00F3rmula:</b> " + expr.toString()));		
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BLUE_BORDER_BOTTOM,"<b>Resultado:</b> " + resu.toString()));
			}
			
		}
		return buf.toString();
	}
	
	private static String getInvoicesInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
		
		String title = "FACTURAS CON RETENCIONES QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod111) 
				+ " DEL " + mod111.getPeriod().getDescription()
				+ " DE " + mod111.getYear();
		return IRPFFormatter.formatInvoices(title,script.getLabel()
			,IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
	private static String getDiffSalaryInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, IMod111KeyDAO keyDAO) {
		return IRPFFormatter.formatDiffInvoices(
				"DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
				,script.getLabel()
				,script.getKeys()
				,FiscalModelDAO.getEffectivePreviousModels(ctx,mod111,Mod111::new)
				 .collect(Collectors.toCollection(LinkedList::new))	
				,IRPFDAO.getSalaryDiffIrpfBreakdown(ctx, mod111)
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
			,IRPFDAO.getInputInvoicesDiffIrpfBreakdown(ctx, mod111)
				.filter( br ->  keyDAO.acceptValue(mod111, br) )	
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
}

