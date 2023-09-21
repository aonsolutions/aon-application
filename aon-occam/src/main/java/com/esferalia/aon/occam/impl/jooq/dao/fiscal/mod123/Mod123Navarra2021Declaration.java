package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod123Navarra2021Declaration extends Mod123Declaration {
	
	public static boolean accept(Mod123 mod) {
		return mod.isNavarra(); 
	}

	private enum Mod123KeyDAO  implements IMod123KeyDAO{
		 NF_C01(Mod123Key.NF_C01
			, (mod,br) -> (isMovableCapital(br)) 
			, (ctx,mod,docs,br) -> addQuota(Mod123Key.NF_C01,mod,br)
			,null,null,null)
		,NF_TIP (Mod123Key.NF_TIP, null,null,null,null,null)
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
		return mod.getAmount(Mod123Key.NF_C01);
	}
	
	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod123 mod) {
		return  mod.isComplementary()
			?ComplementaryBeahaviour.COMPLEMENTARY
			:ComplementaryBeahaviour.REPLACEMENT;
	}
	
	@Override
	Mod123 initialize(AONContext ctx, Mod123 mod123) {
		mod123.setComplementaryDeclarationAvailable(true);
		mod123.setReplacementDeclarationAvailable(true);
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