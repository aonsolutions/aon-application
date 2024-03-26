package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod123AEAT2024Declaration extends Mod123Declaration {
	
	public static boolean accept(Mod123 mod) {
		return mod.isAEAT() && mod.getYear() >= 2024; 
	}
	
	private enum Mod123KeyDAO  implements IMod123KeyDAO {
		
		// Dividendos y otras rentas de participación en fondos	propios de entidades
		 CT_C01(Mod123Key.CT_C01
				, (mod,br) -> isMovableCapitalDividens(br)
				, (ctx,mod,docs,br) -> addPerceptor(Mod123Key.CT_C01,mod,docs,br)
				,null,null,null) // Número de Rentas
		,CT_C04(Mod123Key.CT_C04
			, (mod,br) -> isMovableCapitalDividens(br)
			, (ctx,mod,docs,br) -> addBase(Mod123Key.CT_C04,mod,br)
			,null,null,null) // Base
		,CT_C07(Mod123Key.CT_C07
			, (mod,br) -> isMovableCapitalDividens(br)
			, (ctx,mod,docs,br) -> addQuota(Mod123Key.CT_C07,mod,br)
			,null,null,null) // Retenciones e ingresos a cuenta
		
		// Resto de Rentas
		,CT_C02(Mod123Key.CT_C02
			, (mod,br) -> isMovableCapitalOther(br)
			, (ctx,mod,docs,br) -> addPerceptor(Mod123Key.CT_C02,mod,docs,br)
			,null,null,null) // Número de Rentas
		,CT_C05(Mod123Key.CT_C05
			, (mod,br) -> isMovableCapitalOther(br)
			, (ctx,mod,docs,br) -> addBase(Mod123Key.CT_C05,mod,br)
			,null,null,null) // Base
		,CT_C08(Mod123Key.CT_C08
			, (mod,br) -> isMovableCapitalOther(br)
			, (ctx,mod,docs,br) -> addQuota(Mod123Key.CT_C08,mod,br)
			,null,null,null) // Retenciones e ingresos a cuenta
		
		// Totales
		,CT_C03(Mod123Key.CT_C03, null,null,null, "CT_C01+CT_C02",null) // Número de Rentas
		,CT_C06(Mod123Key.CT_C06, null,null,null, "CT_C04+CT_C05",null) // Base
		,CT_C09(Mod123Key.CT_C09, null,null,null, "CT_C07+CT_C08",null) // Retenciones e ingresos a cuenta		
		
		,CT_C10(Mod123Key.CT_C10, null,null,null,null,null) // Periodificación. Ingresos ejercicios anteriores
		,CT_C11(Mod123Key.CT_C11, null,null,null,null,null) // Periodificación. Regularización 
		
		,CT_C12(Mod123Key.CT_C12, null,null,null, "CT_C09+CT_C11",null) // Suma de retenciones e ingresos a cuenta y regularización

		,CT_C13(Mod123Key.CT_C13, null,null
			, (ctx,mod) -> mod.putAmount(Mod123Key.CT_C13,mod.isComplementary()
				?Mod123DAO.getSamePeriodEffectiveModels(ctx, mod).mapToDouble(Mod123::getDeclarationResult).sum()
				:0.0)
			,null,null) // Resultado de anteriores declaraciones
		
		,CT_C14(Mod123Key.CT_C14, null,null,null, "CT_C12-CT_C13",null) // Total liquidación. Resultado a ingresar 
		
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
		return mod.getAmount(Mod123Key.CT_C14);
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

	@Override
	public Mod123Key[] getSamePeriodExplainKeys() {
		return new Mod123Key[] {Mod123Key.CT_C13}; 
	}

	private static boolean isMovableCapitalDividens(IrpfBreakdown br) {
		return (br.isFromInvoice() && br.getWithholdingType() == WithholdingType.MOVABLE_CAPITAL);
	}
	
	private static boolean isMovableCapitalOther(IrpfBreakdown br) {
		return br.isFromInvoice() && 
			(br.getWithholdingType() == WithholdingType.M193_C1
			|| br.getWithholdingType() == WithholdingType.M193_C2
			|| br.getWithholdingType() == WithholdingType.M193_C3);
	}	

}