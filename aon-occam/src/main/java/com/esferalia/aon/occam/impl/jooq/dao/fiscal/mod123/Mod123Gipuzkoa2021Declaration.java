package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod123Gipuzkoa2021Declaration extends Mod123Declaration {
	
	public static boolean accept(Mod123 mod) {
		return mod.isGipuzkoa(); 
	}

	private enum Mod123KeyDAO  implements IMod123KeyDAO{
		 GP_X00 (Mod123Key.GP_X00,null,null
			 ,Mod123Declaration::addDeponentDocument
			 ,null,null)
		,GP_C01(Mod123Key.GP_C01
			,(mod,br) -> isMovableCapital(br)
			,(ctx,mod,docs,br) -> addPerceptor(Mod123Key.GP_C01,mod,docs,br)
			,null,null,null)
		,GP_C02(Mod123Key.GP_C02
			,(mod,br) -> isMovableCapital(br)
			,(ctx,mod,docs,br) -> addBase(Mod123Key.GP_C02,mod,br)
			,null,null,null)
		,GP_C03(Mod123Key.GP_C03
			,(mod,br) -> isMovableCapital(br)
			,(ctx,mod,docs,br) -> addQuota(Mod123Key.GP_C03,mod,br)
			,null,null,null)
		,GP_C04(Mod123Key.GP_C04, null,null,null,null,null)
		,GP_C05(Mod123Key.GP_C05, null,null,null,null,null)
		,GP_C06(Mod123Key.GP_C06, null,null,null,null,null)
		,GP_C07(Mod123Key.GP_C07, null,null,null,null,null)
		,GP_C08(Mod123Key.GP_C08, null,null,null,null,null)
		,GP_C09(Mod123Key.GP_C09, null,null,null,"GP_C03+GP_C06+GP_C08",null)
		,GP_TIP (Mod123Key.GP_TIP,null,null,null,null,null)
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
		
		@Override
		public Mod123Key getKey() {
			return key;
		}
		@Override
		public boolean acceptValue(Mod123 mod,IrpfBreakdown  br) {
			return  acceptValue != null &&  acceptValue.accept(mod,br);
		}
		@Override
		public void initialize(AONContext ctx,Mod123 mod,Map<Mod123Key,Set<String>> docs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, br);
			}
		}
		@Override
		public void uniqueInitialize(AONContext ctx,Mod123 mod) {
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
	IMod123KeyDAO valueOf(String string) {
		return Mod123KeyDAO.valueOf(string);
	}

	@Override
	IMod123KeyDAO[] getKeys() {
		return Mod123KeyDAO.values();
	}

	@Override
	double getResult(Mod123 mod) {
		return mod.getAmount(Mod123Key.GP_C09);
	}
	
	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod123 mod) {
		return ComplementaryBeahaviour.COMPLEMENTARY;
	}

	@Override
	Mod123 initialize(AONContext ctx, Mod123 mod123) {
		mod123.setComplementaryDeclarationAvailable(true);
		mod123.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod123);
	}
	@Override
	public Mod123Key[] getSamePeriodExplainKeys() {
		return new Mod123Key[] {}; 
	}
	
	private static boolean isMovableCapital(IrpfBreakdown br) {
		return br.isFromInvoice() && 
			(br.getWithholdingType() == WithholdingType.MOVABLE_CAPITAL
			|| br.getWithholdingType() == WithholdingType.M193_C1
			|| br.getWithholdingType() == WithholdingType.M193_C2
			|| br.getWithholdingType() == WithholdingType.M193_C3);
	}
	
}