package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod115Navarra2021Declaration extends Mod115Declaration {
	
	public static boolean accept(Mod115 mod) {
		return mod.isNavarra(); 
	}

	private enum Mod115KeyDAO  implements IMod115KeyDAO{
		NF_C01(Mod115Key.NF_C01
			, (mod,br) -> isRenting(br) 
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod115Key.NF_C01, mod, br)
			,null,null,null)
		,NF_TIP(Mod115Key.NF_TIP,null,null,null,null,null)
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
		return mod.getAmount(Mod115Key.NF_C01);
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