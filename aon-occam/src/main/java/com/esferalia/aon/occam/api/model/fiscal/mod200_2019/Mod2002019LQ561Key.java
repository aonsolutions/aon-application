package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import java.io.Serializable;

// Régimen de cooperativas - Detalle de compensación de cuotas
public enum Mod2002019LQ561Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002019Key[]{Mod2002019Key.LQ673 ,Mod2002019Key.LQ674 ,Mod2002019Key.LQ1224},"Compensaci\u00F3n de cuota a\u00F1o 2000")
	,C02(new Mod2002019Key[]{Mod2002019Key.LQ676 ,Mod2002019Key.LQ677 ,Mod2002019Key.LQ678 },"Compensaci\u00F3n de cuota a\u00F1o 2001")
	,C03(new Mod2002019Key[]{Mod2002019Key.LQ679 ,Mod2002019Key.LQ680 ,Mod2002019Key.LQ681 },"Compensaci\u00F3n de cuota a\u00F1o 2002")
	,C04(new Mod2002019Key[]{Mod2002019Key.LQ682 ,Mod2002019Key.LQ683 ,Mod2002019Key.LQ684 },"Compensaci\u00F3n de cuota a\u00F1o 2003")
	,C05(new Mod2002019Key[]{Mod2002019Key.LQ685 ,Mod2002019Key.LQ686 ,Mod2002019Key.LQ687 },"Compensaci\u00F3n de cuota a\u00F1o 2004")
	,C06(new Mod2002019Key[]{Mod2002019Key.LQ688 ,Mod2002019Key.LQ689 ,Mod2002019Key.LQ690 },"Compensaci\u00F3n de cuota a\u00F1o 2005")
	,C07(new Mod2002019Key[]{Mod2002019Key.LQ691 ,Mod2002019Key.LQ692 ,Mod2002019Key.LQ693 },"Compensaci\u00F3n de cuota a\u00F1o 2006")
	,C08(new Mod2002019Key[]{Mod2002019Key.LQ623 ,Mod2002019Key.LQ624 ,Mod2002019Key.LQ672 },"Compensaci\u00F3n de cuota a\u00F1o 2007")
	,C09(new Mod2002019Key[]{Mod2002019Key.LQ279 ,Mod2002019Key.LQ280 ,Mod2002019Key.LQ281 },"Compensaci\u00F3n de cuota a\u00F1o 2008")
	,C10(new Mod2002019Key[]{Mod2002019Key.LQ587 ,Mod2002019Key.LQ515 ,Mod2002019Key.LQ900 },"Compensaci\u00F3n de cuota a\u00F1o 2009")
	,C11(new Mod2002019Key[]{Mod2002019Key.LQ059 ,Mod2002019Key.LQ099 ,Mod2002019Key.LQ100 },"Compensaci\u00F3n de cuota a\u00F1o 2010")
	,C12(new Mod2002019Key[]{Mod2002019Key.LQ017 ,Mod2002019Key.LQ018 ,Mod2002019Key.LQ019 },"Compensaci\u00F3n de cuota a\u00F1o 2011")
	,C13(new Mod2002019Key[]{Mod2002019Key.LQ772 ,Mod2002019Key.LQ773 ,Mod2002019Key.LQ777 },"Compensaci\u00F3n de cuota a\u00F1o 2012")
	,C14(new Mod2002019Key[]{Mod2002019Key.LQ907 ,Mod2002019Key.LQ908 ,Mod2002019Key.LQ909 },"Compensaci\u00F3n de cuota a\u00F1o 2013")
	,C15(new Mod2002019Key[]{Mod2002019Key.LQ910 ,Mod2002019Key.LQ911 ,Mod2002019Key.LQ912 },"Compensaci\u00F3n de cuota a\u00F1o 2014")
	,C16(new Mod2002019Key[]{Mod2002019Key.LQ935 ,Mod2002019Key.LQ936 ,Mod2002019Key.LQ937 },"Compensaci\u00F3n de cuota a\u00F1o 2015")
	,C17(new Mod2002019Key[]{Mod2002019Key.LQ1511,Mod2002019Key.LQ1512,Mod2002019Key.LQ1513},"Compensaci\u00F3n de cuota a\u00F1o 2016")
	,C18(new Mod2002019Key[]{Mod2002019Key.LQ1767,Mod2002019Key.LQ1768,Mod2002019Key.LQ1769},"Compensaci\u00F3n de cuota a\u00F1o 2017")
	,C19(new Mod2002019Key[]{Mod2002019Key.LQ2113,Mod2002019Key.LQ2114,Mod2002019Key.LQ2115},"Compensaci\u00F3n de cuota a\u00F1o 2018")
	,C20(new Mod2002019Key[]{Mod2002019Key.LQ2281,Mod2002019Key.LQ2282,Mod2002019Key.LQ2283},"Compensaci\u00F3n de cuota a\u00F1o 2019(*)")
	,C21(new Mod2002019Key[]{Mod2002019Key.LQ694 ,Mod2002019Key.LQ561 ,Mod2002019Key.LQ695 },"Total")
	,C22(new Mod2002019Key[]{Mod2002019Key.LQ1225,null                ,Mod2002019Key.LQ1226},"Compensaci\u00F3n de cuota a\u00F1o 2019")
	;
	 
    private String description;
    private Mod2002019Key[] keys;

	private Mod2002019LQ561Key(Mod2002019Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002019Key[] getKeys() {
		return keys; 
	}
}

