package com.esferalia.aon.occam.api.model.fiscal;

import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalModelUtils {
	
	@FunctionalInterface
	private interface IFiscalModelTypeName {
		boolean accept(IFiscalModel mod);
	}
	
	private enum FiscalModelTypeName {
		// ********** MODELO 111 ********** 
		 M111	("111"	,mod -> mod.getModel() == FiscalModelType.M111 && ( mod.isAEAT() || (mod.isMonthPeriod() && (mod.isAraba() || mod.isBizkaia() || mod.isGipuzkoa() ) ) ) ) 
		,M110	("110"	,mod -> mod.getModel() == FiscalModelType.M111 && mod.isQuarterPeriod() && (mod.isAraba() || mod.isBizkaia() || mod.isGipuzkoa() ) )
		,M745	("745"	,mod -> mod.getModel() == FiscalModelType.M111 && mod.isNavarra() && mod.isMonthPeriod() )
		,M715	("715"	,mod -> mod.getModel() == FiscalModelType.M111 && mod.isNavarra() && mod.isQuarterPeriod() )
		// ********** MODELO 115 ********** 
		,M115 	("115"	,mod -> mod.getModel() == FiscalModelType.M115 && !mod.isAraba() && !mod.isNavarra() )
		,M115A 	("115-A",mod -> mod.getModel() == FiscalModelType.M115 && mod.isAraba())
		,M760 	("760"	,mod -> mod.getModel() == FiscalModelType.M115 && mod.isNavarra() && mod.isMonthPeriod() )
		,M759 	("759"	,mod -> mod.getModel() == FiscalModelType.M115 && mod.isNavarra() && mod.isQuarterPeriod() )
		// ********** MODELO 123 ********** 
		,M123 	("123"	,mod -> mod.getModel() == FiscalModelType.M123 && !mod.isNavarra() )
		,M716 	("716"	,mod -> mod.getModel() == FiscalModelType.M123 && mod.isNavarra() )

		// ********** MODELO IVA **********		
		,MF69	("F69",mod -> (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303) && mod.isNavarra() && mod.isQuarterPeriod())	
		,MF66	("F66",mod -> (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303) && mod.isNavarra() && mod.isMonthPeriod())	
		,M303	("303",mod -> (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303 ) 
				&& (mod.isAEAT() || ((mod.isAraba() || mod.isBizkaia()) && !mod.isLastPeriod()))
		)
		,M300	("300",mod -> (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303) && mod.isGipuzkoa() && mod.isQuarterPeriod() && !mod.isLastPeriod() )	
		,M320	("320",mod -> (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303) && mod.isGipuzkoa() && mod.isMonthPeriod() && !mod.isLastPeriod() )
		,M420	("420",mod -> (mod.getModel() == FiscalModelType.M303 && mod.isCanarias() && mod.isQuarterPeriod()), "I.G.I.C. R\u00E9gimen General. Autoliquidaci\u00F3n Trimestral." )
		,M417	("417",mod -> (mod.getModel() == FiscalModelType.M303 && mod.isCanarias() && mod.isMonthPeriod()) , "I.G.I.C. Suministro Inmediato de Informaci\u00F3n. Autoliquidaci\u00F3n.")
		
		// ********** MODELO 390 **********
		,M390	("390",mod -> (mod.getModel() == FiscalModelType.M390 && mod.isAEAT()) 
			|| (mod.getModel() == FiscalModelType.M390_HF)
			|| ( (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303 )
					&& (mod.isAraba() || mod.isBizkaia() || mod.isGipuzkoa()) 
					&& mod.isLastPeriod() )
		)
		,M425	("425",mod -> (mod.getModel() == FiscalModelType.M390 && mod.isCanarias()), "I.G.I.C. Declaraci\u00F3n Resumen Anual.")
		
		// ********** MODELO 130 ********** 
		,M130	("130",mod -> mod.getModel() == FiscalModelType.M130)
		// ********** MODELO 131 ********** 
		,M131	("131",mod -> mod.getModel() == FiscalModelType.M131)
		// ********** MODELO 340 ********** 
		,M340	("340",mod -> mod.getModel() == FiscalModelType.M340)
		// ********** MODELO 347 ********** 
		,M347	("347",mod -> (mod.getModel() == FiscalModelType.M347 && !mod.isCanarias()))
		,M415	("415",mod -> (mod.getModel() == FiscalModelType.M347 && mod.isCanarias()), "Declaraci\u00F3n anual de operaciones con terceras personas.")
		// ********** MODELO 349 ********** 
		,M349	("349",mod -> mod.getModel() == FiscalModelType.M349)
		// ********** MODELO 180 ********** 
		,M180	("180",mod -> mod.getModel() == FiscalModelType.M180)
		// ********** MODELO 184 ********** 
		,M184	("184",mod -> mod.getModel() == FiscalModelType.M184)
		// ********** MODELO 190 ********** 
		,M190	("190",mod -> mod.getModel() == FiscalModelType.M190)
		// ********** MODELO 193 ********** 
		,M193	("193",mod -> mod.getModel() == FiscalModelType.M193)
		// ********** MODELO 200 ********** 
		,M200	("200",mod -> mod.getModel() == FiscalModelType.M200)
		// ********** MODELO 202 ********** 
		,M202	("202",mod -> mod.getModel() == FiscalModelType.M202)
		
		// ********** MODELO 140 & 240 LROE ********** 
		,M140	("140",mod -> mod.getModel() == FiscalModelType.M140)
		,M240	("240",mod -> mod.getModel() == FiscalModelType.M240)

		// ********** SII **********
		,SII	("SII",mod -> mod.getModel() == FiscalModelType.SII)
		
		// ********** MODELO 369 ********** 
		,M369	("369",mod -> mod.getModel() == FiscalModelType.M369)
		
		// ********** MODELO 421 **********
		,M421	("421",mod -> (mod.getModel() == FiscalModelType.M421), "I.G.I.C. R\u00E9gimen Simplificado. Autoliquidaci\u00F3n Trimestral." )
		
		;
		
		private String name;
		private IFiscalModelTypeName accepter;
		private String description;
		
		private FiscalModelTypeName(String name, IFiscalModelTypeName getter) {
			this.name = name;
			this.accepter = getter;
		}
		private FiscalModelTypeName(String name, IFiscalModelTypeName getter, String description) {
			this.name = name;
			this.accepter = getter;
			this.description = description;
		}
		public String getName() {
			return name;
		}
		public String getDescription() {
			return description;
		}
		private boolean accept(IFiscalModel mod) {
			return this.accepter.accept(mod);
		}
		private static String getName(IFiscalModel mod) {
			for (FiscalModelTypeName f : FiscalModelTypeName.values()) {
				if (f.accept(mod)) 
					return f.getName();
			}
			return null;
		}
		private static String getDescription(IFiscalModel mod) {
			for (FiscalModelTypeName f : FiscalModelTypeName.values()) {
				if (f.accept(mod)) 
					return f.getDescription();
			}
			return null;
		}
	}
	
	public static String getModelName(IFiscalModel fm) {
		String name = FiscalModelTypeName.getName(fm);
		return AonStringUtils.isNotBlank(name)?name:fm.getModel().getName();
	}
	
	public static String getPeriodDescription(IFiscalModel fm) {
		String description = "";
		if (fm.getPeriod() != null) {
			if (fm.getModel() == FiscalModelType.M202) {
				if (fm.getPeriod() == Period.T1)
					description = "1\u00BA Per.";
				if (fm.getPeriod() == Period.T2) 
					description = "2\u00BA Per.";
				if (fm.getPeriod() == Period.T3) 
					description = "3\u00BA Per.";				
			}
			else description = fm.getPeriod().getDescription();			
		}		
		return description;
	}

	// POR AHORA SOLO SE USA PARA CANARIAS, PERO SE PODRIA EXTENDER A TODOS Y DEJAR DE USAR AON.MSG...
	public static String getModelDescription(IFiscalModel fm) {
		return FiscalModelTypeName.getDescription(fm);
	}
	
}


