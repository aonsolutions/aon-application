package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod111Navarra2023Declaration extends Mod111Declaration {
	
	public static boolean accept(Mod111 mod) {
		return mod.isNavarra() && mod.getYear() >= 2023; 
	}

	private enum Mod111KeyDAO  implements IMod111KeyDAO{
		 NF_A1(Mod111Key.NF_A1,true
			, (mod,br) -> isRetention(mod,br)
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.NF_A1,mod,br)
			,null,null,null)
		,NF_TIP (Mod111Key.NF_TIP,false,null,null,null,null,null)
		;
		
		private Mod111Key key;
		private boolean diffEnabled;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueUniqueIntializer uniqueInitializer;
		private String expression;
		private String template;
	
		private Mod111KeyDAO(Mod111Key key
				, boolean diffEnabled
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueUniqueIntializer uniqueInitializer
				, String expression
				, String template) {
			this.key = key;
			this.diffEnabled = diffEnabled;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.uniqueInitializer = uniqueInitializer;
			this.expression =  expression;
			this.template =  template;
		}
		
		@Override
		public Mod111Key getKey() {
			return key;
		}
		@Override
		public boolean isDiffEnabled() {
			return diffEnabled;
		}
		@Override
		public boolean acceptValue(Mod111 mod,IrpfBreakdown  br) {
			return  acceptValue != null &&  acceptValue.accept(mod,br);
		}
		@Override
		public void initialize(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs
				,Map<Mod111Key,Set<String>> pdocs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, pdocs, br);
			}
		}
		@Override
		public void uniqueInitialize(AONContext ctx,Mod111 mod) {
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
	IMod111KeyDAO valueOf(String string) {
		return Mod111KeyDAO.valueOf(string);
	}

	@Override
	IMod111KeyDAO[] getKeys() {
		return Mod111KeyDAO.values();
	}

	@Override
	double getResult(Mod111 mod) {
		return mod.getAmount(Mod111Key.NF_A1);
	}
	
	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod111 mod) {
		return  mod.isComplementary()
				?ComplementaryBeahaviour.COMPLEMENTARY
				:ComplementaryBeahaviour.REPLACEMENT;
	}
	
	@Override
	Mod111 initialize(AONContext ctx, Mod111 mod111) {
		mod111.setComplementaryDeclarationAvailable(true);
		mod111.setReplacementDeclarationAvailable(true);
		return super.initializeModel(ctx, mod111);
	}
	
	@Override
	public Mod111Key[] getSamePeriodExplainKeys() {
		return new Mod111Key[] {};
	}
	
	// ----------------------------------------------- -------
	// ----------------------------------------------- FILTROS
	// ----------------------------------------------- -------
	private static boolean isRetention(Mod111 mod111, IrpfBreakdown br) {
		return br.isFromSalary() || isInvoiceRetention(br);
	}
	
	private static boolean isInvoiceRetention(IrpfBreakdown br) {
		return br.isFromInvoice() && 
			(br.getWithholdingType() == WithholdingType.M190_F_01  
			 ||	br.getWithholdingType() == WithholdingType.M190_F_02_1
			 ||	br.getWithholdingType() == WithholdingType.M190_F_02_2
			 || br.getWithholdingType() == WithholdingType.PROFESSIONAL  
			 ||	br.getWithholdingType() == WithholdingType.M190_G_02
			 ||	br.getWithholdingType() == WithholdingType.M190_G_03
			 ||	br.getWithholdingType() == WithholdingType.TRANSPORT_OPERATOR
			 || br.getWithholdingType() == WithholdingType.FARMER
			 ||	br.getWithholdingType() == WithholdingType.M190_H_02
			 ||	br.getWithholdingType() == WithholdingType.M190_H_03
			 || br.getWithholdingType() == WithholdingType.M190_I_01
			 ||	br.getWithholdingType() == WithholdingType.M190_I_02
			 ||	br.getWithholdingType() == WithholdingType.M190_J
			 ||	br.getWithholdingType() == WithholdingType.M190_K_02
			 || br.getWithholdingType() == WithholdingType.M190_K_01  
			 ||	br.getWithholdingType() == WithholdingType.M190_K_03);
	}
	
}