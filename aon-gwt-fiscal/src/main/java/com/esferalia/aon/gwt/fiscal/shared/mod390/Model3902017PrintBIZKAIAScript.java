package com.esferalia.aon.gwt.fiscal.shared.mod390;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Model3902017PrintBIZKAIAScript implements IModelScript<Mod390Key> {
	
	 DVG01 ("IVA DEVENGADO",null,TITLE)
	,DVG02 (null								,new Mod390Key[]{Mod390Key.BZ_C020	,Mod390Key.BZ_X020	,Mod390Key.BZ_C021})
	,DVG03 (getDescription(Mod390Key.BZ_C022)	,new Mod390Key[]{Mod390Key.BZ_C022	,Mod390Key.BZ_X022	,Mod390Key.BZ_C023})
	,DVG04 (null								,new Mod390Key[]{Mod390Key.BZ_C024	,Mod390Key.BZ_X024	,Mod390Key.BZ_C025})
	,DVG05 (null								,new Mod390Key[]{Mod390Key.BZ_C026	,Mod390Key.BZ_X026	,Mod390Key.BZ_C027})
	,DVG06 (getDescription(Mod390Key.BZ_C028)	,new Mod390Key[]{Mod390Key.BZ_C028	,Mod390Key.BZ_X028	,Mod390Key.BZ_C029})
	,DVG07 (null								,new Mod390Key[]{Mod390Key.BZ_C030	,Mod390Key.BZ_X030	,Mod390Key.BZ_C031})
	,DVG08 (null								,new Mod390Key[]{Mod390Key.BZ_C032	,Mod390Key.BZ_X032	,Mod390Key.BZ_C033})
	,DVG09 (getDescription(Mod390Key.BZ_C034)	,new Mod390Key[]{Mod390Key.BZ_C034	,Mod390Key.BZ_X034	,Mod390Key.BZ_C035})
	,DVG10 (null								,new Mod390Key[]{Mod390Key.BZ_C036	,Mod390Key.BZ_X036	,Mod390Key.BZ_C037})
	,DVG11 (null								,new Mod390Key[]{Mod390Key.BZ_C038	,Mod390Key.BZ_X038	,Mod390Key.BZ_C039})
	,DVG12 (getDescription(Mod390Key.BZ_C040)	,new Mod390Key[]{Mod390Key.BZ_C040	,null				,Mod390Key.BZ_C041})
	,DVG13 (getDescription(Mod390Key.BZ_C042)	,new Mod390Key[]{Mod390Key.BZ_C042	,null				,Mod390Key.BZ_C043})
	,DVG14 (getDescription(Mod390Key.BZ_C044)	,new Mod390Key[]{Mod390Key.BZ_C044	,null				,Mod390Key.BZ_C045})
	,DVG15 (getDescription(Mod390Key.BZ_C046)	,new Mod390Key[]{Mod390Key.BZ_C046	,null				,Mod390Key.BZ_C047})
	,DVG16 (getDescription(Mod390Key.BZ_C048)	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C048},TITLE)
	
	,DED01 ("IVA DEDUCIBLE" ,null,TITLE)
	,DED02 (getDescription(Mod390Key.BZ_C060)	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C060})
	,DED03 (getDescription(Mod390Key.BZ_C061)	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C061})
	,DED04 (getDescription(Mod390Key.BZ_C062)	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C062})
	,DED05 (getDescription(Mod390Key.BZ_C063)	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C063})
	,DED06 (getDescription(Mod390Key.BZ_C064)	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C064})
	,DED07 (getDescription(Mod390Key.BZ_C065)	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C065})
	,DED08 (getDescription(Mod390Key.BZ_C066)	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C066},null,TITLE)
	
	,TRI00("Tributaci\u00F3n por raz\u00F3n de territorio" ,null,TITLE)
	,TRI01("Territorio Com\u00FAn"				,new Mod390Key[]{null				,Mod390Key.BZ_C081	,Mod390Key.BZ_C082})
	,TRI02("Araba/\u00C1lava"					,new Mod390Key[]{null				,Mod390Key.BZ_C083	,Mod390Key.BZ_C084})
	,TRI03("Gipuzkoa"							,new Mod390Key[]{null				,Mod390Key.BZ_C085	,Mod390Key.BZ_C086})
	,TRI04("Bizkaia"							,new Mod390Key[]{null				,Mod390Key.BZ_C087	,Mod390Key.BZ_C088})
	,TRI05("Nafarroa/Navarra"					,new Mod390Key[]{null				,Mod390Key.BZ_C089	,Mod390Key.BZ_C090})
	,TRI06("Total"								,new Mod390Key[]{null				,Mod390Key.BZ_C091	,Mod390Key.BZ_C092},TITLE)
	
	,LQ000("RESULTADO" ,null,TITLE)
	,LQ001(Mod390Key.BZ_C095.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C095})
	,LQ002(Mod390Key.BZ_C120.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C120})
	,LQ004(Mod390Key.BZ_C096.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C096})
	,LQ005(Mod390Key.BZ_C097.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C097})
	,LQ006(Mod390Key.BZ_C098.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C098})
	,LQ007(Mod390Key.BZ_C099.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C099})
	,LQ008(Mod390Key.BZ_C100.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C100})
	,LQ009(Mod390Key.BZ_C110.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C110})
	,LQ010(Mod390Key.BZ_C115.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C115})
	,LQ011(Mod390Key.BZ_C116.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C116})
	,LQ012(Mod390Key.BZ_C117.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C117})
	
	,AVG00 ("DATOS ADICIONALES",null,TITLE)
	,AVG01 ("Compras de bienes corrientes",null,TITLE)
	,AVG02 ("4%"								,new Mod390Key[]{Mod390Key.BZ_C142	,Mod390Key.BZ_C143	,Mod390Key.BZ_C144})
	,AVG03 ("10%"								,new Mod390Key[]{Mod390Key.BZ_C145	,Mod390Key.BZ_C146	,Mod390Key.BZ_C147})
	,AVG04 ("21%"								,new Mod390Key[]{Mod390Key.BZ_C148	,Mod390Key.BZ_C149	,Mod390Key.BZ_C150})
	,AVG05 (null								,new Mod390Key[]{Mod390Key.BZ_C151	,Mod390Key.BZ_C152	,Mod390Key.BZ_C153})
	,AVG06 (null								,new Mod390Key[]{Mod390Key.BZ_C154	,Mod390Key.BZ_C155	,Mod390Key.BZ_C156})
	,AVG07 ("Total"								,new Mod390Key[]{Mod390Key.BZ_C157	,Mod390Key.BZ_C158	,Mod390Key.BZ_C159})
	
	,AVG08 ("Gastos",null,TITLE)
	,AVG09 ("4%"								,new Mod390Key[]{Mod390Key.BZ_C160	,Mod390Key.BZ_C161	,Mod390Key.BZ_C162})
	,AVG10 ("10%"								,new Mod390Key[]{Mod390Key.BZ_C163	,Mod390Key.BZ_C164	,Mod390Key.BZ_C165})
	,AVG11 ("21%"								,new Mod390Key[]{Mod390Key.BZ_C166	,Mod390Key.BZ_C167	,Mod390Key.BZ_C168})
	,AVG13 (null								,new Mod390Key[]{Mod390Key.BZ_C169	,Mod390Key.BZ_C170	,Mod390Key.BZ_C171})
	,AVG14 ("Total"								,new Mod390Key[]{Mod390Key.BZ_C172	,Mod390Key.BZ_C173	,Mod390Key.BZ_C174})
	,AVG15 ("Bienes de inversi\u00F3n",null,TITLE)
	,AVG16 ("4%"								,new Mod390Key[]{Mod390Key.BZ_C175	,Mod390Key.BZ_C176	,Mod390Key.BZ_C177})
	,AVG17 ("10%"								,new Mod390Key[]{Mod390Key.BZ_C178	,Mod390Key.BZ_C179	,Mod390Key.BZ_C180})
	,AVG18 ("21%"								,new Mod390Key[]{Mod390Key.BZ_C181	,Mod390Key.BZ_C182	,Mod390Key.BZ_C183})
	,AVG19 (null								,new Mod390Key[]{Mod390Key.BZ_C184	,Mod390Key.BZ_C185	,Mod390Key.BZ_C186})
	,AVG20 ("Total"								,new Mod390Key[]{Mod390Key.BZ_C187	,Mod390Key.BZ_C188	,Mod390Key.BZ_C189})
	,AVG21 ("Totales"							,new Mod390Key[]{Mod390Key.BZ_C190	,Mod390Key.BZ_C191	,Mod390Key.BZ_C192},TITLE)
	
	,ADC00 ("Exclusivamente para aquellos sujetos pasivos acogidos al R\u00E9gimen especial del criterio de caja y para "
			+"aquellos que sean destinatarios de operaciones afectadas por el mismo:",null)
	,ADC01 (getDescription(Mod390Key.BZ_C130)	,new Mod390Key[]{Mod390Key.BZ_C130,Mod390Key.BZ_C131})	
	,ADC02 (getDescription(Mod390Key.BZ_C132)	,new Mod390Key[]{Mod390Key.BZ_C132,Mod390Key.BZ_C133})
	
	,VOL00 ("Volumen de operaciones",null,TITLE)
	,VOL01 (Mod390Key.BZ_C200.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C200})
	,VOL02 (Mod390Key.BZ_C201.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C201})
	,VOL03 (Mod390Key.BZ_C202.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C202})
	,VOL04 (Mod390Key.BZ_C203.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C203})
	,VOL05 (Mod390Key.BZ_C204.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C204})
	,VOL06 (Mod390Key.BZ_C205.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C205})
	,VOL07 (Mod390Key.BZ_C206.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C206})
	,VOL08 (Mod390Key.BZ_C207.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C207})
	,VOL09 (Mod390Key.BZ_C208.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C208})
	,VOL10 (Mod390Key.BZ_C209.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C209})
	,VOL11 (Mod390Key.BZ_C210.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C210})
	,VOL12 (Mod390Key.BZ_C211.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C211})
	,VOL13 (Mod390Key.BZ_C212.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C212})
	,VOL14 (Mod390Key.BZ_C213.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C213})
	,VOL15 (Mod390Key.BZ_C214.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C214})
	,VOL16 (Mod390Key.BZ_C215.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C215})
	
	,OPE00 ("Operaciones espec\u00EDficas",null,TITLE)
	,OPE01 (Mod390Key.BZ_C220.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C220})
	,OPE02 (Mod390Key.BZ_C221.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C221})
	,OPE03 (Mod390Key.BZ_C222.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C222})
	,OPE04 (Mod390Key.BZ_C223.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.BZ_C223})
	
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902017PrintBIZKAIAScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return (this == LQ000) || (this == AVG00) || (this == VOL00);
	};
}
