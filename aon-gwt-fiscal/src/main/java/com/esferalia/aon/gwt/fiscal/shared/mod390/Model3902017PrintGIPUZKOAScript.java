package com.esferalia.aon.gwt.fiscal.shared.mod390;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Model3902017PrintGIPUZKOAScript implements IModelScript<Mod390Key> {
	
	 DVG01 ("IVA devengado",null,null,TITLE)
	,DVG02 (null								,new Mod390Key[]{Mod390Key.GP_C002	,Mod390Key.GP_X002	,Mod390Key.GP_C003})
	,DVG03 (getDescription(Mod390Key.GP_C004)	,new Mod390Key[]{Mod390Key.GP_C004	,Mod390Key.GP_X004	,Mod390Key.GP_C005})
	,DVG04 (null								,new Mod390Key[]{Mod390Key.GP_C006	,Mod390Key.GP_X006	,Mod390Key.GP_C007})
	,DVG05 (getDescription(Mod390Key.GP_C008)	,new Mod390Key[]{Mod390Key.GP_C008	,null			  	,Mod390Key.GP_C009})
	,DVG06 (null								,new Mod390Key[]{Mod390Key.GP_C010	,Mod390Key.GP_X010	,Mod390Key.GP_C011})
	,DVG07 (getDescription(Mod390Key.GP_C012)	,new Mod390Key[]{Mod390Key.GP_C012	,Mod390Key.GP_X012	,Mod390Key.GP_C013})
	,DVG08 (null								,new Mod390Key[]{Mod390Key.GP_C014	,Mod390Key.GP_X014	,Mod390Key.GP_C015})
	,DVG09 (getDescription(Mod390Key.GP_C016)	,new Mod390Key[]{Mod390Key.GP_C016	,null			  	,Mod390Key.GP_C017})
	,DVG10 (getDescription(Mod390Key.GP_C018)	,new Mod390Key[]{Mod390Key.GP_C018	,null			  	,Mod390Key.GP_C019})
	,DVG11 (getDescription(Mod390Key.GP_C106)	,new Mod390Key[]{Mod390Key.GP_C106	,null			  	,Mod390Key.GP_C107})
	,DVG12 (Mod390Key.GP_C020.getDescription() 	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C020})
	
	,DED01 ("IVA deducible",null,null,TITLE)
	,DED02 (getDescription(Mod390Key.GP_C021)	,new Mod390Key[]{Mod390Key.GP_C021	,null			  	,Mod390Key.GP_C022})
	,DED03 (getDescription(Mod390Key.GP_C023)	,new Mod390Key[]{Mod390Key.GP_C023	,null			  	,Mod390Key.GP_C024})
	,DED04 (getDescription(Mod390Key.GP_C025)	,new Mod390Key[]{Mod390Key.GP_C025	,null			  	,Mod390Key.GP_C026})
	,DED06 (Mod390Key.GP_C027.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C027})
	,DED05 (getDescription(Mod390Key.GP_C271)	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C271})
	,DED07 (Mod390Key.GP_C028.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C028})
	,DED08 (Mod390Key.GP_C029.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C029})

	,R000 ("Resultado",null,TITLE)
	,R001 (Mod390Key.GP_C030.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C030})
	,R002 (Mod390Key.GP_C031.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C031})
	,R003 (Mod390Key.GP_C032.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C032})
	,R004 (Mod390Key.GP_C033.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C033})
	,R005 (Mod390Key.GP_C034.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C034})
	,R006 (Mod390Key.GP_C035.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C035})
	
	,R007 (Mod390Key.GP_C036.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C036})
	,R008 (Mod390Key.GP_C037.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C037})
	,R009 (Mod390Key.GP_C038.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C038})
	,R010 (Mod390Key.GP_C039.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C039})
	,R011 (Mod390Key.GP_C040.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C040})
	,R012 (Mod390Key.GP_C041.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C041})
	,R013 (Mod390Key.GP_C042.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C042})
	,R014 (Mod390Key.GP_C043.getDescription()	,new Mod390Key[]{null			  	,null			  	,Mod390Key.GP_C043})
	
	,AVG00 ("Informaci\u00F3n adicional",null,TITLE)
	,AVG01 ("Compras de bienes corrientes",null,TITLE)
	,AVG02 (null								,new Mod390Key[]{Mod390Key.GP_C046	,Mod390Key.GP_X046	,Mod390Key.GP_C047})
	,AVG03 (null								,new Mod390Key[]{Mod390Key.GP_C048	,Mod390Key.GP_X048	,Mod390Key.GP_C049})
	,AVG04 (null								,new Mod390Key[]{Mod390Key.GP_C050	,Mod390Key.GP_X050	,Mod390Key.GP_C051})
	,AVG05 (null								,new Mod390Key[]{Mod390Key.GP_C052	,null				,Mod390Key.GP_C053})
	,AVG06 (null								,new Mod390Key[]{Mod390Key.GP_C054	,null				,Mod390Key.GP_C055})
	,AVG07 ("Total"								,new Mod390Key[]{null				,null				,Mod390Key.GP_C056})
	,AVG08 ("Gastos",null,TITLE)
	,AVG09 (null								,new Mod390Key[]{Mod390Key.GP_C057	,Mod390Key.GP_X057	,Mod390Key.GP_C058})
	,AVG10 (null								,new Mod390Key[]{Mod390Key.GP_C059	,Mod390Key.GP_X059	,Mod390Key.GP_C060})
	,AVG11 (null								,new Mod390Key[]{Mod390Key.GP_C061	,Mod390Key.GP_X061	,Mod390Key.GP_C061})
	,AVG12 (null								,new Mod390Key[]{Mod390Key.GP_C063	,null				,Mod390Key.GP_C063})
	,AVG13 ("Total"								,new Mod390Key[]{null				,null				,Mod390Key.GP_C065})
	,AVG14 ("Bienes de inversi\u00F3n",null,TITLE)
	,AVG15 (null								,new Mod390Key[]{Mod390Key.GP_C066	,Mod390Key.GP_X066	,Mod390Key.GP_C067})
	,AVG16 (null								,new Mod390Key[]{Mod390Key.GP_C068	,Mod390Key.GP_X068	,Mod390Key.GP_C069})
	,AVG17 (null								,new Mod390Key[]{Mod390Key.GP_C070	,Mod390Key.GP_X070	,Mod390Key.GP_C071})
	,AVG18 (null								,new Mod390Key[]{Mod390Key.GP_C072	,null				,Mod390Key.GP_C073})
	,AVG19 ("Total"								,new Mod390Key[]{null				,null				,Mod390Key.GP_C074})	
	,AVG20 ("Totales"							,new Mod390Key[]{Mod390Key.GP_C075	,null				,Mod390Key.GP_C076},TITLE)
	,AVG21 ("Totales"							,new Mod390Key[]{null				,null				,Mod390Key.GP_C077},TITLE)	
	
	,VOL00 ("Volumen de operaciones",null,TITLE)
	,VOL01 (Mod390Key.GP_C082.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C082})
	,VOL02 (Mod390Key.GP_C083.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C083})
	,VOL03 (Mod390Key.GP_C084.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C084})
	,VOL04 (Mod390Key.GP_C085.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C085})
	,VOL05 (Mod390Key.GP_C086.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C086})
	,VOL06 (Mod390Key.GP_C087.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C087})
	,VOL07 (Mod390Key.GP_C088.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C088})
	,VOL08 (Mod390Key.GP_C089.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C089})
	,VOL09 (Mod390Key.GP_C090.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C090})
	,VOL10 (Mod390Key.GP_C091.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C091})
	,VOL11 (Mod390Key.GP_C092.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C092})
	,VOL12 (Mod390Key.GP_C093.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C093})
	,VOL13 (Mod390Key.GP_C095.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C095})
	
	,OPE00 ("Operaciones de compra",null,TITLE)
	,OPE01 (Mod390Key.GP_C096.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C096})
	,OPE02 (Mod390Key.GP_C097.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C097})
	,OPE03 (Mod390Key.GP_C098.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C098})
	,OPE04 (Mod390Key.GP_C099.getDescription()	,new Mod390Key[]{null				,null				,Mod390Key.GP_C099})
	
	,ADC00 ("Exclusivamente para aquellos sujetos pasivos acogidos al R\u00E9gimen especial del criterio de caja y para "
			+"aquellos que sean destinatarios de operaciones afectadas por el mismo:",null)
	,ADC01 (getDescription(Mod390Key.GP_C101)	,new Mod390Key[]{Mod390Key.GP_C101,Mod390Key.GP_C102})	
	,ADC02 (getDescription(Mod390Key.GP_C103)	,new Mod390Key[]{Mod390Key.GP_C103,Mod390Key.GP_C103})
	;
	
	private String label;
	private Mod390Key[] keys;
	private FiscalModelKeyInfo[] infoKeys;
	
	private Model3902017PrintGIPUZKOAScript(String label, Mod390Key[] keys,FiscalModelKeyInfo ... infoKeys) {
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
		return (this == AVG00) || (this == VOL00);
	};
}
