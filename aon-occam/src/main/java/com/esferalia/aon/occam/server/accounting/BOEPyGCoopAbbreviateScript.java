package com.esferalia.aon.occam.server.accounting;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;

public class BOEPyGCoopAbbreviateScript extends BalanceScript {
	
	private IAccMiningKeyAccept accepter;
	
	private static enum AccBOEPyGCoopAbbreviateKey implements IBalanceKey{
		
		  CEA001(1,true , "1." ,"Importe neto de la cifra de negocios.","sap({700,701,702,703,704,705})-sdp({706,708,709})",null)
		 ,CEA002(1,true , "2." ,"Variación de existencias de productos terminados y en curso de fabricación.","sap({71,7930})-sdp({6930})",null)	
		 ,CEA003(1,true , "3." ,"Trabajos realizados por la empresa para su activo.","sap({73})",null)
		 ,CEA004(1,true , "4." ,"Aprovisionamientos.",null,"CEA004A+CEA004B")
			 ,CEA004A(2,true , "a)" ,"Consumos de existencias de socios.","sap({605})-sdp({6063,6083,6093,617})",null)
			 ,CEA004B(2,true , "b)" ,"Otros aprovisionamientos.","sap({600,601,602,606,607,608,609,61,7931,7932,7933,617})-sdp({6931,6932,6933})",null)
		 ,CEA005(1,true , "5." ,"Otros ingresos de explotación.",null,"CEA005A+CEA005B")
		 	,CEA005A(2,true , "a)" ,"Ingresos por operacioines con socios.","sap({756})",null)
		 	,CEA005B(2,true , "b)" ,"Otros ingresos.","sap({740, 747,75})-sap({756,7570,7571,7572,7573,})",null)
		 ,CEA006(1,true , "6." ,"Gastos de personal.",null,"CEA006A+CEA006B")
		 	,CEA006A(2,true , "a)" ,"Servisio de trabajo de socios.","sap({647})",null)
		 	,CEA006B(2,true , "b)" ,"Otros gastos de personal.","sap({64,7950,7957})-sap({647})",null)
		 ,CEA007(1,true , "7." ,"Otros gastos de explotación.","sap({636,639,794,7954})-sdp({62,631,634,65,657,694,695})",null)
		 ,CEA008(1,true , "8." ,"Amortización del inmovilizado.","sap({68})",null)
		 ,CEA009(1,true , "9." ,"Imputación de subvenciones de inmovilizado no financiero y otras.","sap({746})",null)
		 ,CEA010(1,true ,"10." ,"Excesos de provisiones.","sap({7951,7952,7955,7956})",null)
		 ,CEA011(1,true ,"11." ,"Deterioro y resultado por enajenaciones del inmovilizado.","sap({670,671,672,690,691,692,770,771,772,778,790,791,792})-sdp({678})",null)
		 ,CEA012(1,false,"12.","Fondo de Educaciíon, Formación y Promoción.",null,"CEA012A+CEA012B")
		 	,CEA012A(2,true,"a)","Dotación.","sap({657})",null)
		 	,CEA012B(2,true,"b)","Subvenciones, donaciones y ayudas y sanciones.","sap({7570,7571,7572,7573})",null)
		 ,CEA   (0,false,"A)"  ,"RESULTADO DE EXPLOTACIÓN (1+2+3+4+5+6+7+8+9+10+11+12)",null,"CEA001+CEA002+CEA003+CEA004+CEA005+CEA006+CEA007+CEA008+CEA009+CEA010+CEA011+CEA012")
		 ,CEB013(1,true ,"13." ,"Ingresos financieros.","sap({760,761,762,767,769})",null)
			 ,CEB013A(2,true ,"a)" ,"De socios","0.0",null)
			 ,CEB013B(2,true ,"b)" ,"Otros ingresos financieros.","sap({760,761,762,767,769})",null)
		 ,CEB014(1,true ,"14." ,"Gastos financieros.","sap({660,661,662,664,665,669})",null)
			 ,CEB014A(2,true ,"a)" ,"Intereses y retrono obligatorio de las aportaciones al capital social y de otros fondos con características de deuda.","sap({6647})",null)
			 ,CEB014B(2,true ,"b)" ,"Otros gastos financieros.","sap({660,661,662,664,665,669})",null)
		 ,CEB015(1,true ,"15." ,"Variación de valor razonable en instrumentos financiero.","sap({763})-sdp({663})",null)
		 ,CEB016(1,true ,"16." ,"Diferencias de cambio.","sap({768})-sdp({668})",null)
		 ,CEB017(1,true ,"17." ,"Deterioro y resultado por enajenaciones de instrumentos financieros.","sap({666,667,673,675,696,697,698,699,766,773,775,796,797,798,799})",null)
		 ,CEB   (0,false,"B)"  ,"RESULTADO FINANCIERO (13+14+15+16+17)",null,"CEB013+CEB014+CEB015+CEB016+CEB017")
		 ,CEC   (0,false,"C)"  ,"RESULTADO ANTES DE IMPUESTOS (A+B)",null,"CEA+CEB")
		 ,CED018(1,true ,"18." ,"Impuestos sobre beneficios.","sap({6301,638})-sdp({6300,633})",null)
		 ,CED   (0,false,"D)"  ,"RESULTADO DEL EJERCICIO (C+18)",null,"CEC+CED018")
	 	;
	
		private int level;
		private boolean leaf;
		private String prefix;
		private String name;
		private String  initialExpressionProvider;
		private String  computeExpressionProvider;
		
		private AccBOEPyGCoopAbbreviateKey(int level
				,boolean leaf
				,String prefix
				,String name
				,String  initialExpressionProvider
				,String  computeExpressionProvider) {
			this.level = level;
			this.leaf = leaf;
			this.name = name;
			this.prefix = prefix;
			this.initialExpressionProvider = initialExpressionProvider;
			this.computeExpressionProvider = computeExpressionProvider;
		}
		
		@Override
		public String getCode() {
			return this.toString();
		}
		@Override
		public int getLevel() {
			return level;
		}
		@Override
		public String getPrefix() {
			return prefix;
		}
		@Override
		public String getName() {
			return name;
		}
		@Override
		public String getComputeExpression() {
			return computeExpressionProvider;
		}
		@Override
		public String getInitialExpression() {
			return initialExpressionProvider;
		}
		@Override
		public boolean isLeaf(){
			return leaf;
		}
	}

	@Override
	public IAccMiningKeyAccept getAccepter() {
		if (accepter == null) {
			accepter = new IAccMiningKeyAccept() {
	
					@Override
					public boolean acceptKey(Object key) {
						try {
							return (AccBOEPyGCoopAbbreviateKey.valueOf((String) key) != null);
						} catch (IllegalArgumentException e) {
							return false;
						}
					}
				};
		}
		return accepter;
	}
	
	@Override
	public List<AccBOEPyGCoopAbbreviateKey> getKeyList() {
		return new LinkedList<AccBOEPyGCoopAbbreviateKey>( Arrays.asList(AccBOEPyGCoopAbbreviateKey.values()));
	}

}

