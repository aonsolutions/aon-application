package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE_KEY;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.NONE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.KeyTypes;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public enum Model3902017BIZKAIAScript2 implements IModelScript<Mod390Key> {
	
	// ---------------------------------------------------------
	// -------------------- IVA DEDUCIBLE ----------------------
	// ---------------------------------------------------------
	 DED01 ("IVA DEDUCIBLE" ,null,null,TITLE)
	,DED02 ("IVA deducible en operaciones interiores, excluidas operaciones intragrupo"
																,new Mod390Key[]{null,Mod390Key.BZ_C060},new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},NONE)
	,DED03 ("IVA deducible en operaciones interiores"			,new Mod390Key[]{null,Mod390Key.BZ_C061},new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},NONE)
	,DED04 ("IVA deducible en importaciones"					,new Mod390Key[]{null,Mod390Key.BZ_C062},new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},NONE)
	,DED05 ("IVA deducible en Adquisiciones intracomunitarias"	,new Mod390Key[]{null,Mod390Key.BZ_C063},new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},NONE)
	,DED06 ("Compensaciones R\u00E9gimen Especial A.G. y P."	,new Mod390Key[]{null,Mod390Key.BZ_C064},new KeyTypes[]{KeyTypes.DEDUCTIBLE_QUOTA},NONE)
	,DED07 ("Regularizaci\u00F3n de bienes de inversion"		,new Mod390Key[]{null,Mod390Key.BZ_C065},null,NONE)
	,DED08 ("Total a deducir"									,new Mod390Key[]{null,Mod390Key.BZ_C066},null,COMPUTE)
		
	// ---------------------------------------------------------
	// -------- TRIBUTACION POR RAZON TERRITORIO ---------------
	// ---------------------------------------------------------
	,TRI00("Tributaci\u00F3n por raz\u00F3n de territorio" ,null,null,TITLE)
	,TRI01("Territorio Com\u00FAn"								,new Mod390Key[]{Mod390Key.BZ_C081,Mod390Key.BZ_C082},null,NONE)
	,TRI02("Araba/\u00C1lava"									,new Mod390Key[]{Mod390Key.BZ_C083,Mod390Key.BZ_C084},null,NONE)
	,TRI03("Gipuzkoa"											,new Mod390Key[]{Mod390Key.BZ_C085,Mod390Key.BZ_C086},null,NONE)
	,TRI04("Bizkaia"											,new Mod390Key[]{Mod390Key.BZ_C087,Mod390Key.BZ_C088},null,NONE)
	,TRI05("Nafarroa/Navarra"									,new Mod390Key[]{Mod390Key.BZ_C089,Mod390Key.BZ_C090},null,NONE)
	,TRI06("Total"												,new Mod390Key[]{Mod390Key.BZ_C091,Mod390Key.BZ_C092},null,COMPUTE)

	// ---------------------------------------------------------
	// ---------------- RESTO LIQUIDACION ----------------------
	// ---------------------------------------------------------
	,LQ000("RESULTADO" ,null,null,TITLE)
	,LQ001(Mod390Key.BZ_C095.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C095},null,COMPUTE)
	,LQ002(Mod390Key.BZ_C120.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C120},null,NONE)
	,LQ004(Mod390Key.BZ_C096.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C096},null,COMPUTE)
	,LQ005(Mod390Key.BZ_C097.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C097},null,COMPUTE_KEY)
	,LQ006(Mod390Key.BZ_C098.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C098},null,COMPUTE)
	,LQ007(Mod390Key.BZ_C099.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C099},null,COMPUTE_KEY)
	,LQ008(Mod390Key.BZ_C100.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C100},null,COMPUTE_KEY)
	,LQ009(Mod390Key.BZ_C110.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C110},null,COMPUTE)
	,LQ010(Mod390Key.BZ_C115.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C115},null,COMPUTE_KEY)
	,LQ011(Mod390Key.BZ_C116.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C116},null,COMPUTE_KEY)
	,LQ012(Mod390Key.BZ_C117.getDescription()					,new Mod390Key[]{null,Mod390Key.BZ_C117},null,COMPUTE)
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	private KeyTypes[] keyTypes;
	
	private Model3902017BIZKAIAScript2(String label, Mod390Key[] keys,KeyTypes[] keyTypes,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
		this.keyTypes = keyTypes;  
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod390Key[] getKeys() {
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
	@Override
	public KeyTypes[] getKeyTypes() {
		return this.keyTypes;
	}
	
}
