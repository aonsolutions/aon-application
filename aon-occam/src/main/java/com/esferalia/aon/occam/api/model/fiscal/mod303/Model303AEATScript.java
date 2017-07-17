package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model303AEATScript implements IModelScript<Mod303Key> {
	
	 DVG01 ("R\u00E9gimen general. IVA devengado",null,TITLE)
	,DVG02 (null					,new Mod303Key[]{Mod303Key.CT_C01	,Mod303Key.CT_C02	,Mod303Key.CT_C03},INVOICE,DIFF_INVOICE)
	,DVG03 ("R\u00E9gimen general"	,new Mod303Key[]{Mod303Key.CT_C04	,Mod303Key.CT_C05	,Mod303Key.CT_C06},INVOICE,DIFF_INVOICE)
	,DVG04 (null					,new Mod303Key[]{Mod303Key.CT_C07	,Mod303Key.CT_C08	,Mod303Key.CT_C09},INVOICE,DIFF_INVOICE)
	,DVG05 ("Adquisiciones intracomunitarias de bienes y servicios"
		,new Mod303Key[]{Mod303Key.CT_C10	,null			  	,Mod303Key.CT_C11},INVOICE,DIFF_INVOICE)
	,DVG06 ("Otras operaciones con inversi\u00F3n del sujeto pasivo (excepto. adq. intracom)"
		,new Mod303Key[]{Mod303Key.CT_C12	,null			  	,Mod303Key.CT_C13},INVOICE,DIFF_INVOICE)
	,DVG07 ("Modificaci\u00F3n bases y cuotas"
		,new Mod303Key[]{Mod303Key.CT_C14	,null			  	,Mod303Key.CT_C15},INVOICE,DIFF_INVOICE)
	,DVG08 (null
		,new Mod303Key[]{Mod303Key.CT_C16	,Mod303Key.CT_C17	,Mod303Key.CT_C18},INVOICE,DIFF_INVOICE)
	,DVG09 ("Recargo equivalencia"
		,new Mod303Key[]{Mod303Key.CT_C19	,Mod303Key.CT_C20	,Mod303Key.CT_C21},INVOICE,DIFF_INVOICE)
	,DVG10 (null
		,new Mod303Key[]{Mod303Key.CT_C22	,Mod303Key.CT_C23	,Mod303Key.CT_C24},INVOICE,DIFF_INVOICE)
	,DVG11 ("Modificaciones bases y cuotas del recargo de equivalencia"
		,new Mod303Key[]{Mod303Key.CT_C25	,null			  	,Mod303Key.CT_C26},INVOICE,DIFF_INVOICE)
	,DVG12 ("Total cuota devengada"
		,new Mod303Key[]{Mod303Key.CT_C27},COMPUTE)
	
	,DED01 ("R\u00E9gimen general. IVA deducible"
		 ,null,TITLE)
	,DED02 ("Por cuotas soportadas en operaciones interiores corrientes"
		,new Mod303Key[]{Mod303Key.CT_C28	,Mod303Key.CT_C29},INVOICE,DIFF_INVOICE)
	,DED03 ("Por cuotas soportadas en operaciones interiores con bienes de inversi\u00F3n" 
		,new Mod303Key[]{Mod303Key.CT_C30	,Mod303Key.CT_C31},INVOICE,DIFF_INVOICE)
	,DED04 ("Por cuotas soportadas en las importaciones de bienes corrientes"
		,new Mod303Key[]{Mod303Key.CT_C32	,Mod303Key.CT_C33},INVOICE,DIFF_INVOICE)
	,DED05 ("Por cuotas soportadas en las importaciones de bienes de inversi\u00F3n"
		,new Mod303Key[]{Mod303Key.CT_C34	,Mod303Key.CT_C35},INVOICE,DIFF_INVOICE)
	,DED06 ("En adquisiciones intracomunitarias de bienes y servicios corrientes"
		,new Mod303Key[]{Mod303Key.CT_C36	,Mod303Key.CT_C37},INVOICE,DIFF_INVOICE)
	,DED07 ("En adquisiciones intracomunitarias de bienes de inversi\u00F3n"
		,new Mod303Key[]{Mod303Key.CT_C38	,Mod303Key.CT_C39},INVOICE,DIFF_INVOICE)
	,DED08 ("Rectificaci\u00F3n de deducciones"
		,new Mod303Key[]{Mod303Key.CT_C40	,Mod303Key.CT_C41},INVOICE,DIFF_INVOICE)
	,DED09 ("Compensaciones R\u00E9gimen Especial A.G. y P."
		,new Mod303Key[]{Mod303Key.CT_C42},INVOICE,DIFF_INVOICE)
	,DED10 ("Regularizaci\u00F3n inversiones"
		,new Mod303Key[]{Mod303Key.CT_C43},INVOICE,DIFF_INVOICE)
	,DED11 ("Regularizaci\u00F3n por aplicaci\u00F3n del porcentaje definitivo de prorrata"
		,new Mod303Key[]{Mod303Key.CT_C44},INVOICE,DIFF_INVOICE)
	,DED12 ("Total a deducir"
		,new Mod303Key[]{Mod303Key.CT_C45},COMPUTE)
		
	,R01   ("Resultado r\u00E9gimen general"
		,new Mod303Key[]{Mod303Key.CT_C46},COMPUTE)
	
	,R03 ("Informaci\u00F3n adicional"
		 ,null,TITLE)
	,ADC01 ("Entregas intracomunitarias de bienes y servicios"
		,new Mod303Key[]{Mod303Key.CT_C59},INVOICE,DIFF_INVOICE)
	,ADC02 ("Exportaciones y operaciones asimiladas"
		,new Mod303Key[]{Mod303Key.CT_C60},INVOICE,DIFF_INVOICE)
	,ADC03 ("Operaciones no sujetas o con inversi\u00F3n del sujeto pasivo que originan el derecho a deducci\u00F3n"
		,new Mod303Key[]{Mod303Key.CT_C61},INVOICE,DIFF_INVOICE)
	,ADC04 ("Criterio de Caja. Importes devengados en per\u00EDodo de liquidaci\u00F3n seg\u00FAn art. 75 LIVA."
		,new Mod303Key[]{Mod303Key.CT_C62		,Mod303Key.CT_C63},INVOICE,DIFF_INVOICE)	
	,ADC05 ("Criterio de Caja. Cuotas de IVA soportados conforme a la regla general de devengo seg\u00FAn art. 75 LIVA. - Base Imponible"
		,new Mod303Key[]{Mod303Key.CT_C74		,Mod303Key.CT_C75},INVOICE,DIFF_INVOICE)
	
	,R04 ("Resultado"
		 ,null,TITLE)
	,RES001("Regularizaci\u00F3n cuotas art. 80.cinco.5\u00AA LIVA"
		,new Mod303Key[]{Mod303Key.CT_C76},NONE)
	,RES002("Suma de resultados"
		,new Mod303Key[]{Mod303Key.CT_C64},COMPUTE)
	,RES003("% Atribuible a la Administraci\u00F3n del Estado"
		,new Mod303Key[]{Mod303Key.CT_C65},NONE)
	,RES004("Atribuible a la Administraci\u00F3n del Estado"
		,new Mod303Key[]{Mod303Key.CT_C66},COMPUTE)
	,RES005("IVA a la importaci\u00F3n liquidado por la Aduana pendiente de ingreso"  
		,new Mod303Key[]{Mod303Key.CT_C77},NONE)
	,RES006("Cuotas a compensar de periodos anteriores" 
		,new Mod303Key[]{Mod303Key.CT_C67},COMPUTE_KEY)
	,RES007("Exclusivamente para sujetos pasivos que tributan conjuntamente a la Administraci\u00F3n del Estado y a las Diputaciones Forales Resultado de la regularizaci\u00F3n anual" 
		,new Mod303Key[]{Mod303Key.CT_C68},NONE)
	,RES008("Resultado" 
		,new Mod303Key[]{Mod303Key.CT_C69},COMPUTE)
	,RES009("A deducir"
		,new Mod303Key[]{Mod303Key.CT_C70},COMPUTE_KEY)
	,RES010("Resultado de la liquidaci\u00F3n" 
		,new Mod303Key[]{Mod303Key.CT_C71},COMPUTE)
	
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model303AEATScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod303Key[] getKeys() {
		return keys;
	}
	@Override
	public boolean isEnabled() {
		return getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()[0] == TITLE;
	}
	@Override
	public FiscalModelKeyInfo[] getInfoKeys() {
		return infoKeys;
	};
	
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == DVG01);
	};
}
