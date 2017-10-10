package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C001;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C002;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C003;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C010;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C011;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C012;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C019;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C020;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C021;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C028;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C030;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C031;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C032;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C033;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C034;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C035;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C036;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C037;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C038;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C039;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C040;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C041;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C042;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C043;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C044;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C045;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C050;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C051;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C052;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C060;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C061;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C062;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C063;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C080;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C180;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C181;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C182;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C183;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C204;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C205;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C206;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C207;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C208;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C209;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C213;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C214;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C215;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C216;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C217;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C218;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C222;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C223;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C224;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C225;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C226;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C227;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C370;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C371;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C372;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C373;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C374;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C375;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C376;
import static com.esferalia.aon.occam.api.model.type.Mod303Key.AR_C377;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Model3032017PrintARABAScript implements IModelScript<Mod303Key> {
	
	 DVG01 ("IVA devengado"				,null,TITLE)
	,DVG02 (null						,new Mod303Key[]{AR_C001,AR_C002,AR_C003})
	,DVG03 (getDescription(AR_C204)		,new Mod303Key[]{AR_C204,AR_C205,AR_C206})
	,DVG04 (null						,new Mod303Key[]{AR_C207,AR_C208,AR_C209})
	,DVG05 (getDescription(AR_C370)		,new Mod303Key[]{AR_C370,null	,AR_C371})
	,DVG06 (getDescription(AR_C372)		,new Mod303Key[]{AR_C372,null	,AR_C373})
	,DVG07 (null						,new Mod303Key[]{AR_C010,AR_C011,AR_C012})
	,DVG08 (getDescription(AR_C213)		,new Mod303Key[]{AR_C213,AR_C214,AR_C215})
	,DVG09 (null						,new Mod303Key[]{AR_C216,AR_C217,AR_C218})
	,DVG10 (getDescription(AR_C374)		,new Mod303Key[]{AR_C374,null	,AR_C375})
	,DVG11 (null						,new Mod303Key[]{AR_C019,AR_C020,AR_C021})
	,DVG12 (getDescription(AR_C222)		,new Mod303Key[]{AR_C222,AR_C223,AR_C224})
	,DVG13 (null						,new Mod303Key[]{AR_C225,AR_C226,AR_C227})
	,DVG14 (getDescription(AR_C376)		,new Mod303Key[]{AR_C376,null	,AR_C377})
	,DVG15 (getDescription(AR_C028)		,new Mod303Key[]{null	,null	,AR_C028})
	,EMPTY0(null						,null)
	
	,DED01 ("IVA deducible"				,null,TITLE)
	,DED02 (AR_C030.getDescription()	,new Mod303Key[]{null	,null	,AR_C030})
	,DED03 (AR_C031.getDescription()	,new Mod303Key[]{null	,null	,AR_C031})
	,DED04 (AR_C032.getDescription()	,new Mod303Key[]{null	,null	,AR_C032})
	,DED05 (AR_C033.getDescription()	,new Mod303Key[]{null	,null	,AR_C033})
	,DED06 (AR_C034.getDescription()	,new Mod303Key[]{null	,null	,AR_C034})
	,DED07 (AR_C035.getDescription()	,new Mod303Key[]{null	,null	,AR_C035})
	,DED08 (AR_C036.getDescription()	,new Mod303Key[]{null	,null	,AR_C036})
	,DED09 (AR_C037.getDescription()	,new Mod303Key[]{null	,null	,AR_C037})
	,DED10 (AR_C038.getDescription()	,new Mod303Key[]{null	,null	,AR_C038})
	,EMPTY1(null						,null)
	,DED11 (AR_C039.getDescription()	,new Mod303Key[]{null	,null	,AR_C039},TITLE)
	,EMPTY3(null						,null)
	
	,R000 ("Resultado"					,null,TITLE)
	,R001 (AR_C039.getDescription()		,new Mod303Key[]{null	,null	,AR_C039})
	,R002 (AR_C040.getDescription()		,new Mod303Key[]{null	,null	,AR_C040})
	,R003 (AR_C041.getDescription()		,new Mod303Key[]{null	,null	,AR_C041})
	,R004 (AR_C042.getDescription()		,new Mod303Key[]{null	,null	,AR_C042})
	,R005 (AR_C043.getDescription()		,new Mod303Key[]{null	,null	,AR_C043})
	,R006 (AR_C044.getDescription()		,new Mod303Key[]{null	,null	,AR_C044})
	,R007 (AR_C045.getDescription()		,new Mod303Key[]{null	,null	,AR_C045})
	,R008 (AR_C060.getDescription()		,new Mod303Key[]{null	,null	,AR_C060})
	,R009 (AR_C061.getDescription()		,new Mod303Key[]{null	,null	,AR_C061})
	,R010 (AR_C062.getDescription()		,new Mod303Key[]{null	,null	,AR_C062})
	,R011 (AR_C063.getDescription()		,new Mod303Key[]{null	,null	,AR_C063})
	,R012 (AR_C080.getDescription()		,new Mod303Key[]{null	,null	,AR_C080})
	
	,EMPTY4(null						,null)
	,ADC00 ("Informaci\u00F3n adicional",null,TITLE)
	,ADC01 (AR_C050.getDescription() 	,new Mod303Key[]{AR_C050,null	,null	})
	,ADC02 (AR_C051.getDescription() 	,new Mod303Key[]{AR_C051,null	,null	})
	,ADC03 (AR_C052.getDescription() 	,new Mod303Key[]{AR_C052,null	,null	})
	,ADC04 ("Exclusivamente para aquellos sujetos pasivos acogidos al R\u00E9gimen especial del criterio de caja y para aquellos que sean destinatarios de operaciones afectadas por el mismo:",null)
	,ADC05 (AR_C180.getDescription()	,new Mod303Key[]{AR_C180	,null	,AR_C181})	
	,ADC06 (AR_C182.getDescription()	,new Mod303Key[]{AR_C182	,null	,AR_C183})
	
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032017PrintARABAScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return (this == R000);
	};
}
