package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import java.io.Serializable;

// Régimen de cooperativas - Detalle de compensación de cuotas
public enum Mod2002020LQ561Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002020Key[]{Mod2002020Key.LQ673 ,Mod2002020Key.LQ674 ,Mod2002020Key.LQ1224},"Compensaci\u00F3n de cuota a\u00F1o 2000")
	,C02(new Mod2002020Key[]{Mod2002020Key.LQ676 ,Mod2002020Key.LQ677 ,Mod2002020Key.LQ678 },"Compensaci\u00F3n de cuota a\u00F1o 2001")
	,C03(new Mod2002020Key[]{Mod2002020Key.LQ679 ,Mod2002020Key.LQ680 ,Mod2002020Key.LQ681 },"Compensaci\u00F3n de cuota a\u00F1o 2002")
	,C04(new Mod2002020Key[]{Mod2002020Key.LQ682 ,Mod2002020Key.LQ683 ,Mod2002020Key.LQ684 },"Compensaci\u00F3n de cuota a\u00F1o 2003")
	,C05(new Mod2002020Key[]{Mod2002020Key.LQ685 ,Mod2002020Key.LQ686 ,Mod2002020Key.LQ687 },"Compensaci\u00F3n de cuota a\u00F1o 2004")
	,C06(new Mod2002020Key[]{Mod2002020Key.LQ688 ,Mod2002020Key.LQ689 ,Mod2002020Key.LQ690 },"Compensaci\u00F3n de cuota a\u00F1o 2005")
	,C07(new Mod2002020Key[]{Mod2002020Key.LQ691 ,Mod2002020Key.LQ692 ,Mod2002020Key.LQ693 },"Compensaci\u00F3n de cuota a\u00F1o 2006")
	,C08(new Mod2002020Key[]{Mod2002020Key.LQ623 ,Mod2002020Key.LQ624 ,Mod2002020Key.LQ672 },"Compensaci\u00F3n de cuota a\u00F1o 2007")
	,C09(new Mod2002020Key[]{Mod2002020Key.LQ279 ,Mod2002020Key.LQ280 ,Mod2002020Key.LQ281 },"Compensaci\u00F3n de cuota a\u00F1o 2008")
	,C10(new Mod2002020Key[]{Mod2002020Key.LQ587 ,Mod2002020Key.LQ515 ,Mod2002020Key.LQ900 },"Compensaci\u00F3n de cuota a\u00F1o 2009")
	,C11(new Mod2002020Key[]{Mod2002020Key.LQ059 ,Mod2002020Key.LQ099 ,Mod2002020Key.LQ100 },"Compensaci\u00F3n de cuota a\u00F1o 2010")
	,C12(new Mod2002020Key[]{Mod2002020Key.LQ017 ,Mod2002020Key.LQ018 ,Mod2002020Key.LQ019 },"Compensaci\u00F3n de cuota a\u00F1o 2011")
	,C13(new Mod2002020Key[]{Mod2002020Key.LQ772 ,Mod2002020Key.LQ773 ,Mod2002020Key.LQ777 },"Compensaci\u00F3n de cuota a\u00F1o 2012")
	,C14(new Mod2002020Key[]{Mod2002020Key.LQ907 ,Mod2002020Key.LQ908 ,Mod2002020Key.LQ909 },"Compensaci\u00F3n de cuota a\u00F1o 2013")
	,C15(new Mod2002020Key[]{Mod2002020Key.LQ910 ,Mod2002020Key.LQ911 ,Mod2002020Key.LQ912 },"Compensaci\u00F3n de cuota a\u00F1o 2014")
	,C16(new Mod2002020Key[]{Mod2002020Key.LQ935 ,Mod2002020Key.LQ936 ,Mod2002020Key.LQ937 },"Compensaci\u00F3n de cuota a\u00F1o 2015")
	,C17(new Mod2002020Key[]{Mod2002020Key.LQ1511,Mod2002020Key.LQ1512,Mod2002020Key.LQ1513},"Compensaci\u00F3n de cuota a\u00F1o 2016")
	,C18(new Mod2002020Key[]{Mod2002020Key.LQ1767,Mod2002020Key.LQ1768,Mod2002020Key.LQ1769},"Compensaci\u00F3n de cuota a\u00F1o 2017")
	,C19(new Mod2002020Key[]{Mod2002020Key.LQ2113,Mod2002020Key.LQ2114,Mod2002020Key.LQ2115},"Compensaci\u00F3n de cuota a\u00F1o 2018")
	,C20(new Mod2002020Key[]{Mod2002020Key.LQ2281,Mod2002020Key.LQ2282,Mod2002020Key.LQ2283},"Compensaci\u00F3n de cuota a\u00F1o 2019")
	,C21(new Mod2002020Key[]{Mod2002020Key.LQ2452,Mod2002020Key.LQ2453,Mod2002020Key.LQ2454},"Compensaci\u00F3n de cuota a\u00F1o 2020(*)")
	,C22(new Mod2002020Key[]{Mod2002020Key.LQ694 ,Mod2002020Key.LQ561 ,Mod2002020Key.LQ695 },"Total")
	,C23(new Mod2002020Key[]{Mod2002020Key.LQ1225,null                ,Mod2002020Key.LQ1226},"Compensaci\u00F3n de cuota a\u00F1o 2020")
	;
	 
    private String description;
    private Mod2002020Key[] keys;

	private Mod2002020LQ561Key(Mod2002020Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002020Key[] getKeys() {
		return keys; 
	}

}

