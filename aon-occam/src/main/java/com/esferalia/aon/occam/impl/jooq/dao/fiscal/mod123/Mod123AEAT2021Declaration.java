package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod123AEAT2021Declaration extends Mod123Declaration {
	
	public static boolean accept(Mod123 mod) {
		return mod.isAEAT(); 
	}
	
	private enum Mod123KeyDAO  implements IMod123KeyDAO{
		 CT_C01(Mod123Key.CT_C01
			, (mod,br) -> isMovableCapital(br)
			, (ctx,mod,docs,br) -> addPerceptor(Mod123Key.CT_C01,mod,docs,br)
			,null,null,null)
		,CT_C02(Mod123Key.CT_C02
			, (mod,br) -> isMovableCapital(br)
			, (ctx,mod,docs,br) -> addBase(Mod123Key.CT_C02,mod,br)
			,null,null,null)
		,CT_C03(Mod123Key.CT_C03
			, (mod,br) -> isMovableCapital(br)
			, (ctx,mod,docs,br) -> addQuota(Mod123Key.CT_C03,mod,br)
			,null
			,null
			,null)
		,CT_C04(Mod123Key.CT_C04, null,null,null,null,null)
		,CT_C05(Mod123Key.CT_C05, null,null,null,null,null)
		,CT_C06(Mod123Key.CT_C06, null,null,null, "CT_C03+CT_C05",null)
		,CT_C07(Mod123Key.CT_C07, null,null
			, (ctx,mod) -> mod.putAmount(Mod123Key.CT_C07,mod.isComplementary()
				?Mod123DAO.getSamePeriodModels(ctx, mod).mapToDouble(Mod123::getDeclarationResult).sum()
				:0.0)
			,null
			,"{messages : ["
				+ "\"Declaraciones en el mismo periodo/ejercicio:\","
				+ "@foreach{fm : periodModels}"
				+ "\" \u2022 Resultado del modelo @{fm.getModelFullName()} : @{java.text.DecimalFormat.getInstance().format(fm.getDeclarationResult())}\","
				+ "@end{}"
				+ "\" - Resultado de la casilla: @{java.text.DecimalFormat.getInstance().format(CT_C07)}\""
			+"]}")
		,CT_C08(Mod123Key.CT_C08, null,null,null, "CT_C06-CT_C07",null)
		,CT_TIP (Mod123Key.CT_TIP,null,null,null,null,null)

		;
		
		private Mod123Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueUniqueIntializer uniqueInitializer;
		private String expression;
		private String template;
	
		private Mod123KeyDAO(Mod123Key key
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueUniqueIntializer uniqueInitializer
				, String expression
				, String template) {
			this.key = key;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.uniqueInitializer = uniqueInitializer;
			this.expression =  expression;
			this.template =  template;
		}
		
		
		public Mod123Key getKey() {
			return key;
		}
		public boolean acceptValue(Mod123 mod,IrpfBreakdown  br) {
			return  acceptValue != null &&  acceptValue.accept(mod,br);
		}
		public void initialize(AONContext ctx,Mod123 mod,Map<Mod123Key,Set<String>> docs ,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, br);
			}
		}
		public void uniqueInitialize(AONContext ctx,Mod123 mod) {
			if (uniqueInitializer != null) {
				uniqueInitializer.initialize(ctx, mod);
			}
		}
		public String getExpression() {
			return expression;
		}
		
		@Override
		public String getTemplate() {
			return template;
		}
	}

	@Override
	IMod123KeyDAO valueOf(String string) {
		return Mod123KeyDAO.valueOf(string);
	}

	@Override
	IMod123KeyDAO[] getKeys() {
		return Mod123KeyDAO.values();
	}

	@Override
	double getResult(Mod123 mod) {
		return mod.getAmount(Mod123Key.CT_C08);
	}
	
	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod123 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}

	@Override
	Mod123 initialize(AONContext ctx, Mod123 mod123) {
		mod123.setComplementaryDeclarationAvailable(true);
		mod123.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod123);
	}

	private static boolean isMovableCapital(IrpfBreakdown br) {
		return br.isFromInvoice() && br.getWithholdingType() == WithholdingType.MOVABLE_CAPITAL;
	}
}