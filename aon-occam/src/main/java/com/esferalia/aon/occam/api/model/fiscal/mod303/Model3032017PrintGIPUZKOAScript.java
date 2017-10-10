package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C002;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C003;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C004;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C005;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C006;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C007;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C008;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C009;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C010;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C011;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C012;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C013;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C014;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C015;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C016;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C017;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C018;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C019;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C020;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C021;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C022;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C023;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C024;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C025;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C026;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C027;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C028;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C029;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C030;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C031;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C032;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C035;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C039;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C040;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C041;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C042;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C043;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C044;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C045;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C046;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C047;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C048;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C049;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_C050;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_X002;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_X004;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_X006;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_X008;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_X010;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.GP_X012;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Model3032017PrintGIPUZKOAScript implements IModelScript<Mod303Key> {
	
	
	 DVG01 ("IVA devengado",null,TITLE)
	,DVG02 (null						,new Mod303Key[]{GP_C002	,GP_X002	,GP_C003})
	,DVG03 (getDescription(GP_C004)		,new Mod303Key[]{GP_C004	,GP_X004	,GP_C005})
	,DVG04 (null						,new Mod303Key[]{GP_C006	,GP_X006	,GP_C007})
	,DVG05 (getDescription(GP_C039)		,new Mod303Key[]{GP_C039	,null		,GP_C040})
	,DVG06 (null						,new Mod303Key[]{GP_C008	,GP_X008	,GP_C009})
	,DVG07 (getDescription(GP_C010)		,new Mod303Key[]{GP_C010	,GP_X010	,GP_C011})
	,DVG08 (null						,new Mod303Key[]{GP_C012	,GP_X012	,GP_C013})
	,DVG09 (getDescription(GP_C041)		,new Mod303Key[]{GP_C041	,null		,GP_C042})
	,DVG10 (getDescription(GP_C014)		,new Mod303Key[]{GP_C014	,null		,GP_C015})
	,DVG11 (getDescription(GP_C043)		,new Mod303Key[]{GP_C043	,null		,GP_C044})
	,DVG12 (GP_C016.getDescription()	,new Mod303Key[]{null		,null	  	,GP_C016})
	,EMPTY0(null						,null)
	
	,DED01 ("IVA deducible",null,TITLE)
	,DED02 (getDescription(GP_C017)		,new Mod303Key[]{GP_C017	,null		,GP_C018})
	,DED03 (getDescription(GP_C019)		,new Mod303Key[]{GP_C019	,null		,GP_C020})
	,DED04 (getDescription(GP_C021)		,new Mod303Key[]{GP_C021	,null		,GP_C022})
	,DED05 (getDescription(GP_C045)		,new Mod303Key[]{GP_C045	,null		,GP_C046})
	,DED06 (GP_C023.getDescription() 	,new Mod303Key[]{null		,null		,GP_C023})
	,DED07 (GP_C024.getDescription() 	,new Mod303Key[]{null		,null		,GP_C024})
	,DED08 (GP_C025.getDescription() 	,new Mod303Key[]{null		,null		,GP_C025})
	
	,EMPTY1(null						,null)
	,R000 ("Resultado",null,TITLE)
	,R001 (GP_C026.getDescription()		,new Mod303Key[]{null		,null		,GP_C026})
	,R002 (GP_C027.getDescription()		,new Mod303Key[]{null		,null		,GP_C027})
	,R003 (GP_C028.getDescription()		,new Mod303Key[]{null		,null		,GP_C028})
	,R004 (GP_C029.getDescription()		,new Mod303Key[]{null		,null		,GP_C029})
	,R005 (GP_C035.getDescription()		,new Mod303Key[]{null		,null		,GP_C035})
	
	,EMPTY2(null						,null)
	,ADC00 ("Informaci\u00F3n adicional",null,TITLE)
	,ADC01 (GP_C030.getDescription() 	,new Mod303Key[]{GP_C030	,null		,null})
	,ADC02 (GP_C031.getDescription() 	,new Mod303Key[]{GP_C031	,null		,null})
	,ADC03 (GP_C032.getDescription() 	,new Mod303Key[]{GP_C032	,null		,null})
	,ADC04 ("Exclusivamente para sujetos pasivos en R\u00E9gimen especial del criterio de caja y para",null)
	,ADC05 (getDescription(GP_C047)		,new Mod303Key[]{GP_C047	,null		,GP_C048})	
	,ADC06 (getDescription(GP_C049)		,new Mod303Key[]{GP_C049	,null		,GP_C050})
	
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017PrintGIPUZKOAScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	private static String getDescription(Mod303Key key) {
		return AonStringUtils.trim(AonStringUtils.substringBefore(key.getDescription(), AonStringUtils.HYPHEN));
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
		return getInfoKeys()!= null && getInfoKeys().length > 0 && getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE;
	}
	@Override
	public boolean isTitle() {
		return getInfoKeys()!= null && getInfoKeys().length > 0 && getInfoKeys()[0] == TITLE;
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
