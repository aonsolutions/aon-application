// MODELO 417 - 11.- Operaciones realizadas en el ejercicio
package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public enum Model3032025CANARIASLastPeriodScript2 implements IModelScript<Mod303Key> {
	
//	 T00 ("Actividades con reg\u00EDmenes de deducci\u00F3n diferenciados",null,TITLE)
	 
	 T01 ("I.G.I.C. deducible: Grupo 1",null,TITLE)
	,G101("I.G.I.C. deducible en operaciones interiores bienes y servicios corrientes"		 ,new Mod303Key[]{Mod303Key.CA_C200,Mod303Key.CA_C201},NONE)
	,G102("I.G.I.C. deducible en operaciones interiores bienes de inversi\u00F3n"			 ,new Mod303Key[]{Mod303Key.CA_C202,Mod303Key.CA_C203},NONE)
	,G103("I.G.I.C. deducible por importaciones bienes y servicios corrientes"				 ,new Mod303Key[]{Mod303Key.CA_C204,Mod303Key.CA_C205},NONE)
	,G104("I.G.I.C. deducible por importaciones bienes de inversi\u00F3n"					 ,new Mod303Key[]{Mod303Key.CA_C206,Mod303Key.CA_C207},NONE)
	,G105("Compensaciones en r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca",new Mod303Key[]{Mod303Key.CA_C208,Mod303Key.CA_C209},NONE)
	,G106("Rectificaci\u00F3n de deducciones"												 ,new Mod303Key[]{Mod303Key.CA_C210,Mod303Key.CA_C211},NONE)
	,G107("Regularizaci\u00F3n de bienes de inversi\u00F3n"									 ,new Mod303Key[]{null             ,Mod303Key.CA_C212},NONE)
	,G108("Suma de deducciones"																 ,new Mod303Key[]{null             ,Mod303Key.CA_C213},COMPUTE)
	
	,T02 ("I.G.I.C. deducible: Grupo 2",null,TITLE)
	,G201("I.G.I.C. deducible en operaciones interiores bienes y servicios corrientes"		 ,new Mod303Key[]{Mod303Key.CA_C214,Mod303Key.CA_C215},NONE)
	,G202("I.G.I.C. deducible en operaciones interiores bienes de inversi\u00F3n"			 ,new Mod303Key[]{Mod303Key.CA_C216,Mod303Key.CA_C217},NONE)
	,G203("I.G.I.C. deducible por importaciones bienes y servicios corrientes"				 ,new Mod303Key[]{Mod303Key.CA_C218,Mod303Key.CA_C219},NONE)
	,G204("I.G.I.C. deducible por importaciones bienes de inversi\u00F3n"					 ,new Mod303Key[]{Mod303Key.CA_C220,Mod303Key.CA_C221},NONE)
	,G205("Compensaciones en r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca",new Mod303Key[]{Mod303Key.CA_C222,Mod303Key.CA_C223},NONE)
	,G206("Rectificaci\u00F3n de deducciones"												 ,new Mod303Key[]{Mod303Key.CA_C224,Mod303Key.CA_C225},NONE)
	,G207("Regularizaci\u00F3n de bienes de inversi\u00F3n"									 ,new Mod303Key[]{null             ,Mod303Key.CA_C226},NONE)
	,G208("Suma de deducciones"																 ,new Mod303Key[]{null             ,Mod303Key.CA_C227},COMPUTE)
	
	,T03 ("I.G.I.C. deducible: Grupo 3",null,TITLE)
	,G301("I.G.I.C. deducible en operaciones interiores bienes y servicios corrientes"		 ,new Mod303Key[]{Mod303Key.CA_C228,Mod303Key.CA_C229},NONE)
	,G302("I.G.I.C. deducible en operaciones interiores bienes de inversi\u00F3n"			 ,new Mod303Key[]{Mod303Key.CA_C230,Mod303Key.CA_C231},NONE)
	,G303("I.G.I.C. deducible por importaciones bienes y servicios corrientes"				 ,new Mod303Key[]{Mod303Key.CA_C232,Mod303Key.CA_C233},NONE)
	,G304("I.G.I.C. deducible por importaciones bienes de inversi\u00F3n"					 ,new Mod303Key[]{Mod303Key.CA_C234,Mod303Key.CA_C235},NONE)
	,G305("Compensaciones en r\u00E9gimen especial de la agricultura, ganader\u00EDa y pesca",new Mod303Key[]{Mod303Key.CA_C236,Mod303Key.CA_C237},NONE)
	,G306("Rectificaci\u00F3n de deducciones"												 ,new Mod303Key[]{Mod303Key.CA_C238,Mod303Key.CA_C239},NONE)
	,G307("Regularizaci\u00F3n de bienes de inversi\u00F3n"									 ,new Mod303Key[]{null             ,Mod303Key.CA_C240},NONE)
	,G308("Suma de deducciones"																 ,new Mod303Key[]{null             ,Mod303Key.CA_C241},COMPUTE)
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032025CANARIASLastPeriodScript2(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
	}
	
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return false;
	}

}
