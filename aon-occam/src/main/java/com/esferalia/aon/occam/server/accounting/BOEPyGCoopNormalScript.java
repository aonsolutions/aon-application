package com.esferalia.aon.occam.server.accounting;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.HEADER0;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.HEADER1;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.HEADER2;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.TOTAL0;
import static com.esferalia.aon.occam.api.model.AccountBalanceLineStyle.LEAF;

import com.esferalia.aon.occam.api.model.AccountBalanceLineStyle;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;

public class BOEPyGCoopNormalScript extends BalanceScript {
	
	private static final long serialVersionUID = 3036814786119737622L;
	private IAccMiningKeyAccept accepter;
	
	private static enum AccBOEPyGCoopNormalScript implements IBalanceKey{
		CEA0(0,HEADER0,"A)","OPERACIONES CONTINUADAS",null,null)
			,CEA01(1,HEADER1,"1.","Importe neto de la cifra de negocios.",null,"CEA01A+CEA01B")
				,CEA01A(2,LEAF,"a)","Ventas.","sap({700,701,702,703,704})-sdp({706,708,709})",null)
				,CEA01B(2,LEAF,"b)","Prestaciones de servicios.","sap({705})",null)
			,CEA02(1,LEAF,"2.","Variación de existencias de productos terminados y en curso de fabricación.","sap({71,7930})-sdp({6930})",null)
			,CEA03(1,LEAF,"3.","Trabajos realizados por la cooperativa con su estructura interna.","sap({73})",null)
			,CEA04(1,HEADER1,"4.","Aprovisionamientos.",null,"CEA04A+CEA04B+CEA04C+CEA04D+CEA04E")
				,CEA04A(2,LEAF,"a)","Consumo de mercaderías.","sap({600,6060,6080,6090,610})",null)
				,CEA04B(2,LEAF,"b)","Consumo de materias primas y otras materias consumibles.","sap({601,602,6061,6062,6081,6082,6091,6092,611,612})",null)
				,CEA04C(2,LEAF,"c)","Consumo de existencias de socios.","sap({605,6063,6083,6093,617})",null)
				,CEA04D(2,LEAF,"d)","Trabajos realizados por otras empresas.","sap({607})",null)
				,CEA04E(2,LEAF,"e)","Deterioro de mercaderías, materias primas y otros aprovisionamientos.","sap({7931,7932,7933})-sdp({6931,6932,6933})",null)
			,CEA05(1,HEADER1,"5.","Otros ingresos de explotación.",null,"CEA05A+CEA05B+CEA05C")
				,CEA05A(2,LEAF,"a)","Ingresos accesorios y otros de gestión corriente.","sap({75})-sap({756,7570,7570,7571,7572,7573})",null)
				,CEA05B(2,LEAF,"b)","Subvenciones de explotación incorporadas al resultado del ejercicio.","sap({740, 747})",null)
				,CEA05C(2,LEAF,"c)","Ingresos por operaciones con socios.","sap({756})",null)
			,CEA06(1,HEADER1,"6.","Gastos de personal.",null,"CEA06A+CEA06B+CEA06C+CEA06D")
				,CEA06A(2,LEAF,"a)","Sueldos, salarios y asimilados.","sap({640,641,6450})",null)
				,CEA06B(2,LEAF,"b)","Servicios de trabajo de socios.","sap({647})",null)
				,CEA06C(2,LEAF,"c)","Cargas sociales.","sap({642,643,649})",null)
				,CEA06D(2,LEAF,"d)","Provisiones.","sap({7950,7957})-sdp({644,6457})",null)
			,CEA07(1,HEADER1,"7.","Otros gastos de explotación.",null,"CEA07A+CEA07B+CEA07C+CEA07D")
				,CEA07A(2,LEAF,"a)","Servicios exteriores.","sap({62})",null)
				,CEA07B(2,LEAF,"b)","Tributos.","sap({636,639})-sdp({631,634})",null)
				,CEA07C(2,LEAF,"c)","Pérdidas, deterioro y variación de provisiones por operaciones comerciales.","sap({794,7954})-sdp({650,694,695})",null)
				,CEA07D(2,LEAF,"d)","Otros gastos de gestión corriente","sap({651,659})",null)
			,CEA08 (1,LEAF,"8.","Amortización del inmovilizado.","sap({68})",null)
			,CEA09 (1,LEAF,"9.","Imputación de subvenciones de inmovilizado no financiero y otras.","sap({746})",null)
			,CEA010(1,LEAF,"10.","Excesos de provisiones.","sap({7951,7952,7955,7956})",null)
			,CEA011(1,HEADER1,"11.","Deterioro y resultado por enajenaciones del inmovilizado.",null,"CEA011A+CEA011B")
				,CEA011A(2,LEAF,"a)","Deterioros y pérdidas.","sap({690,691,692,790,791,792})",null)
				,CEA011B(2,LEAF,"b)","Resultados por enajenaciones y otras.","sap({670,671,672,770,771,772})",null)
			,CEA012(1,HEADER1,"12.","Fondo de Educaciíon, Formación y Promoción.",null,"CEA012A+CEA012B")
				,CEA012A(2,LEAF,"a)","Dotación.","sap({657})",null)
				,CEA012B(2,LEAF,"b)","Subvenciones, donaciones y ayudas y sanciones.","sap({7570,7571,7572,7573})",null)
			,CEA013(1,HEADER1,"13.","Otros Resultados.","sap({778})-sdp({678})",null)
		,CEA1(0,TOTAL0,"A.1)","RESULTADO DE EXPLOTACIÓN (1+2+3+4+5+6+7+8+9+10+11+12+13)",null,"CEA01+CEA02+CEA03+CEA04+CEA05+CEA06+CEA07+CEA08+CEA09+CEA010+CEA011+CEA012+CEA013")
			,CEA14(1,HEADER1,"14.","Ingresos financieros.",null,"CEA14A+CEA14B")
				,CEA14A(2,HEADER2,"a)","De participaciones en instrumentos de patrimonio.",null,"CEA14A1+CEA14A2")
					,CEA14A1(3,LEAF,"a1)","En empresas del grupo y asociadas.","sap({7600,7601})",null)
					,CEA14A2(3,LEAF,"a2)","En terceros.","sap({7602,7603})",null)
				,CEA14B(2,HEADER2,"b)","De valores negociables y otros instrumentos financieros.",null,"CEA14B1+CEA14B2+CEA14B3")
					,CEA14B1(3,LEAF,"b1)","De empresas del grupo y asociadas.","sap({7610,7611,76200,76201,76210,76211})",null)
					,CEA14B2(3,LEAF,"b2)","De terceros.","sap({7612,7613,76202,76203,76212,76213,767,769})",null)
					,CEA14B3(3,LEAF,"b3)","De socios.",null,null)
			,CEA15(1,HEADER1,"15.","Gastos financieros.",null,"CEA15A+CEA15B+CEA15C+CEA15D")
				,CEA15A(2,LEAF,"a)","Por deudas con empresas del grupo y asociadas.","sap({6610,6611,6615,6616,6620,6621,6640,6641,6650,6651,6654,6655})",null)
				,CEA15B(2,LEAF,"b)","Por deudas con terceros.","sap({6612,6613,6617,6618,6622,6623,6624,6642,6643,6652,6653,6656,6657,669})",null)
				,CEA15C(2,LEAF,"c)","Por actualización de provisiones","sap({660})",null)
				,CEA15D(2,LEAF,"d)","Intereses y retorno obligatorio de las aportaciones al capital social y de otros fondos calificados con características de deuda","sap({6647})",null)
			,CEA16(1,HEADER1,"16.","Variación de valor razonable en instrumentos financieros.",null,"CEA16A+CEA16B")
				,CEA16A(2,LEAF,"a)","Cartera de negociación y otros.","sap({7630,7631,7633})-sdp({6630,6631,6633})",null)
				,CEA16B(2,LEAF,"b)","Imputación al resultado del ejercicio por activos financieros disponibles para la venta.","sap({7632})-sdp({6632})",null)
			,CEA17(1,LEAF,"15.","Diferencias de cambio.","sap({768})-sdp({668})",null)
			,CEA18(1,HEADER1,"16.","Deterioro y resultado por enajenaciones de instrumentos financieros.",null,"CEA18A+CEA18B")
				,CEA18A(2,LEAF,"a)","Deterioros y pérdidas.","sap({696,697,698,699,796,797,798,799})",null)
				,CEA18B(2,LEAF,"b)","Resultados por enajenaciones y otras.","sap({666,667,673,675,766,773,775})",null)
		,CEA2(0,TOTAL0,"A.2)","RESULTADO FINANCIERO (14+15+16+17+18)",null,"CEA14+CEA15+CEA16+CEA17+CEA18")
		,CEA3(0,TOTAL0,"A.3)","RESULTADO ANTES DE IMPUESTOS (A.1+A.2)",null,"CEA1+CEA2")
			,CEA19(1,LEAF,"17.","Impuestos sobre beneficios.","sap({6301,638})-sdp({6300,633})",null)
		,CEA4(0,TOTAL0,"A.4)","RESULTADO DEL EJERCICIO PROCEDENTE DE OPERACIONES CONTINUADAS (A.3+17)",null,"CEA3+CEA19")
		,CEB0(0,TOTAL0,"B)","OPERACIONES INTERRUMPIDAS",null,null)
			,CEA20(1,LEAF,"18.","Resultado del ejercicio procedente de operaciones interrumpidas neto de impuestos.",null,null)
		,CEA5(0,TOTAL0,"A.5)","RESULTADO DEL EJERCICIO (A.4+18)",null,"CEA4+CEA20")
	 	;
	
		private int level;
		private AccountBalanceLineStyle type;
		private String prefix;
		private String name;
		private String  initialExpressionProvider;
		private String  computeExpressionProvider;
		
		private AccBOEPyGCoopNormalScript(int level
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
							return (AccBOEPyGCoopNormalScript.valueOf((String) key) != null);
						} catch (IllegalArgumentException e) {
							return false;
						}
					}
				};
		}
		return accepter;
	}

	@Override
	public List<AccBOEPyGCoopNormalScript> getKeyList() {
		return new LinkedList<AccBOEPyGCoopNormalScript>( Arrays.asList(AccBOEPyGCoopNormalScript.values()));
	}

}
