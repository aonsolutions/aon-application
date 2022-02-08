package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;

public class Mod111Navarra2021Declaration extends Mod111Declaration {
	
	public static boolean accept(Mod111 mod) {
		return mod.isNavarra(); 
	}

	private enum Mod111KeyDAO  implements IMod111KeyDAO{
		 NF_A1(Mod111Key.NF_A1
			, (mod,br) -> (br.isProfessional() || br.isTransportOperator() || br.isFarmer() || br.isSalaryRetention() || br.isSalaryInKindRetention()) 
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.NF_A1,mod,br)
			,null,null)
		,NF_TIP (Mod111Key.NF_TIP, null,null,null,null)
		;
		
		private Mod111Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueUniqueIntializer uniqueInitializer;
		private String expression;
	
		private Mod111KeyDAO(Mod111Key key
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueUniqueIntializer uniqueInitializer
				, String expression) {
			this.key = key;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.uniqueInitializer = uniqueInitializer;
			this.expression =  expression;
		}
		
		
		public Mod111Key getKey() {
			return key;
		}
		public boolean acceptValue(Mod111 mod,IrpfBreakdown  br) {
			return  acceptValue != null &&  acceptValue.accept(mod,br);
		}
		public void initialize(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs
				,Map<Mod111Key,Set<String>> pdocs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, pdocs, br);
			}
		}
		public void uniqueInitialize(AONContext ctx,Mod111 mod) {
			if (uniqueInitializer != null) {
				uniqueInitializer.initialize(ctx, mod);
			}
		}
		public String getExpression() {
			return expression;
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

}