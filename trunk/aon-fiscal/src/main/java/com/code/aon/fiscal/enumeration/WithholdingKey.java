package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum WithholdingKey implements IResourceable, IStringEnum   {
	// ***************************************************************
	// NO DESORDENAR LA LISTA, ESTA TABULADA AUNQUE PAREZCA QUE NO!!
	// ***************************************************************
	
	// ---------------------------------------------------------------------
	// key		,Especie.	,Total
	// ---------------------------------------------------------------------
	// W01 = RENDIMIENTOS DINERARIOS PROCEDENTES DEL TRABAJO
	W01 ("W01"	,false		,false	,null),		
	// W02 = RENDIMIENTOS EN ESPECIE PROCEDENTES DEL TRABAJO
	W02 ("W02"	,true		,false	,null),		
	// W03 = RETRIBUCIONES DE MIEMBROS DE CONSEJOS DE ADMINISTRACION Y JUNTAS
	W03 ("W03"	,false		,false	,null),		
	// W04 = RETRIBUCIONES DE PERSONAS QUE TRIBUTAN EN PROPORCION AL VOLUMEN DE OPERACIONES
	W04 ("W04"	,false		,false	,null),		
	// W05 = RENDIMIENTOS DE TRABAJO EN PERIODOS INFERIOES AL AÑO
	W05 ("W05"	,false		,false	,null),		
	// W06 = PRESTACIONES POR DESEMPLEO
	W06 ("W06"	,false		,false	,null),		
	// W07 = PENSIONES Y HABERES PASIVOS
	W07 ("W07"	,false		,false	,null),		
	// W08 = RENDIMIENTOS DINERARIOS SATISFECHOS POR CONTRAPRESTACIONES PROFESIONALES
	W08 ("W08"	,false		,false	,null),		
	// W09 = RENDIMIENTOS EN ESPECIE SATISFECHOS POR CONTRAPRESTACIONES PROFESIONALES
	W09 ("W09"	,true		,false	,null),
	// W10 = RENDIMIENTOS DINERARIOS DE ACTIVIDADES ECONOMICAS 
	W10 ("W10"	,false		,false	,null),		
	// W11 = RENDIMIENTOS EN ESPECIE DE ACTIVIDADES ECONOMICAS
	W11 ("W11"	,true		,false	,null),
	// W12 = RENDIMIENTOS DINERARIOS DE PREMIOS, CONCURSOS, RIFAS O COMBINACIONES ALEATORIAS
	W12 ("W12"	,false		,false	,null),		
	// W13 = RENDIMIENTOS EN ESPECIE DE PREMIOS, CONCURSOS, RIFAS O COMBINACIONES ALEATORIAS
	W13 ("W13"	,true		,false	,null),
	// W14 = RETENCIONES DINERARIOS SOBRE RENDIMIENTOS DE ACTIVIDADES AGRICOLAS, GANADERAS O FORESTALES.
	W14 ("W14"	,false		,false	,null),		
	// W15 = RETENCIONES EN ESPECIE SOBRE RENDIMIENTOS DE ACTIVIDADES AGRICOLAS, GANADERAS O FORESTALES.
	W15 ("W15"	,true		,false	,null),
	// W16 = CONTRAPRESTACIONES POR LA CESION DE DERECHOS DE IMAGEN.
	W16 ("W16"	,false		,false	,null),		
	// W17 = RENDIMIENTOS NO COMPRENDIDOS EN APARTADOS ANTERIORES.
	W17 ("W17"	,false		,false	,null),		
	// WT = TOTAL
	WT ("WT"	,true		,true	,new WithholdingKey[]{WithholdingKey.W01,WithholdingKey.W02,WithholdingKey.W03,
														  WithholdingKey.W04,WithholdingKey.W05,WithholdingKey.W06,
														  WithholdingKey.W07,WithholdingKey.W08,WithholdingKey.W09,
														  WithholdingKey.W10,WithholdingKey.W11,WithholdingKey.W12,
														  WithholdingKey.W13,WithholdingKey.W14,WithholdingKey.W15,
														  WithholdingKey.W16,WithholdingKey.W17});

	
	private String key;
	private boolean inKind;
	private boolean total;
	private WithholdingKey[] affectedKeys;

	private WithholdingKey( String key, boolean inKind, boolean total,WithholdingKey[] affectedKeys) {
		this.key = key;
		this.inKind = inKind;
		this.total = total;
		this.affectedKeys = affectedKeys;
	}
	
	public String getKey() {
		return key;
	}
	@Override
	public String getValue() {
		return getKey();
	}
	
	public boolean isInKind() {
		return inKind;
	}
	public boolean isTotal() {
		return total;
	}
	public WithholdingKey[] getAffectedKeys() {
		return affectedKeys;
	}

	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_withholding_keys_";

    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }

}