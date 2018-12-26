package com.esferalia.aon.occam.server.accounting;

public enum AccBOEPyGAbbreviateKey implements IBalanceKey{
	
	  CEA001(1,true , "1." ,"Importe neto de la cifra de negocios.","sap({700,701,702,703,704})-sdp({706,708,709})",null)
	 ,CEA002(1,true , "2." ,"Variación de existencias de productos terminados y en curso de fabricación.","sap({71,7930})-sdp({6930})",null)	
	 ,CEA003(1,true , "3." ,"Trabajos realizados por la empresa para su activo.","sap({73})",null)
	 ,CEA004(1,true , "4." ,"Aprovisionamientos.","sap({600,601,602,606,607,608,609,61,7931,7932,7933})-sdp({6931,6932,6933})",null)
	 ,CEA005(1,true , "5." ,"Otros ingresos de explotación.","sap({740, 747,75})",null)
	 ,CEA006(1,true , "6." ,"Gastos de personal.","sap({64,7950,7957})",null)
	 ,CEA007(1,true , "7." ,"Otros gastos de explotación.","sap({62,636,639,794,7954})-sdp({631,634,65,694,695})",null)
	 ,CEA008(1,true , "8." ,"Amortización del inmovilizado.","sap({68})",null)
	 ,CEA009(1,true , "9." ,"Imputación de subvenciones de inmovilizado no financiero y otras.","sap({746})",null)
	 ,CEA010(1,true ,"10." ,"Excesos de provisiones.","sap({7951,7952,7955,7956})",null)
	 ,CEA011(1,true ,"11." ,"Deterioro y resultado por enajenaciones del inmovilizado.","sap({670,671,672,690,691,692,770,771,772,778,790,791,792})-sdp({678})",null)
	 ,CEA   (0,false,"A)"  ,"RESULTADO DE EXPLOTACIÓN (1+2+3+4+5+6+7+8+9+10+11)",null,"CEA001+CEA002+CEA003+CEA004+CEA005+CEA006+CEA007+CEA008+CEA009+CEA010+CEA011")
	 
	 ,CEB012(1,true ,"12." ,"Ingresos financieros.","sap({760,761,762,767,769})",null)
	 ,CEB013(1,true ,"13." ,"Gastos financieros.","sap({660,661,662,664,665,669})",null)
	 ,CEB014(1,true ,"14." ,"Variación de valor razonable en instrumentos financiero.","sap({763})-sdp({663})",null)
	 ,CEB015(1,true ,"15." ,"Diferencias de cambio.","sap({768})-sdp({668})",null)
	 ,CEB016(1,true ,"16." ,"Deterioro y resultado por enajenaciones de instrumentos financieros.","sap({666,667,673,675,696,697,698,699,766,773,775,796,797,798,799})",null)
	 ,CEB   (0,false,"B)"  ,"RESULTADO FINANCIERO (12+13+14+15+16)",null,"CEB012+CEB013+CEB014+CEB015+CEB016")
	 
	 ,CEC   (0,false,"C)"  ,"RESULTADO ANTES DE IMPUESTOS (A+B)",null,"CEA+CEB")
	 ,CED001(1,true ,"17." ,"Impuestos sobre beneficios.","sap({6301,638})-sdp({6300,633})",null)
	 ,CED   (0,false,"D)"  ,"RESULTADO DEL EJERCICIO (C+17)",null,"CEC+CED001")
 	;

	private int level;
	private boolean leaf;
	private String prefix;
	private String name;
	private String  initialExpressionProvider;
	private String  computeExpressionProvider;
	
	private AccBOEPyGAbbreviateKey(int level
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
