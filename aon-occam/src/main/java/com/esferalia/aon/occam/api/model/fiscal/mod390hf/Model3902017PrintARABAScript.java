package com.esferalia.aon.occam.api.model.fiscal.mod390hf;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Model3902017PrintARABAScript implements IModelScript<Mod390Key> {
	
	  DVG01 ("IVA devengado",null,TITLE)
	 ,DVG02 (null								,new Mod390Key[]{Mod390Key.AR_C001	,Mod390Key.AR_C002	,Mod390Key.AR_C003})
	 ,DVG03 (getDescription(Mod390Key.AR_C804)	,new Mod390Key[]{Mod390Key.AR_C804	,Mod390Key.AR_C805	,Mod390Key.AR_C806})
	 ,DVG04 (null								,new Mod390Key[]{Mod390Key.AR_C807	,Mod390Key.AR_C808	,Mod390Key.AR_C809})
	 ,DVG05 (getDescription(Mod390Key.AR_C351)	,new Mod390Key[]{Mod390Key.AR_C351	,null				,Mod390Key.AR_C352})
	 ,DVG06 (getDescription(Mod390Key.AR_C019)	,new Mod390Key[]{Mod390Key.AR_C019	,null				,Mod390Key.AR_C020})
	 ,DVG07 (getDescription(Mod390Key.AR_C223)	,new Mod390Key[]{Mod390Key.AR_C223	,null				,Mod390Key.AR_C224})
	 ,DVG08 (null								,new Mod390Key[]{Mod390Key.AR_C025	,Mod390Key.AR_C026	,Mod390Key.AR_C027})
	 ,DVG09 (getDescription(Mod390Key.AR_C034)	,new Mod390Key[]{Mod390Key.AR_C034	,Mod390Key.AR_C035	,Mod390Key.AR_C036})
	 ,DVG10 (null								,new Mod390Key[]{Mod390Key.AR_C828	,Mod390Key.AR_C829	,Mod390Key.AR_C830})
	 ,DVG11 (null								,new Mod390Key[]{Mod390Key.AR_C831	,Mod390Key.AR_C832	,Mod390Key.AR_C833})
	 ,DVG12 (getDescription(Mod390Key.AR_C037)	,new Mod390Key[]{Mod390Key.AR_C037	,null				,Mod390Key.AR_C038})
	 ,DVG13 (getDescription(Mod390Key.AR_C239)	,new Mod390Key[]{Mod390Key.AR_C239	,null				,Mod390Key.AR_C240})
	 ,DVG14 (null								,new Mod390Key[]{Mod390Key.AR_C010	,Mod390Key.AR_C011	,Mod390Key.AR_C012})
	 ,DVG15 (getDescription(Mod390Key.AR_C813)	,new Mod390Key[]{Mod390Key.AR_C813	,Mod390Key.AR_C814	,Mod390Key.AR_C815})
	 ,DVG16 (null								,new Mod390Key[]{Mod390Key.AR_C816	,Mod390Key.AR_C817	,Mod390Key.AR_C818})
	 ,DVG17 (getDescription(Mod390Key.AR_C353)	,new Mod390Key[]{Mod390Key.AR_C353	,null				,Mod390Key.AR_C354})				
	 ,DVG18 (Mod390Key.AR_C041.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.AR_C041},TITLE)
	 ,DVG19 (Mod390Key.AR_C042.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.AR_C042})
	 ,EMPTY0(null						,null)
	 
	,DED01 ("IVA deducible",null,TITLE)
	,DED02 (null								,new Mod390Key[]{Mod390Key.AR_C043,Mod390Key.AR_C044,Mod390Key.AR_C045})
	,DED03 (getDescription(Mod390Key.AR_C846)	,new Mod390Key[]{Mod390Key.AR_C846,Mod390Key.AR_C847,Mod390Key.AR_C848})
	,DED04 (null								,new Mod390Key[]{Mod390Key.AR_C849,Mod390Key.AR_C850,Mod390Key.AR_C851})
	,DED05 (null								,new Mod390Key[]{Mod390Key.AR_C076,Mod390Key.AR_C077,Mod390Key.AR_C078})
	,DED06 (getDescription(Mod390Key.AR_C879)	,new Mod390Key[]{Mod390Key.AR_C879,Mod390Key.AR_C880,Mod390Key.AR_C881})
	,DED07 (null								,new Mod390Key[]{Mod390Key.AR_C882,Mod390Key.AR_C883,Mod390Key.AR_C884})
	,DED08 (getDescription(Mod390Key.AR_C355)	,new Mod390Key[]{Mod390Key.AR_C355,null				,Mod390Key.AR_C356})
	,DED09 (null								,new Mod390Key[]{Mod390Key.AR_C054,Mod390Key.AR_C055,Mod390Key.AR_C056})
	,DED10 (getDescription(Mod390Key.AR_C857)	,new Mod390Key[]{Mod390Key.AR_C857,Mod390Key.AR_C858,Mod390Key.AR_C859})
	,DED11 (null								,new Mod390Key[]{Mod390Key.AR_C860,Mod390Key.AR_C861,Mod390Key.AR_C862})
	,DED12 (null								,new Mod390Key[]{Mod390Key.AR_C087,Mod390Key.AR_C088,Mod390Key.AR_C089})
	,DED13 (getDescription(Mod390Key.AR_C890)	,new Mod390Key[]{Mod390Key.AR_C890,Mod390Key.AR_C891,Mod390Key.AR_C892})
	,DED14 (null								,new Mod390Key[]{Mod390Key.AR_C893,Mod390Key.AR_C894,Mod390Key.AR_C895})
	,DED15 (getDescription(Mod390Key.AR_C357)	,new Mod390Key[]{Mod390Key.AR_C357,null				,Mod390Key.AR_C358})
	,DED16 (null								,new Mod390Key[]{Mod390Key.AR_C065,Mod390Key.AR_C066,Mod390Key.AR_C067})
	,DED17 (getDescription(Mod390Key.AR_C868)	,new Mod390Key[]{Mod390Key.AR_C868,Mod390Key.AR_C869,Mod390Key.AR_C870})
	,DED18 (null								,new Mod390Key[]{Mod390Key.AR_C871,Mod390Key.AR_C872,Mod390Key.AR_C873})
	,DED19 (null								,new Mod390Key[]{Mod390Key.AR_C098,Mod390Key.AR_C099,Mod390Key.AR_C100})
	,DED20 (getDescription(Mod390Key.AR_C361)	,new Mod390Key[]{Mod390Key.AR_C361,Mod390Key.AR_C362,Mod390Key.AR_C363})
	,DED21 (null								,new Mod390Key[]{Mod390Key.AR_C364,Mod390Key.AR_C365,Mod390Key.AR_C366})
	,DED22 (getDescription(Mod390Key.AR_C359)	,new Mod390Key[]{Mod390Key.AR_C359,null				,Mod390Key.AR_C360})
	,DED23 (getDescription(Mod390Key.AR_C109)	,new Mod390Key[]{Mod390Key.AR_C109,null				,Mod390Key.AR_C110})
	,DED24 (getDescription(Mod390Key.AR_C111)	,new Mod390Key[]{Mod390Key.AR_C111,null				,Mod390Key.AR_C112})
	,DED25 (getDescription(Mod390Key.AR_C115)	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C115})
	,DED26 (getDescription(Mod390Key.AR_C113)	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C113},TITLE)
	
	,R000 ("Resultado",null,TITLE)
	,R001 (Mod390Key.AR_C114.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C114},TITLE)
	,R002 (Mod390Key.AR_C120.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C120})
	,R003 (Mod390Key.AR_C121.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C121})
	,R004 (Mod390Key.AR_C122.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C122})
	,R005 (Mod390Key.AR_C123.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C123})
	,R006 (Mod390Key.AR_C124.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C124})
	,R007 (Mod390Key.AR_C125.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C125})
	,R008 (Mod390Key.AR_C126.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C126})
	,R009 (Mod390Key.AR_C127.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C127})
	,R010 (Mod390Key.AR_C128.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C128})
	,R011 (Mod390Key.AR_C129.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C129})
	,R012 (Mod390Key.AR_C130.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C130})
	,R013 (Mod390Key.AR_C13X.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C13X})
	,R017 (Mod390Key.AR_C134.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C134})
	,R018 (Mod390Key.AR_C135.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C135})
	,R019 (Mod390Key.AR_C140.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C140},TITLE)
	,R020 (Mod390Key.AR_C141.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C141},TITLE)
	,R021 (Mod390Key.AR_C142.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C142},TITLE)
	
	,ADC00 ("Informaci\u00F3n adicional",null,TITLE)
	,ADC01 (Mod390Key.AR_C153.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C153})
	,ADC02 (Mod390Key.AR_C252.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C252})
	,ADC03 (Mod390Key.AR_C156.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C156})
	,ADC04 (Mod390Key.AR_C157.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C157})
	,ADC05 (Mod390Key.AR_C158.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C158})
	,ADC06 (Mod390Key.AR_C214.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C214})
	,ADC07 (Mod390Key.AR_C215.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C215})
	,ADC08 (Mod390Key.AR_C216.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C216})
	,ADC09 (Mod390Key.AR_C217.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C217})
	,ADC10 (Mod390Key.AR_C218.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C218})
	,ADC11 (Mod390Key.AR_C219.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C219})
	,ADC12 (Mod390Key.AR_C154.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C154})
	,ADC13 (Mod390Key.AR_C155.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C155})
	,ADC14 (Mod390Key.AR_C220.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C220})
	,ADC15 (Mod390Key.AR_C221.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C221})
	,ADC16 (Mod390Key.AR_C212.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C212})
	,ADC17 (Mod390Key.AR_C161.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C161})
	,ADC18 (Mod390Key.AR_C162.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C162})
	,ADC19 (Mod390Key.AR_C163.getDescription() 	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C163})
	,ADC20 (Mod390Key.AR_C167.getDescription()	,new Mod390Key[]{null			  ,null				,Mod390Key.AR_C167})
	,ADC21 ("Exclusivamente para aquellos sujetos pasivos acogidos al R\u00E9gimen especial del criterio de caja y para "
			+"aquellos que sean destinatarios de operaciones afectadas por el mismo:",null)
	,ADC22 (getDescription(Mod390Key.AR_C262)	,new Mod390Key[]{null			  ,Mod390Key.AR_C262,Mod390Key.AR_C263})	
	,ADC23 (getDescription(Mod390Key.AR_C264)	,new Mod390Key[]{null			  ,Mod390Key.AR_C264,Mod390Key.AR_C265})
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902017PrintARABAScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
		this.label = label;
		this.keys = keys;
		this.infoKeys = infoKeys;
	}

	private static String getDescription(Mod390Key key) {
		return AonStringUtils.trim(AonStringUtils.substringBefore(key.getDescription(), AonStringUtils.HYPHEN));
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
		return (this == DED01) || (this == R000) ||  (this == ADC00);
	};
}
