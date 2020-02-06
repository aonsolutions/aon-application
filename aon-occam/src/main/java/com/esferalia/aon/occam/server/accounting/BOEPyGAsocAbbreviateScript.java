package com.esferalia.aon.occam.server.accounting;

import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.HEADER0;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.HEADER1;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.LEAF;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.TOTAL0;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.AccountBalanceLineStyle;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;

public class BOEPyGAsocAbbreviateScript extends BalanceScript {

	private IAccMiningKeyAccept accepter;
	
	private static enum AccBOEPyGAbbreviateKey implements IBalanceKey{
		CEA	(0,HEADER0,"A)" ,"Excedente del ejercicio.",null,null)
		  ,CEA01 (1,HEADER1,"1." ,"Ingresos de la actividad propia.",null,"CEA01A+CEA01B+CEA01C+CEA01D+CEA01E")
		  	,CEA01A(2,LEAF  , "a." ,"Cuotas de asociados y afiliados.","sap({720})",null)
		  	,CEA01B(2,LEAF  , "b." ,"Aportaciones de usuarios.","sap({721})",null)
		  	,CEA01C(2,LEAF  , "c." ,"Ingresos de promociones, patrocinadores y colaboradores.","sap({722,723})",null)
		  	,CEA01D(2,LEAF  , "d." ,"Subvenciones, donaciones y legados imputados al excedente del ejercicio.","sap({740,747,748})",null)
		  	,CEA01E(2,LEAF  , "e." ,"Reintegro de ayudas y asignaciones.","sap({728})",null)
		  ,CEA02 (1,HEADER1,"2." ,"Ventas y otros ingresos de la actividad mercantil.","sap({700,701,702,703,704,705})-sdp({706,708,709})",null)
		  ,CEA03 (1,HEADER1,"3." ,"Gastos por ayudas y otros.",null,"CEA03A+CEA03B+CEA03C+CEA03D")
		  	,CEA03A(2,LEAF  , "a." ,"Ayudas monetarias.","sap({650})",null)
		  	,CEA03B(2,LEAF  , "b." ,"Ayudas no monetarias.","sap({651})",null)
		  	,CEA03C(2,LEAF  , "c." ,"Gastos por colaboraciones y del \u00F3rgano de gobierno.","sap({653,654})",null)
		  	,CEA03D(2,LEAF  , "d." ,"Reintegro de subvenciones, donaciones y legados.","sap({658})",null)
	  	  ,CEA04(1,HEADER1, "4." ,"Variaci\u00F3n de existencias de productos terminados y en curso de fabricaci\u00F3n.","sap({6930,71,7930})",null)
	  	  ,CEA05(1,HEADER1, "5." ,"Trabajos realizados por la entidad para su activo.","sap({73})",null)
	  	  ,CEA06(1,HEADER1, "6." ,"Aprovisionamientos.","sap({600,601,602,607,6931,6932,6933,7931,7932,7933})-sdp({606,608,609,61})",null)
	 	  ,CEA07(1,HEADER1, "7." ,"Otros ingresos de la actividad.","sap({75})",null)
	 	  ,CEA08(1,HEADER1, "8." ,"Gastos de personal.","sap({64})",null)
	 	  ,CEA09(1,HEADER1, "9." ,"Otros gastos de la actividad.","sap({62,631,634,655,656,659,694,695,794,7954})-sdp({636,639})",null)
	 	  ,CEA10(1,HEADER1, "10.","Amortizaci\u00F3n del inmovilizado.","sap({68})",null)
	 	  ,CEA11(1,HEADER1, "11.","Subvenciones, donaciones y legados de capital traspasados al excedente del ejercicio.","sap({745,746})",null)
	 	  ,CEA12(1,HEADER1, "12.","Exceso de provisiones.","sap({7951,7952,7955})",null)
	 	  ,CEA13(1,HEADER1, "13.","Deterioro y resultado por enajenaciones del inmovilizado.","sap({670,671,672,690,691,692,770,771,772,790,791,792})",null)
	 	  ,CEA14(1,HEADER1, "14.","Otros resultados.","sap({678,778})",null)
 	  ,CEA1	(0,TOTAL0,"A.1)" ,"EXCEDENTE DE LA ACTIVIDAD. (1+2+3+4+5+6+7+8+9+10+11+12+13+14)",null,"CEA01+CEA02+CEA03+CEA04+CEA05+CEA06+CEA07+CEA08+CEA09+CEA10+CEA11+CEA12+CEA13+CEA14")
 	  	  ,CEA15(1,HEADER1, "15." ,"Ingresos financieros.","sap({760,761,762,767,769})",null)
 	  	  ,CEA16(1,HEADER1, "16." ,"Gastos financieros.","sap({660,661,662,665,669})",null)
 	  	  ,CEA17(1,HEADER1, "17." ,"Variaci\u00F3n de valor razonable en instrumentos financiero.","sap({663,763})",null)
 	  	  ,CEA18(1,HEADER1, "18." ,"Diferencias de cambio.","sap({668,768})",null)
 	  	  ,CEA19(1,HEADER1, "19." ,"Deterioro y resultado por enajenaciones de instrumentos financieros.","sap({666,667,673,675,696,697,698,699,766,773,775,796,797,798,799})",null)
 	  ,CEA2	(0,TOTAL0,"A.2)" ,"EXCEDENTE DE LAS OPERACIONES FINANCIERAS. (15+16+17+18+19)",null,"CEA15+CEA16+CEA17+CEA18+CEA19")
 	  ,CEA3	(0,TOTAL0,"A.3)" ,"EXCEDENTE ANTES DE IMPUESTOS. (A.1+A.2)",null,"CEA1+CEA2")
	  	  ,CEA20(1,HEADER1, "20." ,"Impuestos sobre beneficios.","sap({6300,6301,633,638})",null)
 	  ,CEA4	(0,TOTAL0,"A.4)" ,"Variaci\u00F3n de patrimonio neto reconocida en el excedente del ejercicio (A.3+20)",null,"CEA3+CEA20")
 	  
 	  ,CEB	(0,HEADER0,"B)" ,"Ingresos y gastos imputados directamente al patrimonio neto",null,"CEA3+CEA18")
	  	  ,CEB01(1,HEADER1, "1." ,"Subvenciones recibidas.","sap({940,9420})",null)
 	  	  ,CEB02(1,HEADER1, "2." ,"Donaciones y legados recibidos.","sap({941,9421})",null)
 	  	  ,CEB03(1,HEADER1, "3." ,"Otros ingresos y gastos.","sap({800,89,900,991,992,810,910,85,95})",null) 	  	  
 	  	  ,CEB04(1,HEADER1, "4." ,"Efecto impositivo.","sap({8300,8301,833,834,835,838})",null)
 	  ,CEB1	(0,TOTAL0,"B.1)" ,"Variaci\u00F3n de patrimonio neto por ingresos y gastos reconocidos directamente en el patrimonio neto (1+2+3+4)",null,"CEB01+CEB02+CEB03+CEB04")
 	  ,CEC	(0,HEADER0,"C)" ,"Reclasificaciones al excedente del ejercicio",null,null)
  	  	  ,CEC01(1,HEADER1, "1." ,"Subvenciones recibidas.","sap({840,8420})",null)
	  	  ,CEC02(1,HEADER1, "2." ,"Donaciones y legados recibidos.","sap({841,8421})",null)
	  	  ,CEC03(1,HEADER1, "3." ,"Otros ingresos y gastos.","sap({802,902,993,994,812,912})",null) 	  	  
	  	  ,CEC04(1,HEADER1, "4." ,"Efecto impositivo.","sap({8301,836,837})",null)
 	  ,CEC1	(0,TOTAL0,"C.1)" ,"Variaci\u00F3n de patrimonio neto por reclasificaciones al excedente del ejercicio (1+2+3+4)",null,"CEC01+CEC02+CEC03+CEC04")
 	  ,CED	(0,TOTAL0,"D)" ,"Variaciones de patrimonio neto por ingresos y gastos imputados directamente al patrimonio neto (B.1+C.1)",null,"CEB1+CEC1")
 	  ,CEE	(0,TOTAL0,"E)" ,"Ajustes por cambios de criterio",null,null)
 	  ,CEF	(0,TOTAL0,"F)" ,"Ajustes por errores",null,null)
 	  ,CEG	(0,TOTAL0,"G)" ,"Variaciones en la dotaci\u00F3n fundacional o fondo social",null,null)
 	  ,CEH	(0,TOTAL0,"H)" ,"Otras variaciones",null,null)
 	  ,CEI	(0,TOTAL0,"I)" ,"RESULTADO TOTAL, VARIACI\u00D3N DEL PATRIMONIO NETO EN EL EJERCICIO (A.4+D+E+F+G+H)",null,"CEA4+CED+CEE+CEF+CEG+CEH")
 	 	;
			
		private int level;
		private AccountBalanceLineStyle type;
		private String prefix;
		private String name;
		private String  initialExpressionProvider;
		private String  computeExpressionProvider;
		
		private AccBOEPyGAbbreviateKey(int level
				,AccountBalanceLineStyle type
				,String prefix
				,String name
				,String  initialExpressionProvider
				,String  computeExpressionProvider) {
			this.level = level;
			this.type = type;
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
		public AccountBalanceLineStyle getType() {
			return type;
		}
	}

	@Override
	public IAccMiningKeyAccept getAccepter() {
		if (accepter == null) {
			accepter = new IAccMiningKeyAccept() {
	
					@Override
					public boolean acceptKey(Object key) {
						try {
							return (AccBOEPyGAbbreviateKey.valueOf((String) key) != null);
						} catch (IllegalArgumentException e) {
							return false;
						}
					}
				};
		}
		return accepter;
	}
	
	@Override
	public List<AccBOEPyGAbbreviateKey> getKeyList() {
		return new LinkedList<AccBOEPyGAbbreviateKey>( Arrays.asList(AccBOEPyGAbbreviateKey.values()));
	}

}
