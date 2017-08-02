package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.DIFF_INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.INVOICE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032017BIZKAIAScript2 implements IModelScript<Mod303Key> {
	
	// ---------------------------------------------------------
	// -------------------- IVA DEDUCIBLE ----------------------
	// ---------------------------------------------------------
	 DED01 ("IVA DEDUCIBLE" ,null,TITLE)
	,DED02 ("IVA deducible en operaciones interiores"			,new Mod303Key[]{Mod303Key.BZ_C024},INVOICE,DIFF_INVOICE)
	,DED03 ("IVA deducible en importaciones"					,new Mod303Key[]{Mod303Key.BZ_C025},INVOICE,DIFF_INVOICE)
	,DED04 ("IVA deducible en Adquisiciones intracomunitarias"	,new Mod303Key[]{Mod303Key.BZ_C026},INVOICE,DIFF_INVOICE)
	,DED05 ("Compensaciones R\u00E9gimen Especial A.G. y P."	,new Mod303Key[]{Mod303Key.BZ_C027},INVOICE,DIFF_INVOICE)
	,DED06 ("Regularizaci\u00F3n de bienes de inversion"		,new Mod303Key[]{Mod303Key.BZ_C028},INVOICE,DIFF_INVOICE)
	,DED07 ("Total a deducir"									,new Mod303Key[]{Mod303Key.BZ_C030},COMPUTE)
		
	// ---------------------------------------------------------
	// ---------------- RESTO LIQUIDACION ----------------------
	// ---------------------------------------------------------
	,LQ000("RESULTADO" ,null,TITLE)
	,LQ001("Diferencia"												,new Mod303Key[]{Mod303Key.BZ_C031},COMPUTE)
	,LQ002("Regularizaci\u00F3n cuotas art. 80.cinco.5\u00AA LIVA"	,new Mod303Key[]{Mod303Key.BZ_C045},NONE)
	,LQ003("Porcentaje de tributaci\u00F3n en Bizkaia"				,new Mod303Key[]{Mod303Key.BZ_C032},NONE)
	,LQ004("Cuota atribuible a Bizkaia"								,new Mod303Key[]{Mod303Key.BZ_C033},COMPUTE)
	,LQ005("Cuota a compensar de periodos anteriores"				,new Mod303Key[]{Mod303Key.BZ_C034},NONE)
	,LQ006("Resultado de la regularizaci\u00F3n anual"				,new Mod303Key[]{Mod303Key.BZ_C035},NONE)
	,LQ007("Resultado"												,new Mod303Key[]{Mod303Key.BZ_C036},COMPUTE)
//	,LQ008("A compensar"											,new Mod303Key[]{Mod303Key.BZ_C038},COMPUTE)
//	,LQ009("A devolver"												,new Mod303Key[]{Mod303Key.BZ_C039},COMPUTE)
//	,LQ010("A ingresar"												,new Mod303Key[]{Mod303Key.BZ_C040},COMPUTE)
	,LQ011("Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: ingresado anteriormente"
																	,new Mod303Key[]{Mod303Key.BZ_C041},NONE)
	,LQ012("Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: devuelto anteriormente"
																	,new Mod303Key[]{Mod303Key.BZ_C042},NONE)
	,LQ013("Total deuda tributaria"									,new Mod303Key[]{Mod303Key.BZ_C043},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017BIZKAIAScript2(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return false;
	};
}
