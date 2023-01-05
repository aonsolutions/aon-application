package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod115Araba2021Declaration extends Mod115Declaration {
	
	public static boolean accept(Mod115 mod) {
		return mod.isAraba(); 
	}

	private enum Mod115KeyDAO  implements IMod115KeyDAO{
		 AR_907(Mod115Key.AR_907,null,null,null,null,null)
		,AR_908(Mod115Key.AR_908,null,null,null,null,null)
		,AR_909(Mod115Key.AR_909,null,null,null,null,null)
		,AR_C01(Mod115Key.AR_C01 
			,(mod,br) -> isRenting(br)
			,(ctx,mod,docs,pdocs,br) -> addPerceptor(Mod115Key.AR_C01,mod,docs,pdocs,br)
			,null,null,null)
		,AR_C02(Mod115Key.AR_C02
			, (mod,br) -> isRenting(br)
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod115Key.AR_C02,mod,br)
			,null,null,null)
		,AR_C03(Mod115Key.AR_C03
			, (mod,br) -> isRenting(br)
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod115Key.AR_C03, mod, br)
			,null,null,null)
		,AR_C04(Mod115Key.AR_C04, null,null,null,null,null)
		,AR_C05(Mod115Key.AR_C05, null,null,null,null,null)
		,AR_C06(Mod115Key.AR_C06, null,null,null,null,null)
		,AR_C07(Mod115Key.AR_C07, null,null,null,"AR_C03+AR_C06",null)
		,AR_C08(Mod115Key.AR_C08,null,null
				, (ctx,mod) -> mod.putAmount(Mod115Key.AR_C08,
					mod.isReplacement() 
					?Mod115DAO.getSamePeriodModels(ctx, mod).mapToDouble(Mod115::getDeclarationResult).sum()
					:0.0)
				,null
				,"{messages : ["
						+ "\"Declaraciones en el mismo periodo/ejercicio:\","
						+ "@foreach{fm : periodModels}"
						+ "\" \u2022 Resultado del modelo @{fm.getModelFullName()} : @{java.text.DecimalFormat.getInstance().format(fm.getDeclarationResult())}\","
						+ "@end{}"
						+ "\" - Resultado de la casilla: @{java.text.DecimalFormat.getInstance().format(AR_C08)}\""
					+"]}"
		)
		,AR_C09(Mod115Key.AR_C09, null,null,null,null,null)
		,AR_C10(Mod115Key.AR_C10, null,null,null,null,null)
		,AR_C11(Mod115Key.AR_C11, null,null,null,"AR_C07-AR_C08+AR_C09+AR_C10",null)
		,AR_TIP(Mod115Key.AR_TIP, null,null,null,null,null)
		;
		
		private Mod115Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueUniqueIntializer uniqueInitializer;
		private String expression;
		private String template;
	
		private Mod115KeyDAO(Mod115Key key
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
		
		@Override
		public Mod115Key getKey() {
			return key;
		}
		@Override
		public boolean acceptValue(Mod115 mod,IrpfBreakdown  br) {
			return  acceptValue != null &&  acceptValue.accept(mod,br);
		}
		@Override
		public void initialize(AONContext ctx,Mod115 mod,Map<Mod115Key,Set<String>> docs
				,Map<Mod115Key,Set<String>> pdocs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, pdocs, br);
			}
		}
		@Override
		public void uniqueInitialize(AONContext ctx,Mod115 mod) {
			if (uniqueInitializer != null) {
				uniqueInitializer.initialize(ctx, mod);
			}
		}
		@Override
		public String getExpression() {
			return expression;
		}
		@Override
		public String getTemplate() {
			return template;
		}
	}

	@Override
	IMod115KeyDAO valueOf(String string) {
		return Mod115KeyDAO.valueOf(string);
	}

	@Override
	IMod115KeyDAO[] getKeys() {
		return Mod115KeyDAO.values();
	}

	@Override
	double getResult(Mod115 mod) {
		return mod.getAmount(Mod115Key.AR_C11);
	}

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod115 mod) {
		return  mod.isComplementary()
			?ComplementaryBeahaviour.COMPLEMENTARY
			:ComplementaryBeahaviour.REPLACEMENT;
	}
	
	@Override
	Mod115 initialize(AONContext ctx, Mod115 mod115) {
		mod115.setComplementaryDeclarationAvailable(true);
		mod115.setReplacementDeclarationAvailable(true);
		return super.initializeModel(ctx, mod115);
	}
	
	private static boolean isRenting(IrpfBreakdown br) {
		return br.isFromInvoice() && br.getWithholdingType() == WithholdingType.RENTING;
	}

}