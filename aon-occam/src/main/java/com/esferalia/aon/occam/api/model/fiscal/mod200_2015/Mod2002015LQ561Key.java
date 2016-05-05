package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Régimen de cooperativas - Detalle de compensación de cuotas
public enum Mod2002015LQ561Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002015Key[]{Mod2002015Key.LQ673 ,Mod2002015Key.LQ674,Mod2002015Key.LQ1224},"Compensaci\u00F3n de cuota año 2000")
	,C02(new Mod2002015Key[]{Mod2002015Key.LQ676 ,Mod2002015Key.LQ677,Mod2002015Key.LQ678 },"Compensaci\u00F3n de cuota año 2001")
	,C03(new Mod2002015Key[]{Mod2002015Key.LQ679 ,Mod2002015Key.LQ680,Mod2002015Key.LQ681 },"Compensaci\u00F3n de cuota año 2002")
	,C04(new Mod2002015Key[]{Mod2002015Key.LQ682 ,Mod2002015Key.LQ683,Mod2002015Key.LQ684 },"Compensaci\u00F3n de cuota año 2003")
	,C05(new Mod2002015Key[]{Mod2002015Key.LQ685 ,Mod2002015Key.LQ686,Mod2002015Key.LQ687 },"Compensaci\u00F3n de cuota año 2004")
	,C06(new Mod2002015Key[]{Mod2002015Key.LQ688 ,Mod2002015Key.LQ689,Mod2002015Key.LQ690 },"Compensaci\u00F3n de cuota año 2005")
	,C07(new Mod2002015Key[]{Mod2002015Key.LQ691 ,Mod2002015Key.LQ692,Mod2002015Key.LQ693 },"Compensaci\u00F3n de cuota año 2006")
	,C08(new Mod2002015Key[]{Mod2002015Key.LQ623 ,Mod2002015Key.LQ624,Mod2002015Key.LQ672 },"Compensaci\u00F3n de cuota año 2007")
	,C09(new Mod2002015Key[]{Mod2002015Key.LQ279 ,Mod2002015Key.LQ280,Mod2002015Key.LQ281 },"Compensaci\u00F3n de cuota año 2008")
	,C10(new Mod2002015Key[]{Mod2002015Key.LQ587 ,Mod2002015Key.LQ515,Mod2002015Key.LQ900 },"Compensaci\u00F3n de cuota año 2009")
	,C11(new Mod2002015Key[]{Mod2002015Key.LQ059 ,Mod2002015Key.LQ099,Mod2002015Key.LQ100 },"Compensaci\u00F3n de cuota año 2010")
	,C12(new Mod2002015Key[]{Mod2002015Key.LQ017 ,Mod2002015Key.LQ018,Mod2002015Key.LQ019 },"Compensaci\u00F3n de cuota año 2011")
	,C13(new Mod2002015Key[]{Mod2002015Key.LQ772 ,Mod2002015Key.LQ773,Mod2002015Key.LQ777 },"Compensaci\u00F3n de cuota año 2012")
	,C14(new Mod2002015Key[]{Mod2002015Key.LQ907 ,Mod2002015Key.LQ908,Mod2002015Key.LQ909 },"Compensaci\u00F3n de cuota año 2013")
	,C15(new Mod2002015Key[]{Mod2002015Key.LQ910 ,Mod2002015Key.LQ911,Mod2002015Key.LQ912 },"Compensaci\u00F3n de cuota año 2014")
	,C16(new Mod2002015Key[]{Mod2002015Key.LQ935 ,Mod2002015Key.LQ936,Mod2002015Key.LQ937 },"Compensaci\u00F3n de cuota año 2015(*)")
	,C17(new Mod2002015Key[]{Mod2002015Key.LQ694 ,null /* LQ561 */   ,Mod2002015Key.LQ695 },"Total")
	,C18(new Mod2002015Key[]{Mod2002015Key.LQ1225,null               ,Mod2002015Key.LQ1226},"Compensaci\u00F3n de cuota año 2015")
	;
	 
    private String description;
    private Mod2002015Key[] keys;

	private Mod2002015LQ561Key(Mod2002015Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002015Key[] getKeys() {
		return keys; 
	}
}

