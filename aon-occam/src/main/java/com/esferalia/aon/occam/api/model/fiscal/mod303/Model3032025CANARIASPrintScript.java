package com.esferalia.aon.occam.api.model.fiscal.mod303;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Model3032025CANARIASPrintScript implements IModelScript<Mod303Key> {
	
	 DVG00 ("Liquidaci\u00F3n: IGIC devengado", null, TITLE)
	,DVG01 (getDescription(Mod303Key.CA_DB01), new Mod303Key[]{Mod303Key.CA_DB01, Mod303Key.CA_DT01, Mod303Key.CA_DC01})
	,DVG02 (getDescription(Mod303Key.CA_DB02), new Mod303Key[]{Mod303Key.CA_DB02, Mod303Key.CA_DT02, Mod303Key.CA_DC02})
	,DVG03 (getDescription(Mod303Key.CA_DB03), new Mod303Key[]{Mod303Key.CA_DB03, Mod303Key.CA_DT03, Mod303Key.CA_DC03})
	,DVG04 (getDescription(Mod303Key.CA_DB04), new Mod303Key[]{Mod303Key.CA_DB04, Mod303Key.CA_DT04, Mod303Key.CA_DC04})
	,DVG05 (getDescription(Mod303Key.CA_DB05), new Mod303Key[]{Mod303Key.CA_DB05, Mod303Key.CA_DT05, Mod303Key.CA_DC05})
	,DVG06 (getDescription(Mod303Key.CA_DB06), new Mod303Key[]{Mod303Key.CA_DB06, Mod303Key.CA_DT06, Mod303Key.CA_DC06})
	,DVG07 (getDescription(Mod303Key.CA_DB07), new Mod303Key[]{Mod303Key.CA_DB07, Mod303Key.CA_DT07, Mod303Key.CA_DC07})
	,DVG08 (getDescription(Mod303Key.CA_C019), new Mod303Key[]{Mod303Key.CA_C019,null,Mod303Key.CA_C020 })
	,DVG09 (getDescription(Mod303Key.CA_C021), new Mod303Key[]{Mod303Key.CA_C021,null,Mod303Key.CA_C022 })
	,DVG10 (getDescription(Mod303Key.CA_C023), new Mod303Key[]{Mod303Key.CA_C023,null,Mod303Key.CA_C024 })
	,DVG17 (getDescription(Mod303Key.CA_C025), new Mod303Key[]{null				,null,Mod303Key.CA_C025 })
	,EMPTY0(null,null)
	,DED01 ("Liquidaci\u00F3n: IGIC deducible y Resultado Autoliquidaci\u00F3n", null, TITLE)
	,DED02 (getDescription(Mod303Key.CA_C026), new Mod303Key[]{Mod303Key.CA_C026,null,Mod303Key.CA_C027})
	,DED03 (getDescription(Mod303Key.CA_C028), new Mod303Key[]{Mod303Key.CA_C028,null,Mod303Key.CA_C029})
	,DED04 (getDescription(Mod303Key.CA_C030), new Mod303Key[]{Mod303Key.CA_C030,null,Mod303Key.CA_C031})
	,DED05 (getDescription(Mod303Key.CA_C032), new Mod303Key[]{Mod303Key.CA_C032,null,Mod303Key.CA_C033})
	,DED06 (getDescription(Mod303Key.CA_C034), new Mod303Key[]{Mod303Key.CA_C034,null,Mod303Key.CA_C035})
	,DED09 (getDescription(Mod303Key.CA_C036), new Mod303Key[]{null				,null,Mod303Key.CA_C036})
	,DED10 (getDescription(Mod303Key.CA_C037), new Mod303Key[]{null				,null,Mod303Key.CA_C037})
	,DED11 (getDescription(Mod303Key.CA_C038), new Mod303Key[]{null				,null,Mod303Key.CA_C038})
	,DED12 (getDescription(Mod303Key.CA_C039), new Mod303Key[]{null				,null,Mod303Key.CA_C039})
	,DED13 (getDescription(Mod303Key.CA_C040), new Mod303Key[]{null				,null,Mod303Key.CA_C040})
	,R01   (getDescription(Mod303Key.CA_C041), new Mod303Key[]{null				,null,Mod303Key.CA_C041},TITLE)
	,RES001(getDescription(Mod303Key.CA_C042), new Mod303Key[]{null				,null,Mod303Key.CA_C042})
	,RES002(getDescription(Mod303Key.CA_C043), new Mod303Key[]{null				,null,Mod303Key.CA_C043})
	,RES003(getDescription(Mod303Key.CA_C044), new Mod303Key[]{null				,null,Mod303Key.CA_C044})
	,RES004(getDescription(Mod303Key.CA_C045), new Mod303Key[]{null				,null,Mod303Key.CA_C045},TITLE)
	,EMPTY3(null,null)
	,ADC00 ("Informaci\u00F3n adicional",null,TITLE)
	,ADC01 (getDescription(Mod303Key.CA_C046), new Mod303Key[]{Mod303Key.CA_C046, null, null})
	,ADC02 (getDescription(Mod303Key.CA_C047), new Mod303Key[]{Mod303Key.CA_C047, null, null})
	,EMPTY4(null,null)
	,ADC10 ("Exclusivamente para aquellos sujetos pasivos acogidos al r\u00E9gimen especial " 
			+ "del criterio de caja y para aqu\u00E9llos que sean destinatarios de operaciones " 
			+ "afectadas por el mismo",null,TITLE)
	,ADC11 (getDescription(Mod303Key.CA_C048), new Mod303Key[]{Mod303Key.CA_C048,null,Mod303Key.CA_C049})	
	,ADC12 (getDescription(Mod303Key.CA_C050), new Mod303Key[]{Mod303Key.CA_C050,null,Mod303Key.CA_C051})
	;
	
	private String label;
	private Mod303Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3032025CANARIASPrintScript(String label, Mod303Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
	}
	
	@Override
	public boolean hasGraphicParticularity() {
		return false;
	}

	@Override
	public boolean paintHeaderBefore() {
		return (this == DVG00);
	}
}
