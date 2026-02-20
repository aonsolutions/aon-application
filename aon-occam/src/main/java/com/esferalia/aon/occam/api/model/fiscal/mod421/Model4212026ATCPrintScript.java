package com.esferalia.aon.occam.api.model.fiscal.mod421;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Model4212026ATCPrintScript implements IModelScript<Mod421Key> {
	
	  RES06(getDescription(Mod421Key.C06), new Mod421Key[]{Mod421Key.C06})
	 ,RES07(getDescription(Mod421Key.C07), new Mod421Key[]{Mod421Key.C07})
	 ,RES08(getDescription(Mod421Key.C08), new Mod421Key[]{Mod421Key.C08})
	 ,RES09(getDescription(Mod421Key.C09), new Mod421Key[]{Mod421Key.C09})
//	 ,RES10T1(getDescription(Mod421Key.C10T1), new Mod421Key[]{Mod421Key.C10T1})
//	 ,RES10T2(getDescription(Mod421Key.C10T2), new Mod421Key[]{Mod421Key.C10T2})
//	 ,RES10T3(getDescription(Mod421Key.C10T3), new Mod421Key[]{Mod421Key.C10T3})
	 ,RES11(getDescription(Mod421Key.C11), new Mod421Key[]{Mod421Key.C11})
	 ,RES12(getDescription(Mod421Key.C12), new Mod421Key[]{Mod421Key.C12})
	 ,RES13(getDescription(Mod421Key.C13), new Mod421Key[]{Mod421Key.C13})
	 ,RES14(getDescription(Mod421Key.C14), new Mod421Key[]{Mod421Key.C14})
	 ,RES15(getDescription(Mod421Key.C15), new Mod421Key[]{Mod421Key.C15})
	 ,RES16(getDescription(Mod421Key.C16), new Mod421Key[]{Mod421Key.C16})
	 ,RES17(getDescription(Mod421Key.C17), new Mod421Key[]{Mod421Key.C17})
	 ,RES18(getDescription(Mod421Key.C18), new Mod421Key[]{Mod421Key.C18})
	 ,RES19(getDescription(Mod421Key.C19), new Mod421Key[]{Mod421Key.C19})
	 
//	 DVG00 ("Liquidaci\u00F3n: IGIC devengado", null, TITLE)
//	,DVG01 (getDescription(Mod421Key.CA_DB01), new Mod421Key[]{Mod421Key.CA_DB01, Mod421Key.CA_DT01, Mod421Key.CA_DC01})
//	,DVG02 (getDescription(Mod421Key.CA_DB02), new Mod421Key[]{Mod421Key.CA_DB02, Mod421Key.CA_DT02, Mod421Key.CA_DC02})
//	,DVG03 (getDescription(Mod421Key.CA_DB03), new Mod421Key[]{Mod421Key.CA_DB03, Mod421Key.CA_DT03, Mod421Key.CA_DC03})
//	,DVG04 (getDescription(Mod421Key.CA_DB04), new Mod421Key[]{Mod421Key.CA_DB04, Mod421Key.CA_DT04, Mod421Key.CA_DC04})
//	,DVG05 (getDescription(Mod421Key.CA_DB05), new Mod421Key[]{Mod421Key.CA_DB05, Mod421Key.CA_DT05, Mod421Key.CA_DC05})
//	,DVG06 (getDescription(Mod421Key.CA_DB06), new Mod421Key[]{Mod421Key.CA_DB06, Mod421Key.CA_DT06, Mod421Key.CA_DC06})
//	,DVG07 (getDescription(Mod421Key.CA_DB07), new Mod421Key[]{Mod421Key.CA_DB07, Mod421Key.CA_DT07, Mod421Key.CA_DC07})
//	,DVG08 (getDescription(Mod421Key.CA_C019), new Mod421Key[]{Mod421Key.CA_C019,null,Mod421Key.CA_C020 })
//	,DVG09 (getDescription(Mod421Key.CA_C021), new Mod421Key[]{Mod421Key.CA_C021,null,Mod421Key.CA_C022 })
//	,DVG10 (getDescription(Mod421Key.CA_C023), new Mod421Key[]{Mod421Key.CA_C023,null,Mod421Key.CA_C024 })
//	,DVG17 (getDescription(Mod421Key.CA_C025), new Mod421Key[]{null				,null,Mod421Key.CA_C025 })
//	,EMPTY0(null,null)
//	,DED01 ("Liquidaci\u00F3n: IGIC deducible y Resultado Autoliquidaci\u00F3n", null, TITLE)
//	,DED02 (getDescription(Mod421Key.CA_C026), new Mod421Key[]{Mod421Key.CA_C026,null,Mod421Key.CA_C027})
//	,DED03 (getDescription(Mod421Key.CA_C028), new Mod421Key[]{Mod421Key.CA_C028,null,Mod421Key.CA_C029})
//	,DED04 (getDescription(Mod421Key.CA_C030), new Mod421Key[]{Mod421Key.CA_C030,null,Mod421Key.CA_C031})
//	,DED05 (getDescription(Mod421Key.CA_C032), new Mod421Key[]{Mod421Key.CA_C032,null,Mod421Key.CA_C033})
//	,DED06 (getDescription(Mod421Key.CA_C034), new Mod421Key[]{Mod421Key.CA_C034,null,Mod421Key.CA_C035})
//	,DED09 (getDescription(Mod421Key.CA_C036), new Mod421Key[]{null				,null,Mod421Key.CA_C036})
//	,DED10 (getDescription(Mod421Key.CA_C037), new Mod421Key[]{null				,null,Mod421Key.CA_C037})
//	,DED11 (getDescription(Mod421Key.CA_C038), new Mod421Key[]{null				,null,Mod421Key.CA_C038})
//	,DED12 (getDescription(Mod421Key.CA_C039), new Mod421Key[]{null				,null,Mod421Key.CA_C039})
//	,DED13 (getDescription(Mod421Key.CA_C040), new Mod421Key[]{null				,null,Mod421Key.CA_C040})
//	,R01   (getDescription(Mod421Key.CA_C041), new Mod421Key[]{null				,null,Mod421Key.CA_C041},TITLE)
//	,RES001(getDescription(Mod421Key.CA_C042), new Mod421Key[]{null				,null,Mod421Key.CA_C042})
//	,RES002(getDescription(Mod421Key.CA_C043), new Mod421Key[]{null				,null,Mod421Key.CA_C043})
//	,RES003(getDescription(Mod421Key.CA_C044), new Mod421Key[]{null				,null,Mod421Key.CA_C044})
//	,RES004(getDescription(Mod421Key.CA_C045), new Mod421Key[]{null				,null,Mod421Key.CA_C045},TITLE)
//	,EMPTY3(null,null)
//	,ADC00 ("Informaci\u00F3n adicional",null,TITLE)
//	,ADC01 (getDescription(Mod421Key.CA_C046), new Mod421Key[]{Mod421Key.CA_C046, null, null})
//	,ADC02 (getDescription(Mod421Key.CA_C047), new Mod421Key[]{Mod421Key.CA_C047, null, null})
//	,EMPTY4(null,null)
//	,ADC10 ("Exclusivamente para aquellos sujetos pasivos acogidos al r\u00E9gimen especial " 
//			+ "del criterio de caja y para aqu\u00E9llos que sean destinatarios de operaciones " 
//			+ "afectadas por el mismo",null,TITLE)
//	,ADC11 (getDescription(Mod421Key.CA_C048), new Mod421Key[]{Mod421Key.CA_C048,null,Mod421Key.CA_C049})	
//	,ADC12 (getDescription(Mod421Key.CA_C050), new Mod421Key[]{Mod421Key.CA_C050,null,Mod421Key.CA_C051})
	;
	
	private String label;
	private Mod421Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model4212026ATCPrintScript(String label, Mod421Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	private static String getDescription(Mod421Key key) {
		return AonStringUtils.trim(AonStringUtils.substringBefore(key.getDescription(), AonStringUtils.HYPHEN));
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public Mod421Key[] getKeys() {
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
		return (this == RES06);
	}
}
